package io.basc.framework.orm;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.Field;
import io.basc.framework.util.stream.Processor;

public abstract class EntityStructureMapProcessor<P extends Property, S, T, E extends Throwable>
		implements Processor<S, T, E> {
	private final EntityStructure<? extends P> structore;
	private final ConversionService conversionService;

	public EntityStructureMapProcessor(EntityStructure<? extends P> structore) {
		this(structore, Sys.env.getConversionService());
	}

	public EntityStructureMapProcessor(EntityStructure<? extends P> structore,
			ConversionService conversionService) {
		this.structore = structore;
		this.conversionService = conversionService;
	}

	public EntityStructure<? extends P> getStructore() {
		return structore;
	}


	public ConversionService getConversionService() {
		return conversionService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T process(S source) throws E {
		Object instance = Sys.env.getInstance(structore.getEntityClass());
		for (P property : structore) {
			if (contains(source, property)) {
				Field field = property.getField();
				Object value = getProperty(source, property);
				field.set(instance, value, conversionService);
			}
		}
		return (T) instance;
	}

	protected abstract boolean contains(S source, P property) throws E;

	protected abstract Object getProperty(S source, P property) throws E;
}
