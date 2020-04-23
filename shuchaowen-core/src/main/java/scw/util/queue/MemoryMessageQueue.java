package scw.util.queue;

public class MemoryMessageQueue<E> extends BlockingMessageQueue<E> {

	public MemoryMessageQueue() {
		super(new LinkedBlockingQueue<E>());
	}
}
