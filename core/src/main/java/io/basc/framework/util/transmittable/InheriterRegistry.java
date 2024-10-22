package io.basc.framework.util.transmittable;

import java.util.concurrent.CopyOnWriteArraySet;

import io.basc.framework.util.Registration;
import io.basc.framework.util.register.DisposableRegistration;

public class InheriterRegistry<A, B> extends InheriterDecorator<InheriterCapture<A, B>, InheriterBackup<A, B>> {
	private final CopyOnWriteArraySet<Inheriter<A, B>> registers = new CopyOnWriteArraySet<>();

	@Override
	public InheriterCapture<A, B> capture() {
		InheriterCapture<A, B> capture = new InheriterCapture<>(registers.size());
		for (Inheriter<A, B> inheriter : registers) {
			capture.put(inheriter, inheriter.capture());
		}
		return capture;
	}

	@Override
	public InheriterBackup<A, B> replay(InheriterCapture<A, B> capture) {
		return capture.replay();
	}

	@Override
	public void restore(InheriterBackup<A, B> backup) {
		backup.restore();
	}

	@Override
	public InheriterBackup<A, B> clear() {
		InheriterBackup<A, B> backup = new InheriterBackup<>();
		for (Inheriter<A, B> inheriter : registers) {
			backup.put(inheriter, inheriter.clear());
		}
		return backup;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof InheriterRegistry) {
			return registers.equals(((InheriterRegistry<?, ?>) obj).registers);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return registers.hashCode();
	}

	@Override
	public String toString() {
		return registers.toString();
	}

	/**
	 * 注册传播者
	 * 
	 * @param inheriter 传播者
	 * @return 返回{@link Registration#EMPTY}表示注册失败
	 */
	public Registration register(Inheriter<A, B> inheriter) {
		return registers.add(inheriter) ? new DisposableRegistration(() -> registers.remove(inheriter))
				: Registration.CANCELLED;
	}

	/**
	 * 注销指定传播者
	 * 
	 * @param inheriter 传播者
	 * @return 返回{@link Registration#EMPTY}表示注销失败
	 */
	public Registration unregister(Inheriter<A, B> inheriter) {
		return registers.remove(inheriter) ? new DisposableRegistration(() -> registers.add(inheriter))
				: Registration.CANCELLED;
	}
}
