package io.basc.framework.beans;

import io.basc.framework.core.Ordered;
import io.basc.framework.util.Symbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BeanLifecycleStep extends Symbol implements Ordered {
	private static final long serialVersionUID = 1L;
	/**
	 * 执行依赖前
	 */
	public static final BeanLifecycleStep BEFORE_DEPENDENCE = new BeanLifecycleStep("BEFORE_DEPENDENCE", 100);
	/**
	 * 执行依赖后
	 */
	public static final BeanLifecycleStep AFTER_DEPENDENCE = new BeanLifecycleStep("AFTER_DEPENDENCE", 200);
	/**
	 * 初始化之前
	 */
	public static final BeanLifecycleStep BEFORE_INIT = new BeanLifecycleStep("BEFORE_INIT", 300);
	/**
	 * 初始化之后
	 */
	public static final BeanLifecycleStep AFTER_INIT = new BeanLifecycleStep("AFTER_INIT", 400);
	/**
	 * 销毁之前
	 */
	public static final BeanLifecycleStep BEFORE_DESTROY = new BeanLifecycleStep("BEFORE_DESTROY", 500);
	/**
	 * 销毁之后
	 */
	public static final BeanLifecycleStep AFTER_DESTROY = new BeanLifecycleStep("AFTER_DESTROY", 600);

	private final int order;

	public BeanLifecycleStep(String name, int order) {
		super(name);
		this.order = order;
	}
}
