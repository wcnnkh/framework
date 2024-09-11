package io.basc.framework.util.observe.event;

import io.basc.framework.util.observe.Listenable;
import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Receipt;
import io.basc.framework.util.observe.Registration;

public class Stage implements Listenable<Stage>, Receipt {
	private enum State {
		CANCELLED, FAILURE, NEW, SUCCESS
	}

	private Throwable cause;

	private final ListenerQueue<Stage> queue = new ListenerQueue<>();

	private Object result = null;

	private State state = State.NEW;

	@Override
	public boolean cancel() {
		synchronized (this) {
			if (state != State.NEW) {
				return false;
			}
			state = State.CANCELLED;
			queue.publish(this);
			return true;
		}
	}

	@Override
	public Throwable cause() {
		synchronized (this) {
			return cause;
		}
	}

	public void failure(Throwable ex) {
		synchronized (this) {
			this.state = State.FAILURE;
			this.cause = ex;
			queue.publish(this);
		}
	}

	public Object getResult() {
		return result;
	}

	@Override
	public boolean isCancellable() {
		// 没完成前都可以取消
		return !isDone();
	}

	@Override
	public boolean isCancelled() {
		synchronized (this) {
			return state == State.CANCELLED;
		}
	}

	@Override
	public boolean isDone() {
		synchronized (this) {
			return state != State.NEW;
		}
	}

	@Override
	public boolean isSuccess() {
		synchronized (this) {
			return state == State.SUCCESS;
		}
	}

	@Override
	public Registration registerListener(Listener<? super Stage> listener) {
		synchronized (this) {
			if (isDone()) {
				// 已经结果
				queue.publish(this);
				listener.accept(this);
				return Receipt.success();
			}
			return queue.registerListener(listener);
		}
	}

	public void reset() {
		synchronized (this) {
			result = null;
			cause = null;
			state = State.NEW;
			queue.clear();
		}
	}

	public void success(Object result) {
		synchronized (this) {
			this.state = State.SUCCESS;
			this.result = result;
			queue.publish(this);
		}
	}
}
