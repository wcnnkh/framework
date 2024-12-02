package io.basc.framework.util.spi;

import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import lombok.NonNull;

public interface Configured<S> extends Include<S>, Receipt {

	public static class And<S, W extends Configured<S>> extends Include.And<S, W>
			implements Configured<S>, ReceiptWrapper<W> {

		public And(@NonNull W source, @NonNull Registration registration) {
			super(source, registration);
		}

		@Override
		public Configured<S> and(Registration registration) {
			return Configured.super.and(registration);
		}
	}

	@Override
	default Configured<S> and(Registration registration) {
		return new And<>(this, registration);
	}

	public static final Configured<?> FAILURE_CONFIGURED = new Included<>(true, false, null);

	@SuppressWarnings("unchecked")
	public static <S> Configured<S> failure() {
		return (Configured<S>) FAILURE_CONFIGURED;
	}
}
