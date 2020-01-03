package scw.orm.sql.dialect.mysql;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.FieldSetterListen;
import scw.core.utils.TypeUtils;
import scw.lang.NotFoundException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.FieldColumn;
import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.annotation.Counter;
import scw.orm.sql.enums.CasType;
import scw.orm.support.SimpleGetter;

public final class UpdateSQLByBeanListen extends MysqlDialectSql {
	private static Logger logger = LoggerUtils.getLogger(UpdateSQLByBeanListen.class);
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQLByBeanListen(SqlMapper mappingOperations, Class<?> clazz, FieldSetterListen beanFieldListen,
			String tableName) {
		ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
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
			FieldColumn column = (FieldColumn) context.getColumn();
			if (mappingOperations.getCasType(context) != CasType.AUTO_INCREMENT) {
				continue;
			}

			Field field = column.getField();
			if (field == null) {
				logger.warn("不支持的字段:{}", column.getName());
				continue;
			}

			if (changeMap.containsKey(field.getName())) {
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

		for (Entry<String, Object> entry : changeMap.entrySet()) {
			MappingContext context = tableFieldContext.getMappingContext(entry.getKey());
			if (mappingOperations.isPrimaryKey(context)) {
				continue;
			}

			FieldColumn column = (FieldColumn) context.getColumn();
			Field field = column.getField();
			if (field == null) {
				logger.warn("不支持的字段：{}", column.getName());
				continue;
			}

			Object value = mappingOperations.getter(context, beanFieldListen);
			Counter counter = context.getColumn().getAnnotation(Counter.class);
			if (counter != null && TypeUtils.isNumber(field.getType())) {
				Object oldValue = entry.getValue();
				if (oldValue != null && value != null) {
					// incr or decr
					double oldV = getNumberValue(oldValue);
					double newV = getNumberValue(value);

					if (index++ > 0) {
						sb.append(",");
					}

					double change = newV - oldV;
					keywordProcessing(sb, context.getColumn().getName());
					sb.append("=");

					keywordProcessing(sb, context.getColumn().getName());
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
						keywordProcessing(where, context.getColumn().getName());
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append(">=").append(counter.min());
						where.append(AND);
						keywordProcessing(where, context.getColumn().getName());
						where.append(change > 0 ? "+" : "-");
						where.append(Math.abs(change));
						where.append("<=").append(counter.max());
					}
					continue;
				} else {
					logger.warn("{}中计数器字段[{}]不能为空,class:{},oldValue={},newValue={}", clazz.getName(),
							context.getColumn().getName(), oldValue, value);
				}
			}

			if (index++ > 0) {
				sb.append(",");
			}

			keywordProcessing(sb, context.getColumn().getName());
			sb.append("=?");
			paramList.add(value);
		}

		sb.append(WHERE);

		iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sb, context.getColumn().getName());
			sb.append("=?");
			paramList.add(mappingOperations.getter(context, beanFieldListen));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		iterator = tableFieldContext.getNotPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (mappingOperations.getCasType(context) == CasType.NOTHING) {
				continue;
			}

			FieldColumn column = (FieldColumn) context.getColumn();
			Field field = column.getField();
			if (field == null) {
				logger.warn("不支持的字段:{}", column.getName());
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			Object value;
			if (changeMap.containsKey(column.getField().getName())) {
				// 存在旧值
				value = mappingOperations.getter(context, new SimpleGetter(changeMap.get(column.getField().getName())));
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
