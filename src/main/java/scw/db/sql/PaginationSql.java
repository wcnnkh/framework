package scw.db.sql;

import java.io.Serializable;

import scw.database.SQL;

public class PaginationSql implements Serializable {
	private static final long serialVersionUID = 1L;
	private SQL countSql;
	private SQL resultSql;

	public PaginationSql(SQL countSql, SQL resultSql) {
		this.countSql = countSql;
		this.resultSql = resultSql;
	}

	public SQL getCountSql() {
		return countSql;
	}

	public SQL getResultSql() {
		return resultSql;
	}
}
