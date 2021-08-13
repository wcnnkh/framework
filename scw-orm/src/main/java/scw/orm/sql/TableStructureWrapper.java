package scw.orm.sql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import scw.util.Wrapper;

public class TableStructureWrapper extends Wrapper<TableStructure> implements TableStructure {

	public TableStructureWrapper(TableStructure tableStructure) {
		super(tableStructure);
	}

	@Override
	public Class<?> getEntityClass() {
		return wrappedTarget.getEntityClass();
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public List<Column> getColumns() {
		return wrappedTarget.getColumns();
	}

	@Override
	public Map<String, List<Column>> getIndexGroup() {
		return wrappedTarget.getIndexGroup();
	}

	@Override
	public List<Column> getNotPrimaryKeys() {
		return wrappedTarget.getNotPrimaryKeys();
	}

	@Override
	public List<Column> getPrimaryKeys() {
		return wrappedTarget.getPrimaryKeys();
	}

	@Override
	public Stream<Column> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public Iterator<Column> iterator() {
		return wrappedTarget.iterator();
	}

}
