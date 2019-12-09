package scw.orm;

public interface Setter {
	void setter(MappingContext context, Object value) throws ORMException;
}
