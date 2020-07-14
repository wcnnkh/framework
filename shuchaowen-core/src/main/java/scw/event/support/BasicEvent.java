package scw.event.support;

import scw.event.Event;

public class BasicEvent implements Event {
	private long createTime;

	public BasicEvent() {
		this(System.currentTimeMillis());
	}
	
	public BasicEvent(long createTime) {
		this.createTime = createTime;
	}

	public long getCreateTime() {
		return createTime;
	}

}
