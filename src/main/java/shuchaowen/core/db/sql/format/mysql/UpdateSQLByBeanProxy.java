package shuchaowen.core.db.sql.format.mysql;

import java.util.Map.Entry;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class UpdateSQLByBeanProxy implements SQL {
	private String sql;
	private Object[] params;

	public UpdateSQLByBeanProxy(BeanProxy beanProxy, TableInfo tableInfo, String tableName) {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (beanProxy.getChange_ColumnMap() == null || beanProxy.getChange_ColumnMap().size() == 0) {
			throw new ShuChaoWenRuntimeException("not change properties");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append("update ");
		sb.append("`");
		sb.append(tableName);
		sb.append("`");
		sb.append(" set ");
		int index = 0;
		int i;
		ColumnInfo columnInfo;
		params = new Object[beanProxy.getChange_ColumnMap().size() + tableInfo.getPrimaryKeyColumns().length];
		for (Entry<String, Object> entry : beanProxy.getChange_ColumnMap().entrySet()) {
			if (index > 0) {
				sb.append(",");
			}

			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			params[index++] = columnInfo.getValue(beanProxy);
		}
		beanProxy.startListen();// 重新开始监听

		sb.append(" where ");
		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(" and ");
			}
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			params[index++] = columnInfo.getValue(beanProxy);
		}
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
