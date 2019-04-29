package scw.data.utils.queue;

import java.util.concurrent.TimeUnit;

public interface Queue<E> {
	/**
	 * 将指定元素插入到此队列的尾部（如果立即可行且不会超出此队列的容量），在成功时返回 true，如果此队列已满，则返回 false。
	 * 
	 * @param e
	 * @return
	 */
	boolean offer(E e);
	
	/**
	 * 获取但不移除此队列的头；如果此队列为空，则返回 null。
	 * 
	 * @return
	 */
	E peek();

	/**
	 * 获取并移除此队列的头，如果此队列为空，则返回 null。
	 * 
	 * @return
	 */
	E poll();

	/**
	 * 将指定元素插入到此队列的尾部，如有必要，则等待指定的时间以使空间变得可用
	 * 
	 * @param e
	 * @param timeout
	 * @param unit
	 * @return
	 */
	boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
	 * 将指定元素插入到此队列的尾部，如有必要，则等待空间变得可用。
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
