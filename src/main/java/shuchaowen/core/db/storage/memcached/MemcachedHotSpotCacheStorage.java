package shuchaowen.core.db.storage.memcached;

import java.util.Collection;
import java.util.Map;

import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.storage.AbstractCacheStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.util.XTime;

public class MemcachedHotSpotCacheStorage extends AbstractCacheStorage{
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	private final String prefix;
	private final int exp;// 过期时间
	private final Memcached memcached;
	
	public MemcachedHotSpotCacheStorage(AbstractDB db, Memcached memcached, Storage storage){
		this(db, "", DEFAULT_EXP, memcached, storage);
	}
	
	public MemcachedHotSpotCacheStorage(AbstractDB db, String prefix, int exp, Memcached memcached, Storage storage){
		super(db, storage);
		this.prefix = prefix;
		this.exp = exp;
		this.memcached = memcached;
	}

	@Override
	public <T> T getByIdFromCache(Class<T> type, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<PrimaryKeyParameter, T> getByIdFromCache(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveToCache(Collection<?> beans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateToCache(Collection<?> beans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveOrUpdateToCache(Collection<?> beans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteToCache(Collection<?> beans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getByIdExist(Class<?> type, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}
}
