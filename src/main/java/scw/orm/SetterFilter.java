package scw.orm;

public interface SetterFilter {
	void setter(FieldDefinitionContext context, Object bean, Object value,
			SetterFilterChain chain) throws Exception;
}