package io.basc.framework.beans;

import io.basc.framework.event.ObjectEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生命周期事件
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BeanLifecycleEvent extends ObjectEvent<Object> {
	private static final long serialVersionUID = 1L;
	private final BeanLifecycleStep step;
	private final String beanName;

	public BeanLifecycleEvent(String beanName, Object source, BeanLifecycleStep step) {
		super(source);
		this.step = step;
		this.beanName = beanName;
	}
}
