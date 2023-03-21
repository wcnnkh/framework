/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public abstract class CollectionUtils {
	/**
	 * Iterator wrapping an Enumeration.
	 */
	private static class EnumerationIterator<E> implements Iterator<E> {

		private Enumeration<? extends E> enumeration;

		public EnumerationIterator(Enumeration<? extends E> enumeration) {
			this.enumeration = enumeration;
		}

		public boolean hasNext() {
			return this.enumeration.hasMoreElements();
		}

		public E next() {
			return this.enumeration.nextElement();
		}
	}

	private static final class PreviousIterator<E> implements Iterator<E> {
		private final ListIterator<E> listIterator;

		public PreviousIterator(ListIterator<E> listIterator) {
			this.listIterator = listIterator;
		}

		public boolean hasNext() {
			return listIterator.hasPrevious();
		}

		public E next() {
			return listIterator.previous();
		}

		@Override
		public void remove() {
			listIterator.remove();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final MultiValueMap EMPTY_MULTI_VALUE_MAP = new MultiValueMapWrapper(Collections.emptyMap());

	/**
	 * Convert the supplied array into a List. A primitive array gets converted into
	 * a List of the appropriate wrapper type.
	 * <p>
	 * A {@code null} source value will be converted to an empty List.
	 * 
	 * @param source the (potentially primitive) array
	 * @return the converted List result
	 * @see ObjectUtils#toObjectArray(Object)
	 */
	@SuppressWarnings("rawtypes")
	public static List arrayToList(Object source) {
		return Arrays.asList(ObjectUtils.toObjectArray(source));
	}

	public static <T> int compare(Collection<? extends T> collection1, Collection<? extends T> collection2,
			Comparator<T> comparator) {
		if (CollectionUtils.isEmpty(collection1)) {
			return CollectionUtils.isEmpty(collection2) ? 0 : -1;
		}

		if (CollectionUtils.isEmpty(collection2)) {
			return CollectionUtils.isEmpty(collection1) ? 0 : 1;
		}

		Iterator<? extends T> iterator1 = collection1.iterator();
		Iterator<? extends T> iterator2 = collection2.iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			int v = comparator.compare(iterator1.next(), iterator2.next());
			if (v != 0) {
				return v;
			}
		}
		return collection1.size() - collection2.size();
	}

	public static <T> int compare(Iterator<? extends T> iterator1, Iterator<? extends T> iterator2,
			Comparator<T> comparator) {
		if (CollectionUtils.isEmpty(iterator1)) {
			return CollectionUtils.isEmpty(iterator2) ? 0 : -1;
		}

		if (CollectionUtils.isEmpty(iterator2)) {
			return CollectionUtils.isEmpty(iterator1) ? 0 : 1;
		}

		while (iterator1.hasNext() && iterator2.hasNext()) {
			int v = comparator.compare(iterator1.next(), iterator2.next());
			if (v != 0) {
				return v;
			}
		}
		return iterator1.hasNext() ? 1 : (iterator2.hasNext() ? -1 : 0);
	}

	public static <E> Collection<E> complementary(Iterable<? extends E> universal, Iterable<? extends E> subaggregate) {
		if (isEmpty(universal)) {
			return Collections.emptyList();
		}

		if (isEmpty(subaggregate)) {
			return XUtils.stream(universal.iterator()).collect(Collectors.toList());
		}

		if (universal instanceof Set) {
			Set<E> universalSet = new LinkedHashSet<>();
			universal.forEach(universalSet::add);
			subaggregate.forEach(universalSet::remove);
			return universalSet;
		}
		return complementary(universal.iterator(), subaggregate.iterator());
	}

	public static <E> Collection<E> complementary(Iterable<? extends E> universal, Iterable<? extends E> subaggregate,
			Comparator<? super E> comparator) {
		if (isEmpty(universal)) {
			return Collections.emptyList();
		}

		return complementary(universal.iterator(),
				subaggregate == null ? Collections.emptyIterator() : subaggregate.iterator(), comparator);
	}

	public static <E> Collection<E> complementary(Iterator<? extends E> universal, Iterator<? extends E> subaggregate) {
		return complementary(universal, subaggregate, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1);
	}

	/**
	 * 获取补集(一定有全集大于子集)
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集
	 * @param subaggregate 子集
	 * @param comparator   比较器
	 * @return 返回补集
	 */
	public static <E> List<E> complementary(Iterator<? extends E> universal, Iterator<? extends E> subaggregate,
			Comparator<? super E> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
		if (isEmpty(universal)) {
			// 如果全集不存在那么也就没有补集
			return Collections.emptyList();
		}

		List<E> universalSet = new ArrayList<>();
		universal.forEachRemaining(universalSet::add);
		if (isEmpty(subaggregate)) {
			return universalSet;
		}

		while (subaggregate.hasNext()) {
			E element = subaggregate.next();
			Iterator<E> iterator = universalSet.iterator();
			while (iterator.hasNext()) {
				E source = iterator.next();
				if (comparator.compare(source, element) == 0) {
					iterator.remove();
					break;
				}
			}
		}
		return universalSet;
	}

	/**
	 * Return {@code true} if any element in '{@code candidates}' is contained in
	 * '{@code source}'; otherwise returns {@code false}.
	 * 
	 * @param source     the source Collection
	 * @param candidates the candidates to search for
	 * @return whether any of the candidates has been found
	 */
	@SuppressWarnings("rawtypes")
	public static boolean containsAny(Collection source, Collection candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return false;
		}
		for (Object candidate : candidates) {
			if (source.contains(candidate)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given Collection contains the given element instance.
	 * <p>
	 * Enforces the given instance to be present, rather than returning {@code true}
	 * for an equal element as well.
	 * 
	 * @param collection the Collection to check
	 * @param element    the element to look for
	 * @return {@code true} if found, {@code false} else
	 */
	@SuppressWarnings("rawtypes")
	public static boolean containsInstance(Collection collection, Object element) {
		if (collection != null) {
			for (Object candidate : collection) {
				if (candidate == element) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> MultiValueMap<K, V> emptyMultiValueMap() {
		return EMPTY_MULTI_VALUE_MAP;
	}

	public static <E> boolean equals(Iterable<? extends E> leftIterable, Iterable<? extends E> rightIterable) {
		return equals(leftIterable, rightIterable, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1);
	}

	public static <E> boolean equals(Iterable<? extends E> leftIterable, Iterable<? extends E> rightIterable,
			Comparator<? super E> comparator) {
		if (isEmpty(leftIterable)) {
			return isEmpty(rightIterable);
		}

		if (isEmpty(rightIterable)) {
			return isEmpty(leftIterable);
		}
		return equals(leftIterable.iterator(), rightIterable.iterator(), comparator);
	}

	public static <E> boolean equals(Iterator<? extends E> leftIterator, Iterator<? extends E> rightIterator) {
		return equals(leftIterator, rightIterator, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1);
	}

	public static <E> boolean equals(Iterator<? extends E> leftIterator, Iterator<? extends E> rightIterator,
			Comparator<? super E> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
		if (isEmpty(leftIterator)) {
			return isEmpty(rightIterator);
		}

		if (isEmpty(rightIterator)) {
			return isEmpty(leftIterator);
		}

		while (leftIterator.hasNext() && rightIterator.hasNext()) {
			if (comparator.compare(leftIterator.next(), rightIterator.next()) != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Find the common element type of the given Collection, if any.
	 * 
	 * @param collection the Collection to check
	 * @return the common element type, or {@code null} if no clear common type has
	 *         been found (or the collection was empty)
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?> findCommonElementType(Collection collection) {
		if (isEmpty(collection)) {
			return null;
		}
		Class<?> candidate = null;
		for (Object val : collection) {
			if (val != null) {
				if (candidate == null) {
					candidate = val.getClass();
				} else if (candidate != val.getClass()) {
					return null;
				}
			}
		}
		return candidate;
	}

	/**
	 * Return the first element in '{@code candidates}' that is contained in '
	 * {@code source}'. If no element in '{@code candidates}' is present in '
	 * {@code source}' returns {@code null}. Iteration order is {@link Collection}
	 * implementation specific.
	 * 
	 * @param source     the source Collection
	 * @param candidates the candidates to search for
	 * @return the first present object, or {@code null} if not found
	 */
	@SuppressWarnings("rawtypes")
	public static Object findFirstMatch(Collection source, Collection candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return null;
		}
		for (Object candidate : candidates) {
			if (source.contains(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Find a single value of one of the given types in the given Collection:
	 * searching the Collection for a value of the first type, then searching for a
	 * value of the second type, etc.
	 * 
	 * @param collection the collection to search
	 * @param types      the types to look for, in prioritized order
	 * @return a value of one of the given types found if there is a clear match, or
	 *         {@code null} if none or more than one such value found
	 */
	public static Object findValueOfType(Collection<?> collection, Class<?>[] types) {
		if (isEmpty(collection) || ObjectUtils.isEmpty(types)) {
			return null;
		}
		for (Class<?> type : types) {
			Object value = findValueOfType(collection, type);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Find a single value of the given type in the given Collection.
	 * 
	 * @param collection the Collection to search
	 * @param type       the type to look for
	 * @return a value of the given type found if there is a clear match, or
	 *         {@code null} if none or more than one such value found
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findValueOfType(Collection<?> collection, Class<T> type) {
		if (isEmpty(collection)) {
			return null;
		}
		T value = null;
		for (Object element : collection) {
			if (type == null || type.isInstance(element)) {
				if (value != null) {
					// More than one value found... no clear single value.
					return null;
				}
				value = (T) element;
			}
		}
		return value;
	}

	/**
	 * 获取一个迭代器
	 * 
	 * @param list
	 * @param previous 是否反向迭代
	 * @return
	 */
	public static <E> Iterator<E> getIterator(List<E> list, boolean previous) {
		if (isEmpty(list)) {
			return Collections.emptyIterator();
		}

		if (previous) {
			return new PreviousIterator<E>(list.listIterator(list.size()));
		} else {
			return list.iterator();
		}
	}

	public static <E> int hashCode(Iterable<? extends E> iterable) {
		return hashCode(iterable, (e) -> e.hashCode());
	}

	public static <E> int hashCode(Iterable<? extends E> iterable, ToIntFunction<? super E> hash) {
		if (iterable == null) {
			return 0;
		}
		return hashCode(iterable.iterator(), hash);
	}

	public static <E> int hashCode(Iterator<? extends E> iterator) {
		return hashCode(iterator, (e) -> e.hashCode());
	}

	public static <E> int hashCode(Iterator<? extends E> iterator, ToIntFunction<? super E> hash) {
		if (iterator == null) {
			return 0;
		}

		int result = 1;
		while (iterator.hasNext()) {
			E element = iterator.next();
			result = 31 * result + (element == null ? 0 : hash.applyAsInt(element));
		}
		return result;
	}

	public static <E> Collection<E> intersection(Iterable<? extends E> leftIterable,
			Iterable<? extends E> rightIterable) {
		if (isEmpty(leftIterable) || isEmpty(rightIterable)) {
			return Collections.emptyList();
		}

		if (rightIterable instanceof Set) {
			// 对set做优化
			Set<E> rightSet = new LinkedHashSet<>();
			rightIterable.forEach(rightSet::add);
			List<E> list = null;
			for (E left : leftIterable) {
				if (rightSet.remove(left)) {
					if (list == null) {
						list = new ArrayList<>(rightSet.size());
					}
					list.add(left);
				}
			}
			return list == null ? Collections.emptyList() : list;
		}
		return intersection(leftIterable.iterator(), rightIterable.iterator());
	}

	public static <E, T> Collection<T> intersection(Iterable<? extends E> leftIterable,
			Iterable<? extends E> rightIterable, Comparator<? super E> comparator,
			Combiner<? super E, ? super E, ? extends T> combiner) {
		if (isEmpty(leftIterable) || isEmpty(rightIterable)) {
			return Collections.emptyList();
		}
		return intersection(leftIterable.iterator(), rightIterable.iterator(), comparator, combiner);
	}

	public static <E> Collection<E> intersection(Iterator<? extends E> leftIterator,
			Iterator<? extends E> rightIterator) {
		return intersection(leftIterator, rightIterator, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1,
				(o1, o2) -> o1);
	}

	/**
	 * 交集
	 * 
	 * @param <E>           需要比较的元素类型
	 * @param <T>           返回的元素类型
	 * @param leftIterator  左边来源
	 * @param rightIterator 右边来原
	 * @param comparator    比较是否相同
	 * @param combiner      将结果合并
	 * @return 返回交集列表
	 */
	public static <E, T> Collection<T> intersection(Iterator<? extends E> leftIterator,
			Iterator<? extends E> rightIterator, Comparator<? super E> comparator,
			Combiner<? super E, ? super E, ? extends T> combiner) {
		Assert.requiredArgument(comparator != null, "comparator");
		Assert.requiredArgument(combiner != null, "combiner");
		if (isEmpty(leftIterator) || isEmpty(rightIterator)) {
			return Collections.emptyList();
		}

		List<E> rightList = new ArrayList<>();
		rightIterator.forEachRemaining(rightList::add);
		List<T> list = null;
		while (leftIterator.hasNext()) {
			E left = leftIterator.next();
			Iterator<E> rightListIterator = rightList.iterator();
			while (rightListIterator.hasNext()) {
				E right = rightListIterator.next();
				if (comparator.compare(left, right) == 0) {
					if (list == null) {
						list = new ArrayList<>(rightList.size());
					}

					T element = combiner.combine(left, right);
					list.add(element);
					rightListIterator.remove();
					break;
				}
			}
		}
		return list == null ? Collections.emptyList() : list;
	}

	public static <E> boolean isAll(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
		if (iterable == null) {
			return true;
		}

		return isAll(iterable.iterator(), predicate);
	}

	public static <E> boolean isAll(Iterator<? extends E> iterator, Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		if (iterator == null) {
			return true;
		}

		while (iterator.hasNext()) {
			E element = iterator.next();
			if (!predicate.test(element)) {
				return false;
			}
		}
		return true;
	}

	public static <E> boolean isAny(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
		if (iterable == null) {
			return false;
		}

		return isAny(iterable.iterator(), predicate);
	}

	public static <E> boolean isAny(Iterator<? extends E> iterator, Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		if (iterator == null) {
			return false;
		}

		while (iterator.hasNext()) {
			E element = iterator.next();
			if (predicate.test(element)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEmpty(Iterable<?> iterable) {
		if (iterable == null) {
			return true;
		}

		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).isEmpty();
		}

		Iterator<?> iterator = iterable.iterator();
		return isEmpty(iterator);
	}

	public static boolean isEmpty(Iterator<?> iterator) {
		return iterator == null || !iterator.hasNext();
	}

	/**
	 * Return {@code true} if the supplied Map is {@code null} or empty. Otherwise,
	 * return {@code false}.
	 * 
	 * @param map the Map to check
	 * @return whether the given Map is empty
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		return (map == null || map.isEmpty());
	}

	public static boolean isUnmodifiable(Object collection) {
		if (collection == null) {
			return false;
		}

		if (collection instanceof Collection || collection instanceof Map) {
			return collection.getClass().getSimpleName().startsWith("Unmodifiable");
		}
		return false;
	}

	public static <S, T> Iterator<T> iterator(Iterator<? extends S> iterator,
			Function<? super S, ? extends Iterator<T>> converter) {
		Assert.requiredArgument(converter != null, "converter");
		if (iterator == null) {
			return Collections.emptyIterator();
		}
		return new IterationIterator<>(iterator, converter);
	}

	public static <E> List<E> list(Iterator<? extends E> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return Collections.emptyList();
		}

		return Collections.list(CollectionUtils.toEnumeration(iterator));
	}

	/**
	 * Merge the given array into the given Collection.
	 * 
	 * @param array      the array to merge (may be {@code null})
	 * @param collection the target Collection to merge the array into
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void mergeArrayIntoCollection(Object array, Collection collection) {
		if (collection == null) {
			throw new IllegalArgumentException("Collection must not be null");
		}
		Object[] arr = ObjectUtils.toObjectArray(array);
		for (Object elem : arr) {
			collection.add(elem);
		}
	}

	/**
	 * Merge the given Properties instance into the given Map, copying all
	 * properties (key-value pairs) over.
	 * <p>
	 * Uses {@code Properties.propertyNames()} to even catch default properties
	 * linked into the original Properties instance.
	 * 
	 * @param props the Properties instance to merge (may be {@code null})
	 * @param map   the target Map to merge the properties into
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void mergePropertiesIntoMap(Properties props, Map map) {
		if (map == null) {
			throw new IllegalArgumentException("Map must not be null");
		}
		if (props != null) {
			for (Enumeration en = props.propertyNames(); en.hasMoreElements();) {
				String key = (String) en.nextElement();
				Object value = props.getProperty(key);
				if (value == null) {
					// Potentially a non-String value...
					value = props.get(key);
				}
				map.put(key, value);
			}
		}
	}

	/**
	 * 颠倒一个集合的排列
	 * 
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> List<E> reversal(Collection<E> collection) {
		if (collection == null || collection.isEmpty()) {
			return Collections.emptyList();
		}

		Object[] values = collection.toArray();
		values = ArrayUtils.reversal(values);
		return Arrays.asList((E[]) values);
	}

	public static int size(Collection<?> collection) {
		return collection == null ? 0 : collection.size();
	}

	public static int size(Map<?, ?> map) {
		return map == null ? 0 : map.size();
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> sort(Map<K, V> source) {
		if (isEmpty(source)) {
			return Collections.emptyMap();
		}

		Object[] keys = source.keySet().toArray();
		Arrays.sort(keys);
		LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(source.size());
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			map.put((K) key, source.get(key));
		}
		return map;
	}

	/**
	 * Marshal the elements from the given enumeration into an array of the given
	 * type. Enumeration elements must be assignable to the type of the given array.
	 * The array returned will be a different instance than the array given.
	 */
	public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
		ArrayList<A> elements = new ArrayList<A>();
		while (enumeration.hasMoreElements()) {
			elements.add(enumeration.nextElement());
		}
		return elements.toArray(array);
	}

	public static <E> Enumeration<E> toEnumeration(final Iterator<? extends E> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return Collections.emptyEnumeration();
		}

		return new Enumeration<E>() {

			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			public E nextElement() {
				return iterator.next();
			}
		};
	}

	/**
	 * Adapt an enumeration to an iterator.
	 * 
	 * @param enumeration the enumeration
	 * @return the iterator
	 */
	public static <E> Iterator<E> toIterator(Enumeration<? extends E> enumeration) {
		if (enumeration == null || !enumeration.hasMoreElements()) {
			return Collections.emptyIterator();
		}

		return new EnumerationIterator<E>(enumeration);
	}

	/**
	 * Adapts a {@code Map<K, List<V>>} to an {@code MultiValueMap<K,V>}.
	 *
	 * @param map the map
	 * @return the multi-value map
	 */
	public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> map) {
		return new MultiValueMapWrapper<K, V>(map);

	}

	public static <E> Set<E> toSet(Iterable<E> iterable) {
		Iterator<E> iterator = iterable.iterator();
		if (!iterator.hasNext()) {
			return Collections.emptySet();
		}

		Set<E> sets = new LinkedHashSet<E>();
		while (iterator.hasNext()) {
			sets.add(iterator.next());
		}
		return sets;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
		if (isUnmodifiable(map)) {
			return map;
		}

		if (map instanceof NavigableMap) {
			return Collections.unmodifiableNavigableMap((NavigableMap<K, V>) map);
		} else if (map instanceof SortedMap) {
			return Collections.unmodifiableSortedMap((SortedMap<K, V>) map);
		} else if (map instanceof MultiValueMap) {
			return (Map<K, V>) unmodifiableMultiValueMap((MultiValueMap<K, ?>) map);
		}
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Returns an unmodifiable view of the specified multi-value map.
	 *
	 * @param map the map for which an unmodifiable view is to be returned.
	 * @return an unmodifiable view of the specified multi-value map.
	 */
	public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(Map<? extends K, ? extends List<V>> map) {
		Assert.notNull(map, "'map' must not be null");
		Map<K, List<V>> result = new LinkedHashMap<K, List<V>>(map.size());
		for (Map.Entry<? extends K, ? extends List<? extends V>> entry : map.entrySet()) {
			List<V> values = Collections.unmodifiableList(entry.getValue());
			result.put(entry.getKey(), values);
		}
		Map<K, List<V>> unmodifiableMap = Collections.unmodifiableMap(result);
		return toMultiValueMap(unmodifiableMap);
	}

	public static <E> Set<E> unmodifiableSet(Set<E> set) {
		if (isUnmodifiable(set)) {
			return set;
		}

		if (set instanceof NavigableSet) {
			return Collections.unmodifiableNavigableSet((NavigableSet<E>) set);
		} else if (set instanceof SortedSet) {
			return Collections.unmodifiableSortedSet((SortedSet<E>) set);
		}
		return Collections.unmodifiableSet(set);
	}

	/**
	 * 无序的判断是否一致
	 * 
	 * @param <E>
	 * @param leftColllection
	 * @param rightCollection
	 * @return
	 */
	public static <E> boolean unorderedEquals(Collection<? extends E> leftColllection,
			Collection<? extends E> rightCollection) {
		if (isEmpty(leftColllection)) {
			return isEmpty(rightCollection);
		}

		if (isEmpty(rightCollection)) {
			return isEmpty(leftColllection);
		}

		return leftColllection.size() == rightCollection.size()
				&& intersection(leftColllection, rightCollection).size() == leftColllection.size();
	}
}