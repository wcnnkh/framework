package scw.reflect;

public interface Method {
	
	
	Object invoke(Object obj, Object... args) throws Throwable;
}
