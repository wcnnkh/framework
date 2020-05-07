package scw.mapper;

public interface EntityResolver extends FieldContextFilter{
	boolean isEntity(FieldDescriptor fieldDescriptor);
}
