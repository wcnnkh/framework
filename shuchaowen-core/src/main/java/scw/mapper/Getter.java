package scw.mapper;



public interface Getter extends FieldMetadata {
	Object get(Object instance) throws Exception;
}
