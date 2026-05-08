package run.soeasy.framework.codec.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.NonNull;
import run.soeasy.framework.codec.security.AlgorithmEncoder;
import run.soeasy.framework.codec.security.AlgorithmFactory;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;

/**
 * 消息认证码（MAC）编码器，继承自{@link AlgorithmEncoder}，基于Java安全框架的{@link Mac}实现消息认证码的计算，
 * 支持HmacMD5、HmacSHA1、HmacSHA256等常见HMAC算法，用于验证数据的完整性和真实性。
 * 
 * <p>MAC算法结合密钥和消息数据生成固定长度的认证码，只有持有相同密钥的接收方才能验证认证码的有效性，
 * 适用于接口签名、数据传输完整性校验等场景。
 * 
 * @author soeasy.run
 * @see AlgorithmEncoder
 * @see Mac
 */
public class MAC extends AlgorithmEncoder<Mac> {

    /**
     * 构造MAC编码器（基于算法名称和密钥字节数组）
     * 
     * @param algorithm MAC算法名称（如"HmacMD5"、"HmacSHA256"）
     * @param key 密钥字节数组（用于初始化MAC算法）
     */
    public MAC(@NonNull String algorithm, @NonNull byte[] key) {
        this(algorithm, key, null);
    }

    /**
     * 构造MAC编码器（基于算法名称、密钥字节数组和参数规范）
     * 
     * @param algorithm MAC算法名称
     * @param key 密钥字节数组
     * @param algorithmParameterSpec 算法参数规范（可选，多数HMAC算法不需要）
     */
    public MAC(@NonNull String algorithm, @NonNull byte[] key, AlgorithmParameterSpec algorithmParameterSpec) {
        this(new SecretKeySpec(key, algorithm), algorithmParameterSpec);
    }

    /**
     * 构造MAC编码器（基于密钥对象）
     * 
     * @param key 密钥对象（如{@link SecretKeySpec}，需与MAC算法匹配）
     */
    public MAC(@NonNull Key key) {
        this(key, (AlgorithmParameterSpec) null);
    }

    /**
     * 构造MAC编码器（基于密钥对象和参数规范）
     * 
     * @param key 密钥对象
     * @param algorithmParameterSpec 算法参数规范（可选）
     */
    public MAC(@NonNull Key key, AlgorithmParameterSpec algorithmParameterSpec) {
        this(() -> Mac.getInstance(key.getAlgorithm()), (e) -> e.init(key, algorithmParameterSpec));
    }

    /**
     * 构造MAC编码器（基于算法工厂和初始化器）
     * 
     * <p>通过自定义的{@link AlgorithmFactory}创建Mac实例，并使用初始化器配置，适用于复杂的MAC算法配置场景。
     * 
     * @param encodeAlgorithmFactory Mac算法工厂（提供Mac实例）
     * @param encodeAlgorithmInitializer Mac初始化器（配置密钥、参数等）
     */
    public MAC(AlgorithmFactory<? extends Mac> encodeAlgorithmFactory,
            @NonNull ThrowingConsumer<? super Mac, ? extends GeneralSecurityException> encodeAlgorithmInitializer) {
        super(encodeAlgorithmFactory, encodeAlgorithmInitializer);
    }

    /**
     * 执行MAC计算并传输结果（核心实现）
     * 
     * <p>处理流程：
     * 1. 通过{@link IOUtils}将输入流数据传输到Mac实例（更新待计算数据）
     * 2. 调用{@link Mac#doFinal()}计算最终的消息认证码
     * 3. 将认证码通过{@link BufferConsumer}输出
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param algorithm Mac实例（已初始化，用于计算认证码）
     * @param source 待计算MAC的输入流数据
     * @param bufferSize 读取缓冲区大小
     * @param target 接收MAC结果的缓冲区消费者
     * @throws IOException 当输入流读取失败时抛出
     * @throws E 当消费者处理数据时抛出
     * @throws GeneralSecurityException 当Mac算法执行失败时抛出（如密钥不匹配、算法不支持等）
     */
    @Override
    public <E extends Throwable> void transferTo(Mac algorithm, @NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target)
            throws IOException, E, GeneralSecurityException {
        IOUtils.transferTo(source, bufferSize, algorithm::update);
        byte[] response = algorithm.doFinal();
        target.accept(response, 0, response.length);
    }

}