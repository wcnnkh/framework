package io.basc.framework.consistency;

import io.basc.framework.lang.Nullable;

import java.util.Enumeration;
import java.util.concurrent.locks.Lock;

public interface CompensatePolicy {
	Lock getLock(String group, String id);

	Enumeration<String> getUnfinishedGroups();

	@Nullable
	String getLastUnfinishedId(String group);

	boolean add(String group, String id, Runnable runnable);

	@Nullable
	Runnable get(String group, String id);

	boolean isCancelled(String group, String id);

	boolean cancel(String group, String id);

	boolean isDone(String group, String id);

	boolean done(String group, String id);
}
