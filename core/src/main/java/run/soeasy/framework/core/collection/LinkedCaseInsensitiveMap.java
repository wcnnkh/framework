package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private final LinkedHashMap<String, V> targetMap;

	private final HashMap<String, String> caseInsensitiveKeys;

	private final Locale locale;

	private transient volatile Set<String> keySet;

	private transient volatile Collection<V> values;

	private transient volatile Set<Entry<String, V>> entrySet;

	/**
	 * Create a new LinkedCaseInsensitiveMap that stores case-insensitive keys
	 * according to the default Locale (by default in lower case).
	 * 
	 * @see #convertKey(String)
	 */
	public LinkedCaseInsensitiveMap() {
		this((Locale) null);
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that stores case-insensitive keys
	 * according to the given Locale (by default in lower case).
	 * 
	 * @param locale the Locale to use for case-insensitive key conversion
	 * @see #convertKey(String)
	 */
	public LinkedCaseInsensitiveMap(Locale locale) {
		this(16, locale);
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap} with
	 * the given initial capacity and stores case-insensitive keys according to the
	 * default Locale (by default in lower case).
	 * 
	 * @param initialCapacity the initial capacity
	 * @see #convertKey(String)
	 */
	public LinkedCaseInsensitiveMap(int initialCapacity) {
		this(initialCapacity, null);
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap} with
	 * the given initial capacity and stores case-insensitive keys according to the
	 * given Locale (by default in lower case).
	 * 
	 * @param initialCapacity the initial capacity
	 * @param locale          the Locale to use for case-insensitive key conversion
	 * @see #convertKey(String)
	 */
	public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
		this.targetMap = new LinkedHashMap<String, V>(initialCapacity) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean containsKey(Object key) {
				return LinkedCaseInsensitiveMap.this.containsKey(key);
			}

			@Override
			protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
				boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
				if (doRemove) {
					removeCaseInsensitiveKey(eldest.getKey());
				}
				return doRemove;
			}
		};
		this.caseInsensitiveKeys = new HashMap<String, String>(initialCapacity);
		this.locale = (locale != null ? locale : Locale.getDefault());
	}

	/**
	 * Copy constructor.
	 */
	@SuppressWarnings("unchecked")
	private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
		this.targetMap = (LinkedHashMap<String, V>) other.targetMap.clone();
		this.caseInsensitiveKeys = (HashMap<String, String>) other.caseInsensitiveKeys.clone();
		this.locale = other.locale;
	}

	// Implementation of java.util.Map

	public int size() {
		return this.targetMap.size();
	}

	public boolean isEmpty() {
		return this.targetMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return (key instanceof String && this.caseInsensitiveKeys.containsKey(convertKey((String) key)));
	}

	public boolean containsValue(Object value) {
		return this.targetMap.containsValue(value);
	}

	public V get(Object key) {
		if (key instanceof String) {
			String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key));
			if (caseInsensitiveKey != null) {
				return this.targetMap.get(caseInsensitiveKey);
			}
		}
		return null;
	}

	public V getOrDefault(Object key, V defaultValue) {
		if (key instanceof String) {
			String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key));
			if (caseInsensitiveKey != null) {
				return this.targetMap.get(caseInsensitiveKey);
			}
		}
		return defaultValue;
	}

	public V put(String key, V value) {
		String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
		V oldKeyValue = null;
		if (oldKey != null && !oldKey.equals(key)) {
			oldKeyValue = this.targetMap.remove(oldKey);
		}
		V oldValue = this.targetMap.put(key, value);
		return (oldKeyValue != null ? oldKeyValue : oldValue);
	}

	public void putAll(Map<? extends String, ? extends V> map) {
		if (map.isEmpty()) {
			return;
		}

		for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public V putIfAbsent(String key, V value) {
		String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
		if (oldKey != null) {
			return this.targetMap.get(oldKey);
		}
		return this.targetMap.putIfAbsent(key, value);
	}

	public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
		String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
		if (oldKey != null) {
			return this.targetMap.get(oldKey);
		}
		return this.targetMap.computeIfAbsent(key, mappingFunction);
	}

	public V remove(Object key) {
		if (key instanceof String) {
			String caseInsensitiveKey = removeCaseInsensitiveKey((String) key);
			if (caseInsensitiveKey != null) {
				return this.targetMap.remove(caseInsensitiveKey);
			}
		}
		return null;
	}

	public void clear() {
		this.caseInsensitiveKeys.clear();
		this.targetMap.clear();
	}

	public Set<String> keySet() {
		Set<String> keySet = this.keySet;
		if (keySet == null) {
			keySet = new KeySet(this.targetMap.keySet());
			this.keySet = keySet;
		}
		return keySet;
	}

	public Collection<V> values() {
		Collection<V> values = this.values;
		if (values == null) {
			values = new Values(this.targetMap.values());
			this.values = values;
		}
		return values;
	}

	public Set<Entry<String, V>> entrySet() {
		Set<Entry<String, V>> entrySet = this.entrySet;
		if (entrySet == null) {
			entrySet = new EntrySet(this.targetMap.entrySet());
			this.entrySet = entrySet;
		}
		return entrySet;
	}

	@Override
	public LinkedCaseInsensitiveMap<V> clone() {
		return new LinkedCaseInsensitiveMap<V>(this);
	}

	@Override
	public boolean equals(Object obj) {
		return this.targetMap.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.targetMap.hashCode();
	}

	@Override
	public String toString() {
		return this.targetMap.toString();
	}

	// Specific to LinkedCaseInsensitiveMap

	/**
	 * Return the locale used by this {@code LinkedCaseInsensitiveMap}. Used for
	 * case-insensitive key conversion.
	 * 
	 * @see #LinkedCaseInsensitiveMap(Locale)
	 * @see #convertKey(String)
	 */
	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * Convert the given key to a case-insensitive key.
	 * <p>
	 * The default implementation converts the key to lower-case according to this
	 * Map's Locale.
	 * 
	 * @param key the user-specified key
	 * @return the key to use for storing
	 * @see String#toLowerCase(Locale)
	 */
	protected String convertKey(String key) {
		return key.toLowerCase(getLocale());
	}

	/**
	 * Determine whether this map should remove the given eldest entry.
	 * 
	 * @param eldest the candidate entry
	 * @return {@code true} for removing it, {@code false} for keeping it
	 * @see LinkedHashMap#removeEldestEntry
	 */
	protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
		return false;
	}

	private String removeCaseInsensitiveKey(String key) {
		return this.caseInsensitiveKeys.remove(convertKey(key));
	}

	private class KeySet extends AbstractSet<String> {
		private final Set<String> delegate;

		KeySet(Set<String> delegate) {
			this.delegate = delegate;
		}

		@Override
		public int size() {
			return this.delegate.size();
		}

		@Override
		public boolean contains(Object o) {
			return this.delegate.contains(o);
		}

		@Override
		public Iterator<String> iterator() {
			return new KeySetIterator();
		}

		@Override
		public boolean remove(Object o) {
			return LinkedCaseInsensitiveMap.this.remove(o) != null;
		}

		public void clear() {
			LinkedCaseInsensitiveMap.this.clear();
		}
	}

	private class Values extends AbstractCollection<V> {

		private final Collection<V> delegate;

		Values(Collection<V> delegate) {
			this.delegate = delegate;
		}

		@Override
		public int size() {
			return this.delegate.size();
		}

		@Override
		public boolean contains(Object o) {
			return this.delegate.contains(o);
		}

		@Override
		public Iterator<V> iterator() {
			return new ValuesIterator();
		}

		@Override
		public void clear() {
			LinkedCaseInsensitiveMap.this.clear();
		}
	}

	private class EntrySet extends AbstractSet<Entry<String, V>> {

		private final Set<Entry<String, V>> delegate;

		public EntrySet(Set<Entry<String, V>> delegate) {
			this.delegate = delegate;
		}

		@Override
		public int size() {
			return this.delegate.size();
		}

		@Override
		public boolean contains(Object o) {
			return this.delegate.contains(o);
		}

		@Override
		public Iterator<Entry<String, V>> iterator() {
			return new EntrySetIterator();
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			if (this.delegate.remove(o)) {
				removeCaseInsensitiveKey(((Map.Entry<String, V>) o).getKey());
				return true;
			}
			return false;
		}

		@Override
		public void clear() {
			this.delegate.clear();
			caseInsensitiveKeys.clear();
		}

	}

	private abstract class EntryIterator<T> implements Iterator<T> {

		private final Iterator<Entry<String, V>> delegate;

		private Entry<String, V> last;

		public EntryIterator() {
			this.delegate = targetMap.entrySet().iterator();
		}

		protected Entry<String, V> nextEntry() {
			Entry<String, V> entry = this.delegate.next();
			this.last = entry;
			return entry;
		}

		public boolean hasNext() {
			return this.delegate.hasNext();
		}

		public void remove() {
			this.delegate.remove();
			if (this.last != null) {
				removeCaseInsensitiveKey(this.last.getKey());
				this.last = null;
			}
		}
	}

	private class KeySetIterator extends EntryIterator<String> {

		public String next() {
			return nextEntry().getKey();
		}
	}

	private class ValuesIterator extends EntryIterator<V> {

		public V next() {
			return nextEntry().getValue();
		}
	}

	private class EntrySetIterator extends EntryIterator<Entry<String, V>> {

		public Entry<String, V> next() {
			return nextEntry();
		}
	}
}
