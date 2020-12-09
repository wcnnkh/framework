package scw.sql.orm;

import scw.sql.Sql;

public class ORMException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ORMException(String message){
		super(message);
	}

	public ORMException(String message, Throwable e) {
		super(message, e);
	}
	
	public ORMException(Sql sql, Throwable e) {
		this(sql.toString(), e);
	}
}
