package run.soeasy.framework.core.math;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

import lombok.NonNull;

public class Counter extends AtomicReference<BigInteger> {
	private static final long serialVersionUID = 1L;

	public Counter() {
		super(BigInteger.ZERO);
	}

	/**
	 * Atomically increments by one the current value.
	 *
	 * @return the updated value
	 */
	public final BigInteger incrementAndGet() {
		return addAndGet(BigInteger.ONE);
	}

	public final BigInteger getAndIncrement() {
		return getAndAdd(BigInteger.ONE);
	}

	/**
	 * Atomically decrements by one the current value.
	 *
	 * @return the updated value
	 */
	public final BigInteger decrementAndGet() {
		return addAndGet(BigInteger.ONE.negate());
	}

	public final BigInteger getAndDecrement() {
		return getAndAdd(BigInteger.ONE.negate());
	}

	public final BigInteger getAndAdd(@NonNull BigInteger value) {
		return getAndUpdate((e) -> e == null ? value : e.add(value));
	}

	public final BigInteger addAndGet(@NonNull BigInteger value) {
		return updateAndGet((e) -> e == null ? value : e.add(value));
	}

}
