package io.basc.framework.sql.orm;

import io.basc.framework.orm.ObjectRelationalMapping;

public interface TableResolver extends ObjectRelationalMapping {
	TableStructure resolve(Class<?> entityClass);
}
