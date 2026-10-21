package run.soeasy.framework.codec;

/**
 * 同类型双向数据编解码器接口，继承 {@link Codec} 并约束输入输出为同一类型，
 * 提供同类型数据的双向转换以及函数迭代（重复执行）能力。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li><b>同类型编解码</b>：编码和解码的输入输出均为同一类型 {@code T}，
 * 适用于加密/解密（同一算法）、压缩/解压、哈希加盐等对称操作场景</li>
 * <li><b>类型精确化</b>：覆盖 {@link Codec#encoder()} 和 {@link Codec#decoder()}，
 * 使其返回 {@link UnaryCoder} 类型，保证编码器和解码器均具备同类型转换和迭代能力</li>
 * <li><b>函数迭代</b>：通过 {@link #repeated(int)} 将编码和解码操作分别连续执行多次，
 * 适用于嵌套编码、多层加密、循环哈希等场景</li>
 * <li><b>恒等编解码器</b>：通过 {@link Codec#identity()} 获取同类型恒等编解码器</li>
 * </ul>
 *
 * <p>
 * <b>与 {@link Codec} 的关系：</b>
 * <ul>
 * <li>{@code Codec<T, T>} 是同类型编解码的特化形式</li>
 * <li>{@code UnaryCodec<T>} 为其提供语义化命名、精确返回类型以及额外能力
 * （{@link #repeated(int)}）</li>
 * <li>所有 {@code Codec} 的组合、反向操作在 {@code UnaryCodec} 中完全可用</li>
 * </ul>
 *
 * <p>
 * <b>与 {@link UnaryCoder} 的关系：</b>
 * <ul>
 * <li>{@code UnaryCoder<T>} 是单向同类型转换器</li>
 * <li>{@code UnaryCodec<T>} 组合两个 {@code UnaryCoder<T>}
 * （编码器和解码器），提供双向操作</li>
 * <li>{@link #encoder()} 和 {@link #decoder()} 均返回 {@code UnaryCoder<T>}，
 * 可独立用于单向转换或校验</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>对称加密/解密：同一密钥算法既加密又解密</li>
 * <li>数据压缩/解压：同一压缩算法支持双向操作</li>
 * <li>嵌套编码：多次连续编码（如双重 Base64、多层 XOR）</li>
 * <li>循环哈希：多次哈希迭代（如 PBKDF2 的多次摘要）</li>
 * <li>数据归一化：编码和解码均执行相同的数据清洗逻辑</li>
 * </ul>
 *
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * // 创建 UnaryCodec 实现（如 XOR 加密）
 * UnaryCodec<byte[]> xorCodec = new XorCodec(key);
 *
 * // 直接编解码
 * byte[] encoded = xorCodec.encode(data);
 * byte[] decoded = xorCodec.decode(encoded); // 对称算法中 decoded == data
 *
 * // 函数迭代：双重编码
 * UnaryCodec<byte[]> doubleXor = xorCodec.repeated(2);
 * byte[] doubleEncoded = doubleXor.encode(data); // 编码两次
 * byte[] doubleDecoded = doubleXor.decode(doubleEncoded); // 解码两次，恢复原数据
 *
 * // 获取编码器/解码器单独使用
 * UnaryCoder<byte[]> encoder = xorCodec.encoder();
 * byte[] result = encoder.apply(data); // 单次编码
 *
 * // 恒等编解码器
 * UnaryCodec<Object> identity = Codec.identity();
 * Object same = identity.encode(obj); // 返回 obj 本身
 * }</pre>
 *
 * @author soeasy.run
 * @param <T> 数据类型（编码和解码均为该类型）
 * @see Codec
 * @see UnaryCoder
 * @see Coder
 */
public interface UnaryCodec<T> extends Codec<T, T> {

	/**
	 * 获取编码器（负责 T → T 方向的同类型转换）。
	 * <p>
	 * 覆盖 {@link Codec#encoder()} 以返回 {@link UnaryCoder} 类型，
	 * 保证编码器具备同类型转换、校验和迭代能力。
	 *
	 * @return 同类型编码器实例，不可为 null
	 */
	@Override
	UnaryCoder<T> encoder();

	/**
	 * 获取解码器（负责 T → T 方向的同类型转换）。
	 * <p>
	 * 覆盖 {@link Codec#decoder()} 以返回 {@link UnaryCoder} 类型，
	 * 保证解码器具备同类型转换、校验和迭代能力。
	 *
	 * @return 同类型解码器实例，不可为 null
	 */
	@Override
	UnaryCoder<T> decoder();

	/**
	 * 返回一个新 {@link UnaryCodec}，将当前编解码器的编码和解码操作
	 * 分别连续执行指定次数。
	 * <p>
	 * 数学上等价于对编码器和解码器分别应用 fⁿ(x)：
	 * <pre>
	 * 编码：encodeⁿ(t) = encode(...(encode(t))...)（n 次）
	 * 解码：decodeⁿ(t) = decode(...(decode(t))...)（n 次）
	 * </pre>
	 *
	 * <p>
	 * 适用于嵌套编码、多层加密、循环哈希等需要重复执行同一转换的场景。
	 * 例如 {@code repeated(2)} 可实现双重 Base64 编码或两轮 XOR 加密。
	 *
	 * <p>
	 * 特殊情况优化：
	 * <ul>
	 * <li>{@code count == 1}：返回当前实例本身，零额外开销</li>
	 * <li>{@code count > 1}：返回组合后的新编解码器</li>
	 * </ul>
	 *
	 * @param count 执行次数，必须 ≥ 1
	 * @return 重复执行后的新 {@link UnaryCodec}
	 * @throws IllegalArgumentException 如果 count < 1
	 */
	default UnaryCodec<T> repeated(int count) {
		return new UnaryCodec<T>() {

			@Override
			public UnaryCoder<T> encoder() {
				return UnaryCodec.this.encoder().repeated(count);
			}

			@Override
			public UnaryCoder<T> decoder() {
				return UnaryCodec.this.decoder().repeated(count);
			}
		};
	}
}