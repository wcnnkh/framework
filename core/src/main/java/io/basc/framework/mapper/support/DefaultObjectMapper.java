package io.basc.framework.mapper.support;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFactory;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.filter.FilterableMappingStrategy;
import io.basc.framework.mapper.filter.MappingStrategyFilters;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.value.PropertyFactory;

public class DefaultObjectMapper extends ConversionFactory<Object, ConversionException>
		implements ObjectMapper, ConditionalConversionService, ConversionServiceAware {
	private final Map<Class<?>, ObjectAccessFactory<?>> objectAccessFactoryMap = new TreeMap<>(TypeComparator.DEFAULT);
	private final Map<Class<?>, Mapping<? extends Field>> mappingMap = new ConcurrentHashMap<>();
	private Set<ConvertiblePair> convertiblePairs;
	private final MappingStrategyFilters filters = new MappingStrategyFilters();
	private final DefaultMappingStrategy mappingStrategy = new DefaultMappingStrategy();

	public DefaultObjectMapper() {
		registerObjectAccessFactory(PropertyFactory.class, (s, e) -> new PropertyFactoryAccess(s));
		registerObjectAccessFactory(Map.class, (s, e) -> new MapAccess(s, e, mappingStrategy.getConversionService()));
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		boolean b = !canDirectlyConvert(sourceType, targetType)
				&& ConditionalConversionService.super.canConvert(sourceType, targetType)
				&& (isEntity(targetType) || isEntity(sourceType));
		return b;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertiblePairs == null ? Collections.emptySet() : convertiblePairs;
	}

	public MappingStrategyFilters getFilters() {
		return filters;
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
		return new FilterableMappingStrategy(getFilters(), getMappingStrategy());
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

	@Override
	public void setConversionService(ConversionService conversionService) {
		mappingStrategy.setConversionService(conversionService);
	}

	public ConversionService getConversionService() {
		return mappingStrategy.getConversionService();
	}
}
