package run.soeasy.framework.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingConsumer;

/**
 * JDBC预编译语句包装类，继承自{@link StatementWrapped}，泛型限定为{@link PreparedStatement}及其子类，
 * 封装了预编译语句（{@link PreparedStatement}）的核心操作（执行、更新、查询、参数设置、批处理等），
 * 结合{@link Pipeline}提供链式调用能力，统一处理预编译语句的生命周期与{@link SQLException}，
 * 是JDBC中参数化SQL执行的核心组件。
 * 
 * <p>
 * 该类在父类基础上强化了预编译语句的特性，支持参数绑定、查询结果集包装、批处理参数设置等，
 * 通过函数式管道（{@link Pipeline}）将预编译语句的方法转换为链式操作，简化参数化SQL的编写与执行流程。
 * 
 * @param <T> 包装的预编译语句类型，必须是{@link PreparedStatement}的子类（如{@link java.sql.CallableStatement}）
 * @author soeasy.run
 * @see StatementWrapped
 * @see PreparedStatement
 * @see Pipeline
 */
@Getter
@Setter
public class PreparedStatementWrapped<T extends PreparedStatement> extends StatementWrapped<T> {

	/**
	 * 构造预编译语句包装器
	 * 
	 * @param source 提供预编译语句实例的管道（包含PreparedStatement的获取逻辑，可能抛出{@link SQLException}）
	 */
	public PreparedStatementWrapped(Pipeline<T, SQLException> source) {
		super(source);
	}

	/**
	 * 执行预编译的SQL语句（适用于任意SQL类型）
	 * 
	 * <p>
	 * 调用{@link PreparedStatement#execute()}执行SQL，返回是否产生结果集（true表示有ResultSet，false表示仅有更新计数）。
	 * 
	 * @return 执行后是否存在结果集（true为有结果集，false为无结果集）
	 * @throws SQLException 当SQL执行失败时抛出（如参数不匹配、语法错误等）
	 */
	public boolean execute() throws SQLException {
		return optional().map(statement -> statement.execute()).orElse(false);
	}

	/**
	 * 执行预编译的更新语句（INSERT/UPDATE/DELETE等）
	 * 
	 * <p>
	 * 调用{@link PreparedStatement#executeUpdate()}执行更新操作，返回受影响的行数。
	 * 
	 * @return SQL执行后受影响的行数（对于INSERT可能返回自动生成的键的数量，具体取决于数据库驱动）
	 * @throws SQLException 当更新操作失败时抛出（如约束冲突、权限不足等）
	 */
	public int executeUpdate() throws SQLException {
		return optional().map(statement -> statement.executeUpdate()).orElse(0);
	}

	/**
	 * 执行预编译的查询语句（SELECT），返回结果集包装器
	 * 
	 * <p>
	 * 调用{@link PreparedStatement#executeQuery()}执行查询，将返回的{@link java.sql.ResultSet}包装为{@link ResultSetWrapped}，
	 * 便于结果集的迭代与转换操作。
	 * 
	 * @return 包含查询结果的{@link ResultSetWrapped}实例
	 */
	public ResultSetWrapped query() {
		// 将预编译语句管道映射为结果集管道，再包装为ResultSetWrapped
		return new ResultSetWrapped(map(statement -> statement.executeQuery()));
	}

	/**
	 * 对预编译语句执行后置操作（如设置属性、配置参数等），返回新的包装器以支持链式调用
	 * 
	 * <p>
	 * 通过{@link ThrowingConsumer}接收预编译语句，执行自定义操作（如设置fetchSize、超时时间等），
	 * 操作完成后返回新的{@link PreparedStatementWrapped}，保持链式调用的连贯性。
	 * 
	 * @param after 对预编译语句执行的后置操作（可能抛出{@link SQLException}）
	 * @return 执行后置操作后的{@link PreparedStatementWrapped}实例
	 */
	public PreparedStatementWrapped<T> after(ThrowingConsumer<? super T, ? extends SQLException> after) {
		return new PreparedStatementWrapped<>(map(statement -> {
			after.accept(statement);
			return statement;
		}));
	}

	/**
	 * 为预编译语句设置参数（绑定SQL中的占位符），返回自身以支持链式调用
	 * 
	 * <p>
	 * 通过{@link #after(ThrowingConsumer)}实现，内部调用{@link JdbcUtils#setParams(PreparedStatement, Object[])}，
	 * 自动处理参数绑定（包括枚举类型转换为字符串），适配预编译语句的参数占位符（?）。
	 * 
	 * @param args 待绑定的参数数组（数量需与SQL中的占位符数量一致）
	 * @return 设置参数后的当前{@link PreparedStatementWrapped}实例
	 */
	public PreparedStatementWrapped<T> setParams(Object... args) {
		return after(statement -> JdbcUtils.setParams(statement, args));
	}

	/**
	 * 批量设置参数并添加到批处理队列，支持批量执行SQL
	 * 
	 * <p>
	 * 遍历输入的参数列表（每个元素为一个参数数组），为每个参数数组调用{@link #setParams(Object...)}绑定参数，
	 * 并调用{@link PreparedStatement#addBatch()}添加到批处理，适用于批量插入、更新等场景。
	 * 
	 * @param batchList 批量参数列表（每个元素是一个与SQL占位符匹配的参数数组）
	 * @return 处理批处理后的当前{@link PreparedStatementWrapped}实例
	 */
	public PreparedStatementWrapped<T> batch(Iterable<? extends Object[]> batchList) {
		return after(statement -> {
			Iterator<? extends Object[]> iterator = batchList.iterator();
			while (iterator.hasNext()) {
				Object[] args = iterator.next();
				JdbcUtils.setParams(statement, args);
				statement.addBatch();
			}
		});
	}
}