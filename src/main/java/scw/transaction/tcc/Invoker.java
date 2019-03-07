package scw.transaction.tcc;

public interface Invoker {
	
	public void invoke(Object obj, Object[] args) throws Throwable;
}
