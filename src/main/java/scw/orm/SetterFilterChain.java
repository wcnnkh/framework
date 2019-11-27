package scw.orm;

public interface SetterFilterChain {
	void setter(MappingContext context, Setter setter, Object value) throws Exception;
}
