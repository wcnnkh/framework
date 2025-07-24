package run.soeasy.framework.sequences;

import lombok.NonNull;

/**
 * 整型序列生成器接口，扩展自{@link NumberSequence}接口， 专门用于生成{@code int}类型的序列值，提供整型特化方法和默认实现，
 * 适用于需要生成有限范围整型序列的场景（如本地计数器、有限ID生成）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>整型特化：提供{@link #nextInt()}等整型直接操作方法</li>
 * <li>类型转换：自动将整型值适配为{@link Number}类型返回</li>
 * <li>步长控制：支持自定义步长（正数递增，负数递减）</li>
 * <li>范围检查：超出有效范围时抛出{@link UnsupportedOperationException}</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>本地计数器：多线程环境下的递增计数（如1,2,3...）</li>
 * <li>有限ID生成：在指定范围内生成唯一整型ID（如1-1000的循环ID）</li>
 * <li>循环序列：结合步长生成循环使用的整型序列（如0,1,2,0,1,2...）</li>
 * <li>分页索引：生成分页查询的页码索引序列</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see NumberSequence
 */
@FunctionalInterface
public interface IntSequence extends NumberSequence {
	/**
	 * 获取下一个序列值（带步长参数）。
	 * <p>
	 * 1. 将步长转换为int类型 2. 调用{@link #nextInt(int)}获取整型值 3. 自动适配为{@link Number}类型返回
	 * 
	 * @param step 步长值（不可为null）
	 * @return 下一个整型序列值（自动适配为Number类型）
	 * @throws UnsupportedOperationException 当生成值超出有效范围时抛出
	 */
	@Override
	default Number next(@NonNull Number step) throws UnsupportedOperationException {
		return next(step.intValue());
	}

	/**
	 * 获取下一个整型序列值（使用默认步长）。
	 * <p>
	 * 等价于调用{@code nextInt(getStep().intValue())}，
	 * 默认步长为{@link NumberSequence#DEFAULT_STEP}。
	 * 
	 * @return 下一个整型值
	 * @throws UnsupportedOperationException 当生成值超出有效范围时抛出
	 */
	default int nextInt() throws UnsupportedOperationException {
		return nextInt(getStep().intValue());
	}

	/**
	 * 获取下一个整型序列值（自定义步长）。
	 * <p>
	 * 实现类应根据步长生成下一个整型值， 推荐实现以下逻辑：
	 * <ul>
	 * <li>正数步长：生成递增序列（如step=2生成2,4,6...）</li>
	 * <li>负数步长：生成递减序列（如step=-1生成5,4,3...）</li>
	 * </ul>
	 * 
	 * @param step 步长值（决定递增/递减幅度）
	 * @return 下一个整型值
	 * @throws UnsupportedOperationException 当无法生成有效序列值时抛出
	 */
	int nextInt(int step) throws UnsupportedOperationException;

	@Override
	default NumberSequence step(@NonNull Number step) {
		return new SpecifiedStepsIntSequence<>(this, step);
	}
}