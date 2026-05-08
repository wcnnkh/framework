package run.soeasy.framework.codec.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;

/**
 * 消息摘要编码器，继承自{@link AlgorithmEncoder}，基于Java安全框架的{@link MessageDigest}实现消息摘要（哈希）计算，
 * 支持MD5、SHA-1等常见哈希算法，可用于数据完整性校验、密码加密等场景。
 * 
 * <p>该类提供了预置的MD5和SHA-1编码器实例，支持设置密钥（用于HMAC-like场景），通过流式处理计算数据的消息摘要。
 * 
 * @author soeasy.run
 * @see AlgorithmEncoder
 * @see MessageDigest
 */
@Getter
@Setter
public class MessageDigestEncoder extends AlgorithmEncoder<MessageDigest> {
    /**
     * MD5算法名称常量
     */
    public static final String MD5_ALGORITHM_NAME = "MD5";
    
    /**
     * 预置的MD5消息摘要编码器实例
     */
    public static final AlgorithmEncoder<MessageDigest> MD5 = new MessageDigestEncoder(MD5_ALGORITHM_NAME);
    
    /**
     * SHA-1算法名称常量
     */
    public static final String SHA1_ALGORITHM_NAME = "SHA-1";
    
    /**
     * 预置的SHA-1消息摘要编码器实例
     */
    public static final AlgorithmEncoder<MessageDigest> SHA1 = new MessageDigestEncoder(SHA1_ALGORITHM_NAME);

    /**
     * 摘要计算的密钥（可选），用于在计算摘要前先更新密钥数据
     */
    private byte[] secretKey;

    /**
     * 构造消息摘要编码器（基于指定的算法工厂）
     * 
     * @param encodeAlgorithmFactory 消息摘要算法工厂，用于创建{@link MessageDigest}实例
     */
    public MessageDigestEncoder(@NonNull AlgorithmFactory<? extends MessageDigest> encodeAlgorithmFactory) {
        super(encodeAlgorithmFactory);
    }

    /**
     * 构造消息摘要编码器（基于算法名称）
     * 
     * @param algorithm 消息摘要算法名称（如"MD5"、"SHA-256"等）
     */
    public MessageDigestEncoder(String algorithm) {
        this(() -> MessageDigest.getInstance(algorithm));
    }

    /**
     * 执行消息摘要计算并传输结果（核心实现）
     * 
     * <p>处理流程：
     * 1. 若设置了{@link #secretKey}，先将密钥数据更新到消息摘要算法中
     * 2. 通过{@link IOUtils}将输入流数据传输到消息摘要算法进行处理
     * 3. 计算最终摘要并通过{@link BufferConsumer}输出结果
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param algorithm 消息摘要算法实例（{@link MessageDigest}）
     * @param source 待计算摘要的输入流
     * @param bufferSize 读取缓冲区大小
     * @param target 接收摘要结果的缓冲区消费者
     * @throws IOException 当输入流读取失败时抛出
     * @throws E 当消费者处理数据时抛出
     * @throws GeneralSecurityException 当消息摘要算法执行失败时抛出
     */
    @Override
    public <E extends Throwable> void transferTo(MessageDigest algorithm, @NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target)
            throws IOException, E, GeneralSecurityException {
        if (secretKey != null) {
            algorithm.update(secretKey);
        }
        IOUtils.transferTo(source, bufferSize, algorithm::update);
        byte[] data = algorithm.digest();
        target.accept(data, 0, data.length);
    }
}