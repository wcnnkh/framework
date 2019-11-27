package scw.orm.sql.dialect.mysql;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.FieldSetterListen;
import scw.core.exception.NotFoundException;
import scw.core.utils.TypeUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.SimpleGetter;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableFieldContext;
import scw.sql.orm.annotation.Counter;
import scw.sql.orm.enums.CasType;

public final class UpdateSQLByBeanListen extends MysqlDialectSql {
	private static Logger logger = LoggerUtils.getLogger(UpdateSQLByBeanListen.class);
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQLByBeanListen(MappingOperations mappingOperations, Class<?> clazz, FieldSetterListen beanFieldListen,
			String tableName) throws Exception {
		TableFieldContext tableFieldContext = SqlORMUtils.getTableFieldContext(mappingOperations, clazz);
		if (tableFieldContext.getPrimaryKeys().size() == 0) {
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
		Iterator<MappingContext> iterator = tableFieldContext.getNotPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (SqlORMUtils.getCasType(context.getFieldDefinition()) != CasType.AUTO_INCREMENT) {
				continue;
			}

			if (changeMap.containsKey(context.getFieldDefinition().getField().getName())) {
				continue;
			}

			if (index++ > 0) {
				sb.append(",");
			}

			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("=");
			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("+1");
		}

		for (Entry<String, Object> entry : changeMap.entrySet()) {
			MappingContext context = tableFieldContext.getMappingContext(entry.getKey());
			if (SqlORMUtils.isPrimaryKey(context.getFieldDefinition())) {
				continue;
			}

			Object value = mappingOperations.getter(context, beanFieldListen);
			Counter counter = context.getFieldDefinition().getAnnotation(Counter.class);
			if (counter != null && TypeUtils.isNumber(context.getFieldDefinition().getField().getType())) {
				Object oldValue = entry.getValue();
				if (oldValue != null && value != null) {
					// incr or decr
					double oldV = getNumberValue(oldValue);
					double newV = getNumberValue(value);

					if (index++ > 0) {
						sb.append(",");
					}

					double change = newV - oldV;
					keywordProcessing(sb, context.getFieldDefinition().getName());
					sb.append("=");

					keywordProcessing(sb, context.getFieldDefinition().getName());
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
						keywordProcessing(where, context.getFieldDefinition().getName());
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append(">=").append(counter.min());
						where.append(AND);
						keywordProcessing(where, context.getFieldDefinition().getName());
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append("<=").append(counter.max());
					}
					continue;
				} else {
					logger.warn("{}中计数器字段[{}]不能为空,class:{},oldValue={},newValue={}", clazz.getName(),
							context.getFieldDefinition().getName(), oldValue, value);
				}
			}

			if (index++ > 0) {
				sb.append(",");
			}

			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("=?");
			paramList.add(value);
		}

		sb.append(WHERE);

		iterator = tableFieldContext.getNotPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("=?");
			paramList.add(mappingOperations.getter(context, beanFieldListen));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		iterator = tableFieldContext.getNotPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (SqlORMUtils.getCasType(context.getFieldDefinition()) == CasType.NOTHING) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, context.getFieldDefinition().getName());
			sb.append("=?");
			Object value;
			if (changeMap.containsKey(context.getFieldDefinition().getField().getName())) {
				// 存在旧值
				value = mappingOperations.getter(context,
						new SimpleGetter(changeMap.get(context.getFieldDefinition().getField().getName())));
			} else {
				value = mappingOperations.getter(context, beanFieldListen);
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
