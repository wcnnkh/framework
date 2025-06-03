package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@Getter
@Setter
public class ResultSetWrapper extends JdbcWrapper<ResultSet> {

	public ResultSetWrapper(Pipeline<ResultSet, SQLException> source) {
		super(source);
	}

	public <T> Elements<T> rows(ThrowingFunction<? super ResultSet, ? extends T, ? extends SQLException> mapper) {
		ResultSetIterator<T> iterator = new ResultSetIterator<>(this, mapper);
		return Elements.of(() -> CollectionUtils.unknownSizeStream(iterator));
	}
}
