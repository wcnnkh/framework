package io.basc.framework.observe;

import io.basc.framework.event.Event;
import io.basc.framework.util.Assert;

/**
 * 变更事件
 * 
 * @author shuchaowen
 *
 */
public class ChangeEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final ChangeType type;

	public ChangeEvent(Object source, ChangeType type) {
		super(source);
		Assert.requiredArgument(type != null, "type");
		this.type = type;
	}

	public ChangeType getType() {
		return type;
	}
}
