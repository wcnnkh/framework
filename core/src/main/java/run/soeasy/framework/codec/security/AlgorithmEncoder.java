package run.soeasy.framework.codec.security;

import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.binary.BinaryEncoder;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 基于安全算法的二进制编码器抽象类，继承自{@link AlgorithmTransferrer}并实现{@link BinaryEncoder}，
 * 将安全算法（如加密算法）与二进制编码功能结合，提供通过算法对二进制数据进行编码（如加密）的能力。
 * 
 * <p>该类作为算法传输器与二进制编码器的整合，通过{@link AlgorithmFactory}管理编码算法实例的创建与初始化，
 * 并将自身作为编码传输器（{@link #getEncodeTransferrer()}），实现编码过程的流式传输。
 * 子类需实现具体算法的编码传输逻辑（如加密数据的流式处理）。
 * 
 * @param <T> 编码算法类型，如{@link javax.crypto.Cipher}（加密算法）等安全算法对象
 * @author soeasy.run
 * @see AlgorithmTransferrer
 * @see BinaryEncoder
 * @see AlgorithmFactory
 */
@Getter
public abstract class AlgorithmEncoder<T> extends AlgorithmTransferrer<T> implements BinaryEncoder {

    /**
     * 构造基于指定编码算法工厂的编码器
     * 
     * @param encodeAlgorithmFactory 编码算法工厂，用于创建编码所需的算法实例
     */
    public AlgorithmEncoder(@NonNull AlgorithmFactory<? extends T> encodeAlgorithmFactory) {
        super(encodeAlgorithmFactory);
    }

    /**
     * 构造基于编码算法工厂和初始化器的编码器
     * 
     * @param encodeAlgorithmFactory 编码算法工厂，提供原始算法实例
     * @param encodeAlgorithmInitializer 编码算法初始化器，用于配置算法实例（如设置加密密钥、模式等）
     */
    public AlgorithmEncoder(AlgorithmFactory<? extends T> encodeAlgorithmFactory,
            @NonNull ThrowingConsumer<? super T, ? extends GeneralSecurityException> encodeAlgorithmInitializer) {
        super(encodeAlgorithmFactory, encodeAlgorithmInitializer);
    }

    /**
     * 获取编码用的二进制传输器（当前实例自身）
     * 
     * @return 当前编码器实例，作为编码过程的传输器
     */
    @Override
    public BinaryTransferrer getEncodeTransferrer() {
        return this;
    }
}