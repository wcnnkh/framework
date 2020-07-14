package scw.sql.orm.dialect.mysql;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.aop.support.FieldSetterListen;
import scw.core.utils.TypeUtils;
import scw.lang.NotFoundException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;
import scw.sql.orm.Column;
import scw.sql.orm.CounterInfo;
import scw.sql.orm.ObjectRelationalMapping;
import scw.sql.orm.enums.CasType;
import scw.value.AnyValue;

public final class UpdateSQLByBeanListen extends MysqlDialectSql {
	private static Logger logger = LoggerUtils.getLogger(UpdateSQLByBeanListen.class);
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQLByBeanListen(ObjectRelationalMapping objectRelationalMapping, Class<?> clazz,
			FieldSetterListen beanFieldListen, String tableName) {
		Collection<Column> primaryKeys = objectRelationalMapping.getPrimaryKeys(clazz);
		if (primaryKeys.size() == 0) {
			throw new NotFoundException("not found primary key");
		}

		Map<String, Object> changeMap = beanFieldListen.get_field_setter_map();
		if (changeMap == null || changeMap.isEmpty()) {
			throw new RuntimeException("not change properties");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);

		int index = 0;
		StringBuilder where = null;
		List<Object> paramList = new LinkedList<Object>();

		Collection<Column> notPrimaryKeys = objectRelationalMapping.getNotPrimaryKeys(clazz);
		Iterator<Column> iterator = notPrimaryKeys.iterator();
		// 处理CasType.AUTO_INCREMENT字段
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.getCasType() != CasType.AUTO_INCREMENT) {
				continue;
			}

			Field field = column.getField();
			if (changeMap.containsKey(field.getSetter().getName())) {
				continue;
			}

			if (index++ > 0) {
				sb.append(",");
			}

			keywordProcessing(sb, column.getName());
			sb.append("=");
			keywordProcessing(sb, column.getName());
			sb.append("+1");
		}

		for (Column column : notPrimaryKeys) {
			if (!(changeMap.containsKey(column.getField().getGetter().getName()) || column.isForceUpdate())) {
				continue;
			}

			Object oldValue = changeMap.get(column.getField().getSetter().getName());
			Object value = column.getField().getGetter().get(beanFieldListen);
			CounterInfo counterInfo = column.getCounterInfo();
			if (counterInfo != null && TypeUtils.isNumber(column.getField().getSetter().getType())) {
				if (oldValue != null && value != null) {
					// incr or decr
					double oldV = new AnyValue(oldValue).getAsDoubleValue();
					double newV = new AnyValue(value).getAsDoubleValue();

					if (index++ > 0) {
						sb.append(",");
					}

					double change = newV - oldV;
					keywordProcessing(sb, column.getName());
					sb.append("=");

					keywordProcessing(sb, column.getName());
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
						keywordProcessing(where, column.getName());
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append(">=").append(counterInfo.getMin());
						where.append(AND);
						keywordProcessing(where, column.getName());
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append("<=").append(counterInfo.getMax());
					}
					continue;
				} else {
					logger.warn("{}中计数器字段[{}]不能为空,class:{},oldValue={},newValue={}", clazz.getName(), column.getName(),
							oldValue, value);
				}
			}

			if (index++ > 0) {
				sb.append(",");
			}

			keywordProcessing(sb, column.getName());
			sb.append("=?");
			paramList.add(column.toDataBaseValue(value));
		}

		sb.append(WHERE);

		iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			paramList.add(column.get(beanFieldListen));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.getCasType() == CasType.NOTHING) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			Object value;
			if (changeMap.containsKey(column.getField().getSetter().getName())) {
				// 存在旧值
				value = column.toDataBaseValue(changeMap.get(column.getField().getSetter().getName()));
			} else {
				value = column.get(beanFieldListen);
			}
			paramList.add(value);
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
}
