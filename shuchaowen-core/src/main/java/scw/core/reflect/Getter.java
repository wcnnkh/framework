package scw.core.reflect;



public interface Getter extends FieldMetadata {
	Object get(Object instance) throws Exception;
}
