package io.basc.framework.data.generator;

/**
 * ID生成器
 * 
 * @author wcnnkh
 *
 * @param <T> id类型
 */
public interface IdGenerator<T> {
	T next();
}
