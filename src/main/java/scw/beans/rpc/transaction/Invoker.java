package scw.beans.rpc.transaction;

public interface Invoker {
	
	public void invoke(Object obj, Object[] args) throws Throwable;
}
