package scw.orm;

public interface SetterFilterChain {
	void setter(MappingContext context, Object bean, Object value) throws Exception;
}
