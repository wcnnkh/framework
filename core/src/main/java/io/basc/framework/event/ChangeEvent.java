package io.basc.framework.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChangeEvent<T> extends ObjectEvent<T> {
	private static final long serialVersionUID = 1L;
	private final ChangeType changeType;

	public ChangeEvent(ChangeType changeType, ObjectEvent<T> event) {
		super(event);
		this.changeType = changeType;
	}

	public ChangeEvent(long createTime, ChangeType changeType, T source) {
		super(source, createTime);
		this.changeType = changeType;
	}

	public ChangeEvent(ChangeType changeType, T source) {
		super(source);
		this.changeType = changeType;
	}

	public ChangeEvent(ChangeEvent<T> event) {
		super(event);
		this.changeType = event.changeType;
	}

	public ChangeEvent(ChangeEvent<?> event, T source) {
		super(source, event.getCreateTime());
		this.changeType = event.changeType;
	}
}
