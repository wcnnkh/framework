package scw.event.support;

import scw.core.utils.XTime;
import scw.event.Event;

public class BasicEvent implements Event {
	private final long createTime;
	private final EventType eventType;

	public BasicEvent(EventType eventType) {
		this(eventType, System.currentTimeMillis());
	}

	public BasicEvent(EventType eventType, long createTime) {
		this.eventType = eventType;
		this.createTime = createTime;
	}

	public BasicEvent(BasicEvent event) {
		this(event.eventType, event.createTime);
	}

	public long getCreateTime() {
		return createTime;
	}

	public EventType getEventType() {
		return eventType;
	}

	@Override
	public String toString() {
		return "eventType=" + eventType + ", createTime=" + XTime.format(createTime, "yyyy-MM-dd HH:mm:ss:SSS");
	}
}
