package io.basc.framework.event;

import java.io.Serializable;

import io.basc.framework.util.TimeUtils;
import lombok.Data;
import lombok.ToString.Include;

@Data
public class BasicEvent implements Event, Serializable {
	private static final long serialVersionUID = 1L;
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

	@Include(name = "createTimeDescribe")
	public String getCreateTimeDescribe() {
		return TimeUtils.format(createTime, "yyyy-MM-dd HH:mm:ss,SSS");
	}
}
