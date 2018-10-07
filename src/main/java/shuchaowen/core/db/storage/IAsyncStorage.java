package shuchaowen.core.db.storage;

public interface IAsyncStorage {
	
	void producer(ExecuteInfo executeInfo);
	
	void consumer(ExecuteInfo executeInfo);
}
