package io.basc.framework.util;

/**
 * 装饰器模式
 * 
 * @author shuchaowen
 *
 */
public interface Decorator {
	<T> T getDelegate(Class<T> targetType);
}
