package run.soeasy.framework.codec;

/**
 * 多重编码器包装器接口，扩展自{@link MultipleEncoder}和{@link EncoderWrapper}，
 * 用于包装其他多重编码器实例，支持通过装饰器模式为多重编码器添加额外功能 （如次数校验、过程监控、结果缓存）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>透明委托：所有编码操作透明转发给被包装的多重编码器{@link #getSource()}</li>
 * <li>装饰器模式：支持在不修改原编码器的情况下添加额外功能</li>
 * <li>次数感知：可对编码次数进行校验、限制或统计</li>
 * <li>类型安全：保持泛型类型一致性，确保包装前后的编码行为兼容</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>次数限制：限制最大编码次数，防止无限循环</li>
 * <li>过程监控：记录每次编码的耗时和结果</li>
 * <li>结果缓存：对相同输入和次数的编码结果进行缓存</li>
 * <li>参数校验：验证编码次数是否在有效范围内</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 编码数据类型（源数据和编码结果类型相同）
 * @param <W> 被包装的多重编码器类型（必须实现{@link MultipleEncoder}）
 * @see MultipleEncoder
 * @see EncoderWrapper
 */
public interface MultipleEncoderWrapper<E, W extends MultipleEncoder<E>>
		extends MultipleEncoder<E>, EncoderWrapper<E, E, W> {
	int getEncodeMultiple();

	@Override
	default E encode(E source) throws CodecException {
		int count = getEncodeMultiple();
		return count == 1 ? getSource().encode(source) : getSource().encode(source, count);
	}

	/**
	 * 对源数据执行指定次数的编码操作（委托给被包装的多重编码器）。
	 * <p>
	 * 该方法将多次编码请求透明转发给{@link #getSource()}返回的多重编码器， 实现装饰器模式的核心委托逻辑。
	 * 
	 * @param source 待编码的源数据，不可为null
	 * @param count  编码次数（≥0）
	 * @return 经过指定次数编码后的数据
	 * @throws CodecException          当编码过程中发生错误时抛出
	 * @throws IllegalArgumentException 当count<0时抛出
	 */
	@Override
	default E encode(E source, int count) throws CodecException {
		return getSource().encode(source, count * getEncodeMultiple());
	}

	/**
	 * 创建固定执行次数的多重编码器（委托给被包装的多重编码器）。
	 * <p>
	 * 返回的新编码器在调用{@link #encode(Object)}时， 会自动执行被包装编码器指定次数的编码操作，等价于：
	 * 
	 * <pre>
	 * getSource().encode(source, count);
	 * </pre>
	 * 
	 * @param count 固定编码次数（≥0）
	 * @return 新的多重编码器实例
	 * @throws IllegalArgumentException 当count<0时抛出
	 */
	@Override
	default MultipleEncoder<E> multiple(int count) {
		return getSource().multiple(count * getEncodeMultiple());
	}
}