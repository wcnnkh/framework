package scw.event;

import scw.core.utils.XTime;
import scw.mapper.MapperUtils;

public class BasicEvent implements Event {
	private final long createTime;
	private final EventType eventType;
	
	public BasicEvent() {
		this(EventType.UPDATE);
	}

	public BasicEvent(EventType eventType) {
		this(eventType, System.currentTimeMillis());
	}

	public BasicEvent(BasicEvent basicEvent) {
		this(basicEvent.eventType, basicEvent.createTime);
	}

	public BasicEvent(EventType eventType, long createTime) {
		this.eventType = eventType;
		this.createTime = createTime;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public final long getCreateTime() {
		return createTime;
	}

	public String toString() {
		return XTime.format(createTime, "yyyy-MM-dd HH:mm:ss") + " <" + MapperUtils.getMapper().toString(this) + ">";
	};
}
