package scw.orm;

public interface SetterFilterChain {
	void setter(FieldDefinitionContext context, Object bean, Object value) throws Exception;
}
