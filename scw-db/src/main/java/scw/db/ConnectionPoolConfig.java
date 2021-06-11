package scw.db;

public class ConnectionPoolConfig extends ConnectionConfig {
	// 连接池配置
	private int minSize;
	private int maxSize;

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
