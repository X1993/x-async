package com.github.xasync;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author X1993
 * @date 2023/2/8
 * @description
 */
@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskSubmitParam {

    /**
     * 是否尝试同步执行一次，失败再异步重试
     * @return
     */
    boolean trySync = true;

}
