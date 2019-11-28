package scw.orm;

public interface SetterMapping<M extends Mapper> {
	void setter(MappingContext context, Object bean, M ormOperations) throws Exception;
}