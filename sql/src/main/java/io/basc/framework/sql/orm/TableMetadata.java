package io.basc.framework.sql.orm;

import io.basc.framework.orm.EntityMetadata;

public interface TableMetadata extends EntityMetadata {
	String getEngine();

	String getRowFormat();
}
