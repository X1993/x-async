package com.github.xasync.aop;

import com.github.xasync.TaskMateData;
import com.github.xasync.TaskSubmitParam;
import com.github.xasync.XAsyncContent;
import com.github.xasync.XAsyncTaskExecutor;
import com.github.xasync.exception.IllegalTaskException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author X1993
 * @date 2022/8/16
 * @description
 */
public class XAsyncTaskAdvisor extends DefaultPointcutAdvisor implements MethodInterceptor, ApplicationContextAware {

    public XAsyncTaskAdvisor() {
        setPointcut(new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass)
            {
                return AnnotatedElementUtils.findMergedAnnotation(method , XAsync.class) != null;
            }
        });
        setAdvice(this);
    }

    private ApplicationContext applicationContext;

    private XAsyncTaskExecutor xAsyncTaskExecutor;

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.xAsyncTaskExecutor = applicationContext.getBean(XAsyncTaskExecutor.class);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        Method method = invocation.getMethod();
        XAsync xAsync = AnnotatedElementUtils.findMergedAnnotation(method, XAsync.class);
        if (XAsyncContent.isExecuting()){
            return invocation.proceed();
        }

        String beanName = getProxyBeanName(invocation);

        Object[] args = invocation.getArguments();
        Class<?>[] parameterTypes = method.getParameterTypes();
        int length = parameterTypes.length;
        TaskMateData.PV[] pvs = new TaskMateData.PV[length];
        for (int i = 0; i < length; i++) {
            pvs[i] = new TaskMateData.PV()
                    .setType(parameterTypes[i])
                    .setValue(args[i]);
        }

        String taskId = parseTaskId(xAsync.taskId() ,invocation);

        TaskMateData taskMateData = new TaskMateData()
                .setBeanName(beanName)
                .setMethodName(method.getName())
                .setPvs(pvs)
                .setTaskId(taskId)
                .setStrategy(xAsync.strategy());

        xAsyncTaskExecutor.submit(taskMateData ,new TaskSubmitParam().setTrySync(xAsync.trySync()));
        return null;
    }

    private String parseTaskId(String keySpel ,MethodInvocation invocation)
    {
        if (StringUtils.isEmpty(keySpel)){
            return null;
        }
        Method method = invocation.getMethod();
        String[] parameterNames = discoverer.getParameterNames(method);
        Object[] args = invocation.getArguments();

        EvaluationContext evaluationContext = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            evaluationContext.setVariable(parameterNames[i] ,args[i]);
        }

        Expression keyExpression = expressionParser.parseExpression(keySpel);
        String taskId = keyExpression.getValue(evaluationContext, String.class);

        return taskId;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 4;
    }

    public String getProxyBeanName(MethodInvocation invocation) throws IllegalTaskException
    {
        Object proxy = ((ProxyMethodInvocation) invocation).getProxy();
        for (Map.Entry<String, Object> entry : applicationContext.getBeansOfType(Object.class).entrySet()) {
            Object bean = entry.getValue();
            if (proxy == bean){
                return entry.getKey();
            }
        }
        throw new IllegalTaskException();
    }

}
