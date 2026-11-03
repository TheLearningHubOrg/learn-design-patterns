package org.thelearninghub.patterns.structural;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

/**
 * Dynamic proxy built at runtime by the JDK. One handler serves every method of any
 * interface, which is how frameworks add logging, transactions or metrics to beans
 * without a hand-written proxy class per interface.
 */
public final class RecordingProxy {

    private RecordingProxy() {
    }

    public static <T> T wrap(Class<T> type, T target, List<String> log) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException(type.getName() + " is not an interface; JDK proxies need one");
        }
        InvocationHandler handler = (proxy, method, args) -> {
            log.add(method.getName() + Arrays.toString(args == null ? new Object[0] : args));
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getCause(); // rethrow what the target threw, not the reflection wrapper
            }
        };
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
    }
}
