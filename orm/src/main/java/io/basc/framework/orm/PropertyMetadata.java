package io.basc.framework.orm;

import io.basc.framework.util.Named;

public interface PropertyMetadata extends Named {
	boolean isAutoIncrement();

	boolean isPrimaryKey();

	boolean isNullable();

	String getCharsetName();

	String getComment();

	boolean isUnique();
}
