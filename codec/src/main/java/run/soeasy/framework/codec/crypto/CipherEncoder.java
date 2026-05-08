package run.soeasy.framework.codec.crypto;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;

import lombok.NonNull;
import run.soeasy.framework.codec.security.AlgorithmFactory;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 基于Cipher的加密编码器，继承自{@link CipherTransferrer}，专注于实现加密功能，
 * 封装了Cipher在加密模式（{@link Cipher#ENCRYPT_MODE}）下的初始化与数据处理逻辑，
 * 适用于各类对称加密、非对称加密场景（如AES、RSA等算法的加密操作）。
 * 
 * <p>该类提供多种构造方式，支持直接通过密钥、密钥规范（KeySpec）或自定义初始化器配置加密算法，
 * 简化加密编码器的实例化流程，同时继承{@link CipherTransferrer}的分块/流式处理能力。
 * 
 * @author soeasy.run
 * @see CipherTransferrer
 * @see Cipher
 */
public class CipherEncoder extends CipherTransferrer {

    /**
     * 构造加密编码器（基于Cipher算法工厂和初始化器）
     * 
     * <p>通过自定义{@link CipherInitializer}在加密模式下初始化Cipher，适用于复杂的加密配置场景。
     * 
     * @param algorithmFactory Cipher算法工厂（提供原始Cipher实例）
     * @param cipherInitializer Cipher初始化器（负责在加密模式下配置Cipher，如设置密钥、参数等）
     */
    public CipherEncoder(@NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            CipherInitializer cipherInitializer) {
        super(new CipherFactory(Cipher.ENCRYPT_MODE, algorithmFactory, cipherInitializer));
    }

    /**
     * 构造加密编码器（基于Cipher算法工厂和密钥）
     * 
     * <p>直接使用指定密钥初始化加密模式的Cipher，适用于已有现成密钥的场景（如预生成的对称密钥）。
     * 
     * @param <F> KeyFactory类型（未直接使用，用于泛型兼容）
     * @param <E> 初始化过程可能抛出的异常类型
     * @param algorithmFactory Cipher算法工厂
     * @param key 加密使用的密钥（如SecretKey、PublicKey等）
     */
    public <F extends KeyFactory, E extends GeneralSecurityException> CipherEncoder(
            @NonNull AlgorithmFactory<? extends Cipher> algorithmFactory, @NonNull Key key) {
        this(algorithmFactory, (c, o) -> c.init(o, key));
    }

    /**
     * 构造加密编码器（基于密钥规范生成密钥）
     * 
     * <p>通过KeyFactory和密钥规范（KeySpec）动态生成密钥，并用于初始化加密模式的Cipher，
     * 适用于需要从密钥材料（如字节数组、密钥文件）生成密钥的场景。
     * 
     * @param <F> KeyFactory类型（用于生成密钥）
     * @param <E> 密钥生成和初始化过程可能抛出的异常类型
     * @param algorithmFactory Cipher算法工厂
     * @param keyFactorySpi 从Cipher获取KeyFactory的函数（指定密钥生成算法）
     * @param privateKeySpec 密钥规范（包含密钥材料，如PKCS8EncodedKeySpec）
     * @param cipherKeyInitializer 使用生成的密钥初始化Cipher的处理器
     */
    public <F extends KeyFactory, E extends GeneralSecurityException> CipherEncoder(
            @NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            @NonNull ThrowingFunction<? super Cipher, ? extends F, ? extends E> keyFactorySpi,
            @NonNull KeySpec privateKeySpec, @NonNull CipherKeyInitializer<? super Key> cipherKeyInitializer) {
        super(new CipherFactory(Cipher.ENCRYPT_MODE, algorithmFactory, keyFactorySpi,
                (e) -> e.generatePrivate(privateKeySpec), cipherKeyInitializer));
    }

    /**
     * 构造加密编码器（基于转换模式和密钥规范）
     * 
     * <p>通过转换字符串（如"AES/CBC/PKCS5Padding"）创建Cipher，结合密钥规范生成密钥，
     * 适用于标准加密算法的快速初始化，自动关联Cipher算法与KeyFactory。
     * 
     * @param transformation 加密转换模式（格式："算法/模式/填充"）
     * @param privateKeySpec 密钥规范（用于生成加密密钥）
     * @param cipherKeyInitializer 使用生成的密钥初始化Cipher的处理器
     */
    public CipherEncoder(String transformation, @NonNull KeySpec privateKeySpec,
            @NonNull CipherKeyInitializer<? super Key> cipherKeyInitializer) {
        this(() -> Cipher.getInstance(transformation), 
             (e) -> KeyFactory.getInstance(e.getAlgorithm()), 
             privateKeySpec,
             cipherKeyInitializer);
    }
}