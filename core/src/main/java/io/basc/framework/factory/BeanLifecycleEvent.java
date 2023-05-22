package io.basc.framework.factory;

import io.basc.framework.core.Ordered;
import io.basc.framework.event.ObjectEvent;
import lombok.Data;

/**
 * 生命周期事件
 * 
 * @author wcnnkh
 *
 */
@Data
public class BeanLifecycleEvent extends ObjectEvent<Object> {
	private static final long serialVersionUID = 1L;
	private final Step step;

	public BeanLifecycleEvent(Object source, Step step) {
		super(source);
		this.step = step;
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
