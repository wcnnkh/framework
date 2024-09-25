package io.basc.framework.util.actor;

import java.util.EventObject;

import io.basc.framework.util.TimeUtils;
import io.basc.framework.util.XUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * 一个基础事件的定义
 * @author shuchaowen
 *
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class Event extends EventObject {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final String id;
	/** System time when the event happened. */
	private final long timestamp;

	/**
	 * 使用原事件构造一下新的对象，对象属性不变(浅拷贝)
	 * 
	 * @param event
	 */
	public Event(@NonNull Event event) {
		this(event.source, event);
	}

	/**
	 * 根据来源构造一个新的事件
	 * 
	 * @param source
	 */
	public Event(@NonNull Object source) {
		this(source, XUtils.getUUID(), System.currentTimeMillis());
	}

	/**
	 * 使用上下文来构造，用来传播事件id和时间
	 * 
	 * @param source
	 * @param context
	 */
	public Event(@NonNull Object source, @NonNull Event context) {
		this(source, context.id, context.timestamp);
	}

	/**
	 * 全参数构造一个事件
	 * 
	 * @param source
	 * @param id
	 * @param timestamp
	 */
	public Event(@NonNull Object source, @NonNull String id, long timestamp) {
		super(source);
		this.id = id;
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return TimeUtils.MILLISECOND.format(timestamp) + " on event " + id + " source " + source;
	}
}