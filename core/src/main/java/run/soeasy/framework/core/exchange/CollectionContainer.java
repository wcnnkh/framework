package run.soeasy.framework.core.exchange;

import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 基于Collection的注册表核心实现
 * <p>
 * 核心定位：轻量级注册表实现，直接操作底层Collection中的业务元素，兼顾简洁性与性能，贴合Registry接口契约。
 * <p>
 * 核心规则（必读）： 1.
 * 回滚支持规则：Set子类（HashSet/ConcurrentSkipListSet等）保证元素唯一性，支持精准回滚；List/Queue等非Set集合不保证，不支持回滚；
 * 2. 原子性边界：单次add/remove操作的原子性由底层Collection保证，跨操作（如注册+回滚）无原子性保证； 3.
 * 线程安全：完全依赖底层Collection的线程安全特性（建议使用并发集合：CopyOnWriteArrayList/ConcurrentSkipListSet）；
 * 4. 回滚语义：支持回滚的集合（Set）中，注册回滚=删除元素，注销回滚=重新添加元素；不支持回滚的集合回滚逻辑置空； 5.
 * 空元素规则：是否允许null元素由底层Collection决定（如HashSet禁止null，ArrayList允许null）； 6.
 * 匹配规则：元素的equals/hashCode决定匹配逻辑，需业务元素正确实现这两个方法。
 *
 * @author soeasy.run
 * @param <E> 注册表中存储的业务元素类型（建议正确实现equals/hashCode保证匹配准确性）
 * @see Registry 注册表核心接口契约
 * @see java.util.Collection 底层存储容器接口
 */
@RequiredArgsConstructor
@Getter
public class CollectionContainer<E, D extends Collection<E>> implements Registry<E>, Iterable<E> {
	/**
	 * 底层存储容器：直接存储业务元素
	 * <p>
	 * 建议使用并发安全实现类，避免多线程场景下的并发修改异常
	 */
	@NonNull
	private final D delegate;

	/**
	 * 判断当前注册表是否支持回滚操作
	 * <p>
	 * 判定逻辑（final方法，禁止子类重写）：
	 * <ul>
	 * <li>Set及其子类（HashSet/LinkedHashSet/ConcurrentSkipListSet）→ 返回true（支持精准回滚）；
	 * <li>非Set类型（List/Queue/Deque等）→ 返回false（不支持回滚）；
	 * <li>自定义Collection需继承Set并保证唯一性，否则按不支持回滚处理。
	 * </ul>
	 *
	 * @return true=支持回滚，false=不支持回滚
	 */
	public final boolean isRollbackSupported() {
		return delegate instanceof Set;
	}

	// --------------------- 注册表核心操作 ---------------------
	/**
	 * 获取注册表中所有业务元素（只读流）
	 * <p>
	 * 直接基于底层Collection生成只读Streamable，无转换开销，元素与底层存储完全一致。
	 *
	 * @return 只读的业务元素流（不支持修改操作，修改需通过register/deregister）
	 */
	@Override
	public Stream<E> stream() {
		return delegate.stream();
	}

	/**
	 * 注册单个业务元素（直接添加到底层Collection）
	 * <p>
	 * 操作逻辑： 1. 调用Collection.add(E)将元素添加到底层容器； 2.
	 * 支持回滚的集合场景：绑定回滚逻辑（注销该元素），返回支持回滚的成功操作句柄； 3. 不支持回滚的集合场景：回滚逻辑置空，返回不支持回滚的成功操作句柄；
	 * 4. 添加失败（如Set重复元素）返回失败状态的Operation。
	 *
	 * @param element 待注册的业务元素（非null，是否允许null由底层Collection决定）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（支持回滚的集合绑定回滚逻辑，否则无）；
	 *         <li>失败：Operation.failure（包含具体失败原因）。
	 *         </ul>
	 * @throws RuntimeException 底层Collection抛出的异常（如null元素不允许、并发修改异常）
	 */
	@Override
	public Operation register(E element) {
		try {
			boolean addSuccess = delegate.add(element);
			if (!addSuccess) {
				return Operation.failure(new RuntimeException(String.format("注册失败：元素添加失败（可能已存在），元素=%s", element)));
			}

			return Operation.success(isRollbackSupported() ? () -> {
				try {
					return deregister(element).sync().isSuccess();
				} catch (Exception e) {
					throw new RuntimeException(String.format("注册回滚失败，元素=%s", element), e);
				}
			} : null);
		} catch (Exception e) {
			return Operation.failure(new RuntimeException(String.format("注册异常，元素=%s", element), e));
		}
	}

	/**
	 * 注销单个业务元素（直接从底层Collection删除）
	 * <p>
	 * 操作逻辑： 1. 调用Collection.remove(E)从底层容器删除元素； 2.
	 * 支持回滚的集合场景：绑定回滚逻辑（重新注册该元素），返回支持回滚的成功操作句柄； 3. 不支持回滚的集合场景：回滚逻辑置空，返回不支持回滚的成功操作句柄；
	 * 4. 删除失败（元素不存在）返回失败状态的Operation。
	 *
	 * @param element 待注销的业务元素（非null）
	 * @return 操作句柄：
	 *         <ul>
	 *         <li>成功：Operation.success（支持回滚的集合绑定回滚逻辑，否则无）；
	 *         <li>失败：Operation.failure（包含具体失败原因）。
	 *         </ul>
	 * @throws RuntimeException 底层Collection抛出的异常（如并发修改异常）
	 */
	@Override
	public Operation deregister(E element) {
		try {
			boolean removeSuccess = delegate.remove(element);
			if (!removeSuccess) {
				return Operation.failure(new RuntimeException(String.format("注销失败：元素不存在或删除失败，元素=%s", element)));
			}

			// 仅支持回滚的集合绑定回滚逻辑（重新注册元素）
			return Operation.success(isRollbackSupported() ? () -> {
				try {
					return register(element).sync().isSuccess();
				} catch (Exception e) {
					throw new RuntimeException(String.format("注销回滚失败，元素=%s", element), e);
				}
			} : null);
		} catch (Exception e) {
			return Operation.failure(new RuntimeException(String.format("注销异常，元素=%s", element), e));
		}
	}

	// --------------------- 集合状态查询 ---------------------
	/**
	 * 判断注册表是否为空
	 * <p>
	 * 直接委托底层Collection.isEmpty()，无额外逻辑，反映底层容器的真实状态。
	 *
	 * @return true=注册表无元素，false=注册表包含至少一个元素
	 */
	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	/**
	 * 判断注册表是否包含指定业务元素
	 * <p>
	 * 直接委托底层Collection.contains(E)，基于元素的equals/hashCode匹配。
	 *
	 * @param element 待校验的业务元素（非null）
	 * @return true=包含该元素，false=不包含
	 */
	@Override
	public boolean contains(Object element) {
		return delegate.contains(element);
	}

	@Override
	public Operation reset() {
		delegate.clear();
		return Operation.success();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		delegate.forEach(action);
	}

	@Override
	public boolean isRandomAccess() {
		return delegate instanceof RandomAccess;
	}
}