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

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

import io.basc.framework.core.annotation.MergedAnnotations.SearchStrategy;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ConcurrentReferenceHashMap;

public abstract class OrderUtils {

	/** Cache marker for a non-annotated Class. */
	private static final Object NOT_ANNOTATED = new Object();

	private static final String JAVAX_PRIORITY_ANNOTATION = "javax.annotation.Priority";

	/** Cache for @Order value (or NOT_ANNOTATED marker) per Class. */
	private static final Map<AnnotatedElement, Object> orderCache = new ConcurrentReferenceHashMap<>(64);

	public static int getOrder(Class<?> type, int defaultOrder) {
		Integer order = getOrder(type);
		return (order != null ? order : defaultOrder);
	}

	@Nullable
	public static Integer getOrder(Class<?> type, @Nullable Integer defaultOrder) {
		Integer order = getOrder(type);
		return (order != null ? order : defaultOrder);
	}

	@Nullable
	public static Integer getOrder(Class<?> type) {
		return getOrder((AnnotatedElement) type);
	}

	@Nullable
	public static Integer getOrder(AnnotatedElement element) {
		return getOrderFromAnnotations(element, MergedAnnotations.from(element, SearchStrategy.TYPE_HIERARCHY));
	}

	@Nullable
	static Integer getOrderFromAnnotations(AnnotatedElement element, MergedAnnotations annotations) {
		if (!(element instanceof Class)) {
			return findOrder(annotations);
		}
		Object cached = orderCache.get(element);
		if (cached != null) {
			return (cached instanceof Integer ? (Integer) cached : null);
		}
		Integer result = findOrder(annotations);
		orderCache.put(element, result != null ? result : NOT_ANNOTATED);
		return result;
	}

	@Nullable
	private static Integer findOrder(MergedAnnotations annotations) {
		MergedAnnotation<Order> orderAnnotation = annotations.get(Order.class);
		if (orderAnnotation.isPresent()) {
			return orderAnnotation.getInt(MergedAnnotation.VALUE);
		}
		MergedAnnotation<?> priorityAnnotation = annotations.get(JAVAX_PRIORITY_ANNOTATION);
		if (priorityAnnotation.isPresent()) {
			return priorityAnnotation.getInt(MergedAnnotation.VALUE);
		}
		return null;
	}

	@Nullable
	public static Integer getPriority(Class<?> type) {
		return MergedAnnotations.from(type, SearchStrategy.TYPE_HIERARCHY).get(JAVAX_PRIORITY_ANNOTATION)
				.getValue(MergedAnnotation.VALUE, Integer.class).orElse(null);
	}

}
