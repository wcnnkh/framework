package scw.orm;

public interface GetterFilter {
	Object getter(MappingContext context, Getter getter, GetterFilterChain chain) throws ORMException;
}