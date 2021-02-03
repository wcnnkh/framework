package scw.beans;

import scw.event.BasicEvent;

/**
 * 生命周期事件
 * @author shuchaowen
 *
 */
@SuppressWarnings("serial")
public class BeanLifeCycleEvent extends BasicEvent{
	private transient final BeanFactory beanFactory;
	private final Step step;
	private final BeanDefinition beanDefinition;
	private final Object source;

	public BeanLifeCycleEvent(BeanDefinition beanDefinition, Object source, BeanFactory beanFactory, Step step) {
		this.source = source;
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

	public Object getSource() {
		return source;
	}

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public static enum Step{
		/**
		 * 执行依赖前
		 */
		BEFORE_DEPENDENCE,
		/**
		 * 执行依赖后
		 */
		AFTER_DEPENDENCE,
		/**
		 * 初始化之前
		 */
		BEFORE_INIT,
		/**
		 * 初始化之后
		 */
		AFTER_INIT,
		/**
		 * 销毁之前
		 */
		BEFORE_DESTROY,
		/**
		 * 销毁之后
		 */
		AFTER_DESTROY;
	}
}
