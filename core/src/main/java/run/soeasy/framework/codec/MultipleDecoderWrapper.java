package run.soeasy.framework.codec;

/**
 * 多重解码器包装器接口，扩展自{@link MultipleDecoder}和{@link DecoderWrapper}，
 * 用于包装其他多重解码器实例，支持通过装饰器模式为多重解码器添加额外功能 （如次数校验、过程监控、结果验证）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>透明委托：所有解码操作透明转发给被包装的多重解码器{@link #getSource()}</li>
 * <li>装饰器模式：支持在不修改原解码器的情况下添加额外功能</li>
 * <li>次数感知：可对解码次数进行校验、限制或统计</li>
 * <li>类型安全：保持泛型类型一致性，确保包装前后的解码行为兼容</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>次数限制：限制最大解码次数，防止无限循环</li>
 * <li>过程监控：记录每次解码的耗时和结果</li>
 * <li>结果验证：对多次解码后的结果进行有效性验证</li>
 * <li>参数校验：验证解码次数是否在有效范围内</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 解码数据类型（源数据和解码结果类型相同）
 * @param <W> 被包装的多重解码器类型（必须实现{@link MultipleDecoder<D>}）
 * @see MultipleDecoder
 * @see DecoderWrapper
 */
public interface MultipleDecoderWrapper<D, W extends MultipleDecoder<D>>
		extends MultipleDecoder<D>, DecoderWrapper<D, D, W> {
	int getDecodeMultiple();

	@Override
	default D decode(D source) throws CodecException {
		int count = getDecodeMultiple();
		return count == 1 ? getSource().decode(source) : getSource().decode(source, count);
	}

	/**
	 * 对源数据执行指定次数的解码操作（委托给被包装的多重解码器）。
	 * <p>
	 * 该方法将多次解码请求透明转发给{@link #getSource()}返回的多重解码器， 实现装饰器模式的核心委托逻辑。
	 * 
	 * @param source 待解码的源数据，不可为null
	 * @param count  解码次数（≥0）
	 * @return 经过指定次数解码后的数据
	 * @throws CodecException          当解码过程中发生错误时抛出
	 * @throws IllegalArgumentException 当count<0时抛出
	 */
	@Override
	default D decode(D source, int count) throws CodecException {
		return getSource().decode(source, count * getDecodeMultiple());
	}

	/**
	 * 创建固定执行次数的多重解码器（委托给被包装的多重解码器）。
	 * <p>
	 * 返回的新解码器在调用{@link #decode(Object)}时， 会自动执行被包装解码器指定次数的解码操作，等价于：
	 * 
	 * <pre>
	 * getSource().decode(source, count);
	 * </pre>
	 * 
	 * @param count 固定解码次数（≥0）
	 * @return 新的多重解码器实例
	 * @throws IllegalArgumentException 当count<0时抛出
	 */
	@Override
	default MultipleDecoder<D> multiple(int count) {
		return getSource().multiple(count * getDecodeMultiple());
	}
}