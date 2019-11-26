package scw.orm;

public interface ValueMapping {
	void iterator(MappingContext context, Object bean, MappingOperations ormOperations)
			throws Exception;
}
