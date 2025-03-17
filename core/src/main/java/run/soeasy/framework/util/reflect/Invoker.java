package run.soeasy.framework.util.reflect;

@FunctionalInterface
public interface Invoker {
	Object invoke(Object... args) throws Throwable;
}
