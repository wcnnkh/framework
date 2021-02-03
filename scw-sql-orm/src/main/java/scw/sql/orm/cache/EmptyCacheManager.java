package scw.sql.orm.cache;

import java.util.Collection;
import java.util.Map;

public final class EmptyCacheManager implements CacheManager{

	public void save(Object bean) {
		//ignore
	}

	public void update(Object bean) {
		//ignore
	}

	public void delete(Object bean) {
		//ignore
	}

	public void deleteById(Class<?> type, Object... params) {
		//ignore
	}

	public void saveOrUpdate(Object bean) {
		//ignore
	}

	public <T> T getById(Class<T> type, Object... params) {
		//ignore
		return null;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) {
		//ignore
		return null;
	}

	public boolean isSearchDB(Class<?> type, Object... params) {
		return true;
	}

}
