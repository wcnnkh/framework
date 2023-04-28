package io.basc.framework.mapper;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.convert.ConversionFactory;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.value.PropertyFactory;

public class DefaultObjectMapper<S, E extends Throwable> extends ConversionFactory<S, E>
		implements ObjectMapper<S, E>, ConversionServiceAware {
	private final ObjectMapperContext context = new ObjectMapperContext();
	private final Map<Class<?>, ObjectAccessFactory<?, ? extends E>> objectAccessFactoryMap = new TreeMap<>(
			TypeComparator.DEFAULT);
	private final Map<Class<?>, Mapping<? extends Field>> mappingMap = new ConcurrentHashMap<>();
	private final DefaultMappingStrategy<E> defaultMappingStrategy = new DefaultMappingStrategy<>();

	public DefaultObjectMapper() {
		registerObjectAccessFactory(PropertyFactory.class, (s, e) -> new PropertyFactoryAccess<>(s));
		registerObjectAccessFactory(Map.class, (s, e) -> new MapAccess<>(s, e, getConversionService()));
	}

	public final ObjectMapperContext getContext() {
		return context;
	}

	public ObjectMapperContext getContext(TypeDescriptor targetType, ObjectMapperContext parent) {
		return parent;
	}

	public final ConversionService getConversionService() {
		return this.context.getConversionService();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ObjectAccessFactory<T, E> getObjectAccessFactory(Class<? extends T> type) {
		Object object = objectAccessFactoryMap.get(type);
		if (object == null) {
			for (Entry<Class<?>, ObjectAccessFactory<?, ? extends E>> entry : objectAccessFactoryMap.entrySet()) {
				if (ClassUtils.isAssignable(entry.getKey(), type)) {
					object = entry.getValue();
					break;
				}
			}
		}
		return (ObjectAccessFactory<T, E>) object;
	}

	@Override
	public Mapping<? extends Field> getMapping(Class<?> entityClass) {
		Mapping<? extends Field> structure = mappingMap.get(entityClass);
		if (structure == null) {
			synchronized (this) {
				structure = mappingMap.get(entityClass);
				if (structure == null) {
					structure = ObjectMapper.super.getMapping(entityClass);
					mappingMap.put(entityClass, structure);
				}
			}
		}
		return structure;
	}

	@Override
	public boolean isMappingRegistred(Class<?> entityClass) {
		return mappingMap.containsKey(entityClass);
	}

	@Override
	public <T> void registerObjectAccessFactory(Class<T> type, ObjectAccessFactory<? super T, ? extends E> factory) {
		Assert.requiredArgument(type != null, "type");
		if (factory == null) {
			objectAccessFactoryMap.remove(type);
		} else {
			objectAccessFactoryMap.put(type, factory);
		}
	}

	@Override
	public void registerMapping(Class<?> entityClass, Mapping<? extends Field> structure) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (structure == null) {
			mappingMap.remove(entityClass);
		} else {
			mappingMap.put(entityClass, structure);
		}
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.context.setConversionService(conversionService);
	}

	public DefaultMappingStrategy<E> getDefaultMappingStrategy() {
		return defaultMappingStrategy;
	}

	@Override
	public MappingStrategy<E> getMappingStrategy(TypeDescriptor typeDescriptor) {
		return defaultMappingStrategy;
	}
}
