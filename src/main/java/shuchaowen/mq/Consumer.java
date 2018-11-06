package shuchaowen.mq;

public interface Consumer<T>{
	/**
	 * 消费者
	 * @param message
	 * @throws Exception 如果出现异常则重试此消息
	 */
	void handler(T message) throws Exception;
}
