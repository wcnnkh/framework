package scw.core.cglib.core.internal;

public interface Function<K, V> {
    V apply(K key);
}
