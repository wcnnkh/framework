package scw.orm;

public interface Setter {
	void setter(MappingContext context, Object bean, MappingOperations ormOperations)
			throws Exception;
}
