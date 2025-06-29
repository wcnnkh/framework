package run.soeasy.framework.core.comparator;

/**
 * * 有序接口 定义对象优先级排序的标准，通过getOrder()返回的整数值确定顺序
 * 
 * 排序规则： - 数值越小优先级越高（HIGHEST_PRECEDENCE < DEFAULT_PRECEDENCE <
 * LOWEST_PRECEDENCE） - 适用于需要排序的组件（如拦截器、转换器、处理器等）
 * 
 * @author soeasy.run
 *
 */
public interface Ordered {

	/**
	 * 最高优先级常量 数值为Integer.MIN_VALUE，在排序中优先级最高
	 */
	int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

	/**
	 * 默认优先级常量 数值为0，当未指定具体优先级时使用
	 */
	int DEFAULT_PRECEDENCE = 0;

	/**
	 * 最低优先级常量 数值为Integer.MAX_VALUE，在排序中优先级最低
	 */
	int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

	/**
	 * 获取对象的顺序值
	 * 
	 * 排序规则： - 数值越小优先级越高（例如：A.getOrder() < B.getOrder() → A优先级高于B） - 相同顺序值的对象排序位置不确定
	 * 
	 * @return 顺序值，数值越小优先级越高
	 * @see #HIGHEST_PRECEDENCE 最高优先级
	 * @see #LOWEST_PRECEDENCE 最低优先级
	 */
	int getOrder();
}