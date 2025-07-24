package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyAccessor;

/**
 * ResultSet属性访问器的抽象基类，实现{@link PropertyAccessor}接口，
 * 封装了{@link ResultSet}实例及其元数据（{@link ResultSetMetaData}）的管理，
 * 为子类提供访问ResultSet数据的基础功能，包括元数据的延迟加载、类型描述符的统一获取等。
 * 
 * <p>该抽象类通过持有{@link ResultSet}，实现了{@link PropertyAccessor}接口中类型描述符相关的方法，
 * 定义了访问ResultSet数据的规范，子类需实现具体的属性获取逻辑（如按列名、列索引获取值等）。
 * 
 * @author soeasy.run
 * @see PropertyAccessor
 * @see ResultSet
 * @see ResultSetMetaData
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractResultSetAccessor implements PropertyAccessor {

    /**
     * 被访问的ResultSet实例（非空，包含待访问的数据）
     */
    @NonNull
    private final ResultSet resultSet;

    /**
     * ResultSet的元数据（延迟初始化，通过{@link #getResultSetMetaData()}获取）
     */
    private ResultSetMetaData resultSetMetaData;

    /**
     * 同步获取ResultSet的元数据（延迟初始化）
     * 
     * <p>首次调用时从ResultSet中获取元数据并缓存，后续调用直接返回缓存值，
     * 获取过程中发生的{@link SQLException}会被转换为{@link JdbcException}抛出。
     * 
     * @return ResultSet的元数据（包含列信息、类型等）
     * @throws JdbcException 当获取元数据失败时抛出
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
     * 同步设置ResultSet的元数据（允许外部指定元数据，覆盖默认的延迟加载）
     * 
     * @param resultSetMetaData 要设置的ResultSet元数据
     */
    public synchronized void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    /**
     * 获取返回值的类型描述符（实现{@link PropertyAccessor}接口）
     * 
     * @return 与{@link #getRequiredTypeDescriptor()}一致的类型描述符
     */
    @Override
    public final TypeDescriptor getReturnTypeDescriptor() {
        return getTypeDescriptor();
    }

    /**
     * 获取所需类型的描述符（实现{@link PropertyAccessor}接口）
     * 
     * @return 与{@link #getTypeDescriptor()}一致的类型描述符
     */
    @Override
    public final TypeDescriptor getRequiredTypeDescriptor() {
        return getTypeDescriptor();
    }

    /**
     * 获取当前访问器的类型描述符（默认基于{@link #get()}方法返回值的类型）
     * 
     * <p>子类可重写此方法以提供更精确的类型描述（如指定具体的列类型）。
     * 
     * @return 表示当前访问数据类型的{@link TypeDescriptor}
     */
    public TypeDescriptor getTypeDescriptor() {
        return TypeDescriptor.forObject(get());
    }
}