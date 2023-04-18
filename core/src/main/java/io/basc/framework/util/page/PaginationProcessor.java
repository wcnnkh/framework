package io.basc.framework.util.page;

import io.basc.framework.util.Assert;

public class PaginationProcessor<K, T> implements PageableProcessor<Long, T> {
	private final long total;
	private final Pager<? super Long, T> pager;

	public PaginationProcessor(long total, Pager<? super Long, T> pager) {
		Assert.requiredArgument(pager != null, "pager");
		this.total = total;
		this.pager = pager;
	}

	@Override
	public Pageable<Long, T> process(Long start, long limit) {
		return new StandardPageable<>(start, pager.paging(start, limit),
				PageSupport.hasMore(total, start, limit) ? PageSupport.getNextStart(start, limit) : null);
	}

}
