package scw.orm.sql;

import java.io.Reader;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import scw.core.exception.NotSupportException;
import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.FieldSetterListenUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.IteratorMapping;
import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.annotation.AutoIncrement;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.annotation.NotColumn;
import scw.orm.sql.annotation.PrimaryKey;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.annotation.Transient;
import scw.orm.sql.dialect.DefaultSqlType;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.dialect.SqlType;
import scw.orm.sql.dialect.SqlTypeFactory;
import scw.orm.sql.enums.CasType;
import scw.orm.sql.enums.OperationType;
import scw.sql.Sql;

public final class SqlORMUtils {
	private static Logger logger = LoggerUtils.getLogger(SqlORMUtils.class);

	private SqlORMUtils() {
	};

	public static boolean isPrimaryKey(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(PrimaryKey.class) != null;
	}

	public static boolean isIndexColumn(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(Index.class) != null;
	}

	public static boolean isNullAble(FieldDefinition fieldDefinition) {
		if (fieldDefinition.getField().getType().isPrimitive() || isPrimaryKey(fieldDefinition)
				|| isIndexColumn(fieldDefinition)) {
			return false;
		}

		Column column = fieldDefinition.getAnnotation(Column.class);
		return column == null ? true : column.nullAble();
	}

	public static boolean isDataBaseField(FieldDefinition fieldDefinition) {
		Column column = fieldDefinition.getAnnotation(Column.class);
		if (column != null) {
			return true;
		}

		Class<?> type = fieldDefinition.getField().getType();
		if (Class.class.isAssignableFrom(type) || type.isEnum() || type.isArray() || Map.class.isAssignableFrom(type)
				|| Collection.class.isAssignableFrom(type)) {
			return true;
		}

		return isDataBaseType(type);
	}

	public static boolean isDataBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type) || java.util.Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type);
	}

	public static boolean ignoreField(FieldDefinition field) {
		if (AnnotationUtils.isDeprecated(field)) {
			return true;
		}

		NotColumn exclude = field.getAnnotation(NotColumn.class);
		if (exclude != null) {
			return true;
		}

		Transient tr = field.getAnnotation(Transient.class);
		if (tr != null) {
			return true;
		}

		if (Modifier.isStatic(field.getField().getModifiers())
				|| Modifier.isTransient(field.getField().getModifiers())) {
			return true;
		}
		return false;
	}

	public static String getCharsetName(FieldDefinition fieldDefinition) {
		Column column = fieldDefinition.getAnnotation(Column.class);
		return column == null ? null : column.charsetName().trim();
	}

	public static boolean isAutoIncrement(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(AutoIncrement.class) != null;
	}

	public static SqlType getSqlType(FieldDefinition fieldDefinition, SqlTypeFactory sqlTypeFactory) {
		String type = null;
		Column column = fieldDefinition.getAnnotation(Column.class);
		if (column != null) {
			type = column.type();
		}

		SqlType tempSqlType = StringUtils.isEmpty(type)
				? sqlTypeFactory.getSqlType(fieldDefinition.getField().getType()) : sqlTypeFactory.getSqlType(type);
		type = tempSqlType.getName();

		int len = -1;
		if (column != null) {
			len = column.length();
		}
		if (len <= 0) {
			len = tempSqlType.getLength();
		}
		return new DefaultSqlType(type, len);
	}

	public static boolean isUnique(FieldDefinition fieldDefinition) {
		Column column = fieldDefinition.getAnnotation(Column.class);
		return column == null ? false : column.unique();
	}

	public static CasType getCasType(FieldDefinition fieldDefinition) {
		if (isPrimaryKey(fieldDefinition)) {
			return CasType.NOTHING;
		}

		Column column = fieldDefinition.getAnnotation(Column.class);
		if (column == null) {
			return CasType.NOTHING;
		}
		return column.casType();
	}

	/**
	 * 获取主键数据
	 * 
	 * @param bean
	 * @param tableInfo
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> LinkedList<Object> getPrimaryKeys(MappingOperations mappingOperations, Class<? extends T> clazz,
			final T bean) throws Exception {
		final LinkedList<Object> keys = new LinkedList<Object>();
		mappingOperations.iterator(null, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				if (isPrimaryKey(context.getFieldDefinition())) {
					keys.add(mappingOperations.getter(context, bean));
				}
			}
		});
		return keys;
	}

	public static Sql toSql(OperationType operationType, SqlMappingOperations sqlMappingOperations,
			SqlDialect sqlDialect, Class<?> clazz, Object bean, String tableName) {
		switch (operationType) {
		case SAVE:
			return sqlDialect.toInsertSql(sqlMappingOperations, bean, clazz, tableName);
		case DELETE:
			return sqlDialect.toDeleteSql(sqlMappingOperations, bean, clazz, tableName);
		case SAVE_OR_UPDATE:
			return sqlDialect.toSaveOrUpdateSql(sqlMappingOperations, bean, clazz, tableName);
		case UPDATE:
			return sqlDialect.toUpdateSql(sqlMappingOperations, bean, clazz, tableName);
		default:
			throw new NotSupportException(operationType.name());
		}
	}

	public static void registerCglibProxyTableBean(String pageName) {
		if (!StringUtils.isEmpty(pageName)) {
			logger.info("register proxy package:{}", pageName);
		}

		for (Class<?> type : ClassUtils.getClassList(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			FieldSetterListenUtils.createFieldSetterListenProxyClass(type);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K> Map<String, K> getInIdKeyMap(SqlMappingOperations sqlMappingOperations, Class<?> clazz,
			Collection<K> inIds, Object[] params) {
		if (CollectionUtils.isEmpty(inIds)) {
			return Collections.EMPTY_MAP;
		}

		Map<String, K> keyMap = new HashMap<String, K>();
		Iterator<K> valueIterator = inIds.iterator();

		while (valueIterator.hasNext()) {
			K k = valueIterator.next();
			Object[] ids;
			if (params == null || params.length == 0) {
				ids = new Object[] { k };
			} else {
				ids = new Object[params.length];
				System.arraycopy(params, 0, ids, 0, params.length);
				ids[ids.length - 1] = valueIterator.next();
			}
			keyMap.put(sqlMappingOperations.getObjectKeyById(clazz, Arrays.asList(ids)), k);
		}
		return keyMap;
	}
}
