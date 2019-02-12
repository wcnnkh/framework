package scw.db.sql;

import java.io.Serializable;

import scw.sql.Sql;

public class PaginationSql implements Serializable {
	private static final long serialVersionUID = 1L;
	private Sql countSql;
	private Sql resultSql;

	public PaginationSql(Sql countSql, Sql resultSql) {
		this.countSql = countSql;
		this.resultSql = resultSql;
	}

	public Sql getCountSql() {
		return countSql;
	}

	public Sql getResultSql() {
		return resultSql;
	}
}
