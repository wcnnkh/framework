package run.soeasy.framework.codec.crypto;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;

import lombok.NonNull;
import run.soeasy.framework.codec.binary.BinaryDecoder;
import run.soeasy.framework.codec.security.AlgorithmFactory;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 基于Cipher的解密解码器，继承自{@link CipherTransferrer}并实现{@link BinaryDecoder}，
 * 专注于实现解密功能，封装了Cipher在解密模式（{@link Cipher#DECRYPT_MODE}）下的初始化与数据处理逻辑，
 * 适用于各类对称解密、非对称解密场景（如AES、RSA等算法的解密操作）。
 * 
 * <p>该类提供多种构造方式，支持直接通过密钥、密钥规范（KeySpec）或自定义初始化器配置解密算法，
 * 简化解密解码器的实例化流程，同时继承{@link CipherTransferrer}的分块/流式处理能力，适配大文件或流式数据的解密。
 * 
 * @author soeasy.run
 * @see CipherTransferrer
 * @see BinaryDecoder
 * @see Cipher
 */
public class CipherDecoder extends CipherTransferrer implements BinaryDecoder {

    /**
     * 构造解密解码器（基于Cipher算法工厂和初始化器）
     * 
     * <p>通过自定义{@link CipherInitializer}在解密模式下初始化Cipher，适用于复杂的解密配置场景（如需要特定参数的解密算法）。
     * 
     * @param algorithmFactory Cipher算法工厂（提供原始Cipher实例）
     * @param cipherInitializer Cipher初始化器（负责在解密模式下配置Cipher，如设置密钥、参数等）
     */
    public CipherDecoder(@NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            CipherInitializer cipherInitializer) {
        super(new CipherFactory(Cipher.DECRYPT_MODE, algorithmFactory, cipherInitializer));
    }

    /**
     * 构造解密解码器（基于Cipher算法工厂和密钥）
     * 
     * <p>直接使用指定密钥初始化解密模式的Cipher，适用于已有现成密钥的场景（如预获取的对称密钥、公钥等）。
     * 
     * @param <F> KeyFactory类型（未直接使用，用于泛型兼容）
     * @param <E> 初始化过程可能抛出的异常类型
     * @param algorithmFactory Cipher算法工厂
     * @param key 解密使用的密钥（如SecretKey、PublicKey等）
     */
    public <F extends KeyFactory, E extends GeneralSecurityException> CipherDecoder(
            @NonNull AlgorithmFactory<? extends Cipher> algorithmFactory, @NonNull Key key) {
        this(algorithmFactory, (c, o) -> c.init(o, key));
    }

    /**
     * 构造解密解码器（基于密钥规范生成密钥）
     * 
     * <p>通过KeyFactory和公钥规范（KeySpec）动态生成密钥，并用于初始化解密模式的Cipher，
     * 适用于需要从密钥材料（如字节数组、证书提取的公钥规范）生成密钥的场景（如RSA公钥解密）。
     * 
     * @param <F> KeyFactory类型（用于生成公钥）
     * @param <E> 密钥生成和初始化过程可能抛出的异常类型
     * @param algorithmFactory Cipher算法工厂
     * @param keyFactorySpi 从Cipher获取KeyFactory的函数（指定密钥生成算法）
     * @param publicKeySpec 公钥规范（包含密钥材料，如X509EncodedKeySpec）
     * @param cipherKeyInitializer 使用生成的密钥初始化Cipher的处理器
     */
    public <F extends KeyFactory, E extends GeneralSecurityException> CipherDecoder(
            @NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            @NonNull ThrowingFunction<? super Cipher, ? extends F, ? extends E> keyFactorySpi,
            @NonNull KeySpec publicKeySpec, @NonNull CipherKeyInitializer<? super Key> cipherKeyInitializer) {
        super(new CipherFactory(Cipher.DECRYPT_MODE, algorithmFactory, keyFactorySpi,
                (e) -> e.generatePublic(publicKeySpec), cipherKeyInitializer));
    }

    /**
     * 构造解密解码器（基于转换模式和密钥规范）
     * 
     * <p>通过转换字符串（如"AES/CBC/PKCS5Padding"）创建Cipher，自动从Cipher算法中提取KeyFactory信息，
     * 适用于标准算法转换模式下的解密初始化，简化算法名称与KeyFactory的关联逻辑。
     * 
     * @param transformation 解密转换模式（格式："算法/模式/填充"，如"AES/CBC/PKCS5Padding"）
     * @param publicKeySpec 公钥规范（用于生成解密密钥）
     * @param cipherKeyInitializer 使用生成的密钥初始化Cipher的处理器
     */
    public CipherDecoder(String transformation, @NonNull KeySpec publicKeySpec,
            @NonNull CipherKeyInitializer<? super Key> cipherKeyInitializer) {
        this(() -> Cipher.getInstance(transformation), 
             (e) -> KeyFactory.getInstance(e.getAlgorithm()), 
             publicKeySpec,
             cipherKeyInitializer);
    }

    /**
     * 获取解密用的二进制传输器（当前实例自身）
     * 
     * @return 当前{@link CipherDecoder}实例，作为解密过程的流式传输器
     */
    @Override
    public final BinaryTransferrer getDecodeTransferrer() {
        return this;
    }
}