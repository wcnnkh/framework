package run.soeasy.framework.core.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.util.spi.ServiceMap;

public class DefaultMapperRegistry<S, E extends Throwable> extends ServiceMap<Mapper<S, ?, E>>
		implements MapperFactory<S, E> {
	@SuppressWarnings("unchecked")
	@Override
	public <T> Mapper<S, T, E> getMapper(@NonNull Class<? extends T> requiredType) {
		return (Mapper<S, T, E>) search(requiredType).first();
	}
}
