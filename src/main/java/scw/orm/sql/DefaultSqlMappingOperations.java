package scw.orm.sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.IteratorCallback;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.orm.DefaultFieldDefinitionFactory;
import scw.orm.DefaultMappingOperations;
import scw.orm.FieldDefinitionFactory;
import scw.orm.GetterFilter;
import scw.orm.IteratorMapping;
import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.SetterFilter;
import scw.orm.SimpleGetter;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.annotation.Table;

public class DefaultSqlMappingOperations extends DefaultMappingOperations implements SqlMappingOperations {
	private static final char DEFAULT_CONNECTOR_CHARACTER = StringUtils
			.parseChar(SystemPropertyUtils.getProperty("orm.object.id.connector.character"), ':');

	public DefaultSqlMappingOperations(Collection<? extends SetterFilter> setterFilters,
			Collection<? extends GetterFilter> getterFilters) {
		this(new DefaultFieldDefinitionFactory(), setterFilters, getterFilters, new TableInstanceFactory());
	}

	public DefaultSqlMappingOperations(FieldDefinitionFactory fieldDefinitionFactory,
			Collection<? extends SetterFilter> setterFilters, Collection<? extends GetterFilter> getterFilters,
			NoArgsInstanceFactory instanceFactory) {
		super(fieldDefinitionFactory, setterFilters, getterFilters, instanceFactory);
	}

	public String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			return StringUtils.humpNamingReplacement(clazz.getSimpleName(), "_");
		}

		if (StringUtils.isEmpty(table.name())) {
			return StringUtils.humpNamingReplacement(clazz.getSimpleName(), "_");
		}

		return table.name();
	}

	public Collection<MappingContext> getPrimaryKeys(MappingContext supperContext, Class<?> clazz) {
		return getMappingContexts(supperContext, clazz, new IteratorCallback<MappingContext>() {

			public boolean iteratorCallback(MappingContext data) {
				return SqlORMUtils.isPrimaryKey(data.getFieldDefinition());
			}
		});
	}

	public Collection<MappingContext> getNotPrimaryKeys(MappingContext supperContext, Class<?> clazz) {
		return getMappingContexts(supperContext, clazz, new IteratorCallback<MappingContext>() {

			public boolean iteratorCallback(MappingContext data) {
				return !SqlORMUtils.isPrimaryKey(data.getFieldDefinition())
						&& SqlORMUtils.isDataBaseField(data.getFieldDefinition());
			}
		});
	}

	public Collection<MappingContext> getSqlMappingContext(MappingContext supperContext, Class<?> clazz) {
		return getMappingContexts(supperContext, clazz, new IteratorCallback<MappingContext>() {

			public boolean iteratorCallback(MappingContext data) {
				return SqlORMUtils.isDataBaseField(data.getFieldDefinition());
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

			if (isPrmaryKey(context)) {
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
		return SqlORMUtils.isDataBaseField(mappingContext.getFieldDefinition());
	}

	public boolean isPrmaryKey(MappingContext mappingContext) {
		return SqlORMUtils.isPrimaryKey(mappingContext.getFieldDefinition());
	}

	public Collection<MappingContext> getPrimaryKeys(Class<?> clazz) {
		return getPrimaryKeys(null, clazz);
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

	protected void appendObjectKeyByValue(Appendable appendable, Object value) throws IOException {
		appendable.append(DEFAULT_CONNECTOR_CHARACTER);
		appendable.append(
				StringUtils.transferredMeaning(value == null ? null : value.toString(), DEFAULT_CONNECTOR_CHARACTER));
	}

	public <T> String getObjectKey(Class<? extends T> clazz, final T bean) {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		try {
			iterator(null, clazz, new IteratorMapping() {

				public void iterator(MappingContext context, MappingOperations mappingOperations) throws Exception {
					if (isPrmaryKey(context)) {
						appendObjectKeyByValue(sb, getter(context, bean));
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public String getObjectKeyById(Class<?> clazz, Collection<Object> ids) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		Iterator<MappingContext> iterator = getPrimaryKeys(clazz).iterator();
		Iterator<Object> valueIterator = ids.iterator();
		while (iterator.hasNext() && valueIterator.hasNext()) {
			try {
				appendObjectKeyByValue(sb, getter(iterator.next(), new SimpleGetter(valueIterator.next())));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sb.toString();
	}

	public boolean isTable(Class<?> clazz) {
		return clazz.getAnnotation(Table.class) != null;
	}

	public boolean isIndexColumn(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(Index.class) != null;
	}

	public boolean isNullAble(MappingContext context) {
		if (context.getFieldDefinition().getField().getType().isPrimitive() || isPrmaryKey(context)
				|| isIndexColumn(context.getFieldDefinition())) {
			return false;
		}

		Column column = context.getFieldDefinition().getAnnotation(Column.class);
		return column == null ? true : column.nullAble();
	}
}
