package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import run.soeasy.framework.core.function.Pipeline;

/**
 * 数据库连接工厂接口，定义获取数据库连接（{@link Connection}）的标准方法，
 * 并提供默认方法创建包装后的连接对象（{@link ConnectionWrapped}），
 * 是获取和管理数据库连接的核心接口，适配各类数据源（如JDBC驱动、连接池等）。
 * 
 * <p>
 * 该接口通过{@link #getConnection()}提供原始连接获取能力，通过默认方法{@link #newPipeline()}
 * 将连接包装为支持函数式管道（{@link Pipeline}）的{@link ConnectionWrapped}，简化连接的链式操作与资源管理。
 * 
 * @author soeasy.run
 * @see Connection
 * @see ConnectionWrapped
 * @see Pipeline
 */
public interface ConnectionFactory {

	/**
	 * 获取数据库连接（原始连接）
	 * 
	 * <p>
	 * 该方法由具体实现类提供（如基于DriverManager、连接池等），返回可用于数据库操作的{@link Connection}实例，
	 * 调用者需负责连接的关闭（或通过{@link #newPipeline()}获取自动管理的连接）。
	 * 
	 * @return 数据库连接实例（非空，需在使用后关闭）
	 * @throws SQLException 当获取连接失败时抛出（如连接参数错误、数据库不可达等）
	 */
	Connection getConnection() throws SQLException;

	/**
	 * 创建包装后的数据库连接对象（{@link ConnectionWrapped}）
	 * 
	 * <p>
	 * 默认实现： 1.
	 * 通过{@link Pipeline#forSupplier(run.soeasy.framework.core.function.ThrowingSupplier)}将{@link #getConnection()}转换为连接管道；
	 * 2. 为管道绑定关闭操作（调用{@link Connection#close()}），确保资源自动释放； 3.
	 * 包装为{@link ConnectionWrapped}，支持链式创建Statement、执行SQL等操作。
	 * 
	 * @return 包装后的连接对象{@link ConnectionWrapped}
	 */
	default ConnectionWrapped newPipeline() {
		// 创建连接管道，绑定关闭操作，再包装为ConnectionWrapped
		Pipeline<Connection, SQLException> connectionPipeline = Pipeline.forSupplier(this::getConnection)
				.onClose(Connection::close).closeable();
		return new ConnectionWrapped(connectionPipeline);
	}
}