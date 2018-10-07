package shuchaowen.core.db.storage;

public interface IAsyncStorage {
	/**
	 * 生产
	 * @param executeInfo
	 */
	void producer(ExecuteInfo executeInfo);
	
	/**
	 * 消费
	 * @param executeInfo
	 */
	void consumer(ExecuteInfo executeInfo);
}
