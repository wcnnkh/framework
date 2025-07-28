package run.soeasy.framework.codec;

/**
 * 多重编解码器接口，集成{@link Codec}、{@link MultipleEncoder}和{@link MultipleDecoder}功能，
 * 支持对同一数据执行多次编码和解码操作，适用于需要重复应用相同编解码逻辑的场景 （如多层加密/解密、多轮数据转换/还原）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>双向多重操作：同时支持多次编码（D→D）和多次解码（D→D）</li>
 * <li>链式组合：通过{@link #multiple(int)}创建固定次数的多重编解码器</li>
 * <li>类型一致性：源数据和编解码结果类型必须相同（T→T）</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>多层加密/解密：对数据执行多次加密/解密增强安全性</li>
 * <li>多轮数据转换/还原：对数据进行多次相同规则的转换和还原</li>
 * <li>模拟复杂过程：通过多次简单编解码模拟复杂编解码过程</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <T> 编解码数据类型（源数据和编解码结果类型相同）
 * @see Codec
 * @see MultipleEncoder
 * @see MultipleDecoder
 */
public interface MultipleCodec<T> extends Codec<T, T>, MultipleEncoder<T>, MultipleDecoder<T> {
	/**
	 * 创建固定执行次数的多重编解码器（装饰器模式）。
	 * <p>
	 * 返回的新编解码器在调用{@link #encode(Object)}或{@link #decode(Object)}时，
	 * 会自动执行当前编解码器指定次数的编码或解码操作，等价于：
	 * 
	 * <pre>
	 * MultipleCodec&lt;T&gt; original = ...;
	 * MultipleCodec&lt;T&gt; fixed = original.multiple(3);
	 * // 以下两者效果相同
	 * fixed.encode(data);  // 等价于 original.encode(data, 3)
	 * fixed.decode(data);  // 等价于 original.decode(data, 3)
	 * </pre>
	 * 
	 * @param count 固定编解码次数（≥0）
	 * @return 新的多重编解码器实例
	 * @throws IllegalArgumentException 当count&lt;0时抛出
	 */
	@Override
	default MultipleCodec<T> multiple(int count) {
		return new MultipleCodecWrapper<T, MultipleCodec<T>>() {
			@Override
			public MultipleCodec<T> getSource() {
				return MultipleCodec.this;
			}

			@Override
			public int getMultiple() {
				return count;
			}
		};
	}
}