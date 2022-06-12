/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

/**
 * A service interface for type conversion. This is the entry point into the
 * convert system. Call {@link #convert(Object, Class)} to perform a thread-safe
 * type conversion using this system.
 */
public interface ConversionService extends Converter<Object, Object, ConversionException> {

	default boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
		return canConvert(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canConvert(@Nullable TypeDescriptor sourceType, Class<?> targetType) {
		return canConvert(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canConvert(@Nullable Class<?> sourceType, TypeDescriptor targetType) {
		return canConvert(sourceType == null ? null : TypeDescriptor.valueOf(sourceType), targetType);
	}

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the
	 * {@code targetType}. The TypeDescriptors provide additional context about the
	 * source and target locations where conversion would occur, often object fields
	 * or property locations.
	 * <p>
	 * If this method returns {@code true}, it means
	 * {@link #convert(Object, TypeDescriptor, TypeDescriptor)} is capable of
	 * converting an instance of {@code sourceType} to {@code targetType}.
	 * <p>
	 * Special note on collections, arrays, and maps types: For conversion between
	 * collection, array, and map types, this method will return {@code true} even
	 * though a convert invocation may still generate a {@link ConversionException}
	 * if the underlying elements are not convertible. Callers are expected to
	 * handle this exceptional case when working with collections and maps.
	 * 
	 * @param sourceType context about the source type to convert from (may be
	 *                   {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed between the source and
	 *         target types, {@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
}
