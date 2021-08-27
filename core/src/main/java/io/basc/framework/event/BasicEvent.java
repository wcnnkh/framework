package io.basc.framework.event;

import io.basc.framework.core.utils.XTime;
import io.basc.framework.mapper.MapperUtils;

import java.io.Serializable;

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
		return XTime.format(createTime, "yyyy-MM-dd HH:mm:ss") + " <" + MapperUtils.getFieldFactory().getFields(getClass()).getValueMap(this).toString() + ">";
	};
}
