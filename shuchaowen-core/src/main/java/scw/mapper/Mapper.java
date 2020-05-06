package scw.mapper;

public interface Mapper {
	<T> T mapping(Class<? extends T> type, FieldContext parentContext) throws Exception;
}
