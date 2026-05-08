package run.soeasy.framework.codec.security;

import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingConsumer;

/**
 * 可配置的算法工厂，实现{@link AlgorithmFactory}接口，通过组合基础算法工厂和算法初始化器，
 * 支持在算法实例创建后进行自定义配置（如设置密钥、模式、参数等），适用于需要对算法实例进行额外初始化的场景。
 * 
 * <p>该类将算法的创建与配置分离，基础算法工厂负责实例创建，初始化器负责后续配置，
 * 灵活适配不同算法的个性化配置需求，同时保持算法工厂接口的一致性。
 * 
 * @param <T> 算法类型（如Cipher、Signature等安全算法对象）
 * @author soeasy.run
 * @see AlgorithmFactory
 * @see ThrowingConsumer
 */
@Getter
@RequiredArgsConstructor
public class ConfigurableAlgorithmFactory<T> implements AlgorithmFactory<T> {

    /**
     * 基础算法工厂，负责创建原始的算法实例
     */
    @NonNull
    private final AlgorithmFactory<? extends T> algorithmFactory;

    /**
     * 算法初始化器，用于在算法实例创建后对其进行配置（如设置密钥、参数等）
     */
    @NonNull
    private final ThrowingConsumer<? super T, ? extends GeneralSecurityException> algorithmInitializer;

    /**
     * 创建并配置算法实例
     * 
     * <p>处理流程：
     * 1. 通过基础算法工厂{@link #algorithmFactory}创建原始算法实例
     * 2. 使用初始化器{@link #algorithmInitializer}对实例进行配置
     * 3. 返回配置完成的算法实例
     * 
     * @return 配置后的算法实例
     * @throws GeneralSecurityException 当算法创建失败或初始化配置出错时抛出
     */
    @Override
    public T getAlgorithm() throws GeneralSecurityException {
        T algorithm = algorithmFactory.getAlgorithm();
        algorithmInitializer.accept(algorithm);
        return algorithm;
    }
}