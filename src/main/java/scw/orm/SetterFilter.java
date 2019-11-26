package scw.orm;

public interface SetterFilter {
	void setter(MappingContext context, Object bean, Object value,
			SetterFilterChain chain) throws Exception;
}