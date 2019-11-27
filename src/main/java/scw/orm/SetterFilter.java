package scw.orm;

public interface SetterFilter {
	void setter(MappingContext context, Setter setter, Object value, SetterFilterChain chain) throws Exception;
}