package io.basc.framework.util.stream;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;

import io.basc.framework.util.Wrapper;

public abstract class BaseStreamWrapper<T, W extends BaseStream<T, W>> extends Wrapper<W> implements BaseStream<T, W> {
	/**
	 * 默认是自动关闭的
	 * @see #afterExecution()
	 */
	private boolean autoClose = true;

	public BaseStreamWrapper(W wrappedTarget) {
		super(wrappedTarget);
		if (wrappedTarget instanceof BaseStreamWrapper) {
			this.autoClose = ((BaseStreamWrapper<?, ?>) wrappedTarget).autoClose;
		}
	}

	/**
	 * 是否应该自动关闭<br/>
	 * 并非所有情况都适用，例如调用iterator/spliterator方法或获取到此对象后未调用任何方法
	 * 
	 * @see #afterExecution()
	 * @return
	 */
	public boolean isAutoClose() {
		return autoClose;
	}

	/**
	 * 是否应该自动关闭
	 * 
	 * @param autoClose
	 */
	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	/**
	 * 执行前
	 */
	protected void beforeExecution() {
	}

	/**
	 * 在执行完后
	 */
	protected void afterExecution() {
		if (isAutoClose()) {
			close();
		}
	}

	@Override
	public void close() {
		wrappedTarget.close();
	}

	@Override
	public Iterator<T> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public Spliterator<T> spliterator() {
		return wrappedTarget.spliterator();
	}

	@Override
	public boolean isParallel() {
		return wrappedTarget.isParallel();
	}
}