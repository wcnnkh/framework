package scw.orm.sql;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import scw.core.utils.MultiIterator;
import scw.orm.MappingContext;

public class TableFieldContext {
	private LinkedList<MappingContext> primaryKeys;
	private LinkedList<MappingContext> notPrimaryKeys;
	private Map<String, MappingContext> contextMap;

	public TableFieldContext(LinkedList<MappingContext> primaryKeys, LinkedList<MappingContext> notPrimaryKeys,
			Map<String, MappingContext> contextMap) {
		this.primaryKeys = primaryKeys;
		this.notPrimaryKeys = notPrimaryKeys;
		this.contextMap = contextMap;
	}

	public LinkedList<MappingContext> getPrimaryKeys() {
		return primaryKeys;
	}

	public LinkedList<MappingContext> getNotPrimaryKeys() {
		return notPrimaryKeys;
	}

	public MappingContext getMappingContext(String name) {
		return contextMap.get(name);
	}

	public int size() {
		return primaryKeys.size() + notPrimaryKeys.size();
	}

	@SuppressWarnings("unchecked")
	public Iterator<MappingContext> iterator() {
		return new MultiIterator<MappingContext>(primaryKeys.iterator(), notPrimaryKeys.iterator());
	}

}
