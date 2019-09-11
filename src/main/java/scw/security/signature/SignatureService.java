package scw.security.signature;

public interface SignatureService<K, V> {
	V signature(K message) throws Exception;
}
