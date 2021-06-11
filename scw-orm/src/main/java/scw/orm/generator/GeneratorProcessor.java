package scw.orm.generator;

public interface GeneratorProcessor {
	<T> void process(Class<? extends T> entityClass, Object entity);
}
