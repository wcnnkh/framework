package run.soeasy.framework.core.collection;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;

/**
 * 支持软引用或弱引用的并发哈希映射，用于实现内存敏感的缓存。
 * 该类在多线程环境下提供高效的操作，同时允许垃圾回收器在内存不足时自动回收不再使用的条目。
 * 
 * <p>设计特点：
 * <ul>
 *   <li>基于分段锁机制实现高并发性能，允许多个线程同时访问不同段</li>
 *   <li>支持软引用(SOFT)和弱引用(WEAK)两种引用类型</li>
 *   <li>自动清理被垃圾回收的条目，无需显式调用清理方法</li>
 *   <li>通过负载因子和并发级别参数优化内存使用和性能</li>
 * </ul>
 * 
 * <p>使用注意：
 * <ul>
 *   <li>由于使用引用类型，无法保证存入的条目会一直存在</li>
 *   <li>支持null键和null值</li>
 *   <li>迭代器是弱一致性的，可能反映创建后的部分修改</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <K> 键类型
 * @param <V> 值类型
 * @see ConcurrentHashMap
 * @see SoftReference
 * @see WeakReference
 */
public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    /** 默认初始容量 */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** 默认负载因子，控制扩容阈值 */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /** 默认并发级别，决定分段锁数量 */
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    /** 默认引用类型为软引用 */
    private static final ReferenceType DEFAULT_REFERENCE_TYPE = ReferenceType.SOFT;

    /** 最大并发级别，限制分段锁数组大小 */
    private static final int MAXIMUM_CONCURRENCY_LEVEL = 1 << 16;

    /** 最大段大小，限制每个段的哈希表容量 */
    private static final int MAXIMUM_SEGMENT_SIZE = 1 << 30;

    /** 分段锁数组，通过高位哈希值索引 */
    private final Segment[] segments;

    /** 负载因子，控制哈希表的扩容时机 */
    private final float loadFactor;

    /** 引用类型：软引用或弱引用 */
    private final ReferenceType referenceType;

    /** 位移值，用于计算分段索引和哈希表大小 */
    private final int shift;

    /** 延迟初始化的键值对集合 */
    private volatile Set<Map.Entry<K, V>> entrySet;

    /**
     * 使用默认参数创建实例：
     * 初始容量=16，负载因子=0.75，并发级别=16，引用类型=软引用
     */
    public ConcurrentReferenceHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL, DEFAULT_REFERENCE_TYPE);
    }

    /**
     * 使用指定初始容量创建实例，其他参数使用默认值
     * 
     * @param initialCapacity 初始容量，必须非负
     */
    public ConcurrentReferenceHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL, DEFAULT_REFERENCE_TYPE);
    }

    /**
     * 使用指定初始容量和负载因子创建实例，其他参数使用默认值
     * 
     * @param initialCapacity 初始容量，必须非负
     * @param loadFactor      负载因子，必须大于0
     */
    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, DEFAULT_CONCURRENCY_LEVEL, DEFAULT_REFERENCE_TYPE);
    }

    /**
     * 使用指定初始容量和并发级别创建实例，其他参数使用默认值
     * 
     * @param initialCapacity  初始容量，必须非负
     * @param concurrencyLevel 并发级别，必须大于0
     */
    public ConcurrentReferenceHashMap(int initialCapacity, int concurrencyLevel) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
    }

    /**
     * 使用指定初始容量和引用类型创建实例，其他参数使用默认值
     * 
     * @param initialCapacity 初始容量，必须非负
     * @param referenceType   引用类型（软引用或弱引用）
     */
    public ConcurrentReferenceHashMap(int initialCapacity, ReferenceType referenceType) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL, referenceType);
    }

    /**
     * 使用指定初始容量、负载因子和并发级别创建实例，引用类型使用默认值
     * 
     * @param initialCapacity  初始容量，必须非负
     * @param loadFactor       负载因子，必须大于0
     * @param concurrencyLevel 并发级别，必须大于0
     */
    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        this(initialCapacity, loadFactor, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
    }

    /**
     * 使用指定参数创建实例
     * 
     * @param initialCapacity  初始容量，必须非负
     * @param loadFactor       负载因子，必须大于0
     * @param concurrencyLevel 并发级别，必须大于0
     * @param referenceType    引用类型（软引用或弱引用）
     */
    @SuppressWarnings("unchecked")
    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel,
            @NonNull ReferenceType referenceType) {
        Assert.isTrue(initialCapacity >= 0, "Initial capacity must not be negative");
        Assert.isTrue(loadFactor > 0f, "Load factor must be positive");
        Assert.isTrue(concurrencyLevel > 0, "Concurrency level must be positive");
        
        // 初始化参数
        this.loadFactor = loadFactor;
        this.shift = calculateShift(concurrencyLevel, MAXIMUM_CONCURRENCY_LEVEL);
        int size = 1 << this.shift;
        this.referenceType = referenceType;
        
        // 计算每个段的初始容量
        int roundedUpSegmentCapacity = (int) ((initialCapacity + size - 1L) / size);
        int initialSize = 1 << calculateShift(roundedUpSegmentCapacity, MAXIMUM_SEGMENT_SIZE);
        
        // 初始化分段数组
        Segment[] segments = (Segment[]) Array.newInstance(Segment.class, size);
        int resizeThreshold = (int) (initialSize * getLoadFactor());
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new Segment(initialSize, resizeThreshold);
        }
        this.segments = segments;
    }

    /**
     * 获取负载因子
     * 
     * @return 负载因子
     */
    protected final float getLoadFactor() {
        return this.loadFactor;
    }

    /**
     * 获取分段数组大小
     * 
     * @return 分段数组大小
     */
    protected final int getSegmentsSize() {
        return this.segments.length;
    }

    /**
     * 获取指定索引的段
     * 
     * @param index 段索引
     * @return 段实例
     */
    protected final Segment getSegment(int index) {
        return this.segments[index];
    }

    /**
     * 创建引用管理器，每个段会调用此方法创建自己的引用管理器
     * 
     * @return 引用管理器实例
     */
    protected ReferenceManager createReferenceManager() {
        return new ReferenceManager();
    }

    /**
     * 计算对象的哈希值，应用额外的哈希函数减少哈希冲突
     * 此方法使用与ConcurrentHashMap相同的Wang/Jenkins算法
     * 
     * @param o 待计算哈希值的对象，可为null
     * @return 计算后的哈希值
     */
    protected int getHash(Object o) {
        int hash = (o != null ? o.hashCode() : 0);
        // Wang/Jenkins哈希算法，增强哈希分布均匀性
        hash += (hash << 15) ^ 0xffffcd7d;
        hash ^= (hash >>> 10);
        hash += (hash << 3);
        hash ^= (hash >>> 6);
        hash += (hash << 2) + (hash << 14);
        hash ^= (hash >>> 16);
        return hash;
    }

    @Override
    public V get(Object key) {
        Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = (ref != null ? ref.get() : null);
        return (entry != null ? entry.getValue() : null);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = (ref != null ? ref.get() : null);
        return (entry != null ? entry.getValue() : defaultValue);
    }

    @Override
    public boolean containsKey(Object key) {
        Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = (ref != null ? ref.get() : null);
        return (entry != null && ObjectUtils.equals(entry.getKey(), key));
    }

    /**
     * 根据键获取引用，支持在必要时重组段结构
     * 
     * @param key         键，可为null
     * @param restructure 重组策略
     * @return 引用实例，或null（如果未找到）
     */
    protected final Reference<K, V> getReference(Object key, Restructure restructure) {
        int hash = getHash(key);
        return getSegmentForHash(hash).getReference(key, hash, restructure);
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, true);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return put(key, value, false);
    }

    /**
     * 内部实现的put方法，支持控制是否覆盖现有值
     * 
     * @param key             键
     * @param value           值
     * @param overwriteExisting 是否覆盖现有值
     * @return 旧值，或null（如果不存在）
     */
    private V put(final K key, final V value, final boolean overwriteExisting) {
        return doTask(key, new Task<V>(TaskOption.RESTRUCTURE_BEFORE, TaskOption.RESIZE) {
            @Override
            protected V execute(Reference<K, V> ref, Entry<K, V> entry, Entries<V> entries) {
                if (entry != null) {
                    V oldValue = entry.getValue();
                    if (overwriteExisting) {
                        entry.setValue(value);
                    }
                    return oldValue;
                }
                Assert.state(entries != null, "No entries segment");
                entries.add(value);
                return null;
            }
        });
    }

    @Override
    public V remove(Object key) {
        return doTask(key, new Task<V>(TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY) {
            @Override
            protected V execute(Reference<K, V> ref, Entry<K, V> entry) {
                if (entry != null) {
                    if (ref != null) {
                        ref.release();
                    }
                    return entry.value;
                }
                return null;
            }
        });
    }

    @Override
    public boolean remove(Object key, final Object value) {
        Boolean result = doTask(key, new Task<Boolean>(TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY) {
            @Override
            protected Boolean execute(Reference<K, V> ref, Entry<K, V> entry) {
                if (entry != null && ObjectUtils.equals(entry.getValue(), value)) {
                    if (ref != null) {
                        ref.release();
                    }
                    return true;
                }
                return false;
            }
        });
        return (Boolean.TRUE.equals(result));
    }

    @Override
    public boolean replace(K key, final V oldValue, final V newValue) {
        Boolean result = doTask(key, new Task<Boolean>(TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY) {
            @Override
            protected Boolean execute(Reference<K, V> ref, Entry<K, V> entry) {
                if (entry != null && ObjectUtils.equals(entry.getValue(), oldValue)) {
                    entry.setValue(newValue);
                    return true;
                }
                return false;
            }
        });
        return (Boolean.TRUE.equals(result));
    }

    @Override
    public V replace(K key, final V value) {
        return doTask(key, new Task<V>(TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY) {
            @Override
            protected V execute(Reference<K, V> ref, Entry<K, V> entry) {
                if (entry != null) {
                    V oldValue = entry.getValue();
                    entry.setValue(value);
                    return oldValue;
                }
                return null;
            }
        });
    }

    @Override
    public void clear() {
        for (Segment segment : this.segments) {
            segment.clear();
        }
    }

    /**
     * 清除所有被垃圾回收的条目
     * 通常在频繁读取但很少更新的场景中使用，主动触发垃圾回收条目的清理
     */
    public void purgeUnreferencedEntries() {
        for (Segment segment : this.segments) {
            segment.restructureIfNecessary(false);
        }
    }

    @Override
    public int size() {
        int size = 0;
        for (Segment segment : this.segments) {
            size += segment.getCount();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (Segment segment : this.segments) {
            if (segment.getCount() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = new EntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }

    /**
     * 在指定段上执行任务
     * 
     * @param key  键
     * @param task 任务
     * @return 任务执行结果
     */
    private <T> T doTask(Object key, Task<T> task) {
        int hash = getHash(key);
        return getSegmentForHash(hash).doTask(hash, key, task);
    }

    /**
     * 根据哈希值获取对应的段
     * 
     * @param hash 哈希值
     * @return 段实例
     */
    private Segment getSegmentForHash(int hash) {
        return this.segments[(hash >>> (32 - this.shift)) & (this.segments.length - 1)];
    }

    /**
     * 计算位移值，用于生成2的幂次方大小的数组
     * 
     * @param minimumValue 最小值
     * @param maximumValue 最大值
     * @return 位移值，使1 &lt;&lt; shift生成的大小在min和max之间
     */
    protected static int calculateShift(int minimumValue, int maximumValue) {
        int shift = 0;
        int value = 1;
        while (value < minimumValue && value < maximumValue) {
            value <<= 1;
            shift++;
        }
        return shift;
    }

    /**
     * 支持的引用类型
     */
    public enum ReferenceType {

        /** 使用软引用，在内存不足时可能被垃圾回收 */
        SOFT,

        /** 使用弱引用，在对象没有强引用时会被立即回收 */
        WEAK
    }

    /**
     * 分段锁实现，用于支持高并发操作
     * 每个段维护自己的哈希表和锁，不同段的操作可以并发执行
     */
    @SuppressWarnings("serial")
    protected final class Segment extends ReentrantLock {

        private final ReferenceManager referenceManager;

        private final int initialSize;

        /** 引用数组，通过低位哈希值索引 */
        private volatile Reference<K, V>[] references;

        /** 引用总数，包括已被垃圾回收但尚未清理的引用 */
        private final AtomicInteger count = new AtomicInteger();

        /** 扩容阈值，当count超过此值时触发扩容 */
        private int resizeThreshold;

        public Segment(int initialSize, int resizeThreshold) {
            this.referenceManager = createReferenceManager();
            this.initialSize = initialSize;
            this.references = createReferenceArray(initialSize);
            this.resizeThreshold = resizeThreshold;
        }

        /**
         * 根据键和哈希值查找引用
         * 
         * @param key       键
         * @param hash      哈希值
         * @param restructure 重组策略
         * @return 引用实例，或null（如果未找到）
         */
        public Reference<K, V> getReference(Object key, int hash, Restructure restructure) {
            if (restructure == Restructure.WHEN_NECESSARY) {
                restructureIfNecessary(false);
            }
            if (this.count.get() == 0) {
                return null;
            }
            // 使用本地副本防止其他线程修改
            Reference<K, V>[] references = this.references;
            int index = getIndex(hash, references);
            Reference<K, V> head = references[index];
            return findInChain(head, key, hash);
        }

        /**
         * 在段上执行任务，执行期间会锁定段
         * 
         * @param hash 键的哈希值
         * @param key  键
         * @param task 任务
         * @return 任务执行结果
         */
        public <T> T doTask(final int hash, final Object key, final Task<T> task) {
            boolean resize = task.hasOption(TaskOption.RESIZE);
            if (task.hasOption(TaskOption.RESTRUCTURE_BEFORE)) {
                restructureIfNecessary(resize);
            }
            if (task.hasOption(TaskOption.SKIP_IF_EMPTY) && this.count.get() == 0) {
                return task.execute(null, null, null);
            }
            lock();
            try {
                final int index = getIndex(hash, this.references);
                final Reference<K, V> head = this.references[index];
                Reference<K, V> ref = findInChain(head, key, hash);
                Entry<K, V> entry = (ref != null ? ref.get() : null);
                Entries<V> entries = value -> {
                    @SuppressWarnings("unchecked")
                    Entry<K, V> newEntry = new Entry<>((K) key, value);
                    Reference<K, V> newReference = Segment.this.referenceManager.createReference(newEntry, hash, head);
                    Segment.this.references[index] = newReference;
                    Segment.this.count.incrementAndGet();
                };
                return task.execute(ref, entry, entries);
            } finally {
                unlock();
                if (task.hasOption(TaskOption.RESTRUCTURE_AFTER)) {
                    restructureIfNecessary(resize);
                }
            }
        }

        /**
         * 清空段内所有条目
         */
        public void clear() {
            if (this.count.get() == 0) {
                return;
            }
            lock();
            try {
                this.references = createReferenceArray(this.initialSize);
                this.resizeThreshold = (int) (this.references.length * getLoadFactor());
                this.count.set(0);
            } finally {
                unlock();
            }
        }

        /**
         * 在必要时重组段结构
         * 包括清理被垃圾回收的条目和扩容操作
         * 
         * @param allowResize 是否允许扩容
         */
        protected final void restructureIfNecessary(boolean allowResize) {
            int currCount = this.count.get();
            boolean needsResize = allowResize && (currCount > 0 && currCount >= this.resizeThreshold);
            Reference<K, V> ref = this.referenceManager.pollForPurge();
            if (ref != null || (needsResize)) {
                restructure(allowResize, ref);
            }
        }

        /**
         * 重组段结构，清理无效引用并在必要时扩容
         * 
         * @param allowResize 是否允许扩容
         * @param ref         待清理的引用
         */
        private void restructure(boolean allowResize, Reference<K, V> ref) {
            boolean needsResize;
            lock();
            try {
                int countAfterRestructure = this.count.get();
                Set<Reference<K, V>> toPurge = Collections.emptySet();
                if (ref != null) {
                    toPurge = new HashSet<>();
                    while (ref != null) {
                        toPurge.add(ref);
                        ref = this.referenceManager.pollForPurge();
                    }
                }
                countAfterRestructure -= toPurge.size();

                // 重新计算是否需要扩容
                needsResize = (countAfterRestructure > 0 && countAfterRestructure >= this.resizeThreshold);
                boolean resizing = false;
                int restructureSize = this.references.length;
                if (allowResize && needsResize && restructureSize < MAXIMUM_SEGMENT_SIZE) {
                    restructureSize <<= 1;
                    resizing = true;
                }

                // 创建新数组或重用现有数组
                Reference<K, V>[] restructured = (resizing ? createReferenceArray(restructureSize) : this.references);

                // 重组哈希表
                for (int i = 0; i < this.references.length; i++) {
                    ref = this.references[i];
                    if (!resizing) {
                        restructured[i] = null;
                    }
                    while (ref != null) {
                        if (!toPurge.contains(ref)) {
                            Entry<K, V> entry = ref.get();
                            if (entry != null) {
                                int index = getIndex(ref.getHash(), restructured);
                                restructured[index] = this.referenceManager.createReference(entry, ref.getHash(),
                                        restructured[index]);
                            }
                        }
                        ref = ref.getNext();
                    }
                }

                // 更新volatile成员
                if (resizing) {
                    this.references = restructured;
                    this.resizeThreshold = (int) (this.references.length * getLoadFactor());
                }
                this.count.set(Math.max(countAfterRestructure, 0));
            } finally {
                unlock();
            }
        }

        /**
         * 在链表中查找匹配的引用
         * 
         * @param ref   链表头
         * @param key   键
         * @param hash  哈希值
         * @return 匹配的引用，或null（如果未找到）
         */
        private Reference<K, V> findInChain(Reference<K, V> ref, Object key, int hash) {
            Reference<K, V> currRef = ref;
            while (currRef != null) {
                if (currRef.getHash() == hash) {
                    Entry<K, V> entry = currRef.get();
                    if (entry != null) {
                        K entryKey = entry.getKey();
                        if (ObjectUtils.equals(entryKey, key)) {
                            return currRef;
                        }
                    }
                }
                currRef = currRef.getNext();
            }
            return null;
        }

        @SuppressWarnings({ "unchecked" })
        private Reference<K, V>[] createReferenceArray(int size) {
            return new Reference[size];
        }

        private int getIndex(int hash, Reference<K, V>[] references) {
            return (hash & (references.length - 1));
        }

        /**
         * 获取当前引用数组大小
         * 
         * @return 引用数组大小
         */
        public final int getSize() {
            return this.references.length;
        }

        /**
         * 获取当前段中的引用计数
         * 
         * @return 引用计数
         */
        public final int getCount() {
            return this.count.get();
        }
    }

    /**
     * 条目引用接口，封装对Entry的引用
     * 通常由SoftReference或WeakReference实现
     */
    protected interface Reference<K, V> {

        /**
         * 获取引用的条目，若条目已被垃圾回收则返回null
         */
        Entry<K, V> get();

        /**
         * 获取引用的哈希值
         */
        int getHash();

        /**
         * 获取链表中的下一个引用
         */
        Reference<K, V> getNext();

        /**
         * 释放此引用并确保它会被清理
         */
        void release();
    }

    /**
     * 键值对条目
     */
    protected static final class Entry<K, V> implements Map.Entry<K, V> {

        private final K key;

        private volatile V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V previous = this.value;
            this.value = value;
            return previous;
        }

        @Override
        public String toString() {
            return (this.key + "=" + this.value);
        }

        @Override
        public final boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof Map.Entry<?, ?>)) {
                return false;
            }

            Map.Entry<?, ?> otherEntry = (java.util.Map.Entry<?, ?>) other;
            return (ObjectUtils.equals(getKey(), otherEntry.getKey())
                    && ObjectUtils.equals(getValue(), otherEntry.getValue()));
        }

        @Override
        public final int hashCode() {
            return (ObjectUtils.hashCode(this.key) ^ ObjectUtils.hashCode(this.value));
        }
    }

    /**
     * 可在段上执行的任务接口
     */
    private abstract class Task<T> {

        private final EnumSet<TaskOption> options;

        public Task(TaskOption... options) {
            this.options = (options.length == 0 ? EnumSet.noneOf(TaskOption.class) : EnumSet.of(options[0], options));
        }

        public boolean hasOption(TaskOption option) {
            return this.options.contains(option);
        }

        /**
         * 执行任务
         * 
         * @param ref     找到的引用（或null）
         * @param entry   找到的条目（或null）
         * @param entries 操作条目的接口
         * @return 任务执行结果
         */
        protected T execute(Reference<K, V> ref, Entry<K, V> entry, Entries<V> entries) {
            return execute(ref, entry);
        }

        /**
         * 简化的任务执行方法，适用于不需要操作条目的任务
         * 
         * @param ref   找到的引用（或null）
         * @param entry 找到的条目（或null）
         * @return 任务执行结果
         */
        protected T execute(Reference<K, V> ref, Entry<K, V> entry) {
            return null;
        }
    }

    /**
     * 任务选项枚举
     */
    private enum TaskOption {

        /** 执行前重组段结构 */
        RESTRUCTURE_BEFORE,
        
        /** 执行后重组段结构 */
        RESTRUCTURE_AFTER,
        
        /** 段为空时跳过执行 */
        SKIP_IF_EMPTY,
        
        /** 允许扩容 */
        RESIZE
    }

    /**
     * 条目操作接口
     */
    private interface Entries<V> {

        /**
         * 添加新条目
         * 
         * @param value 值
         */
        void add(V value);
    }

    /**
     * 键值对集合实现
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (java.util.Map.Entry<?, ?>) o;
                Reference<K, V> ref = ConcurrentReferenceHashMap.this.getReference(entry.getKey(), Restructure.NEVER);
                Entry<K, V> otherEntry = (ref != null ? ref.get() : null);
                if (otherEntry != null) {
                    return ObjectUtils.equals(entry.getValue(), otherEntry.getValue());
                }
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> entry = (java.util.Map.Entry<?, ?>) o;
                return ConcurrentReferenceHashMap.this.remove(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    /**
     * 键值对迭代器实现
     */
    private class EntryIterator implements Iterator<Map.Entry<K, V>> {

        private int segmentIndex;

        private int referenceIndex;

        private Reference<K, V>[] references;

        private Reference<K, V> reference;

        private Entry<K, V> next;

        private Entry<K, V> last;

        public EntryIterator() {
            moveToNextSegment();
        }

        @Override
        public boolean hasNext() {
            getNextIfNecessary();
            return (this.next != null);
        }

        @Override
        public Entry<K, V> next() {
            getNextIfNecessary();
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.last = this.next;
            this.next = null;
            return this.last;
        }

        private void getNextIfNecessary() {
            while (this.next == null) {
                moveToNextReference();
                if (this.reference == null) {
                    return;
                }
                this.next = this.reference.get();
            }
        }

        private void moveToNextReference() {
            if (this.reference != null) {
                this.reference = this.reference.getNext();
            }
            while (this.reference == null && this.references != null) {
                if (this.referenceIndex >= this.references.length) {
                    moveToNextSegment();
                    this.referenceIndex = 0;
                } else {
                    this.reference = this.references[this.referenceIndex];
                    this.referenceIndex++;
                }
            }
        }

        private void moveToNextSegment() {
            this.reference = null;
            this.references = null;
            if (this.segmentIndex < ConcurrentReferenceHashMap.this.segments.length) {
                this.references = ConcurrentReferenceHashMap.this.segments[this.segmentIndex].references;
                this.segmentIndex++;
            }
        }

        @Override
        public void remove() {
            Assert.state(this.last != null, "No element to remove");
            ConcurrentReferenceHashMap.this.remove(this.last.getKey());
            this.last = null;
        }
    }

    /**
     * 重组策略枚举
     */
    protected enum Restructure {

        /** 必要时重组 */
        WHEN_NECESSARY,
        
        /** 从不重组 */
        NEVER
    }

    /**
     * 引用管理器，负责创建和管理引用
     */
    protected class ReferenceManager {

        private final ReferenceQueue<Entry<K, V>> queue = new ReferenceQueue<>();

        /**
         * 创建新的引用
         * 
         * @param entry 条目
         * @param hash  哈希值
         * @param next  链表中的下一个引用
         * @return 新的引用实例
         */
        public Reference<K, V> createReference(Entry<K, V> entry, int hash, Reference<K, V> next) {
            if (ConcurrentReferenceHashMap.this.referenceType == ReferenceType.WEAK) {
                return new WeakEntryReference<>(entry, hash, next, this.queue);
            }
            return new SoftEntryReference<>(entry, hash, next, this.queue);
        }

        /**
         * 获取需要清理的引用
         * 
         * @return 需要清理的引用，或null（如果没有）
         */
        @SuppressWarnings("unchecked")
        public Reference<K, V> pollForPurge() {
            return (Reference<K, V>) this.queue.poll();
        }
    }

    /**
     * 软引用实现
     */
    private static final class SoftEntryReference<K, V> extends SoftReference<Entry<K, V>> implements Reference<K, V> {
        private final int hash;

        private final Reference<K, V> nextReference;

        public SoftEntryReference(Entry<K, V> entry, int hash, Reference<K, V> next,
                ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        /**
         * 返回此引用的哈希值，用于快速查找
         * 
         * @return 哈希值
         */
        @Override
        public int getHash() {
            return this.hash;
        }

        /**
         * 返回链表中的下一个引用，形成哈希冲突时的链表结构
         * 
         * @return 下一个引用，若无则返回null
         */
        @Override
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        /**
         * 释放此引用，将其加入引用队列以便清理
         * 同时清除引用以加速垃圾回收
         */
        @Override
        public void release() {
            enqueue();
            clear();
        }
    }

    /**
     * 弱引用实现，当没有强引用指向条目时会被立即回收
     */
    private static final class WeakEntryReference<K, V> extends WeakReference<Entry<K, V>> implements Reference<K, V> {

        private final int hash;

        private final Reference<K, V> nextReference;

        public WeakEntryReference(Entry<K, V> entry, int hash, Reference<K, V> next,
                ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        /**
         * 返回此引用的哈希值，用于快速查找
         * 
         * @return 哈希值
         */
        @Override
        public int getHash() {
            return this.hash;
        }

        /**
         * 返回链表中的下一个引用，形成哈希冲突时的链表结构
         * 
         * @return 下一个引用，若无则返回null
         */
        @Override
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        /**
         * 释放此引用，将其加入引用队列以便清理
         * 同时清除引用以加速垃圾回收
         */
        @Override
        public void release() {
            enqueue();
            clear();
        }
    }
}