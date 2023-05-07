package com.hdfk7.boot.starter.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.hdfk7.boot.starter.common.component.RedisSequence;

public class IdUtil {
    public static String idStr() {
        return SpringUtil.getBean(RedisSequence.class).nextIdStr();
    }

    public static long id() {
        return SpringUtil.getBean(RedisSequence.class).nextId();
    }
}
