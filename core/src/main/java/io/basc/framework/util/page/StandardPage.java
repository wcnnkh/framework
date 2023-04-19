package io.basc.framework.util.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StandardPage<K, T> extends StandardPageable<K, T> implements Page<K, T> {
	private final long total;
	private long pageSize;

	public StandardPage(long total, long pageSize) {
		this.total = total;
		this.pageSize = pageSize;
	}

	public StandardPage(long total, long pageSize, Pageable<K, T> pageable) {
		super(pageable);
		this.total = total;
		this.pageSize = pageSize;
	}

	public StandardPage(Page<K, T> page) {
		super(page);
		this.total = page.getTotal();
		this.pageSize = page.getPageSize();
	}
}
