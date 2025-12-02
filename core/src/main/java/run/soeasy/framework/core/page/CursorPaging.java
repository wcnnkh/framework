package run.soeasy.framework.core.page;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Listable;

/**
 * 基于游标的分页实现 使用懒加载机制，首次访问数据时执行实际查询
 * <p>核心特性：
 * 1. 懒加载：当前页数据首次访问时才执行查询，避免无效数据库/远程调用；
 * 2. 游标定位：通过游标ID（K类型）标识分页位置，而非传统偏移量；
 * 3. 总数懒计算：总记录数优先取查询结果的已知总数，未知时遍历所有页累加计算；
 * 4. 线程安全：双重检查锁定保证懒加载的线程安全性。</p>
 * 
 * @author soeasy.run
 *
 * @param <K> 游标的类型，用于标识分页位置（如Long型偏移量、String型唯一标识等）
 * @param <V> 分页内容的元素类型
 */
public class CursorPaging<K, V> implements Paging<K, V> {
	/** 
	 * 当前页数据，使用双重检查锁定机制延迟初始化
	 * <p>首次调用{@link #getCurrentPage()}时执行实际查询，查询后缓存结果</p>
	 */
	private volatile Pageable<K, V> currentPage;
	
	/** 
	 * 当前页的游标ID
	 * <p>作为分页查询的起始位置标识，由构造器传入，不可修改</p>
	 */
	private final K cursorId;
	
	/** 
	 * 每页大小
	 * <p>表示单次查询返回的最大元素数量，构造时校验需大于0</p>
	 */
	private final int pageSize;
	
	/** 
	 * 分页查询器，不可为null
	 * <p>封装实际的分页查询逻辑，入参为游标ID和每页大小，返回分页结果Pageable</p>
	 */
	@NonNull
	private final PagingQuery<K, Pageable<K, V>> pagingQuery;
	
	/** 
	 * 总记录数，null表示总数未知，需遍历所有页计算
	 * <p>若手动通过{@link #setTotal(Long)}设置，则优先使用该值；否则通过{@link #getTotal()}计算</p>
	 */
	private Long total;

	/**
	 * 创建默认每页大小的游标分页实例（每页大小为{@link Integer#MAX_VALUE}）
	 * <p>适用于“一次性查询所有数据”的场景，本质是不分页查询</p>
	 * 
	 * @param cursorId    起始游标ID，作为分页查询的起始位置标识
	 * @param pagingQuery 分页查询器，不可为null，封装实际查询逻辑
	 * @throws IllegalArgumentException 若pagingQuery为null时抛出（由@NonNull注解触发）
	 */
	public CursorPaging(K cursorId, @NonNull PagingQuery<K, Pageable<K, V>> pagingQuery) {
		this(cursorId, Integer.MAX_VALUE, pagingQuery);
	}

	/**
	 * 创建指定每页大小的分页实例
	 * 
	 * @param cursorId    起始游标ID，作为分页查询的起始位置标识
	 * @param pageSize    每页大小，需大于0
	 * @param pagingQuery 分页查询器，不可为null，封装实际查询逻辑
	 * @throws IllegalArgumentException 若pageSize≤0或pagingQuery为null时抛出
	 */
	public CursorPaging(K cursorId, int pageSize, @NonNull PagingQuery<K, Pageable<K, V>> pagingQuery) {
		Assert.isTrue(pageSize > 0, "PageSize must be greater than to 0");
		this.cursorId = cursorId;
		this.pageSize = pageSize;
		this.pagingQuery = pagingQuery;
	}

	/**
	 * 获取当前页数据，使用双重检查锁定实现懒加载
	 * <p>首次调用时执行实际查询，并缓存结果；后续调用直接返回缓存的currentPage</p>
	 * <p>线程安全：通过volatile修饰currentPage + 同步代码块，保证多线程下仅执行一次查询</p>
	 * 
	 * @return 当前页的Pageable对象，永不返回null（无数据时返回空Pageable）
	 */
	private Pageable<K, V> getCurrentPage() {
		if (currentPage == null) {
			synchronized (this) {
				if (currentPage == null) {
					currentPage = pagingQuery.query(cursorId, pageSize);
					if (currentPage == null) {
						// 处理无数据情况
						currentPage = new Cursor<>(cursorId, Listable.empty(), null, null);
					}
				}
			}
		}
		return currentPage;
	}

	/**
	 * 获取当前页的游标ID
	 * 
	 * @return 当前页的游标ID，与构造器传入的起始游标ID一致，不可修改
	 */
	@Override
	public final K getCursorId() {
		return cursorId;
	}

	/**
	 * 获取当前页的元素列表（懒加载）
	 * <p>首次调用时触发{@link #getCurrentPage()}执行实际查询，后续调用返回缓存数据</p>
	 * 
	 * @return 当前页的元素集合Elements<V>，永不返回null（无数据时返回空Elements）
	 */
	@Override
	public final Elements<V> getElements() {
		return getCurrentPage().getElements();
	}

	/**
	 * 获取下一页的游标ID（懒加载）
	 * <p>首次调用时触发{@link #getCurrentPage()}执行实际查询，后续调用返回缓存结果</p>
	 * <p>返回null表示无下一页数据</p>
	 * 
	 * @return 下一页的游标ID，null表示无下一页
	 */
	@Override
	public final K getNextCursorId() {
		return getCurrentPage().getNextCursorId();
	}

	/**
	 * 获取每页大小
	 * 
	 * @return 每页大小，构造时校验需大于0，不可修改
	 */
	@Override
	public final int getPageSize() {
		return pageSize;
	}

	/**
	 * 获取总记录数（懒计算）
	 * <p>计算规则（优先级从高到低）：
	 * 1. 若已通过{@link #setTotal(Long)}设置total，直接返回该值；
	 * 2. 若当前页查询结果包含已知总数（isKnowTotal=true），返回当前页的总数；
	 * 3. 遍历所有分页，累加每一页的元素数量得到总记录数（性能较低，仅适用于总数未知场景）。</p>
	 * <p>注意：遍历累加时使用{@link Math#addExact(long, long)}，总数溢出会抛出ArithmeticException</p>
	 * 
	 * @return 总记录数，≥0；若总数未知且遍历后无数据，返回0
	 * @throws ArithmeticException 当累加总数超出Long范围时抛出
	 */
	@Override
	public Long getTotal() {
		if (total != null) {
			return total;
		}

		if (getCurrentPage().isKnowTotal()) {
			return getCurrentPage().getTotal();
		}

		long total = 0;
		for (Paging<K, V> paging : pages()) {
			if (paging.isKnowTotal()) {
				return paging.getTotal();
			}
			total = Math.addExact(paging.getElements().count(), total);
		}
		return total;
	}

	/**
	 * 判断是否存在下一页（懒加载）
	 * <p>首次调用时触发{@link #getCurrentPage()}执行实际查询，后续调用返回缓存结果</p>
	 * 
	 * @return true表示存在下一页，false表示无下一页
	 */
	@Override
	public final boolean hasNextPage() {
		return getCurrentPage().hasNextPage();
	}

	/**
	 * 判断是否已知总记录数
	 * <p>判断规则：
	 * 1. 若已手动设置total（非null），返回true；
	 * 2. 若当前页查询结果包含已知总数，返回true；
	 * 3. 其他情况返回false（需遍历计算总数）。</p>
	 * 
	 * @return true表示已知总记录数，false表示未知
	 */
	@Override
	public boolean isKnowTotal() {
		return total != null || getCurrentPage().isKnowTotal();
	}

	/**
	 * 创建新的游标分页实例，跳转到指定游标位置并指定每页大小
	 * <p>新实例会继承当前实例的“总数是否已知”状态：
	 * - 若当前实例已知总数（isKnowTotal=true），新实例直接复用该总数；
	 * - 若当前实例未知总数，新实例total为null。</p>
	 * 
	 * @param cursorId 目标游标ID，作为新分页的起始位置
	 * @param pageSize 新分页的每页大小，需大于0
	 * @return 新的CursorPaging实例，继承当前实例的总数状态
	 * @throws IllegalArgumentException 若pageSize≤0时抛出
	 */
	@Override
	public Paging<K, V> query(K cursorId, int pageSize) {
		CursorPaging<K, V> paging = new CursorPaging<>(cursorId, pageSize, pagingQuery);
		paging.setTotal(isKnowTotal() ? getTotal() : total);
		return paging;
	}

	/**
	 * 手动设置总记录数
	 * <p>设置后{@link #getTotal()}会优先返回该值，无需遍历计算，提升性能</p>
	 * 
	 * @param total 总记录数，可为null（表示恢复“总数未知”状态）；非null时需≥0
	 * @throws IllegalArgumentException 若total<0时抛出
	 */
	public void setTotal(Long total) {
		Assert.isTrue(total == null || total >= 0, "Total must be greater than or equal to 0");
		this.total = total;
	}
}