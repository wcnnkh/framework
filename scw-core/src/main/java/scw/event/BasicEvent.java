package scw.event;

import scw.core.utils.XTime;
import scw.mapper.MapperUtils;

public class BasicEvent implements Event {
	private final long createTime;
	
	public BasicEvent(){
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

	public String toString() {
		return XTime.format(createTime, "yyyy-MM-dd HH:mm:ss") + " <" + MapperUtils.getMapper().toString(this) + ">";
	};
}
