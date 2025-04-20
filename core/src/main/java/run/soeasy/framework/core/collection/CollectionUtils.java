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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.exe.Function;
import run.soeasy.framework.core.reflect.Fields;
import run.soeasy.framework.core.reflect.ReflectionUtils;

public abstract class CollectionUtils {
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

	private static final Set<Class<?>> approximableCollectionTypes = new HashSet<Class<?>>();

	private static final Set<Class<?>> approximableMapTypes = new HashSet<Class<?>>();
	@SuppressWarnings({ "rawtypes" })
	private static final MultiValueMap EMPTY_MULTI_VALUE_MAP = new DefaultMultiValueMap<>(Collections.emptyMap());
	private static final Field KEY_TYPE_FIELD = ReflectionUtils.getDeclaredField(EnumMap.class, "keyType");
	private static final Field ELEMENT_TYPE_FIELD = ReflectionUtils.getDeclaredField(EnumSet.class, "elementType");
	static {
		ReflectionUtils.makeAccessible(KEY_TYPE_FIELD);
		ReflectionUtils.makeAccessible(ELEMENT_TYPE_FIELD);

		// Standard collection interfaces
		approximableCollectionTypes.add(Collection.class);
		approximableCollectionTypes.add(List.class);
		approximableCollectionTypes.add(Set.class);
		approximableCollectionTypes.add(SortedSet.class);
		approximableCollectionTypes.add(NavigableSet.class);
		approximableMapTypes.add(Map.class);
		approximableMapTypes.add(SortedMap.class);
		approximableMapTypes.add(NavigableMap.class);

		// Common concrete collection classes
		approximableCollectionTypes.add(ArrayList.class);
		approximableCollectionTypes.add(LinkedList.class);
		approximableCollectionTypes.add(HashSet.class);
		approximableCollectionTypes.add(LinkedHashSet.class);
		approximableCollectionTypes.add(TreeSet.class);
		approximableCollectionTypes.add(EnumSet.class);
		approximableMapTypes.add(HashMap.class);
		approximableMapTypes.add(LinkedHashMap.class);
		approximableMapTypes.add(TreeMap.class);
		approximableMapTypes.add(EnumMap.class);
	}

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

	/**
	 * 克隆一个{@link Collection}
	 * 
	 * @param <C>
	 * @param <E>
	 * @param collection
	 * @return
	 */
	public static <C extends Collection<E>, E> C clone(C collection) {
		return clone(collection, false);
	}

	/**
	 * 克隆一个{@link Collection}
	 * 
	 * @param <C>
	 * @param <E>
	 * @param collection
	 * @param deep
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <C extends Collection<E>, E> C clone(C collection, boolean deep) {
		Assert.requiredArgument(collection != null, "collection");
		if (!deep) {
			C value = ReflectionUtils.invokeCloneMethod(collection);
			if (value != null) {
				return value;
			}
			return ReflectionUtils.clone(collection, deep);
		}

		C cloneCollection;
		Class<?> collectionClass = collection.getClass();
		if (collectionClass == TreeSet.class) {
			cloneCollection = (C) new TreeSet<E>(((TreeSet<E>) collection).comparator());
		} else {
			try {
				cloneCollection = (C) CollectionUtils.createCollection(collection.getClass(),
						CollectionUtils.getEnumSetElementType(collection), collection.size());
			} catch (IllegalArgumentException e) {
				return ReflectionUtils.clone(collection, deep);
			}
		}

		for (Fields fields : ReflectionUtils.getDeclaredFields(collectionClass).entity().recursion()) {
			Class<?> sourceClass = fields.getSource().getRawType();
			if (sourceClass == Object.class || sourceClass.getName().startsWith("java.util.")
					|| sourceClass.getName().endsWith("Set") || sourceClass.getName().endsWith("List")) {
				continue;
			}

			fields.copy(collection, cloneCollection, deep);
		}

		for (E e : collection) {
			cloneCollection.add(ObjectUtils.clone(e, deep));
		}
		return cloneCollection;

	}

	/**
	 * 克隆一个{@link Map}
	 * 
	 * @param <M>
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @return
	 */
	public static <M extends Map<K, V>, K, V> M clone(M map) {
		Assert.requiredArgument(map != null, "map");
		return clone(map, false);
	}

	/**
	 * 克隆一个{@link Map}
	 * 
	 * @param <M>
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @param deep {@link ObjectUtils#clone(Object, boolean)}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Map<K, V>, K, V> M clone(M map, boolean deep) {
		Assert.requiredArgument(map != null, "map");
		if (!deep) {
			return ReflectionUtils.clone(map, false);
		}

		Class<?> mapType = map.getClass();
		M cloneMap;
		if (mapType == TreeMap.class) {
			cloneMap = (M) new TreeMap<K, V>(((TreeMap<K, V>) map).comparator());
		} else {
			try {
				cloneMap = (M) CollectionUtils.createMap(map.getClass(), CollectionUtils.getEnumMapKeyType(map),
						map.size());
			} catch (Exception e) {
				return ReflectionUtils.clone(map, deep);
			}
		}

		for (Fields fields : ReflectionUtils.getDeclaredFields(mapType).entity().recursion()) {
			Class<?> sourceClass = fields.getSource().getRawType();
			if (sourceClass == Object.class || sourceClass.getName().startsWith("java.util.")
					|| sourceClass.getName().endsWith("Map")) {
				continue;
			}

			fields.copy(map, cloneMap, deep);
		}

		for (Entry<K, V> entry : map.entrySet()) {
			cloneMap.put(ObjectUtils.clone(entry.getKey(), deep), ObjectUtils.clone(entry.getValue(), deep));
		}
		return cloneMap;
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
	private static Class<? extends Enum> asEnumType(Class<?> enumType) {
		Assert.notNull(enumType, "Enum type must not be null");
		if (!Enum.class.isAssignableFrom(enumType)) {
			throw new IllegalArgumentException("Supplied type is not an enum: " + enumType.getName());
		}
		return enumType.asSubclass(Enum.class);
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

	public static <K, V, SK, SV, E extends Throwable> Map<K, V> convert(Map<? extends SK, ? extends SV> sourceMap,
			Function<? super SK, ? extends K, ? extends E> keyConverter,
			Function<? super SV, ? extends V, ? extends E> valueConverter) throws E {
		if (CollectionUtils.isEmpty(sourceMap)) {
			return Collections.emptyMap();
		}

		Map<K, V> targetMap = createMap(sourceMap.getClass(), getEnumMapKeyType(sourceMap), sourceMap.size());
		for (Entry<? extends SK, ? extends SV> entry : sourceMap.entrySet()) {
			K key = keyConverter.apply(entry.getKey());
			V value = valueConverter.apply(entry.getValue());
			targetMap.put(key, value);
		}
		return targetMap;
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
	 * @see #isApproximableCollectionType
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
	 * @see #isApproximableMapType
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

	public static <E> List<E> createArrayList(boolean concurrent) {
		return createArrayList(concurrent, 16);
	}

	public static <E> List<E> createArrayList(boolean concurrent, int initialCapacity) {
		return concurrent ? new CopyOnWriteArrayList<E>() : new ArrayList<E>(initialCapacity);
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
	public static <E> Collection<E> createCollection(Class<?> collectionType, Class<?> elementType, int capacity)
			throws IllegalArgumentException {
		Assert.notNull(collectionType, "Collection type must not be null");
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
				return (Collection<E>) ReflectionUtils.newInstance(collectionType);
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
	 * <strong>Warning</strong>: Since the parameterized type {@code K} is not bound
	 * to the supplied {@code keyType}, type safety cannot be guaranteed if the
	 * desired {@code mapType} is {@link EnumMap}. In such scenarios, the caller is
	 * responsible for ensuring that the {@code keyType} is an enum type matching
	 * type {@code K}. As an alternative, the caller may wish to treat the return
	 * value as a raw map or map keyed by {@link Object}. Similarly, type safety
	 * cannot be enforced if the desired {@code mapType} is {@link MultiValueMap}.
	 * 
	 * @param mapType  the desired type of the target map (never {@code null})
	 * @param keyType  the map's key type, or {@code null} if unknown (note: only
	 *                 relevant for {@link EnumMap} creation)
	 * @param capacity the initial capacity
	 * @return a new map instance
	 * @see java.util.LinkedHashMap
	 * @see java.util.TreeMap
	 * @see java.util.EnumMap
	 * @throws IllegalArgumentException if the supplied {@code mapType} is
	 *                                  {@code null}; or if the desired
	 *                                  {@code mapType} is {@link EnumMap} and the
	 *                                  supplied {@code keyType} is not a subtype of
	 *                                  {@link Enum}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> createMap(Class<?> mapType, Class<?> keyType, int capacity) {
		Assert.notNull(mapType, "Map type must not be null");
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
				return (Map<K, V>) ReflectionUtils.newInstance(mapType);
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName(), ex);
			}
		}
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

	public static <E> Set<E> createSet(boolean concurrent) {
		return concurrent ? new CopyOnWriteArraySet<E>() : new LinkedHashSet<E>();
	}

	public static <E> Set<E> createSet(boolean concurrent, int initialCapacity) {
		return concurrent ? new CopyOnWriteArraySet<E>() : new LinkedHashSet<E>(initialCapacity);
	}

	/**
	 * Create a variant of {@link java.util.Properties} that sorts properties
	 * alphanumerically based on their keys.
	 * <p>
	 * This can be useful when storing the {@link Properties} instance in a
	 * properties file, since it allows such files to be generated in a repeatable
	 * manner with consistent ordering of properties. Comments in generated
	 * properties files can also be optionally omitted.
	 * 
	 * @param omitComments {@code true} if comments should be omitted when storing
	 *                     properties in a file
	 * @return a new {@code Properties} instance
	 * @see #createStringAdaptingProperties()
	 * @see #createSortedProperties(Properties, boolean)
	 */
	public static Properties createSortedProperties(boolean omitComments) {
		return new SortedProperties(omitComments);
	}

	/**
	 * Create a variant of {@link java.util.Properties} that sorts properties
	 * alphanumerically based on their keys.
	 * <p>
	 * This can be useful when storing the {@code Properties} instance in a
	 * properties file, since it allows such files to be generated in a repeatable
	 * manner with consistent ordering of properties. Comments in generated
	 * properties files can also be optionally omitted.
	 * <p>
	 * The returned {@code Properties} instance will be populated with properties
	 * from the supplied {@code properties} object, but default properties from the
	 * supplied {@code properties} object will not be copied.
	 * 
	 * @param properties   the {@code Properties} object from which to copy the
	 *                     initial properties
	 * @param omitComments {@code true} if comments should be omitted when storing
	 *                     properties in a file
	 * @return a new {@code Properties} instance
	 * @see #createStringAdaptingProperties()
	 * @see #createSortedProperties(boolean)
	 */
	public static Properties createSortedProperties(Properties properties, boolean omitComments) {
		return new SortedProperties(properties, omitComments);
	}

	/**
	 * Create a variant of {@link java.util.Properties} that automatically adapts
	 * non-String values to String representations in
	 * {@link Properties#getProperty}.
	 * <p>
	 * In addition, the returned {@code Properties} instance sorts properties
	 * alphanumerically based on their keys.
	 * 
	 * @return a new {@code Properties} instance
	 * @see #createSortedProperties(boolean)
	 * @see #createSortedProperties(Properties, boolean)
	 */
	@SuppressWarnings("serial")
	public static Properties createStringAdaptingProperties() {
		return new SortedProperties(false) {
			@Override
			public String getProperty(String key) {
				Object value = get(key);
				return (value != null ? value.toString() : null);
			}
		};
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
			return Streams.stream(universal.iterator()).collect(Collectors.toList());
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
			Comparator<? super E> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
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
			Iterator<? extends E> rightIterator, Comparator<? super E> comparator,
			BiFunction<? super E, ? super E, ? extends T> combiner) {
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

	/**
	 * Determine whether the given collection type is an <em>approximable</em> type,
	 * i.e. a type that {@link #createApproximateCollection} can approximate.
	 * 
	 * @param collectionType the collection type to check
	 * @return {@code true} if the type is <em>approximable</em>
	 */
	public static boolean isApproximableCollectionType(Class<?> collectionType) {
		return (collectionType != null && approximableCollectionTypes.contains(collectionType));
	}

	/**
	 * Determine whether the given map type is an <em>approximable</em> type, i.e. a
	 * type that {@link #createApproximateMap} can approximate.
	 * 
	 * @param mapType the map type to check
	 * @return {@code true} if the type is <em>approximable</em>
	 */
	public static boolean isApproximableMapType(Class<?> mapType) {
		return (mapType != null && approximableMapTypes.contains(mapType));
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
			java.util.function.Function<? super S, ? extends Iterator<? extends T>> converter) {
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
	 * 当comparator返回0进，会实际检验对象是还相等
	 * 
	 * @param <K>
	 * @param <V>
	 * @param comparator
	 * @return
	 */
	public static <K, V> TreeMap<K, V> newStrictTreeMap(Comparator<? super K> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
		return new TreeMap<>((o1, o2) -> {
			int order = comparator.compare(o1, o2);
			if (order == 0) {
				// 当排序认为相等时并不代表对象是相同的
				if (o1 == o2 || ObjectUtils.equals(o1, o2)) {
					return 0;
				}

				// 返回1,后添加的放在后面
				return 1;
			}
			return order;
		});
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
	 * 递归实现<br/>
	 * 从每个集合中取一个组合为新集合的所有可能
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