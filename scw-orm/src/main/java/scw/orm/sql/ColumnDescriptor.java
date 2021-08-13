package scw.orm.sql;

public interface ColumnDescriptor {
	String getName();

	boolean isPrimaryKey();

	boolean isAutoIncrement();

	boolean isNullable();

	boolean isUnique();

	String getCharsetName();

	String getComment();
}
