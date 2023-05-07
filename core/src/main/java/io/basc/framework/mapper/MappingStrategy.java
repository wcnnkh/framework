package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;

/**
 * 映射策略
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface MappingStrategy {

	<S extends Field, T extends Field> void transform(ObjectMapper mapper, Object source, TypeDescriptor sourceType,
			@Nullable MappingContext<? extends S> sourceContext, Mapping<? extends S> sourceMapping, Object target,
			TypeDescriptor targetType, @Nullable MappingContext<? extends T> targetContext,
			Mapping<? extends T> targetMapping) throws MappingException;

	<S extends Field, T extends Field> void transform(ObjectMapper mapper, Object source, TypeDescriptor sourceType,
			@Nullable MappingContext<? extends Field> sourceContext, Mapping<? extends S> sourceMapping,
			ObjectAccess targetAccess, @Nullable MappingContext<? extends T> targetContext) throws MappingException;

	<S extends Field, T extends Field> void transform(ObjectMapper mapper, ObjectAccess sourceAccess,
			@Nullable MappingContext<? extends S> sourceContext, Object target, TypeDescriptor targetType,
			@Nullable MappingContext<? extends T> targetContext, Mapping<? extends T> targetMapping)
			throws MappingException;

	<S extends Field, T extends Field> void transform(ObjectMapper mapper, ObjectAccess sourceAccess,
			@Nullable MappingContext<? extends S> sourceContext, ObjectAccess targetAccess,
			@Nullable MappingContext<? extends T> targetContext) throws MappingException;
}
