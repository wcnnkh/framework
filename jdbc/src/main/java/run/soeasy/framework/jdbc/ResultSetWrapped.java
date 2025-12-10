package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.lang.model.util.Elements;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * JDBC结果集包装类，继承自{@link JdbcWrapped}，用于包装{@link ResultSet}的管道（{@link Pipeline}），
 * 提供将结果集行数据转换为元素集合（{@link Elements}）的核心方法，支持结果集的流式处理与行数据映射，
 * 是结果集数据提取与转换的关键组件。
 * 
 * <p>该类通过包装结果集的管道，结合{@link ResultSetIterator}实现结果集的迭代，
 * 并通过{@link Elements}提供统一的集合访问接口，简化从ResultSet到业务对象的转换流程，
 * 统一处理结果集操作中的{@link SQLException}（转换为{@link JdbcException}）。
 * 
 * @author soeasy.run
 * @see JdbcWrapped
 * @see ResultSet
 * @see Elements
 * @see ResultSetIterator
 */
@Getter
@Setter
public class ResultSetWrapped extends JdbcWrapped<ResultSet> {

    /**
     * 构造结果集包装类
     * 
     * @param source 提供结果集的管道（包含ResultSet的获取逻辑，可能抛出{@link SQLException}）
     */
    public ResultSetWrapped(Pipeline<ResultSet, SQLException> source) {
        super(source);
    }

    /**
     * 将结果集的行数据通过映射函数转换为{@link Elements}集合
     * 
     * <p>核心功能：
     * 1. 创建{@link ResultSetIterator}，将当前结果集管道与行映射函数关联，实现结果集的迭代；
     * 2. 通过{@link CollectionUtils#unknownSizeStream(java.util.Iterator)}将迭代器转换为未知大小的流；
     * 3. 包装为{@link Elements}，提供结果集行数据的集合式访问（支持迭代、流式处理等）。
     * 
     * @param <T> 结果集行转换后的目标类型
     * @param mapper 行映射函数，用于将{@link ResultSet}的当前行转换为T类型对象（可能抛出{@link SQLException}）
     * @return 包含转换后元素的{@link Elements}实例，可用于遍历或流式处理结果集数据
     */
    public <T> Streamable<T> rows(ThrowingFunction<? super ResultSet, ? extends T, ? extends SQLException> mapper) {
        // 创建结果集迭代器，关联当前管道与映射函数
        ResultSetIterator<T> iterator = new ResultSetIterator<>(this, mapper);
        // 将迭代器转换为Elements集合，支持流式处理
        return Streamable.of(() -> CollectionUtils.unknownSizeStream(iterator));
    }
}