package com.taskmag.server.common.util;

import cn.hutool.core.lang.UUID;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static String uuid() {
        return UUID.fastUUID().toString(true);
    }
}
