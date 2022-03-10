package com.pooch.api.utils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResultSetMapperUtils<T> {
    // This method is already implemented in package
    // but as far as I know it accepts only public class attributes
    private void setProperty(Object clazz, String fieldName, Object columnValue, String columnTypeName) {
        try {
            // get all fields of the class (including public/protected/private)
            Field field = clazz.getClass().getDeclaredField(fieldName);
            // this is necessary in case the field visibility is set at private
            field.setAccessible(true);

            // log.info("GenericType={}, Type={}, SimpleName={},
            // DeclaringClass={}",field.getGenericType().getTypeName(), field.getType(),
            // field.getClass().getSimpleName(), field.getDeclaringClass().getSimpleName());
            if (columnTypeName.equalsIgnoreCase("INT UNSIGNED".toLowerCase())||
                    columnTypeName.equalsIgnoreCase("INT".toLowerCase())) {
                if (null != columnValue) {
                    Number i = new Double(columnValue.toString());
                    if (field.getType().equals(Long.class)) {
                        field.set(clazz, i.longValue());
                    } else if (field.getType().equals(Integer.class)) {
                        field.set(clazz, i.intValue());
                    } else {
                        field.set(clazz, columnValue);
                    }
                }

            } else {
                field.set(clazz, columnValue);
            }

        } catch (NoSuchFieldException e) {
            log.warn("NoSuchFieldException msg={}, fieldName={}, columnValue={}, columnTypeName={}, clazz={}", e.getLocalizedMessage(), fieldName, columnValue, columnTypeName, clazz);
        } catch (SecurityException e) {
            log.warn("SecurityException msg={}, fieldName={}, columnValue={}, columnTypeName={}, clazz={}", e.getLocalizedMessage(), fieldName, columnValue, columnTypeName, clazz);
        } catch (IllegalArgumentException e) {
            log.warn("IllegalArgumentException msg={}, fieldName={}, columnValue={}, columnTypeName={}, clazz={}", e.getLocalizedMessage(), fieldName, columnValue, columnTypeName, clazz);
            // e.printStackTrace();
        } catch (IllegalAccessException e) {
            log.warn("IllegalAccessException msg={}, fieldName={}, columnValue={}, columnTypeName={}, clazz={}", e.getLocalizedMessage(), fieldName, columnValue, columnTypeName, clazz);
        } catch (Exception e) {
            log.warn("error msg={}, fieldName={}, columnValue={}, columnTypeName={}, clazz={}", e.getLocalizedMessage(), fieldName, columnValue, columnTypeName, clazz);
        }
    }

    public List<T> mapRersultSetToList(ResultSet rs, Class<T> clazz) {
        List<T> outputList = null;
        try {
            // make sure resultset is not null
            if (rs != null) {

                while (rs.next()) {

                    // log.info("fields");

                    T bean = mapRersultSetToSingleObject(rs, clazz);

                    if (outputList == null) {
                        outputList = new ArrayList<T>();
                    }
                    outputList.add(bean);
                } // EndOf while(rs.next())
            } else {
                // ResultSet is empty
                log.warn("ResultSet is empty");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputList;
    }

    public T mapRersultSetToSingleObject(ResultSet rs, Class<T> clazz) {

        T bean = null;

        try {
            // make sure resultset is not null
            if (rs != null) {

                // get the resultset metadata
                ResultSetMetaData rsmd = rs.getMetaData();

                bean = (T) clazz.newInstance();

                for (int index = 0; index < rsmd.getColumnCount(); index++) {
                    // get the SQL column name
                    String columnName = rsmd.getColumnName(index + 1);

                    // get the value of the SQL column
                    Object columnValue = rs.getObject(index + 1);

                    String columnTypeName = rsmd.getColumnTypeName(index + 1);

                    if (columnValue != null) {

                        // log.info("columnName={}, columnValue={}, columnTypeName={}", columnName, columnValue,
                        // columnTypeName);

                        this.setProperty(bean, columnName, columnValue, columnTypeName);

                    }

                } // EndOf for(_iterator...)

            } else {
                // ResultSet is empty
                log.warn("ResultSet is empty");
                return null;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return bean;
    }
}
