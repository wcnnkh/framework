package scw.message;

import scw.core.Assert;
import scw.core.utils.ObjectUtils;
import scw.lang.Nullable;

public abstract class AbstractMessage<T> implements Message<T>{
	private final T payload;

	AbstractMessage(T payload) {
		Assert.notNull(payload, "payload must not be null");
		this.payload = payload;
	}

	/**
	 * Return the message payload (never {@code null}).
	 */
	public T getPayload() {
		return this.payload;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractMessage)) {
			return false;
		}
		
		AbstractMessage<?> otherMessage = (AbstractMessage<?>) other;
		return ObjectUtils.nullSafeEquals(this.payload, otherMessage.payload);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.payload);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " payload=[" + toStringPayload() +
				"], payloadLength=" + getPayloadLength();
	}

	protected abstract String toStringPayload();
}
