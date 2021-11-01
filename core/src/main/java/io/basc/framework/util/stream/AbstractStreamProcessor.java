package io.basc.framework.util.stream;

import io.basc.framework.lang.Nullable;

public abstract class AbstractStreamProcessor<T, E extends Throwable> implements StreamProcessor<T, E> {
	//默认不自动关闭
	private boolean autoClose = false;
	private boolean closed;
	protected RunnableProcessor<E> closeProcessor;

	public AbstractStreamProcessor() {
	}

	public AbstractStreamProcessor(@Nullable RunnableProcessor<E> closeProcessor) {
		this.closeProcessor = closeProcessor;
	}

	@Override
	public <S> StreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor) {
		
		return new AbstractStreamProcessor<S, E>() {

			@Override
			public S process() throws E {
				try {
					this.beforeProcess();
					T v = AbstractStreamProcessor.this.process();
					return processor.process(v);
				} finally {
					this.afterProcess();
				}
			}
		}.onClose(() -> {
			AbstractStreamProcessor.this.close();
		});
	}

	@Override
	public StreamProcessor<T, E> onClose(RunnableProcessor<E> closeProcessor) {
		if (this.closeProcessor == null) {
			this.closeProcessor = closeProcessor;
		} else {
			this.closeProcessor = this.closeProcessor.afterProcess(closeProcessor);
		}
		return this;
	}

	@Override
	public void close() throws E {
		if (closed) {
			return;
		}

		closed = true;
		if (this.closeProcessor != null) {
			this.closeProcessor.process();
		}
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	/**
	 * 执行前
	 */
	protected void beforeProcess() throws E {
	}

	/**
	 * 在执行完后
	 */
	protected void afterProcess() throws E {
		if (isAutoClose()) {
			close();
		}
	}
}
