package io.basc.framework.data.generator;

/**
 * ID工厂
 * 
 * @author wcnnkh
 *
 * @param <T> id类型
 */
public interface IdGeneratorFactory<T> {
	IdGenerator<T> getIdGenerator(String name);
}
