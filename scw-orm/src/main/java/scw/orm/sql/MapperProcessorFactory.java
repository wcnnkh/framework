package scw.orm.sql;

import java.sql.ResultSet;

import scw.convert.TypeDescriptor;
import scw.util.stream.Processor;

@FunctionalInterface
public interface MapperProcessorFactory {
	<T> Processor<ResultSet, T, ? extends Throwable> getMapperProcessor(TypeDescriptor type);
	
	default <T> Processor<ResultSet, T, ? extends Throwable> getMapperProcessor(Class<? extends T> type){
		return getMapperProcessor(TypeDescriptor.valueOf(type));
	}
}
