package io.basc.framework.convert.config.support;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.convert.lang.ConversionComparator;
import io.basc.framework.util.NestingChecker;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ThreadLocalNestingChecker;
import io.basc.framework.value.Value;

public class ConfigurableConversionService extends DefaultMapperRegistry<Object, ConversionException>
		implements ConversionService, Comparable<Object>, Configurable {
	private static final NestingChecker<ConversionService> NESTING_CHECKERS = new ThreadLocalNestingChecker<>();
	private final ConfigurableServices<ConversionService> registry = new ConfigurableServices<>(
			ConversionComparator.INSTANCE, ConversionService.class);
	private boolean configured;

	@Override
	public boolean isConfigured() {
		synchronized (this) {
			return configured;
		}
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		synchronized (this) {
			configured = true;
			if (!registry.isConfigured()) {
				registry.configure(serviceLoaderFactory);
			}
		}
	}

	public ConfigurableConversionService() {
		registry.getServiceInjectorRegistry().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				((ConversionServiceAware) service).setConversionService(this);
			}
			return Registration.EMPTY;
		});
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		if (canDirectlyConvert(sourceType, targetType)) {
			return true;
		}

		if (isConverterRegistred(targetType.getType())) {
			return true;
		}

		if (canInstantiated(targetType) && (isTransformerRegistred(targetType.getType())
				|| isReverseTransformerRegistred(sourceType.getType()))) {
			return true;
		}

		for (ConversionService service : registry.getServices()) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return true;
				}
			} finally {
				registration.unregister();
			}
		}
		return false;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		TypeDescriptor sourceTypeToUse = sourceType;
		if (sourceType == null && source != null) {
			sourceTypeToUse = TypeDescriptor.forObject(source);
		}

		for (ConversionService service : getRegistry().getServices()) {
			if (NESTING_CHECKERS.isNestingExists(service)) {
				continue;
			}

			Registration registration = NESTING_CHECKERS.registerNestedElement(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return service.convert(source, sourceTypeToUse, targetType);
				}
			} finally {
				registration.unregister();
			}
		}

		if (canDirectlyConvert(sourceTypeToUse, targetType)) {
			return source;
		}

		if (sourceTypeToUse == null) {
			Object value = Value.EMPTY.getAsObject(targetType);
			return value;
		}

		if (isConverterRegistred(targetType.getType())) {
			return super.convert(source, sourceTypeToUse, targetType);
		}

		if (isInverterRegistred(sourceTypeToUse.getType())) {
			return super.invert(source, sourceTypeToUse, targetType);
		}

		if (canInstantiated(targetType) && isTransformerRegistred(sourceTypeToUse.getType())) {
			Object target = newInstance(targetType);
			super.reverseTransform(source, sourceTypeToUse, target, targetType);
			return target;
		}
		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	@Override
	public final <R> R invert(Object source, Class<? extends Object> sourceType, Class<? extends R> targetType)
			throws ConversionException {
		return convert(source, sourceType, targetType);
	}

	public ConfigurableServices<ConversionService> getRegistry() {
		return registry;
	}

	@Override
	public int compareTo(Object o) {
		for (ConversionService service : getRegistry().getServices()) {
			if (ConversionComparator.INSTANCE.compare(service, o) == -1) {
				return -1;
			}
		}
		return 1;
	}
}
