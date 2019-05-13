package scw.mq;

public interface Producer<T> {
	/**
	 * 推送数据
	 * 
	 * @param message
	 */
	void push(T message);
}
