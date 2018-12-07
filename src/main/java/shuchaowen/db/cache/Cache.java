package shuchaowen.db.cache;

public interface Cache {
	<T> T get();
	
	void delete();
}
