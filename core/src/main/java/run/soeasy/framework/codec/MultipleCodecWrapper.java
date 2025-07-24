package run.soeasy.framework.codec;

/**
 * 多重编解码器包装器接口，扩展自{@link MultipleCodec}、{@link CodecWrapper}、
 * {@link MultipleEncoderWrapper}和{@link MultipleDecoderWrapper}，
 * 用于包装其他多重编解码器实例，支持通过装饰器模式为多重编解码器添加额外功能 （如次数校验、过程监控、结果缓存）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>透明委托：所有编解码操作透明转发给被包装的多重编解码器{@link #getSource()}</li>
 * <li>装饰器模式：支持在不修改原编解码器的情况下添加额外功能</li>
 * <li>双向多重增强：同时增强多次编码和多次解码能力</li>
 * <li>次数感知：可对编解码次数进行校验、限制或统计</li>
 * <li>链式组合：支持与其他编解码器组合，形成复杂的编解码流程</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>次数限制：限制最大编解码次数，防止无限循环</li>
 * <li>过程监控：记录每次编解码的耗时和结果</li>
 * <li>结果缓存：对相同输入和次数的编解码结果进行缓存</li>
 * <li>参数校验：验证编解码次数是否在有效范围内</li>
 * <li>安全增强：在多次编解码前后添加安全检查或数据脱敏</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <T> 编解码数据类型（源数据和编解码结果类型相同）
 * @param <W> 被包装的多重编解码器类型（必须实现{@link MultipleCodec<T>}）
 * @see MultipleCodec
 * @see CodecWrapper
 * @see MultipleEncoderWrapper
 * @see MultipleDecoderWrapper
 */
public interface MultipleCodecWrapper<T, W extends MultipleCodec<T>>
		extends MultipleCodec<T>, CodecWrapper<T, T, W>, MultipleEncoderWrapper<T, W>, MultipleDecoderWrapper<T, W> {
	int getMultiple();

	@Override
	default int getDecodeMultiple() {
		return this.getMultiple();
	}

	@Override
	default int getEncodeMultiple() {
		return this.getMultiple();
	}

	/**
	 * 创建固定执行次数的多重编解码器（委托给被包装的多重编解码器）。
	 * <p>
	 * 返回的新编解码器在调用{@link #encode(Object)}或{@link #decode(Object)}时，
	 * 会自动执行被包装编解码器指定次数的编码或解码操作，等价于：
	 * 
	 * <pre>
	 * getSource().encode(source, count); // 多次编码
	 * getSource().decode(source, count); // 多次解码
	 * </pre>
	 * 
	 * @param count 固定编解码次数（≥0）
	 * @return 新的多重编解码器实例
	 * @throws IllegalArgumentException 当count<0时抛出
	 */
	@Override
	default MultipleCodec<T> multiple(int count) {
		return getSource().multiple(count * getMultiple());
	}
}