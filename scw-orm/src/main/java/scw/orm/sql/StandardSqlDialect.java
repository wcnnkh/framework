package scw.orm.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.aop.support.FieldSetterListen;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.annotation.AnnotatedElementUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.env.Sys;
import scw.lang.Nullable;
import scw.lang.ParameterException;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.orm.DefaultObjectRelationalMapping;
import scw.orm.annotation.Version;
import scw.orm.sql.annotation.Counter;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.value.AnyValue;

/**
 * 标准的sql方言
 * 
 * @author shuchaowen
 *
 */
public abstract class StandardSqlDialect extends DefaultObjectRelationalMapping implements SqlDialect {
	protected static final String UPDATE_PREFIX = "update ";
	protected static final String DELETE_PREFIX = "delete from ";
	protected static final String SELECT_ALL_PREFIX = "select * from ";
	protected static final String INSERT_INTO_PREFIX = "insert into ";
	protected static final String VALUES = ") values(";

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

	@Override
	public Fields getFields(Class<?> clazz, boolean useSuperClass, Field parentField) {
		return super.getFields(clazz, useSuperClass, parentField).accept(FieldFeature.SUPPORT_GETTER)
				.accept(FieldFeature.SUPPORT_SETTER).accept(FieldFeature.EXISTING_GETTER_FIELD);
	}

	public Object getDataBaseValue(Object entity, Field field) {
		return toDataBaseValue(field.getGetter().get(entity), new TypeDescriptor(field.getGetter()));
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
	
	public Counter getCounter(Field field) {
		return field.getAnnotation(Counter.class);
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

	public void appendFieldName(StringBuilder sb, FieldDescriptor fieldDescriptor) {
		keywordProcessing(sb, getName(fieldDescriptor));
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

	public Map<IndexInfo, List<IndexInfo>> getIndexInfoMap(Class<?> entityClass) {
		Map<IndexInfo, List<IndexInfo>> indexMap = new LinkedHashMap<IndexInfo, List<IndexInfo>>();
		for (Field column : getFields(entityClass)) {
			scw.orm.sql.annotation.Index index = AnnotatedElementUtils.getMergedAnnotation(column,
					scw.orm.sql.annotation.Index.class);
			if (index == null) {
				continue;
			}

			IndexInfo indexInfo = new IndexInfo(column, index.name(), index.type(), index.length(), index.method(),
					index.order());
			List<IndexInfo> list = indexMap.get(indexInfo);
			if (list == null) {
				list = new ArrayList<IndexInfo>();
				indexMap.put(indexInfo, list);
			}
			list.add(indexInfo);
		}
		return indexMap;
	}

	@Override
	public String getComment(Field field) {
		String desc = getDescription(field.getGetter());
		if (desc == null) {
			desc = getDescription(field.getSetter());
		}
		return desc;
	}

	@Override
	public String getCharsetName(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.getCharsetName(fieldDescriptor, null);
	}
	
	//--------------以下为标准实现-----------------
	
	@Override
	public Sql toSelectByIdsSql(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKeys(entityClass).shared();
		if (ids.length > primaryKeys.size()) {
			throw new SqlDialectException("Wrong number of primary key parameters");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);
		Iterator<Field> iterator = primaryKeys.iterator();
		Iterator<Object> valueIterator = Arrays.asList(ids).iterator();
		if (iterator.hasNext() && valueIterator.hasNext()) {
			sb.append(WHERE);
		}

		Object[] params = new Object[ids.length];
		int i = 0;
		while (iterator.hasNext() && valueIterator.hasNext()) {
			Field column = iterator.next();
			Object value = valueIterator.next();
			params[i++] = toDataBaseValue(value, TypeDescriptor.forObject(value));
			appendFieldName(sb, column.getGetter());
			sb.append("=?");
			if (iterator.hasNext() && valueIterator.hasNext()) {
				sb.append(AND);
			}
		}
		return new SimpleSql(sb.toString(), params);
	}
	
	@Override
	public <T> Sql save(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Field> iterator = getFields(entityClass).iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			if (isAutoIncrement(column.getSetter())) {
				continue;
			}

			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			appendFieldName(cols, column.getGetter());
			values.append("?");
			params.add(getDataBaseValue(entity, column));
		}
		sql.append(INSERT_INTO_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append("(");
		sql.append(cols);
		sql.append(VALUES);
		sql.append(values);
		sql.append(")");
		return new SimpleSql(sql.toString(), params.toArray());
	}
	
	@Override
	public <T> Sql delete(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKeys(entityClass).shared();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		List<Object> params = new ArrayList<Object>(primaryKeys.size());
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);
		Iterator<Field> iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sql, column.getGetter());
			sql.append("=?");
			params.add(getDataBaseValue(entity, column));
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}

		// 添加版本号字段变更条件
		for (Field column : getNotPrimaryKeys(entityClass)) {
			if (column.isAnnotationPresent(Version.class)) {
				// 因为一定存在主键，所有一定有where条件，此处直接and
				sql.append(AND);
				appendFieldName(sql, column.getGetter());
				sql.append("=?");
				params.add(getDataBaseValue(entity, column));
			}
		}
		return new SimpleSql(sql.toString(), params.toArray());
	}
	
	@Override
	public Sql deleteById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKeys(entityClass);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (primaryKeys.size() != ids.length) {
			throw new ParameterException("主键数量不一致:" + tableName);
		}

		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);

		int i = 0;
		Object[] params = new Object[ids.length];
		Iterator<Field> iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sql, column.getGetter());
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
	public Sql getInIds(String tableName, Class<?> entityClass, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException {
		if (CollectionUtils.isEmpty(inPrimaryKeys)) {
			throw new SqlDialectException("in 语句至少要有一个in条件");
		}

		Fields primaryKeyColumns = getPrimaryKeys(entityClass);
		int whereSize = ArrayUtils.isEmpty(primaryKeys) ? 0 : primaryKeys.length;
		if (whereSize > primaryKeyColumns.size()) {
			throw new NullPointerException("primaryKeys length  greater than primary key lenght");
		}

		List<Object> params = new ArrayList<Object>(inPrimaryKeys.size() + whereSize);
		StringBuilder sb = new StringBuilder();
		Iterator<Field> iterator = primaryKeyColumns.iterator();
		if (whereSize > 0) {
			for (int i = 0; i < whereSize && iterator.hasNext(); i++) {
				if (sb.length() != 0) {
					sb.append(AND);
				}

				Field column = iterator.next();
				appendFieldName(sb, column.getGetter());
				sb.append("=?");
				params.add(toDataBaseValue(primaryKeys[i]));
			}
		}

		if (iterator.hasNext()) {
			if (sb.length() != 0) {
				sb.append(AND);
			}

			appendFieldName(sb, iterator.next().getGetter());
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
		keywordProcessing(sb, tableName);
		sb.append(WHERE).append(where);
		return new SimpleSql(sb.toString(), params.toArray());
	}
	
	@Override
	public Sql toMaxIdSql(Class<?> clazz, String tableName, Field field) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		appendFieldName(sb, field.getGetter());
		sb.append(" from ");
		keywordProcessing(sb, tableName);
		sb.append(" order by ");
		appendFieldName(sb, field.getGetter());
		sb.append(" desc");
		return new SimpleSql(sb.toString());
	}
	
	@Nullable
	protected Map<String, Object> getChangeMap(Object entity) throws SqlDialectException{
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
	public <T> Sql update(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKeys(entityClass).shared();
		if (primaryKeys.size() == 0) {
			throw new SqlDialectException(tableName + " not found primary key");
		}

		Map<String, Object> changeMap = null;
		if (entity instanceof FieldSetterListen) {
			changeMap = ((FieldSetterListen) entity)._getFieldSetterMap();
			if (CollectionUtils.isEmpty(changeMap)) {
				throw new SqlDialectException("not change properties");
			}
		}

		Fields notPrimaryKeys = getNotPrimaryKeys(entityClass).shared();
		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);
		List<Object> params = new ArrayList<Object>(notPrimaryKeys.size());
		Iterator<Field> iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			if (changeMap != null && !changeMap.containsKey(column.getSetter().getName())) {
				// 忽略没有变化的字段
				continue;
			}

			appendFieldName(sb, column.getGetter());
			sb.append("=");
			AnyValue newValue = new AnyValue(getDataBaseValue(entity, column));
			AnyValue oldValue = new AnyValue(changeMap == null? null:changeMap.get(column.getSetter().getName()));
			appendUpdateValue(sb, params, entity, column, oldValue, newValue);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append(WHERE);
		iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sb, column.getGetter());
			sb.append("=?");
			params.add(getDataBaseValue(entity, column));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		// 添加版本号字段变更条件
		for (Field column : notPrimaryKeys) {
			Counter counter = getCounter(column);
			if(counter != null) {
				sb.append(AND);
				appendFieldName(sb, column.getGetter());
				sb.append(">=").append(counter.min());
				sb.append(AND);
				appendFieldName(sb, column.getGetter());
				sb.append("<=").append(counter.max());
			}
			
			if (column.isAnnotationPresent(Version.class)) {
				AnyValue oldVersion = null;
				if(changeMap != null && changeMap.containsKey(column.getSetter().getName())) {
					oldVersion = new AnyValue(changeMap.get(column.getSetter().getName()));
					if(oldVersion.getAsDoubleValue() == 0) {
						//如果存在变更但版本号为0就忽略此条件
						continue;
					}
				}
				
				// 因为一定存在主键，所有一定有where条件，此处直接and
				sb.append(AND);
				appendFieldName(sb, column.getGetter());
				sb.append("=?");
				
				//如果存在旧值就使用旧值
				params.add(oldVersion == null? getDataBaseValue(entity, column): toDataBaseValue(oldVersion.getAsLongValue()));
			}
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}
	
	protected final void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Field column, Map<String, Object> changeMap) {
		AnyValue newValue = new AnyValue(getDataBaseValue(entity, column));
		AnyValue oldValue = new AnyValue(changeMap == null? null:changeMap.get(column.getSetter().getName()));
		appendUpdateValue(sb, params, entity, column, oldValue, newValue);
	}
	
	protected void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Field column, AnyValue oldValue, AnyValue newValue) {
		Counter counter = getCounter(column);
		if(counter != null) {
			appendCounterValue(sb, params, entity, column, oldValue, newValue, counter);
		} else if(column.isAnnotationPresent(Version.class)) {
			appendFieldName(sb, column.getGetter());
			sb.append("+");
			sb.append(newValue.getAsDoubleValue() - oldValue.getAsByteValue());
		}else {
			sb.append("?");
			params.add(newValue.getValue());
		}
	}
	
	protected void appendCounterValue(StringBuilder sb, List<Object> params, Object entity, Field column, AnyValue oldValue, AnyValue newValue, Counter counter) {
		throw new SqlDialectException("This counter field cannot be processed: " + column);
	}
}
