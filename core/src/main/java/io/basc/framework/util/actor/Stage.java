package io.basc.framework.util.actor;

import io.basc.framework.util.Listener;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;

public class Stage implements ListenableRegistration<Stage>, Receipt {
	private enum State {
		CANCELLED, FAILURE, NEW, SUCCESS
	}

	private Throwable cause;

	private final DisposableExchange<Stage> exchange = new DisposableExchange<>();

	private Object result = null;

	private State state = State.NEW;

	@Override
	public boolean cancel() {
		synchronized (this) {
			if (state != State.NEW) {
				return false;
			}
			state = State.CANCELLED;
			exchange.publish(this);
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
			exchange.publish(this);
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
				exchange.publish(this);
				listener.accept(this);
				return Receipt.success();
			}
			return exchange.registerListener(listener);
		}
	}

	public void reset() {
		synchronized (this) {
			result = null;
			cause = null;
			state = State.NEW;
			exchange.clear();
		}
	}

	public void success(Object result) {
		synchronized (this) {
			this.state = State.SUCCESS;
			this.result = result;
			exchange.publish(this);
		}
	}
}
