package run.soeasy.framework.codec.crypto;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;

import javax.crypto.Cipher;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.security.AlgorithmFactory;
import run.soeasy.framework.codec.security.ConfigurableAlgorithmFactory;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * Cipher工厂类，继承自{@link ConfigurableAlgorithmFactory}，专门用于创建和配置{@link Cipher}实例，
 * 整合了Cipher的操作模式（加密/解密）与初始化逻辑，通过{@link CipherInitializer}完成Cipher的参数配置（如密钥、模式等），
 * 简化基于Cipher的加密/解密算法实例化流程。
 * 
 * <p>该类支持多种初始化方式，可直接通过{@link CipherInitializer}配置，或结合{@link KeyFactory}动态生成密钥并初始化，
 * 适配对称加密、非对称加密等各类基于Cipher的加解密场景。
 * 
 * @author soeasy.run
 * @see ConfigurableAlgorithmFactory
 * @see Cipher
 * @see CipherInitializer
 */
@Getter
public class CipherFactory extends ConfigurableAlgorithmFactory<Cipher> {

    /**
     * Cipher操作模式（如{@link Cipher#ENCRYPT_MODE}加密模式、{@link Cipher#DECRYPT_MODE}解密模式）
     */
    private final int opmode;

    /**
     * 构造Cipher工厂（指定操作模式、基础工厂和初始化器）
     * 
     * <p>通过{@link CipherInitializer}在指定操作模式下初始化Cipher实例，适用于已有成熟初始化逻辑的场景。
     * 
     * @param opmode Cipher操作模式（加密/解密）
     * @param algorithmFactory 基础Cipher算法工厂（提供原始Cipher实例）
     * @param cipherInitializer Cipher初始化器（负责在指定模式下初始化Cipher，如设置密钥、参数等）
     */
    public CipherFactory(int opmode, @NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            @NonNull CipherInitializer cipherInitializer) {
        super(algorithmFactory, (e) -> cipherInitializer.init(e, opmode));
        this.opmode = opmode;
    }

    /**
     * 构造Cipher工厂（结合KeyFactory生成密钥并初始化）
     * 
     * <p>通过KeyFactory动态生成密钥（Key），并使用{@link UsingKeyFactoryCipherInitializer}完成Cipher初始化，
     * 适用于需要动态生成密钥的场景（如从密钥材料生成密钥）。
     * 
     * @param <K> 密钥类型（继承{@link Key}）
     * @param <F> KeyFactory类型（继承{@link KeyFactory}，用于生成密钥）
     * @param <E> 密钥生成过程可能抛出的异常类型
     * @param opmode Cipher操作模式（加密/解密）
     * @param algorithmFactory 基础Cipher算法工厂
     * @param keyFactorySpi 从Cipher获取KeyFactory的函数（用于指定密钥生成算法）
     * @param keySpi 从KeyFactory生成密钥的函数（核心密钥生成逻辑）
     * @param cipherKeyInitializer 使用生成的密钥初始化Cipher的处理器
     */
    public <K extends Key, F extends KeyFactory, E extends GeneralSecurityException> CipherFactory(int opmode,
            @NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            @NonNull ThrowingFunction<? super Cipher, ? extends F, ? extends E> keyFactorySpi,
            @NonNull ThrowingFunction<? super F, ? extends K, ? extends E> keySpi,
            @NonNull CipherKeyInitializer<? super K> cipherKeyInitializer) {
        this(opmode, algorithmFactory,
                new UsingKeyFactoryCipherInitializer<K, F, E>(keyFactorySpi, keySpi, cipherKeyInitializer));
    }

    /**
     * 构造Cipher工厂（基于转换模式和密钥生成逻辑）
     * 
     * <p>通过转换字符串（如"AES/CBC/PKCS5Padding"）创建Cipher，自动从Cipher算法中提取KeyFactory信息，
     * 适用于标准算法转换模式下的Cipher初始化，简化算法名称与KeyFactory的关联逻辑。
     * 
     * @param <K> 密钥类型（继承{@link Key}）
     * @param <E> 密钥生成过程可能抛出的异常类型
     * @param opmode Cipher操作模式（加密/解密）
     * @param transformation 加密转换模式（格式："算法/模式/填充"，如"AES/CBC/PKCS5Padding"）
     * @param keySpi 从KeyFactory生成密钥的函数
     * @param cipherKeyInitializer 使用生成的密钥初始化Cipher的处理器
     */
    public <K extends Key, E extends GeneralSecurityException> CipherFactory(int opmode, @NonNull String transformation,
            @NonNull ThrowingFunction<? super KeyFactory, ? extends K, ? extends E> keySpi,
            @NonNull CipherKeyInitializer<? super K> cipherKeyInitializer) {
        this(opmode, () -> Cipher.getInstance(transformation),
                // 从Cipher算法中提取基础算法名称作为KeyFactory的算法（如从"AES/CBC..."中提取"AES"）
                (e) -> KeyFactory.getInstance(StringUtils.split(e.getAlgorithm(), "/").first().toString()), 
                keySpi,
                cipherKeyInitializer);
    }
}