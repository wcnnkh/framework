package run.soeasy.framework.core.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 基于游标的分页切片接口，定义高效处理大数据集分页查询的核心方法 核心特征： 1. 聚焦大数据集高效分页，基于游标定位下一页，避免偏移量分页的性能问题；
 * 2. 总记录数为可选能力，未知总数时可避免全量数据遍历的开销； 3. 继承Streamable，直接支持流式处理当前分页数据。
 * 
 * @author soeasy.run
 * @param <K> 游标类型（如 Long/String/复合标识），用于标识分页起始位置
 * @param <V> 分页内容的元素类型（如业务实体类）
 */
public interface Slice<K, V> extends Streamable<V> {
	/**
	 * 获取当前页的游标（分页定位标识） 通常为上一页最后一个元素的唯一标识，用于定位当前分页的起始位置
	 * 
	 * @return 当前游标，首次请求时可为null
	 */
	K getCurrentCursor();

	/**
	 * 获取下一页的游标（分页定位标识） 用于标识下一页数据的起始位置
	 * 
	 * @return 下一页游标，若无下一页则返回null
	 */
	K getNextCursor();

	/**
	 * 判断是否存在下一页 基于{@link #getNextCursor()}的返回值进行判断
	 * 
	 * @return 存在下一页返回true，否则返回false
	 */
	default boolean hasNext() {
		return getNextCursor() != null;
	}

	/**
	 * 判断是否已知总记录数 某些场景下（如大数据集）可能无法高效获取总数，此时返回false；
	 * 未知总数的分页在调用getTotalCount()时会使用代价较高的循环计数
	 * 
	 * @return 如果已知总数返回true，否则返回false
	 */
	default boolean isTotalCountKnown() {
		return getTotalCount() != null;
	}

	/**
	 * 获取总记录数 如果{@link #isTotalCountKnown()}返回false，此方法可能会触发全量数据遍历，性能开销较大
	 * 
	 * @return 总记录数
	 */
	Long getTotalCount();
	
	@Override
	default Slice<K, V> cached() {
		return cached(ArrayList::new);
	}

	@Override
	default Slice<K, V> cached(@NonNull Supplier<? extends Collection<V>> collectionFactory) {
		return new CachedSlice<>(this, collectionFactory);
	}

	@Override
	default Slice<K, V> reload() {
		return this;
	}
}