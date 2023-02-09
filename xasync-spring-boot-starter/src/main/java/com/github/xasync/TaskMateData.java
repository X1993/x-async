package com.github.xasync;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.lang.reflect.Parameter;

/**
 * @author X1993
 * @date 2022/6/2
 * @description
 */
@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskMateData implements Serializable {

    /**
     * 任务id
     */
    String taskId;

    /**
     * bean name
     */
    String beanName;

    /**
     * bean class
     */
    Class beanClass;

    /**
     * 方法名
     * 注意：如果bean是通过子类继承实现增强的（例如cglib代码），那么该方法修饰符不能是 private。
     * 建议只访问public方法
     */
    String methodName;

    /**
     * 参数列表
     */
    PV[] pvs = new PV[0];

    /**
     * 参数
     */
    @Data
    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PV implements Serializable{

        /**
         * 参数类型 {@link Parameter#getType()}
         */
        Class type;

        /**
         * 序列化之后的值
         */
        Object value;

    }

}
