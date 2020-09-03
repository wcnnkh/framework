package scw.util;

public interface Target {
	<T> T getTarget(Class<T> targetType);
}
