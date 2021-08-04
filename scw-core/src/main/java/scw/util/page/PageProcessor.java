package scw.util.page;

@FunctionalInterface
public interface PageProcessor<T> {
	Page<T> process(long page, long count);
}
