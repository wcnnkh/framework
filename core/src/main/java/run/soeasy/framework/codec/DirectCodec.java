package run.soeasy.framework.codec;

/**
 * 直接方法实现的双向编解码器接口，将 {@link #encode(Object)} 和
 * {@link #decode(Object)} 方法直接作为编码器和解码器，无需组合外部
 * {@link Coder} 实例。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li><b>方法即编解码器</b>：{@link #encoder()} 和 {@link #decoder()}
 * 默认实现通过方法引用（{@code this::encode} / {@code this::decode}）
 * 直接返回当前实例的方法作为 {@link Coder}，无需额外对象</li>
 * <li><b>最小化实现</b>：实现类只需关注 {@link #encode(Object)} 和
 * {@link #decode(Object)} 的业务逻辑，其余由默认方法自动适配</li>
 * <li><b>与 {@link CompositeCodec} 的对称</b>：{@code DirectCodec}
 * 通过方法直接提供编解码逻辑，{@code CompositeCodec} 通过组合外部
 * {@code Coder} 提供编解码逻辑，两者覆盖不同的使用场景</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>快速实现编解码逻辑：只需编写 encode/decode 方法，无需构造 Coder 对象</li>
 * <li>作为自定义 Codec 的基类接口：实现类专注业务逻辑</li>
 * <li>与 {@link CompositeCodec} 配合：简单场景用 DirectCodec，复杂组合用 CompositeCodec</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <S> 源数据类型（解码后类型，编码前类型）
 * @param <T> 目标数据类型（编码后类型，解码前类型）
 * @see Codec
 * @see CompositeCodec
 */
public interface DirectCodec<S, T> extends Codec<S, T> {

	@Override
	default Coder<T, S> decoder() {
		return this::decode;
	}

	@Override
	default Coder<S, T> encoder() {
		return this::encode;
	}

	@Override
	T encode(S source);

	@Override
	S decode(T target);
}