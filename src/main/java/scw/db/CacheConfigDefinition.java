package scw.db;

import scw.db.annotation.CacheConfig;
import scw.db.cache.CacheType;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public final class CacheConfigDefinition {
	private CacheType cacheType;
	private int exp;
	private boolean fullKeys;

	public CacheConfigDefinition(Class<?> clz) {
		TableInfo tableInfo = ORMUtils.getTableInfo(clz);
		CacheConfig cacheConfig = tableInfo.getAnnotation(CacheConfig.class);
		if (cacheConfig != null) {
			this.cacheType = cacheConfig.type();
			this.fullKeys = cacheConfig.fullKeys();
			this.exp = cacheType == CacheType.full? 0:(int) cacheConfig.timeUnit().toSeconds(cacheConfig.exp());
		}
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
