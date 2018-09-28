package shuchaowen.core.db.sql.format.mysql;

import java.util.Map.Entry;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class SaveOrUpdateSQLByBeanProxy implements SQL{
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQLByBeanProxy(BeanProxy beanProxy, TableInfo tableInfo, String tableName) throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (beanProxy.getChange_ColumnMap() == null || beanProxy.getChange_ColumnMap().isEmpty()) {
			throw new ShuChaoWenRuntimeException("not change properties");
		}

		this.params = new Object[tableInfo.getColumns().length + tableInfo.getPrimaryKeyColumns().length
				+ beanProxy.getChange_ColumnMap().size()];
		int index = 0;
		StringBuilder sb = new StringBuilder(512);
		ColumnInfo columnInfo;
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		int i;
		for (i = 0; i < tableInfo.getColumns().length; i++) {
			columnInfo = tableInfo.getColumns()[i];
			if (i > 0) {
				cols.append(",");
				values.append(",");
			}

			cols.append(columnInfo.getSqlColumnName());
			values.append("?");
			params[index++] = columnInfo.getValueToDB(beanProxy);
		}

		sb.append("insert into `");
		sb.append(tableName);
		sb.append("`(");
		sb.append(cols);
		sb.append(") values(");
		sb.append(values);
		sb.append(") ON DUPLICATE KEY UPDATE ");

		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(",");
			}
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			params[index++] = columnInfo.getValueToDB(beanProxy);
		}

		for (Entry<String, Object> entry : beanProxy.getChange_ColumnMap().entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			sb.append(",");
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			params[index++] = columnInfo.getValueToDB(beanProxy);
		}
		beanProxy.startListen();// 重新开始监听
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
