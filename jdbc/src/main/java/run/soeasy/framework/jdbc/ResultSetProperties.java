package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.transform.property.TypedProperties;

/**
 * 基于ResultSet的类型化属性集合实现类，实现{@link TypedProperties}接口，
 * 用于将{@link ResultSet}中的列数据封装为可迭代、可通过索引或名称访问的属性集合，
 * 提供对数据库结果集列的统一访问入口，适配属性访问框架的标准接口。
 * 
 * <p>该类通过管理{@link ResultSetMetaData}（延迟初始化），为每个列创建对应的{@link PropertyAccessor}（{@link ResultSetColumnAccessor}或{@link ResultSetPropertyAccessor}），
 * 支持迭代所有列、按索引访问列、按名称访问列，简化ResultSet列数据的批量处理与访问。
 * 
 * @author soeasy.run
 * @see TypedProperties
 * @see ResultSet
 * @see ResultSetMetaData
 * @see PropertyAccessor
 */
@RequiredArgsConstructor
@Getter
public class ResultSetProperties implements TypedProperties {

    /**
     * 结果集元数据（延迟初始化，通过{@link #getResultSetMetaData()}获取，缓存以避免重复获取）
     */
    private ResultSetMetaData resultSetMetaData;

    /**
     * 被包装的数据库结果集（非空，包含待访问的列数据）
     */
    @NonNull
    private final ResultSet resultSet;

    /**
     * 同步获取结果集元数据（延迟初始化）
     * 
     * <p>首次调用时从{@link #resultSet}中获取元数据并缓存，后续调用直接返回缓存值；
     * 若获取元数据失败（如SQLException），转换为{@link JdbcException}抛出。
     * 
     * @return 结果集的元数据（包含列数、列名、类型等信息）
     * @throws JdbcException 当获取元数据时发生SQL错误时抛出
     */
    public synchronized ResultSetMetaData getResultSetMetaData() {
        if (resultSetMetaData == null) {
            try {
                resultSetMetaData = resultSet.getMetaData();
            } catch (SQLException e) {
                throw new JdbcException(e);
            }
        }
        return resultSetMetaData;
    }

    /**
     * 手动设置结果集元数据（用于预加载或覆盖默认元数据，可优化性能或用于测试场景）
     * 
     * @param resultSetMetaData 要设置的结果集元数据
     */
    public synchronized void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    /**
     * 获取所有列的属性访问器迭代器
     * 
     * <p>通过元数据获取列总数，生成0到列数-1的整数流，映射为对应索引的{@link ResultSetColumnAccessor}，
     * 迭代器遍历所有列的属性访问器。
     * 
     * @return 包含所有列属性访问器的{@link Iterator}
     * @throws JdbcException 当获取列总数时发生SQL错误时抛出
     */
    @Override
    public Iterator<PropertyAccessor> iterator() {
        ResultSetMetaData resultSetMetaData = getResultSetMetaData();
        int columnCount;
        try {
            columnCount = resultSetMetaData.getColumnCount();
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
        // 生成0到columnCount-1的索引流，转换为PropertyAccessor并创建迭代器
        return IntStream.range(0, columnCount).mapToObj(this::get).iterator();
    }

    /**
     * 通过索引获取指定列的属性访问器（索引从0开始）
     * 
     * <p>将输入索引（0-based）转换为ResultSet的列索引（1-based），创建{@link ResultSetColumnAccessor}，
     * 并设置预加载的元数据以避免重复获取，提高性能。
     * 
     * @param index 列索引（0-based，需满足0 ≤ index &lt; 列总数，否则抛出IndexOutOfBoundsException）
     * @return 对应列的{@link ResultSetColumnAccessor}实例
     * @throws IndexOutOfBoundsException 当索引超出有效范围时抛出
     */
    @Override
    public PropertyAccessor get(int index) throws IndexOutOfBoundsException {
        // 转换为ResultSet的1-based索引
        ResultSetColumnAccessor propertyAccessor = new ResultSetColumnAccessor(resultSet, index + 1);
        // 设置已加载的元数据，避免重复获取
        propertyAccessor.setResultSetMetaData(getResultSetMetaData());
        return propertyAccessor;
    }

    /**
     * 通过列名获取指定列的属性访问器
     * 
     * <p>创建{@link ResultSetPropertyAccessor}，并设置预加载的元数据，支持通过列名（或别名）访问列数据，
     * 若列名不唯一可能抛出{@link NoUniqueElementException}（由底层访问器处理）。
     * 
     * @param name 列名或别名（非空，需与结果集中的列名匹配）
     * @return 对应列的{@link ResultSetPropertyAccessor}实例
     * @throws NoUniqueElementException 当列名对应多个列时抛出（由底层实现决定）
     */
    @Override
    public PropertyAccessor get(String name) throws NoUniqueElementException {
        ResultSetPropertyAccessor propertyAccessor = new ResultSetPropertyAccessor(resultSet, name);
        // 设置已加载的元数据，优化性能
        propertyAccessor.setResultSetMetaData(getResultSetMetaData());
        return propertyAccessor;
    }
}