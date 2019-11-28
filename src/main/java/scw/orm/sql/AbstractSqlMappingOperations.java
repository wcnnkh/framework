package scw.orm.sql;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.IteratorCallback;
import scw.orm.AbstractMappingOperations;
import scw.orm.MappingContext;
import scw.orm.sql.annotation.AutoIncrement;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.annotation.NotColumn;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.annotation.Transient;
import scw.orm.sql.enums.CasType;
import scw.orm.sql.support.SqlORMUtils;

public abstract class AbstractSqlMappingOperations extends AbstractMappingOperations implements SqlMapper {
	public Collection<MappingContext> getNotPrimaryKeys(MappingContext supperContext, Class<?> clazz) {
		return getMappingContexts(supperContext, clazz, new IteratorCallback<MappingContext>() {

			public boolean iteratorCallback(MappingContext data) {
				return !isPrimaryKey(data) && isDataBaseMappingContext(data);
			}
		});
	}

	public Collection<MappingContext> getSqlMappingContext(MappingContext supperContext, Class<?> clazz) {
		return getMappingContexts(supperContext, clazz, new IteratorCallback<MappingContext>() {

			public boolean iteratorCallback(MappingContext data) {
				return isDataBaseMappingContext(data);
			}
		});
	}

	protected void appendTableMappingContext(Class<?> declaringClass, MappingContext superContext, Class<?> clazz,
			Collection<MappingContext> primaryKeys, Collection<MappingContext> notPrimaryKeys,
			Map<String, MappingContext> contextMap, boolean useFieldName) {
		Map<String, FieldDefinition> map = getFieldDefinitionFactory().getFieldDefinitionMap(clazz);
		for (Entry<String, FieldDefinition> entry : map.entrySet()) {
			MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
			if (!isDataBaseMappingContext(context)) {
				continue;
			}

			if (isPrimaryKey(context)) {
				primaryKeys.add(context);
			} else {
				notPrimaryKeys.add(context);
			}
			contextMap.put(useFieldName ? context.getFieldDefinition().getField().getName()
					: context.getFieldDefinition().getName(), context);
		}

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			appendTableMappingContext(declaringClass, superContext, superClazz, primaryKeys, notPrimaryKeys, contextMap,
					useFieldName);
		}
	}

	public TableMappingContext getTableMappingContext(MappingContext supperContext, Class<?> tableClass,
			boolean useFieldName) {
		List<MappingContext> primaryKeys = new ArrayList<MappingContext>(8);
		List<MappingContext> notPrimaryKeys = new ArrayList<MappingContext>(8);
		Map<String, MappingContext> contextMap = new LinkedHashMap<String, MappingContext>();
		appendTableMappingContext(tableClass, supperContext, tableClass, primaryKeys, notPrimaryKeys, contextMap,
				useFieldName);
		return new TableMappingContext(primaryKeys, notPrimaryKeys, contextMap);
	}

	public boolean isDataBaseMappingContext(MappingContext mappingContext) {
		Column column = mappingContext.getFieldDefinition().getAnnotation(Column.class);
		if (column != null) {
			return true;
		}

		Class<?> type = mappingContext.getFieldDefinition().getField().getType();
		if (Class.class.isAssignableFrom(type) || type.isEnum() || type.isArray() || Map.class.isAssignableFrom(type)
				|| Collection.class.isAssignableFrom(type)) {
			return true;
		}

		return SqlORMUtils.isDataBaseType(type);
	}

	public Collection<MappingContext> getNotPrimaryKeys(Class<?> clazz) {
		return getNotPrimaryKeys(null, clazz);
	}

	public Collection<MappingContext> getSqlMappingContext(Class<?> clazz) {
		return getSqlMappingContext(null, clazz);
	}

	public TableMappingContext getTableMappingContext(Class<?> tableClass) {
		return getTableMappingContext(null, tableClass, true);
	}

	public boolean isTable(Class<?> clazz) {
		return clazz.getAnnotation(Table.class) != null;
	}

	public boolean isIndexColumn(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(Index.class) != null;
	}

	public boolean isNullAble(MappingContext context) {
		if (context.getFieldDefinition().getField().getType().isPrimitive() || isPrimaryKey(context)
				|| isIndexColumn(context.getFieldDefinition())) {
			return false;
		}

		Column column = context.getFieldDefinition().getAnnotation(Column.class);
		return column == null ? true : column.nullAble();
	}

	public boolean isIgnore(MappingContext context) {
		if (AnnotationUtils.isDeprecated(context.getFieldDefinition())) {
			return true;
		}

		NotColumn exclude = context.getFieldDefinition().getAnnotation(NotColumn.class);
		if (exclude != null) {
			return true;
		}

		Transient tr = context.getFieldDefinition().getAnnotation(Transient.class);
		if (tr != null) {
			return true;
		}

		if (Modifier.isStatic(context.getFieldDefinition().getField().getModifiers())
				|| Modifier.isTransient(context.getFieldDefinition().getField().getModifiers())) {
			return true;
		}
		return false;
	}

	public boolean isAutoIncrement(MappingContext context) {
		return context.getFieldDefinition().getAnnotation(AutoIncrement.class) != null;
	}

	public String getCharsetName(MappingContext context) {
		Column column = context.getFieldDefinition().getAnnotation(Column.class);
		return column == null ? null : column.charsetName().trim();
	}

	public boolean isUnique(MappingContext context) {
		Column column = context.getFieldDefinition().getAnnotation(Column.class);
		return column == null ? false : column.unique();
	}

	public CasType getCasType(MappingContext context) {
		if (isPrimaryKey(context)) {
			return CasType.NOTHING;
		}

		Column column = context.getFieldDefinition().getAnnotation(Column.class);
		if (column == null) {
			return CasType.NOTHING;
		}
		return column.casType();
	}
}
