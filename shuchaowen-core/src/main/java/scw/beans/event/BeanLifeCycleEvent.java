package scw.beans.event;

import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

/**
 * 生命周期事件
 * @author shuchaowen
 *
 */
public class BeanLifeCycleEvent extends BeanEvent{
	private static final long serialVersionUID = 1L;
	private transient final PropertyFactory propertyFactory;
	private final Step step;

	public BeanLifeCycleEvent(Object source, BeanFactory beanFactory, PropertyFactory propertyFactory, Step step) {
		super(source, beanFactory);
		this.propertyFactory = propertyFactory;
		this.step = step;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}
	
	public Step getStep() {
		return step;
	}



	public static enum Step{
		BEFORE_DEPENDENCE,
		AFTER_DEPENDENCE,
		BEFORE_INIT,
		AFTER_INIT,
		BEFORE_DESTROY,
		AFTER_DESTROY;
	}
}
