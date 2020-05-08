package scw.mapper;

public interface EntityResolver extends FieldFilter{
	boolean isEntity(FieldDescriptor fieldDescriptor);
}
