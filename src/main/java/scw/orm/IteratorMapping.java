package scw.orm;

public interface IteratorMapping<M extends Mapper> {
	void iterator(MappingContext context, M mappingOperations) throws Exception;
}
