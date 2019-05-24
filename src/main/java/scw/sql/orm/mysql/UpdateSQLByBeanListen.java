package scw.sql.orm.mysql;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.exception.ParameterException;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableFieldListen;
import scw.sql.orm.TableInfo;
import scw.sql.orm.annotation.Counter;

public final class UpdateSQLByBeanListen implements Sql {
	private static Logger logger = LoggerFactory.getLogger(UpdateSQLByBeanListen.class);
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQLByBeanListen(TableFieldListen beanFieldListen, TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		Map<String, Object> changeMap = beanFieldListen.get_field_change_map();
		if (changeMap == null || changeMap.isEmpty()) {
			throw new ParameterException("not change properties");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UpdateSQL.UPDATE_PREFIX);
		sb.append(tableName);
		sb.append(UpdateSQL.SET);

		int index = 0;
		StringBuilder where = null;
		ColumnInfo columnInfo;
		List<Object> paramList = new LinkedList<Object>();
		for (Entry<String, Object> entry : changeMap.entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			if (columnInfo.isPrimaryKey()) {
				continue;
			}

			Object value = columnInfo.getValueToDB(beanFieldListen);
			Counter counter = columnInfo.getCounter();
			if (counter != null && ClassUtils.isNumberType(columnInfo.getType())) {
				Object oldValue = entry.getValue();
				if (oldValue != null && value != null) {
					// incr or decr
					double oldV = getNumberValue(oldValue);
					double newV = getNumberValue(value);

					if (index++ > 0) {
						sb.append(",");
					}

					double change = newV - oldV;
					sb.append("`");
					sb.append(columnInfo.getName());
					sb.append("`=");

					sb.append("`");
					sb.append(columnInfo.getName());
					sb.append(change > 0 ? "`+" : "`-");
					sb.append(Math.abs(change));

					if (where == null) {
						where = new StringBuilder();
					} else {
						where.append(UpdateSQL.AND);
					}

					if (change == 0) {
						where.append("1 != 1");
					} else {
						where.append("`");
						where.append(columnInfo.getName());
						where.append(change > 0 ? "`+" : "`-");
						where.append(Math.abs(change));
						where.append(">=").append(counter.min());
						where.append(UpdateSQL.AND);
						where.append("`");
						where.append(columnInfo.getName());
						where.append("`");
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append("<=").append(counter.max());
					}
					continue;
				} else {
					logger.warn("{}中计数器字段[{}]不能为空,class:{},oldValue={},newValue={}", tableInfo.getSource().getName(),
							columnInfo.getField().getName(), oldValue, value);
				}
			}

			if (index++ > 0) {
				sb.append(",");
			}

			sb.append("`");
			sb.append(columnInfo.getName());
			sb.append("`=?");
			paramList.add(value);
		}

		beanFieldListen.clear_field_listen();// 重新开始监听

		sb.append(UpdateSQL.WHERE);
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(UpdateSQL.AND);
			}

			sb.append("`");
			sb.append(columnInfo.getName());
			sb.append("`=?");

			paramList.add(columnInfo.getValueToDB(beanFieldListen));
		}

		if (where != null) {
			sb.append(UpdateSQL.AND).append(where);
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

	public static double getNumberValue(Object value) {
		if (value == null) {
			throw new NullPointerException();
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else {
			return (Double) value;
		}
	}
}
