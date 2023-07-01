package io.basc.framework.convert.support;

import java.util.TreeMap;

import io.basc.framework.convert.Mapper;
import io.basc.framework.convert.MapperRegistry;
import io.basc.framework.convert.ReverseTransformer;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.ReversibleTransformer;
import io.basc.framework.convert.Transformer;

public class DefautMapperRegistry<S, E extends Throwable> extends DefaultReversibleConverterRegistry<S, E>
		implements MapperRegistry<S, E> {
	private TreeMap<Class<?>, ReversibleTransformer<? super S, ?, ? extends E>> reversibleTransformerMap;
	private TreeMap<Class<?>, Transformer<? super S, ?, ? extends E>> transformerMap;
	private TreeMap<Class<?>, ReverseTransformer<?, ? super S, ? extends E>> reverseTransformerMap;
	private TreeMap<Class<?>, Mapper<S, ?, ? extends E>> mapperMap;

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> type) {
		ReversibleTransformer<S, T, E> reversibleTransformer = (ReversibleTransformer<S, T, E>) get(type,
				reversibleTransformerMap);
		if (reversibleTransformer == null) {
			reversibleTransformer = getMapper(type);
		}
		return reversibleTransformer;
	}

	@Override
	public <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type) {
		ReversibleConverter<S, T, E> reversibleConverter = super.getReversibleConverter(type);
		if (reversibleConverter == null) {
			reversibleConverter = getMapper(type);
		}
		return reversibleConverter;
	}

	@Override
	public <T> void registerReversibleTransformer(Class<T> type,
			ReversibleTransformer<? super S, ? super T, ? extends E> transformer) {
		this.reversibleTransformerMap = register(type, transformer, reversibleTransformerMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> type) {
		Transformer<S, T, E> transformer = (Transformer<S, T, E>) get(type, transformerMap);
		if (transformer == null) {
			transformer = getReversibleTransformer(type);
		}
		return transformer;
	}

	@Override
	public <T> void registerTransformer(Class<T> type, Transformer<? super S, ? super T, ? extends E> transformer) {
		this.transformerMap = register(type, transformer, transformerMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReverseTransformer<T, S, E> getReverseTransformer(Class<? extends T> type) {
		ReverseTransformer<T, S, E> reverseTransformer = (ReverseTransformer<T, S, E>) get(type, reverseTransformerMap);
		if (reverseTransformer == null) {
			reverseTransformer = getReversibleTransformer(type);
		}
		return reverseTransformer;
	}

	@Override
	public <T> void registerReverseTransformer(Class<T> type,
			ReverseTransformer<? super T, ? super S, ? extends E> transformer) {
		this.reverseTransformerMap = register(type, transformer, reverseTransformerMap);
	}

	@Override
	public <T> void registerMapper(Class<? extends T> type, Mapper<S, T, ? extends E> mapper) {
		this.mapperMap = register(type, mapper, mapperMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Mapper<S, T, E> getMapper(Class<? extends T> type) {
		return (Mapper<S, T, E>) get(type, mapperMap);
	}
}
