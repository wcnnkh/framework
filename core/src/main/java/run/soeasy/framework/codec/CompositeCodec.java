package run.soeasy.framework.codec;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 组合式双向编解码器实现，将两个 {@link Coder}（编码器和解码器）
 * 组合为一个完整的 {@link Codec}。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li><b>组合模式</b>：将编码器（S→T）和解码器（T→S）组合为统一的
 * 双向编解码器，避免手动实现 {@code Codec} 接口的样板代码</li>
 * <li><b>精确类型保留</b>：通过泛型参数 {@code E} 和 {@code D}
 * 分别约束编码器和解码器的具体类型，使 {@link #encoder()} 和
 * {@link #decoder()} 返回精确类型而非原始 {@link Coder}，
 * 调用方无需强转即可访问子类特有方法</li>
 * <li><b>不可变</b>：编码器和解码器在构造时注入，实例创建后不可更改</li>
 * <li><b>默认实现复用</b>：{@link #from(Codec)}、{@link #to(Codec)}、
 * {@link #reversed()} 等操作直接继承 {@code Codec} 接口的默认实现，
 * 无需额外编码</li>
 * </ul>
 *
 * <p>
 * <b>典型用途：</b>
 * <ul>
 * <li>作为 {@code Codec} 的便捷实现类，快速组合已有的 {@code Coder} 实例</li>
 * <li>在 {@code Codec} 的 {@link Codec#from(Codec)} / {@link Codec#to(Codec)}
 * 默认实现中作为返回类型，保留组合后的精确类型</li>
 * <li>作为自定义 {@code Codec} 实现的基类，子类只需提供编码器和解码器</li>
 * </ul>
 *
 * <p>
 * <b>使用示例：</b>
 *
 * <pre>{@code
 * // 组合两个 Coder 为 Codec
 * Coder<byte[], String> encoder = Base64Coder.encode();
 * Coder<String, byte[]> decoder = Base64Coder.decode();
 * Codec<byte[], String> base64Codec = new CompositeCodec<>(encoder, decoder);
 *
 * // 直接编解码
 * String encoded = base64Codec.encode("hello".getBytes());
 * byte[] decoded = base64Codec.decode(encoded);
 *
 * // 精确类型返回
 * Base64Coder encodeCoder = base64Codec.encoder(); // 无需强转
 * Base64Coder decodeCoder = base64Codec.decoder(); // 无需强转
 *
 * // 链式组合
 * Codec<byte[], Integer> chained = base64Codec.to(intStringCodec);
 * }</pre>
 *
 * @author soeasy.run
 * @param <S> 源数据类型（解码后类型，编码前类型）
 * @param <T> 目标数据类型（编码后类型，解码前类型）
 * @param <E> 编码器的具体类型，必须实现 {@link Coder}{@code <S, T>}
 * @param <D> 解码器的具体类型，必须实现 {@link Coder}{@code <T, S>}
 * @see Codec
 * @see Coder
 */
@RequiredArgsConstructor
public class CompositeCodec<S, T, E extends Coder<S, T>, D extends Coder<T, S>>
		implements Codec<S, T> {

	/**
	 * 编码器实例，负责 S → T 方向的转换。
	 */
	@NonNull
	private final E encoder;

	/**
	 * 解码器实例，负责 T → S 方向的转换。
	 */
	@NonNull
	private final D decoder;

	/**
	 * 返回解码器实例。
	 * <p>
	 * 协变返回类型为 {@code D}（解码器的具体类型），
	 * 调用方无需强转即可使用子类特有方法。
	 *
	 * @return 解码器实例，不可为 null
	 */
	@Override
	public D decoder() {
		return decoder;
	}

	/**
	 * 返回编码器实例。
	 * <p>
	 * 协变返回类型为 {@code E}（编码器的具体类型），
	 * 调用方无需强转即可使用子类特有方法。
	 *
	 * @return 编码器实例，不可为 null
	 */
	@Override
	public E encoder() {
		return encoder;
	}
}