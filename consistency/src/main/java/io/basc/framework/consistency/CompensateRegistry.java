package io.basc.framework.consistency;

import io.basc.framework.lang.Nullable;

/**
 * 会保证任务至少执行一次 ,但可能会多次执行
 * 
 * @author wcnnkh
 *
 */
public interface CompensateRegistry {
	Compensator register(String group, String id, Runnable runnable) throws CompensateException;

	@Nullable
	Compensator getCompensator(String group, String id);
}
