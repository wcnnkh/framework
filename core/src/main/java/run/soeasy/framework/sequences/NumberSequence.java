package run.soeasy.framework.sequences;

import lombok.NonNull;

/**
 * 数字序列生成器接口，扩展自{@link Sequence<Number>}接口， 专门用于生成数字类型的序列值，支持步长控制、范围限制和默认实现，
 * 适用于需要生成连续数字序列的场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>步长控制：支持自定义步长（默认值为{@link #DEFAULT_STEP}）</li>
 * <li>动态生成：通过{@link #next(Number)}方法按步长生成下一个序列值</li>
 * <li>默认实现：提供{@link #next()}的默认实现，调用带参数版本</li>
 * <li>异常处理：超出有效范围时抛出{@link UnsupportedOperationException}</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>计数器：生成递增/递减的数字序列（如1,2,3...）</li>
 * <li>ID生成：生成指定步长的唯一数字ID</li>
 * <li>数值迭代：按步长遍历数字范围（如0,2,4...）</li>
 * </ul>
 * 
 * @author soeasy.run
 */

@FunctionalInterface
public interface NumberSequence extends Sequence<Number> {
	/** 默认步长值（1），用于无特殊步长要求的场景 */
	public static final Number DEFAULT_STEP = 1;

	/**
	 * 获取序列生成的步长（默认{@link #DEFAULT_STEP}）。
	 * <p>
	 * 子类可覆盖此方法提供自定义步长：
	 * <ul>
	 * <li>正数步长：生成递增序列（如步长2生成2,4,6...）</li>
	 * <li>负数步长：生成递减序列（如步长-1生成5,4,3...）</li>
	 * </ul>
	 * 
	 * @return 步长值，不可为null
	 */
	@NonNull
	default Number getStep() {
		return DEFAULT_STEP;
	}

	/**
	 * 获取下一个序列值（默认实现）。
	 * <p>
	 * 调用{@link #next(Number)}并使用{@link #getStep()}的返回值， 等价于{@code next(getStep())}。
	 * 
	 * @return 下一个序列值
	 */
	@Override
	@NonNull
	default Number next() throws UnsupportedOperationException {
		return next(getStep());
	}

	/**
	 * 获取下一个序列值（带步长参数）。
	 * <p>
	 * 实现类应根据步长生成下一个序列值， 超出有效范围时必须抛出{@link UnsupportedOperationException}。
	 * 
	 * @param step 步长值（决定递增/递减幅度），不可为null
	 * @return 下一个序列值
	 * @throws UnsupportedOperationException 当无法生成有效序列值时抛出
	 */
	@NonNull
	Number next(@NonNull Number step) throws UnsupportedOperationException;

	default NumberSequence step(@NonNull Number step) {
		return new SpecifiedStepsNumberSequence<>(this, step);
	}
}