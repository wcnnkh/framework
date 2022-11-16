package io.basc.framework.util.page;

public class AllPage<S extends Pages<K, T>, K, T> extends AllPageable<S, K, T>
		implements Page<K, T> {

	public AllPage(S source) {
		super(source);
	}
	
	@Override
	public long getTotal() {
		return source.getTotal();
	}
	
	@Override
	public long getCount() {
		return getTotal();
	}
}
