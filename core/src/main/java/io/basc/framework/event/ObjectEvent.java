package io.basc.framework.event;

import java.util.EventObject;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.TimeUtils;

/**
 * 这和jdk自身提供的区别是source字段可以被序列化
 * 
 * @see EventObject
 * @author shuchaowen
 *
 * @param <T>
 */
public class ObjectEvent<T> extends BasicEvent {
	private static final long serialVersionUID = 1L;
	private final T source;

	public ObjectEvent(ObjectEvent<T> event) {
		this(event.getSource(), event.getCreateTime());
	}

	public ObjectEvent(T source) {
		this(source, System.currentTimeMillis());
	}

	public ObjectEvent(T source, long createTime) {
		super(createTime);
		this.source = source;
	}

	public T getSource() {
		return source;
	}

	@Override
	public String toString() {
		return TimeUtils.format(getCreateTime(), "yyyy-MM-dd HH:mm:ss") + " <" + ReflectionUtils.toString(this) + ">";
	}
}
