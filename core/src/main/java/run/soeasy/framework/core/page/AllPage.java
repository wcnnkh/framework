package run.soeasy.framework.core.page;

public class AllPage<S extends Pageable<K, T>, K, T> extends AllCursor<S, K, T> implements Page<K, T> {

	public AllPage(S source) {
		super(source);
	}

	@Override
	public long getTotal() {
		return source.getTotal();
	}

	@Override
	public long getPageSize() {
		// 优化父类实现
		long pageSize = source.getTotal();
		long mod = pageSize % source.getPageSize();
		if (mod != 0) {
			pageSize = pageSize - mod + source.getPageSize();
		}
		return pageSize;
	}
}