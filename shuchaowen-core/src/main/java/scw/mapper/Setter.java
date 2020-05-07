package scw.mapper;

public interface Setter extends FieldDescriptor {
	void set(Object instance, Object value) throws Exception;
}
