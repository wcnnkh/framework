package scw.orm.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.annotation.AnnotatedElementUtils;
import scw.core.utils.CollectionUtils;
import scw.env.Sys;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.orm.DefaultObjectRelationalMapping;

public abstract class AbstractSqlDialect extends DefaultObjectRelationalMapping implements SqlDialect {
	protected static final String UPDATE_PREFIX = "update ";
	protected static final String DELETE_PREFIX = "delete from ";
	protected static final String SELECT_ALL_PREFIX = "select * from ";
	protected static final String INSERT_INTO_PREFIX = "insert into ";
	protected static final String VALUES = ") values(";

	protected static final String SET = " set ";
	protected static final String WHERE = " where ";
	protected static final String AND = " and ";
	protected static final String OR = " or ";

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
}
