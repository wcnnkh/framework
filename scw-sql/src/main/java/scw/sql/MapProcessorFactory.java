package scw.sql;

import java.sql.ResultSet;

import scw.convert.TypeDescriptor;
import scw.util.stream.Processor;

@FunctionalInterface
public interface MapProcessorFactory {
	<T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(TypeDescriptor type);
	
	default <T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(Class<? extends T> type){
		return getMapProcessor(TypeDescriptor.valueOf(type));
	}
}
