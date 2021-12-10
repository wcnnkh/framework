/*
 * Copyright 2002-2020 the original author or authors.
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

package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;

/**
 * {@link InvocationHandler} for an {@link Annotation} that Spring has
 * <em>synthesized</em> (i.e. wrapped in a dynamic proxy) with additional
 * functionality such as attribute alias handling.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/annotation/SynthesizedMergedAnnotationInvocationHandler.java
 * @param <A> the annotation type
 * @see Annotation
 * @see AnnotationUtils#synthesizeAnnotation(Annotation, AnnotatedElement)
 */
final class SynthesizedMergedAnnotationInvocationHandler<A extends Annotation> implements InvocationHandler {

	private final MergedAnnotation<?> annotation;

	private final Class<A> type;

	private final AttributeMethods attributes;

	private final Map<String, Object> valueCache = new ConcurrentHashMap<>(8);

	@Nullable
	private volatile Integer hashCode;

	@Nullable
	private volatile String string;

	private SynthesizedMergedAnnotationInvocationHandler(MergedAnnotation<A> annotation, Class<A> type) {
		Assert.notNull(annotation, "MergedAnnotation must not be null");
		Assert.notNull(type, "Type must not be null");
		Assert.isTrue(type.isAnnotation(), "Type must be an annotation");
		this.annotation = annotation;
		this.type = type;
		this.attributes = AttributeMethods.forAnnotationType(type);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		if (ReflectionUtils.isEqualsMethod(method)) {
			return annotationEquals(args[0]);
		}
		if (ReflectionUtils.isHashCodeMethod(method)) {
			return annotationHashCode();
		}
		if (ReflectionUtils.isToStringMethod(method)) {
			return annotationToString();
		}
		if (isAnnotationTypeMethod(method)) {
			return this.type;
		}
		if (this.attributes.indexOf(method.getName()) != -1) {
			return getAttributeValue(method);
		}
		throw new AnnotationConfigurationException(
				String.format("Method [%s] is unsupported for synthesized annotation type [%s]", method, this.type));
	}

	private boolean isAnnotationTypeMethod(Method method) {
		return (method.getName().equals("annotationType") && method.getParameterCount() == 0);
	}

	/**
	 * See {@link Annotation#equals(Object)} for a definition of the required
	 * algorithm.
	 * 
	 * @param other the other object to compare against
	 */
	private boolean annotationEquals(Object other) {
		if (this == other) {
			return true;
		}
		if (!this.type.isInstance(other)) {
			return false;
		}
		for (int i = 0; i < this.attributes.size(); i++) {
			Method attribute = this.attributes.get(i);
			Object thisValue = getAttributeValue(attribute);
			Object otherValue = ReflectionUtils.invokeMethod(attribute, other);
			if (!ObjectUtils.equals(thisValue, otherValue)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * See {@link Annotation#hashCode()} for a definition of the required algorithm.
	 */
	private int annotationHashCode() {
		Integer hashCode = this.hashCode;
		if (hashCode == null) {
			hashCode = computeHashCode();
			this.hashCode = hashCode;
		}
		return hashCode;
	}

	private Integer computeHashCode() {
		int hashCode = 0;
		for (int i = 0; i < this.attributes.size(); i++) {
			Method attribute = this.attributes.get(i);
			Object value = getAttributeValue(attribute);
			hashCode += (127 * attribute.getName().hashCode()) ^ getValueHashCode(value);
		}
		return hashCode;
	}

	private int getValueHashCode(Object value) {
		return ObjectUtils.hashCode(value, false);
	}

	private String annotationToString() {
		String string = this.string;
		if (string == null) {
			StringBuilder builder = new StringBuilder("@").append(this.type.getName()).append('(');
			for (int i = 0; i < this.attributes.size(); i++) {
				Method attribute = this.attributes.get(i);
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(attribute.getName());
				builder.append('=');
				builder.append(toString(getAttributeValue(attribute)));
			}
			builder.append(')');
			string = builder.toString();
			this.string = string;
		}
		return string;
	}

	private String toString(Object value) {
		if (value instanceof Class) {
			return ((Class<?>) value).getName();
		}
		return ObjectUtils.toString(value);
	}

	private Object getAttributeValue(Method method) {
		Object value = this.valueCache.computeIfAbsent(method.getName(), attributeName -> {
			Class<?> type = ClassUtils.resolvePrimitiveIfNecessary(method.getReturnType());
			return this.annotation.getValue(attributeName, type)
					.orElseThrow(() -> new NoSuchElementException("No value found for attribute named '" + attributeName
							+ "' in merged annotation " + this.annotation.getType().getName()));
		});

		// Clone non-empty arrays so that users cannot alter the contents of values in
		// our cache.
		if (value.getClass().isArray() && Array.getLength(value) > 0) {
			value = cloneArray(value);
		}

		return value;
	}

	/**
	 * Clone the provided array, ensuring that the original component type is
	 * retained.
	 * 
	 * @param array the array to clone
	 */
	private Object cloneArray(Object array) {
		return ArrayUtils.clone(array);
	}

	@SuppressWarnings("unchecked")
	static <A extends Annotation> A createProxy(MergedAnnotation<A> annotation, Class<A> type) {
		ClassLoader classLoader = type.getClassLoader();
		InvocationHandler handler = new SynthesizedMergedAnnotationInvocationHandler<>(annotation, type);
		Class<?>[] interfaces = isVisible(classLoader, SynthesizedAnnotation.class)
				? new Class<?>[] { type, SynthesizedAnnotation.class }
				: new Class<?>[] { type };
		return (A) Proxy.newProxyInstance(classLoader, interfaces, handler);
	}

	private static boolean isVisible(ClassLoader classLoader, Class<?> interfaceClass) {
		if (classLoader == interfaceClass.getClassLoader()) {
			return true;
		}
		try {
			return Class.forName(interfaceClass.getName(), false, classLoader) == interfaceClass;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

}
