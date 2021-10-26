package io.basc.framework.sql.orm;

import io.basc.framework.orm.PropertyDescriptor;

public interface ColumnDescriptor extends PropertyDescriptor{
	boolean isAutoIncrement();

	boolean isUnique();

	String getCharsetName();

	String getComment();
}
