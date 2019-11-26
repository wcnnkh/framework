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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.orm.IteratorMapping;
import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.sql.orm.DefaultSqlType;
import scw.sql.orm.SqlType;
import scw.sql.orm.SqlTypeFactory;
import scw.sql.orm.annotation.AutoIncrement;
import scw.sql.orm.annotation.Column;
import scw.sql.orm.annotation.Index;
import scw.sql.orm.annotation.NotColumn;
import scw.sql.orm.annotation.PrimaryKey;
import scw.sql.orm.annotation.Transient;
import scw.sql.orm.enums.CasType;

public final class SqlORMUtils {
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

	public static void iteratorTableFieldDefinition(MappingOperations mappingOperations, Class<?> clazz,
			final IteratorMapping iterator) throws Exception {
		mappingOperations.iterator(null, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				if (!isDataBaseField(context.getFieldDefinition())) {
					return;
				}

				iterator.iterator(context, mappingOperations);
			}
		});
	}

	public static void iteratorPrimaryKeyFieldDefinition(MappingOperations mappingOperations, Class<?> clazz,
			final IteratorMapping iterator) throws Exception {
		mappingOperations.iterator(null, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				if (!isDataBaseField(context.getFieldDefinition())) {
					return;
				}

				if (isPrimaryKey(context.getFieldDefinition())) {
					iterator.iterator(context, mappingOperations);
				}
			}
		});
	}

	public static void iteratorNotPrimaryKeyFieldDefinition(MappingOperations mappingOperations, Class<?> clazz,
			final IteratorMapping iterator) throws Exception {
		mappingOperations.iterator(null, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				if (!isDataBaseField(context.getFieldDefinition())) {
					return;
				}

				if (isPrimaryKey(context.getFieldDefinition())) {
					return;
				}

				iterator.iterator(context, mappingOperations);
			}
		});
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

	public static LinkedList<MappingContext> getPrimaryKeyFieldContexts(MappingOperations mappingOperations,
			Class<?> clazz) throws Exception {
		final LinkedList<MappingContext> fieldDefinitions = new LinkedList<MappingContext>();
		iteratorPrimaryKeyFieldDefinition(mappingOperations, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				fieldDefinitions.add(context);
			}
		});
		return fieldDefinitions;
	}
	
	/**
	 * @param mappingOperations
	 * @param clazz
	 * @param useFieldName 是否使用原始的字段名
	 * @return
	 * @throws Exception
	 */
	public static Map<String, MappingContext> getTableFieldContextMap(MappingOperations mappingOperations, Class<?> clazz, final boolean useFieldName) throws Exception{
		final Map<String, MappingContext> map = new LinkedHashMap<String, MappingContext>();
		iteratorTableFieldDefinition(mappingOperations, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				map.put(useFieldName? context.getFieldDefinition().getField().getName():context.getFieldDefinition().getName(), context);
			}
		});
		return map;
	}

	public static LinkedList<MappingContext> getTableFieldContexts(MappingOperations mappingOperations, Class<?> clazz)
			throws Exception {
		final LinkedList<MappingContext> fieldDefinitions = new LinkedList<MappingContext>();
		iteratorTableFieldDefinition(mappingOperations, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				fieldDefinitions.add(context);
			}
		});
		return fieldDefinitions;
	}

	public static LinkedList<MappingContext> getNotPrimaryKeyFieldContexts(MappingOperations mappingOperations,
			Class<?> clazz) throws Exception {
		final LinkedList<MappingContext> fieldDefinitions = new LinkedList<MappingContext>();
		iteratorNotPrimaryKeyFieldDefinition(mappingOperations, clazz, new IteratorMapping() {

			public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
				fieldDefinitions.add(context);
			}
		});
		return fieldDefinitions;
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
}
