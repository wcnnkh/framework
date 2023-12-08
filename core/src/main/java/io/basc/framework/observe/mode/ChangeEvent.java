package io.basc.framework.observe.mode;

import io.basc.framework.event.Event;
import io.basc.framework.observe.Pull;

/**
 * 变更事件(拉模型中的事件)
 * 
 * @author shuchaowen
 *
 */
public class ChangeEvent extends Event implements Pull {
	private static final long serialVersionUID = 1L;
	private final long lastModified;

	public ChangeEvent(Object source, long lastModified) {
		super(source);
		this.lastModified = lastModified;
	}

	@Override
	public long lastModified() {
		return lastModified;
	}

}
