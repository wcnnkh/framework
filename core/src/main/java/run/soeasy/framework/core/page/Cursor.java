package run.soeasy.framework.core.page;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Listable;
import run.soeasy.framework.core.collection.ListableWrapper;

/**
 * 游标实现类 封装分页数据及游标信息，实现序列化以便跨进程传递
 * 
 * @author soeasy.run
 *
 * @param <K> 游标ID的类型，需实现Serializable
 * @param <V> 分页数据项的类型
 */
@Data
public class Cursor<K, V> implements Pageable<K, V>, ListableWrapper<V, Listable<V>>, Serializable {
	private static final long serialVersionUID = 1L;

	/** 当前页的游标ID，用于标识数据起始位置 */
	private final K cursorId;
	/** 分页数据的源集合，不可为null */
	@NonNull
	private final Listable<V> source;
	/** 下一页的游标ID，null表示无下一页 */
	private final K nextCursorId;

	private final Long total;
}