package run.soeasy.framework.codec;

/**
 * 多重解码器接口，扩展自{@link Decoder}，支持对同一数据执行多次解码操作， 适用于需要重复应用相同解码逻辑的场景（如多层解密、多轮数据还原）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>重复解码：通过{@link #decode(Object, int)}指定解码次数</li>
 * <li>链式组合：通过{@link #multiple(int)}创建固定次数的多重解码器</li>
 * <li>类型一致性：源数据和解码结果类型必须相同（D→D）</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>多层解密：对数据执行多次解密操作还原原始内容</li>
 * <li>多轮数据还原：对数据进行多次相同规则的还原</li>
 * <li>模拟复杂过程：通过多次简单解码模拟复杂解码过程</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 解码数据类型（源数据和解码结果类型相同）
 * @see Decoder
 */
public interface MultipleDecoder<D> extends Decoder<D, D> {
	/**
	 * 对源数据执行多次解码操作。
	 * <p>
	 * 连续调用{@link #decode(Object)}方法指定次数，每次解码的结果作为下一次解码的输入， 等价于：
	 * 
	 * <pre>
	 * D result = source;
	 * for (int i = 0; i &lt; count; i++) {
	 * 	result = decode(result);
	 * }
	 * return result;
	 * </pre>
	 * 
	 * @param source 待解码的源数据，不可为null
	 * @param count  解码次数（≥0）
	 * @return 经过指定次数解码后的数据
	 * @throws CodecException          当解码过程中发生错误时抛出
	 */
	default D decode(D source, int count) throws CodecException {
		D v = source;
		for (int i = 0; i < count; i++) {
			v = decode(v);
		}
		return v;
	}

	/**
	 * 创建固定执行次数的多重解码器（装饰器模式）。
	 * <p>
	 * 返回的新解码器在调用{@link #decode(Object)}时， 会自动执行当前解码器指定次数的解码操作，等价于：
	 * 
	 * <pre>
	 * MultipleDecoder&lt;D&gt; original = ...;
	 * MultipleDecoder&lt;D&gt; fixed = original.multiple(3);
	 * // 以下两者效果相同
	 * fixed.decode(data);
	 * original.decode(data, 3);
	 * </pre>
	 * 
	 * @param count 固定解码次数（≥0）
	 * @return 新的多重解码器实例
	 */
	default MultipleDecoder<D> multiple(int count) {
		return new MultipleDecoderWrapper<D, MultipleDecoder<D>>() {
			@Override
			public MultipleDecoder<D> getSource() {
				return MultipleDecoder.this;
			}

			@Override
			public int getDecodeMultiple() {
				return count;
			}
		};
	}
}