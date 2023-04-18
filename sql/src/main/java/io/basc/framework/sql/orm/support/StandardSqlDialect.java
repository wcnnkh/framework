package io.basc.framework.sql.orm.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import io.basc.framework.aop.support.FieldSetterListen;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Environment;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.ParameterException;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.orm.repository.Condition;
import io.basc.framework.orm.repository.ConditionKeywords;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.WithCondition;
import io.basc.framework.sql.EasySql;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlUtils;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.sql.orm.SqlDialectException;
import io.basc.framework.sql.orm.SqlType;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.Value;

/**
 * 标准的sql方言
 * 
 * @author wcnnkh
 *
 */
public abstract class StandardSqlDialect extends DefaultTableMapper implements SqlDialect {
	protected static final String AND = " and ";
	protected static final String DELETE_PREFIX = "delete from ";
	private static final String IN = " in (";
	protected static final String INSERT_INTO_PREFIX = "insert into ";
	protected static final String OR = " or ";

	private static final char POINT = '.';
	protected static final String SELECT_ALL_PREFIX = "select * from ";
	protected static final String SET = " set ";
	protected static final String UPDATE_PREFIX = "update ";

	protected static final String VALUES = " values ";
	protected static final String WHERE = " where ";

	private Environment environment;
	private String escapeCharacter = "`";

	private void and(StringBuilder sb, List<Object> params, Object entity, Iterator<Column> columns) {
		while (columns.hasNext()) {
			Column column = columns.next();
			Object value = toDataBaseValue(column.getGetter().getValue(entity));
			if (ObjectUtils.isEmpty(value)) {
				continue;
			}

			if (sb.length() != 0) {
				sb.append(" and ");
			}
			keywordProcessing(sb, column.getName());
			sb.append(" = ?");
			params.add(value);
		}
	}

	protected void appendExtendWhere(Column column, StringBuilder sb, Collection<Object> params,
			Map<String, Object> changeMap, Object entity) {
		Collection<Range<Double>> numberRanges = column.getNumberRanges();
		if (!CollectionUtils.isEmpty(numberRanges)) {
			for (Range<Double> range : numberRanges) {
				if (range.getLowerBound().isBounded()) {
					sb.append(AND);
					keywordProcessing(sb, column.getName());
					if (column.isIncrement()) {
						Value newValue = Value.of(toDataBaseValue(column.getGetter().getValue(entity)));
						Value oldValue = changeMap == null ? null
								: Value.of(changeMap.get(column.getSetter().getName()));
						if (oldValue != null) {
							sb.append("+");
							sb.append(newValue.getAsDouble() - oldValue.getAsDouble());
						}
					}
					sb.append(">");
					if (range.getLowerBound().isInclusive()) {
						sb.append("=");
					}
					sb.append(range.getLowerBound().get());
				}

				if (range.getUpperBound().isBounded()) {
					sb.append(AND);
					keywordProcessing(sb, column.getName());
					if (column.isIncrement()) {
						Value newValue = Value.of(toDataBaseValue(column.getGetter().getValue(entity)));
						Value oldValue = changeMap == null ? null
								: Value.of(changeMap.get(column.getSetter().getName()));
						if (oldValue != null) {
							sb.append("+");
							sb.append(newValue.getAsDouble() - oldValue.getAsDouble());
						}
					}
					sb.append("<");
					if (range.getUpperBound().isInclusive()) {
						sb.append("=");
					}
					sb.append(range.getUpperBound().get());
				}
			}
		}

		if (column.isVersion()) {
			Value oldVersion = null;
			if (changeMap != null && changeMap.containsKey(column.getSetter().getName())) {
				oldVersion = Value.of(changeMap.get(column.getSetter().getName()));
				if (oldVersion.getAsLong() == 0) {
					// 如果存在变更但版本号为0就忽略此条件
					return;
				}
			}

			// 因为一定存在主键，所有一定有where条件，此处直接and
			sb.append(AND);
			keywordProcessing(sb, column.getName());
			sb.append("=?");

			// 如果存在旧值就使用旧值
			params.add(oldVersion == null ? toDataBaseValue(column.getGetter().getValue(entity))
					: toDataBaseValue(Value.of(oldVersion.getAsLong())));
		}
	}

	protected void appendOrders(List<? extends OrderColumn> orders, StringBuilder sb) {
		Iterator<? extends OrderColumn> iterator = orders.iterator();
		while (iterator.hasNext()) {
			OrderColumn orderColumn = iterator.next();
			keywordProcessing(sb, orderColumn.getName());
			if (orderColumn.getSort() != null) {
				sb.append(" " + orderColumn.getSort());
			}

			// 不做嵌套
			List<OrderColumn> orderColumns = orderColumn.getWithOrders();
			if (!CollectionUtils.isEmpty(orderColumns)) {
				sb.append(", ");
				appendOrders(orderColumns, sb);
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
	}

	protected final void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			Map<String, Object> changeMap) {
		Value newValue = Value.of(toDataBaseValue(column.getGetter().getValue(entity)));
		Value oldValue = changeMap == null ? null : Value.of(changeMap.get(column.getSetter().getName()));
		appendUpdateValue(sb, params, entity, column, oldValue, newValue);
	}

	protected void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			@Nullable Value oldValue, Value newValue) {
		if (column.isIncrement() && oldValue != null) {
			keywordProcessing(sb, column.getName());
			sb.append("+");
			sb.append(newValue.getAsDouble() - oldValue.getAsDouble());
		} else {
			sb.append("?");
			params.add(newValue.getSource());
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean appendWhere(Condition condition, StringBuilder sb, List<Object> params) {
		if (condition == null || condition.isInvalid() || condition.getParameter() == null) {
			return false;
		}

		ConditionKeywords conditionKeywords = getConditionKeywords();
		if (conditionKeywords.getEqualKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append("=?");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getEndWithKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" like ");
			concat(sb, "'%'", "?");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getEqualOrGreaterThanKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" >= ?");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getEqualOrLessThanKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" <= ?");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getGreaterThanKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" > ?");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getInKeywords().exists(condition.getCondition())) {
			if (!condition.getParameter().isPresent()) {
				return false;
			}

			List<Object> list;
			TypeDescriptor typeDescriptor = condition.getParameter().getTypeDescriptor();
			if (typeDescriptor.isArray() || typeDescriptor.isCollection()) {
				list = (List<Object>) condition.getParameter().convert(
						TypeDescriptor.collection(List.class, typeDescriptor.getElementTypeDescriptor()),
						getEnvironment().getConversionService());
				typeDescriptor = typeDescriptor.getElementTypeDescriptor();
			} else {
				list = Arrays.asList(condition.getParameter().getSource());
			}

			if (list == null || list.isEmpty()) {
				return false;
			}

			keywordProcessing(sb, condition.getParameter().getName());
			Iterator<Object> iterator = list.iterator();
			sb.append(" in(");
			while (iterator.hasNext()) {
				sb.append("?");
				params.add(toDataBaseValue(Value.of(iterator.next(), typeDescriptor)));
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		} else if (conditionKeywords.getLessThanKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" < ?");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getLikeKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" like ");
			concat(sb, "'%'", "?", "'%'");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getNotEqualKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" is not ?");
			params.add(toDataBaseValue(condition.getParameter()));
		} else if (conditionKeywords.getSearchKeywords().exists(condition.getCondition())) {
			String value = condition.getParameter().convert(String.class, getEnvironment().getConversionService());
			if (StringUtils.isEmpty(value)) {
				return false;
			}

			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" like '%");
			for (int i = 0; i < value.length(); i++) {
				sb.append(value.charAt(i));
				sb.append("%");
			}
			sb.append("'");
		} else if (conditionKeywords.getStartWithKeywords().exists(condition.getCondition())) {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" like ");
			concat(sb, "?", "'%'");
			params.add(toDataBaseValue(condition.getParameter()));
		} else {
			keywordProcessing(sb, condition.getParameter().getName());
			sb.append(" ").append(condition.getCondition()).append(" ?");
			params.add(toDataBaseValue(condition.getParameter()));
		}
		return true;
	}

	public void concat(StringBuilder sb, String... strs) {
		if (strs == null || strs.length == 0) {
			return;
		}

		sb.append("concat(");
		for (int i = 0; i < strs.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(strs[i]);
		}
		sb.append(")");
	}

	@Nullable
	protected Map<String, Object> getChangeMap(Object entity) throws SqlDialectException {
		Map<String, Object> changeMap = null;
		if (entity instanceof FieldSetterListen) {
			changeMap = ((FieldSetterListen) entity)._getFieldSetterMap();
			if (CollectionUtils.isEmpty(changeMap)) {
				throw new SqlDialectException("not change properties");
			}
		}
		return changeMap;
	}

	public String getCreateTablePrefix() {
		return "CREATE TABLE IF NOT EXISTS";
	}

	// --------------以下为标准实现-----------------

	@Override
	public Environment getEnvironment() {
		return environment == null ? Sys.getEnv() : environment;
	}

	public String getEscapeCharacter() {
		return escapeCharacter;
	}

	@Override
	public Sql getInIds(TableStructure tableStructure, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException {
		if (CollectionUtils.isEmpty(inPrimaryKeys)) {
			throw new SqlDialectException("in 语句至少要有一个in条件");
		}

		Elements<Column> primaryKeyColumns = tableStructure.getPrimaryKeys();
		int whereSize = ArrayUtils.isEmpty(primaryKeys) ? 0 : primaryKeys.length;
		if (whereSize > primaryKeyColumns.count()) {
			throw new NullPointerException("primaryKeys length  greater than primary key lenght");
		}

		List<Object> params = new ArrayList<Object>(inPrimaryKeys.size() + whereSize);
		StringBuilder sb = new StringBuilder();
		Iterator<Column> iterator = primaryKeyColumns.iterator();
		if (whereSize > 0) {
			for (int i = 0; i < whereSize && iterator.hasNext(); i++) {
				if (sb.length() != 0) {
					sb.append(AND);
				}

				Column column = iterator.next();
				keywordProcessing(sb, column.getName());
				sb.append("=?");
				params.add(toDataBaseValue(Value.of(primaryKeys[i])));
			}
		}

		if (iterator.hasNext()) {
			if (sb.length() != 0) {
				sb.append(AND);
			}

			keywordProcessing(sb, iterator.next().getName());
			sb.append(IN);
			Iterator<?> valueIterator = inPrimaryKeys.iterator();
			while (valueIterator.hasNext()) {
				params.add(toDataBaseValue(Value.of(valueIterator.next())));
				sb.append("?");
				if (valueIterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		String where = sb.toString();
		sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		sb.append(WHERE).append(where);
		return new SimpleSql(sb.toString(), params.toArray());
	}

	public String getSqlName(String tableName, String column) {
		StringBuilder sb = new StringBuilder();
		keywordProcessing(sb, tableName, column);
		return sb.toString();
	}

	public void keywordProcessing(StringBuilder sb, String column) {
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public void keywordProcessing(StringBuilder sb, String tableName, String column) {
		sb.append(getEscapeCharacter()).append(tableName).append(getEscapeCharacter());
		sb.append(POINT);
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void setEscapeCharacter(String escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	@Override
	public Sql toCountSql(Sql sql) throws SqlDialectException {
		String str = sql.getSql();
		str = str.toLowerCase();
		EasySql countSql = new EasySql();
		countSql.append("select count(*) from (");
		int orderIndex = str.lastIndexOf(" order by ");
		if (orderIndex != -1 && str.indexOf(")", orderIndex) == -1) {
			countSql.append(SqlUtils.sub(sql, 0, orderIndex));
		} else {
			// 不存在 order by 子语句
			countSql.append(sql);
		}
		countSql.append(") as count_" + XUtils.getUUID());
		return countSql;
	}

	public Object toDataBaseValue(Value value) {
		if (value == null || !value.isPresent()) {
			return null;
		}

		SqlType sqlType = getSqlType(value.getTypeDescriptor().getType());
		if (sqlType == null) {
			return value;
		}

		return value.convert(TypeDescriptor.valueOf(sqlType.getType()), getEnvironment().getConversionService());
	}

	@Override
	public Sql toDeleteByIdSql(TableStructure tableStructure, Object... ids) throws SqlDialectException {
		Elements<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (primaryKeys.count() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (primaryKeys.count() != ids.length) {
			throw new ParameterException("主键数量不一致:" + tableStructure.getName());
		}

		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableStructure.getName());
		sql.append(WHERE);

		int i = 0;
		Object[] params = new Object[ids.length];
		Iterator<Column> iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sql, column.getName());
			sql.append("=?");
			params[i] = toDataBaseValue(Value.of(ids[i]));
			i++;
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}
		return new SimpleSql(sql.toString(), params);
	}

	@Override
	public Sql toDeleteSql(TableStructure structure, Conditions conditions) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, structure.getName());

		Sql conditionsSql = toSql(conditions);
		if (conditionsSql != null) {
			sql.append(WHERE);
			sql.append(conditionsSql.getSql());
			params.addAll(Arrays.asList(conditionsSql.getParams()));
		}
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public Sql toDeleteSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		Elements<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (primaryKeys.count() == 0) {
			throw new NullPointerException("not found primary key");
		}

		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableStructure.getName());
		sql.append(WHERE);
		Iterator<Column> iterator = tableStructure.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sql, column.getName());
			sql.append("=?");
			params.add(toDataBaseValue(column.getGetter().getValue(entity)));
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}

		// 添加版本号字段变更条件
		tableStructure.getNotPrimaryKeys().forEach((column) -> {
			if (column.isVersion()) {
				// 因为一定存在主键，所有一定有where条件，此处直接and
				sql.append(AND);
				keywordProcessing(sql, column.getName());
				sql.append("=?");
				params.add(toDataBaseValue(column.getGetter().getValue(entity)));
			}
		});
		return new SimpleSql(sql.toString(), params.toArray());
	}

	/**
	 * 只是因为大部分数据库都支持limit请求，所以才写了此默认实现。 并非所以的数据库都支持limit语法，如: sql server
	 */
	@Override
	public Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException {
		Pair<Integer, Integer> range = StringUtils.indexOf(sql.getSql(), "(", ")");
		int fromIndex = 0;
		if (range != null) {
			fromIndex = range.getValue();
		}

		StringBuilder sb;
		if (sql.getSql().toLowerCase().indexOf(" limit ", fromIndex) != -1) {
			// 如果已经存在limit了，那么嵌套一上
			sb = new StringBuilder();
			sb.append("select * from (");
			sb.append(sql.getSql());
			sb.append(")");
		} else {
			sb = new StringBuilder(sql.getSql());
		}

		sb.append(" limit ").append(start);
		if (limit != 0) {
			sb.append(",").append(limit);
		}
		return new SimpleSql(sb.toString(), sql.getParams());
	}

	@Override
	public Sql toMaxIdSql(TableStructure tableStructure, Field field) throws SqlDialectException {
		Column column = tableStructure.getByName(field.getName());
		if (column == null) {
			throw new SqlDialectException("not found " + field);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		keywordProcessing(sb, column.getName());
		sb.append(" from ");
		keywordProcessing(sb, tableStructure.getName());
		sb.append(" order by ");
		keywordProcessing(sb, column.getName());
		sb.append(" desc");
		return new SimpleSql(sb.toString());
	}

	@Override
	public Sql toQuerySql(TableStructure tableStructure, Object query) {
		StringBuilder sb = new StringBuilder(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		StringBuilder whereSql = new StringBuilder();
		List<Object> whereParams = new ArrayList<Object>(8);
		and(whereSql, whereParams, query, tableStructure.columns().iterator());
		if (StringUtils.isEmpty(whereSql)) {
			return new SimpleSql(sb.toString());
		}
		Sql where = new SimpleSql(whereSql.toString(), whereParams.toArray());
		return new SimpleSql(sb.append(" where ").append(where.getSql()).toString(), where.getParams());
	}

	@Override
	public Sql toQuerySqlByIndexs(TableStructure tableStructure, Object query) {
		StringBuilder sb = new StringBuilder(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		StringBuilder whereSql = new StringBuilder();
		List<Object> whereParams = new ArrayList<Object>(8);
		and(whereSql, whereParams, query, tableStructure.columns().filter((col) -> col.hasIndex()).iterator());
		Sql where = new SimpleSql(whereSql.toString(), whereParams.toArray());
		if (StringUtils.isEmpty(where.getSql())) {
			return new SimpleSql(sb.toString());
		}
		return new SimpleSql(sb.append(" where ").append(where.getSql()).toString(), where.getParams());
	}

	@Override
	public Sql toQuerySqlByPrimaryKeys(TableStructure tableStructure, Object query) {
		StringBuilder sb = new StringBuilder(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		StringBuilder whereSql = new StringBuilder();
		List<Object> whereParams = new ArrayList<Object>(8);
		and(whereSql, whereParams, query, tableStructure.columns().filter((col) -> col.isPrimaryKey()).iterator());
		Sql where = new SimpleSql(whereSql.toString(), whereParams.toArray());
		if (StringUtils.isEmpty(where.getSql())) {
			return new SimpleSql(sb.toString());
		}
		return new SimpleSql(sb.append(" where ").append(where.getSql()).toString(), where.getParams());
	}

	@Override
	public Sql toSaveColumnsSql(TableStructure structure, Collection<? extends Parameter> columns) {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<? extends Parameter> iterator = columns.iterator();
		while (iterator.hasNext()) {
			Parameter column = iterator.next();
			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, column.getName());
			values.append("?");
			params.add(toDataBaseValue(column));
		}
		sql.append(INSERT_INTO_PREFIX);
		keywordProcessing(sql, structure.getName());
		sql.append("(");
		sql.append(cols);
		sql.append(")");
		sql.append(VALUES);
		sql.append("(");
		sql.append(values);
		sql.append(")");
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public Sql toSaveSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.columns().iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement() && !hasEffectiveValue(entity, column)) {
				continue;
			}

			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, column.getName());
			values.append("?");
			params.add(toDataBaseValue(column.getGetter().getValue(entity)));
		}
		sql.append(INSERT_INTO_PREFIX);
		keywordProcessing(sql, tableStructure.getName());
		sql.append("(");
		sql.append(cols);
		sql.append(")");
		sql.append(VALUES);
		sql.append("(");
		sql.append(values);
		sql.append(")");
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public Sql toSelectByIdsSql(TableStructure tableStructure, Object... ids) throws SqlDialectException {
		Elements<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (ids.length > primaryKeys.count()) {
			throw new SqlDialectException("Wrong number of primary key parameters");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		Iterator<Column> iterator = primaryKeys.iterator();
		Iterator<Object> valueIterator = Arrays.asList(ids).iterator();
		if (iterator.hasNext() && valueIterator.hasNext()) {
			sb.append(WHERE);
		}

		Object[] params = new Object[ids.length];
		int i = 0;
		while (iterator.hasNext() && valueIterator.hasNext()) {
			Column column = iterator.next();
			Object value = valueIterator.next();
			params[i++] = toDataBaseValue(Value.of(value, getConversionService()));
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			if (iterator.hasNext() && valueIterator.hasNext()) {
				sb.append(AND);
			}
		}
		return new SimpleSql(sb.toString(), params);
	}

	@Override
	public Sql toSelectSql(TableStructure structure, Conditions conditions, List<? extends OrderColumn> orders) {
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, structure.getName());
		List<Object> params = new ArrayList<Object>();

		Sql conditionsSql = toSql(conditions);
		if (conditionsSql != null) {
			sb.append(WHERE);
			sb.append(conditionsSql.getSql());
			params.addAll(Arrays.asList(conditionsSql.getParams()));
		}

		if (!CollectionUtils.isEmpty(orders)) {
			sb.append(" order by ");
			appendOrders(orders, sb);
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public Sql toSql(Conditions conditions) {
		return toString(conditions, new AtomicInteger());
	}

	public Sql toString(Conditions conditions, AtomicInteger count) {
		if (conditions == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		if (appendWhere(conditions.getCondition(), sb, params)) {
			count.getAndIncrement();
		}

		List<WithCondition> withConditions = conditions.getWiths();
		if (!CollectionUtils.isEmpty(withConditions)) {
			for (WithCondition condition : withConditions) {
				AtomicInteger withCount = new AtomicInteger();
				Sql sql = toString(condition.getCondition(), withCount);
				if (sql == null || withCount.get() == 0) {
					continue;
				}

				if (count.get() > 0) {
					if (getRelationshipKeywords().getAndKeywords().exists(condition.getWith())) {
						sb.append(" and ");
					} else if (getRelationshipKeywords().getOrKeywords().exists(condition.getWith())) {
						sb.append(" or ");
					} else {
						sb.append(" ").append(condition.getWith()).append(" ");
					}
				}

				if (withCount.get() > 1) {
					sb.append("(");
				}

				sb.append(sql.getSql());
				params.addAll(Arrays.asList(sql.getParams()));

				if (withCount.get() > 1) {
					sb.append(")");
				}

				count.getAndIncrement();
			}
		}

		if (count.get() == 0) {
			return null;
		}

		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public Sql toUpdatePartSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		Map<String, Object> changeMap = getChangeMap(entity);
		return toUpdateSql(tableStructure, entity, changeMap, (column) -> {
			if (changeMap != null && !changeMap.containsKey(column.getSetter().getName())) {
				return false;
			}

			// 如果字段不能为空，且实体字段没有值就忽略
			if (!column.isNullable() && !hasEffectiveValue(entity, column)) {
				return false;
			}
			return true;
		});
	}

	@Override
	public Sql toUpdateSql(TableStructure structure, Collection<? extends Parameter> columns, Conditions conditions) {
		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, structure.getName());
		sb.append(SET);
		List<Object> params = new ArrayList<Object>();
		Iterator<? extends Parameter> iterator = columns.iterator();
		while (iterator.hasNext()) {
			Parameter column = iterator.next();
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			params.add(toDataBaseValue(column));
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		Sql conditionsSql = toSql(conditions);
		if (conditionsSql != null) {
			sb.append(WHERE);
			sb.append(conditionsSql.getSql());
			params.addAll(Arrays.asList(conditionsSql.getParams()));
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public Sql toUpdateSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		Map<String, Object> changeMap = getChangeMap(entity);
		return toUpdateSql(tableStructure, entity, changeMap, (column) -> true);
	}

	protected Sql toUpdateSql(TableStructure tableStructure, Object entity, Map<String, Object> changeMap,
			Predicate<Column> accept) throws SqlDialectException {
		Elements<Column> primaryKeyColumns = tableStructure.getPrimaryKeys();
		if (primaryKeyColumns.count() == 0) {
			throw new SqlDialectException(tableStructure.getName() + " not found primary key");
		}

		Elements<Column> notPrimaryKeys = tableStructure.getNotPrimaryKeys();
		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		sb.append(SET);
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (changeMap != null && !changeMap.containsKey(column.getSetter().getName())) {
				// 忽略没有变化的字段
				continue;
			}

			if (!accept.test(column)) {
				continue;
			}

			keywordProcessing(sb, column.getName());
			sb.append("=");
			appendUpdateValue(sb, params, entity, column, changeMap);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append(WHERE);
		iterator = primaryKeyColumns.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			params.add(toDataBaseValue(column.getGetter().getValue(entity)));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		// 添加版本号字段变更条件
		notPrimaryKeys.forEach((column) -> {
			if (!accept.test(column)) {
				return;
			}

			appendExtendWhere(column, sb, params, changeMap, entity);
		});
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public <T> Sql toUpdateSql(TableStructure tableStructure, T entity, T condition) throws SqlDialectException {
		Map<String, Object> changeMap = new HashMap<String, Object>();
		tableStructure.columns().forEach((column) -> {
			Object value = column.get(condition);
			if (value == null && column.isNullable()) {
				return;
			}

			changeMap.put(column.getSetter().getName(), value);
		});
		return toUpdateSql(tableStructure, entity, changeMap, (col) -> true);
	}
}
