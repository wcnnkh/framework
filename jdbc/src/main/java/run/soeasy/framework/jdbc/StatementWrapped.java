package run.soeasy.framework.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * JDBC Statement包装器，继承自{@link JdbcWrapped}，泛型限定为{@link Statement}及其子类（如{@link java.sql.PreparedStatement}），
 * 封装了Statement的批处理操作（清除批处理、执行批处理、执行大型批处理），结合{@link Pipeline}提供链式的Statement操作能力，
 * 简化JDBC批处理命令的管理与执行。
 * 
 * <p>该类通过包装Statement的管道（{@link Pipeline}），将Statement的方法转换为管道操作，
 * 支持批处理命令的链式调用，同时统一处理{@link SQLException}，适配JDBC操作的函数式编程模式。
 * 
 * @param <T> 包装的Statement类型，必须是{@link Statement}的子类（如{@link java.sql.PreparedStatement}、{@link java.sql.CallableStatement}）
 * @author soeasy.run
 * @see JdbcWrapped
 * @see Statement
 * @see Pipeline
 */
public class StatementWrapped<T extends Statement> extends JdbcWrapped<T> {

    /**
     * 构造Statement包装器
     * 
     * @param source 提供Statement实例的管道（包含Statement的获取逻辑，可能抛出{@link SQLException}）
     */
    public StatementWrapped(Pipeline<T, SQLException> source) {
        super(source);
    }

    /**
     * 清除批处理命令，并返回新的Statement包装器以支持链式调用
     * 
     * <p>通过管道的{@link Pipeline#map(ThrowingFunction)}操作执行{@link Statement#clearBatch()}，
     * 清除当前Statement中的所有批处理命令，返回包含处理后Statement的新包装器。
     * 
     * @return 新的{@link StatementWrapped}实例，支持链式调用后续操作
     */
    public StatementWrapped<T> clearBatch() {
        return new StatementWrapped<T>(map((statement) -> {
            statement.clearBatch();
            return statement;
        }));
    }

    /**
     * 执行批处理命令，返回每个命令的影响行数数组
     * 
     * <p>通过管道的{@link Pipeline#optional()}获取Statement实例，调用{@link Statement#executeBatch()}执行批处理，
     * 返回int数组，其中每个元素表示对应批处理命令影响的行数（SQL语句的典型返回值）。
     * 
     * @return int数组，包含每个批处理命令的执行结果（影响行数）
     * @throws SQLException 当批处理执行失败时抛出（如SQL语法错误、约束冲突等）
     */
    public int[] executeBatch() throws SQLException {
        return optional().map((statement) -> statement.executeBatch()).get();
    }

    /**
     * 执行大型批处理命令，返回每个命令的大型影响行数数组（适用于返回值超过Integer.MAX_VALUE的场景）
     * 
     * <p>调用{@link Statement#executeLargeBatch()}执行批处理，返回long数组，支持大型结果集的行数表示，
     * 若底层Statement不支持该方法（如旧版本JDBC驱动），则抛出{@link UnsupportedOperationException}。
     * 
     * @return long数组，包含每个批处理命令的执行结果（大型影响行数）
     * @throws SQLException 当批处理执行失败时抛出
     * @throws UnsupportedOperationException 当底层Statement不支持executeLargeBatch()方法时抛出
     */
    public long[] executeLargeBatch() throws SQLException, UnsupportedOperationException {
        return optional().map((statement) -> statement.executeLargeBatch()).get();
    }
}