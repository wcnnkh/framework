package scw.orm;

public interface GetterFilterChain {
	Object getter(FieldDefinitionContext context, Object bean)
			throws Exception;
}