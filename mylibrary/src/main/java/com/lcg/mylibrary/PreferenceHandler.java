package com.lcg.mylibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Preference数据存储于获取
 *
 * @author lei.chuguang
 * @since 2013-12-20
 */
public class PreferenceHandler {
    private static PreferenceHandler mPreferenceConfig;
    private Context mContext;
    private Editor edit = null;
    private SharedPreferences mSharedPreferences;
    private Boolean isLoad = false;

    private PreferenceHandler() {
    }

    /**
     * 获得系统资源类
     *
     * @return
     */
    public static PreferenceHandler getInstance() {
        if (mPreferenceConfig == null) {
            mPreferenceConfig = new PreferenceHandler();
        }
        return mPreferenceConfig;
    }

    public PreferenceHandler init(Context context) {
        if (isLoad) {
            return this;
        }
        mContext = context;
        try {
            mSharedPreferences = mContext.getSharedPreferences("xueba",
                    Context.MODE_PRIVATE);
            edit = mSharedPreferences.edit();
            isLoad = true;
        } catch (Exception e) {
            e.printStackTrace();
            isLoad = false;
        }
        return this;
    }

    public Boolean isLoadConfig() {
        return isLoad;
    }

    public void close() {
    }

    public boolean isClosed() {
        return false;
    }

    public void setString(String key, String value) {
        edit.putString(key, value);
        edit.commit();
    }

    public void setInt(String key, int value) {
        edit.putInt(key, value);
        edit.commit();
    }

    public void setBoolean(String key, Boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public void setByte(String key, byte[] value) {
        setString(key, String.valueOf(value));
    }

    public void setShort(String key, short value) {
        setString(key, String.valueOf(value));
    }

    public void setLong(String key, long value) {
        edit.putLong(key, value);
        edit.commit();
    }

    public void setFloat(String key, float value) {
        edit.putFloat(key, value);
        edit.commit();
    }

    public void setDouble(String key, double value) {
        setString(key, String.valueOf(value));
    }

    public void setString(int resID, String value) {
        setString(this.mContext.getString(resID), value);

    }

    public void setInt(int resID, int value) {
        setInt(this.mContext.getString(resID), value);
    }

    public void setBoolean(int resID, Boolean value) {
        setBoolean(this.mContext.getString(resID), value);
    }

    public void setByte(int resID, byte[] value) {
        setByte(this.mContext.getString(resID), value);
    }

    public void setShort(int resID, short value) {
        setShort(this.mContext.getString(resID), value);
    }

    public void setLong(int resID, long value) {
        setLong(this.mContext.getString(resID), value);
    }

    public void setFloat(int resID, float value) {
        setFloat(this.mContext.getString(resID), value);
    }

    public void setDouble(int resID, double value) {
        setDouble(this.mContext.getString(resID), value);
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, Boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public byte getByte(String key, byte defaultValue) {
        try {
            return (byte) mSharedPreferences.getInt(key, defaultValue);
        } catch (Exception e) {
        }
        return 0;
    }

    public short getShort(String key, Short defaultValue) {
        try {
            return Short.valueOf(getString(key, ""));
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public long getLong(String key, Long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    public float getFloat(String key, Float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    public double getDouble(String key, Double defaultValue) {
        try {
            return Double.valueOf(getString(key, ""));
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public String getString(int resID, String defaultValue) {
        return getString(this.mContext.getString(resID), defaultValue);
    }

    public int getInt(int resID, int defaultValue) {
        return getInt(this.mContext.getString(resID), defaultValue);
    }

    public boolean getBoolean(int resID, Boolean defaultValue) {
        return getBoolean(this.mContext.getString(resID), defaultValue);
    }

    public byte getByte(int resID, byte defaultValue) {
        return getByte(this.mContext.getString(resID), defaultValue);
    }

    public short getShort(int resID, Short defaultValue) {
        return getShort(this.mContext.getString(resID), defaultValue);
    }

    public long getLong(int resID, Long defaultValue) {
        return getLong(this.mContext.getString(resID), defaultValue);
    }

    public float getFloat(int resID, Float defaultValue) {
        return getFloat(this.mContext.getString(resID), defaultValue);
    }

    public double getDouble(int resID, Double defaultValue) {
        return getDouble(this.mContext.getString(resID), defaultValue);
    }

    @Deprecated
    public void setConfig(Object entity) {
        if (entity != null) {
            Class<?> clazz = entity.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (isBaseDateType(field)) {
                    field.setAccessible(true);
                    setValue(field, field.getName(), entity);
                }
            }
        }
    }

    public void setConfigFull(Object entity) {
        if (entity != null) {
            Class<?> clazz = entity.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (isBaseDateType(field)) {
                    field.setAccessible(true);
                    setValue(field, clazz.getName().replace(".", "_") + "_"
                            + field.getName(), entity);
                }
            }
        }
    }

    private void setValue(Field field, String columnName, Object entity) {
        try {
            Class<?> clazz = field.getType();
            if (clazz.equals(String.class)) {
                setString(columnName, (String) field.get(entity));
            } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                setInt(columnName, (Integer) field.get(entity));
            } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
                setFloat(columnName, (Float) field.get(entity));
            } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                setDouble(columnName, (Double) field.get(entity));
            } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
                setShort(columnName, (Short) field.get(entity));
            } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                setLong(columnName, (Long) field.get(entity));
            } else if (clazz.equals(Boolean.class)
                    || clazz.equals(boolean.class)) {
                setBoolean(columnName, (Boolean) field.get(entity));
            } else if (clazz.equals(Date.class)) {
                setLong(columnName, ((Date) field.get(entity)).getTime());
            } else if (clazz.equals(java.sql.Date.class)) {
                setLong(columnName,
                        ((java.sql.Date) field.get(entity)).getTime());
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Deprecated
    public <T> T getConfig(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        T entity = null;
        try {
            entity = (T) clazz.newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                if (isBaseDateType(field)) {
                    field.setAccessible(true);
                    getValue(field, field.getName(), entity);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public <T> T getConfigFull(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        T entity = null;
        try {
            entity = (T) clazz.newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                if (isBaseDateType(field)) {
                    field.setAccessible(true);
                    getValue(field, clazz.getName().replace(".", "_") + "_"
                            + field.getName(), entity);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private <T> void getValue(Field field, String columnName, T entity) {
        try {
            Class<?> clazz = field.getType();
            if (clazz.equals(String.class)) {
                field.set(entity, getString(columnName, ""));
            } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                field.set(entity, getInt(columnName, 0));
            } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
                field.set(entity, getFloat(columnName, 0f));
            } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                field.set(entity, getDouble(columnName, 0.0));
            } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
                field.set(entity, getShort(columnName, (short) 0));
            } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                field.set(entity, getLong(columnName, 0l));
            } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
                field.set(entity, getByte(columnName, (byte) 0));
            } else if (clazz.equals(Boolean.class)
                    || clazz.equals(boolean.class)) {
                field.set(entity, getBoolean(columnName, false));
            } else if (clazz.equals(Date.class)) {
                field.set(entity, new Date(getLong(columnName, 0l)));
            } else if (clazz.equals(java.sql.Date.class)) {
                field.set(entity, new java.sql.Date(getLong(columnName, 0l)));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        edit.remove(key);
        edit.commit();
    }

    public void remove(String... keys) {
        for (String key : keys)
            remove(key);
    }

    public void clear() {
        edit.clear();
        edit.commit();
    }

    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    public boolean containsConfigFull(Class<?> clazz) {
        boolean b = false;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            boolean contains = contains(clazz.getName().replace(".", "_") + "_"
                    + field.getName());
            if (contains) {
                b = true;
                break;
            }
        }
        return b;
    }

    public void open() {

    }

    /**
     * 是否为基本的数据类型
     *
     * @param field
     * @return
     */
    private boolean isBaseDateType(Field field) {
        Class<?> clazz = field.getType();
        return clazz.equals(String.class) || clazz.equals(Integer.class)
                || clazz.equals(Byte.class) || clazz.equals(Long.class)
                || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class)
                || clazz.equals(Boolean.class) || clazz.equals(Date.class)
                || clazz.equals(java.sql.Date.class) || clazz.isPrimitive();
    }
}
