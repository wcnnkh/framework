package run.soeasy.framework.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.function.Pipeline;

public class ConnectionWrapper extends JdbcWrapper<Connection> {

	public ConnectionWrapper(Pipeline<Connection, SQLException> source) {
		super(source);
	}

	public <P extends Statement> StatementWrapper<P> createStatement(
			Function<? super Connection, P, ? extends SQLException> function) {
		return new StatementWrapper<P>(map(function).onClose((e) -> e.close()).newPipeline());
	}

	public PreparedStatementWrapper<PreparedStatement> prepareStatement(String sql) {
		return new PreparedStatementWrapper<>(createStatement((e) -> e.prepareStatement(sql)));
	}

	public PreparedStatementWrapper<CallableStatement> prepareCall(String sql) {
		return new PreparedStatementWrapper<>(createStatement((e) -> e.prepareCall(sql)));
	}
}
