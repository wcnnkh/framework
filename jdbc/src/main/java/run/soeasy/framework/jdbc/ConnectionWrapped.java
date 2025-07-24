package run.soeasy.framework.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * JDBC连接包装类，继承自{@link JdbcWrapped<Connection>}，用于包装数据库连接（{@link Connection}），
 * 提供创建各类Statement（{@link Statement}、{@link PreparedStatement}、{@link CallableStatement}）的便捷方法，
 * 结合{@link Pipeline}实现连接与语句的链式操作，统一管理数据库连接的生命周期与异常处理。
 * 
 * <p>该类通过包装连接的管道（{@link Pipeline<Connection, SQLException>}），将连接的操作转换为函数式管道调用，
 * 支持创建Statement时指定自定义创建逻辑，并自动绑定语句的关闭操作，简化JDBC连接与语句的联动管理。
 * 
 * @author soeasy.run
 * @see JdbcWrapped
 * @see Connection
 * @see Statement
 * @see PreparedStatement
 * @see CallableStatement
 */
public class ConnectionWrapped extends JdbcWrapped<Connection> {

    /**
     * 构造连接包装类
     * 
     * @param source 提供数据库连接的管道（包含连接的获取逻辑，可能抛出{@link SQLException}）
     */
    public ConnectionWrapped(Pipeline<Connection, SQLException> source) {
        super(source);
    }

    /**
     * 通过自定义函数从连接创建特定类型的Statement，并包装为{@link StatementWrapped}
     * 
     * <p>功能：
     * 1. 利用传入的{@link ThrowingFunction}从{@link Connection}创建目标Statement（如{@link PreparedStatement}）；
     * 2. 为创建的Statement管道绑定关闭操作（调用{@link Statement#close()}）；
     * 3. 返回包装后的{@link StatementWrapped}，支持链式的Statement操作。
     * 
     * @param <P> Statement的具体类型（需为{@link Statement}的子类）
     * @param function 从Connection创建Statement的函数（如{@code conn -> conn.prepareStatement(sql)}）
     * @return 包装目标Statement的{@link StatementWrapped}实例
     */
    public <P extends Statement> StatementWrapped<P> createStatement(
            ThrowingFunction<? super Connection, P, SQLException> function) {
        // 映射连接为目标Statement，并设置关闭时自动关闭Statement
        Pipeline<P, SQLException> statementPipeline = map(function).onClose(Statement::close);
        return new StatementWrapped<>(statementPipeline);
    }

    /**
     * 创建预编译语句（{@link PreparedStatement}）的包装器
     * 
     * <p>通过调用{@link #createStatement(ThrowingFunction)}，使用{@link Connection#prepareStatement(String)}创建预编译语句，
     * 并包装为{@link PreparedStatementWrapped}，支持参数绑定、执行SQL等操作。
     * 
     * @param sql 预编译的SQL语句（可包含参数占位符{@code ?}）
     * @return 包装{@link PreparedStatement}的{@link PreparedStatementWrapped}实例
     */
    public PreparedStatementWrapped<PreparedStatement> prepareStatement(String sql) {
        return new PreparedStatementWrapped<>(createStatement((conn) -> conn.prepareStatement(sql)));
    }

    /**
     * 创建存储过程调用语句（{@link CallableStatement}）的包装器
     * 
     * <p>通过调用{@link #createStatement(ThrowingFunction)}，使用{@link Connection#prepareCall(String)}创建存储过程调用语句，
     * 并包装为{@link PreparedStatementWrapped}，支持存储过程的调用与参数处理。
     * 
     * @param sql 调用存储过程的SQL语句（如{@code "{call procedure_name(?, ?)}"}）
     * @return 包装{@link CallableStatement}的{@link PreparedStatementWrapped}实例
     */
    public PreparedStatementWrapped<CallableStatement> prepareCall(String sql) {
        return new PreparedStatementWrapped<>(createStatement((conn) -> conn.prepareCall(sql)));
    }
}