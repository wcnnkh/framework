package run.soeasy.framework.core.comparator;

/**
 * 排序源提供者接口 为对象提供用于排序的替代源对象，支持从非Ordered对象中提取排序信息
 * 
 * 应用场景： - 当对象本身未实现Ordered接口，但关联对象实现了Ordered时 - 需要从对象的某个属性中获取排序信息时 -
 * 支持基于组合对象的排序场景
 * 
 * @author soeasy.run
 *
 */
@FunctionalInterface
public interface OrderSourceProvider {

	/**
	 * 获取对象的排序源
	 * 
	 * @param obj 待排序的原始对象
	 * @return 排序源对象（可以是实现Ordered接口的对象或对象数组） 若没有合适的排序源，返回null
	 * 
	 * @implNote 排序源对象可以是： - 实现Ordered接口的单一对象 - 包含Ordered对象的数组或集合 - 任何可以从中提取排序信息的对象
	 */
	Object getOrderSource(Object obj);
}