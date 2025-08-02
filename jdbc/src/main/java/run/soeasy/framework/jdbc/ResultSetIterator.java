package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CloseableIterator;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 基于ResultSet的可关闭迭代器，继承自{@link JdbcWrapped}并实现{@link CloseableIterator}接口，
 * 用于迭代处理{@link ResultSet}中的行数据，通过映射函数将每行数据转换为目标类型{@code T}，
 * 同时支持资源关闭以释放ResultSet，处理JDBC操作中的迭代逻辑与异常转换。
 * 
 * <p>该迭代器通过同步机制保证线程安全，在迭代过程中自动处理ResultSet的行移动（{@link ResultSet#next()}），
 * 并将底层{@link SQLException}转换为{@link JdbcException}，是结果集遍历与对象转换的核心组件。
 * 
 * @param <T> 迭代元素的目标类型（由映射函数从ResultSet行转换而来）
 * @author soeasy.run
 * @see CloseableIterator
 * @see JdbcWrapped
 * @see ResultSet
 */
public class ResultSetIterator<T> extends JdbcWrapped<ResultSet> implements CloseableIterator<T> {

    /**
     * 缓存下一个待处理的ResultSet（用于hasNext()预判断后缓存结果）
     */
    private Supplier<ResultSet> next;

    /**
     * 错误标记，用于记录迭代过程中是否发生异常（发生异常后终止迭代）
     */
    private boolean error = false;

    /**
     * 当前操作的ResultSet实例（从管道中获取，可能为null）
     */
    private ResultSet resultSet;

    /**
     * 结果集行映射函数，用于将ResultSet的当前行转换为T类型对象（可能抛出异常）
     */
    private final ThrowingFunction<? super ResultSet, ? extends T, ? extends Throwable> mapper;

    /**
     * 构造ResultSet迭代器
     * 
     * @param source 提供ResultSet的JDBC管道（包含结果集获取逻辑，可能抛出SQLException）
     * @param mapper 行映射函数（将ResultSet当前行转换为T类型，非空）
     */
    public ResultSetIterator(Pipeline<ResultSet, SQLException> source,
            @NonNull ThrowingFunction<? super ResultSet, ? extends T, ? extends Throwable> mapper) {
        super(source);
        this.mapper = mapper;
    }

    /**
     * 判断是否存在下一行数据
     * 
     * <p>处理逻辑：
     * 1. 若已发生错误（error=true），直接返回false；
     * 2. 若已缓存下一行（next≠null），返回true；
     * 3. 首次调用时从管道获取ResultSet，调用{@link ResultSet#next()}判断是否有下一行；
     * 4. 若有下一行，缓存ResultSet到next；若发生异常，标记错误并关闭资源，转换异常为{@link JdbcException}。
     * 
     * @return 存在下一行返回true，否则返回false
     * @throws JdbcException 当ResultSet操作失败时抛出（如结果集已关闭、数据库连接异常等）
     */
    @Override
    public boolean hasNext() {
        synchronized (this) {
            if (error) {
                return false;
            }

            // 已缓存下一行，直接返回true
            if (next != null) {
                return true;
            }

            try {
                // 首次获取ResultSet
                if (resultSet == null) {
                    resultSet = get(); // 从管道获取ResultSet（可能抛出SQLException）
                }

                // 同步操作ResultSet，避免并发问题
                synchronized (resultSet) {
                    if (resultSet.next()) {
                        // 缓存当前ResultSet作为下一行
                        this.next = () -> resultSet;
                        return true;
                    }
                }
            } catch (Throwable e) {
                error = true; // 标记错误状态
                // 发生异常时关闭资源，并添加抑制异常
                try {
                    super.close();
                } catch (Throwable e1) {
                    e.addSuppressed(e1);
                }
                throw newJdbcException(e);
            }
            return false;
        }
    }

    /**
     * 创建JDBC异常（可被子类重写以定制异常类型）
     * 
     * @param exception 根源异常（通常为{@link SQLException}或映射函数抛出的异常）
     * @return 包装后的{@link JdbcException}
     */
    protected JdbcException newJdbcException(Throwable exception) {
        return new JdbcException(exception);
    }

    /**
     * 获取下一个转换后的对象
     * 
     * <p>处理逻辑：
     * 1. 调用{@link #hasNext()}判断是否有下一行，无则抛出{@link NoSuchElementException}；
     * 2. 从缓存中获取ResultSet，应用映射函数转换为T类型；
     * 3. 清除缓存的next，若发生异常，标记错误并关闭资源，转换异常为{@link JdbcException}。
     * 
     * @return 转换后的T类型对象
     * @throws NoSuchElementException 当无下一行数据时抛出
     * @throws JdbcException 当映射转换或ResultSet操作失败时抛出
     */
    @Override
    public T next() throws JdbcException {
        synchronized (this) {
            if (!hasNext()) {
                throw new NoSuchElementException("ResultSet has no more elements");
            }

            // 同步操作ResultSet，确保线程安全
            synchronized (resultSet) {
                ResultSet rs = next.get(); // 获取缓存的下一行ResultSet
                next = null; // 清除缓存
                try {
                    return mapper.apply(rs); // 应用映射函数转换行数据
                } catch (Throwable e) {
                    error = true; // 标记错误状态
                    // 发生异常时关闭资源，并添加抑制异常
                    try {
                        super.close();
                    } catch (Throwable e1) {
                        e.addSuppressed(e1);
                    }
                    throw newJdbcException(e);
                }
            }
        }
    }

    /**
     * 关闭迭代器，释放ResultSet资源
     * 
     * <p>调用父类的关闭方法释放底层资源，若发生异常则转换为{@link JdbcException}。
     * 
     * @throws JdbcException 当关闭资源失败时抛出
     */
    @Override
    public void close() throws JdbcException {
        synchronized (this) {
            try {
                super.close(); // 关闭管道及关联的资源（如ResultSet）
            } catch (Throwable e) {
                throw newJdbcException(e);
            }
        }
    }
}