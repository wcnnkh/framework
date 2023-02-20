package io.basc.framework.mapper;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.basc.framework.convert.ConversionFactory;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Processor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class DefaultObjectMapper<S, E extends Throwable> extends ConversionFactory<S, E>
		implements ObjectMapper<S, E>, ConversionServiceAware {
	private static final ThreadLocal<Integer> ENTITY_NESTING_DEPTH = new NamedThreadLocal<Integer>(
			DefaultObjectMapper.class.getName() + "#ENTITY_NESTING_DEPTH") {
		protected Integer initialValue() {
			return 0;
		};
	};

	private final ObjectMapperContext context = new ObjectMapperContext();
	private final Map<Class<?>, ObjectAccessFactory<?, ? extends E>> objectAccessFactoryMap = new TreeMap<>(
			TypeComparator.DEFAULT);
	private final Map<Class<?>, Structure<? extends Field>> structureMap = new ConcurrentHashMap<>();

	public DefaultObjectMapper() {
		registerObjectAccessFactory(PropertyFactory.class, (s, e) -> new PropertyFactoryAccess<>(s));
		registerObjectAccessFactory(Map.class, (s, e) -> new AnyMapAccess<>(s, e, getConversionService()));
	}

	protected boolean accept(Field sourceField, FieldDescriptor fieldDescriptor, ObjectMapperContext context) {
		if (context.getFilter() != null) {
			if (!context.getFilter().test(sourceField)) {
				return false;
			}
		}

		if (!context.getIgnoreAnnotationNameMatcher().isEmpty()) {
			// 如果字段上存在beans下的注解应该忽略此字段
			for (Annotation annotation : fieldDescriptor.getAnnotations()) {
				if (context.getIgnoreAnnotationNameMatcher().get(annotation.annotationType().getName()).orElse(false)) {
					return false;
				}
			}
		}

		if (StringUtils.isNotEmpty(context.getNamePrefix()) || !context.getIgnoreNameMatcher().isEmpty()) {
			Collection<String> names = sourceField.getNames(context);
			if (names != null) {
				boolean accept = false;
				for (String name : names) {
					if (name.startsWith(context.getNamePrefix())
							&& !context.getIgnoreNameMatcher().get(name).orElse(false)) {
						accept = true;
						break;
					}
				}

				if (!accept) {
					return false;
				}
			}
		}
		return true;
	}

	protected void appendMapProperty(Map<String, Object> valueMap, String prefix, ObjectAccess<E> objectAccess,
			ObjectMapperContext context) throws E {
		Enumeration<String> keys = objectAccess.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (StringUtils.isNotEmpty(prefix) && (key.equals(prefix) || valueMap.containsKey(key))) {
				continue;
			}

			if (key.startsWith(prefix)) {
				Value value = objectAccess.get(key);
				if (value == null) {
					continue;
				}

				if (context.isIgnoreNull() && !value.isPresent()) {
					continue;
				}

				valueMap.put(StringUtils.isEmpty(prefix) ? key
						: key.substring(prefix.length() + (prefix.endsWith(context.getNameConnector()) ? 0
								: context.getNameConnector().length())),
						value.get());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType, Field parentField,
			ObjectMapperContext context) throws E {
		R target = (R) newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(source, sourceType, target, targetType, parentField, context);
		return target;
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(ObjectAccess<E> sourceAccess, TypeDescriptor targetType, Field parentField,
			ObjectMapperContext context) throws E {
		R target = (R) newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(sourceAccess, target, targetType, parentField, context);
		return target;
	}

	public <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType, @Nullable Field parentField)
			throws E {
		return convert(source, sourceType, targetType, parentField, this.context);
	}

	@Override
	public Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		return convert(source, sourceType, targetType, targetStructure, this.context);
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure, ObjectMapperContext context) throws E {
		R target = (R) newInstance(targetType);
		if (target == null) {
			return null;
		}
		transform(source, sourceType, target, targetType, targetStructure, context);
		return target;
	}

	@Override
	public <T> void copy(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			Iterator<? extends Field> properties) throws E {
		copy(source, sourceType, target, targetType, properties, this.context);
	}

	public <T> void copy(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			Iterator<? extends Field> properties, ObjectMapperContext context) throws E {
		while (properties.hasNext()) {
			Field field = properties.next();
			if (field.isSupportGetter() && field.isSupportSetter()) {
				if (!accept(field, field.getSetter(), context)) {
					continue;
				}

				Parameter value = field.getParameter(source);
				if (value == null || !value.isPresent()) {
					continue;
				}

				field.set(target, value);
			}
		}
	}

	@Override
	public <T> void copy(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			Structure<? extends Field> structure) throws E {
		copy(source, sourceType, target, targetType, structure, this.context);
	}

	public <T> void copy(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			Structure<? extends Field> structure, ObjectMapperContext context) throws E {
		Iterator<? extends Structure<? extends Field>> iterator = structure.pages().iterator();
		while (iterator.hasNext()) {
			Structure<? extends Field> useStructure = iterator.next();
			copy(source, sourceType, target, targetType, useStructure.iterator(), context);
		}
	}

	public final ObjectMapperContext getContext() {
		return context;
	}

	protected ObjectMapperContext getContext(TypeDescriptor targetType, ObjectMapperContext parent) {
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
	public Structure<? extends Field> getStructure(Class<?> entityClass) {
		Structure<? extends Field> structure = structureMap.get(entityClass);
		if (structure == null) {
			structure = ObjectMapper.super.getStructure(entityClass);
		}
		return structure;
	}

	private Processor<Field, Parameter, E> getValueProcessor(ObjectMapperContext context, ObjectAccess<E> objectAccess)
			throws E {
		return (p) -> {
			Collection<String> names = p.getNames(context);
			if (context.getLogger().isTraceEnabled()) {
				context.getLogger().trace(p.getName() + " - " + names);
			}

			for (String name : names) {
				Parameter value = objectAccess.get(name);
				if (value == null || !value.isPresent()) {
					continue;
				}
				return value;
			}

			if (Map.class.isAssignableFrom(p.getSetter().getType())) {
				Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
				for (String name : names) {
					appendMapProperty(valueMap, name + context.getNameConnector(), objectAccess, context);
				}
				if (!CollectionUtils.isEmpty(valueMap)) {
					Value value = Value.of(valueMap,
							TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class));
					return new Parameter(
							context.getNamePrefix() == null ? p.getName() : (context.getNamePrefix() + p.getName()),
							value);
				}
			}
			return null;
		};
	}

	public <R extends S> R invert(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			TypeDescriptor targetType) throws E {
		return invert(source, sourceType, sourceStructure, targetType, this.context);
	}

	@SuppressWarnings("unchecked")
	public <R extends S> R invert(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			TypeDescriptor targetType, ObjectMapperContext context) throws E {
		R target = (R) newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(source, sourceType, sourceStructure, target, targetType, context);
		return target;
	}

	protected boolean isEntity(TypeDescriptor sourceType, Field field, ParameterDescriptor parameter,
			ObjectMapperContext context) {
		return isEntity(parameter.getType())
				|| context.getEntityTypeMatcher().get(parameter.getType().getName()).orElse(false);
	}

	@Override
	public boolean isStructureRegistred(Class<?> entityClass) {
		return structureMap.containsKey(entityClass);
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
	public void registerStructure(Class<?> entityClass, Structure<? extends Field> structure) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (structure == null) {
			structureMap.remove(entityClass);
		} else {
			structureMap.put(entityClass, structure);
		}
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.context.setConversionService(conversionService);
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Iterator<? extends Field> sourceProperties,
			Object target, TypeDescriptor targetType, Iterator<? extends Field> targetProperties) throws E {
		transform(source, sourceType, sourceProperties, target, targetType, targetProperties, this.context);
	}

	public void transform(Object source, TypeDescriptor sourceType, Iterator<? extends Field> sourceProperties,
			Object target, TypeDescriptor targetType, Iterator<? extends Field> targetProperties,
			ObjectMapperContext context) throws E {
		Comparator<FieldDescriptor> comparator = (left, right) -> {
			if (left.getDeclaringClass() == right.getDeclaringClass()) {
				if (left.getType() == right.getType()) {
					return 0;
				}

				return right.getType().isAssignableFrom(left.getType()) ? 1 : -1;
			}
			return right.getDeclaringClass().isAssignableFrom(left.getDeclaringClass()) ? 1 : -1;
		};

		try {
			List<Field> targetFields = XUtils.stream(targetProperties)
					.filter((e) -> e.isSupportSetter() && accept(e, e.getSetter(), context))
					.sorted((left, right) -> comparator.compare(left.getGetter(), right.getGetter()))
					.collect(Collectors.toList());
			List<Field> sourceFields = XUtils.stream(sourceProperties)
					.filter((e) -> e.isSupportGetter() && accept(e, e.getGetter(), context))
					.sorted((left, right) -> comparator.compare(left.getSetter(), right.getSetter()))
					.collect(Collectors.toList());
			for (Field sourceField : sourceFields) {
				if (targetFields.isEmpty()) {
					break;
				}

				Iterator<Field> iterator = targetFields.iterator();
				while (iterator.hasNext()) {
					Field targetField = iterator.next();
					if (sourceField.test(targetField)) {
						if (!accept(sourceField, targetField.getSetter(), context)) {
							continue;
						}

						Value value;
						if ((context.getEntityNestingMaxiumDepth() < 0
								|| ENTITY_NESTING_DEPTH.get() < context.getEntityNestingMaxiumDepth())
								&& isEntity(targetType, targetField, targetField.getSetter(), context)) {
							ENTITY_NESTING_DEPTH.set(ENTITY_NESTING_DEPTH.get() + 1);
							TypeDescriptor entityType = new TypeDescriptor(targetField.getSetter());
							ObjectMapperContext contextUse = getContext(entityType, context);
							Object entity = convert(source, sourceType, entityType, targetField, contextUse);
							value = Value.of(entity, entityType, context.getConversionService());
						} else {
							Parameter parameter = sourceField.getParameter(source);
							if (parameter != null) {
								parameter.setConverter(context.getConversionService());
							}
							value = parameter;
						}

						if (value == null) {
							break;
						}

						if (context.isIgnoreNull() && !value.isPresent()) {
							break;
						}

						targetField.set(target, value);
						iterator.remove();
						break;
					}
				}
			}
		} finally {
			ENTITY_NESTING_DEPTH.remove();
		}
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Iterator<? extends Field> sourceProperties,
			ObjectAccess<? extends E> targetAccess) throws E {
		transform(source, sourceType, sourceProperties, targetAccess, this.context);
	}

	public void transform(Object source, TypeDescriptor sourceType, Iterator<? extends Field> sourceProperties,
			ObjectAccess<? extends E> targetAccess, ObjectMapperContext context) throws E {
		while (sourceProperties.hasNext()) {
			Field field = sourceProperties.next();
			if (!field.isSupportGetter()) {
				continue;
			}

			if (!accept(field, field.getGetter(), context)) {
				continue;
			}

			Parameter parameter = field.getParameter(source);
			if (parameter == null) {
				continue;
			}

			if (context.isIgnoreNull() && !parameter.isPresent()) {
				continue;
			}

			targetAccess.set(parameter);
		}
	}

	public void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Field parentField, ObjectMapperContext context) throws E {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, context);
			return;
		}

		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, getObjectAccess(target, targetType), context);
			return;
		}

		Structure<? extends Field> targetStructure = getStructure(targetType.getType());
		if (parentField != null) {
			targetStructure = targetStructure.setParentField(parentField);
		}
		if (targetType.getType() == sourceType.getType()
				|| sourceType.getType().isAssignableFrom(targetType.getType())) {
			copy(source, sourceType, target, targetType, targetStructure, context);
			return;
		}

		transform(source, sourceType, getStructure(sourceType.getType()), target, targetType, targetStructure, context);
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		transform(source, sourceType, target, targetType, targetStructure);
	}

	public void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure, ObjectMapperContext context) throws E {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, targetStructure, context);
		} else {
			transform(source, sourceType, getStructure(sourceType.getType()), target, targetType, targetStructure,
					context);
		}
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, ObjectAccess<? extends E> targetAccess) throws E {
		transform(source, sourceType, targetAccess, this.context);
	}

	public void transform(Object source, TypeDescriptor sourceType, ObjectAccess<? extends E> targetAccess,
			ObjectMapperContext context) throws E {
		if (source == null) {
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), targetAccess, context);
			return;
		}

		Structure<? extends Field> sourceStructure = getStructure(sourceType.getType());
		transform(source, sourceType, sourceStructure, targetAccess, context);
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			Object target, TypeDescriptor targetType) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, sourceStructure, getObjectAccess(target, targetType));
		} else {
			transform(source, sourceType, sourceStructure, target, targetType, getStructure(targetType.getType()));
		}
	}

	public void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			Object target, TypeDescriptor targetType, ObjectMapperContext context) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, sourceStructure, getObjectAccess(target, targetType), context);
		} else {
			transform(source, sourceType, sourceStructure, target, targetType, getStructure(targetType.getType()),
					context);
		}
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			Object target, TypeDescriptor targetType, Structure<? extends Field> targetStructure) throws E {
		transform(source, sourceType, sourceStructure, target, targetType, targetStructure, this.context);
	}

	public void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			Object target, TypeDescriptor targetType, Structure<? extends Field> targetStructure,
			ObjectMapperContext context) throws E {
		transform(source, sourceType, sourceStructure.all().stream().iterator(), target, targetType,
				targetStructure.all().stream().iterator(), context);
	}

	@Override
	public void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			ObjectAccess<? extends E> targetAccess) throws E {
		transform(source, sourceType, sourceStructure, targetAccess, this.context);
	}

	public void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			ObjectAccess<? extends E> targetAccess, ObjectMapperContext context) throws E {
		Iterator<? extends Structure<? extends Field>> iterator = sourceStructure.pages().iterator();
		while (iterator.hasNext()) {
			Structure<? extends Field> structure = iterator.next();
			transform(source, sourceType, structure.iterator(), targetAccess, context);
		}
	}

	@Override
	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType) throws E {
		transform(sourceAccess, target, targetType, this.context);
	}

	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType, Field parentField,
			ObjectMapperContext context) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(sourceAccess, getObjectAccess(target, targetType), context);
		} else {
			Structure<? extends Field> targetStructure = getStructure(targetType.getType());
			if (parentField != null) {
				targetStructure = targetStructure.setParentField(parentField);
			}
			transform(sourceAccess, target, targetType, targetStructure, context);
		}
	}

	@Override
	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Iterator<? extends Field> targetProperties) throws E {
		transform(sourceAccess, target, targetType, targetProperties, this.context);
	}

	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Iterator<? extends Field> targetProperties, ObjectMapperContext context) throws E {
		transform(sourceAccess, target, targetType, targetProperties, context,
				getValueProcessor(context, sourceAccess));
	}

	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Iterator<? extends Field> targetProperties, ObjectMapperContext context,
			Processor<Field, Parameter, E> valueProcessor) throws E {
		try {
			while (targetProperties.hasNext()) {
				Field field = targetProperties.next();
				if (!field.isSupportSetter()) {
					continue;
				}

				if (!accept(field, field.getSetter(), context)) {
					continue;
				}

				Parameter parameter;
				if ((context.getEntityNestingMaxiumDepth() < 0
						|| ENTITY_NESTING_DEPTH.get() < context.getEntityNestingMaxiumDepth())
						&& isEntity(targetType, field, field.getSetter(), context)) {
					ENTITY_NESTING_DEPTH.set(ENTITY_NESTING_DEPTH.get() + 1);
					TypeDescriptor entityType = new TypeDescriptor(field.getSetter());
					ObjectMapperContext contextToUse = getContext(targetType, context);
					Object entity = convert(sourceAccess, entityType, field, contextToUse);
					parameter = new Parameter(field.getName(), entity, entityType);
				} else {
					parameter = valueProcessor.process(field);
				}

				if (parameter == null) {
					continue;
				}

				if (context.isIgnoreNull() && !parameter.isPresent()) {
					continue;
				}

				parameter.setConverter(context.getConversionService());
				field.set(target, parameter);
			}
		} finally {
			ENTITY_NESTING_DEPTH.remove();
		}
	}

	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			ObjectMapperContext context) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(sourceAccess, getObjectAccess(target, targetType), context);
			return;
		}

		Structure<? extends Field> targetStructure = getStructure(targetType.getType());
		transform(sourceAccess, target, targetType, targetStructure, context);
	}

	@Override
	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		transform(sourceAccess, target, targetType, targetStructure, this.context);
	}

	public void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure, ObjectMapperContext context) throws E {
		Iterator<? extends Structure<? extends Field>> iterator = targetStructure.pages().iterator();
		ObjectMapperContext useContext = context;
		while (iterator.hasNext()) {
			Structure<? extends Field> structure = iterator.next();
			useContext = getContext(targetType.convert(ResolvableType.forClass(structure.getSourceClass())),
					useContext);
			transform(sourceAccess, target, targetType, structure.iterator(), useContext);
		}
	}

	public void transform(ObjectAccess<E> sourceAccess, ObjectAccess<? extends E> targetAccess) throws E {
		transform(sourceAccess, targetAccess, this.context);
	}

	public void transform(ObjectAccess<E> sourceAccess, ObjectAccess<? extends E> targetAccess,
			ObjectMapperContext context) throws E {
		sourceAccess.copyByPrefix(targetAccess, context.getNamePrefix());
	}
}
