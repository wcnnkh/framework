package run.soeasy.framework.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingConsumer;

@Getter
@Setter
public class PreparedStatementWrapper<T extends PreparedStatement> extends StatementWrapper<T> {

	public PreparedStatementWrapper(Pipeline<T, SQLException> source) {
		super(source);
	}

	public boolean execute() throws SQLException {
		return optional().filter((e) -> e.execute()).isPresent();
	}

	public int executeUpdate() throws SQLException {
		return optional().map((e) -> e.executeUpdate()).get();
	}

	public ResultSetWrapper query() {
		return new ResultSetWrapper(map((e) -> e.executeQuery()));
	}

	public PreparedStatementWrapper<T> after(ThrowingConsumer<? super T, ? extends SQLException> after) {
		return new PreparedStatementWrapper<>(map((e) -> {
			after.accept(e);
			return e;
		}));
	}

	public PreparedStatementWrapper<T> setParams(Object... args) {
		return after((e) -> JdbcUtils.setParams(e, args));
	}

	public PreparedStatementWrapper<T> batch(Iterable<? extends Object[]> batchList) {
		return after((e) -> {
			for (Object[] args : batchList) {
				JdbcUtils.setParams(e, args);
			}
			e.addBatch();
		});
	}
}
