package scw.orm.sql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scw.core.utils.MultiIterator;
import scw.orm.MappingContext;

public final class TableMappingContext implements Iterable<MappingContext> {
	private List<MappingContext> primaryKeys;
	private List<MappingContext> notPrimaryKeys;
	private Map<String, MappingContext> contextMap;

	public TableMappingContext(List<MappingContext> primaryKeys, List<MappingContext> notPrimaryKeys,
			Map<String, MappingContext> contextMap) {
		this.primaryKeys = primaryKeys;
		this.notPrimaryKeys = notPrimaryKeys;
		this.contextMap = contextMap;
	}

	public int size() {
		return primaryKeys.size() + notPrimaryKeys.size();
	}

	@SuppressWarnings("unchecked")
	public Iterator<MappingContext> iterator() {
		return new MultiIterator<MappingContext>(primaryKeys.iterator(), notPrimaryKeys.iterator());
	}

	public MappingContext getMappingContext(String name) {
		return contextMap.get(name);
	}

	public final List<MappingContext> getPrimaryKeys() {
		return primaryKeys;
	}

	public final List<MappingContext> getNotPrimaryKeys() {
		return notPrimaryKeys;
	}
}
