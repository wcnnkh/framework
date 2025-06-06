package run.soeasy.framework.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

public class ConnectionWrapper extends JdbcWrapper<Connection> {

	public ConnectionWrapper(Pipeline<Connection, SQLException> source) {
		super(source);
	}

	public <P extends Statement> StatementWrapper<P> createStatement(
			ThrowingFunction<? super Connection, P, SQLException> function) {
		return new StatementWrapper<P>(map(function).onClose((e) -> e.close()));
	}

	public PreparedStatementWrapper<PreparedStatement> prepareStatement(String sql) {
		return new PreparedStatementWrapper<>(createStatement((e) -> e.prepareStatement(sql)));
	}

	public PreparedStatementWrapper<CallableStatement> prepareCall(String sql) {
		return new PreparedStatementWrapper<>(createStatement((e) -> e.prepareCall(sql)));
	}
}
