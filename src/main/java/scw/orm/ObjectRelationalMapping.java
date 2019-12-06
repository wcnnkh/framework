package scw.orm;

import java.util.Iterator;
import java.util.List;

public interface ObjectRelationalMapping extends Iterable<MappingContext> {
	List<MappingContext> getPrimaryKeys();

	List<MappingContext> getNotPrimaryKeys();

	List<MappingContext> getEntitys();

	Iterator<MappingContext> iteratorPrimaryKeyAndNotPrimaryKey();
	
	MappingContext getMappingContext(String columnName);
}
