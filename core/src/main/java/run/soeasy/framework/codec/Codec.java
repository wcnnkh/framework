package run.soeasy.framework.codec;

/**
 * 编解码器接口，集成{@link Encoder}和{@link Decoder}功能， 提供数据双向转换能力，支持泛型类型参数实现不同类型数据的编解码，
 * 适用于需要同时处理编码和解码的场景（如序列化/反序列化、加密/解密）。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>双向转换：同时支持编码（D→E）和解码（E→D）操作</li>
 * <li>链式组合：通过{@link #from(Codec)}和{@link #to(Codec)}组合多个编解码器</li>
 * <li>反向操作：通过{@link #reverse()}获取编解码方向相反的新编解码器</li>
 * <li>身份编解码：通过{@link #identity()}获取不改变数据的编解码器</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>数据序列化：对象与字节流的相互转换</li>
 * <li>数据加密：明文与密文的相互转换</li>
 * <li>格式转换：JSON/XML与对象的相互转换</li>
 * <li>协议转换：不同协议数据的相互转换</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 源数据类型（解码后类型，编码前类型）
 * @param <E> 目标数据类型（编码后类型，解码前类型）
 * @see Encoder
 * @see Decoder
 */
public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D> {
	/**
	 * 获取不改变数据的身份编解码器。
	 * <p>
	 * 该编解码器的编码和解码结果与源数据完全一致， 适用于不需要实际编解码的场景（如空操作）。
	 * 
	 * @param <R> 数据类型
	 * @return 身份编解码器实例
	 */
	@SuppressWarnings("unchecked")
	public static <R> Codec<R, R> identity() {
		return (Codec<R, R>) IdentityCodec.INSTANCE;
	}

	/**
	 * 组合前置编解码器形成新的编解码器（装饰器模式）。
	 * <p>
	 * 新编解码器的编解码流程为：
	 * 
	 * <pre>
	 * 编码：F → [前置编解码器] → D → [当前编解码器] → E
	 * 解码：E → [当前编解码器] → D → [前置编解码器] → F
	 * </pre>
	 * 
	 * @param <F>   前置编解码器的源类型
	 * @param codec 前置编解码器，不可为null
	 * @return 组合后的新编解码器
	 */
	default <F> Codec<F, E> from(Codec<F, D> codec) {
		return new ChainCodec<F, D, E>() {

			@Override
			public Decoder<E, D> getFromDecoder() {
				return Codec.this;
			}

			@Override
			public Decoder<D, F> getToDecoder() {
				return codec;
			}

			@Override
			public Encoder<F, D> getFromEncoder() {
				return codec;
			}

			@Override
			public Encoder<D, E> getToEncoder() {
				return Codec.this;
			}
		};
	}

	/**
	 * 获取编解码方向相反的新编解码器。
	 * <p>
	 * 原编解码器的编码操作将变为新编解码器的解码操作，反之亦然：
	 * 
	 * <pre>
	 * 原编解码器：encode(D) → E，decode(E) → D
	 * 反转后：    encode(E) → D，decode(D) → E
	 * </pre>
	 * 
	 * @return 反向编解码器实例
	 */
	default <T> Codec<E, D> reverse() {
		return new ChainCodec<E, E, D>() {

			@Override
			public Decoder<D, E> getFromDecoder() {
				return this;
			}

			@Override
			public Decoder<E, E> getToDecoder() {
				return Decoder.identity();
			}

			@Override
			public Encoder<E, E> getFromEncoder() {
				return Encoder.identity();
			}

			@Override
			public Encoder<E, D> getToEncoder() {
				return this;
			}
		};
	}

	/**
	 * 组合后置编解码器形成新的编解码器（装饰器模式）。
	 * <p>
	 * 新编解码器的编解码流程为：
	 * 
	 * <pre>
	 * 编码：D → [当前编解码器] → E → [后置编解码器] → T
	 * 解码：T → [后置编解码器] → E → [当前编解码器] → D
	 * </pre>
	 * 
	 * @param <T>   后置编解码器的目标类型
	 * @param codec 后置编解码器，不可为null
	 * @return 组合后的新编解码器
	 */
	default <T> Codec<D, T> to(Codec<E, T> codec) {
		return new ChainCodec<D, E, T>() {

			@Override
			public Decoder<T, E> getFromDecoder() {
				return codec;
			}

			@Override
			public Decoder<E, D> getToDecoder() {
				return Codec.this;
			}

			@Override
			public Encoder<D, E> getFromEncoder() {
				return Codec.this;
			}

			@Override
			public Encoder<E, T> getToEncoder() {
				return codec;
			}
		};
	}
}