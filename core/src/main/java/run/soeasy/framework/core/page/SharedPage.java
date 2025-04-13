package run.soeasy.framework.core.page;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SharedPage<K, T> extends SharedCursor<K, T> implements Page<K, T> {
	private static final long serialVersionUID = 1L;
	private long total;
	private long pageSize;

	/**
	 * 默认的构造方法
	 * 
	 */
	public SharedPage() {
	}

	public SharedPage(K cursorId, long pageSize) {
		this(cursorId, null, pageSize, 0);
	}

	public SharedPage(K cursorId, List<T> rows, long pageSize, long total) {
		this(cursorId, rows, null, pageSize, total);
	}

	public SharedPage(K cursorId, List<T> rows, K nextCursorId, long pageSize, long total) {
		super(cursorId, rows, nextCursorId);
		this.total = total;
		this.pageSize = pageSize;
	}

	public SharedPage(Page<K, T> page) {
		super(page);
		this.total = page.getTotal();
	}

	@Override
	public void setList(List<T> list) {
		if (total == 0) {
			total = list.size();
		}
		super.setList(list);
	}
}
