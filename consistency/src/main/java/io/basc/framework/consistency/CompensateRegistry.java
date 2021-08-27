package io.basc.framework.consistency;

import io.basc.framework.lang.Nullable;

/**
 * 会保证任务至少执行一次 ,但可能会多次执行
 * @author shuchaowen
 *
 */
public interface CompensateRegistry {
	/**
	 * 注册一个补偿行为，如果已经存在就返回与之关联的补偿器
	 * @param group
	 * @param id
	 * @param runnable 大多数实现都是使用的序列化， 如果无法序列化的字段应该使用transient关键字
	 * @return
	 * @throws CompensateException
	 */
	Compensator register(String group, String id, Runnable runnable) throws CompensateException;
	
	/**
	 * 获取补偿器
	 * @param group
	 * @param id
	 * @return 即便是已注册过的，结果可能为空，因为有些实现策略是执行成功或取消后删除
	 */
	@Nullable
	Compensator getCompensator(String group, String id);
}
