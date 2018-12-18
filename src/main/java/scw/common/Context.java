package scw.common;

import scw.common.exception.ShuChaoWenRuntimeException;

/**
 * 使用ThreadLocal实现上下文功能
 * 
 * @author shuchaowen
 *
 */
public abstract class Context<T> {
	private final ThreadLocal<ContextInfo<T>> context = new ThreadLocal<ContextInfo<T>>();

	private ContextInfo<T> getContextInfo() {
		ContextInfo<T> contextInfo = context.get();
		if (contextInfo == null) {
			contextInfo = new ContextInfo<T>();
			context.set(contextInfo);
		}
		return contextInfo;
	}

	protected T getValue() {
		ContextInfo<T> contextInfo = context.get();
		return contextInfo == null ? null : contextInfo.getValue();
	}

	protected void setValue(T value) {
		getContextInfo().setValue(value);
	}

	/**
	 * 开始 使用方法 begin try{
	 *
	 * }finally{ end }
	 * 
	 */
	public void begin() {
		ContextInfo<T> contextInfo = getContextInfo();
		if (contextInfo.getCount() == 0) {
			firstBegin();
		}
		contextInfo.incrCount();
	}

	/**
	 * 结束 开始多少次就是提交多少次
	 * 
	 * @throws Throwable
	 */
	public void end() {
		ContextInfo<T> contextInfo = getContextInfo();
		if (contextInfo.getCount() < 1) {
			throw new ShuChaoWenRuntimeException("这已经是最后一次了，无法结束[" + contextInfo.getCount() + "]");
		}

		contextInfo.decrCount();
		if (contextInfo.getCount() == 0) {// 真实提交
			try {
				lastCommit();
			} finally {
				context.remove();// 结束后清理内存
			}
		}
	}

	/**
	 * 在第一次开始的时候应该执行的内容
	 */
	protected abstract void firstBegin();

	/**
	 * 在最后一次提交的时候应该执行的内容
	 * 
	 * @throws Throwable
	 */
	protected abstract void lastCommit();
}
