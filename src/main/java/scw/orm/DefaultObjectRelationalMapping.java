package scw.orm;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scw.core.utils.MultiIterator;

@SuppressWarnings("unchecked")
public final class DefaultObjectRelationalMapping implements ObjectRelationalMapping {
	private List<MappingContext> primaryKeys;
	private List<MappingContext> notPrimaryKeys;
	private List<MappingContext> entitys;
	private Map<String, MappingContext> contextMap;

	public DefaultObjectRelationalMapping(List<MappingContext> primaryKeys, List<MappingContext> notPrimaryKeys,
			List<MappingContext> entitys, Map<String, MappingContext> contextMap) {
		this.primaryKeys = primaryKeys == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(primaryKeys);
		this.notPrimaryKeys = notPrimaryKeys == null ? Collections.EMPTY_LIST
				: Collections.unmodifiableList(notPrimaryKeys);
		this.entitys = entitys == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(entitys);
		this.contextMap = contextMap == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(contextMap);
	}

	public Iterator<MappingContext> iterator() {
		return new MultiIterator<MappingContext>(primaryKeys.iterator(), notPrimaryKeys.iterator(), entitys.iterator());
	}

	public List<MappingContext> getPrimaryKeys() {
		return primaryKeys;
	}

	public List<MappingContext> getNotPrimaryKeys() {
		return notPrimaryKeys;
	}

	public List<MappingContext> getEntitys() {
		return entitys;
	}

	public MappingContext getMappingContext(String columnName) {
		return contextMap.get(columnName);
	}

	public Iterator<MappingContext> iteratorPrimaryKeyAndNotPrimaryKey() {
		return new MultiIterator<MappingContext>(primaryKeys.iterator(), notPrimaryKeys.iterator());
	}

}
