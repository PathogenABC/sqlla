package com.greenbean.poplar.sqlla.view;

import java.util.*;

/**
 * Created by chrisding on 2017/5/16.
 * Function: 结果集视图的实现类
 */
class ViewObjectImpl implements ViewObject {

    private final Map<String, Object> mValues;
    private Set<Entry> mEntries;

    ViewObjectImpl(Map<String, Object> values) {
        if (values == null) {
            mValues = Collections.emptyMap();
        } else {
            mValues = values;
        }
    }

    @Override
    public int pairs() {
        return mValues.size();
    }

    @Override
    public Object optValue(String key, Object nullVal) {
        return mValues.containsKey(key) ? mValues.get(key) : nullVal;
    }

    @Override
    public Object optValue(String key) {
        return optValue(key, null);
    }

    @Override
    public byte optByte(String key, byte nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Number) {
                return (byte) value;
            }
            if (value instanceof String) {
                return Byte.parseByte((String) value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a numeric");
        }
        return nullVal;
    }

    @Override
    public byte optByte(String key) {
        return optByte(key, (byte) 0);
    }

    @Override
    public short optShort(String key, short nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Number) {
                return (short) value;
            }
            if (value instanceof String) {
                return Short.parseShort((String) value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a numeric");
        }
        return nullVal;
    }

    @Override
    public short optShort(String key) {
        return optShort(key, (short) 0);
    }

    @Override
    public int optInt(String key, int nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Number) {
                return (int) value;
            }
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a numeric");
        }
        return nullVal;
    }

    @Override
    public int optInt(String key) {
        return optInt(key, 0);
    }

    @Override
    public long optLong(String key, long nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Number) {
                return (long) value;
            }
            if (value instanceof String) {
                return Long.parseLong((String) value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a numeric");
        }
        return nullVal;
    }

    @Override
    public long optLong(String key) {
        return optLong(key, 0L);
    }

    @Override
    public float optFloat(String key, float nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Number) {
                return (float) value;
            }
            if (value instanceof String) {
                return Float.parseFloat((String) value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a numeric");
        }
        return nullVal;
    }

    @Override
    public float optFloat(String key) {
        return optFloat(key, 0F);
    }

    @Override
    public double optDouble(String key, double nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Number) {
                return (double) value;
            }
            if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a numeric");
        }
        return nullVal;
    }

    @Override
    public double optDouble(String key) {
        return optDouble(key, 0D);
    }

    @Override
    public boolean optBoolean(String key, boolean nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Boolean) {
                return (boolean) value;
            }
            if (value instanceof Number) {
                return value != 0;
            }
            if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a boolean like");
        }
        return nullVal;
    }

    @Override
    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }

    @Override
    public String optString(String key, String nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value == null) return null;
            Class<?> valueClass = value.getClass();
            if (valueClass.isPrimitive() || valueClass == String.class) {
                return String.valueOf(value);
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a string like");
        }
        return nullVal;
    }

    @Override
    public String optString(String key) {
        return optString(key, null);
    }

    @Override
    public byte[] optBytes(String key, byte[] nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof byte[]) {
                return (byte[]) value;
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a byte array");
        }
        return nullVal;
    }

    @Override
    public byte[] optBytes(String key) {
        return optBytes(key, null);
    }

    @Override
    public Date optDate(String key, Date nullVal) {
        if (mValues.containsKey(key)) {
            Object value = optValue(key);
            if (value instanceof Date) {
                return (Date) value;
            }
            throw new IllegalArgumentException("value of key[" + key + "] is not a byte array");
        }
        return nullVal;
    }

    @Override
    public java.util.Date optDate(String key) {
        return optDate(key, null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(0);
        builder.append('{');
        for (Entry entry : this) {
            builder.append(entry.key()).append(':');
            Object val = entry.val();
            if (val == null) {
                builder.append("null");
            } else if (val instanceof Number) {
                if (val instanceof Float || val instanceof Double) {
                    builder.append(((Number) val).doubleValue());
                } else {
                    builder.append(((Number) val).longValue());
                }
            } else if (val instanceof Boolean) {
                builder.append((boolean) val);
            } else if (val instanceof byte[]) {
                builder.append("bytes(").append(((byte[]) val).length).append(')');
            } else {
                builder.append('\'').append(String.valueOf(val)).append('\'');
            }
            builder.append(", ");
        }
        builder.append('}');
        int length = builder.length();
        if (length > 2) {
            builder.delete(length - 3, length - 2);
        }
        return builder.toString();
    }

    @Override
    public Iterator<Entry> iterator() {
        if (mEntries == null) {
            Set<Map.Entry<String, Object>> rawEntries = mValues.entrySet();
            Set<Entry> entries = new HashSet<>();
            for (Map.Entry<String, Object> entry : rawEntries) {
                entries.add(new EntryImpl(entry));
            }
            mEntries = entries;
        }
        return mEntries.iterator();
    }

    private static class EntryImpl implements Entry {

        private final Map.Entry<String, Object> mTargetEntry;

        private EntryImpl(Map.Entry<String, Object> targetEntry) {
            this.mTargetEntry = targetEntry;
        }

        @Override
        public String key() {
            return mTargetEntry.getKey();
        }

        @Override
        public Object val() {
            return mTargetEntry.getValue();
        }
    }
}
