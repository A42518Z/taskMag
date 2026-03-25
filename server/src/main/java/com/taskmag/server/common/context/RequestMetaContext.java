package com.taskmag.server.common.context;

public final class RequestMetaContext {
    private static final ThreadLocal<RequestMeta> HOLDER = new ThreadLocal<>();

    private RequestMetaContext() {
    }

    public static void set(RequestMeta requestMeta) {
        HOLDER.set(requestMeta);
    }

    public static RequestMeta get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
