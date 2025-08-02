package run.soeasy.framework.codec;

import java.util.function.BiPredicate;

import run.soeasy.framework.core.ObjectUtils;

/**
 * 编码器接口，定义数据编码转换的标准，扩展自{@link BiPredicate}以支持编码结果验证，
 * 支持泛型类型参数实现不同类型数据的编码转换，适用于数据加密、格式转换等场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>函数式设计：可作为lambda表达式或方法引用的目标类型</li>
 * <li>双向验证：通过{@link #test(Object, Object)}验证编码结果正确性</li>
 * <li>链式组合：通过{@link #fromEncoder(Encoder)}和{@link #toEncoder(Encoder)}组合编码器</li>
 * <li>身份编码：通过{@link #identity()}获取不改变数据的编码器</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>数据加密：将明文转换为密文（如Base64、AES加密）</li>
 * <li>格式转换：对象与字节流、JSON等格式的相互转换</li>
 * <li>参数校验：编码时同时验证结果有效性</li>
 * <li>多级处理：组合多个编码器实现复杂编码流程</li>
 * </ul>
 * 
 * @author soeasy.run
 * @param <D> 源数据类型（待编码类型）
 * @param <E> 编码后数据类型
 * @see BiPredicate
 * @see EncodeException
 */
@FunctionalInterface
public interface Encoder<D, E> extends BiPredicate<D, E> {
	/**
	 * 对源数据进行编码转换。
	 * <p>
	 * 实现类应定义具体的编码逻辑，将源数据转换为目标编码格式， 编码失败时抛出{@link CodecException}。
	 * 
	 * @param source 待编码的源数据
	 * @return 编码后的数据
	 * @throws CodecException 编码过程中发生错误时抛出
	 */
	E encode(D source) throws CodecException;

	/**
	 * 验证编码结果是否正确（实现{@link BiPredicate#test}）。
	 * <p>
	 * 1. 对源数据执行编码{@link #encode(Object)} 2. 比较编码结果与预期值是否相等
	 * 
	 * @param source 待编码的源数据
	 * @param encode 预期的编码结果
	 * @return true表示编码结果与预期一致
	 * @throws CodecException 编码过程中发生错误时抛出
	 */
	@Override
	default boolean test(D source, E encode) throws CodecException {
		return ObjectUtils.equals(this.encode(source), encode);
	}

	/**
	 * 组合前置编码器形成新的编码器（装饰器模式）。
	 * <p>
	 * 新编码器的编码流程为：
	 * 
	 * <pre>
	 * F → [前置编码器] → D → [当前编码器] → E
	 * </pre>
	 * 
	 * 适用于需要先将F类型转换为D类型再编码为E类型的场景。
	 * 
	 * @param <F>     前置编码器的源类型
	 * @param encoder 前置编码器，不可为null
	 * @return 组合后的新编码器
	 */
	default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
		return new ChainEncoder<F, D, E>() {

			@Override
			public Encoder<F, D> getFromEncoder() {
				return encoder;
			}

			@Override
			public Encoder<D, E> getToEncoder() {
				return Encoder.this;
			}
		};
	}

	/**
	 * 组合后置编码器形成新的编码器（装饰器模式）。
	 * <p>
	 * 新编码器的编码流程为：
	 * 
	 * <pre>
	 * D → [当前编码器] → E → [后置编码器] → T
	 * </pre>
	 * 
	 * 适用于需要将当前编码结果E进一步转换为T类型的场景。
	 * 
	 * @param <T>     后置编码器的目标类型
	 * @param encoder 后置编码器，不可为null
	 * @return 组合后的新编码器
	 */
	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
		return new ChainEncoder<D, E, T>() {

			@Override
			public Encoder<D, E> getFromEncoder() {
				return Encoder.this;
			}

			@Override
			public Encoder<E, T> getToEncoder() {
				return encoder;
			}
		};
	}

	/**
	 * 获取不改变数据的身份编码器。
	 * <p>
	 * 该编码器的编码结果与源数据完全一致， 适用于不需要实际编码的场景（如空操作）。
	 * 
	 * @param <R> 数据类型
	 * @return 身份编码器实例
	 */
	@SuppressWarnings("unchecked")
	public static <R> Encoder<R, R> identity() {
		return (Encoder<R, R>) IdentityCodec.INSTANCE;
	}
}