package run.soeasy.framework.core.exchange.event;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ChangeEvent<T> extends BaseEvent {
	private static final long serialVersionUID = 1L;

	private static void check(Object before, Object after) {
		if (before == null && after == null) {
			throw new IllegalArgumentException("Cannot be empty before and after the change");
		}
	}

	private static ChangeType getChangeType(Object before, Object after) {
		if (before == null && after != null) {
			return ChangeType.CREATE;
		} else if (before != null && after == null) {
			return ChangeType.DELETE;
		} else {
			return ChangeType.UPDATE;
		}
	}

	private static <T> T selectSource(T before, T after) {
		check(before, after);
		// 优先使用after，因为这表示当前值
		return after == null ? before : after;
	}

	private static <T> T selectUpdatedSource(T before, T after) {
		if (before == null && after == null) {
			throw new IllegalArgumentException("Cannot be empty before and after the change");
		}

		// 选择选择before，因为这表示被更新的值
		return before == null ? after : before;
	}

	/**
	 * 变更类型
	 */
	private final ChangeType changeType;

	/**
	 * 当发生update事件时，update之前的数据
	 */
	private final T updatedSource;

	/**
	 * 浅拷贝
	 * 
	 * @param changeEvent
	 */
	public ChangeEvent(ChangeEvent<T> changeEvent) {
		super(changeEvent);
		this.changeType = changeEvent.changeType;
		this.updatedSource = changeEvent.updatedSource;
	}

	private ChangeEvent(BaseEvent event, T source, T updateSource, ChangeType changeType) {
		super(updateSource, event);
		this.changeType = changeType;
		this.updatedSource = updateSource;
	}

	/**
	 * 构造一个指定类型的变更事件
	 * 
	 * @param source
	 * @param changeType
	 */
	public ChangeEvent(@NonNull T source, @NonNull ChangeType changeType) {
		super(source);
		this.changeType = changeType;
		this.updatedSource = null;
	}

	/**
	 * 根据变更前后的来源来构造一个变更事件(beforeSource和afterSource不能同时为空)
	 * 
	 * @param beforeSource 变更前的值
	 * @param afterSource  变更后的值
	 */
	public ChangeEvent(T beforeSource, T afterSource) {
		super(selectSource(beforeSource, afterSource));
		this.updatedSource = selectUpdatedSource(beforeSource, beforeSource);
		this.changeType = getChangeType(beforeSource, beforeSource);
	}

	public <R> ChangeEvent<R> convert(@NonNull Function<? super T, ? extends R> mapper) {
		R targetSource = mapper.apply(getSource());
		R targetUpdateSource = updatedSource == null ? null : mapper.apply(updatedSource);
		return new ChangeEvent<R>(this, targetSource, targetUpdateSource, changeType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSource() {
		return (T) super.getSource();
	}
}
