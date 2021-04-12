package scw.compensat;

import java.util.Enumeration;
import java.util.concurrent.locks.Lock;

import scw.lang.Nullable;

public interface CompenstPolicy {
	Lock getLock(String group, String id);
	
	Enumeration<String> getUnfinishedGroups();

	@Nullable
	String getLastUnfinishedId(String group);

	/**
	 * 添加不一个存在的任务
	 * @param group
	 * @param id
	 * @param runnable
	 * @return
	 */
	boolean add(String group, String id, Runnable runnable);
	
	@Nullable
	Runnable get(String group, String id);

	boolean isCancelled(String group, String id);
	
	boolean cancel(String group, String id);

	boolean isDone(String group, String id);
	
	boolean done(String group, String id);
}
