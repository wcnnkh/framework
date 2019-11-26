package scw.orm;

public interface GetterFilterChain {
	Object getter(MappingContext context, Getter getter) throws Exception;
}