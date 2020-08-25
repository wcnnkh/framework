package scw.aop;

public interface FilterAccept {
	boolean isAccept(MethodInvoker invoker, Object[] args);
}
