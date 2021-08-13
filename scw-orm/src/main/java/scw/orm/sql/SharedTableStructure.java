package scw.orm.sql;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public class SharedTableStructure implements TableColumnDescriptor, Serializable{
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Collection<Column> columns;
	
	public SharedTableStructure(String name, Collection<Column> columns) {
		this.name = name;
		this.columns =columns;
	}
	
	@Override
	public Iterator<Column> iterator() {
		return columns.iterator();
	}

	@Override
	public String getName() {
		return name;
	}

}
