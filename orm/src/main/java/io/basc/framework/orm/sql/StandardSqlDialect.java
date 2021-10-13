package io.basc.framework.orm.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.aop.support.FieldSetterListen;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.ParameterException;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.annotation.Version;
import io.basc.framework.orm.sql.annotation.AnnotationTableResolver;
import io.basc.framework.orm.sql.annotation.Counter;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.AnyValue;

/**
 * 标准的sql方言
 * 
 * @author shuchaowen
 *
 */
public abstract class StandardSqlDialect extends AnnotationTableResolver implements SqlDialect {
	protected static final String UPDATE_PREFIX = "update ";
	protected static final String DELETE_PREFIX = "delete from ";
	protected static final String SELECT_ALL_PREFIX = "select * from ";
	protected static final String INSERT_INTO_PREFIX = "insert into ";
	protected static final String VALUES = " values ";

	protected static final String SET = " set ";
	protected static final String WHERE = " where ";
	protected static final String AND = " and ";
	protected static final String OR = " or ";

	private static final String IN = " in (";
	private static final char POINT = '.';

	private String escapeCharacter = "`";
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Object getDataBaseValue(Object entity, Field field) {
		return toDataBaseValue(field.get(entity), new TypeDescriptor(field.getGetter()));
	}

	public Object toDataBaseValue(Object value) {
		return toDataBaseValue(value, TypeDescriptor.forObject(value));
	}

	public Object toDataBaseValue(Object value, TypeDescriptor sourceType) {
		if (value == null) {
			return null;
		}

		SqlType sqlType = getSqlType(value.getClass());
		if (sqlType == null) {
			return value;
		}

		return getConversionService().convert(value, sourceType, TypeDescriptor.valueOf(sqlType.getType()));
	}

	private void appendObjectKeyByValue(StringBuilder appendable, Field field, Object value) {
		appendable.append(OBJECT_KEY_CONNECTOR);
		appendable.append(field.getGetter().getName());
		appendable.append(OBJECT_KEY_CONNECTOR);
		String str = String.valueOf(value);
		str = str.replaceAll(OBJECT_KEY_CONNECTOR, "\\" + OBJECT_KEY_CONNECTOR);
		appendable.append(str);
	}

	@Override
	public String getObjectKeyByIds(Class<?> clazz, Collection<Object> ids) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		Iterator<Field> primaryKeys = getPrimaryKeys(clazz).iterator();
		Iterator<Object> valueIterator = ids.iterator();
		while (primaryKeys.hasNext() && valueIterator.hasNext()) {
			appendObjectKeyByValue(sb, primaryKeys.next(), toDataBaseValue(valueIterator.next()));
		}
		return sb.toString();
	}

	public final <T> String getObjectKey(Class<? extends T> clazz, final T bean) {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		for (Field column : getPrimaryKeys(clazz)) {
			appendObjectKeyByValue(sb, column, getDataBaseValue(bean, column));
		}
		return sb.toString();
	}

	@Override
	public <K> Map<String, K> getInIdsKeyMap(Class<?> clazz, Collection<? extends K> lastPrimaryKeys,
			Object[] primaryKeys) {
		if (CollectionUtils.isEmpty(lastPrimaryKeys)) {
			return Collections.emptyMap();
		}

		Map<String, K> keyMap = new LinkedHashMap<String, K>();
		Iterator<? extends K> valueIterator = lastPrimaryKeys.iterator();

		while (valueIterator.hasNext()) {
			K k = valueIterator.next();
			Object[] ids;
			if (primaryKeys == null || primaryKeys.length == 0) {
				ids = new Object[] { k };
			} else {
				ids = new Object[primaryKeys.length];
				System.arraycopy(primaryKeys, 0, ids, 0, primaryKeys.length);
				ids[ids.length - 1] = k;
			}
			keyMap.put(getObjectKeyByIds(clazz, Arrays.asList(ids)), k);
		}
		return keyMap;
	}

	public String getEscapeCharacter() {
		return escapeCharacter;
	}

	public void setEscapeCharacter(String escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	public void keywordProcessing(StringBuilder sb, String column) {
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public void keywordProcessing(StringBuilder sb, String tableName, String column) {
		sb.append(getEscapeCharacter()).append(tableName).append(getEscapeCharacter());
		sb.append(POINT);
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public String getSqlName(String tableName, String column) {
		StringBuilder sb = new StringBuilder();
		keywordProcessing(sb, tableName, column);
		return sb.toString();
	}

	public String getCreateTablePrefix() {
		return "CREATE TABLE IF NOT EXISTS";
	}

	// --------------以下为标准实现-----------------

	@Override
	public Sql toSelectByIdsSql(TableStructure tableStructure, Object... ids) throws SqlDialectException {
		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (ids.length > primaryKeys.size()) {
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
			params[i++] = toDataBaseValue(value, TypeDescriptor.forObject(value));
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			if (iterator.hasNext() && valueIterator.hasNext()) {
				sb.append(AND);
			}
		}
		return new SimpleSql(sb.toString(), params);
	}
	
	@Override
	public <T> Sql save(TableStructure tableStructure, T entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement()) {
				continue;
			}

			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, column.getName());
			values.append("?");
			params.add(getDataBaseValue(entity, column.getField()));
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
	
	protected void appendCondition(StringBuilder sb, Collection<Object> params, TableStructure tableStructure, Object condition) {
		if(condition == null) {
			return ;
		}
		
		boolean first = true;
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			Object value = getDataBaseValue(condition, column.getField());
			if(value == null && !column.isNullable()) {
				continue;
			}
			
			if(first) {
				//TODO 这样写是否不利于复用
				int index = sb.indexOf(WHERE);
				if(index == -1) {
					sb.append(WHERE);
				}else {
					sb.append(AND);
				}
				first = false;
			}else {
				sb.append(AND);
			}
			
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			params.add(value);
		}
	}

	@Override
	public <T> Sql delete(TableStructure tableStructure, T entity, T condition) throws SqlDialectException {
		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableStructure.getName());
		sql.append(WHERE);
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sql, column.getName());
			sql.append("=?");
			params.add(getDataBaseValue(entity, column.getField()));
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}

		// 添加版本号字段变更条件
		tableStructure.getNotPrimaryKeys().forEach((column) -> {
			if (column.getField().isAnnotationPresent(Version.class)) {
				// 因为一定存在主键，所有一定有where条件，此处直接and
				sql.append(AND);
				keywordProcessing(sql, column.getName());
				sql.append("=?");
				params.add(getDataBaseValue(entity, column.getField()));
			}
		});
		
		appendCondition(sql, params, tableStructure, condition);
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public Sql deleteById(TableStructure tableStructure, Object... ids) throws SqlDialectException {
		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (primaryKeys.size() != ids.length) {
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
			params[i] = toDataBaseValue(ids[i]);
			i++;
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}
		return new SimpleSql(sql.toString(), params);
	}

	@Override
	public Sql getInIds(TableStructure tableStructure, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException {
		if (CollectionUtils.isEmpty(inPrimaryKeys)) {
			throw new SqlDialectException("in 语句至少要有一个in条件");
		}

		List<Column> primaryKeyColumns = tableStructure.getPrimaryKeys();
		int whereSize = ArrayUtils.isEmpty(primaryKeys) ? 0 : primaryKeys.length;
		if (whereSize > primaryKeyColumns.size()) {
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
				params.add(toDataBaseValue(primaryKeys[i]));
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
				params.add(toDataBaseValue(valueIterator.next()));
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

	@Override
	public Sql toMaxIdSql(TableStructure tableStructure, Field field) throws SqlDialectException {
		Column column = tableStructure.find(field);
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

	@Override
	public <T> Sql update(TableStructure tableStructure, T entity, T condition) throws SqlDialectException {
		List<Column> primaryKeyColumns = tableStructure.getPrimaryKeys();
		if (primaryKeyColumns.size() == 0) {
			throw new SqlDialectException(tableStructure.getName() + " not found primary key");
		}

		Map<String, Object> changeMap = getChangeMap(entity);
		List<Column> notPrimaryKeys = tableStructure.getNotPrimaryKeys();
		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		sb.append(SET);
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (changeMap != null && !changeMap.containsKey(column.getField().getSetter().getName())) {
				// 忽略没有变化的字段
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
			params.add(getDataBaseValue(entity, column.getField()));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		// 添加版本号字段变更条件
		notPrimaryKeys.forEach((column) -> {
			Counter counter = getCounter(column.getField());
			if (counter != null) {
				sb.append(AND);
				keywordProcessing(sb, column.getName());
				sb.append(">=").append(counter.min());
				sb.append(AND);
				keywordProcessing(sb, column.getName());
				sb.append("<=").append(counter.max());
			}

			if (column.getField().isAnnotationPresent(Version.class)) {
				AnyValue oldVersion = null;
				if (changeMap != null && changeMap.containsKey(column.getField().getSetter().getName())) {
					oldVersion = new AnyValue(changeMap.get(column.getField().getSetter().getName()));
					if (oldVersion.getAsDoubleValue() == 0) {
						// 如果存在变更但版本号为0就忽略此条件
						return;
					}
				}

				// 因为一定存在主键，所有一定有where条件，此处直接and
				sb.append(AND);
				keywordProcessing(sb, column.getName());
				sb.append("=?");

				// 如果存在旧值就使用旧值
				params.add(oldVersion == null ? getDataBaseValue(entity, column.getField())
						: toDataBaseValue(oldVersion.getAsLongValue()));
			}
		});
		
		appendCondition(sb, params, tableStructure, condition);
		return new SimpleSql(sb.toString(), params.toArray());
	}

	protected final void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			Map<String, Object> changeMap) {
		AnyValue newValue = new AnyValue(getDataBaseValue(entity, column.getField()));
		AnyValue oldValue = new AnyValue(
				changeMap == null ? null : changeMap.get(column.getField().getSetter().getName()));
		appendUpdateValue(sb, params, entity, column, oldValue, newValue);
	}

	protected void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			AnyValue oldValue, AnyValue newValue) {
		Counter counter = getCounter(column.getField());
		if (counter != null) {
			appendCounterValue(sb, params, entity, column, oldValue, newValue, counter);
		} else if (column.getField().isAnnotationPresent(Version.class)) {
			keywordProcessing(sb, column.getName());
			sb.append("+");
			sb.append(newValue.getAsDoubleValue() - oldValue.getAsByteValue());
		} else {
			sb.append("?");
			params.add(newValue.getValue());
		}
	}
	
	@Override
	public Sql query(TableStructure tableStructure, Object query) {
		StringBuilder sb = new StringBuilder(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		
		Sql where = getConditionalStatement(tableStructure, query);
		if(StringUtils.isEmpty(where.getSql())){
			return new SimpleSql(sb.toString());
		}
		
		return new SimpleSql(sb.append(" where ").append(where.getSql()).toString(), where.getParams());
	}

	protected void appendCounterValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			AnyValue oldValue, AnyValue newValue, Counter counter) {
		throw new SqlDialectException("This counter field cannot be processed: " + column);
	}
	
	private void and(StringBuilder sb, List<Object> params, Object entity, Iterator<Column> columns){
		while(columns.hasNext()){
			Column column = columns.next();
			Object value = column.getField().get(entity);
			if(value == null){
				continue;
			}
			
			if(sb.length() != 0){
				sb.append(" and ");
			}
			keywordProcessing(sb, column.getName());
			sb.append(" = ?");
			params.add(value);
		}
	}
	
	@Override
	public <T> Sql toSaveOrUpdateSql(TableStructure tableStructure, T entity) throws SqlDialectException {
		return saveOrUpdate(save(tableStructure, entity), update(tableStructure, entity, null));
	}
	
	private Sql getConditionalStatement(TableStructure tableStructure, Object entity){
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<Object>(8);
		and(sb, params, entity, tableStructure.stream().filter((col) -> tableStructure.indexExists(col)).iterator());
		and(sb, params, entity, tableStructure.stream().filter((col) -> !tableStructure.indexExists(col)).iterator());
		return new SimpleSql(sb.toString(), params.toArray());
	}
	
	
}
