package run.soeasy.framework.codec.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.io.BinaryEncoder;
import run.soeasy.framework.core.comparator.Comparison;
import run.soeasy.framework.core.function.ThrowingConsumer;

/**
 * 基于安全算法的签名器抽象类，继承自{@link AlgorithmCoder}，整合了签名生成（编码）与签名验证功能，
 * 适用于通过安全算法（如数字签名算法）实现数据签名及验证的场景。
 * 
 * <p>
 * 该类通过两个算法工厂分别管理签名生成算法（继承自{@link AlgorithmCoder}）和签名验证算法， 提供统一的签名生成（编码）与验证接口。
 * 
 * @param <T> 签名算法类型，如{@link java.security.Signature}等支持签名与验证的算法对象
 * @author soeasy.run
 * @see AlgorithmCoder
 * @see AlgorithmFactory
 */
@Getter
public abstract class AlgorithmEncoder<T> extends AlgorithmCoder<T> implements BinaryEncoder {

	/**
	 * 用于签名验证的算法工厂，负责创建和初始化验证签名所需的算法实例
	 */
	@NonNull
	private final AlgorithmFactory<? extends T> verifyAlgorithmFactory;

	/**
	 * 构造签名器（指定签名生成和验证的算法工厂）
	 * 
	 * @param encodeAlgorithmFactory 签名生成算法工厂（用于生成签名，继承自编码逻辑）
	 * @param verifyAlgorithmFactory 签名验证算法工厂（用于验证签名有效性）
	 */
	public AlgorithmEncoder(@NonNull AlgorithmFactory<? extends T> encodeAlgorithmFactory,
			@NonNull AlgorithmFactory<? extends T> verifyAlgorithmFactory) {
		super(encodeAlgorithmFactory);
		this.verifyAlgorithmFactory = verifyAlgorithmFactory;
	}

	/**
	 * 构造签名器（支持对签名生成和验证算法进行初始化配置）
	 * 
	 * @param encodeAlgorithmFactory     签名生成算法工厂
	 * @param encodeAlgorithmInitializer 签名生成算法初始化器（如设置私钥、签名参数等）
	 * @param verifyAlgorithmFactory     签名验证算法工厂
	 * @param verifyAlgorithmInitializer 签名验证算法初始化器（如设置公钥、验证参数等）
	 */
	public AlgorithmEncoder(@NonNull AlgorithmFactory<? extends T> encodeAlgorithmFactory,
			@NonNull ThrowingConsumer<? super T, ? extends GeneralSecurityException> encodeAlgorithmInitializer,
			@NonNull AlgorithmFactory<? extends T> verifyAlgorithmFactory,
			@NonNull ThrowingConsumer<? super T, ? extends GeneralSecurityException> verifyAlgorithmInitializer) {
		this(new ConfigurableAlgorithmFactory<>(encodeAlgorithmFactory, encodeAlgorithmInitializer),
				new ConfigurableAlgorithmFactory<>(verifyAlgorithmFactory, verifyAlgorithmInitializer));
	}

	@Override
	public @NonNull Comparison evaluate(@NonNull InputStream left, @NonNull InputStream right) throws IOException {
		try {
			T algorithm = this.verifyAlgorithmFactory.getAlgorithm();
			return evaluate(algorithm, left, right);
		} catch (GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	public abstract Comparison evaluate(T algorithm, @NonNull InputStream reader, @NonNull InputStream verified)
			throws IOException, GeneralSecurityException;
}