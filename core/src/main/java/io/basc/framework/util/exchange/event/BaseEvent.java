package io.basc.framework.util.exchange.event;

import java.util.EventObject;

import io.basc.framework.util.TimeUtils;
import io.basc.framework.util.sequences.uuid.UUIDSequences;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * 一个基础事件的定义
 * 
 * @author shuchaowen
 *
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class BaseEvent extends EventObject {
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
	public BaseEvent(@NonNull BaseEvent event) {
		this(event.source, event);
	}

	/**
	 * 根据来源构造一个新的事件
	 * 
	 * @param source
	 */
	public BaseEvent(@NonNull Object source) {
		this(source, UUIDSequences.getInstance().next(), System.currentTimeMillis());
	}

	/**
	 * 使用上下文来构造，用来传播事件id和时间
	 * 
	 * @param source
	 * @param context
	 */
	public BaseEvent(@NonNull Object source, @NonNull BaseEvent context) {
		this(source, context.id, context.timestamp);
	}

	/**
	 * 全参数构造一个事件
	 * 
	 * @param source
	 * @param id
	 * @param timestamp
	 */
	public BaseEvent(@NonNull Object source, @NonNull String id, long timestamp) {
		super(source);
		this.id = id;
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return TimeUtils.MILLISECOND.format(timestamp) + " on event " + id + " source " + source;
	}
}