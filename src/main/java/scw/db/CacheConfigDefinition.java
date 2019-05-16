package scw.db;

import java.util.concurrent.TimeUnit;

import scw.db.cache.CacheType;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public final class CacheConfigDefinition {
	private CacheType cacheType;
	private int exp;
	private boolean fullKeys;

	public CacheConfigDefinition(Class<?> clz) {
		TableInfo tableInfo = ORMUtils.getTableInfo(clz);
		Cache cache = tableInfo.getAnnotation(Cache.class);
		if (cache != null) {
			this.cacheType = cache.type();
			this.fullKeys = cache.fullKeys();
			this.exp = cacheType == CacheType.full ? 0 : (int) cache.timeUnit().toSeconds(cache.exp());
		}
	}

	public CacheConfigDefinition(CacheType cacheType, TimeUnit timeUnit, int exp, boolean fullKeys) {
		this.cacheType = cacheType;
		this.exp = cacheType == CacheType.full ? 0 : (int) timeUnit.toSeconds(exp);
		this.fullKeys = fullKeys;
	}

	public CacheType getCacheType() {
		return cacheType;
	}

	public int getExp() {
		return exp;
	}

	public boolean isFullKeys() {
		return fullKeys;
	}

	public boolean isEmpty() {
		return cacheType == null;
	}
}
