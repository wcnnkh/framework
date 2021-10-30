package io.basc.framework.sql.orm.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.basc.framework.aop.support.FieldSetterListen;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.ParameterException;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.sql.EditableSql;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlUtils;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.sql.orm.SqlDialectException;
import io.basc.framework.sql.orm.SqlType;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.util.Accept;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

/**
 * 标准的sql方言
 * 
 * @author shuchaowen
 *
 */
public abstract class StandardSqlDialect extends DefaultTableMapping implements SqlDialect {
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
	public Sql toInsertSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.columns().iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement() && !MapperUtils.isExistValue(column.getField(), entity)) {
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

	@Override
	public Sql toDeleteSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (primaryKeys.size() == 0) {
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
			params.add(getDataBaseValue(entity, column.getField()));
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
				params.add(getDataBaseValue(entity, column.getField()));
			}
		});
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public Sql toDeleteByIdSql(TableStructure tableStructure, Object... ids) throws SqlDialectException {
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
	
	protected Sql toUpdateSql(TableStructure tableStructure, Object entity, Map<String, Object> changeMap, Accept<Column> accept) throws SqlDialectException{
		List<Column> primaryKeyColumns = tableStructure.getPrimaryKeys();
		if (primaryKeyColumns.size() == 0) {
			throw new SqlDialectException(tableStructure.getName() + " not found primary key");
		}

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
			
			if(!accept.accept(column)){
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
			if(!accept.accept(column)){
				return ;
			}
			
			appendExtendWhere(column, sb, params, changeMap, entity);
		});
		return new SimpleSql(sb.toString(), params.toArray());
	}

	protected void appendExtendWhere(Column column, StringBuilder sb, Collection<Object> params, Map<String, Object> changeMap, Object entity){
		Collection<Range<Double>> numberRanges = column.getNumberRanges();
		if(!CollectionUtils.isEmpty(numberRanges)) {
			for(Range<Double> range : numberRanges) {
				if(range.getLowerBound().getValue().isPresent()) {
					sb.append(AND);
					keywordProcessing(sb, column.getName());
					if(column.isIncrement()){
						AnyValue newValue = new AnyValue(getDataBaseValue(entity, column.getField()));
						AnyValue oldValue = changeMap == null ? null
								: new AnyValue(changeMap.get(column.getField().getSetter().getName()));
						if(oldValue != null) {
							sb.append("+");
							sb.append(newValue.getAsDoubleValue() - oldValue.getAsByteValue());
						}
					}
					sb.append(">");
					if(range.getLowerBound().isInclusive()) {
						sb.append("=");
					}
					sb.append(range.getLowerBound().getValue());
				}
				
				if(range.getUpperBound().getValue().isPresent()) {
					sb.append(AND);
					keywordProcessing(sb, column.getName());
					if(column.isIncrement()){
						AnyValue newValue = new AnyValue(getDataBaseValue(entity, column.getField()));
						AnyValue oldValue = changeMap == null ? null
								: new AnyValue(changeMap.get(column.getField().getSetter().getName()));
						if(oldValue != null) {
							sb.append("+");
							sb.append(newValue.getAsDoubleValue() - oldValue.getAsByteValue());
						}
					}
					sb.append("<");
					if(range.getUpperBound().isInclusive()) {
						sb.append("=");
					}
					sb.append(range.getUpperBound().getValue());
				}
			}
		}
		
		if(column.isVersion()) {
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
	}
	
	@Override
	public Sql toUpdatePartSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		Map<String, Object> changeMap = getChangeMap(entity);
		return toUpdateSql(tableStructure, entity, changeMap, (column) -> {
			if (changeMap != null && !changeMap.containsKey(column.getField().getSetter().getName())) {
				return false;
			}

			// 如果字段不能为空，且实体字段没有值就忽略
			if (!column.isNullable() && !MapperUtils.isExistDefaultValue(column.getField(), entity)) {
				return false;
			}
			return true;
		});
	}
	
	@Override
	public Sql toUpdateSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		Map<String, Object> changeMap = getChangeMap(entity);
		return toUpdateSql(tableStructure, entity, changeMap, (column) -> true);
	}
	
	@Override
	public <T> Sql toUpdateSql(TableStructure tableStructure, T entity,
			T condition) throws SqlDialectException {
		Map<String, Object> changeMap = new HashMap<String, Object>();
		tableStructure.columns().forEach((column) -> {
			Object value = column.getField().get(condition);
			if(value == null && column.isNullable()){
				return ;
			}
			
			changeMap.put(column.getField().getSetter().getName(), value);
		});
		return toUpdateSql(tableStructure, entity, changeMap, (col) -> true);
	}

	protected final void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			Map<String, Object> changeMap) {
		AnyValue newValue = new AnyValue(getDataBaseValue(entity, column.getField()));
		AnyValue oldValue = changeMap == null ? null
				: new AnyValue(changeMap.get(column.getField().getSetter().getName()));
		appendUpdateValue(sb, params, entity, column, oldValue, newValue);
	}

	protected void appendUpdateValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			@Nullable Value oldValue, Value newValue) {
		if(column.isIncrement() && oldValue != null) {
			keywordProcessing(sb, column.getName());
			sb.append("+");
			sb.append(newValue.getAsDoubleValue() - oldValue.getAsByteValue());
		} else {
			sb.append("?");
			params.add(newValue.getSourceValue());
		}
	}

	@Override
	public Sql query(TableStructure tableStructure, Object query) {
		StringBuilder sb = new StringBuilder(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableStructure.getName());

		Sql where = getConditionalStatement(tableStructure, query);
		if (StringUtils.isEmpty(where.getSql())) {
			return new SimpleSql(sb.toString());
		}

		return new SimpleSql(sb.append(" where ").append(where.getSql()).toString(), where.getParams());
	}

	private void and(StringBuilder sb, List<Object> params, Object entity, Iterator<Column> columns) {
		while (columns.hasNext()) {
			Column column = columns.next();
			Object value = column.getField().get(entity);
			if (value == null) {
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

	private Sql getConditionalStatement(TableStructure tableStructure, Object entity) {
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<Object>(8);
		and(sb, params, entity, tableStructure.columns().filter((col) -> col.hasIndex()).iterator());
		and(sb, params, entity, tableStructure.columns().filter((col) -> col.hasIndex()).iterator());
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public Sql toCountSql(Sql sql) throws SqlDialectException {
		String str = sql.getSql();
		str = str.toLowerCase();
		if (str.lastIndexOf(" group by ") != -1) {
			// 如果存在group by语句
			EditableSql countSql = new EditableSql();
			countSql.append("select count(*) from (");
			countSql.append(sql);
			countSql.append(") as basc_" + XUtils.getUUID());
			return countSql;
		}

		int fromIndex = str.indexOf(" from ");// ignore select
		if (fromIndex == -1) {
			throw new IndexOutOfBoundsException(str);
		}

		EditableSql countSql = new EditableSql();
		countSql.append("select count(*)");
		int orderIndex = str.lastIndexOf(" order by ");
		if (orderIndex != -1 && str.indexOf(")", orderIndex) == -1) {
			countSql.append(SqlUtils.sub(sql, fromIndex, orderIndex));
		} else {
			// 不存在 order by 子语句
			countSql.append(SqlUtils.sub(sql, fromIndex));
		}
		return countSql;
	}
}
