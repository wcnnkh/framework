package shuchaowen.core.util;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;

/**
 * 使用ThreadLocal实现上下文功能
 * @author shuchaowen
 *
 */
public abstract class Context<T> {
	private final ThreadLocal<ContextInfo<T>> context = new ThreadLocal<ContextInfo<T>>();
	
	private ContextInfo<T> getContextInfo(){
		ContextInfo<T> contextInfo = context.get();
		if(contextInfo == null){
			contextInfo = new ContextInfo<T>();
			context.set(contextInfo);
		}
		return contextInfo;
	}
	
	public boolean isBegin(){
		return getContextInfo().getCount() > 0;
	}
	
	protected T getValue(){
		return getContextInfo().getValue();
	}
	
	protected void setValue(T value){
		getContextInfo().setValue(value);
	}
	
	/**
	 * 开始
	 */
	public void begin(){
		ContextInfo<T> contextInfo = getContextInfo();
		if(contextInfo.getCount() == 0){
			firstBegin();
		}
		contextInfo.incrCount();
	}
	
	/**
	 * 提交  
	 * 开始多少次就是提交多少次
	 * @throws Throwable
	 */
	public void commit() throws Throwable{
		ContextInfo<T> contextInfo = getContextInfo();
		if(contextInfo.getCount() < 1){
			throw new ShuChaoWenRuntimeException("这已经是最后一次了，无法提交[" + contextInfo.getCount() + "]");
		}
		
		contextInfo.decrCount();
		if(contextInfo.getCount() == 0){//真实提交
			lastCommit();
		}
	}
	
	/**
	 * 在第一次开始的时候应该执行的内容
	 */
	protected abstract void firstBegin();
	
	/**
	 * 在最后一次提交的时候应该执行的内容
	 * @throws Throwable
	 */
	protected abstract void lastCommit() throws Throwable;
}

class ContextInfo<T>{
	private T value;
	private int count;
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	
	public void incrCount(){
		count ++;
	}
	
	public void decrCount(){
		count --;
	}
}
