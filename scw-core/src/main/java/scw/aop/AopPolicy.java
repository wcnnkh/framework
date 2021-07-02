package scw.aop;

@FunctionalInterface
public interface AopPolicy {
	boolean isProxy(Object instance);
}