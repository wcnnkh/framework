package scw.sql.orm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.json.JSONUtils;
import scw.lang.Ignore;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFilter;
import scw.mapper.FilterFeature;
import scw.mapper.Mapper;
import scw.mapper.MapperUtils;
import scw.sql.SqlUtils;
import scw.sql.orm.annotation.AutoIncrement;
import scw.sql.orm.annotation.Column;
import scw.sql.orm.annotation.Index;
import scw.sql.orm.annotation.NotColumn;
import scw.sql.orm.annotation.PrimaryKey;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.enums.CasType;
import scw.util.EnumUtils;

/**
 * 默认的orm定义
 * 
 * @author shuchaowen
 *
 */
@Configuration(order = Integer.MIN_VALUE)
public class ObjectRelationalMapping {
	/**
	 * 默认对象主键的连接符
	 */
	public static final char PRIMARY_KEY_CONNECTOR_CHARACTER = StringUtils
			.parseChar(GlobalPropertyFactory.getInstance().getString("orm.primary.key.connector.character"), ':');

	public Mapper getMapper() {
		return MapperUtils.getMapper();
	}

	public boolean isEntity(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.getType().getAnnotation(Table.class) != null;
	}

	public boolean isPrimaryKey(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.getAnnotatedElement().getAnnotation(PrimaryKey.class) != null;
	}

	public boolean isIgnore(FieldDescriptor fieldDescriptor) {
		Ignore ignore = fieldDescriptor.getAnnotatedElement().getAnnotation(Ignore.class);
		if (ignore != null) {
			return true;
		}

		NotColumn exclude = fieldDescriptor.getAnnotatedElement().getAnnotation(NotColumn.class);
		if (exclude != null) {
			return true;
		}
		return false;
	}

	public String getDisplayName(FieldDescriptor fieldDescriptor) {
		Column column = fieldDescriptor.getAnnotatedElement().getAnnotation(Column.class);
		if (column != null && !StringUtils.isEmpty(column.name())) {
			return column.name();
		}
		return fieldDescriptor.getName();
	}

	public void set(Field field, Object instance, Object value) {
		if (value == null) {
			return;
		}

		Class<?> type = field.getSetter().getType();
		if (type.isInstance(value)) {
			field.getSetter().set(instance, value);
			return;
		}

		if (TypeUtils.isBoolean(type)) {
			if (value != null) {
				if (value instanceof Number) {
					field.getSetter().set(instance, ((Number) value).intValue() == 1);
				} else {
					field.getSetter().set(instance, StringUtils.parseBoolean(value.toString()));
				}
			}
		} else if (TypeUtils.isInt(type)) {
			if (value instanceof Number) {
				field.getSetter().set(instance, ((Number) value).intValue());
			} else {
				field.getSetter().set(instance, StringUtils.parseInt(value.toString()));
			}
		} else if (TypeUtils.isLong(type)) {
			if (value instanceof Number) {
				field.getSetter().set(instance, ((Number) value).longValue());
			} else {
				field.getSetter().set(instance, StringUtils.parseLong(value.toString()));
			}
		} else if (TypeUtils.isByte(type)) {
			if (value instanceof Number) {
				field.getSetter().set(instance, ((Number) value).byteValue());
			} else {
				field.getSetter().set(instance, StringUtils.parseByte(value.toString()));
			}
		} else if (TypeUtils.isFloat(type)) {
			if (value instanceof Number) {
				field.getSetter().set(instance, ((Number) value).floatValue());
			} else {
				field.getSetter().set(instance, StringUtils.parseFloat(value.toString()));
			}
		} else if (TypeUtils.isDouble(type)) {
			if (value instanceof Number) {
				field.getSetter().set(instance, ((Number) value).doubleValue());
			} else {
				field.getSetter().set(instance, StringUtils.parseDouble(value.toString()));
			}
		} else if (TypeUtils.isShort(type)) {
			if (value instanceof Number) {
				field.getSetter().set(instance, ((Number) value).shortValue());
			} else {
				field.getSetter().set(instance, StringUtils.parseShort(value.toString()));
			}
		} else if (type.isEnum()) {
			field.getSetter().set(instance, EnumUtils.valueOf(type, value.toString()));
		} else {
			Object obj = JSONUtils.parseObject(value.toString(), field.getSetter().getGenericType());
			if (obj == null) {
				return;
			}
			field.getSetter().set(instance, obj);
		}
	}

	public Object get(Field field, Object instance) {
		Object value = field.getGetter().get(instance);
		Class<?> type = field.getGetter().getType();
		if (type.isEnum()) {
			return value == null ? null : value.toString();
		}

		if (boolean.class == type) {
			boolean b = value == null ? false : (Boolean) value;
			return b ? 1 : 0;
		}

		if (Boolean.class == type) {
			if (value == null) {
				return null;
			}
			return (Boolean) value ? 1 : 0;
		}

		if (SqlUtils.isDataBaseType(type)) {
			return value;
		} else {
			if (value == null) {
				return null;
			}

			return JSONUtils.toJSONString(value);
		}
	}

	protected void appendObjectKeyByValue(StringBuilder appendable, Object value) {
		appendable.append(PRIMARY_KEY_CONNECTOR_CHARACTER);
		appendable.append(StringUtils.transferredMeaning(value == null ? null : value.toString(),
				PRIMARY_KEY_CONNECTOR_CHARACTER));
	}

	public Collection<Field> getPrimaryKeys(Class<?> entityClass) {
		return getMapper().getFields(entityClass, null, new FieldFilter() {

			public boolean accept(Field field) {
				return field.isSupportGetter() && isPrimaryKey(field.getGetter()) && !isIgnore(field.getGetter());
			}
		}, FilterFeature.IGNORE_STATIC);
	}

	public Collection<Field> getNotPrimaryKeys(Class<?> entityClass) {
		return getMapper().getFields(entityClass, null, new FieldFilter() {

			public boolean accept(Field field) {
				return field.isSupportGetter() && !isPrimaryKey(field.getGetter()) && !isIgnore(field.getGetter())
						&& !isEntity(field.getGetter());
			}
		}, FilterFeature.IGNORE_STATIC);
	}

	public <T> String getObjectKey(Class<? extends T> clazz, final T bean) {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		for (Field field : getPrimaryKeys(clazz)) {
			appendObjectKeyByValue(sb, get(field, bean));
		}
		return sb.toString();
	}

	public String getObjectKeyById(Class<?> clazz, Collection<Object> primaryKeys) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		Iterator<Field> iterator = getPrimaryKeys(clazz).iterator();
		Iterator<Object> valueIterator = primaryKeys.iterator();
		while (iterator.hasNext() && valueIterator.hasNext()) {
			appendObjectKeyByValue(sb, get(iterator.next(), valueIterator.next()));
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public <K> Map<String, K> getInIdKeyMap(Class<?> clazz, Collection<? extends K> lastPrimaryKeys,
			Object[] primaryKeys) {
		if (CollectionUtils.isEmpty(lastPrimaryKeys)) {
			return Collections.EMPTY_MAP;
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
			keyMap.put(getObjectKeyById(clazz, Arrays.asList(ids)), k);
		}
		return keyMap;
	}

	protected boolean findField(FieldDescriptor fieldDescriptor, String name, Class<?> type) {
		return (type == null || type == fieldDescriptor.getType())
				&& (name.equals(fieldDescriptor.getName()) || name.equals(getDisplayName(fieldDescriptor)));
	}

	public Field getField(Class<?> entityClass, final String name, final Class<?> type) {
		return getMapper().getField(entityClass, new FieldFilter() {
			public boolean accept(Field field) {
				return field.isSupportGetter() && field.isSupportSetter()
						&& (findField(field.getGetter(), name, type) || findField(field.getSetter(), name, type));
			}
		}, FilterFeature.IGNORE_STATIC);
	}

	public boolean isTable(Class<?> clazz) {
		return clazz.getAnnotation(Table.class) != null;
	}

	public boolean isIndexColumn(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.getAnnotatedElement().getAnnotation(Index.class) != null;
	}

	public boolean isNullable(FieldDescriptor fieldDescriptor) {
		if (fieldDescriptor.getType().isPrimitive()) {
			return false;
		}

		if (isPrimaryKey(fieldDescriptor)) {
			return false;
		}

		if (isIndexColumn(fieldDescriptor)) {
			return false;
		}

		Column column = fieldDescriptor.getAnnotatedElement().getAnnotation(Column.class);
		return column == null ? true : column.nullAble();
	}

	public boolean isAutoIncrement(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.getAnnotatedElement().getAnnotation(AutoIncrement.class) != null;
	}

	public String getCharsetName(FieldDescriptor fieldDescriptor) {
		Column column = fieldDescriptor.getAnnotatedElement().getAnnotation(Column.class);
		return column == null ? null : column.charsetName().trim();
	}

	public boolean isUnique(FieldDescriptor fieldDescriptor) {
		Column column = fieldDescriptor.getAnnotatedElement().getAnnotation(Column.class);
		return column == null ? false : column.unique();
	}

	public CasType getCasType(FieldDescriptor fieldDescriptor) {
		if (isPrimaryKey(fieldDescriptor)) {
			return CasType.NOTHING;
		}

		Column column = fieldDescriptor.getAnnotatedElement().getAnnotation(Column.class);
		if (column == null) {
			return CasType.NOTHING;
		}
		return column.casType();
	}
}
