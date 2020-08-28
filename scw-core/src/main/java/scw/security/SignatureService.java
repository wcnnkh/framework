package scw.security;

public interface SignatureService<K, V> {
	V signature(K message) throws Exception;
}
