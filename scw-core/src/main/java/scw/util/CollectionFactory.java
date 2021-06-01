package scw.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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

import scw.convert.Converter;
import scw.core.Assert;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.CollectionUtils;
import scw.env.Sys;
import scw.lang.Nullable;

public final class CollectionFactory {
	private static final Field KEY_TYPE_FIELD = ReflectionUtils.findField(EnumMap.class, "keyType");
	private static final Field ELEMENT_TYPE_FIELD = ReflectionUtils.findField(EnumSet.class, "elementType");

	private static final Set<Class<?>> approximableCollectionTypes = new HashSet<Class<?>>();

	private static final Set<Class<?>> approximableMapTypes = new HashSet<Class<?>>();

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

	private CollectionFactory() {
	}

	/**
	 * Determine whether the given collection type is an <em>approximable</em> type,
	 * i.e. a type that {@link #createApproximateCollection} can approximate.
	 * 
	 * @param collectionType the collection type to check
	 * @return {@code true} if the type is <em>approximable</em>
	 */
	public static boolean isApproximableCollectionType(@Nullable Class<?> collectionType) {
		return (collectionType != null && approximableCollectionTypes.contains(collectionType));
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
	public static <E> Collection<E> createApproximateCollection(@Nullable Object collection, int capacity) {
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
	public static <E> Collection<E> createCollection(Class<?> collectionType, @Nullable Class<?> elementType,
			int capacity) {
		Assert.notNull(collectionType, "Collection type must not be null");
		if (collectionType.isInterface()) {
			if (Set.class == collectionType || Collection.class == collectionType) {
				return new LinkedHashSet<E>(capacity);
			} else if (List.class == collectionType) {
				return new ArrayList<E>(capacity);
			} else if (SortedSet.class == collectionType || NavigableSet.class == collectionType) {
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
				return (Collection<E>) Sys.getInstanceFactory().getInstance(collectionType);
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Could not instantiate Collection type: " + collectionType.getName(),
						ex);
			}
		}
	}

	/**
	 * Determine whether the given map type is an <em>approximable</em> type, i.e. a
	 * type that {@link #createApproximateMap} can approximate.
	 * 
	 * @param mapType the map type to check
	 * @return {@code true} if the type is <em>approximable</em>
	 */
	public static boolean isApproximableMapType(@Nullable Class<?> mapType) {
		return (mapType != null && approximableMapTypes.contains(mapType));
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
	public static <K, V> Map<K, V> createApproximateMap(@Nullable Object map, int capacity) {
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
	public static <K, V> Map<K, V> createMap(Class<?> mapType, @Nullable Class<?> keyType, int capacity) {
		Assert.notNull(mapType, "Map type must not be null");
		if (mapType.isInterface()) {
			if (Map.class == mapType) {
				return new LinkedHashMap<K, V>(capacity);
			} else if (SortedMap.class == mapType || NavigableMap.class == mapType) {
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
				return (Map<K, V>) Sys.getInstanceFactory().getInstance(mapType);
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName(), ex);
			}
		}
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
			@Nullable
			public String getProperty(String key) {
				Object value = get(key);
				return (value != null ? value.toString() : null);
			}
		};
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

	public static <E> List<E> createArrayList(boolean concurrent) {
		return createArrayList(concurrent, 16);
	}

	public static <E> List<E> createArrayList(boolean concurrent, int initialCapacity) {
		return concurrent ? new CopyOnWriteArrayList<E>() : new ArrayList<E>(initialCapacity);
	}

	public static <E> Set<E> createSet(boolean concurrent) {
		return concurrent ? new CopyOnWriteArraySet<E>() : new LinkedHashSet<E>();
	}

	public static <E> Set<E> createSet(boolean concurrent, int initialCapacity) {
		return concurrent ? new CopyOnWriteArraySet<E>() : new LinkedHashSet<E>(initialCapacity);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Class<T> getEnumMapKeyType(Map map) {
		Class<T> keyType = null;
		if (map instanceof EnumMap) {
			keyType = (Class<T>) ReflectionUtils.getField(KEY_TYPE_FIELD, map);
		}
		return keyType;
	}

	/**
	 * 克隆一个map
	 * 
	 * @param <M>
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Map<K, V>, K, V> M clone(M map) {
		Assert.requiredArgument(map != null, "map");
		if (map instanceof Cloneable) {
			return ReflectionUtils.clone((Cloneable) map);
		} else {
			M cloneMap = (M) createMap(map.getClass(), getEnumMapKeyType(map), map.size());
			cloneMap.putAll(map);
			return cloneMap;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getEnumSetElementType(@SuppressWarnings("rawtypes") Collection collection) {
		Class<T> elementType = null;
		if (collection instanceof EnumSet) {
			elementType = (Class<T>) ReflectionUtils.getField(ELEMENT_TYPE_FIELD, collection);
		}
		return elementType;
	}

	/**
	 * 克隆一个collection
	 * 
	 * @param <C>
	 * @param <E>
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <C extends Collection<E>, E> C clone(C collection) {
		Assert.requiredArgument(collection != null, "collection");
		if (collection instanceof Cloneable) {
			return ReflectionUtils.clone((Cloneable) collection);
		} else {
			C cloneCollection = (C) createCollection(collection.getClass(), getEnumSetElementType(collection),
					collection.size());
			cloneCollection.addAll(collection);
			return cloneCollection;
		}
	}

	public static <K, V, SK, SV> Map<K, V> convert(Map<? extends SK, ? extends SV> sourceMap,
			Converter<SK, K> keyConverter, Converter<SV, V> valueConverter) {
		if (CollectionUtils.isEmpty(sourceMap)) {
			return Collections.emptyMap();
		}

		Map<K, V> targetMap = createMap(sourceMap.getClass(), getEnumMapKeyType(sourceMap), sourceMap.size());
		for (Entry<? extends SK, ? extends SV> entry : sourceMap.entrySet()) {
			K key = keyConverter.convert(entry.getKey());
			V value = valueConverter.convert(entry.getValue());
			targetMap.put(key, value);
		}
		return targetMap;
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
}
