package scw.sql.orm.mysql;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import scw.beans.BeanFieldListen;
import scw.common.exception.ParameterException;
import scw.common.utils.ClassUtils;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;
import scw.sql.orm.annoation.NumberRange;

public class SaveOrUpdateSQLByBeanListen implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQLByBeanListen(BeanFieldListen beanFieldListen, TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (beanFieldListen.get_field_change_map() == null || beanFieldListen.get_field_change_map().isEmpty()) {
			throw new ParameterException("not change properties");
		}

		List<Object> paramList = new LinkedList<Object>();
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

		int index = 0;
		StringBuilder where = new StringBuilder();
		for (Entry<String, Object> entry : beanFieldListen.get_field_change_map().entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			if (columnInfo.getPrimaryKey() != null) {
				continue;
			}

			Object value = columnInfo.getValueToDB(beanFieldListen);
			NumberRange numberRange = columnInfo.getNumberRange();
			if (numberRange != null && ClassUtils.isNumberType(columnInfo.getType())) {
				Object oldValue = entry.getValue();
				if (oldValue != null && value != null) {
					// incr or decr
					double oldV = ClassUtils.getNumberValue(oldValue);
					double newV = ClassUtils.getNumberValue(value);
					if (oldV != newV) {
						if (index++ > 0) {
							sb.append(",");
						}

						double change = newV - oldV;
						sb.append(columnInfo.getSqlColumnName());
						sb.append("=");
						sb.append(columnInfo.getSqlColumnName());
						sb.append(change > 0 ? "+" : "-");
						sb.append(Math.abs(change));

						if (where.length() > 0) {
							where.append(" and ");
						}

						where.append(columnInfo.getSqlColumnName());
						where.append(">=").append(numberRange.min());
						where.append("<=").append(numberRange.max());
					}
					continue;
				}
			}

			if (index++ > 0) {
				sb.append(",");
			}

			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			paramList.add(value);
		}
		beanFieldListen.start_field_listen();// 重新开始监听

		if (where.length() != 0) {
			sb.append(where);
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

	public boolean isStoredProcedure() {
		return false;
	}
}
