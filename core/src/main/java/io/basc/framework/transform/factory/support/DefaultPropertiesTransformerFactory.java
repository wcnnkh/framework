package io.basc.framework.transform.factory.support;

import java.util.Map;
import java.util.TreeMap;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.observe.register.ObservableServiceLoader;
import io.basc.framework.register.LimitedRegistration;
import io.basc.framework.register.Registration;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.PropertiesTransformer;
import io.basc.framework.transform.ReadOnlyProperty;
import io.basc.framework.transform.array.ArrayProperties;
import io.basc.framework.transform.factory.config.PropertiesTransformerRegistry;
import io.basc.framework.transform.map.MapProperties;
import io.basc.framework.transform.strategy.DefaultPropertiesTransformStrategy;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.transform.strategy.filter.FilterablePropertiesTransformStrategy;
import io.basc.framework.transform.strategy.filter.PropertiesTransformFilter;
import io.basc.framework.util.XUtils;

public class DefaultPropertiesTransformerFactory<E extends Throwable>
		extends DefaultReversibleTransformerFactory<Object, E> implements PropertiesTransformerRegistry<E> {
	private static class InternalPropertiesTransformStrategy extends FilterablePropertiesTransformStrategy {
		private final String id;

		public InternalPropertiesTransformStrategy(Iterable<? extends PropertiesTransformFilter> filters,
				PropertiesTransformStrategy dottomlessMappingStrategy, String id) {
			super(filters, dottomlessMappingStrategy);
			this.id = id;
		}
	}

	private final String id = XUtils.getUUID();
	private TreeMap<Class<?>, PropertiesTransformer<?, ? extends E>> propertiesTransformerMap;

	private final ObservableServiceLoader<PropertiesTransformFilter> propertiesTransformFilterRegistry = new ObservableServiceLoader<>();

	private final DefaultPropertiesTransformStrategy propertiesTransformStrategy = new DefaultPropertiesTransformStrategy();

	public DefaultPropertiesTransformerFactory() {
		registerPropertiesTransformer(Object[].class, (s, e) -> new ArrayProperties(s));
		registerPropertiesTransformer(Properties.class, (s, e) -> s);
		registerPropertiesTransformer(Map.class,
				(obj, type) -> new MapProperties(obj, type, propertiesTransformStrategy.getConversionService()));
		registerPropertiesTransformer(Parameters.class, (s, e) -> () -> s.getElements().map(ReadOnlyProperty::new));
	}

	@Override
	public boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canReverseTransform(sourceType, targetType)
				|| PropertiesTransformerRegistry.super.canReverseTransform(sourceType, targetType);
	}

	@Override
	public boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canTransform(sourceType, targetType)
				|| PropertiesTransformerRegistry.super.canTransform(sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> PropertiesTransformer<T, E> getPropertiesTransformer(Class<? extends T> requiredType) {
		return (PropertiesTransformer<T, E>) get(requiredType, this.propertiesTransformerMap);
	}

	public ObservableServiceLoader<PropertiesTransformFilter> getPropertiesTransformFilterRegistry() {
		return propertiesTransformFilterRegistry;
	}

	public DefaultPropertiesTransformStrategy getPropertiesTransformStrategy() {
		return propertiesTransformStrategy;
	}

	@Override
	public PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor requiredTypeDescriptor) {
		return wrap(propertiesTransformStrategy);
	}

	@Override
	public <T> Registration registerPropertiesTransformer(Class<T> requiredType,
			PropertiesTransformer<T, ? extends E> propertiesTransformer) {
		this.propertiesTransformerMap = register(requiredType, propertiesTransformer, propertiesTransformerMap);
		return LimitedRegistration.of(() -> propertiesTransformerMap.remove(requiredType));
	}

	@Override
	public void reverseTransform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E {
		if (super.canReverseTransform(sourceType, targetType)) {
			super.reverseTransform(source, sourceType, target, targetType);
		}
		PropertiesTransformerRegistry.super.reverseTransform(source, sourceType, target, targetType);
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType) throws E {
		if (super.canTransform(sourceType, targetType)) {
			super.transform(source, sourceType, target, targetType);
		}

		PropertiesTransformerRegistry.super.transform(source, sourceType, target, targetType);
	}

	protected PropertiesTransformStrategy wrap(PropertiesTransformStrategy propertiesTransformStrategy) {
		if (propertiesTransformStrategy instanceof InternalPropertiesTransformStrategy) {
			if (((InternalPropertiesTransformStrategy) propertiesTransformStrategy).id.equals(this.id)) {
				return propertiesTransformStrategy;
			}
		}
		return new InternalPropertiesTransformStrategy(propertiesTransformFilterRegistry.getServices(),
				propertiesTransformStrategy, id);
	}
}
