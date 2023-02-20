package io.basc.framework.convert;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.comparator.TypeComparator;

public class ConversionFactory<S, E extends Throwable> implements ReversibleMapperFactory<S, E> {
	private Map<Class<?>, Converter<S, ?, ? extends E>> converterMap;
	private Map<Class<?>, Inverter<?, ? extends S, ? extends E>> inverterMap;
	private Map<Class<?>, Mapper<S, ?, ? extends E>> mapperMap;
	private Map<Class<?>, ReverseTransformer<?, ? extends S, ? extends E>> reverseTransformerMap;
	private Map<Class<?>, ReversibleConverter<S, ?, ? extends E>> reversibleConverterMap;
	private Map<Class<?>, ReversibleMapper<S, ?, ? extends E>> reversibleMapperMapper;
	private Map<Class<?>, ReversibleTransformer<S, ?, ? extends E>> reversibleTransformerMap;
	private Map<Class<?>, Transformer<S, ?, ? extends E>> transformerMap;

	@Override
	public final <R> R convert(S source, Class<? extends R> targetType) throws E {
		return ReversibleMapperFactory.super.convert(source, targetType);
	}

	@Override
	public final <R> R convert(S source, Class<? extends S> sourceType, Class<? extends R> targetType) throws E {
		return ReversibleMapperFactory.super.convert(source, sourceType, targetType);
	}

	@Override
	public final <R> R convert(S source, Class<? extends S> sourceType, TypeDescriptor targetType) throws E {
		return ReversibleMapperFactory.super.convert(source, sourceType, targetType);
	}

	@Override
	public final Object convert(S source, TypeDescriptor targetType) throws E {
		return ReversibleMapperFactory.super.convert(source, targetType);
	}

	@Override
	public final <R> R convert(S source, TypeDescriptor sourceType, Class<? extends R> targetType) throws E {
		return ReversibleMapperFactory.super.convert(source, sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	private <T> T get(Class<?> type, Map<Class<?>, ?> sourceMap) {
		if (sourceMap == null || sourceMap.isEmpty()) {
			return null;
		}

		Object target = sourceMap.get(type);
		if (target == null) {
			for (Entry<Class<?>, ?> entry : sourceMap.entrySet()) {
				if (ClassUtils.isAssignable(entry.getKey(), type)) {
					target = entry.getValue();
					if (target != null) {
						break;
					}
				}
			}
		}
		return (T) target;
	}

	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> type) {
		Converter<S, T, E> converter = get(type, converterMap);
		if (converter != null) {
			return converter;
		}

		converter = getReversibleConverter(type);
		if (converter != null) {
			return converter;
		}

		return getMapper(type);
	}

	@Override
	public <R> Inverter<R, S, E> getInverter(Class<? extends R> type) {
		Inverter<R, S, E> inverter = get(type, inverterMap);
		if (inverter != null) {
			return inverter;
		}
		return getReversibleConverter(type);
	}

	@Override
	public <T> Mapper<S, T, E> getMapper(Class<? extends T> type) {
		Mapper<S, T, E> mapper = get(type, mapperMap);
		if (mapper != null) {
			return mapper;
		}
		return getReversibleMapper(type);
	}

	@Override
	public <R> ReverseTransformer<R, S, E> getReverseTransformer(Class<? extends R> type) {
		ReverseTransformer<R, S, E> reverseTransformer = get(type, reverseTransformerMap);
		if (reverseTransformer != null) {
			return reverseTransformer;
		}

		return getReversibleTransformer(type);
	}

	@Override
	public <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type) {
		ReversibleConverter<S, T, E> reversibleConverter = get(type, reversibleConverterMap);
		if (reversibleConverter != null) {
			return reversibleConverter;
		}
		return getReversibleMapper(type);
	}

	@Override
	public <T> ReversibleMapper<S, T, E> getReversibleMapper(Class<? extends T> type) {
		return get(type, reversibleMapperMapper);
	}

	@Override
	public <R> ReversibleTransformer<S, R, E> getReversibleTransformer(Class<? extends R> type) {
		ReversibleTransformer<S, R, E> reversibleTransformer = get(type, reversibleTransformerMap);
		if (reversibleTransformer != null) {
			return reversibleTransformer;
		}
		return getReversibleMapper(type);
	}

	@Override
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> type) {
		Transformer<S, T, E> transformer = get(type, transformerMap);
		if (transformer != null) {
			return transformer;
		}

		transformer = getReversibleTransformer(type);
		if (transformer != null) {
			return transformer;
		}

		return getMapper(type);
	}

	@Override
	public final <R extends S> R invert(Object source, Class<? extends Object> sourceType,
			Class<? extends R> targetType) throws E {
		return ReversibleMapperFactory.super.invert(source, sourceType, targetType);
	}

	@Override
	public final <R extends S> R invert(Object source, Class<? extends Object> sourceType, TypeDescriptor targetType)
			throws E {
		return ReversibleMapperFactory.super.invert(source, sourceType, targetType);
	}

	@Override
	public final <R extends S> R invert(Object source, Class<? extends R> targetType) throws E {
		return ReversibleMapperFactory.super.invert(source, targetType);
	}

	@Override
	public final S invert(Object source, TypeDescriptor targetType) throws E {
		return ReversibleMapperFactory.super.invert(source, targetType);
	}

	@Override
	public final <R extends S> R invert(Object source, TypeDescriptor sourceType, Class<? extends R> targetType)
			throws E {
		return ReversibleMapperFactory.super.invert(source, sourceType, targetType);
	}

	private <T> Map<Class<?>, T> register(Class<?> type, T conversion, Map<Class<?>, T> sourceMap) {
		Assert.requiredArgument(type != null, "type");
		if (conversion == null) {
			if (sourceMap != null) {
				sourceMap.remove(type);
			}
		} else {
			if (sourceMap == null) {
				sourceMap = new TreeMap<>(TypeComparator.DEFAULT);
			}

			sourceMap.put(type, conversion);
		}
		return sourceMap;
	}

	@Override
	public <T> void registerConverter(Class<T> type, Converter<S, ? extends T, ? extends E> converter) {
	  this.converterMap =	register(type, converter, converterMap);
	}

	@Override
	public <R> void registerInverter(Class<R> type, Inverter<R, ? extends S, ? extends E> inverter) {
		this.inverterMap =  register(type, inverter, inverterMap);
	}

	@Override
	public <T> void registerMapper(Class<T> type, Mapper<S, T, ? extends E> mapper) {
		this.mapperMap =  register(type, mapper, mapperMap);
	}

	@Override
	public <R> void registerReverseTransformer(Class<R> type,
			ReverseTransformer<R, S, ? extends E> reverseTransformer) {
		this.reverseTransformerMap = register(type, reverseTransformer, reverseTransformerMap);
	}

	@Override
	public <T> void registerReversibleConverter(Class<T> type, ReversibleConverter<S, T, ? extends E> converter) {
		this.reversibleConverterMap =  register(type, converter, reversibleConverterMap);
	}

	@Override
	public <T> void registerReversibleMapper(Class<T> type, ReversibleMapper<S, T, ? extends E> mapper) {
		this.reversibleMapperMapper = register(type, mapper, reversibleMapperMapper);
	}

	@Override
	public <R> void registerReversibleTransformer(Class<R> type, ReversibleTransformer<S, R, ? extends E> reverser) {
		this.reversibleTransformerMap = register(type, reverser, reversibleTransformerMap);
	}

	@Override
	public <T> void registerTransformer(Class<T> type, Transformer<S, T, ? extends E> transformer) {
		this.transformerMap =  register(type, transformer, transformerMap);
	}

	@Override
	public final void reverseTransform(Object source, Class<? extends Object> sourceType, S target) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, sourceType, target);
	}

	@Override
	public final void reverseTransform(Object source, Class<? extends Object> sourceType, S target,
			Class<? extends S> targetType) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, sourceType, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, Class<? extends Object> sourceType, S target,
			TypeDescriptor targetType) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, sourceType, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, S target) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, target);
	}

	@Override
	public final void reverseTransform(Object source, S target, Class<? extends S> targetType) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, S target, TypeDescriptor targetType) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, TypeDescriptor sourceType, S target) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, sourceType, target);
	}

	@Override
	public final void reverseTransform(Object source, TypeDescriptor sourceType, S target,
			Class<? extends S> targetType) throws E {
		ReversibleMapperFactory.super.reverseTransform(source, sourceType, target, targetType);
	}

	@Override
	public final void transform(S source, Class<? extends S> sourceType, Object target) throws E {
		ReversibleMapperFactory.super.transform(source, sourceType, target);
	}

	@Override
	public final void transform(S source, Class<? extends S> sourceType, Object target,
			Class<? extends Object> targetType) throws E {
		ReversibleMapperFactory.super.transform(source, sourceType, target, targetType);
	}

	@Override
	public final void transform(S source, Class<? extends S> sourceType, Object target, TypeDescriptor targetType)
			throws E {
		ReversibleMapperFactory.super.transform(source, sourceType, target, targetType);
	}

	@Override
	public final void transform(S source, Object target) throws E {
		ReversibleMapperFactory.super.transform(source, target);
	}

	@Override
	public final void transform(S source, Object target, Class<? extends Object> targetType) throws E {
		ReversibleMapperFactory.super.transform(source, target, targetType);
	}

	@Override
	public final void transform(S source, Object target, TypeDescriptor targetType) throws E {
		ReversibleMapperFactory.super.transform(source, target, targetType);
	}

	@Override
	public final void transform(S source, TypeDescriptor sourceType, Object target) throws E {
		ReversibleMapperFactory.super.transform(source, sourceType, target);
	}

	@Override
	public final void transform(S source, TypeDescriptor sourceType, Object target, Class<? extends Object> targetType)
			throws E {
		ReversibleMapperFactory.super.transform(source, sourceType, target, targetType);
	}
}
