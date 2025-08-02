package run.soeasy.framework.codec.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.core.function.ThrowingConsumer;

/**
 * 基于安全算法的签名器抽象类，继承自{@link AlgorithmEncoder}，整合了签名生成（编码）与签名验证功能，
 * 适用于通过安全算法（如数字签名算法）实现数据签名及验证的场景。
 * 
 * <p>
 * 该类通过两个算法工厂分别管理签名生成算法（继承自{@link AlgorithmEncoder}）和签名验证算法，
 * 提供统一的签名生成（编码）与验证接口，将安全异常转换为{@link CodecException}以适配编解码框架。
 * 
 * @param <T> 签名算法类型，如{@link java.security.Signature}等支持签名与验证的算法对象
 * @author soeasy.run
 * @see AlgorithmEncoder
 * @see AlgorithmFactory
 */
@Getter
public abstract class AlgorithmSigner<T> extends AlgorithmEncoder<T> {

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
	public AlgorithmSigner(@NonNull AlgorithmFactory<? extends T> encodeAlgorithmFactory,
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
	public AlgorithmSigner(@NonNull AlgorithmFactory<? extends T> encodeAlgorithmFactory,
			@NonNull ThrowingConsumer<? super T, ? extends GeneralSecurityException> encodeAlgorithmInitializer,
			@NonNull AlgorithmFactory<? extends T> verifyAlgorithmFactory,
			@NonNull ThrowingConsumer<? super T, ? extends GeneralSecurityException> verifyAlgorithmInitializer) {
		this(new ConfigurableAlgorithmFactory<>(encodeAlgorithmFactory, encodeAlgorithmInitializer),
				new ConfigurableAlgorithmFactory<>(verifyAlgorithmFactory, verifyAlgorithmInitializer));
	}

	/**
	 * 验证签名有效性
	 * 
	 * <p>
	 * 处理逻辑： 1. 通过{@link #verifyAlgorithmFactory}获取签名验证算法实例 2.
	 * 调用抽象方法{@link #test}执行具体的签名验证逻辑 3. 将安全相关异常转换为{@link CodecException}
	 * 
	 * @param source     待验证签名的原始数据输入流
	 * @param bufferSize 读取缓冲区大小
	 * @param target     待验证的签名数据（目标字节数组）
	 * @return 签名验证通过返回true，否则返回false
	 * @throws CodecException 当算法获取或验证过程发生安全异常时抛出
	 * @throws IOException    当输入流读取失败时抛出
	 */
	@Override
	public boolean test(@NonNull InputStream source, int bufferSize, byte[] target) throws CodecException, IOException {
		try {
			T algorithm = this.verifyAlgorithmFactory.getAlgorithm();
			return test(algorithm, source, bufferSize, target);
		} catch (GeneralSecurityException e) {
			throw new CodecException(e);
		}
	}

	/**
	 * 抽象方法：执行具体的签名验证逻辑（由子类实现）
	 * 
	 * @param algorithm  签名验证算法实例
	 * @param source     原始数据输入流
	 * @param bufferSize 读取缓冲区大小
	 * @param target     待验证的签名数据
	 * @return 验证通过返回true，否则返回false
	 * @throws CodecException           验证逻辑中发生编解码错误时抛出
	 * @throws IOException              输入流读取失败时抛出
	 * @throws GeneralSecurityException 算法执行过程中发生安全异常时抛出
	 */
	public abstract boolean test(T algorithm, @NonNull InputStream source, int bufferSize, byte[] target)
			throws CodecException, IOException, GeneralSecurityException;
}