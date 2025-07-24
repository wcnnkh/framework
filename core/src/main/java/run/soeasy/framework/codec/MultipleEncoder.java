package run.soeasy.framework.codec;

/**
 * 多重编码器接口，扩展自{@link Encoder}，支持对同一数据执行多次编码操作， 适用于需要重复应用相同编码逻辑的场景（如多层加密、多轮数据转换）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>重复编码：通过{@link #encode(Object, int)}指定编码次数</li>
 * <li>链式组合：通过{@link #multiple(int)}创建固定次数的多重编码器</li>
 * <li>类型一致性：源数据和编码结果类型必须相同（E→E）</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>多层加密：对数据执行多次加密操作增强安全性</li>
 * <li>多轮数据转换：对数据进行多次相同规则的转换</li>
 * <li>模拟复杂过程：通过多次简单编码模拟复杂编码过程</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 编码数据类型（源数据和编码结果类型相同）
 * @see Encoder
 */
public interface MultipleEncoder<E> extends Encoder<E, E> {
	/**
	 * 对源数据执行多次编码操作。
	 * <p>
	 * 连续调用{@link #encode(Object)}方法指定次数，每次编码的结果作为下一次编码的输入， 等价于：
	 * 
	 * <pre>
	 * E result = source;
	 * for (int i = 0; i < count; i++) {
	 * 	result = encode(result);
	 * }
	 * return result;
	 * </pre>
	 * 
	 * @param source 待编码的源数据，不可为null
	 * @param count  编码次数（≥0）
	 * @return 经过指定次数编码后的数据
	 * @throws CodecException          当编码过程中发生错误时抛出
	 * @throws IllegalArgumentException 当count<0时抛出
	 */
	default E encode(E source, int count) throws CodecException {
		E e = source;
		for (int i = 0; i < count; i++) {
			e = encode(e);
		}
		return e;
	}

	/**
	 * 创建固定执行次数的多重编码器（装饰器模式）。
	 * <p>
	 * 返回的新编码器在调用{@link #encode(Object)}时， 会自动执行当前编码器指定次数的编码操作，等价于：
	 * 
	 * <pre>
	 * MultipleEncoder<E> original = ...;
	 * MultipleEncoder<E> fixed = original.multiple(3);
	 * // 以下两者效果相同
	 * fixed.encode(data);
	 * original.encode(data, 3);
	 * </pre>
	 * 
	 * @param count 固定编码次数（≥0）
	 * @return 新的多重编码器实例
	 * @throws IllegalArgumentException 当count<0时抛出
	 */
	default MultipleEncoder<E> multiple(int count) {
		return new MultipleEncoderWrapper<E, MultipleEncoder<E>>() {

			@Override
			public MultipleEncoder<E> getSource() {
				return MultipleEncoder.this;
			}

			@Override
			public int getEncodeMultiple() {
				return count;
			}
		};
	}
}