package io.basc.framework.mapper.support;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.convert.ConditionalConversionService;
import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.ConvertiblePair;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.support.DefaultConversionService;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.filter.FilterableMappingStrategy;
import io.basc.framework.mapper.filter.MappingStrategyFilter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ServiceRegistry;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.value.PropertyFactory;

public class DefaultObjectMapper extends DefaultConversionService
		implements ObjectMapper, ConditionalConversionService {
	private final Map<Class<?>, ObjectAccessFactory<?>> objectAccessFactoryMap = new TreeMap<>(TypeComparator.DEFAULT);
	private final Map<Class<?>, Mapping<? extends Field>> mappingMap = new ConcurrentHashMap<>();
	private Set<ConvertiblePair> convertiblePairs;
	private final ServiceRegistry<MappingStrategyFilter> filterRegistry = new ServiceRegistry<>();
	private final DefaultMappingStrategy mappingStrategy = new DefaultMappingStrategy();

	public DefaultObjectMapper() {
		registerObjectAccessFactory(PropertyFactory.class, (s, e) -> new PropertyFactoryAccess(s));
		registerObjectAccessFactory(Map.class, (s, e) -> new MapAccess(s, e, mappingStrategy.getConversionService()));
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertiblePairs == null ? Collections.emptySet() : convertiblePairs;
	}

	public ServiceRegistry<MappingStrategyFilter> getFilterRegistry() {
		return filterRegistry;
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

	public DefaultMappingStrategy getMappingStrategy() {
		return mappingStrategy;
	}

	@Override
	public MappingStrategy getMappingStrategy(TypeDescriptor typeDescriptor) {
		return new FilterableMappingStrategy(filterRegistry.getServices(), getMappingStrategy());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ObjectAccessFactory<T> getObjectAccessFactory(Class<? extends T> type) {
		Object object = objectAccessFactoryMap.get(type);
		if (object == null) {
			for (Entry<Class<?>, ObjectAccessFactory<?>> entry : objectAccessFactoryMap.entrySet()) {
				if (ClassUtils.isAssignable(entry.getKey(), type)) {
					object = entry.getValue();
					break;
				}
			}
		}
		return (ObjectAccessFactory<T>) object;
	}

	@Override
	public boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}
		// 一定可以进行转换
		return true;
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws ConversionException, ConverterNotFoundException {
		if (super.canTransform(sourceType, targetType)) {
			super.transform(source, sourceType, target, targetType);
			return;
		}
		transform(source, sourceType, null, target, targetType, null, getMappingStrategy(targetType));
	}

	@Override
	public boolean isMappingRegistred(Class<?> entityClass) {
		return mappingMap.containsKey(entityClass);
	}

	@Override
	public void registerMapping(Class<?> entityClass, Mapping<? extends Field> mapping) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (mapping == null) {
			mappingMap.remove(entityClass);
		} else {
			mappingMap.put(entityClass, mapping);
		}
	}

	@Override
	public <T> void registerObjectAccessFactory(Class<T> type, ObjectAccessFactory<? super T> factory) {
		Assert.requiredArgument(type != null, "type");
		ConvertiblePair c1 = new ConvertiblePair(type, Object.class);
		ConvertiblePair c2 = new ConvertiblePair(Object.class, type);
		if (factory == null) {
			if (convertiblePairs != null) {
				convertiblePairs.remove(c1);
				convertiblePairs.remove(c2);
			}
			objectAccessFactoryMap.remove(type);
		} else {
			if (convertiblePairs == null) {
				convertiblePairs = new LinkedHashSet<ConvertiblePair>();
			}
			convertiblePairs.add(c1);
			convertiblePairs.add(c2);
			objectAccessFactoryMap.put(type, factory);
		}
	}
}
