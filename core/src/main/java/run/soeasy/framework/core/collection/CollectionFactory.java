package run.soeasy.framework.core.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
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
import java.util.TreeMap;
import java.util.TreeSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.lang.InstanceFactory;
import run.soeasy.framework.core.lang.ResolvableType;

@Getter
@Setter
public class CollectionFactory implements InstanceFactory {
	private int capacity = 16;

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		return Map.class.isAssignableFrom(requiredType.getRawType())
				|| Collection.class.isAssignableFrom(requiredType.getRawType());
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		if (Map.class.isAssignableFrom(requiredType.getRawType())) {
			ResolvableType keyType = requiredType.as(Map.class).getActualTypeArgument(0);
			return createMap(requiredType.getRawType(), keyType == null ? null : keyType.getRawType(), capacity);
		}

		if (Collection.class.isAssignableFrom(requiredType.getRawType())) {
			ResolvableType elementType = requiredType.as(Collection.class).getActualTypeArgument(0);
			return createCollection(requiredType.getRawType(), elementType.getRawType(), capacity);
		}
		throw new UnsupportedOperationException(String.valueOf(requiredType));
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
	private static Class<? extends Enum> asEnumType(Class<?> enumType) {
		Assert.notNull(enumType, "Enum type must not be null");
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
}
