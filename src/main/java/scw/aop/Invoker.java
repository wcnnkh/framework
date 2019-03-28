package scw.aop;

public interface Invoker {
	Object invoke(Object... args) throws Throwable;
}
