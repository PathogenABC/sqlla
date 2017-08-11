package com.greenbean.poplar.sqlla;

import java.lang.reflect.*;

/**
 * Created by chrisding on 2016/11/10.
 * <br/>Function: 类型工具
 */
public final class TypeUtils {

    public static Class<?> getRawType(Type type) {
        if (type == null) throw new NullPointerException("type == null");

        if (type instanceof Class<?>) {
            // Type is a normal class.
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) throw new IllegalArgumentException();
            return (Class<?>) rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }

        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }

    public static Class<?> getGenericComponentRawType(Type type) {
        return getGenericComponentRawType(0, type);
    }

    public static Class<?> getGenericComponentRawType(int index, Type type) {
        Type componentType = getGenericComponentType(index, type);
        if (componentType != null) {
            return getRawType(componentType);
        }
        return null;
    }

    public static Type getGenericComponentType(int index, Type type) {
        if (type instanceof ParameterizedType) {
            return getGenericComponentType(index, (ParameterizedType) type);
        }
        return null;
    }

    public static Type getGenericComponentType(int index, ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
            throw new IllegalArgumentException(
                    "Index " + index + " not in range [0," + types.length + ") for " + type);
        }
        Type paramType = types[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    /**
     * @return primitive or string level
     */
    public static Object toPrimitiveOrString(String value, Class<?> typeClass) {
        if (typeClass == String.class) {
            return value;
        }
        if (!typeClass.isPrimitive()) {
            return null;
        }
        if (typeClass == Byte.class || typeClass == byte.class) {
            return Byte.valueOf(value);
        }
        if (typeClass == Short.class || typeClass == short.class) {
            return Short.valueOf(value);
        }
        if (typeClass == Integer.class || typeClass == int.class) {
            return Integer.valueOf(value);
        }
        if (typeClass == Long.class || typeClass == long.class) {
            return Long.valueOf(value);
        }
        if (typeClass == Float.class || typeClass == float.class) {
            return Float.valueOf(value);
        }
        if (typeClass == Double.class || typeClass == double.class) {
            return Double.valueOf(value);
        }
        if (typeClass == Character.class || typeClass == char.class) {
            return value.charAt(0);
        }
        if (typeClass == Boolean.class || typeClass == boolean.class) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive()
                || type == Void.class || type == Byte.class || type == Short.class
                || type == Integer.class || type == Long.class || type == Float.class
                || type == Double.class || type == Boolean.class || type == Character.class;
    }

    public static Object createDefault(Class<?> typeClass) {

        if (Number.class.isAssignableFrom(typeClass)
                || typeClass == byte.class || typeClass == short.class
                || typeClass == int.class || typeClass == long.class
                || typeClass == float.class || typeClass == double.class) {

        }
        if (typeClass == Byte.class || typeClass == byte.class) {
            return (byte) 0;
        }

        if (typeClass == Short.class || typeClass == short.class) {
            return (short) 0;
        }

        if (typeClass == Integer.class || typeClass == int.class) {
            return 0;
        }

        if (typeClass == Long.class || typeClass == long.class) {
            return 0L;
        }

        if (typeClass == Float.class || typeClass == float.class) {
            return 0F;
        }

        if (typeClass == Double.class || typeClass == double.class) {
            return 0D;
        }

        if (typeClass == Character.class || typeClass == char.class) {
            return Character.MIN_VALUE;
        }
        if (typeClass == Boolean.class || typeClass == boolean.class) {
            return Boolean.FALSE;
        }
        return null;
    }

    public static void main(String[] args) {
        Object aDefault = createDefault(Boolean.class);
        System.out.printf("default = " + aDefault);
    }
}
