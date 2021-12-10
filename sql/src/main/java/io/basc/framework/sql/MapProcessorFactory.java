package io.basc.framework.sql;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.stream.Processor;

import java.sql.ResultSet;

@FunctionalInterface
public interface MapProcessorFactory {
	<T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(TypeDescriptor type);

	default <T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(Class<? extends T> type) {
		return getMapProcessor(TypeDescriptor.valueOf(type));
	}
}
