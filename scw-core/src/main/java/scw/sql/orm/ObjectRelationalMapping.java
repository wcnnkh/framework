package scw.sql.orm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import scw.aop.ProxyUtils;
import scw.aop.support.FieldSetterListenUtils;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.Ignore;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.FilterFeature;
import scw.mapper.Mapper;
import scw.mapper.MapperUtils;
import scw.sql.orm.annotation.NotColumn;
import scw.sql.orm.annotation.Table;
import scw.util.AbstractIterator;
import scw.util.Accept;

/**
 * 默认的orm定义
 * 
 * @author shuchaowen
 *
 */
public class ObjectRelationalMapping implements FieldFilter {
	/**
	 * 默认对象主键的连接符
	 */
	public static final char PRIMARY_KEY_CONNECTOR_CHARACTER = StringUtils
			.parseChar(GlobalPropertyFactory.getInstance().getString("orm.primary.key.connector.character"), ':');

	public Mapper getMapper() {
		return MapperUtils.getMapper();
	}

	protected void appendObjectKeyByValue(StringBuilder appendable, Column column, Object value) {
		appendable.append(PRIMARY_KEY_CONNECTOR_CHARACTER);
		appendable.append(StringUtils.transferredMeaning(value == null ? null : value.toString(),
				PRIMARY_KEY_CONNECTOR_CHARACTER));
	}

	/**
	 * 迭代可以映射的字段，包含entity字段
	 * 
	 * @param entityClass
	 * @param parentField
	 *            指明父级字段
	 * @return
	 */
	public Iterable<Column> iterable(Class<?> entityClass, Field parentField) {
		return new ColumnIterable(getMapper().iterable(entityClass, true, parentField, this));
	}

	/**
	 * 迭代可以映射的字段，包含entity字段
	 * 
	 * @param entityClass
	 * @return
	 */
	public final Iterable<Column> iterable(Class<?> entityClass) {
		return iterable(entityClass, null);
	}

	/**
	 * 获取数据库字段
	 * 
	 * @param entityClass
	 * @return
	 */
	public final Collection<Column> getColumns(Class<?> entityClass) {
		LinkedHashSet<Column> columns = new LinkedHashSet<Column>();
		for(Column column : iterable(entityClass)){
			if (column.isEntity() || columns.contains(column)) {
				continue;
			}

			columns.add(column);
		}
		return columns;
	}

	/**
	 * 获取主键字段
	 * 
	 * @param entityClass
	 * @return
	 */
	public final Collection<Column> getPrimaryKeys(Class<?> entityClass) {
		LinkedHashSet<Column> columns = new LinkedHashSet<Column>();
		for(Column column : iterable(entityClass)){
			if (!column.isPrimaryKey() || column.isEntity() || columns.contains(column)) {
				continue;
			}

			columns.add(column);
		}
		return columns;
	}

	/**
	 * 获取非主键字段
	 * 
	 * @param entityClass
	 * @return
	 */
	public final Collection<Column> getNotPrimaryKeys(Class<?> entityClass) {
		LinkedHashSet<Column> columns = new LinkedHashSet<Column>();
		for(Column column : iterable(entityClass)){
			if (column.isPrimaryKey() || column.isEntity() || columns.contains(column)) {
				continue;
			}

			columns.add(column);
		}
		return columns;
	}

	protected Column createColumn(Field field) {
		return new Column(field);
	}

	public final <T> String getObjectKey(Class<? extends T> clazz, final T bean) {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		for (Column column : getPrimaryKeys(clazz)) {
			Object value = column.get(bean);
			appendObjectKeyByValue(sb, column, value);
		}
		return sb.toString();
	}

	public final String getObjectKeyById(Class<?> clazz, Collection<Object> primaryKeys) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		Iterator<Column> columnIterator = getPrimaryKeys(clazz).iterator();
		Iterator<Object> valueIterator = primaryKeys.iterator();
		while (columnIterator.hasNext() && valueIterator.hasNext()) {
			Column column = columnIterator.next();
			appendObjectKeyByValue(sb, column, column.toDataBaseValue(valueIterator.next()));
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public final <K> Map<String, K> getInIdKeyMap(Class<?> clazz, Collection<? extends K> lastPrimaryKeys,
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

	public Column getColumn(Class<?> entityClass, String name) {
		for(Column column : iterable(entityClass)){
			if (column.isEntity()) {
				continue;
			}

			if (column.getName().equals(name) || column.getField().getGetter().getName().equalsIgnoreCase(name)
					|| column.getField().getSetter().getName().equalsIgnoreCase(name)) {
				return column;
			}
		}
		return null;
	}

	public boolean isTable(Class<?> clazz) {
		return clazz.getAnnotation(Table.class) != null;
	}

	/**
	 * 必须满足的条件
	 */
	public boolean accept(Field field) {
		if (!(field.isSupportGetter() && field.isSupportSetter()
				&& FilterFeature.IGNORE_STATIC.getFilter().accept(field))) {
			return false;
		}

		if (field.getSetter().getField() == null || field.getGetter().getField() == null) {
			return false;
		}

		Ignore ignore = field.getAnnotatedElement().getAnnotation(Ignore.class);
		if (ignore != null) {
			return false;
		}

		NotColumn exclude = field.getAnnotatedElement().getAnnotation(NotColumn.class);
		if (exclude != null) {
			return false;
		}
		return true;
	}

	private final class ColumnIterable implements Iterable<Column> {
		private Iterable<Field> iterable;

		public ColumnIterable(Iterable<Field> iterable) {
			this.iterable = iterable;
		}

		public Iterator<Column> iterator() {
			return new ColumnIterator(iterable.iterator());
		}
	}

	private final class ColumnIterator extends AbstractIterator<Column> {
		private Iterator<Field> iterator;

		public ColumnIterator(Iterator<Field> iterator) {
			this.iterator = iterator;
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public Column next() {
			return createColumn(iterator.next());
		}
	}

	public Map<IndexInfo, List<IndexInfo>> getIndexInfoMap(Class<?> entityClass) {
		Map<IndexInfo, List<IndexInfo>> indexMap = new LinkedHashMap<IndexInfo, List<IndexInfo>>();
		for (Column column : getColumns(entityClass)) {
			scw.sql.orm.annotation.Index index = column.getField().getAnnotatedElement()
					.getAnnotation(scw.sql.orm.annotation.Index.class);
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

	public String getTableName(Class<?> tableClass) {
		Table table = tableClass.getAnnotation(Table.class);
		if (table == null) {
			return StringUtils.humpNamingReplacement(tableClass.getSimpleName(), "_");
		}

		if (StringUtils.isEmpty(table.name())) {
			return StringUtils.humpNamingReplacement(tableClass.getSimpleName(), "_");
		}
		return table.name();
	}

	public Column findColumn(Class<?> tableClass, Accept<Column> accept) {
		for(Column column : iterable(tableClass)){
			if (accept.accept(column)) {
				return column;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T newEntity(Class<? extends T> entityClass) {
		if (isTable(entityClass) && ProxyUtils.getProxyFactory().isSupport(entityClass)) {
			return (T) FieldSetterListenUtils.getFieldSetterListenProxy(entityClass).create();
		} else {
			return InstanceUtils.INSTANCE_FACTORY.getInstance(entityClass);
		}
	}
}
