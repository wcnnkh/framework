package scw.sql.orm.mysql;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.FieldSetterListen;
import scw.core.exception.ParameterException;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;
import scw.sql.orm.annotation.Counter;
import scw.sql.orm.enums.CasType;

public final class UpdateSQLByBeanListen extends MysqlOrmSql {
	private static Logger logger = LoggerFactory.getLogger(UpdateSQLByBeanListen.class);
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQLByBeanListen(FieldSetterListen beanFieldListen, TableInfo tableInfo, String tableName)
			throws Exception {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		Map<String, Object> changeMap = beanFieldListen.get_field_setter_map();
		if (changeMap == null || changeMap.isEmpty()) {
			throw new ParameterException("not change properties");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);

		int index = 0;
		StringBuilder where = null;
		List<Object> paramList = new LinkedList<Object>();
		for (ColumnInfo columnInfo : tableInfo.getNotPrimaryKeyColumns()) {
			if (columnInfo.getCasType() != CasType.AUTO_INCREMENT) {
				continue;
			}

			if (changeMap.containsKey(columnInfo.getField().getName())) {
				continue;
			}

			if (index++ > 0) {
				sb.append(",");
			}

			keywordProcessing(sb, columnInfo.getName());
			sb.append("=");
			keywordProcessing(sb, columnInfo.getName());
			sb.append("+1");
		}

		ColumnInfo columnInfo;
		for (Entry<String, Object> entry : changeMap.entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			if (columnInfo.isPrimaryKey()) {
				continue;
			}

			Object value = columnInfo.get(beanFieldListen);
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
					keywordProcessing(sb, columnInfo.getName());
					sb.append("=");

					keywordProcessing(sb, columnInfo.getName());
					sb.append(change > 0 ? "+" : "-");
					sb.append(Math.abs(change));

					if (where == null) {
						where = new StringBuilder();
					} else {
						where.append(AND);
					}

					if (change == 0) {
						where.append("1 != 1");
					} else {
						keywordProcessing(where, columnInfo.getName());
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append(">=").append(counter.min());
						where.append(AND);
						keywordProcessing(where, columnInfo.getName());
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

			keywordProcessing(sb, columnInfo.getName());
			sb.append("=?");
			paramList.add(value);
		}

		sb.append(WHERE);
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(AND);
			}

			sb.append("`");
			sb.append(columnInfo.getName());
			sb.append("`=?");
			paramList.add(columnInfo.get(beanFieldListen));
		}

		for (int i = 0; i < tableInfo.getNotPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getNotPrimaryKeyColumns()[i];
			if (columnInfo.getCasType() == CasType.NOTHING) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, columnInfo.getName());
			sb.append("=?");
			if (changeMap.containsKey(columnInfo.getField().getName())) {
				// 存在旧值
				paramList.add(changeMap.get(columnInfo.getField().getName()));
			} else {
				paramList.add(columnInfo.get(beanFieldListen));
			}
		}
		beanFieldListen.clear_field_setter_listen();// 重新开始监听

		if (where != null) {
			sb.append(AND).append(where);
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
