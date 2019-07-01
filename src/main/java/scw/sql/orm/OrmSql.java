package scw.sql.orm;

import scw.sql.Sql;

public abstract class OrmSql implements Sql {
	private static final long serialVersionUID = 1L;
	protected static final String UPDATE_PREFIX = "update ";
	protected static final String DELETE_PREFIX = "delete from ";
	protected static final String SELECT_ALL_PREFIX = "select * from ";
	protected static final String INSERT_INTO_PREFIX = "insert into ";

	protected static final String SET = " set ";
	protected static final String WHERE = " where ";
	protected static final String AND = " and ";
	protected static final String OR = " or ";

	public boolean isStoredProcedure() {
		return false;
	}

	/**
	 * 关键字处理
	 * 
	 * @param sb
	 * @param text
	 */
	public abstract void keywordProcessing(StringBuilder sb, String text);

	public abstract void keywordProcessing(StringBuilder sb, String tableName, String column);

	public abstract String getSqlName(String tableName, String column);
}
