package scw.event;

import java.util.EventObject;

import scw.core.utils.XTime;

public class ObjectEvent<T> extends EventObject implements Event {
	private static final long serialVersionUID = 1L;
	private final long createTime;
	private final EventType eventType;
	
	public ObjectEvent(T source) {
		this(EventType.UPDATE, source);
	}

	public ObjectEvent(EventType eventType, T source) {
		super(source);
		this.eventType = eventType;
		this.createTime = System.currentTimeMillis();
	}

	public long getCreateTime() {
		return createTime;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSource() {
		return (T) super.getSource();
	}

	@Override
	public String toString() {
		return "eventType=" + eventType + " createTime="
				+ XTime.format(createTime, "yyyy-MM-dd HH:mm:ss,SSS") + " "
				+ super.toString();
	}
}
