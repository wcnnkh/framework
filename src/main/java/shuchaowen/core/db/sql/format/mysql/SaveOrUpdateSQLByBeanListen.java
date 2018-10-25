package shuchaowen.core.db.sql.format.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import shuchaowen.core.beans.BeanListen;
import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class SaveOrUpdateSQLByBeanListen implements SQL{
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQLByBeanListen(BeanListen beanListen, TableInfo tableInfo, String tableName) throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (beanListen.get_field_change_map() == null || beanListen.get_field_change_map().isEmpty()) {
			throw new ShuChaoWenRuntimeException("not change properties");
		}

		List<Object> paramList = new ArrayList<Object>();
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
			paramList.add(columnInfo.getValueToDB(beanListen));
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
			paramList.add(columnInfo.getValueToDB(beanListen));
		}

		for (Entry<String, Object> entry : beanListen.get_field_change_map().entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			if(columnInfo.getPrimaryKey() != null){
				continue;
			}
			
			sb.append(",");
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			paramList.add(columnInfo.getValueToDB(beanListen));
		}
		beanListen.start_field_listen();// 重新开始监听
		this.sql = sb.toString();
		this.params = paramList.toArray();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
