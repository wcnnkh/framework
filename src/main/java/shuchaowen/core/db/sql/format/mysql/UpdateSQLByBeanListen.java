package shuchaowen.core.db.sql.format.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import shuchaowen.core.beans.BeanFieldListen;
import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class UpdateSQLByBeanListen implements SQL {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQLByBeanListen(BeanFieldListen beanFieldListen, TableInfo tableInfo, String tableName) throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (beanFieldListen.get_field_change_map() == null || beanFieldListen.get_field_change_map().size() == 0) {
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
		List<Object> paramList = new ArrayList<Object>();
		for (Entry<String, Object> entry : beanFieldListen.get_field_change_map().entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			if(columnInfo.getPrimaryKey() != null){
				continue;
			}
			
			if (index++ > 0) {
				sb.append(",");
			}
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			paramList.add(columnInfo.getValueToDB(beanFieldListen));
		}
		beanFieldListen.start_field_listen();// 重新开始监听

		sb.append(" where ");
		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(" and ");
			}
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			
			paramList.add(columnInfo.getValueToDB(beanFieldListen));
		}
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
