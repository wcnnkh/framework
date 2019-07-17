package scw.data.utils;

/**
 * 阻塞队列
 * @author shuchaowen
 *
 * @param <E>
 */
public interface BlockingQueue<E> {
	/**
	 * 将指定元素插入此队列中，将等待可用的空间（如果有必要）。
	 * 
	 * @param e
	 */
	void put(E e) throws InterruptedException;

	/**
	 * 获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。
	 * 
	 * @return
	 */
	E take() throws InterruptedException;
}
