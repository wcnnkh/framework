package scw.logger;

import scw.core.UnsafeStringBuffer;
import scw.event.BasicEvent;
import scw.event.EventListener;
import scw.event.support.DefaultAsyncBasicEventDispatcher;
import scw.logger.AsyncConsoleLoggerFactory.MessageEvent;

public class AsyncConsoleLoggerFactory extends AbstractConsoleLoggerFactory implements EventListener<MessageEvent> {
	private final DefaultAsyncBasicEventDispatcher<MessageEvent> asyncBasicEventDispatcher = new DefaultAsyncBasicEventDispatcher<MessageEvent>(
			false, getClass().getSimpleName());
	private final UnsafeStringBuffer unsafeStringBuffer;

	public AsyncConsoleLoggerFactory() {
		unsafeStringBuffer = new UnsafeStringBuffer();
		asyncBasicEventDispatcher.registerListener(this);
	}

	protected final void log(Message message) {
		asyncBasicEventDispatcher.publishEvent(new MessageEvent(message));
	}

	public synchronized void destroy() {
		asyncBasicEventDispatcher.destroy();
	}

	@Override
	protected Appendable createAppendable() {
		unsafeStringBuffer.reset();
		return unsafeStringBuffer;
	}

	public static class MessageEvent extends BasicEvent {
		private static final long serialVersionUID = 1L;
		private final Message message;

		public MessageEvent(Message message) {
			this.message = message;
		}

		public Message getMessage() {
			return message;
		}
	}

	public void onEvent(MessageEvent event) {
		try {
			console(event.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
