package scw.orm.sql;

public interface TableStructure extends Iterable<Column> {
	String getName();
}
