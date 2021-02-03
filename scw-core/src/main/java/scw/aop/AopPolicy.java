package scw.aop;



public interface AopPolicy {
	boolean isProxy(Object instance);
}