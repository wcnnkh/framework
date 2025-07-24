package run.soeasy.framework.codec.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BinaryTransferrer;
import run.soeasy.framework.io.BufferConsumer;

/**
 * 算法传输器抽象类，实现{@link BinaryTransferrer}接口，基于算法工厂{@link AlgorithmFactory}提供的算法实例，
 * 处理与安全相关的二进制数据传输（如加密、解密等），并将安全相关异常{@link GeneralSecurityException}转换为{@link CodecException}。
 * 
 * <p>该类作为安全算法与二进制传输的桥梁，通过算法工厂管理算法实例的创建与配置，
 * 子类需实现具体的算法传输逻辑（如加密传输、解密传输）。
 * 
 * @param <T> 算法类型（如Cipher、MessageDigest等安全算法对象）
 * @author soeasy.run
 * @see BinaryTransferrer
 * @see AlgorithmFactory
 * @see CodecException
 */
@Getter
@RequiredArgsConstructor
public abstract class AlgorithmTransferrer<T> implements BinaryTransferrer {

    /**
     * 算法工厂，用于创建和配置算法实例（如初始化加密/解密算法）
     */
    @NonNull
    private final AlgorithmFactory<? extends T> algorithmFactory;

    /**
     * 构造算法传输器，支持通过初始化器配置算法实例
     * 
     * @param algorithmFactory 基础算法工厂，提供原始算法实例
     * @param algorithmInitializer 算法初始化器，用于对算法实例进行额外配置（如设置密钥、模式等）
     */
    public AlgorithmTransferrer(AlgorithmFactory<? extends T> algorithmFactory,
            @NonNull ThrowingConsumer<? super T, ? extends GeneralSecurityException> algorithmInitializer) {
        this(new ConfigurableAlgorithmFactory<>(algorithmFactory, algorithmInitializer));
    }

    /**
     * 重写二进制传输方法，通过算法工厂获取算法实例并执行传输，处理安全异常转换
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param source 输入流数据源
     * @param bufferSize 传输缓冲区大小
     * @param target 缓冲区消费者（接收处理后的数据）
     * @throws IOException 当I/O操作失败时抛出
     * @throws CodecException 当安全算法处理失败时抛出（包装GeneralSecurityException）
     * @throws E 当消费者处理数据时抛出
     */
    @Override
    public <E extends Throwable> void transferTo(@NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, CodecException, E {
        try {
            T algorithm = this.algorithmFactory.getAlgorithm();
            transferTo(algorithm, source, bufferSize, target);
        } catch (GeneralSecurityException e) {
            throw new CodecException(e);
        }
    }

    /**
     * 抽象方法，由子类实现具体的算法传输逻辑
     * 
     * @param <E> 消费者可能抛出的异常类型
     * @param algorithm 算法实例（如Cipher对象）
     * @param source 输入流数据源
     * @param bufferSize 传输缓冲区大小
     * @param target 缓冲区消费者（接收处理后的数据）
     * @throws IOException 当I/O操作失败时抛出
     * @throws E 当消费者处理数据时抛出
     * @throws GeneralSecurityException 当算法处理失败时抛出（如加密/解密错误）
     */
    public abstract <E extends Throwable> void transferTo(T algorithm, @NonNull InputStream source, int bufferSize,
            @NonNull BufferConsumer<? super byte[], ? extends E> target)
            throws IOException, E, GeneralSecurityException;
}