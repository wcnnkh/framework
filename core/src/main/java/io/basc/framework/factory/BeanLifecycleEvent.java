package io.basc.framework.factory;

import io.basc.framework.core.Ordered;
import io.basc.framework.event.Event;

/**
 * 生命周期事件
 * 
 * @author shuchaowen
 *
 */
public class BeanLifecycleEvent implements Event {
	private final long createTime;
	private final Object bean;
	private final BeanDefinition definition;
	private final Step step;

	public BeanLifecycleEvent(BeanDefinition definition, Object bean, Step step) {
		this.createTime = System.currentTimeMillis();
		this.bean = bean;
		this.definition = definition;
		this.step = step;
	}

	public Object getBean() {
		return bean;
	}

	public long getCreateTime() {
		return this.createTime;
	};
	
	public BeanDefinition getDefinition() {
		return definition;
	}
	
	public Step getStep() {
		return step;
	}



	public static enum Step implements Ordered {
		/**
		 * 执行依赖前
		 */
		BEFORE_DEPENDENCE(1),
		/**
		 * 执行依赖后
		 */
		AFTER_DEPENDENCE(2),
		/**
		 * 初始化之前
		 */
		BEFORE_INIT(3),
		/**
		 * 初始化之后
		 */
		AFTER_INIT(4),
		/**
		 * 销毁之前
		 */
		BEFORE_DESTROY(5),
		/**
		 * 销毁之后
		 */
		AFTER_DESTROY(6);

		private final int order;

		Step(int order) {
			this.order = order;
		}

		public int getOrder() {
			return order;
		}
	}
}
