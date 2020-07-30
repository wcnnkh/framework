package scw.event;

import java.util.EventObject;

public class ObjectEvent<T> extends EventObject implements Event {
	private static final long serialVersionUID = 1L;
	private final long createTime;

	public ObjectEvent(T source) {
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
