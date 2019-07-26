package scw.sql.orm;

import scw.sql.Sql;

public interface Query<T> extends Sql{
	T and();

	T or();
}