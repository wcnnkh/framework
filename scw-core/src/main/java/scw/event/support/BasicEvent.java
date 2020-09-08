package scw.event.support;

import scw.core.utils.XTime;
import scw.event.Event;
import scw.mapper.MapperUtils;

public class BasicEvent implements Event {
	private final long createTime;

	public BasicEvent() {
		this(System.currentTimeMillis());
	}

	public BasicEvent(BasicEvent basicEvent) {
		this(basicEvent.createTime);
	}

	public BasicEvent(long createTime) {
		this.createTime = createTime;
	}

	public final long getCreateTime() {
		return createTime;
	}

	public final String getCreateTimeAfterFormatting() {
		return XTime.format(createTime, "yyyy-MM-dd HH:mm:ss:SSS");
	}

	public String toString() {
		return MapperUtils.getMapper().toString(this);
	};
}
