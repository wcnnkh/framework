package scw.orm;

public interface GetterFilter {
	Object getter(FieldDefinitionContext context, Object bean, GetterFilterChain chain) throws Exception;
}