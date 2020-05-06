package scw.mapper;

public interface Getter extends FieldDescriptor {
	Object get(Object instance) throws Exception;
}
