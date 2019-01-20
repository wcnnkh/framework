package scw.database;

/**
 * 事务的配图
 * @author asus1
 *
 */
public class ContextConfig {
	private boolean autoCommit;// 是否自动提交
	private boolean selectCache;// 是否使用查询缓存
	private boolean debug;// 是否是调试模式

	public ContextConfig(boolean autoCommit, boolean selectCache,
			boolean debug) {
		this.autoCommit = autoCommit;
		this.selectCache = selectCache;
		this.debug = debug;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public boolean isSelectCache() {
		return selectCache;
	}

	public void setSelectCache(boolean selectCache) {
		this.selectCache = selectCache;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
