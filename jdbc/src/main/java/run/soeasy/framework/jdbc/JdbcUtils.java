package run.soeasy.framework.jdbc;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.LinkedMultiValueMap;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * JDBC工具类，提供JDBC操作中的常用静态方法，包括结果集处理、列名解析、参数设置、数据类型判断等，
 * 简化JDBC编程中的重复工作，统一处理数据库交互的常见场景（如ResultSet解析、PreparedStatement参数绑定等）。
 * 
 * <p>该类为工具类（{@link UtilityClass}），不可实例化，所有方法均为静态方法，操作可能抛出{@link SQLException}。
 * 
 * @author soeasy.run
 * @see ResultSet
 * @see PreparedStatement
 * @see ResultSetMetaData
 * @see SQLException
 */
@UtilityClass
public final class JdbcUtils {

    /**
     * 从结果集中提取一行数据，返回包含列值的对象数组
     * 
     * @param resultSet 结果集（包含待提取的行数据）
     * @param size 行中的列数（需与结果集的实际列数匹配）
     * @return 包含一行数据的Object数组，数组长度为size，元素顺序与列顺序一致
     * @throws SQLException 当结果集操作失败时抛出（如列索引越界）
     */
    public static Object[] getRowValues(ResultSet resultSet, int size) throws SQLException {
        Object[] values = new Object[size];
        for (int i = 1; i <= size; i++) {
            values[i - 1] = resultSet.getObject(i);
        }
        return values;
    }

    /**
     * 获取结果集中所有列的名称数组（基于结果集元数据的列数）
     * 
     * @param rsmd 结果集元数据（包含列信息）
     * @return 列名数组，长度为结果集的列数，元素为列名
     * @throws SQLException 当元数据操作失败时抛出
     * @see #getColumnNames(ResultSetMetaData, int)
     */
    public static String[] getColumnNames(ResultSetMetaData rsmd) throws SQLException {
        return getColumnNames(rsmd, rsmd.getColumnCount());
    }

    /**
     * 获取结果集中指定数量列的名称数组
     * 
     * @param rsmd 结果集元数据（包含列信息）
     * @param size 需要获取的列数（需小于等于元数据的总列数）
     * @return 列名数组，长度为size，元素为前size列的名称
     * @throws SQLException 当元数据操作失败时抛出
     * @see #lookupColumnName(ResultSetMetaData, int)
     */
    public static String[] getColumnNames(ResultSetMetaData rsmd, int size) throws SQLException {
        String[] names = new String[size];
        for (int i = 1; i <= size; i++) {
            names[i - 1] = lookupColumnName(rsmd, i);
        }
        return names;
    }

    /**
     * 判断类型是否为数据库支持的常见数据类型
     * 
     * <p>支持的类型包括：
     * <ul>
     * <li>基本类型及其包装类（如int、Integer、double等）；
     * <li>字符串类型（{@link String}）；
     * <li>日期时间类型（{@link Date}、{@link java.util.Date}、{@link Time}、{@link Timestamp}）；
     * <li>数据库大对象类型（{@link Array}、{@link Blob}、{@link Clob}、{@link NClob}、{@link Reader}）；
     * <li>高精度数字类型（{@link BigDecimal}）。
     * </ul>
     * 
     * @param type 待判断的类型
     * @return 是数据库支持的类型则返回true，否则返回false
     */
    public static boolean isDataBaseType(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
                || Date.class.isAssignableFrom(type) || java.util.Date.class.isAssignableFrom(type)
                || Time.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)
                || Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
                || Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
                || Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type);
    }

    /**
     * 根据结果集元数据和列索引查找列名（符合JDBC 4.0规范）
     * 
     * <p>查找逻辑：
     * 1. 优先使用{@link ResultSetMetaData#getColumnLabel(int)}获取列的别名（SQL中AS指定的名称）；
     * 2. 若别名为空，则使用{@link ResultSetMetaData#getColumnName(int)}获取列的实际名称。
     * 
     * @param resultSetMetaData 结果集元数据
     * @param columnIndex 列索引（从1开始）
     * @return 列的名称（别名或实际名称）
     * @throws SQLException 当元数据操作失败时抛出（如列索引无效）
     */
    public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
        String name = resultSetMetaData.getColumnLabel(columnIndex);
        if (StringUtils.isEmpty(name)) {
            name = resultSetMetaData.getColumnName(columnIndex);
        }
        return name;
    }

    /**
     * 将结果集当前行转换为列名-值的多值映射（MultiValueMap）
     * 
     * @param rs 结果集（当前行指向待转换的行）
     * @return 包含列名和对应值的{@link LinkedMultiValueMap}，键为列名，值为对应列的值
     * @throws SQLException 当结果集或元数据操作失败时抛出
     */
    public static MultiValueMap<String, Object> getRowValueMap(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int cols = metaData.getColumnCount();
        MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>(cols, 1);
        for (int i = 1; i <= cols; i++) {
            String name = JdbcUtils.lookupColumnName(metaData, i);
            Object value = rs.getObject(i);
            values.add(name, value);
        }
        return values;
    }

    /**
     * 为预处理语句（PreparedStatement）设置参数
     * 
     * <p>处理逻辑：
     * 1. 遍历参数数组，依次为PreparedStatement的占位符设置值（索引从1开始）；
     * 2. 若参数为枚举（{@link Enum}），则转换为枚举的名称（{@link Enum#name()}）；
     * 3. 其他类型直接通过{@link PreparedStatement#setObject(int, Object)}设置。
     * 
     * @param preparedStatement 预处理语句（包含参数占位符）
     * @param args 待设置的参数数组（与占位符数量需匹配）
     * @throws SQLException 当参数设置失败时抛出（如参数类型不匹配、索引越界）
     */
    public static void setParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
        if (args == null || args.length == 0) {
            return;
        }

        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            if (value != null && value instanceof Enum) {
                // 枚举类型转换为名称字符串
                value = ((Enum<?>) value).name();
            }
            preparedStatement.setObject(i + 1, value);
        }
    }
}