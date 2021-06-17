package scw.sql;

import java.sql.SQLException;

import scw.util.stream.Callback;

@FunctionalInterface
public interface SqlCallback<S> extends Callback<S, SQLException> {
	void call(S source) throws SQLException;
}
