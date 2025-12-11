package run.soeasy.framework.core.streaming;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;

/**
 * 纯映射的Streamable装饰器：基于原有Streamable做无副作用的map转换
 * <p>
 * 纯映射（pureMapper）要求：无副作用、幂等、不修改原元素，仅做数据转换
 * </p>
 *
 * @param <E> 原元素类型
 * @param <T> 映射后元素类型
 * @author soeasy.run
 */
@Getter
public class PureMappedStreamable<E, T> implements Streamable<T> {

	/** 原始Streamable实例，所有核心能力的底层依赖（上游保证非空） */
	private final Streamable<E> originalStreamable;

	/** 纯映射器：无副作用、幂等，仅做E->T的转换（上游保证非空） */
	private final Function<? super E, ? extends T> pureMapper;

	/**
	 * 构造纯映射的Streamable
	 * <p>
	 * 非空约束：originalStreamable和pureMapper由上游调用方保证非空
	 * </p>
	 *
	 * @param originalStreamable 原始Streamable
	 * @param pureMapper         纯映射器
	 */
	public PureMappedStreamable(Streamable<E> originalStreamable, Function<? super E, ? extends T> pureMapper) {
		this.originalStreamable = originalStreamable;
		this.pureMapper = pureMapper;
	}

	/**
	 * 生成映射后的流，依赖原Streamable的流并执行map转换
	 * <p>
	 * 流的关闭由上层export/transfer等方法保证，无需手动处理
	 * </p>
	 */
	@Override
	public Stream<T> stream() {
		return originalStreamable.stream().map(pureMapper);
	}

	// ========== 核心性能优化：复用原Streamable的计算结果 ==========
	@Override
	public long count() {
		return originalStreamable.count();
	}

	@Override
	public boolean isEmpty() {
		return originalStreamable.isEmpty();
	}

	@Override
	public boolean isUnique() {
		return originalStreamable.isUnique();
	}

	// ========== 补充高性能方法：避免重复创建流/映射 ==========
	@Override
	public Optional<T> findFirst() {
		return originalStreamable.findFirst().map(pureMapper);
	}

	@Override
	public T first() {
		E originalFirst = originalStreamable.first();
		return originalFirst == null ? null : pureMapper.apply(originalFirst);
	}

	@Override
	public T last() {
		E originalLast = originalStreamable.last();
		return originalLast == null ? null : pureMapper.apply(originalLast);
	}

	@Override
	public T getUnique() throws NoSuchElementException, NoUniqueElementException {
		E originalUnique = originalStreamable.getUnique();
		return pureMapper.apply(originalUnique);
	}

	@Override
	public T getAt(int index) {
		E originalUnique = originalStreamable.getAt(index);
		return pureMapper.apply(originalUnique);
	}
}