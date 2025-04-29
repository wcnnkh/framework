package run.soeasy.framework.core.function;

public interface Singleton<T, E extends Throwable> extends ThrowingSupplier<T, E>, Reloadable, Variable {
	long lastModified();
	
	
}
