package scw.sql.orm.mysql;

import scw.sql.orm.OrmSql;

public abstract class MysqlOrmSql extends OrmSql {
	private static final long serialVersionUID = 1L;
	private static final char ESCAPE_CHARACTER = '`';

	@Override
	protected void keywordProcessing(StringBuilder sb, String text) {
		sb.append(ESCAPE_CHARACTER).append(text).append(ESCAPE_CHARACTER);
	}
}
