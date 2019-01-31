package scw.jdbc;

import java.util.List;

public interface SqlOperations {

	boolean execute(Sql sql) throws SqlException;

	void query(Sql sql, ResultSetCallback resultSetCallback) throws SqlException;

	void query(Sql sql, RowCallback rowCallback) throws SqlException;

	<T> T query(Sql sql, ResultSetMapper<T> resultSetMapper) throws SqlException;

	<T> List<T> query(Sql sql, RowMapper<T> rowMapper) throws SqlException;

	int update(Sql sql) throws SqlException;
}
