package run.soeasy.framework.codec.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;

/**
 * 数字签名编码器，继承自{@link AlgorithmSigner}，基于Java安全框架的{@link Signature}实现数字签名的生成与验证，
 * 支持各类标准签名算法（如SHA1WithRSA），适用于需要确保数据完整性和身份认证的场景（如接口签名、文件签名等）。
 * 
 * <p>该类通过私钥初始化签名生成算法，通过公钥或证书初始化签名验证算法，实现签名生成（编码）与验证的一体化流程。
 * 
 * @author soeasy.run
 * @see AlgorithmSigner
 * @see Signature
 */
public class SignatureEncoder extends AlgorithmSigner<Signature> {
    /**
     * 常用签名算法：SHA1哈希算法结合RSA加密算法（"SHA1WithRSA"）
     */
    public static final String SHA1_WITH_RSA = "SHA1WithRSA";

    /**
     * 构造签名编码器（分别指定签名生成和验证的算法工厂及初始化器）
     * 
     * @param encodeAlgorithmFactory 签名生成算法工厂（提供{@link Signature}实例）
     * @param encodeAlgorithmInitializer 签名生成初始化器（如设置私钥：{@link Signature#initSign(PrivateKey)}）
     * @param verifyAlgorithmFactory 签名验证算法工厂（提供{@link Signature}实例）
     * @param verifyAlgorithmInitializer 签名验证初始化器（如设置公钥：{@link Signature#initVerify(PublicKey)}）
     */
    public SignatureEncoder(@NonNull AlgorithmFactory<? extends Signature> encodeAlgorithmFactory,
            @NonNull ThrowingConsumer<? super Signature, ? extends GeneralSecurityException> encodeAlgorithmInitializer,
            @NonNull AlgorithmFactory<? extends Signature> verifyAlgorithmFactory,
            @NonNull ThrowingConsumer<? super Signature, ? extends GeneralSecurityException> verifyAlgorithmInitializer) {
        super(encodeAlgorithmFactory, encodeAlgorithmInitializer, verifyAlgorithmFactory, verifyAlgorithmInitializer);
    }

    /**
     * 构造签名编码器（使用同一算法工厂，分别指定签名生成和验证的初始化器）
     * 
     * @param algorithmFactory 签名算法工厂（同时用于生成和验证的{@link Signature}实例）
     * @param encodeAlgorithmInitializer 签名生成初始化器（如私钥初始化）
     * @param verifyAlgorithmInitializer 签名验证初始化器（如公钥初始化）
     */
    public SignatureEncoder(@NonNull AlgorithmFactory<? extends Signature> algorithmFactory,
            @NonNull ThrowingConsumer<? super Signature, ? extends GeneralSecurityException> encodeAlgorithmInitializer,
            @NonNull ThrowingConsumer<? super Signature, ? extends GeneralSecurityException> verifyAlgorithmInitializer) {
        this(algorithmFactory, encodeAlgorithmInitializer, algorithmFactory, verifyAlgorithmInitializer);
    }

    /**
     * 构造签名编码器（基于算法名称、私钥和证书）
     * 
     * <p>签名生成使用私钥初始化，签名验证使用证书中的公钥初始化
     * 
     * @param algorithm 签名算法名称（如{@link #SHA1_WITH_RSA}）
     * @param privateKey 用于签名生成的私钥
     * @param certificate 包含公钥的证书（用于签名验证）
     */
    public SignatureEncoder(@NonNull String algorithm, PrivateKey privateKey, Certificate certificate) {
        this(() -> Signature.getInstance(algorithm), (e) -> e.initSign(privateKey), (e) -> e.initVerify(certificate));
    }

    /**
     * 构造签名编码器（基于算法名称、私钥和公钥）
     * 
     * <p>签名生成使用私钥初始化，签名验证使用公钥初始化
     * 
     * @param algorithm 签名算法名称（如{@link #SHA1_WITH_RSA}）
     * @param privateKey 用于签名生成的私钥
     * @param publicKey 用于签名验证的公钥
     */
    public SignatureEncoder(@NonNull String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this(() -> Signature.getInstance(algorithm), (e) -> e.initSign(privateKey), (e) -> e.initVerify(publicKey));
    }

    /**
     * 验证签名有效性（实现具体验证逻辑）
     * 
     * <p>流程：
     * 1. 通过{@link IOUtils}将输入流数据传输到签名算法（更新待验证数据）
     * 2. 调用{@link Signature#verify(byte[])}验证签名是否匹配
     * 
     * @param algorithm 签名验证算法实例（已通过公钥初始化）
     * @param source 原始数据输入流（待验证签名对应的数据）
     * @param bufferSize 读取缓冲区大小
     * @param target 待验证的签名数据
     * @return 签名验证通过返回true，否则返回false
     * @throws CodecException 编解码过程中发生错误时抛出
     * @throws IOException 输入流读取失败时抛出
     * @throws GeneralSecurityException 签名验证算法执行失败时抛出（如签名无效、算法错误等）
     */
    @Override
    public boolean test(Signature algorithm, @NonNull InputStream source, int bufferSize, byte[] target)
            throws CodecException, IOException, GeneralSecurityException {
        IOUtils.transferTo(source, bufferSize, algorithm::update);
        return algorithm.verify(target);
    }

    /**
     * 生成数字签名（实现具体签名逻辑）
     * 
     * <p>流程：
     * 1. 通过{@link IOUtils}将输入流数据传输到签名算法（更新待签名数据）
     * 2. 调用{@link Signature#sign()}生成签名字节数组
     * 3. 将签名数据传递给缓冲区消费者
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param algorithm 签名生成算法实例（已通过私钥初始化）
     * @param source 待签名的原始数据输入流
     * @param bufferSize 读取缓冲区大小
     * @param target 接收签名数据的缓冲区消费者
     * @throws IOException 输入流读取失败时抛出
     * @throws E 消费者处理签名数据时抛出
     * @throws GeneralSecurityException 签名生成算法执行失败时抛出（如私钥错误、算法不支持等）
     */
    @Override
    public <E extends Throwable> void transferTo(Signature algorithm, @NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target)
            throws IOException, E, GeneralSecurityException {
        IOUtils.transferTo(source, bufferSize, algorithm::update);
        byte[] data = algorithm.sign();
        target.accept(data, 0, data.length);
    }
}