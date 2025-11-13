package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.NonNull;

/**
 * 基于列名的ResultSet属性访问器，继承自{@link AbstractResultSetAccessor}，
 * 用于通过列名（或别名）访问{@link ResultSet}中的特定列数据，支持根据列名获取和更新列值，
 * 是ResultSet列数据访问的具体实现之一（按名称访问），与{@link ResultSetColumnAccessor}（按索引访问）形成互补。
 * 
 * <p>该类通过列名定位目标列，封装了ResultSet的getObject(String)和updateObject(String, Object)操作，
 * 自动将{@link SQLException}转换为{@link JdbcException}，简化通过列名进行的结果集数据读写。
 * 
 * @author soeasy.run
 * @see AbstractResultSetAccessor
 * @see ResultSetColumnAccessor
 * @see ResultSet
 */
public class ResultSetPropertyAccessor extends AbstractResultSetAccessor {

    /**
     * 列名（或别名），用于定位结果集中的目标列（非空）
     */
    @NonNull
    private final String name;

    /**
     * 构造基于列名的ResultSet访问器
     * 
     * @param resultSet 待访问的ResultSet（非空，包含目标列）
     * @param name 列名或别名（非空，用于定位列，需与结果集中的列名/别名匹配）
     */
    public ResultSetPropertyAccessor(@NonNull ResultSet resultSet, @NonNull String name) {
        super(resultSet);
        this.name = name;
    }

    /**
     * 通过列名获取当前列的值
     * 
     * <p>调用{@link ResultSet#getObject(String)}获取列值，若发生SQLException则转换为{@link JdbcException}。
     * 
     * @return 列的当前值（可能为null，类型由数据库列类型决定）
     * @throws JdbcException 当ResultSet操作失败时抛出（如列名不存在、结果集已关闭等）
     */
    @Override
    public synchronized Object get() throws JdbcException{
        try {
            return getResultSet().getObject(name);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    /**
     * 获取当前访问的列名（或别名）
     * 
     * @return 列名（构造时传入的名称，用于定位列）
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 通过列名更新当前列的值
     * 
     * <p>调用{@link ResultSet#updateObject(String, Object)}更新列值，若发生SQLException则转换为{@link JdbcException}。
     * 
     * @param value 待设置的新值（可为null，需与列的类型兼容）
     * @throws JdbcException 当ResultSet更新操作失败时抛出（如列不可更新、列名无效等）
     */
    @Override
    public synchronized void set(Object value) throws JdbcException{
        try {
            getResultSet().updateObject(this.name, value);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }
}