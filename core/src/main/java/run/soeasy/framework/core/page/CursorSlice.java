package run.soeasy.framework.core.page;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 基于游标的分页切片默认实现（不可变设计）
 * 核心特征：
 * 1. 所有成员变量不可变，保证线程安全；
 * 2. reload操作返回新实例，复用Streamable的动态刷新能力；
 * 3. 依赖Streamable承载分页数据，支持流式处理。
 * 
 * @author soeasy.run
 * @param <K> 游标类型（如Long/String/复合标识）
 * @param <V> 分页元素类型（业务实体类）
 */
@Getter
@RequiredArgsConstructor
public class CursorSlice<K, V> implements Slice<K, V> {
	private final K currentCursor;
	private final Streamable<V> elements;
	private final K nextCursor;
	private final Long totalCount;

	@Override
	public Stream<V> stream() {
		return elements.stream();
	}

	@Override
	public Slice<K, V> reload() {
		Streamable<V> reload = elements.reload();
		if (reload == elements) {
			return this;
		}
		return new CursorSlice<>(currentCursor, reload, nextCursor, totalCount);
	}
}