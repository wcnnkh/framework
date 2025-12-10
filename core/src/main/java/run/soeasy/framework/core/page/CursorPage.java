package run.soeasy.framework.core.page;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 基于游标的分页实现类 封装分页数据及游标信息，用于高效处理大数据集的分页查询
 * 
 * @author soeasy.run
 * @param <K> 游标类型，用于标识分页起始位置
 * @param <V> 分页数据项的类型
 */
@Data
public class CursorPage<K, V> implements Pageable<K, V> {
	/** 当前页的游标，用于标识数据起始位置 */
	private final K currentCursor;
	/** 分页数据的源集合，不可为null */
	@NonNull
	private final Streamable<V> elements;
	/** 下一页的游标，null表示无下一页 */
	private final K nextCursor;
	/** 总记录数，null表示未知总数 */
	private final Long totalCount;

	/**
	 * 基于游标的分页构造方法
	 * 
	 * @param currentCursor 当前页游标（首次请求可为null）
	 * @param elements      分页数据集合（不可为null）
	 * @param nextCursor    下一页游标（无下一页则为null）
	 * @param totalCount    总记录数（未知则为null，需≥0）
	 */
	public CursorPage(K currentCursor, @NonNull Streamable<V> elements, K nextCursor, Long totalCount) {
		Assert.isTrue(totalCount == null || totalCount >= 0, "Total count must be greater than or equal to 0");
		this.currentCursor = currentCursor;
		this.elements = elements;
		this.nextCursor = nextCursor;
		this.totalCount = totalCount;
	}
}