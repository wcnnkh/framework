package scw.sql.orm;

import java.util.List;

import scw.db.sql.Select;
import scw.sql.Sql;
import scw.sql.orm.result.ResultSet;

public interface SqlSelect {

	ResultSet select(Sql sql);

	<T> List<T> select(Class<T> type, Sql sql);

	<T> T selectOne(Class<T> type, Sql sql);
	
	Select createSelect();
}
