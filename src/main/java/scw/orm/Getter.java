package scw.orm;

public interface Getter {
	Object getter(MappingContext context) throws Exception;
}
