package scw.core.reflect;

@FunctionalInterface
public interface Invoker {
	Object invoke(Object... args) throws Throwable;
}
