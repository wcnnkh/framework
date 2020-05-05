package scw.core.reflect;



public interface Setter extends FieldMetadata{
	void set(Object instance, Object value) throws Exception;
}
