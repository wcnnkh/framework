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
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingContext;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.filter.FilterableMappingStrategy;
import io.basc.framework.mapper.filter.MappingStrategyFilter;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.value.PropertyFactory;

public class DefaultObjectMapper extends DefaultConversionService
		implements ObjectMapper, ConditionalConversionService {
	private Set<ConvertiblePair> convertiblePairs;
	private final ServiceRegistry<MappingStrategyFilter> filterRegistry = new ServiceRegistry<>();
	@Nullable
	private MappingFactory mappingFactory;
	private final Map<Class<?>, Mapping<? extends FieldDescriptor>> mappingMap = new ConcurrentHashMap<>();
	private final DefaultMappingStrategy mappingStrategy = new DefaultMappingStrategy();
	private final Map<Class<?>, ObjectAccessFactory<?>> objectAccessFactoryMap = new TreeMap<>(TypeComparator.DEFAULT);

	public DefaultObjectMapper() {
		registerObjectAccessFactory(PropertyFactory.class, (s, e) -> new PropertyFactoryAccess(s));
		registerObjectAccessFactory(Map.class, (s, e) -> new MapAccess(s, e, mappingStrategy.getConversionService()));
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
	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertiblePairs == null ? Collections.emptySet() : convertiblePairs;
	}

	public ServiceRegistry<MappingStrategyFilter> getFilterRegistry() {
		return filterRegistry;
	}

	@Override
	public Mapping<? extends FieldDescriptor> getMapping(Class<?> entityClass) {
		Mapping<? extends FieldDescriptor> structure = mappingMap.get(entityClass);
		if (structure == null) {
			synchronized (this) {
				structure = mappingMap.get(entityClass);
				if (structure == null) {
					structure = mappingFactory.getMapping(entityClass);
					mappingMap.put(entityClass, structure);
				}
			}
		}
		return structure;
	}

	public MappingFactory getMappingFactory() {
		return mappingFactory;
	}

	public DefaultMappingStrategy getMappingStrategy() {
		return mappingStrategy;
	}

	@Override
	public MappingStrategy getMappingStrategy(TypeDescriptor typeDescriptor) {
		return wrap(mappingStrategy);
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
	public void registerMapping(Class<?> entityClass, Mapping<? extends FieldDescriptor> mapping) {
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

	public void setMappingFactory(MappingFactory mappingFactory) {
		this.mappingFactory = mappingFactory;
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
	public void transform(ObjectAccess sourceAccess, MappingContext sourceContext, Object target,
			TypeDescriptor targetType, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		ObjectMapper.super.transform(sourceAccess, sourceContext, target, targetType, targetContext,
				wrap(mappingStrategy));
	}

	@Override
	public <S extends FieldDescriptor, T extends FieldDescriptor> void transform(Object source, TypeDescriptor sourceType,
			MappingContext sourceContext, Mapping<? extends S> sourceMapping, Object target, TypeDescriptor targetType,
			MappingContext targetContext, Mapping<? extends T> targetMapping, MappingStrategy strategy)
			throws MappingException {
		ObjectMapper.super.transform(source, sourceType, sourceContext, sourceMapping, target, targetType,
				targetContext, targetMapping, wrap(strategy));
	}

	@Override
	public <T extends FieldDescriptor> void transform(Object source, TypeDescriptor sourceType, MappingContext sourceContext,
			Mapping<? extends T> sourceMapping, ObjectAccess targetAccess, MappingContext targetContext,
			MappingStrategy strategy) throws MappingException {
		ObjectMapper.super.transform(source, sourceType, sourceContext, sourceMapping, targetAccess, targetContext,
				wrap(strategy));
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, MappingContext sourceContext, Object target,
			TypeDescriptor targetType, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		ObjectMapper.super.transform(source, sourceType, sourceContext, target, targetType, targetContext,
				wrap(mappingStrategy));
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, MappingContext sourceContext,
			ObjectAccess targetAccess, MappingContext targetContext, MappingStrategy mappingStrategy)
			throws MappingException {
		ObjectMapper.super.transform(source, sourceType, sourceContext, targetAccess, targetContext,
				wrap(mappingStrategy));
	}

	@Override
	public <T extends FieldDescriptor> void transform(ObjectAccess sourceAccess, MappingContext sourceContext, Object target,
			TypeDescriptor targetType, MappingContext targetContext, Mapping<? extends T> targetMapping,
			MappingStrategy strategy) throws MappingException {
		ObjectMapper.super.transform(sourceAccess, sourceContext, target, targetType, targetContext, targetMapping,
				wrap(strategy));
	}

	@Override
	public void transform(ObjectAccess sourceAccess, MappingContext sourceContext, ObjectAccess targetAccess,
			MappingContext targetContext, MappingStrategy strategy) throws MappingException {
		ObjectMapper.super.transform(sourceAccess, sourceContext, targetAccess, targetContext, wrap(strategy));
	}

	@Override
	public final void transform(Object source, Object target, MappingStrategy mappingStrategy) {
		ObjectMapper.super.transform(source, target, mappingStrategy);
	}

	@Override
	public final void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			MappingStrategy mappingStrategy) {
		ObjectMapper.super.transform(source, sourceType, target, targetType, mappingStrategy);
	}

	protected MappingStrategy wrap(MappingStrategy mappingStrategy) {
		if (mappingStrategy instanceof InternalMappingStrategy) {
			if (((InternalMappingStrategy) mappingStrategy).id.equals(this.id)) {
				return mappingStrategy;
			}
		}
		return new InternalMappingStrategy(mappingStrategy);
	}

	private final String id = XUtils.getUUID();

	private class InternalMappingStrategy extends FilterableMappingStrategy {
		private final String id = DefaultObjectMapper.this.id;

		public InternalMappingStrategy(MappingStrategy dottomlessMappingStrategy) {
			super(filterRegistry.getServices(), dottomlessMappingStrategy);
		}
	}
}
