package shuchaowen.core.db.excepion;

import java.util.Arrays;

import shuchaowen.core.db.sql.SQL;

public class ExecuteSqlException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ExecuteSqlException(SQL sql, Throwable e){
		super(sqlToMessage(sql), e);
	}
	
	private static String sqlToMessage(SQL sql){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(sql.getSql());
		sb.append("}");
		if(sql.getParams() != null){
			sb.append(Arrays.toString(sql.getParams()));
		}
		return sb.toString();
	}
}
