package scw.event;

public class EventObject<T> extends java.util.EventObject implements Event {
	private static final long serialVersionUID = 1L;
	private final long createTime;

	public EventObject(T source) {
		super(source);
		this.createTime = System.currentTimeMillis();
	}

	public long getCreateTime() {
		return createTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSource() {
		return (T) super.getSource();
	}
}
