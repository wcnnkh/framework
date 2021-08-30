package io.basc.framework.beans;

import io.basc.framework.event.Event;

/**
 * 生命周期事件
 * @author shuchaowen
 *
 */
public class BeanlifeCycleEvent implements Event{
	private final long createTime;
	private final BeanFactory beanFactory;
	private final Step step;
	private final Object source;
	private final BeanDefinition beanDefinition;

	public BeanlifeCycleEvent(BeanDefinition beanDefinition, Object source, BeanFactory beanFactory, Step step) {
		this.createTime = System.currentTimeMillis();
		this.source = source;
		this.beanFactory = beanFactory;
		this.beanDefinition = beanDefinition;
		this.step = step;
	}
	
	public Object getSource() {
		return source;
	}
	
	public long getCreateTime() {
		return this.createTime;
	};
	
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
