package scw.orm;

import java.util.List;

public interface ObjectRelationalMapping extends Iterable<MappingContext> {
	List<MappingContext> getPrimaryKeys();

	List<MappingContext> getNotPrimaryKeys();

	List<MappingContext> getEntitys();

	MappingContext getMappingContext(String columnName);
}
