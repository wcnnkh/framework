package scw.mapper.support;

import scw.mapper.FieldContext;

public interface Mapper {
	<T> T mapping(Class<? extends T> type, FieldContext parentContext) throws Exception;
}
