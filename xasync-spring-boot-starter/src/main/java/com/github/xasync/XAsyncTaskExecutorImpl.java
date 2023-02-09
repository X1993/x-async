package com.github.xasync;

import com.github.xasync.exception.IllegalTaskException;
import com.github.xasync.exception.TaskExecuteException;
import com.github.xasync.exception.handler.TaskExecuteExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 异常任务执行器
 * @author X1993
 * @date 2022/6/2
 * @description
 */
@Slf4j
public class XAsyncTaskExecutorImpl implements XAsyncTaskExecutor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String , XAsyncTaskStorage> asyncTaskStorageMap;

    private TaskExecuteExceptionHandler exceptionHandler;

    private XAsyncTaskProperties xAsyncTaskProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
        //XAsyncTaskStorage实现中可能会使用到这个类，避免构造函数循环引用
        this.asyncTaskStorageMap = applicationContext.getBeansOfType(XAsyncTaskStorage.class).values()
                .stream()
                .collect(Collectors.toMap(XAsyncTaskStorage::code ,Function.identity()));
        this.exceptionHandler = applicationContext.getBean(TaskExecuteExceptionHandler.class);
        this.xAsyncTaskProperties = applicationContext.getBean(XAsyncTaskProperties.class);
    }

    @Override
    public void submit(TaskMateData taskMateData, TaskSubmitParam submitParam) throws IllegalTaskException
    {
        if (StringUtils.isEmpty(taskMateData.getTaskId())){
            taskMateData.setTaskId(UUID.randomUUID().toString());
        }

        if (submitParam.isTrySync()){
            try {
                execute(taskMateData);
            } catch (TaskExecuteException e){
                XAsyncTaskStorage xAsyncTaskStorage = getXAsyncTaskStorage(taskMateData.getStrategy());
                xAsyncTaskStorage.save(taskMateData);
            }
        }else {
            XAsyncTaskStorage xAsyncTaskStorage = getXAsyncTaskStorage(taskMateData.getStrategy());
            xAsyncTaskStorage.save(taskMateData);
        }
    }

    private XAsyncTaskStorage getXAsyncTaskStorage(String strategy) throws IllegalTaskException {
        strategy = StringUtils.isEmpty(strategy) ? xAsyncTaskProperties.getDefaultStrategy() : strategy;
        XAsyncTaskStorage xAsyncTaskStorage = asyncTaskStorageMap.get(strategy);
        if (xAsyncTaskStorage == null){
            throw new IllegalTaskException(MessageFormat.format("XAsync不支持或没有启用【{0}】策略" ,strategy));
        }
        return xAsyncTaskStorage;
    }

    @Override
    public void execute(TaskMateData taskMateData ,TaskExecuteParam executeParam) throws TaskExecuteException, IllegalTaskException
    {
        log.debug("执行任务,{}" ,taskMateData);
        Object targetBean = getBean(taskMateData);

        TaskMateData.PV[] pvs = taskMateData.getPvs();
        Class[] argTypes = new Class[pvs.length];
        Object[] argValues = new Object[pvs.length];

        for (int i = 0; i < pvs.length; i++)
        {
            Class argType = pvs[i].getType();
            if (argType == null){
                log.warn("无法获取匹配的bean，任务无法执行，{}" ,taskMateData);
                throw new IllegalTaskException(MessageFormat.format(
                        "无法获取匹配的bean，任务无法执行，{0}" ,taskMateData));
            }
            Object argValue = pvs[i].getValue();
            argTypes[i] = argType;
            argValues[i] = argValue;
            if (argValue != null) {
                boolean isValidation = validationArg(argType ,argValue);

                if (!isValidation) {
                    log.error("参数值与参数类型不匹配，参数下标:【{}】，声明类型:【{}】，实际类型：【{}】，{}",
                            i, argType, argValue.getClass(), taskMateData);
                    throw new IllegalTaskException(MessageFormat.format(
                            "参数值与参数类型不匹配，参数下标:【{0}】，声明类型:【{1}】，实际类型：【{2}】，{3}",
                            i, argType, argValue.getClass(), taskMateData));
                }
            }
        }

        Method targetMethod = null;
        boolean accessible = false;
        long startTimestamp = System.currentTimeMillis();
        XAsyncTaskStorage xAsyncTaskStorage = getXAsyncTaskStorage(taskMateData.getStrategy());

        try {
            targetMethod = ReflectionUtils.findMethod(targetBean.getClass() ,
                    taskMateData.getMethodName(), argTypes);
            if (targetMethod == null){
                log.warn("无法匹配方法，任务无法执行,{}" ,taskMateData);
                throw new IllegalTaskException(MessageFormat.format(
                        "无法匹配方法，任务无法执行,{0}" ,taskMateData));
            }
            accessible = targetMethod.isAccessible();
            if (!accessible){
                targetMethod.setAccessible(true);
            }
            XAsyncContent.executing();
            targetMethod.invoke(targetBean ,argValues);
            log.debug("任务执行成功，{}" ,taskMateData.getTaskId());
            xAsyncTaskStorage.remove(taskMateData.getTaskId());
        } catch (IllegalAccessException e) {
            log.error("任务无法执行,{}" ,taskMateData ,e);
            xAsyncTaskStorage.remove(taskMateData.getTaskId());
            throw new IllegalTaskException(MessageFormat.format(
                    "任务无法执行,{0}" ,taskMateData) ,e);
        } catch (IllegalTaskException e){
            log.error("非法的任务定义，任务无法执行,{}" ,taskMateData ,e);
            xAsyncTaskStorage.remove(taskMateData.getTaskId());
            throw e;
        }catch (Exception e){
            String errorMsg = MessageFormat.format("异步任务执行失败，{0}", taskMateData);
            log.error(errorMsg ,e);
            exceptionHandler.handle(taskMateData ,errorMsg ,e);
            throw new TaskExecuteException(errorMsg ,e);
        } finally {
            if (targetMethod != null && !accessible){
                targetMethod.setAccessible(false);
            }
            log.info("任务{}耗时{}毫秒" ,taskMateData.getTaskId() ,System.currentTimeMillis() - startTimestamp);
            XAsyncContent.complete();
        }
    }

    private boolean validationArg(Class argType ,Object argValue)
    {
        boolean isImplement = argType.isAssignableFrom(argValue.getClass());
        if (!isImplement && argType.isPrimitive()){
            //兼容基本类型
            if (boolean.class.equals(argType) && argValue instanceof Boolean){
                return true;
            } else if (byte.class.equals(argType) && argValue instanceof Byte){
                return true;
            } else if (char.class.equals(argType) && argValue instanceof Character){
                return true;
            } else if (short.class.equals(argType) && argValue instanceof Short){
                return true;
            } else if (int.class.equals(argType) && argValue instanceof Integer){
                return true;
            } else if (float.class.equals(argType) && argValue instanceof Float){
                return true;
            } else if (long.class.equals(argType) && argValue instanceof Long){
                return true;
            } else if (double.class.equals(argType) && argValue instanceof Double){
                return true;
            }
        }

        return isImplement;
    }

    /* 解析执行对象 */
    private Object getBean(TaskMateData taskMateData) throws IllegalTaskException {
        String beanName = taskMateData.getBeanName();
        Class<?> beanClass = taskMateData.getBeanClass();

        Object target = null;
        try {
            if (beanName != null){
                if (beanClass != null){
                    target = applicationContext.getBean(beanName ,beanClass);
                }else {
                    target = applicationContext.getBean(beanName);
                }
            }else if (beanClass != null){
                target = applicationContext.getBean(beanClass);
            }else {
                log.warn("无法获取匹配的bean，任务无法执行，{}" ,taskMateData);
                throw new IllegalTaskException(MessageFormat.format("无法获取匹配的bean，任务无法执行，{0}" ,taskMateData));
            }
        } catch (BeansException e){
            log.warn("无法获取匹配的bean，任务无法执行，{}" ,taskMateData ,e);
            throw new IllegalTaskException(MessageFormat.format("无法获取匹配的bean，任务无法执行，{0}" ,taskMateData));
        }

        if (target == null)
        {
            log.warn("无法获取匹配的bean，任务无法执行，{}" ,taskMateData);
            throw new IllegalTaskException(MessageFormat.format("无法获取匹配的bean，任务无法执行，{0}" ,taskMateData));
        }

        return target;
    }

}
