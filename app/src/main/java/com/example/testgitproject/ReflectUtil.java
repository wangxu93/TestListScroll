package com.example.testgitproject;

import android.text.TextUtils;

import java.lang.reflect.Method;

public class ReflectUtil {
    /**
     * 遍历当前类以及父类去查找并执行方法
     *
     * @param targetObject
     * @param methodName
     * @param params
     * @param paramTypes
     * @return
     */
    public static Object invokeMethod(Object targetObject, String methodName, Object[] params, Class[] paramTypes) {
        Object returnObj = null;
        if (targetObject == null || TextUtils.isEmpty(methodName)) {
            return null;
        }
        Class cls = targetObject.getClass();
        Method method = null;
        for (; cls != Object.class; cls = cls.getSuperclass()) {
            try {
                method = cls.getDeclaredMethod(methodName, paramTypes);
                break;
            } catch (Exception e) {
            }
        }
        if (method != null) {
            method.setAccessible(true);
            try {
                returnObj = method.invoke(targetObject, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnObj;
    }
}
