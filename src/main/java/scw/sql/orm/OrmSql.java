package scw.sql.orm;

import scw.sql.Sql;

public abstract class OrmSql implements Sql {
	private static final long serialVersionUID = 1L;
	protected static final String UPDATE_PREFIX = "update ";
	protected static final String DELETE_PREFIX = "delete from ";

	protected static final String SET = " set ";
	protected static final String WHERE = " where ";
	protected static final String AND = " and ";
	protected static final String OR = " or ";

	public boolean isStoredProcedure() {
		return false;
	}

	/**
	 * 关键字处理
	 * @param sb
	 * @param text
	 */
	protected abstract void keywordProcessing(StringBuilder sb, String text);
}
