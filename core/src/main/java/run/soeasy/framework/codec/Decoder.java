package run.soeasy.framework.codec;

import run.soeasy.framework.core.collection.Elements;

/**
 * 解码器接口，定义数据解码转换的标准，支持泛型类型参数实现不同类型数据的解码转换，
 * 适用于数据解密、格式解析等场景，与{@link Encoder}接口形成编解码闭环。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>函数式设计：可作为lambda表达式或方法引用的目标类型</li>
 * <li>批量处理：通过{@link #decodeAll(Elements)}实现集合批量解码</li>
 * <li>链式组合：通过{@link #fromDecoder(Decoder)}和{@link #toDecoder(Decoder)}组合解码器</li>
 * <li>身份解码：通过{@link #identity()}获取不改变数据的解码器</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>数据解密：将密文转换为明文（如Base64解码、AES解密）</li>
 * <li>格式解析：将字节流、JSON等格式转换为对象</li>
 * <li>多级处理：组合多个解码器实现复杂解码流程</li>
 * <li>空操作场景：使用身份解码器跳过实际解码过程</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <E> 源数据类型（待解码类型）
 * @param <D> 解码后数据类型
 * @see Encoder
 * @see DecodeException
 */
@FunctionalInterface
public interface Decoder<E, D> {
	/**
	 * 对源数据进行解码转换。
	 * <p>
	 * 实现类应定义具体的解码逻辑，将源数据转换为目标格式， 解码失败时抛出{@link CodecException}。
	 * 
	 * @param source 待解码的源数据，不可为null
	 * @return 解码后的数据
	 * @throws CodecException 解码过程中发生错误时抛出
	 */
	D decode(E source) throws CodecException;

	/**
	 * 组合前置解码器形成新的解码器（装饰器模式）。
	 * <p>
	 * 新解码器的解码流程为：
	 * 
	 * <pre>
	 * F → [前置解码器] → E → [当前解码器] → D
	 * </pre>
	 * 
	 * 适用于需要先将F类型转换为E类型再解码为D类型的场景。
	 * 
	 * @param <F>     前置解码器的源类型
	 * @param decoder 前置解码器，不可为null
	 * @return 组合后的新解码器
	 */
	default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return new ChainDecoder<F, E, D>() {

			@Override
			public Decoder<F, E> getFromDecoder() {
				return decoder;
			}

			@Override
			public Decoder<E, D> getToDecoder() {
				return Decoder.this;
			}
		};
	}

	/**
	 * 组合后置解码器形成新的解码器（装饰器模式）。
	 * <p>
	 * 新解码器的解码流程为：
	 * 
	 * <pre>
	 * E → [当前解码器] → D → [后置解码器] → T
	 * </pre>
	 * 
	 * 适用于需要将当前解码结果D进一步转换为T类型的场景。
	 * 
	 * @param <T>     后置解码器的目标类型
	 * @param decoder 后置解码器，不可为null
	 * @return 组合后的新解码器
	 */
	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return new ChainDecoder<E, D, T>() {

			@Override
			public Decoder<E, D> getFromDecoder() {
				return Decoder.this;
			}

			@Override
			public Decoder<D, T> getToDecoder() {
				return decoder;
			}
		};
	}

	/**
	 * 获取不改变数据的身份解码器。
	 * <p>
	 * 该解码器的解码结果与源数据完全一致， 适用于不需要实际解码的场景（如空操作）。
	 * 
	 * @param <R> 数据类型
	 * @return 身份解码器实例
	 */
	@SuppressWarnings("unchecked")
	public static <R> Decoder<R, R> identity() {
		return (Decoder<R, R>) IdentityCodec.INSTANCE;
	}
}