/*
 * Copyright 2002-2019 the original author or authors.
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

package run.soeasy.framework.core.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

import run.soeasy.framework.core.DecoratingProxy;
import run.soeasy.framework.core.annotation.MergedAnnotations.SearchStrategy;
import run.soeasy.framework.util.comparator.OrderComparator;

public class AnnotationAwareOrderComparator extends OrderComparator {

	/**
	 * Shared default instance of {@code AnnotationAwareOrderComparator}.
	 */
	public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

	@Override
	protected Integer findOrder(Object obj) {
		Integer order = super.findOrder(obj);
		if (order != null) {
			return order;
		}
		return findOrderFromAnnotation(obj);
	}

	private Integer findOrderFromAnnotation(Object obj) {
		AnnotatedElement element = (obj instanceof AnnotatedElement ? (AnnotatedElement) obj : obj.getClass());
		MergedAnnotations annotations = MergedAnnotations.from(element, SearchStrategy.TYPE_HIERARCHY);
		Integer order = OrderUtils.getOrderFromAnnotations(element, annotations);
		if (order == null && obj instanceof DecoratingProxy) {
			return findOrderFromAnnotation(((DecoratingProxy) obj).getDecoratedClass());
		}
		return order;
	}

	@Override
	public Integer getPriority(Object obj) {
		if (obj instanceof Class) {
			return OrderUtils.getPriority((Class<?>) obj);
		}
		Integer priority = OrderUtils.getPriority(obj.getClass());
		if (priority == null && obj instanceof DecoratingProxy) {
			return getPriority(((DecoratingProxy) obj).getDecoratedClass());
		}
		return priority;
	}

	public static void sort(List<?> list) {
		if (list.size() > 1) {
			list.sort(INSTANCE);
		}
	}

	public static void sort(Object[] array) {
		if (array.length > 1) {
			Arrays.sort(array, INSTANCE);
		}
	}

	public static void sortIfNecessary(Object value) {
		if (value instanceof Object[]) {
			sort((Object[]) value);
		} else if (value instanceof List) {
			sort((List<?>) value);
		}
	}

}
