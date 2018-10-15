package shuchaowen.core.util;

/**
 * 使用ThreadLocal实现上下文功能
 * @author shuchaowen
 *
 */
public class Context<T> {
	private final ThreadLocal<ContextInfo<T>> context = new ThreadLocal<ContextInfo<T>>();
	
	private ContextInfo<T> getContextInfo(){
		ContextInfo<T> contextInfo = context.get();
		if(contextInfo == null){
			contextInfo = new ContextInfo<T>();
			context.set(contextInfo);
		}
		return contextInfo;
	}
	
	public T getValue(){
		return getContextInfo().getValue();
	}
	
	public void setValue(T value){
		getContextInfo().setValue(value);
	}
	
	public void begin(){
		ContextInfo<T> contextInfo = getContextInfo();
		if(contextInfo.getCount() == 0){
			firstBegin();
		}
		contextInfo.incrCount();
	}
	
	public void commit() throws Throwable{
		ContextInfo<T> contextInfo = getContextInfo();
		contextInfo.decrCount();
		if(contextInfo.getCount() == 0){//真实提交
			execute();
		}
	}
	
	protected void firstBegin(){
		//ignore
	}
	
	protected void execute() throws Throwable{
		//ignore
	}
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
