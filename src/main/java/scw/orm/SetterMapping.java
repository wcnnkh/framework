package scw.orm;

public interface SetterMapping {
	void setter(MappingContext context, Object bean, MappingOperations ormOperations)
			throws Exception;
}