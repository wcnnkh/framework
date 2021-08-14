package scw.orm.sql;

import scw.orm.ObjectRelationalMapping;

public interface TableResolver extends ObjectRelationalMapping {
	TableStructure resolve(Class<?> entityClass);
}
