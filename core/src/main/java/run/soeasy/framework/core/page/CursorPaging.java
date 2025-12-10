package run.soeasy.framework.core.page;

import java.util.Iterator;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 基于游标的分页实现 使用懒加载机制，首次访问数据时执行实际查询
 * <p>
 * 核心特性： 1. 懒加载：当前页数据首次访问时才执行查询，避免无效数据库/远程调用； 2. 游标定位：通过游标（K类型）标识分页位置，而非传统偏移量；
 * 3. 总数懒计算：总记录数优先取查询结果的已知总数，未知时遍历所有页累加计算； 4. 线程安全：双重检查锁定保证懒加载的线程安全性。
 * </p>
 * 
 * @author soeasy.run
 * @param <K> 游标的类型，用于标识分页位置（如Long型偏移量、String型唯一标识等）
 * @param <V> 分页内容的元素类型
 */
public class CursorPaging<K, V> implements Paging<K, V>, SliceWrapper<K, V, Slice<K, V>> {
	/**
	 * 当前页数据，使用双重检查锁定机制延迟初始化
	 * <p>
	 * 首次调用{@link #getSource()}时执行实际查询，查询后缓存结果
	 * </p>
	 */
	private volatile Slice<K, V> source;

	/**
	 * 当前页的游标
	 * <p>
	 * 作为分页查询的起始位置标识，由构造器传入，不可修改
	 * </p>
	 */
	private final K cursor;

	/**
	 * 每页大小
	 * <p>
	 * 表示单次查询返回的最大元素数量，构造时校验需严格大于0（不允许为0或负数）
	 * </p>
	 */
	private final int pageSize;

	/**
	 * 分页查询器，不可为null
	 * <p>
	 * 封装实际的分页查询逻辑，入参为游标和每页大小，返回分页结果Pageable
	 * </p>
	 */
	@NonNull
	private final PagingQuery<K, Slice<K, V>> pagingQuery;

	/**
	 * 总记录数，null表示总数未知，需遍历所有页计算
	 * <p>
	 * 若手动通过{@link #setTotalCount(Long)}设置，则优先使用该值；否则通过{@link #getTotalCount()}计算
	 * </p>
	 */
	private Long totalCount;

	/**
	 * 创建默认每页大小的游标分页实例（每页大小为{@link Integer#MAX_VALUE}）
	 * <p>
	 * 适用于“一次性查询所有数据”的场景，本质是不分页查询
	 * </p>
	 * 
	 * @param cursor      起始游标，作为分页查询的起始位置标识
	 * @param pagingQuery 分页查询器，不可为null，封装实际查询逻辑
	 * @throws IllegalArgumentException 若pagingQuery为null时抛出（由@NonNull注解触发）
	 */
	public CursorPaging(K cursor, @NonNull PagingQuery<K, Slice<K, V>> pagingQuery) {
		this(cursor, Integer.MAX_VALUE, pagingQuery);
	}

	/**
	 * 创建指定每页大小的分页实例
	 * 
	 * @param cursor      起始游标，作为分页查询的起始位置标识
	 * @param pageSize    每页大小，必须>0（不允许为0或负数）
	 * @param pagingQuery 分页查询器，不可为null，封装实际查询逻辑
	 * @throws IllegalArgumentException 若pageSize≤0或pagingQuery为null时抛出
	 */
	public CursorPaging(K cursor, int pageSize, @NonNull PagingQuery<K, Slice<K, V>> pagingQuery) {
		Assert.isTrue(pageSize > 0, "Page size must be greater than 0, cannot be 0 or negative");
		this.cursor = cursor;
		this.pageSize = pageSize;
		this.pagingQuery = pagingQuery;
	}

	/**
	 * 获取当前页数据，使用双重检查锁定实现懒加载
	 * <p>
	 * 首次调用时执行实际查询，并缓存结果；后续调用直接返回缓存的currentPage
	 * </p>
	 * <p>
	 * 线程安全：通过volatile修饰currentPage + 同步代码块，保证多线程下仅执行一次查询
	 * </p>
	 * 
	 * @return 当前页的Pageable对象，永不返回null（无数据时返回空Pageable）
	 */
	@Override
	public Slice<K, V> getSource() {
		if (source == null) {
			synchronized (this) {
				if (source == null) {
					source = pagingQuery.query(cursor, pageSize);
					if (source == null) {
						// 处理无数据情况
						source = new CursorSlice<>(cursor, Streamable.empty(), null, null);
					}
				}
			}
		}
		return source;
	}

	/**
	 * 获取当前页的游标
	 * 
	 * @return 当前页的游标，与构造器传入的起始游标一致，不可修改
	 */
	@Override
	public final K getCurrentCursor() {
		return cursor;
	}

	/**
	 * 获取每页大小
	 * <p>
	 * 构造时已校验>0，返回值恒>0，不可修改
	 * </p>
	 * 
	 * @return 每页大小（恒>0）
	 */
	@Override
	public final int getPageSize() {
		return pageSize;
	}

	/**
	 * 获取总记录数（懒计算）
	 * <p>
	 * 计算规则（优先级从高到低）： 1. 若已通过{@link #setTotalCount(Long)}设置totalCount，直接返回该值； 2.
	 * 若当前页查询结果包含已知总数（isKnownTotal=true），返回当前页的总数； 3.
	 * 遍历所有分页，累加每一页的元素数量得到总记录数（性能较低，仅适用于总数未知场景）。
	 * </p>
	 * <p>
	 * 注意：遍历累加时使用{@link Math#addExact(long, long)}，总数溢出会抛出ArithmeticException
	 * </p>
	 * 
	 * @return 总记录数，≥0；若总数未知且遍历后无数据，返回0
	 * @throws ArithmeticException 当累加总数超出Long范围时抛出
	 */
	@Override
	public Long getTotalCount() {
		if (totalCount != null) {
			return totalCount;
		}

		if (getSource().isTotalCountKnown()) {
			return getSource().getTotalCount();
		}

		long total = 0;
		try (Stream<Paging<K, V>> stream = pages().stream()) {
			Iterator<Paging<K, V>> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Paging<K, V> page = iterator.next();
				if (page.isTotalCountKnown()) {
					return page.getTotalCount();
				}
				total = Math.addExact(page.count(), total);
			}
		}
		return total;
	}

	/**
	 * 判断是否已知总记录数
	 * <p>
	 * 判断规则： 1. 若已手动设置totalCount（非null），返回true； 2. 若当前页查询结果包含已知总数，返回true； 3.
	 * 其他情况返回false（需遍历计算总数）。
	 * </p>
	 * 
	 * @return true表示已知总记录数，false表示未知
	 */
	@Override
	public boolean isTotalCountKnown() {
		return totalCount != null || SliceWrapper.super.isTotalCountKnown();
	}

	/**
	 * 创建新的游标分页实例，跳转到指定游标位置并指定每页大小
	 * <p>
	 * 新实例会继承当前实例的“总数是否已知”状态： - 若当前实例已知总数（isKnownTotal=true），新实例直接复用该总数； -
	 * 若当前实例未知总数，新实例totalCount为null。
	 * </p>
	 * 
	 * @param cursor   目标游标，作为新分页的起始位置
	 * @param pageSize 新分页的每页大小，必须>0（不允许为0或负数）
	 * @return 新的CursorPaging实例，继承当前实例的总数状态
	 * @throws IllegalArgumentException 若pageSize≤0时抛出
	 */
	@Override
	public Paging<K, V> query(K cursor, int pageSize) {
		CursorPaging<K, V> paging = new CursorPaging<>(cursor, pageSize, pagingQuery);
		paging.setTotalCount(isTotalCountKnown() ? getTotalCount() : totalCount);
		return paging;
	}

	/**
	 * 手动设置总记录数
	 * <p>
	 * 设置后{@link #getTotalCount()}会优先返回该值，无需遍历计算，提升性能
	 * </p>
	 * 
	 * @param totalCount 总记录数，可为null（表示恢复“总数未知”状态）；非null时需≥0
	 * @throws IllegalArgumentException 若totalCount<0时抛出
	 */
	public void setTotalCount(Long totalCount) {
		Assert.isTrue(totalCount == null || totalCount >= 0, "Total count must be greater than or equal to 0");
		this.totalCount = totalCount;
	}
}