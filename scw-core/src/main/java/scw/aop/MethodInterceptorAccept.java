package scw.aop;

public interface MethodInterceptorAccept {
	boolean isAccept(MethodInvoker invoker, Object[] args);
}
