package run.soeasy.framework.jdbc.convert;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.jdbc.JdbcException;

/**
 * 基于JDBC {@link ResultSet} 的标准化属性映射实现类
 * <p>
 * 实现{link PropertyMapping}&lt;{@link PropertyAccessor}&gt; 接口，将数据库结果集的列数据封装为统一的
 * {@link PropertyAccessor} 视图，提供“按索引/按名称”访问列数据的标准化入口，适配框架的属性访问体系，
 * 简化JDBC结果集的列操作与批量处理。
 * <p>
 * 核心设计：
 * 1. 元数据延迟初始化：{@link ResultSetMetaData} 首次访问时加载并缓存，避免重复调用ResultSet.getMetaData()；
 * 2. 索引适配：对外暴露0-based列索引（符合Java习惯），内部自动转换为ResultSet的1-based索引；
 * 3. 异常封装：将检查型{@link SQLException} 转换为运行时{@link JdbcException}，简化异常处理；
 * 4. 性能优化：预加载元数据并传递给{@link PropertyAccessor} 实现类，减少重复获取元数据的开销。
 *
 * @author soeasy.run
 * @see PropertyMapping 属性映射核心接口（当前实现泛型为{@link PropertyAccessor}）
 * @see PropertyAccessor 属性访问器接口（适配列数据读取）
 * @see ResultSet 数据库结果集核心类
 * @see ResultSetMetaData 结果集元数据（列信息载体）
 * @see ResultSetColumnAccessor 按列索引的属性访问器实现
 * @see ResultSetPropertyAccessor 按列名称的属性访问器实现
 */
@RequiredArgsConstructor
@Getter
public class ResultSetProperties implements PropertyMapping<PropertyAccessor> {

    /**
     * 结果集元数据（延迟初始化）
     * <p>
     * 首次调用{@link #getResultSetMetaData()} 时从{@link #resultSet} 加载，后续调用直接返回缓存值；
     * 支持通过{@link #setResultSetMetaData(ResultSetMetaData)} 手动设置（用于测试/预加载场景）。
     */
    private ResultSetMetaData resultSetMetaData;

    /**
     * 被封装的数据库结果集（非空）
     * <p>
     * 核心数据源，所有列数据的读取均基于此ResultSet；需保证ResultSet处于有效状态（未关闭、未遍历结束）。
     */
    @NonNull
    private final ResultSet resultSet;

    /**
     * 同步获取并缓存结果集元数据（延迟初始化）
     * <p>
     * 线程安全设计：通过synchronized保证多线程下元数据仅初始化一次；
     * 异常处理：将{@link SQLException} 封装为{@link JdbcException} 运行时异常抛出，避免强制try-catch。
     *
     * @return 非空的ResultSetMetaData（包含列数、列名、列类型等核心信息）
     * @throws JdbcException 当ResultSet关闭、元数据获取失败时抛出，包含原始SQLException
     */
    public synchronized ResultSetMetaData getResultSetMetaData() throws JdbcException {
        if (resultSetMetaData == null) {
            try {
                resultSetMetaData = resultSet.getMetaData();
            } catch (SQLException e) {
                throw new JdbcException("Failed to obtain ResultSetMetaData from ResultSet", e);
            }
        }
        return resultSetMetaData;
    }

    /**
     * 手动设置结果集元数据（覆盖/预加载）
     * <p>
     * 适用场景：
     * 1. 测试场景：Mock元数据，无需依赖真实ResultSet；
     * 2. 性能优化：预加载元数据，避免首次访问时的SQLException；
     * 3. 元数据修正：覆盖ResultSet默认返回的元数据（如自定义列名映射）。
     *
     * @param resultSetMetaData 要设置的元数据（可为null，置空后下次调用getResultSetMetaData()会重新加载）
     */
    public synchronized void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    /**
     * 获取所有列对应的属性访问器流（延迟加载）
     * <p>
     * 核心逻辑：
     * 1. 先获取元数据并确定列总数；
     * 2. 生成0~列总数-1的索引流，逐个转换为{@link ResultSetColumnAccessor}；
     * 3. 返回的{@link Streamable} 为惰性加载（泛型为{@link PropertyAccessor}），仅在遍历/消费时生成访问器实例。
     *
     * @return 包含所有列属性访问器的{@link Streamable}&lt;{@link PropertyAccessor}&gt;（顺序与ResultSet列顺序一致）
     * @throws JdbcException 获取列总数失败时抛出
     */
    @Override
    public Streamable<PropertyAccessor> elements() {
        ResultSetMetaData metaData = getResultSetMetaData();
        int columnCount;
        try {
            columnCount = metaData.getColumnCount();
        } catch (SQLException e) {
            throw new JdbcException("Failed to get column count from ResultSetMetaData", e);
        }
        // 0-based索引流 → 转换为ResultSetColumnAccessor → 封装为Streamable
        return Streamable.of(() -> IntStream.range(0, columnCount)
                .mapToObj(index -> getAt(index).getValue())
                .iterator());
    }

    /**
     * 严格按索引获取列属性访问器（0-based）
     * <p>
     * 核心规则：
     * 1. 索引校验：0 ≤ index &lt; 列总数，否则抛出IndexOutOfBoundsException；
     * 2. 索引转换：自动将0-based索引转为ResultSet的1-based列索引；
     * 3. 性能优化：将已缓存的元数据传递给访问器，避免重复获取。
     *
     * @param index 列索引（0-based，与Java数组/集合索引规则一致）
     * @return 包含列名和{@link ResultSetColumnAccessor}的{@link KeyValue}&lt;String, {@link PropertyAccessor}&gt;对象（非空）
     * @throws IndexOutOfBoundsException 索引为负数或≥列总数时抛出
     * @throws JdbcException 获取列总数/元数据失败时抛出
     */
    @Override
    public KeyValue<String, PropertyAccessor> getAt(int index) throws IndexOutOfBoundsException {
        try {
            int columnCount = getResultSetMetaData().getColumnCount();
            if (index < 0 || index >= columnCount) {
                throw new IndexOutOfBoundsException(
                        String.format("Column index %d out of bounds (0 ≤ index &lt; %d)", index, columnCount)
                );
            }
        } catch (SQLException e) {
            throw new JdbcException("Failed to validate column index: " + index, e);
        }

        // 转换为ResultSet的1-based索引创建访问器
        ResultSetColumnAccessor columnAccessor = new ResultSetColumnAccessor(resultSet, index + 1);
        columnAccessor.setResultSetMetaData(getResultSetMetaData());
        return KeyValue.of(columnAccessor.getName(), columnAccessor);
    }

    /**
     * 宽松按索引获取列属性访问器（0-based）
     * <p>
     * 与{@link #getAt(int)} 区别：索引越界时返回空{@link Optional}，不抛出异常，适配“容错性访问”场景。
     *
     * @param index 列索引（0-based，允许负数/超出列总数）
     * @return 存在则返回包含{@link KeyValue}&lt;String, {@link PropertyAccessor}&gt;的{@link Optional}，否则返回Optional.empty()
     * @throws JdbcException 获取元数据/列信息失败时抛出（非索引越界场景）
     */
    @Override
    public Optional<KeyValue<String, PropertyAccessor>> at(int index) {
        try {
            return Optional.of(getAt(index));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * 按列名获取属性访问器
     * <p>
     * 核心逻辑：
     * 1. 创建{@link ResultSetPropertyAccessor} 绑定指定列名；
     * 2. 传递已缓存的元数据，优化列名校验/类型获取性能；
     * 3. 返回单例{@link Streamable}&lt;{@link PropertyAccessor}&gt;（列名唯一，仅对应一个访问器）。
     * <p>
     * 注意：列名匹配规则遵循JDBC规范（通常大小写不敏感，具体取决于数据库驱动）。
     *
     * @param key 列名（非空，需与ResultSet元数据中的列名匹配）
     * @return 包含该列属性访问器的单例{@link Streamable}&lt;{@link PropertyAccessor}&gt;（非空）
     * @throws JdbcException 元数据传递/访问器创建失败时抛出
     */
    @Override
    public Streamable<PropertyAccessor> getValues(String key) {
        ResultSetPropertyAccessor propertyAccessor = new ResultSetPropertyAccessor(resultSet, key);
        propertyAccessor.setResultSetMetaData(getResultSetMetaData());
        return Streamable.singleton(propertyAccessor);
    }
}