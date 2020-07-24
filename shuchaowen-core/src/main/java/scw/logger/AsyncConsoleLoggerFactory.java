package scw.logger;

import scw.core.UnsafeStringBuffer;
import scw.util.Consumer;
import scw.util.queue.MemoryAsyncExecuteQueue;

public class AsyncConsoleLoggerFactory extends AbstractConsoleLoggerFactory
		implements Consumer<Message> {
	private final MemoryAsyncExecuteQueue<Message> asyncExecuteQueue = new MemoryAsyncExecuteQueue<Message>(
			getClass().getSimpleName(), true);
	private final UnsafeStringBuffer unsafeStringBuffer;

	public AsyncConsoleLoggerFactory() {
		unsafeStringBuffer = new UnsafeStringBuffer();
		asyncExecuteQueue.addConsumer(this);
	}

	protected final void log(Message message) {
		asyncExecuteQueue.put(message);
	}

	public void accept(Message message) {
		try {
			console(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void destroy() {
		asyncExecuteQueue.destroy();
	}

	@Override
	protected Appendable createAppendable() {
		unsafeStringBuffer.reset();
		return unsafeStringBuffer;
	}
}
