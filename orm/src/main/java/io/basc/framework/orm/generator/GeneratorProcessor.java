package io.basc.framework.orm.generator;

public interface GeneratorProcessor {
	<T> void process(Class<? extends T> entityClass, Object entity);
}
