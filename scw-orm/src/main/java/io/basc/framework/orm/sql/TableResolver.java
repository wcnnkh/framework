package io.basc.framework.orm.sql;

import io.basc.framework.orm.ObjectRelationalMapping;

public interface TableResolver extends ObjectRelationalMapping {
	TableStructure resolve(Class<?> entityClass);
}
