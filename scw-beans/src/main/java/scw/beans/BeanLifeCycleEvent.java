package scw.beans;

import scw.event.ObjectEvent;

/**
 * 生命周期事件
 * @author shuchaowen
 *
 */
public class BeanLifeCycleEvent extends ObjectEvent<Object>{
	private static final long serialVersionUID = 1L;
	private transient final BeanFactory beanFactory;
	private final Step step;
	private final BeanDefinition beanDefinition;

	public BeanLifeCycleEvent(BeanDefinition beanDefinition, Object source, BeanFactory beanFactory, Step step) {
		super(source);
		this.beanFactory = beanFactory;
		this.beanDefinition = beanDefinition;
		this.step = step;
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	public Step getStep() {
		return step;
	}

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
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
