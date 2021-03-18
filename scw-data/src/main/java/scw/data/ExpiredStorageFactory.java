package scw.data;

public interface ExpiredStorageFactory {
	ExpiredStorage getExpiredCache(int exp);
}
