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
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 集合工具类
 * <p>
 * 提供集合比较、运算、转换、创建、遍历等一站式实用方法，覆盖有序/无序集合操作、枚举集合元信息处理等场景。
 * 
 * @author soeasy.run
 */
@UtilityClass
public class CollectionUtils {
	private static final Field ELEMENT_TYPE_FIELD = ReflectionUtils.findDeclaredField(EnumSet.class, "elementType")
			.first();
	private static final Field KEY_TYPE_FIELD = ReflectionUtils.findDeclaredField(EnumMap.class, "keyType").first();
	static {
		ReflectionUtils.makeAccessible(KEY_TYPE_FIELD);
		ReflectionUtils.makeAccessible(ELEMENT_TYPE_FIELD);
	}

	/**
	 * 对可迭代对象中所有元素执行带异常处理的消费操作
	 * <p>
	 * 递归遍历所有元素，确保每个元素都被消费，异常可正常抛出。
	 * 
	 * @param <T>      元素类型
	 * @param <E>      消费操作可能抛出的异常类型
	 * @param iterable 可迭代对象，不可为null
	 * @param consumer 消费函数，不可为null
	 * @throws E 消费过程中抛出的异常
	 */
	public static <T, E extends Throwable> void acceptAll(@NonNull Iterable<? extends T> iterable,
			@NonNull ThrowingConsumer<? super T, ? extends E> consumer) throws E {
		acceptAll(iterable.iterator(), consumer);
	}

	/**
	 * 对迭代器中所有元素执行带异常处理的消费操作
	 * <p>
	 * 递归遍历所有元素，确保每个元素都被消费，异常可正常抛出。
	 * 
	 * @param <T>      元素类型
	 * @param <E>      消费操作可能抛出的异常类型
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
	 * 将类转换为枚举类型
	 * <p>
	 * 校验类是否为枚举类型，非枚举类型抛出IllegalArgumentException。
	 * 
	 * @param enumType 待转换的类
	 * @return 枚举类型Class对象
	 * @throws IllegalArgumentException 输入类不是枚举类型时抛出
	 */
	@SuppressWarnings("rawtypes")
	private static Class<? extends Enum> asEnumType(@NonNull Class<?> enumType) {
		if (!Enum.class.isAssignableFrom(enumType)) {
			throw new IllegalArgumentException("Supplied type is not an enum: " + enumType.getName());
		}
		return enumType.asSubclass(Enum.class);
	}

	/**
	 * 计算两个可迭代对象的补集（全集 - 子集）
	 * <p>
	 * 默认使用ObjectUtils.equals判断元素相等，全集为Set时返回LinkedHashSet，否则返回ArrayList。
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集，可为null（返回空列表）
	 * @param subaggregate 子集，可为null（返回全集拷贝）
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
	 * 计算两个可迭代对象的补集（全集 - 子集），支持自定义元素比较器
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集，可为null（返回空列表）
	 * @param subaggregate 子集，可为null（返回全集拷贝）
	 * @param comparator   元素比较器，用于判断元素相等
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
	 * 计算两个迭代器的补集（全集 - 子集）
	 * <p>
	 * 默认使用ObjectUtils.equals判断元素相等。
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集迭代器，可为null（返回空列表）
	 * @param subaggregate 子集迭代器，可为null（返回全集拷贝）
	 * @return 补集元素集合
	 */
	public static <E> Collection<E> complementary(Iterator<? extends E> universal, Iterator<? extends E> subaggregate) {
		return complementary(universal, subaggregate, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1);
	}

	/**
	 * 计算两个迭代器的补集（全集 - 子集），支持自定义元素比较器
	 * 
	 * @param <E>          元素类型
	 * @param universal    全集迭代器，可为null（返回空列表）
	 * @param subaggregate 子集迭代器，可为null（返回全集拷贝）
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
	 * 创建与原集合类型最相似的空集合
	 * <p>
	 * 适配规则：
	 * <ul>
	 * <li>LinkedList → 新LinkedList</li>
	 * <li>List（非LinkedList）→ 新ArrayList</li>
	 * <li>EnumSet → 清空后的同类型EnumSet</li>
	 * <li>SortedSet → 新TreeSet（复用原比较器）</li>
	 * <li>其他 → 新LinkedHashSet</li>
	 * </ul>
	 * 
	 * @param <E>        元素类型
	 * @param collection 原集合，不可为null
	 * @return 与原集合类型匹配的空集合
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E> Collection<E> createApproximateCollection(Object collection) {
		if (collection instanceof LinkedList) {
			return new LinkedList<E>();
		} else if (collection instanceof List) {
			return new ArrayList<E>();
		} else if (collection instanceof EnumSet) {
			// Cast is necessary for compilation in Eclipse 4.4.1.
			Collection<E> enumSet = (Collection<E>) EnumSet.copyOf((EnumSet) collection);
			enumSet.clear();
			return enumSet;
		} else if (collection instanceof SortedSet) {
			return new TreeSet<E>(((SortedSet<E>) collection).comparator());
		} else {
			return new LinkedHashSet<E>();
		}
	}

	/**
	 * 创建与原Map类型最相似的空Map
	 * <p>
	 * 适配规则：
	 * <ul>
	 * <li>EnumMap → 清空后的同类型EnumMap</li>
	 * <li>SortedMap → 新TreeMap（复用原比较器）</li>
	 * <li>其他 → 新LinkedHashMap</li>
	 * </ul>
	 * 
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @param map 原Map，不可为null
	 * @return 与原Map类型匹配的空Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> createApproximateMap(Object map) {
		if (map instanceof EnumMap) {
			EnumMap enumMap = new EnumMap((EnumMap) map);
			enumMap.clear();
			return enumMap;
		} else if (map instanceof SortedMap) {
			return new TreeMap<K, V>(((SortedMap<K, V>) map).comparator());
		} else {
			return new LinkedHashMap<K, V>();
		}
	}

	/**
	 * 创建最适合的集合实现（元素类型为null）
	 * <p>
	 * 等价于createCollection(collectionType, null)。
	 * 
	 * @param <E>            元素类型
	 * @param collectionType 集合接口类型，不可为null
	 * @return 对应接口的默认实现类实例
	 * @throws IllegalArgumentException 不支持的集合接口类型
	 */
	public static <E> Collection<E> createCollection(Class<?> collectionType) {
		return createCollection(collectionType, null);
	}

	/**
	 * 创建最适合的集合实现
	 * <p>
	 * 接口适配规则：
	 * <ul>
	 * <li>Set/Collection → LinkedHashSet</li>
	 * <li>List → ArrayList</li>
	 * <li>SortedSet → TreeSet</li>
	 * <li>NavigableSet → TreeSet</li>
	 * <li>EnumSet → 基于elementType创建空EnumSet（elementType不可为null）</li>
	 * </ul>
	 * 
	 * @param <E>            元素类型
	 * @param collectionType 集合接口/类类型，不可为null
	 * @param elementType    元素类型，仅EnumSet类型需要
	 * @return 对应类型的集合实例
	 * @throws IllegalArgumentException 不支持的集合类型、创建EnumSet时elementType为null、实例化自定义集合类失败
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> Collection<E> createCollection(@NonNull Class<?> collectionType, Class<?> elementType)
			throws IllegalArgumentException {
		if (collectionType.isInterface()) {
			if (Set.class == collectionType || Collection.class == collectionType) {
				return new LinkedHashSet<E>();
			} else if (List.class == collectionType) {
				return new ArrayList<E>();
			} else if (SortedSet.class == collectionType) {
				return new TreeSet<E>();
			} else if (NavigableSet.class == collectionType) {
				return new TreeSet<E>();
			} else {
				throw new IllegalArgumentException("Unsupported Collection interface: " + collectionType.getName());
			}
		} else if (EnumSet.class.isAssignableFrom(collectionType)) {
			Assert.notNull(elementType, "Cannot create EnumSet for unknown element type");
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
	 * 创建最适合的Map实现（键类型为null）
	 * <p>
	 * 等价于createMap(mapType, null)。
	 * 
	 * @param <K>     键类型
	 * @param <V>     值类型
	 * @param mapType Map接口类型，不可为null
	 * @return 对应接口的默认实现类实例
	 * @throws IllegalArgumentException 不支持的Map接口类型
	 */
	public static <K, V> Map<K, V> createMap(Class<?> mapType) {
		return createMap(mapType, null);
	}

	/**
	 * 创建最适合的Map实现
	 * <p>
	 * 接口适配规则：
	 * <ul>
	 * <li>Map → LinkedHashMap</li>
	 * <li>SortedMap → TreeMap</li>
	 * <li>NavigableMap → TreeMap</li>
	 * <li>MultiValueMap → LinkedMultiValueMap</li>
	 * <li>EnumMap → 基于keyType创建空EnumMap（keyType不可为null）</li>
	 * </ul>
	 * 
	 * @param <K>     键类型
	 * @param <V>     值类型
	 * @param mapType Map接口/类类型，不可为null
	 * @param keyType 键类型，仅EnumMap类型需要
	 * @return 对应类型的Map实例
	 * @throws IllegalArgumentException 不支持的Map类型、创建EnumMap时keyType为null、实例化自定义Map类失败
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> createMap(@NonNull Class<?> mapType, Class<?> keyType) {
		if (mapType.isInterface()) {
			if (Map.class == mapType) {
				return new LinkedHashMap<K, V>();
			} else if (SortedMap.class == mapType) {
				return new TreeMap<K, V>();
			} else if (NavigableMap.class == mapType) {
				return new TreeMap<K, V>();
			} else if (MultiValueMap.class == mapType) {
				return new LinkedMultiValueMap();
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
	 * 深度优先遍历多个可迭代对象，生成所有可能的组合
	 * 
	 * @param <T>    元素类型
	 * @param source 可迭代对象列表，每个元素代表一层选择
	 * @return 所有组合的列表（每个组合为一个元素路径）
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
	 * 无序判断两个可迭代集合是否相等（元素相同但顺序可不同）
	 * <p>
	 * 重载入口方法，内部转发至 {@link #equalsInAnyOrder(Iterator, Iterator, BiPredicate)} 核心实现，
	 * 仅做空值/引用快速校验，不涉及具体匹配逻辑。
	 * <p>
	 * 核心规则：
	 * <ul>
	 * <li>空值处理：两个Iterable引用相同（含均为null）返回true；仅其一为null返回false；</li>
	 * <li>转发逻辑：非空且引用不同时，提取迭代器转发至核心方法处理；</li>
	 * </ul>
	 * 
	 * @param <L>           左集合元素类型
	 * @param <R>           右集合元素类型
	 * @param leftIterable  左可迭代集合，可为null
	 * @param rightIterable 右可迭代集合，可为null
	 * @param predicate     元素匹配谓词，不可为null（用于自定义元素相等规则）
	 * @return 元素数量相同且每个元素都能找到唯一匹配项（无序）返回true，否则false；两个空集合返回true，仅其一为空返回false
	 * @throws NullPointerException 当匹配谓词predicate为null时抛出
	 */
	public static <L, R> boolean equalsInAnyOrder(Iterable<? extends L> leftIterable,
			Iterable<? extends R> rightIterable, @NonNull BiPredicate<? super L, ? super R> predicate) {
		if (leftIterable == rightIterable) {
			return true;
		}

		if (leftIterable == null || rightIterable == null) {
			return false;
		}
		return equalsInAnyOrder(leftIterable.iterator(), rightIterable.iterator(), predicate);
	}

	/**
	 * 无序判断两个迭代器的元素是否相等（元素相同但顺序可不同）
	 * <p>
	 * 核心实现方法，处理具体的元素匹配逻辑：
	 * <p>
	 * 注意：该方法仅修改迭代器元素拷贝后的内部列表，<b>不会修改原始输入的迭代器/可迭代集合</b>； 若需自定义元素匹配规则，通过谓词灵活指定。
	 * <p>
	 * 核心规则：
	 * <ul>
	 * <li>空值处理：两个Iterator引用相同（含均为null）返回true；仅其一为null返回false；</li>
	 * <li>空迭代器：两个空迭代器（无元素）返回true，仅其一为空返回false；</li>
	 * <li>数量校验：元素数量不一致直接返回false；</li>
	 * <li>匹配逻辑：逐个匹配元素，匹配成功后移除已匹配项，避免重复匹配；</li>
	 * </ul>
	 * 
	 * @param <L>           左迭代器元素类型
	 * @param <R>           右迭代器元素类型
	 * @param leftIterator  左元素迭代器，可为null
	 * @param rightIterator 右元素迭代器，可为null
	 * @param predicate     元素匹配谓词，不可为null（用于自定义元素相等规则）
	 * @return 元素数量相同且每个元素都能找到唯一匹配项（无序）返回true，否则false；两个空迭代器返回true，仅其一为空返回false
	 * @throws NullPointerException 当匹配谓词predicate为null时抛出
	 */
	public static <L, R> boolean equalsInAnyOrder(Iterator<? extends L> leftIterator,
			Iterator<? extends R> rightIterator, @NonNull BiPredicate<? super L, ? super R> predicate) {
		if (leftIterator == rightIterator) {
			return true;
		}
		if (leftIterator == null || rightIterator == null) {
			return false;
		}

		if (!leftIterator.hasNext() && !rightIterator.hasNext()) {
			return true;// 空的
		}

		List<L> leftList = new ArrayList<>();
		List<R> rightList = new ArrayList<>();
		while (leftIterator.hasNext() && rightIterator.hasNext()) {
			leftList.add(leftIterator.next());
			rightList.add(rightIterator.next());
		}

		if (leftIterator.hasNext() || rightIterator.hasNext()) {
			return false;// 数量不一致
		}

		leftIterator = leftList.iterator();
		while (leftIterator.hasNext()) {
			L left = leftIterator.next();
			rightIterator = rightList.iterator();
			boolean find = false;
			while (rightIterator.hasNext()) {
				R right = rightIterator.next();
				if (predicate.test(left, right)) {
					leftIterator.remove();
					rightIterator.remove();
					find = true;
					break;
				}
			}

			if (!find) {
				return false;
			}
		}
		return leftList.isEmpty() && rightList.isEmpty();
	}

	/**
	 * 按顺序比较两个可迭代对象的元素
	 * <p>
	 * 要求元素数量相等且每个位置元素匹配才返回true。
	 * 
	 * @param <L>           左元素类型
	 * @param <R>           右元素类型
	 * @param leftIterable  左可迭代对象，不可为null
	 * @param rightIterable 右可迭代对象，不可为null
	 * @param predicate     元素比较谓词，不可为null
	 * @return 所有对应元素满足条件且长度相等返回true，否则false
	 */
	public static <L, R> boolean equalsInOrder(@NonNull Iterable<? extends L> leftIterable,
			@NonNull Iterable<? extends R> rightIterable, @NonNull BiPredicate<? super L, ? super R> predicate) {
		return equalsInOrder(leftIterable.iterator(), rightIterable.iterator(), predicate);
	}

	/**
	 * 按顺序比较两个迭代器的元素
	 * <p>
	 * 要求元素数量相等且每个位置元素匹配才返回true。
	 * 
	 * @param <L>           左元素类型
	 * @param <R>           右元素类型
	 * @param leftIterator  左迭代器，不可为null
	 * @param rightIterator 右迭代器，不可为null
	 * @param predicate     元素比较谓词，不可为null
	 * @return 所有对应元素满足条件且长度相等返回true，否则false
	 */
	public static <L, R> boolean equalsInOrder(@NonNull Iterator<? extends L> leftIterator,
			@NonNull Iterator<? extends R> rightIterator, @NonNull BiPredicate<? super L, ? super R> predicate) {
		while (leftIterator.hasNext() && rightIterator.hasNext()) {
			if (!predicate.test(leftIterator.next(), rightIterator.next())) {
				return false;
			}
		}
		return !leftIterator.hasNext() && !rightIterator.hasNext();
	}

	/**
	 * 查找集合中所有非空元素的共同类型
	 * <p>
	 * 集合为空/全为null/存在不同类型元素时返回null。
	 * 
	 * @param collection 待检查集合，可为null
	 * @return 共同类型Class对象，无共同类型返回null
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
	 * @param iterable 可迭代对象，可为null
	 * @return 第一个元素，空可迭代/参数为null返回null
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
	 * 获取EnumMap的键类型（通过反射）
	 * 
	 * @param <T> 键类型（枚举类型）
	 * @param map EnumMap实例，可为null
	 * @return 键的Class对象，非EnumMap/参数为null返回null
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
	 * 获取EnumSet的元素类型（通过反射）
	 * 
	 * @param <T>        元素类型（枚举类型）
	 * @param collection EnumSet实例，可为null
	 * @return 元素的Class对象，非EnumSet/参数为null返回null
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
	 * 获取列表的正向/反向迭代器
	 * 
	 * @param <E>      元素类型
	 * @param list     列表，可为null（返回空迭代器）
	 * @param previous 是否反向迭代：true=从后往前，false=正向
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
	 * <p>
	 * 默认使用元素自身的hashCode()，空对象返回0，空可迭代返回1。
	 * 
	 * @param <E>      元素类型
	 * @param iterable 可迭代对象，可为null
	 * @return 哈希码值
	 */
	public static <E> int hashCode(Iterable<? extends E> iterable) {
		return hashCode(iterable, (e) -> e.hashCode());
	}

	/**
	 * 计算可迭代对象的哈希码，支持自定义哈希函数
	 * 
	 * @param <E>      元素类型
	 * @param iterable 可迭代对象，可为null
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
	 * <p>
	 * 默认使用元素自身的hashCode()，空对象返回0，空迭代器返回1。
	 * 
	 * @param <E>      元素类型
	 * @param iterator 迭代器，可为null
	 * @return 哈希码值
	 */
	public static <E> int hashCode(Iterator<? extends E> iterator) {
		return hashCode(iterator, (e) -> e.hashCode());
	}

	/**
	 * 计算迭代器的哈希码，支持自定义哈希函数
	 * 
	 * @param <E>      元素类型
	 * @param iterator 迭代器，可为null
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
	 * <p>
	 * 默认使用ObjectUtils.equals判断元素相等，右集合为Set时优化性能。
	 * 
	 * @param <E>           元素类型
	 * @param leftIterable  左可迭代对象，可为null（返回空列表）
	 * @param rightIterable 右可迭代对象，可为null（返回空列表）
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
	 * 计算两个可迭代对象的交集，支持自定义比较器和合并函数
	 * 
	 * @param <E>           源元素类型
	 * @param <T>           结果元素类型
	 * @param leftIterable  左可迭代对象，可为null（返回空列表）
	 * @param rightIterable 右可迭代对象，可为null（返回空列表）
	 * @param comparator    元素比较器
	 * @param combiner      合并函数
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
	 * <p>
	 * 默认使用ObjectUtils.equals判断元素相等。
	 * 
	 * @param <E>           元素类型
	 * @param leftIterator  左迭代器，可为null（返回空列表）
	 * @param rightIterator 右迭代器，可为null（返回空列表）
	 * @return 交集元素集合
	 */
	public static <E> Collection<E> intersection(Iterator<? extends E> leftIterator,
			Iterator<? extends E> rightIterator) {
		return intersection(leftIterator, rightIterator, (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1,
				(o1, o2) -> o1);
	}

	/**
	 * 计算两个迭代器的交集，支持自定义比较器和合并函数
	 * 
	 * @param <E>           源元素类型
	 * @param <T>           结果元素类型
	 * @param leftIterator  左迭代器，可为null（返回空列表）
	 * @param rightIterator 右迭代器，可为null（返回空列表）
	 * @param comparator    元素比较器，不可为null
	 * @param combiner      合并函数，不可为null
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
	 * 判断可迭代对象是否为空
	 * <p>
	 * null/空Collection/无元素的可迭代对象均返回true。
	 * 
	 * @param iterable 可迭代对象，可为null
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
	 * 判断迭代器是否为空（无元素）
	 * 
	 * @param iterator 迭代器，可为null
	 * @return 为null/无元素返回true，否则false
	 */
	public static boolean isEmpty(Iterator<?> iterator) {
		return iterator == null || !iterator.hasNext();
	}

	/**
	 * 判断Map是否为空
	 * 
	 * @param map Map对象，可为null
	 * @return 为null/空Map返回true，否则false
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		return (map == null || map.isEmpty());
	}

	/**
	 * 创建只读List
	 * <p>
	 * 返回不可修改的List视图，底层基于ArrayList拷贝原集合。
	 * 
	 * @param <T>        元素类型
	 * @param collection 源集合，可为null/空（返回空只读列表）
	 * @return 只读List实例
	 */
	public static <T> List<T> newReadOnlyList(Collection<T> collection) {
		if (isEmpty(collection)) {
			return Collections.emptyList();
		}
		List<T> target = new ArrayList<>(collection);
		return Collections.unmodifiableList(target);
	}

	private static void recursion(List<? extends Iterable<String>> source, Streamable<String> parents, int deep,
			List<Streamable<String>> target) {
		if (deep == source.size()) {
			target.add(parents);
		} else {
			for (String name : source.get(deep)) {
				Streamable<String> names = parents.concat(Streamable.singleton(name));
				recursion(source, names, deep + 1, target);
			}
		}
	}

	/**
	 * 递归生成多个字符串集合的所有组合
	 * 
	 * @param source 字符串可迭代对象列表
	 * @return 所有组合的Streamable列表
	 */
	public static List<Streamable<String>> recursiveComposition(List<? extends Iterable<String>> source) {
		List<Streamable<String>> target = new ArrayList<>();
		recursion(source, Streamable.empty(), 0, target);
		return target;
	}

	/**
	 * 反转集合元素顺序
	 * 
	 * @param <E>        元素类型
	 * @param collection 集合，可为null/空（返回空列表）
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
	 * 将Enumeration转换为数组
	 * 
	 * @param <A>         目标数组类型
	 * @param <E>         枚举元素类型
	 * @param enumeration 枚举对象，可为null（返回空数组）
	 * @param array       目标数组，用于确定返回数组类型
	 * @return 包含枚举所有元素的数组
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
	 * @param iterator 迭代器，可为null（返回空枚举）
	 * @return Enumeration实例
	 */
	public static <E> Enumeration<E> toEnumeration(final Iterator<? extends E> iterator) {
		if (iterator == null) {
			return Collections.emptyEnumeration();
		}

		return new IteratorToEnumeration<>(iterator);
	}

	/**
	 * 将Enumeration转换为Iterator
	 * 
	 * @param <E>         元素类型
	 * @param enumeration 枚举，可为null（返回空迭代器）
	 * @return Iterator实例
	 */
	public static <E> Iterator<E> toIterator(Enumeration<? extends E> enumeration) {
		if (enumeration == null) {
			return Collections.emptyIterator();
		}

		return new EnumerationToIterator<>(enumeration);
	}

	/**
	 * 创建未知大小的流
	 * <p>
	 * 基于迭代器创建顺序流，迭代器实现AutoCloseable时流关闭会自动关闭迭代器。
	 * 
	 * @param <T>      元素类型
	 * @param iterator 迭代器，可为null（返回空流）
	 * @return Stream实例
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
}