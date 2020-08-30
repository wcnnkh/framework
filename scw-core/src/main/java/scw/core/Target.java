package scw.core;

public interface Target {
	<T> T getTarget(Class<T> targetType);
}
