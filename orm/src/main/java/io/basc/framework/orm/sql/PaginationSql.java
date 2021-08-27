package io.basc.framework.orm.sql;

import io.basc.framework.sql.Sql;

import java.io.Serializable;

public final class PaginationSql implements Serializable {
	private static final long serialVersionUID = 1L;
	private Sql countSql;
	private Sql resultSql;
	
	//用于序列化
	@SuppressWarnings("unused")
	private PaginationSql(){};

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
