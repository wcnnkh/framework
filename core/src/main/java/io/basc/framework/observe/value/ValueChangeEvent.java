package io.basc.framework.observe.value;

import io.basc.framework.observe.mode.ChangeEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 值的变更事件
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ValueChangeEvent<T> extends ChangeEvent {
	private static final long serialVersionUID = 1L;
	private final T oldValue;
	private final T newValue;

	public ValueChangeEvent(Object source, long lastModified, T oldValue, T newValue) {
		super(source, lastModified);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
}
