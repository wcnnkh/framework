package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityMapping;

public interface TableMapping<T extends Column> extends EntityMapping<T> {
	String getEngine();

	String getRowFormat();

	boolean isAutoCreate();
}
