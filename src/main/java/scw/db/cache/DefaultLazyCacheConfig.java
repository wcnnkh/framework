package scw.db.cache;

import scw.core.utils.XTime;

public final class DefaultLazyCacheConfig implements LazyCacheConfig {
	private final int exp;
	private final boolean keys;
	private final boolean disable;

	public DefaultLazyCacheConfig(int exp, boolean keys, boolean disable) {
		this.exp = exp;
		this.keys = keys;
		this.disable = disable;
	}

	public DefaultLazyCacheConfig(LazyCache lazyCache) {
		if (lazyCache == null) {
			this.exp = (int) (XTime.ONE_DAY * 2 / 1000);
			this.keys = false;
			this.disable = false;
		} else {
			this.exp = (int) Math.max(XTime.ONE_MINUTE / 1000, lazyCache.expTimeUnit().toSeconds(lazyCache.exp()));
			this.keys = lazyCache.keys();
			this.disable = lazyCache.disable();
		}
	}

	public int getExp() {
		return exp;
	}

	public boolean isKeys() {
		return keys;
	}

	public boolean isDisable() {
		return disable;
	}

}
