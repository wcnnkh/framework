package scw.sql.orm.mysql;

import java.util.LinkedList;
import java.util.List;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;
import scw.sql.orm.annotation.Counter;

public class SaveOrUpdateSQL extends MysqlOrmSql {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(SaveOrUpdateSQL.class);
	private static final String TEMP = ") ON DUPLICATE KEY UPDATE ";
	private static final String IF = "IF(";
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQL(Object obj, TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		ColumnInfo columnInfo;
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new LinkedList<Object>();
		int i;
		for (i = 0; i < tableInfo.getColumns().length; i++) {
			columnInfo = tableInfo.getColumns()[i];
			if (i > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, columnInfo.getName());
			values.append("?");
			params.add(ORMUtils.get(columnInfo.getField(), obj));
		}

		sb.append(INSERT_INTO_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append("(");
		sb.append(cols);
		sb.append(InsertSQL.VALUES);
		sb.append(values);
		sb.append(TEMP);

		int index = 0;
		for (i = 0; i < tableInfo.getColumns().length; i++) {
			columnInfo = tableInfo.getColumns()[i];
			if (columnInfo.isPrimaryKey()) {
				continue;
			}

			Object v = ORMUtils.get(columnInfo.getField(), obj);
			Counter counter = columnInfo.getCounter();
			if (index++ > 0) {
				sb.append(",");
			}

			if (counter == null) {
				keywordProcessing(sb, columnInfo.getName());
				sb.append("=?");
				params.add(v);
			} else {
				if (v == null) {
					logger.warn("{}中计数器字段{}的值为空", tableInfo.getSource().getName(), columnInfo.getName());
					keywordProcessing(sb, columnInfo.getName());
					sb.append("=?");
					params.add(v);
				} else {
					keywordProcessing(sb, columnInfo.getName());
					sb.append("=");
					sb.append(IF);
					keywordProcessing(sb, columnInfo.getName());
					sb.append("+").append(v);
					sb.append(">=").append(counter.min());
					sb.append(AND);
					keywordProcessing(sb, columnInfo.getName());
					sb.append("+").append(v);
					sb.append("<=").append(counter.max());
					sb.append(",");
					keywordProcessing(sb, columnInfo.getName());
					sb.append("+?");
					params.add(v);
					sb.append(",");
					keywordProcessing(sb, columnInfo.getName());
					sb.append(")");
				}
			}
		}

		this.sql = sb.toString();
		this.params = params.toArray();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
