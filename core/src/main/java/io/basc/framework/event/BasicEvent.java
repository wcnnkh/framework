package io.basc.framework.event;

import java.io.Serializable;

import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.TimeUtils;

public class BasicEvent implements Event, Serializable{
	private static final long serialVersionUID = 1L;
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
		return TimeUtils.format(createTime, "yyyy-MM-dd HH:mm:ss") + " <" + MapperUtils.toString(this) + ">";
	};
}
