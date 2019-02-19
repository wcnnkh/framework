package scw.sql.orm.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import scw.beans.BeanFieldListen;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public class SaveOrUpdateSQLByBeanListen implements Sql{
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQLByBeanListen(BeanFieldListen beanFieldListen, TableInfo tableInfo, String tableName) throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (beanFieldListen.get_field_change_map() == null || beanFieldListen.get_field_change_map().isEmpty()) {
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
			paramList.add(columnInfo.getValueToDB(beanFieldListen));
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
			paramList.add(columnInfo.getValueToDB(beanFieldListen));
		}

		for (Entry<String, Object> entry : beanFieldListen.get_field_change_map().entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			if(columnInfo.getPrimaryKey() != null){
				continue;
			}
			
			sb.append(",");
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			paramList.add(columnInfo.getValueToDB(beanFieldListen));
		}
		beanFieldListen.start_field_listen();// 重新开始监听
		this.sql = sb.toString();
		this.params = paramList.toArray();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	public boolean isStoredProcedure() {
		return false;
	}
}
