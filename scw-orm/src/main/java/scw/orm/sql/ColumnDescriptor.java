package scw.orm.sql;

import scw.orm.PropertyDescriptor;

public interface ColumnDescriptor extends PropertyDescriptor{
	boolean isAutoIncrement();

	boolean isUnique();

	String getCharsetName();

	String getComment();
}
