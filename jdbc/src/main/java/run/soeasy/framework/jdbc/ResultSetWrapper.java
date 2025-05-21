package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Streams;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.invoke.Function;

@Getter
@Setter
public class ResultSetWrapper extends JdbcWrapper<ResultSet> {

	public ResultSetWrapper(Pipeline<ResultSet, SQLException> source) {
		super(source);
	}

	public <T> Elements<T> rows(Function<? super ResultSet, ? extends T, ? extends SQLException> mapper) {
		ResultSetIterator<T> iterator = new ResultSetIterator<>(this, mapper);
		return Elements.of(() -> Streams.stream(iterator));
	}
}
