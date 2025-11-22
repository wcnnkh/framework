package run.soeasy.framework.sequences;

import lombok.NonNull;

class PrefetchingSequence<T> implements Sequence<T> {

	// 被包装的底层序列
	private final Sequence<T> delegate;

	// 每次从底层序列预取的元素数量
	private final long batchSize;

	private volatile Sequence<T> sequence;

	/**
	 * 构造一个预取序列装饰器。
	 *
	 * @param delegate  被包装的底层序列，不能为空。
	 * @param batchSize 每次预取的批次大小，必须大于0。
	 */
	public PrefetchingSequence(@NonNull Sequence<T> delegate, long batchSize) {
		this.delegate = delegate;
		if (batchSize <= 0) {
			throw new IllegalArgumentException("Batch size must be greater than zero.");
		}
		this.batchSize = batchSize;
	}

	@Override
	public @NonNull T next() {
		if (!hasNext()) {
			throw new UnsupportedOperationException("Sequence has been exhausted.");
		}
		return this.sequence.next();
	}

	@Override
	public boolean hasNext() {
		if (sequence == null || !sequence.hasNext()) {
			synchronized (this) {
				if (sequence == null || !sequence.hasNext()) {
					sequence = delegate.snapshot(batchSize);
				}
			}
		}
		return sequence.hasNext();
	}
}