package run.soeasy.framework.core.streaming;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import run.soeasy.framework.core.exchange.Stage;

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
class MappedStreamable<E, T> implements Streamable<T> {

	/** 原始Streamable实例，所有核心能力的底层依赖（上游保证非空） */
	private final Streamable<E> originalStreamable;

	/** 纯映射器：无副作用、幂等，仅做E->T的转换（上游保证非空） */
	private final Function<? super E, ? extends T> mapper;

	/**
	 * 构造纯映射的Streamable
	 * <p>
	 * 非空约束：originalStreamable和pureMapper由上游调用方保证非空
	 * </p>
	 *
	 * @param originalStreamable 原始Streamable
	 * @param mapper             纯映射器
	 */
	public MappedStreamable(Streamable<E> originalStreamable, Function<? super E, ? extends T> mapper) {
		this.originalStreamable = originalStreamable;
		this.mapper = mapper;
	}

	/**
	 * 生成映射后的流，依赖原Streamable的流并执行map转换
	 * <p>
	 * 流的关闭由上层export/transfer等方法保证，无需手动处理
	 * </p>
	 */
	@Override
	public Stream<T> stream() {
		return originalStreamable.stream().map(mapper);
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

	@Override
	public Stage<T> first() {
		return originalStreamable.first().map(mapper);
	}

	@Override
	public Stage<T> last() {
		return originalStreamable.last().map(mapper);
	}

	@Override
	public Stage<T> unique() {
		return originalStreamable.unique().map(mapper);
	}

	@Override
	public Stage<T> at(int index) {
		return originalStreamable.at(index).map(mapper);
	}

	@Override
	public Optional<T> findFirst() {
		return originalStreamable.findFirst().map(mapper);
	}

	@Override
	public Optional<T> findAt(int index) {
		return originalStreamable.findAt(index).map(mapper);
	}

	@Override
	public Optional<T> findLast() {
		return originalStreamable.findLast().map(mapper);
	}

	@Override
	public Optional<T> findUnique() {
		return originalStreamable.findUnique().map(mapper);
	}

	@Override
	public T getAt(int index) throws IndexOutOfBoundsException {
		return mapper.apply(originalStreamable.getAt(index));
	}

	@Override
	public T getFirst() throws NoSuchElementException {
		return mapper.apply(originalStreamable.getFirst());
	}

	@Override
	public T getLast() throws NoSuchElementException {
		return mapper.apply(originalStreamable.getLast());
	}

	@Override
	public T getUnique() throws NoSuchElementException, NoUniqueElementException {
		return mapper.apply(originalStreamable.getUnique());
	}
}