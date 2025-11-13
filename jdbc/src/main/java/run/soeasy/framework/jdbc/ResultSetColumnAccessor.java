package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 基于列索引的ResultSet属性访问器，继承自{@link AbstractResultSetAccessor}，
 * 用于通过列索引访问{@link ResultSet}中的特定列数据，支持获取列值、设置列值、获取列名及类型描述符，
 * 是ResultSet列数据访问的具体实现之一（按索引访问）。
 * 
 * <p>该类通过列索引定位目标列，封装了ResultSet的getObject、updateObject等操作，
 * 自动处理{@link SQLException}并转换为{@link JdbcException}，简化ResultSet列数据的读写操作。
 * 
 * @author soeasy.run
 * @see AbstractResultSetAccessor
 * @see ResultSet
 */
public class ResultSetColumnAccessor extends AbstractResultSetAccessor {

    /**
     * 目标列的索引（从1开始，与ResultSet的列索引规则一致）
     */
    private final int columnIndex;

    /**
     * 构造基于列索引的ResultSet访问器
     * 
     * @param resultSet 待访问的ResultSet（非空）
     * @param columnIndex 列索引（需为有效索引，1 ≤ columnIndex ≤ 列总数）
     */
    public ResultSetColumnAccessor(@NonNull ResultSet resultSet, int columnIndex) {
        super(resultSet);
        this.columnIndex = columnIndex;
    }

    /**
     * 获取当前列的列值（通过列索引）
     * 
     * @return 列的当前值（可能为null，类型由数据库列类型决定）
     * @throws JdbcException 当ResultSet操作失败时抛出（如列索引无效、结果集已关闭等）
     */
    @Override
    public Object get() throws JdbcException{
        try {
            return getResultSet().getObject(columnIndex);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    /**
     * 获取当前访问的列索引
     * 
     * @return 列索引（从1开始）
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * 获取当前列的名称（基于元数据，优先使用别名）
     * 
     * @return 列的名称（通过{@link JdbcUtils#lookupColumnName(ResultSetMetaData, int)}获取）
     * @throws JdbcException 当获取元数据或列名失败时抛出
     */
    @Override
    public String getName() throws JdbcException{
        try {
            return JdbcUtils.lookupColumnName(getResultSetMetaData(), columnIndex);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    /**
     * 获取当前列的类型描述符（基于数据库列的实际类型）
     * 
     * <p>处理流程：
     * 1. 通过{@link ResultSetMetaData#getColumnClassName(int)}获取列的类名；
     * 2. 加载该类名对应的{@link Class}对象；
     * 3. 基于Class对象创建{@link TypeDescriptor}；
     * 4. 若获取类名或加载类失败，使用父类的默认实现（基于当前列值的类型）。
     * 
     * @return 表示当前列数据类型的{@link TypeDescriptor}
     */
    @Override
    public TypeDescriptor getTypeDescriptor() {
        ResultSetMetaData resultSetMetaData = getResultSetMetaData();
        String className;
        try {
            className = resultSetMetaData.getColumnClassName(columnIndex);
        } catch (SQLException e) {
            return super.getTypeDescriptor();
        }
        Class<?> clazz = ClassUtils.getClass(className, null);
        if (clazz == null) {
            return super.getTypeDescriptor();
        }
        return TypeDescriptor.valueOf(clazz);
    }

    /**
     * 更新当前列的值（通过列索引）
     * 
     * @param value 待设置的新值（可为null，需与列的类型兼容）
     * @throws JdbcException 当ResultSet更新操作失败时抛出（如列不可更新、索引无效等）
     */
    @Override
    public synchronized void set(Object value) throws JdbcException{
        try {
            getResultSet().updateObject(this.columnIndex, value);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }
}