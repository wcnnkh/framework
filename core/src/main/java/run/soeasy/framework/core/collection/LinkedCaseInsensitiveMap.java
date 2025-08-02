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

/**
 * 不区分大小写的有序映射实现，继承自LinkedHashMap并提供键的大小写不敏感特性。
 * 该类维护原始键与小写键的映射关系，支持通过Locale定制大小写转换规则，
 * 适用于需要不区分键大小写的场景（如HTTP头字段、配置项等）。
 *
 * <p>核心特性：
 * <ul>
 *   <li>键的大小写不敏感：通过Locale将键转换为小写进行存储和查找</li>
 *   <li>保持插入顺序：继承LinkedHashMap的有序特性</li>
 *   <li>可定制Locale：支持通过构造函数指定大小写转换的Locale</li>
 *   <li>线程不安全：建议在单线程环境使用或外部同步</li>
 *   <li>完整实现Map接口：支持所有标准Map操作</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
 * map.put("User-Agent", "Mozilla");
 * map.put("user-agent", "Chrome"); // 实际会覆盖前者，因为键不区分大小写
 * System.out.println(map.get("USER-AGENT")); // 输出: Chrome
 * }</pre>
 *
 * @param <V> 值的类型
 * @see LinkedHashMap
 * @see Locale
 */
public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    /** 存储原始键值对的LinkedHashMap，保持插入顺序 */
    private final LinkedHashMap<String, V> targetMap;

    /** 存储小写键到原始键的映射，用于大小写不敏感查找 */
    private final HashMap<String, String> caseInsensitiveKeys;

    /** 用于键大小写转换的Locale */
    private final Locale locale;

    /** 缓存的keySet，避免重复创建 */
    private transient volatile Set<String> keySet;

    /** 缓存的values集合，避免重复创建 */
    private transient volatile Collection<V> values;

    /** 缓存的entrySet，避免重复创建 */
    private transient volatile Set<Entry<String, V>> entrySet;

    /**
     * 创建新的不区分大小写的映射，使用默认Locale进行键转换（默认小写）。
     * 初始容量为16，负载因子为0.75。
     *
     * @see #convertKey(String)
     */
    public LinkedCaseInsensitiveMap() {
        this((Locale) null);
    }

    /**
     * 创建新的不区分大小写的映射，使用指定Locale进行键转换。
     * 初始容量为16，负载因子为0.75。
     *
     * @param locale 用于键大小写转换的Locale，null表示使用默认Locale
     * @see #convertKey(String)
     */
    public LinkedCaseInsensitiveMap(Locale locale) {
        this(16, locale);
    }

    /**
     * 创建新的不区分大小写的映射，指定初始容量，使用默认Locale。
     *
     * @param initialCapacity 初始容量
     * @see #convertKey(String)
     */
    public LinkedCaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * 创建新的不区分大小写的映射，指定初始容量和Locale。
     *
     * @param initialCapacity 初始容量
     * @param locale          用于键大小写转换的Locale，null表示使用默认Locale
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
     * 克隆构造函数，创建与原映射完全相同的副本。
     * 副本将共享相同的Locale，但拥有独立的键值存储。
     */
    @SuppressWarnings("unchecked")
    private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
        this.targetMap = (LinkedHashMap<String, V>) other.targetMap.clone();
        this.caseInsensitiveKeys = (HashMap<String, String>) other.caseInsensitiveKeys.clone();
        this.locale = other.locale;
    }

    // --------------------- Map接口实现 ---------------------

    @Override
    public int size() {
        return this.targetMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && this.caseInsensitiveKeys.containsKey(convertKey((String) key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key));
            if (caseInsensitiveKey != null) {
                return this.targetMap.get(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key));
            if (caseInsensitiveKey != null) {
                return this.targetMap.get(caseInsensitiveKey);
            }
        }
        return defaultValue;
    }

    @Override
    public V put(String key, V value) {
        String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
        V oldKeyValue = null;
        if (oldKey != null && !oldKey.equals(key)) {
            oldKeyValue = this.targetMap.remove(oldKey);
        }
        V oldValue = this.targetMap.put(key, value);
        return (oldKeyValue != null ? oldKeyValue : oldValue);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }

        for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V putIfAbsent(String key, V value) {
        String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
        if (oldKey != null) {
            return this.targetMap.get(oldKey);
        }
        return this.targetMap.putIfAbsent(key, value);
    }

    @Override
    public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
        String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
        if (oldKey != null) {
            return this.targetMap.get(oldKey);
        }
        return this.targetMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V remove(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = removeCaseInsensitiveKey((String) key);
            if (caseInsensitiveKey != null) {
                return this.targetMap.remove(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Override
    public void clear() {
        this.caseInsensitiveKeys.clear();
        this.targetMap.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> keySet = this.keySet;
        if (keySet == null) {
            keySet = new KeySet(this.targetMap.keySet());
            this.keySet = keySet;
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = this.values;
        if (values == null) {
            values = new Values(this.targetMap.values());
            this.values = values;
        }
        return values;
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        Set<Entry<String, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = new EntrySet(this.targetMap.entrySet());
            this.entrySet = entrySet;
        }
        return entrySet;
    }

    // --------------------- 扩展功能 ---------------------

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

    /**
     * 返回此映射使用的Locale，用于键的大小写转换。
     *
     * @return 用于键转换的Locale
     * @see #LinkedCaseInsensitiveMap(Locale)
     * @see #convertKey(String)
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * 将给定键转换为用于存储的不区分大小写的键。
     * 默认为根据Locale将键转换为小写。
     *
     * @param key 用户指定的键
     * @return 存储使用的键
     * @see String#toLowerCase(Locale)
     */
    protected String convertKey(String key) {
        return key.toLowerCase(getLocale());
    }

    /**
     * 确定是否移除最旧的条目，默认返回false（不移除）。
     * 可重写此方法实现LRU等淘汰策略。
     *
     * @param eldest 候选移除的条目
     * @return true表示移除，false表示保留
     * @see LinkedHashMap#removeEldestEntry
     */
    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return false;
    }

    /**
     * 从caseInsensitiveKeys中移除指定键的小写映射。
     *
     * @param key 原始键
     * @return 被移除的原始键，若无则返回null
     */
    private String removeCaseInsensitiveKey(String key) {
        return this.caseInsensitiveKeys.remove(convertKey(key));
    }

    // --------------------- 内部类实现 ---------------------

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

        @Override
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

        EntrySet(Set<Entry<String, V>> delegate) {
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

        EntryIterator() {
            this.delegate = targetMap.entrySet().iterator();
        }

        protected Entry<String, V> nextEntry() {
            Entry<String, V> entry = this.delegate.next();
            this.last = entry;
            return entry;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public void remove() {
            this.delegate.remove();
            if (this.last != null) {
                removeCaseInsensitiveKey(this.last.getKey());
                this.last = null;
            }
        }
    }

    private class KeySetIterator extends EntryIterator<String> {
        @Override
        public String next() {
            return nextEntry().getKey();
        }
    }

    private class ValuesIterator extends EntryIterator<V> {
        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    private class EntrySetIterator extends EntryIterator<Entry<String, V>> {
        @Override
        public Entry<String, V> next() {
            return nextEntry();
        }
    }
}