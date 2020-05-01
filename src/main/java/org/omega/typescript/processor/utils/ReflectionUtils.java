package org.omega.typescript.processor.utils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by kibork on 5/1/2020.
 */
public class ReflectionUtils {

    // ---------------- Fields & Constants --------------

    // ------------------ Logic      --------------------

    public static <T> T call(final Object obj, final String method, final Object... params) {
        return callOnClass(obj.getClass(), obj, method, Arrays.stream(params).map(Object::getClass).toArray(Class[]::new), params);
    }

    @SuppressWarnings("unchecked")
    public static <T> T callOnClass(final Class<?> clazz, final Object obj, final String method, final Class<?>[] parameterTypes, final Object[] params) {
        try {
            final Method declaredMethod = clazz.getDeclaredMethod(method, parameterTypes);
            declaredMethod.setAccessible(true);
            return (T)declaredMethod.invoke(obj, params);
        } catch (Exception e) {
            throw new RuntimeException("Unable to call " + method + " on class " + clazz, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(final Object obj, final String fieldName) {
        final Class<?> clazz = obj.getClass();
        try {
            return (T)Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.getName().equals(fieldName))
                    .peek(field -> field.setAccessible(true))
                    .findAny().orElseThrow();
        } catch (Exception e) {
            throw new RuntimeException("Unable get field " + fieldName + " on class " + clazz, e);
        }
    }
}
