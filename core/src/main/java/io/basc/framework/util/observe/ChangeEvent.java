package io.basc.framework.util.observe;

import io.basc.framework.util.event.Event;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ChangeEvent<T> extends Event {
	private static final long serialVersionUID = 1L;
	private final ChangeType changeType;
	private final T parentSource;

	/**
	 * 浅拷贝
	 * 
	 * @param changeEvent
	 */
	public ChangeEvent(ChangeEvent<T> changeEvent) {
		super(changeEvent);
		this.changeType = changeEvent.changeType;
		this.parentSource = changeEvent.parentSource;
	}

	/**
	 * 构造一个指定类型的变更事件
	 * 
	 * @param source
	 * @param changeType
	 */
	public ChangeEvent(@NonNull T source, @NonNull ChangeType changeType) {
		this(source, changeType, null);
	}

	/**
	 * 构造一个存在父级的变更事件
	 * 
	 * @param source
	 * @param changeType
	 * @param parentSource
	 */
	public ChangeEvent(@NonNull T source, @NonNull ChangeType changeType, T parentSource) {
		super(source);
		this.changeType = changeType;
		this.parentSource = parentSource;
	}

	/**
	 * 传递上下文构造一个事件
	 * 
	 * @param source
	 * @param context
	 * @param changeType
	 * @param parentSource
	 */
	public ChangeEvent(@NonNull T source, @NonNull Event context, @NonNull ChangeType changeType, T parentSource) {
		super(source, context);
		this.changeType = changeType;
		this.parentSource = parentSource;
	}

	/**
	 * 构造一个Update事件
	 * 
	 * @param source
	 * @param parentSource
	 */
	public ChangeEvent(@NonNull T source, T parentSource) {
		this(source, ChangeType.UPDATE, parentSource);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSource() {
		return (T) super.getSource();
	}
}
