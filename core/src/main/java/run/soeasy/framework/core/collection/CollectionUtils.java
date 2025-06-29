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

/**
 * 集合工具类 提供集合操作的各种实用方法，包括比较、过滤、转换、合并等功能
 * 
 * @author soeasy.run
 */
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

	/**
	 * 获取EnumMap的键类型
	 * 
	 * @param <T> 键类型
	 * @param map EnumMap实例
	 * @return 键的Class对象，非EnumMap返回null
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Class<T> getEnumMapKeyType(Map map) {
		Class<T> keyType = null;
		if (map instanceof EnumMap) {
			keyType = (Class<T>) ReflectionUtils.get(KEY_TYPE_FIELD, map);
		}
		return keyType;
	}

	/**
	 * 获取EnumSet的元素类型
	 * 
	 * @param <T>        元素类型
	 * @param collection EnumSet实例
	 * @return 元素的Class对象，非EnumSet返回null
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getEnumSetElementType(@SuppressWarnings("rawtypes") Collection collection) {
		Class<T> elementType = null;
		if (collection instanceof EnumSet) {
			elementType = (Class<T>) ReflectionUtils.get(ELEMENT_TYPE_FIELD, collection);
		}
		return elementType;
	}

	/**
	 * 比较两个集合的元素顺序
	 * 
	 * @param <T>         元素类型
	 * @param collection1 第一个集合
	 * @param collection2 第二个集合
	 * @param comparator  元素比较器
	 * @return 比较结果：负数-前者小，0-相等，正数-前者大
	 */
	public static <T> int compare(Collection<? extends T> collection1, Collection<? extends T> collection2,
			Comparator<T> comparator) {
		if (isEmpty(collection1)) {
			return isEmpty(collection2) ? 0 : -1;
		}

		if (isEmpty(collection2)) {
			return isEmpty(collection1) ? 0 : 1;
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

	/**
	 * 比较两个迭代器的元素顺序
	 * 
	 * @param <T>        元素类型
	 * @param iterator1  第一个迭代器
	 * @param iterator2  第二个迭代器
	 * @param comparator 元素比较器
	 * @return 比较结果：负数-前者小，0-相等，正数-前者大
	 */
	public static <T> int compare(Iterator<? extends T> iterator1, Iterator<? extends T> iterator2,
			Comparator<T> comparator) {
		if (isEmpty(iterator1)) {
			return isEmpty(iterator2) ? 0 : -1;
		}

		if (isEmpty(iterator2)) {
			return isEmpty(iterator1) ? 0 : 1;
		}

		while (iterator1.hasNext() && iterator2.hasNext()) {
			int v = comparator.compare(iterator1.next(), iterator2.next());
			if (v != 0) {
				return v;
			}
		}
		return iterator1.hasNext() ? 1 : (iterator2.hasNext() ? -1 : 0);
	}

	/**
	 * 计算两个可迭代对象的补集（全集-子集）
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集
	 * @param subaggregate 子集
	 * @return 补集元素集合
	 */
	public static <E> Collection<E> complementary(Iterable<? extends E> universal, Iterable<? extends E> subaggregate) {
		if (isEmpty(universal)) {
			return Collections.emptyList();
		}

		if (isEmpty(subaggregate)) {
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

	/**
	 * 计算两个可迭代对象的补集（全集-子集），使用自定义比较器
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集
	 * @param subaggregate 子集
	 * @param comparator   元素比较器
	 * @return 补集元素集合
	 */
	public static <E> Collection<E> complementary(Iterable<? extends E> universal, Iterable<? extends E> subaggregate,
			Comparator<? super E> comparator) {
		if (isEmpty(universal)) {
			return Collections.emptyList();
		}

		return complementary(universal.iterator(),
				subaggregate == null ? Collections.emptyIterator() : subaggregate.iterator(), comparator);
	}

	/**
	 * 计算两个迭代器的补集（全集-子集）
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集迭代器
	 * @param subaggregate 子集迭代器
	 * @return 补集元素集合
	 */
	public static <E> Collection<E> complementary(Iterator<? extends E> universal, Iterator<? extends E> subaggregate) {
		return complementary(universal, subaggregate, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1);
	}

	/**
	 * 计算两个迭代器的补集（全集-子集），使用自定义比较器
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集迭代器
	 * @param subaggregate 子集迭代器
	 * @param comparator   元素比较器，不可为null
	 * @return 补集元素列表
	 */
	public static <E> List<E> complementary(Iterator<? extends E> universal, Iterator<? extends E> subaggregate,
			@NonNull Comparator<? super E> comparator) {
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
	 * 检查源集合是否包含任意候选元素
	 * 
	 * @param source     源集合
	 * @param candidates 候选元素集合
	 * @return 包含任意候选元素返回true，否则false
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
	 * 检查集合是否包含指定对象实例（严格引用相等）
	 * 
	 * @param collection 待检查集合
	 * @param element    目标元素
	 * @return 包含实例返回true，否则false
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
	 * 深度优先遍历多个可迭代对象，生成所有可能的组合
	 * 
	 * @param <T>    元素类型
	 * @param source 可迭代对象列表，每个元素代表一层选择
	 * @return 所有组合的列表，每个组合是一个元素路径
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

	/**
	 * 创建指定类型的空集合
	 * 
	 * @param <T> 集合类型
	 * @param type 集合类型（Map/Set/Collection及其子类型）
	 * @return 空集合实例
	 * @throws IllegalArgumentException 不支持的集合类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T empty(Class<?> type) {
	    if (type == null) {
	        throw new IllegalArgumentException("Type must not be null");
	    }
	    
	    // 处理Map及其子接口
	    if (Map.class.isAssignableFrom(type)) {
	        if (SortedMap.class.isAssignableFrom(type)) {
	            return (T) Collections.emptyNavigableMap();
	        } else {
	            return (T) Collections.emptyMap();
	        }
	    }
	    
	    // 处理Set及其子接口
	    if (Set.class.isAssignableFrom(type)) {
	        if (SortedSet.class.isAssignableFrom(type)) {
	            return (T) Collections.emptyNavigableSet();
	        } else {
	            return (T) Collections.emptySet();
	        }
	    }
	    
	    // 处理Collection及其子接口
	    if (Collection.class.isAssignableFrom(type)) {
	        return (T) Collections.emptyList();
	    }
	    
	    throw new IllegalArgumentException("Unsupported Collection type: " + type);
	}

	/**
	 * 获取空的MultiValueMap实例
	 * 
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @return 空的MultiValueMap
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> MultiValueMap<K, V> emptyMultiValueMap() {
		return EMPTY_MULTI_VALUE_MAP;
	}

	/**
	 * 按顺序比较两个可迭代对象的元素
	 * 
	 * @param <L>           左元素类型
	 * @param <R>           右元素类型
	 * @param leftIterable  左可迭代对象
	 * @param rightIterable 右可迭代对象
	 * @param predicate     元素比较谓词
	 * @return 所有对应元素满足条件且长度相等返回true
	 */
	public static <L, R> boolean equals(@NonNull Iterable<? extends L> leftIterable,
			@NonNull Iterable<? extends R> rightIterable, @NonNull BiPredicate<? super L, ? super R> predicate) {
		return equals(leftIterable.iterator(), rightIterable.iterator(), predicate);
	}

	/**
	 * 按顺序比较两个迭代器的元素
	 * 
	 * @param <L>           左元素类型
	 * @param <R>           右元素类型
	 * @param leftIterator  左迭代器
	 * @param rightIterator 右迭代器
	 * @param predicate     元素比较谓词
	 * @return 所有对应元素满足条件且长度相等返回true
	 */
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
	 * 查找集合中所有元素的共同类型
	 * 
	 * @param collection 待检查集合
	 * @return 共同类型Class对象，无共同类型或集合为空返回null
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
	 * 获取可迭代对象的第一个元素
	 * 
	 * @param <T>      元素类型
	 * @param iterable 可迭代对象
	 * @return 第一个元素，空可迭代返回null
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
		return iterator.hasNext() ? iterator.next() : null;
	}

	/**
	 * 获取列表的正向或反向迭代器
	 * 
	 * @param <E>      元素类型
	 * @param list     列表
	 * @param previous 是否反向迭代
	 * @return 迭代器实例
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

	/**
	 * 计算可迭代对象的哈希码
	 * 
	 * @param <E>      元素类型
	 * @param iterable 可迭代对象
	 * @return 哈希码值
	 */
	public static <E> int hashCode(Iterable<? extends E> iterable) {
		return hashCode(iterable, (e) -> e.hashCode());
	}

	/**
	 * 计算可迭代对象的哈希码，使用自定义哈希函数
	 * 
	 * @param <E>      元素类型
	 * @param iterable 可迭代对象
	 * @param hash     哈希值计算函数
	 * @return 哈希码值
	 */
	public static <E> int hashCode(Iterable<? extends E> iterable, ToIntFunction<? super E> hash) {
		if (iterable == null) {
			return 0;
		}
		return hashCode(iterable.iterator(), hash);
	}

	/**
	 * 计算迭代器的哈希码
	 * 
	 * @param <E>      元素类型
	 * @param iterator 迭代器
	 * @return 哈希码值
	 */
	public static <E> int hashCode(Iterator<? extends E> iterator) {
		return hashCode(iterator, (e) -> e.hashCode());
	}

	/**
	 * 计算迭代器的哈希码，使用自定义哈希函数
	 * 
	 * @param <E>      元素类型
	 * @param iterator 迭代器
	 * @param hash     哈希值计算函数
	 * @return 哈希码值
	 */
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

	/**
	 * 计算两个可迭代对象的交集
	 * 
	 * @param <E>           元素类型
	 * @param leftIterable  左可迭代对象
	 * @param rightIterable 右可迭代对象
	 * @return 交集元素集合
	 */
	public static <E> Collection<E> intersection(Iterable<? extends E> leftIterable,
			Iterable<? extends E> rightIterable) {
		if (isEmpty(leftIterable) || isEmpty(rightIterable)) {
			return Collections.emptyList();
		}

		if (rightIterable instanceof Set) {
			// 对Set做优化
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

	/**
	 * 计算两个可迭代对象的交集，使用自定义合并函数
	 * 
	 * @param <E>           元素类型
	 * @param <T>           结果类型
	 * @param leftIterable  左可迭代对象
	 * @param rightIterable 右可迭代对象
	 * @param comparator    元素比较器
	 * @param combiner      元素合并函数
	 * @return 交集元素集合
	 */
	public static <E, T> Collection<T> intersection(Iterable<? extends E> leftIterable,
			Iterable<? extends E> rightIterable, Comparator<? super E> comparator,
			BiFunction<? super E, ? super E, ? extends T> combiner) {
		if (isEmpty(leftIterable) || isEmpty(rightIterable)) {
			return Collections.emptyList();
		}
		return intersection(leftIterable.iterator(), rightIterable.iterator(), comparator, combiner);
	}

	/**
	 * 计算两个迭代器的交集
	 * 
	 * @param <E>           元素类型
	 * @param leftIterator  左迭代器
	 * @param rightIterator 右迭代器
	 * @return 交集元素集合
	 */
	public static <E> Collection<E> intersection(Iterator<? extends E> leftIterator,
			Iterator<? extends E> rightIterator) {
		return intersection(leftIterator, rightIterator, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1,
				(o1, o2) -> o1);
	}

	/**
	 * 计算两个迭代器的交集，使用自定义比较器和合并函数
	 * 
	 * @param <E>           元素类型
	 * @param <T>           结果类型
	 * @param leftIterator  左迭代器
	 * @param rightIterator 右迭代器
	 * @param comparator    元素比较器，不可为null
	 * @param combiner      元素合并函数，不可为null
	 * @return 交集元素集合
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

	/**
	 * 检查可迭代对象中所有元素是否满足条件
	 * 
	 * @param <E>       元素类型
	 * @param iterable  可迭代对象
	 * @param predicate 条件谓词
	 * @return 所有元素满足条件返回true，否则false
	 */
	public static <E> boolean isAll(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
		if (iterable == null) {
			return true;
		}

		return isAll(iterable.iterator(), predicate);
	}

	/**
	 * 检查迭代器中所有元素是否满足条件
	 * 
	 * @param <E>       元素类型
	 * @param iterator  迭代器
	 * @param predicate 条件谓词，不可为null
	 * @return 所有元素满足条件返回true，否则false
	 */
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

	/**
	 * 检查可迭代对象中是否存在满足条件的元素
	 * 
	 * @param <E>       元素类型
	 * @param iterable  可迭代对象
	 * @param predicate 条件谓词
	 * @return 存在满足条件的元素返回true，否则false
	 */
	public static <E> boolean isAny(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
		if (iterable == null) {
			return false;
		}

		return isAny(iterable.iterator(), predicate);
	}

	/**
	 * 检查迭代器中是否存在满足条件的元素
	 * 
	 * @param <E>       元素类型
	 * @param iterator  迭代器
	 * @param predicate 条件谓词，不可为null
	 * @return 存在满足条件的元素返回true，否则false
	 */
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

	/**
	 * 判断可迭代对象是否为空
	 * 
	 * @param iterable 可迭代对象
	 * @return 为空返回true，否则false
	 */
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

	/**
	 * 判断迭代器是否为空
	 * 
	 * @param iterator 迭代器
	 * @return 为空返回true，否则false
	 */
	public static boolean isEmpty(Iterator<?> iterator) {
		return iterator == null || !iterator.hasNext();
	}

	/**
	 * 判断Map是否为空
	 * 
	 * @param map Map对象
	 * @return 为空返回true，否则false
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		return (map == null || map.isEmpty());
	}

	/**
	 * 判断集合是否为不可修改的
	 * 
	 * @param collection 集合对象
	 * @return 不可修改返回true，否则false
	 */
	public static boolean isUnmodifiable(Object collection) {
		if (collection == null) {
			return false;
		}

		if (collection instanceof Collection || collection instanceof Map) {
			return collection.getClass().getSimpleName().startsWith("Unmodifiable");
		}
		return false;
	}

	/**
	 * 转换迭代器元素类型，生成新迭代器
	 * 
	 * @param <S>       源元素类型
	 * @param <T>       目标元素类型
	 * @param iterator  源迭代器
	 * @param converter 元素转换函数，不可为null
	 * @return 新迭代器实例
	 */
	public static <S, T> Iterator<T> iterator(Iterator<? extends S> iterator,
			@NonNull Function<? super S, ? extends Iterator<? extends T>> converter) {
		if (iterator == null) {
			return Collections.emptyIterator();
		}
		return new IterationIterator<>(iterator, converter);
	}

	/**
	 * 将迭代器转换为List
	 * 
	 * @param <E>      元素类型
	 * @param iterator 迭代器
	 * @return List实例，空迭代器返回空List
	 */
	public static <E> List<E> list(Iterator<? extends E> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return Collections.emptyList();
		}

		return Collections.list(toEnumeration(iterator));
	}

	/**
	 * 递归生成多个字符串集合的所有组合
	 * 
	 * @param source 字符串集合列表，每个元素代表一层选择
	 * @return 所有组合的Elements列表
	 */
	public static List<Elements<String>> recursiveComposition(List<? extends Iterable<String>> source) {
		List<Elements<String>> target = new ArrayList<>();
		recursion(source, Elements.empty(), 0, target);
		return target;
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
	 * 反转集合元素顺序
	 * 
	 * @param <E>        元素类型
	 * @param collection 集合
	 * @return 反转后的List实例
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

	/**
	 * 获取集合大小
	 * 
	 * @param collection 集合
	 * @return 元素数量，null返回0
	 */
	public static int size(Collection<?> collection) {
		return collection == null ? 0 : collection.size();
	}

	/**
	 * 获取Map大小
	 * 
	 * @param map Map对象
	 * @return 键值对数量，null返回0
	 */
	public static int size(Map<?, ?> map) {
		return map == null ? 0 : map.size();
	}

	/**
	 * 按键排序Map
	 * 
	 * @param <K>    键类型
	 * @param <V>    值类型
	 * @param source 源Map
	 * @return 按键排序后的LinkedHashMap
	 */
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
	 * 将Enumeration转换为数组
	 * 
	 * @param <A>         数组类型
	 * @param <E>         元素类型
	 * @param enumeration Enumeration对象
	 * @param array       目标数组
	 * @return 包含所有元素的数组
	 */
	public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
		ArrayList<A> elements = new ArrayList<A>();
		while (enumeration.hasMoreElements()) {
			elements.add(enumeration.nextElement());
		}
		return elements.toArray(array);
	}

	/**
	 * 将Iterator转换为Enumeration
	 * 
	 * @param <E>      元素类型
	 * @param iterator Iterator对象
	 * @return Enumeration实例
	 */
	public static <E> Enumeration<E> toEnumeration(final Iterator<? extends E> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return Collections.emptyEnumeration();
		}

		return new IteratorToEnumeration<>(iterator, java.util.function.Function.identity());
	}

	/**
	 * 将Enumeration转换为Iterator
	 * 
	 * @param <E>         元素类型
	 * @param enumeration Enumeration对象
	 * @return Iterator实例
	 */
	public static <E> Iterator<E> toIterator(Enumeration<? extends E> enumeration) {
		if (enumeration == null || !enumeration.hasMoreElements()) {
			return Collections.emptyIterator();
		}

		return new EnumerationToIterator<>(enumeration, java.util.function.Function.identity());
	}

	/**
	 * 将Map&lt;K, List&lt;V&gt;&gt;转换为MultiValueMap&lt;K, V&gt;
	 * 
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @param map 源Map
	 * @return MultiValueMap实例
	 */
	public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> map) {
	    return new DefaultMultiValueMap<>(map);
	}

	/**
	 * 将可迭代对象转换为Set
	 * 
	 * @param <E>      元素类型
	 * @param iterable 可迭代对象
	 * @return Set实例，空可迭代返回空Set
	 */
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

	/**
	 * 创建不可修改的Map视图
	 * 
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @param map 源Map
	 * @return 不可修改的Map视图
	 */
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
	 * 创建不可修改的MultiValueMap视图
	 * 
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @param map 源MultiValueMap
	 * @return 不可修改的MultiValueMap视图
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

	/**
	 * 创建不可修改的Set视图
	 * 
	 * @param <E> 元素类型
	 * @param set 源Set
	 * @return 不可修改的Set视图
	 */
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
	 * 无序判断两个集合是否相等（元素相同但顺序可不同）
	 * 
	 * @param <E>             元素类型
	 * @param leftColllection 左集合
	 * @param rightCollection 右集合
	 * @return 元素相同返回true，否则false
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

	/**
	 * 创建只读List
	 * 
	 * @param <T>        元素类型
	 * @param collection 源集合
	 * @return 只读List实例
	 */
	public static <T> List<T> newReadOnlyList(Collection<T> collection) {
		if (isEmpty(collection)) {
			return Collections.emptyList();
		}
		List<T> target = new ArrayList<>(collection);
		return Collections.unmodifiableList(target);
	}

	/**
	 * 创建未知大小的流
	 * 
	 * @param <T>      元素类型
	 * @param iterator 迭代器
	 * @return 流实例
	 */
	public static <T> Stream<T> unknownSizeStream(Iterator<? extends T> iterator) {
		if (iterator == null) {
			return Stream.empty();
		}
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		Stream<T> stream = StreamSupport.stream(spliterator, false);
		if (iterator instanceof AutoCloseable) {
			stream = stream.onClose(() -> ObjectUtils.closeQuietly((AutoCloseable) iterator));
		}
		return stream;
	}

	/**
	 * 创建最适合的集合实现
	 * 
	 * @param <E>            元素类型
	 * @param collectionType 集合接口类型
	 * @param elementType    元素类型，可为null
	 * @param capacity       初始容量
	 * @return 集合实例
	 * @throws IllegalArgumentException 不支持的集合类型
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
	 * 创建最适合的集合实现（元素类型为null）
	 * 
	 * @param <E>            元素类型
	 * @param collectionType 集合接口类型
	 * @param capacity       初始容量
	 * @return 集合实例
	 * @throws IllegalArgumentException 不支持的集合类型
	 */
	public static <E> Collection<E> createCollection(Class<?> collectionType, int capacity) {
		return createCollection(collectionType, null, capacity);
	}

	/**
	 * 创建最适合的Map实现（键类型为null）
	 * 
	 * @param <K>      键类型
	 * @param <V>      值类型
	 * @param mapType  Map接口类型
	 * @param capacity 初始容量
	 * @return Map实例
	 * @throws IllegalArgumentException 不支持的Map类型
	 */
	public static <K, V> Map<K, V> createMap(Class<?> mapType, int capacity) {
		return createMap(mapType, null, capacity);
	}

	/**
	 * 创建最适合的Map实现
	 * 
	 * @param <K>      键类型
	 * @param <V>      值类型
	 * @param mapType  Map接口类型
	 * @param keyType  键类型，可为null
	 * @param capacity 初始容量
	 * @return Map实例
	 * @throws IllegalArgumentException 不支持的Map类型
	 */
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
	 * 将类转换为枚举类型
	 * 
	 * @param enumType 待转换的类
	 * @return 枚举类型Class对象
	 * @throws IllegalArgumentException 非枚举类型
	 */
	@SuppressWarnings("rawtypes")
	private static Class<? extends Enum> asEnumType(@NonNull Class<?> enumType) {
		if (!Enum.class.isAssignableFrom(enumType)) {
			throw new IllegalArgumentException("Supplied type is not an enum: " + enumType.getName());
		}
		return enumType.asSubclass(Enum.class);
	}

	/**
	 * 创建与原集合最相似的空集合
	 * 
	 * @param <E>        元素类型
	 * @param collection 原集合
	 * @param capacity   初始容量
	 * @return 新集合实例
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
	 * 创建与原Map最相似的空Map
	 * 
	 * @param <K>      键类型
	 * @param <V>      值类型
	 * @param map      原Map
	 * @param capacity 初始容量
	 * @return 新Map实例
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
	 * 对迭代器中所有元素执行带异常处理的消费操作
	 * 
	 * @param <T>      元素类型
	 * @param <E>      异常类型
	 * @param iterator 迭代器，不可为null
	 * @param consumer 消费函数，不可为null
	 * @throws E 消费过程中抛出的异常
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
	 * 对可迭代对象中所有元素执行带异常处理的消费操作
	 * 
	 * @param <T>      元素类型
	 * @param <E>      异常类型
	 * @param iterable 可迭代对象，不可为null
	 * @param consumer 消费函数，不可为null
	 * @throws E 消费过程中抛出的异常
	 */
	public static <T, E extends Throwable> void acceptAll(@NonNull Iterable<? extends T> iterable,
			@NonNull ThrowingConsumer<? super T, ? extends E> consumer) throws E {
		acceptAll(iterable.iterator(), consumer);
	}
}