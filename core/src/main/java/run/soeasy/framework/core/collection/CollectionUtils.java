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

package run.soeasy.framework.core.collection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.type.ReflectionUtils;

@UtilityClass
public class CollectionUtils {
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

	@SuppressWarnings({ "rawtypes" })
	private static final MultiValueMap EMPTY_MULTI_VALUE_MAP = new DefaultMultiValueMap<>(Collections.emptyMap());
	private static final Field KEY_TYPE_FIELD = ReflectionUtils.findDeclaredField(EnumMap.class, "keyType").first();
	private static final Field ELEMENT_TYPE_FIELD = ReflectionUtils.findDeclaredField(EnumSet.class, "elementType")
			.first();
	static {
		ReflectionUtils.makeAccessible(KEY_TYPE_FIELD);
		ReflectionUtils.makeAccessible(ELEMENT_TYPE_FIELD);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Class<T> getEnumMapKeyType(Map map) {
		Class<T> keyType = null;
		if (map instanceof EnumMap) {
			keyType = (Class<T>) ReflectionUtils.get(KEY_TYPE_FIELD, map);
		}
		return keyType;
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getEnumSetElementType(@SuppressWarnings("rawtypes") Collection collection) {
		Class<T> elementType = null;
		if (collection instanceof EnumSet) {
			elementType = (Class<T>) ReflectionUtils.get(ELEMENT_TYPE_FIELD, collection);
		}
		return elementType;
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
		if (CollectionUtils.isEmpty(universal)) {
			return Collections.emptyList();
		}

		if (CollectionUtils.isEmpty(subaggregate)) {
			return unknownSizeStream(universal.iterator()).collect(Collectors.toList());
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
		if (CollectionUtils.isEmpty(universal)) {
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
			@NonNull Comparator<? super E> comparator) {
		if (CollectionUtils.isEmpty(universal)) {
			// 如果全集不存在那么也就没有补集
			return Collections.emptyList();
		}

		List<E> universalSet = new ArrayList<>();
		universal.forEachRemaining(universalSet::add);
		if (CollectionUtils.isEmpty(subaggregate)) {
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
		if (CollectionUtils.isEmpty(source) || CollectionUtils.isEmpty(candidates)) {
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

	/**
	 * 深度递归
	 * 
	 * @param <T>
	 * @param source
	 * @return 返回所有路径
	 */
	public static <T> List<List<T>> depthFirstTraversal(List<? extends Iterable<? extends T>> source) {
		List<List<T>> target = new ArrayList<>();
		depthFirstTraversal(source, target, new ArrayList<>(source.size()), 0);
		return target;
	}

	private static <T> void depthFirstTraversal(List<? extends Iterable<? extends T>> source, List<List<T>> target,
			List<T> results, int deep) {
		for (T element : source.get(deep)) {
			results.add(element);
			if (deep == source.size()) {
				// 最后一层了
				target.add(results);
			} else {
				List<T> copyList = new ArrayList<>(source.size());
				copyList.addAll(results);
				depthFirstTraversal(source, target, copyList, deep + 1);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T empty(Class<?> type) {
		if (Map.class.isAssignableFrom(type)) {
			return (T) Collections.emptyNavigableMap();
		} else if (Set.class.isAssignableFrom(type)) {
			return (T) Collections.emptyNavigableSet();
		} else if (Collection.class.isAssignableFrom(type)) {
			return (T) Collections.emptyList();
		}
		throw new IllegalArgumentException("Unsupported Collection type: " + type);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> MultiValueMap<K, V> emptyMultiValueMap() {
		return EMPTY_MULTI_VALUE_MAP;
	}

	public static <L, R> boolean equals(@NonNull Iterable<? extends L> leftIterable,
			@NonNull Iterable<? extends R> rightIterable, @NonNull BiPredicate<? super L, ? super R> predicate) {
		return equals(leftIterable.iterator(), rightIterable.iterator(), predicate);
	}

	public static <L, R> boolean equals(@NonNull Iterator<? extends L> leftIterator,
			@NonNull Iterator<? extends R> rightIterator, @NonNull BiPredicate<? super L, ? super R> predicate) {
		while (leftIterator.hasNext() && rightIterator.hasNext()) {
			if (!predicate.test(leftIterator.next(), rightIterator.next())) {
				return false;
			}
		}
		return !leftIterator.hasNext() && !rightIterator.hasNext();
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
	 * 获取第一个
	 * 
	 * @param <T>
	 * @param iterable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T first(Iterable<? extends T> iterable) {
		if (iterable == null) {
			return null;
		}

		if (iterable instanceof List) {
			List<T> list = (List<T>) iterable;
			if (list.isEmpty()) {
				return null;
			}
			return list.get(0);
		}

		Iterator<? extends T> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
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
			BiFunction<? super E, ? super E, ? extends T> combiner) {
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
			Iterator<? extends E> rightIterator, @NonNull Comparator<? super E> comparator,
			@NonNull BiFunction<? super E, ? super E, ? extends T> combiner) {
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

					T element = combiner.apply(left, right);
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

	public static <E> boolean isAll(Iterator<? extends E> iterator, @NonNull Predicate<? super E> predicate) {
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

	public static <E> boolean isAny(Iterator<? extends E> iterator, @NonNull Predicate<? super E> predicate) {
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
			@NonNull Function<? super S, ? extends Iterator<? extends T>> converter) {
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

	private static void recursion(List<? extends Iterable<String>> source, Elements<String> parents, int deep,
			List<Elements<String>> target) {
		if (deep == source.size()) {
			target.add(parents);
		} else {
			for (String name : source.get(deep)) {
				Elements<String> names = parents.concat(Elements.singleton(name));
				recursion(source, names, deep + 1, target);
			}
		}
	}

	/**
	 * 递归实现, 从每个集合中取一个组合为新集合的所有可能
	 * 
	 * @param source
	 * @return
	 */
	public static List<Elements<String>> recursiveComposition(List<? extends Iterable<String>> source) {
		List<Elements<String>> target = new ArrayList<>();
		recursion(source, Elements.empty(), 0, target);
		return target;
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

		return new IteratorToEnumeration<>(iterator, java.util.function.Function.identity());
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

		return new EnumerationToIterator<>(enumeration, java.util.function.Function.identity());
	}

	/**
	 * Adapts a {@code Map<K, List<V>>} to an {@code MultiValueMap<K,V>}.
	 *
	 * @param map the map
	 * @return the multi-value map
	 */
	public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> map) {
		return new DefaultMultiValueMap<>(map);
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
	public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(
			@NonNull Map<? extends K, ? extends List<V>> map) {
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

	public static <T> List<T> newReadOnlyList(Collection<T> collection) {
		if (isEmpty(collection)) {
			return Collections.emptyList();
		}
		List<T> target = new ArrayList<>(collection);
		return Collections.unmodifiableList(target);
	}

	/**
	 * 构造一个未知数量的流
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Stream<T> unknownSizeStream(Iterator<? extends T> iterator) {
		if (iterator == null) {
			return Stream.empty();
		}
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		Stream<T> stream = StreamSupport.stream(spliterator, false);
		if (iterator instanceof AutoCloseable) {
			stream = stream.onClose(ObjectUtils::closeQuietly);
		}
		return stream;
	}

	/**
	 * Create the most appropriate collection for the given collection type.
	 * <p>
	 * <strong>Warning</strong>: Since the parameterized type {@code E} is not bound
	 * to the supplied {@code elementType}, type safety cannot be guaranteed if the
	 * desired {@code collectionType} is {@link EnumSet}. In such scenarios, the
	 * caller is responsible for ensuring that the supplied {@code elementType} is
	 * an enum type matching type {@code E}. As an alternative, the caller may wish
	 * to treat the return value as a raw collection or collection of
	 * {@link Object}.
	 * 
	 * @param collectionType the desired type of the target collection (never
	 *                       {@code null})
	 * @param elementType    the collection's element type, or {@code null} if
	 *                       unknown (note: only relevant for {@link EnumSet}
	 *                       creation)
	 * @param capacity       the initial capacity
	 * @return a new collection instance
	 * @see java.util.LinkedHashSet
	 * @see java.util.ArrayList
	 * @see java.util.TreeSet
	 * @see java.util.EnumSet
	 * @throws IllegalArgumentException if the supplied {@code collectionType} is
	 *                                  {@code null}; or if the desired
	 *                                  {@code collectionType} is {@link EnumSet}
	 *                                  and the supplied {@code elementType} is not
	 *                                  a subtype of {@link Enum}
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> Collection<E> createCollection(@NonNull Class<?> collectionType, Class<?> elementType,
			int capacity) throws IllegalArgumentException {
		if (collectionType.isInterface()) {
			if (Set.class == collectionType || Collection.class == collectionType) {
				if (capacity == 0) {
					return Collections.emptySet();
				}
				return new LinkedHashSet<E>(capacity);
			} else if (List.class == collectionType) {
				if (capacity == 0) {
					return Collections.emptyList();
				}
				return new ArrayList<E>(capacity);
			} else if (SortedSet.class == collectionType) {
				if (capacity == 0) {
					return Collections.emptySortedSet();
				}
				return new TreeSet<E>();
			} else if (NavigableSet.class == collectionType) {
				if (capacity == 0) {
					return Collections.emptyNavigableSet();
				}
				return new TreeSet<E>();
			} else {
				throw new IllegalArgumentException("Unsupported Collection interface: " + collectionType.getName());
			}
		} else if (EnumSet.class.isAssignableFrom(collectionType)) {
			Assert.notNull(elementType, "Cannot create EnumSet for unknown element type");
			// Cast is necessary for compilation in Eclipse 4.4.1.
			return (Collection<E>) EnumSet.noneOf(asEnumType(elementType));
		} else {
			if (!Collection.class.isAssignableFrom(collectionType)) {
				throw new IllegalArgumentException("Unsupported Collection type: " + collectionType.getName());
			}
			try {
				return (Collection<E>) collectionType.newInstance();
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Could not instantiate Collection type: " + collectionType.getName(),
						ex);
			}
		}
	}

	/**
	 * Create the most appropriate collection for the given collection type.
	 * <p>
	 * Delegates to {@link #createCollection(Class, Class, int)} with a {@code null}
	 * element type.
	 * 
	 * @param collectionType the desired type of the target collection (never
	 *                       {@code null})
	 * @param capacity       the initial capacity
	 * @return a new collection instance
	 * @throws IllegalArgumentException if the supplied {@code collectionType} is
	 *                                  {@code null} or of type {@link EnumSet}
	 */
	public static <E> Collection<E> createCollection(Class<?> collectionType, int capacity) {
		return createCollection(collectionType, null, capacity);
	}

	/**
	 * Create the most appropriate map for the given map type.
	 * <p>
	 * Delegates to {@link #createMap(Class, Class, int)} with a {@code null} key
	 * type.
	 * 
	 * @param mapType  the desired type of the target map
	 * @param capacity the initial capacity
	 * @return a new map instance
	 * @throws IllegalArgumentException if the supplied {@code mapType} is
	 *                                  {@code null} or of type {@link EnumMap}
	 */
	public static <K, V> Map<K, V> createMap(Class<?> mapType, int capacity) {
		return createMap(mapType, null, capacity);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> createMap(@NonNull Class<?> mapType, Class<?> keyType, int capacity) {
		if (mapType.isInterface()) {
			if (Map.class == mapType) {
				if (capacity == 0) {
					return Collections.emptyMap();
				}
				return new LinkedHashMap<K, V>(capacity);
			} else if (SortedMap.class == mapType) {
				if (capacity == 0) {
					return Collections.emptySortedMap();
				}
				return new TreeMap<K, V>();
			} else if (NavigableMap.class == mapType) {
				if (capacity == 0) {
					return Collections.emptyNavigableMap();
				}
				return new TreeMap<K, V>();
			} else if (MultiValueMap.class == mapType) {
				if (capacity == 0) {
					return (Map<K, V>) CollectionUtils.emptyMultiValueMap();
				}
				return new LinkedMultiValueMap(capacity);
			} else {
				throw new IllegalArgumentException("Unsupported Map interface: " + mapType.getName());
			}
		} else if (EnumMap.class == mapType) {
			Assert.notNull(keyType, "Cannot create EnumMap for unknown key type");
			return new EnumMap(asEnumType(keyType));
		} else {
			if (!Map.class.isAssignableFrom(mapType)) {
				throw new IllegalArgumentException("Unsupported Map type: " + mapType.getName());
			}
			try {
				return (Map<K, V>) mapType.newInstance();
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName(), ex);
			}
		}
	}

	/**
	 * Cast the given type to a subtype of {@link Enum}.
	 * 
	 * @param enumType the enum type, never {@code null}
	 * @return the given type as subtype of {@link Enum}
	 * @throws IllegalArgumentException if the given type is not a subtype of
	 *                                  {@link Enum}
	 */
	@SuppressWarnings("rawtypes")
	private static Class<? extends Enum> asEnumType(@NonNull Class<?> enumType) {
		if (!Enum.class.isAssignableFrom(enumType)) {
			throw new IllegalArgumentException("Supplied type is not an enum: " + enumType.getName());
		}
		return enumType.asSubclass(Enum.class);
	}

	/**
	 * Create the most approximate collection for the given collection.
	 * <p>
	 * <strong>Warning</strong>: Since the parameterized type {@code E} is not bound
	 * to the type of elements contained in the supplied {@code collection}, type
	 * safety cannot be guaranteed if the supplied {@code collection} is an
	 * {@link EnumSet}. In such scenarios, the caller is responsible for ensuring
	 * that the element type for the supplied {@code collection} is an enum type
	 * matching type {@code E}. As an alternative, the caller may wish to treat the
	 * return value as a raw collection or collection of {@link Object}.
	 * 
	 * @param collection the original collection object, potentially {@code null}
	 * @param capacity   the initial capacity
	 * @return a new, empty collection instance
	 * @see java.util.LinkedList
	 * @see java.util.ArrayList
	 * @see java.util.EnumSet
	 * @see java.util.TreeSet
	 * @see java.util.LinkedHashSet
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E> Collection<E> createApproximateCollection(Object collection, int capacity) {
		if (collection instanceof LinkedList) {
			return new LinkedList<E>();
		} else if (collection instanceof List) {
			return new ArrayList<E>(capacity);
		} else if (collection instanceof EnumSet) {
			// Cast is necessary for compilation in Eclipse 4.4.1.
			Collection<E> enumSet = (Collection<E>) EnumSet.copyOf((EnumSet) collection);
			enumSet.clear();
			return enumSet;
		} else if (collection instanceof SortedSet) {
			return new TreeSet<E>(((SortedSet<E>) collection).comparator());
		} else {
			return new LinkedHashSet<E>(capacity);
		}
	}

	/**
	 * Create the most approximate map for the given map.
	 * <p>
	 * <strong>Warning</strong>: Since the parameterized type {@code K} is not bound
	 * to the type of keys contained in the supplied {@code map}, type safety cannot
	 * be guaranteed if the supplied {@code map} is an {@link EnumMap}. In such
	 * scenarios, the caller is responsible for ensuring that the key type in the
	 * supplied {@code map} is an enum type matching type {@code K}. As an
	 * alternative, the caller may wish to treat the return value as a raw map or
	 * map keyed by {@link Object}.
	 * 
	 * @param map      the original map object, potentially {@code null}
	 * @param capacity the initial capacity
	 * @return a new, empty map instance
	 * @see java.util.EnumMap
	 * @see java.util.TreeMap
	 * @see java.util.LinkedHashMap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> createApproximateMap(Object map, int capacity) {
		if (map instanceof EnumMap) {
			EnumMap enumMap = new EnumMap((EnumMap) map);
			enumMap.clear();
			return enumMap;
		} else if (map instanceof SortedMap) {
			return new TreeMap<K, V>(((SortedMap<K, V>) map).comparator());
		} else {
			return new LinkedHashMap<K, V>(capacity);
		}
	}

	/**
	 * 对迭代器中的所有元素执行消费操作，支持异常抛出。
	 * <p>
	 * 该方法通过递归方式遍历迭代器，对每个元素调用指定的消费者函数。
	 * 采用try-finally结构确保递归调用的完整性，即使中间操作抛出异常也会继续处理剩余元素。
	 * </p>
	 * 
	 * @param <T>      元素类型
	 * @param <E>      可能抛出的异常类型
	 * @param iterator 待遍历的迭代器
	 * @param consumer 元素消费者函数，支持抛出异常E
	 * @throws E 当消费者函数执行时抛出异常
	 * @see ThrowingConsumer
	 */
	public static <T, E extends Throwable> void acceptAll(@NonNull Iterator<? extends T> iterator,
			@NonNull ThrowingConsumer<? super T, ? extends E> consumer) throws E {
		if (iterator.hasNext()) {
			try {
				consumer.accept(iterator.next());
			} finally {
				acceptAll(iterator, consumer);
			}
		}
	}

	/**
	 * 对可迭代对象中的所有元素执行消费操作，支持异常抛出。
	 * <p>
	 * 该方法将可迭代对象转换为迭代器后，调用{@link #acceptAll(Iterator, ThrowingConsumer)}
	 * 实现元素遍历和消费，保持一致的异常处理逻辑。
	 * </p>
	 * 
	 * @param <T>      元素类型
	 * @param <E>      可能抛出的异常类型
	 * @param iterable 待遍历的可迭代对象
	 * @param consumer 元素消费者函数，支持抛出异常E
	 * @throws E 当消费者函数执行时抛出异常
	 * @see #acceptAll(Iterator, ThrowingConsumer)
	 */
	public static <T, E extends Throwable> void acceptAll(@NonNull Iterable<? extends T> iterable,
			@NonNull ThrowingConsumer<? super T, ? extends E> consumer) throws E {
		acceptAll(iterable.iterator(), consumer);
	}
}