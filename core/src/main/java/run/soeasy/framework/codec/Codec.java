package run.soeasy.framework.codec;

import lombok.NonNull;

/**
 * 双向数据编解码器接口，组合两个 {@link Coder}（编码器和解码器）， 提供数据双向转换、链式组合、反向操作、闭环组合以及恒等编解码器能力。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li><b>双向转换</b>：通过 {@link #encode(Object)} 和 {@link #decode(Object)} 提供
 * 编码（S→T）和解码（T→S）操作，底层委托给 {@link #encoder()} 和 {@link #decoder()} 返回的
 * {@link Coder}</li>
 * <li><b>函数式访问</b>：通过 {@link #encoder()} 和 {@link #decoder()} 直接获取
 * {@link Coder} 实例，可当作 {@link java.util.function.Function} 传递给 Stream、Optional
 * 等函数式 API</li>
 * <li><b>链式组合</b>：通过 {@link #from(Codec)} 和 {@link #to(Codec)} 将多个
 * 编解码器串联，形成复合转换链路</li>
 * <li><b>反向操作</b>：通过 {@link #reversed()} 获取编解码方向相反的新实例， 即原编码变为解码、原解码变为编码</li>
 * <li><b>闭环组合</b>：通过 {@link #toUnary(Codec)} 将当前编解码器与映射编解码器组合， 转换为同类型的
 * {@link UnaryCodec}</li>
 * <li><b>恒等编解码器</b>：通过 {@link #identity()} 获取不做任何转换的 {@link UnaryCodec} 单例</li>
 * </ul>
 *
 * <p>
 * <b>与 {@link Coder} 的关系：</b>
 * <ul>
 * <li>{@code Coder} 是单向转换器，代表编码或解码中的<b>单一方向</b></li>
 * <li>{@code Codec} 是双向编解码器，内部通过组合两个 {@code Coder}（{@link #encoder()} 和
 * {@link #decoder()}）实现双向转换</li>
 * <li>{@code Codec} 的 {@code from}/{@code to} 组合操作底层委托给 {@code Coder} 的
 * {@link Coder#from(Coder)} / {@link Coder#to(Coder)} 实现</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>数据序列化/反序列化：对象与字节流、JSON/XML 的双向转换</li>
 * <li>数据加密/解密：明文与密文的相互转换</li>
 * <li>格式转换：不同数据表示形式的双向映射</li>
 * <li>协议适配：不同协议数据格式的双向转换</li>
 * </ul>
 *
 * <p>
 * <b>使用示例：</b>
 *
 * <pre>{@code
 * // 创建 Codec 实现（如 Base64Codec）
 * Codec<byte[], String> base64Codec = new Base64Codec();
 *
 * // 直接编解码
 * String encoded = base64Codec.encode("hello".getBytes());
 * byte[] decoded = base64Codec.decode(encoded);
 *
 * // 函数式使用
 * stream.map(base64Codec.encoder()).collect(Collectors.toList());
 *
 * // 链式组合：压缩 → Base64 编码 → 加密
 * Codec<byte[], byte[]> fullCodec = compressCodec.to(base64Codec).to(encryptCodec);
 * byte[] result = fullCodec.encode(rawData);
 *
 * // 反向操作
 * Codec<String, byte[]> reversed = base64Codec.reversed();
 * byte[] original = reversed.decode(encoded); // 等价于原 codec.encode()
 *
 * // 闭环组合：编码后通过 mapper 映射回源类型
 * UnaryCodec<byte[]> unary = base64Codec.toUnary(base64Codec.reversed());
 *
 * // 恒等编解码器
 * UnaryCodec<String> identity = Codec.identity();
 * }</pre>
 *
 * @author soeasy.run
 * @param <S> 源数据类型（解码后类型，编码前类型）
 * @param <T> 目标数据类型（编码后类型，解码前类型）
 * @see Coder
 * @see UnaryCodec
 */
public interface Codec<S, T> {
	static <S, T> Codec<S, T> of(@NonNull Coder<S, T> encoder, @NonNull Coder<T, S> decoder) {
		return new CompositeCodec<>(encoder, decoder);
	}

	/**
	 * 返回同类型恒等编解码器单例。
	 * <p>
	 * 该编解码器的编码和解码操作均直接返回输入值本身， 即 {@code encode(x) == x} 且 {@code decode(x) == x}。
	 *
	 * <p>
	 * 恒等编解码器在链式组合中充当中性元素：
	 * 
	 * <pre>{@code
	 * codec.from(Codec.identity()) == codec
	 * codec.to(Codec.identity()) == codec
	 * }</pre>
	 *
	 * @param <E> 数据类型
	 * @return 恒等编解码器单例，不可为 null
	 */
	@SuppressWarnings("unchecked")
	static <E> UnaryCodec<E> identity() {
		return (UnaryCodec<E>) IdentityCodec.INSTANCE;
	}

	/**
	 * 将目标类型数据解码为源类型数据。
	 * <p>
	 * 默认实现委托给 {@link #decoder()} 返回的 {@link Coder} 执行转换。
	 *
	 * @param target 待解码的数据（编码后类型）
	 * @return 解码后的数据（源类型）
	 */
	default S decode(T target) {
		return decoder().apply(target);
	}

	/**
	 * 获取解码器（负责 T → S 方向的转换）。
	 * <p>
	 * 返回的 {@link Coder} 实例用于执行解码操作， 可通过 {@link Coder#apply(Object)} 直接调用， 也可通过
	 * {@link Coder#check(Object, Object)} 进行结果校验。
	 *
	 * @return 解码器实例，不可为 null
	 */
	Coder<T, S> decoder();

	/**
	 * 将源类型数据编码为目标类型数据。
	 * <p>
	 * 默认实现委托给 {@link #encoder()} 返回的 {@link Coder} 执行转换。
	 *
	 * @param source 待编码的数据（源类型）
	 * @return 编码后的数据（目标类型）
	 */
	default T encode(S source) {
		return encoder().apply(source);
	}

	/**
	 * 获取编码器（负责 S → T 方向的转换）。
	 * <p>
	 * 返回的 {@link Coder} 实例用于执行编码操作， 可通过 {@link Coder#apply(Object)} 直接调用， 也可通过
	 * {@link Coder#check(Object, Object)} 进行结果校验。
	 *
	 * @return 编码器实例，不可为 null
	 */
	Coder<S, T> encoder();

	/**
	 * 组合前置编解码器，形成新的编解码器（装饰器模式）。
	 * <p>
	 * 新编解码器的转换流程为：
	 *
	 * <pre>
	 * 编码：R → [前置编解码器编码] → S → [当前编解码器编码] → T
	 * 解码：T → [当前编解码器解码] → S → [前置编解码器解码] → R
	 * </pre>
	 *
	 * <p>
	 * 底层实现委托给 {@link Coder#from(Coder)} 串联编码器和解码器。
	 *
	 * @param <R>  前置编解码器的源类型
	 * @param from 前置编解码器，不可为 null
	 * @return 组合后的新编解码器
	 */
	default <R> Codec<R, T> from(@NonNull Codec<R, S> from) {
		return new Codec<R, T>() {

			@Override
			public Coder<T, R> decoder() {
				return from.decoder().from(Codec.this.decoder());
			}

			@Override
			public Coder<R, T> encoder() {
				return Codec.this.encoder().from(from.encoder());
			}
		};
	}

	/**
	 * 获取编解码方向相反的新编解码器。
	 * <p>
	 * 原编解码器的编码操作将变为新编解码器的解码操作，反之亦然：
	 *
	 * <pre>
	 * 原编解码器：encode(S) → T，decode(T) → S
	 * 反转后：    encode(T) → S，decode(S) → T
	 * </pre>
	 *
	 * <p>
	 * 调用两次 {@code reversed()} 将返回原始编解码器：
	 *
	 * <pre>{@code
	 * reversed().reversed() == this
	 * }</pre>
	 *
	 * @return 反向编解码器实例
	 */
	default Codec<T, S> reversed() {
		return new Codec<T, S>() {
			@Override
			public Coder<S, T> decoder() {
				return Codec.this.encoder();
			}

			@Override
			public Coder<T, S> encoder() {
				return Codec.this.decoder();
			}

			@Override
			public Codec<S, T> reversed() {
				return Codec.this;
			}
		};
	}

	/**
	 * 组合后置编解码器，形成新的编解码器（装饰器模式）。
	 * <p>
	 * 新编解码器的转换流程为：
	 *
	 * <pre>
	 * 编码：S → [当前编解码器编码] → T → [后置编解码器编码] → R
	 * 解码：R → [后置编解码器解码] → T → [当前编解码器解码] → S
	 * </pre>
	 *
	 * <p>
	 * 底层实现委托给 {@link Coder#to(Coder)} 串联编码器和解码器。
	 *
	 * @param <R> 后置编解码器的目标类型
	 * @param to  后置编解码器，不可为 null
	 * @return 组合后的新编解码器
	 */
	default <R> Codec<S, R> to(@NonNull Codec<T, R> to) {
		return new Codec<S, R>() {
			@Override
			public Coder<R, S> decoder() {
				return to.decoder().to(Codec.this.decoder());
			}

			@Override
			public Coder<S, R> encoder() {
				return Codec.this.encoder().to(to.encoder());
			}
		};
	}

	/**
	 * 将当前编解码器与映射编解码器组合，转换为同类型的 {@link UnaryCodec}（S↔S 闭环双向编解码器）。
	 * <p>
	 * 组合后的 {@code UnaryCodec} 编码和解码流程均为 S → S 的闭环：
	 *
	 * <pre>
	 * 编码：S → [当前编码器] → T → [mapper 编码器] → S
	 * 解码：S → [mapper 解码器] → T → [当前解码器] → S
	 * </pre>
	 *
	 * <p>
	 * 典型用途是传入 {@code this.reversed()} 构建往返操作， 或传入任意 {@code Codec<T, S>} 实现自定义闭环映射：
	 *
	 * <pre>{@code
	 * // 往返：编码后立即解码回来
	 * UnaryCodec<S> roundTrip = codec.toUnary(codec.reversed());
	 *
	 * // 自定义映射：编码后通过不同的 mapper 转回
	 * UnaryCodec<S> custom = codec.toUnary(anotherCodec);
	 * }</pre>
	 *
	 * @param mapper 映射编解码器（负责 T → S 方向的转换），不可为 null
	 * @return 组合后的同类型双向编解码器
	 */
	default UnaryCodec<S> toUnary(@NonNull Codec<T, S> mapper) {
		return new UnaryCodec<S>() {

			@Override
			public UnaryCoder<S> encoder() {
				return Codec.this.encoder().toUnary(mapper.encoder());
			}

			@Override
			public UnaryCoder<S> decoder() {
				return mapper.decoder().toUnary(Codec.this.decoder());
			}
		};
	}
}