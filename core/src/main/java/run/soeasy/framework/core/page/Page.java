package run.soeasy.framework.core.page;

import lombok.Data;
import lombok.NonNull;

@Data
public class Page<K, V> implements SliceWrapper<K, V, Slice<K, V>> {
	private final Slice<K, V> source;
	private final long pageNumber;
	private final int pageSize;

	public Page(@NonNull Slice<K, V> source, long pageNumber, int pageSize) {
		this.source = source;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}
}
