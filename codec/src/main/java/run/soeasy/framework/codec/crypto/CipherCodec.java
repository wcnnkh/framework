package run.soeasy.framework.codec.crypto;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.binary.TransferrerCodec;
import run.soeasy.framework.codec.security.AlgorithmFactory;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 基于Cipher的加解密编解码器，继承自{@link TransferrerCodec}，整合了{@link CipherEncoder}（加密）和{@link CipherDecoder}（解密），
 * 支持对称加密和非对称加密的完整流程，提供灵活的构造方式适配不同加密场景（如指定密钥、密钥规范、工作模式和填充方式等）。
 * 
 * <p>该类作为加解密功能的统一入口，通过组合加密器和解密器，实现数据的加密编码与解密解码一体化处理，
 * 适用于AES、RSA等各类基于{@link Cipher}的加密算法。
 * 
 * @author soeasy.run
 * @see TransferrerCodec
 * @see CipherEncoder
 * @see CipherDecoder
 * @see Cipher
 */
@Getter
public class CipherCodec extends TransferrerCodec<CipherTransferrer, CipherTransferrer> {

    /**
     * 构造非对称加解密编解码器（基于私钥加密、公钥解密）
     * 
     * <p>适用于非对称加密场景，使用私钥进行加密，公钥进行解密（如数字签名验证流程的加密环节）。
     * 
     * @param <K> 密钥类型
     * @param <F> KeyFactory类型
     * @param <E> 加密过程可能抛出的异常类型
     * @param transformation 加密转换模式（格式："算法/模式/填充"）
     * @param encodeKey 用于加密的私钥
     * @param decodeKey 用于解密的公钥
     */
    public <K extends Key, F extends KeyFactory, E extends GeneralSecurityException> CipherCodec(@NonNull String transformation,
            PrivateKey encodeKey, PublicKey decodeKey) {
        this(transformation, (a, b) -> a.init(b, encodeKey), (a, b) -> a.init(b, decodeKey));
    }

    /**
     * 构造非对称加解密编解码器（基于密钥规范生成密钥）
     * 
     * <p>通过私钥规范生成加密密钥，公钥规范生成解密密钥，适用于从密钥材料（如字节数组）构建非对称加密器的场景。
     * 
     * @param <K> 密钥类型
     * @param <F> KeyFactory类型
     * @param <E> 密钥生成过程可能抛出的异常类型
     * @param transformation 加密转换模式
     * @param privateKeySpec 用于生成加密密钥的私钥规范（如PKCS8EncodedKeySpec）
     * @param publicKeySpec 用于生成解密密钥的公钥规范（如X509EncodedKeySpec）
     */
    public <K extends Key, F extends KeyFactory, E extends GeneralSecurityException> CipherCodec(@NonNull String transformation,
            KeySpec privateKeySpec, KeySpec publicKeySpec) {
        super(
            privateKeySpec == null ? null : new CipherEncoder(transformation, privateKeySpec, (a, b, c) -> a.init(b, c)),
            publicKeySpec == null ? null : new CipherDecoder(transformation, publicKeySpec, (a, b, c) -> a.init(b, c))
        );
    }

    /**
     * 构造加解密编解码器（基于转换模式和初始化器）
     * 
     * <p>通过自定义加密和解密初始化器，灵活配置加密和解密过程，适用于需要分别定制加密和解密逻辑的场景。
     * 
     * @param transformation 加密转换模式
     * @param encryptCipherInitializer 加密初始化器（配置加密模式的Cipher）
     * @param decryptCipherInitializer 解密初始化器（配置解密模式的Cipher）
     */
    public CipherCodec(@NonNull String transformation, CipherInitializer encryptCipherInitializer,
            CipherInitializer decryptCipherInitializer) {
        this(() -> Cipher.getInstance(transformation), encryptCipherInitializer, decryptCipherInitializer);
    }

    /**
     * 构造加解密编解码器（基于算法工厂和初始化器）
     * 
     * <p>通过基础Cipher算法工厂和初始化器，分别创建加密器和解密器，适用于需要复用Cipher实例创建逻辑的场景。
     * 
     * @param algorithmFactory Cipher算法工厂（提供原始Cipher实例）
     * @param encodeCipherInitializer 加密初始化器
     * @param decodeCipherInitializer 解密初始化器
     */
    public CipherCodec(@NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            CipherInitializer encodeCipherInitializer, CipherInitializer decodeCipherInitializer) {
        super(
            encodeCipherInitializer == null ? null : new CipherEncoder(algorithmFactory, encodeCipherInitializer),
            decodeCipherInitializer == null ? null : new CipherDecoder(algorithmFactory, decodeCipherInitializer)
        );
    }

    /**
     * 构造对称加解密编解码器（加密和解密使用相同初始化器）
     * 
     * <p>适用于对称加密场景（如AES），加密和解密使用相同的密钥和参数配置，简化对称加密的实例化流程。
     * 
     * @param algorithmFactory Cipher算法工厂
     * @param cipherInitializer 加解密共用的初始化器（同时用于加密模式和解密模式）
     */
    public CipherCodec(@NonNull AlgorithmFactory<? extends Cipher> algorithmFactory,
            CipherInitializer cipherInitializer) {
        this(algorithmFactory, cipherInitializer, cipherInitializer);
    }

    /**
     * 构造对称加解密编解码器（指定算法、工作模式、填充方式和Cipher创建逻辑）
     * 
     * <p>通过算法名称、工作模式和填充方式动态生成转换模式，支持自定义Cipher创建逻辑，适用于需要灵活控制Cipher实例化的场景。
     * 
     * @param algorithm 加密算法名称（如"AES"、"DES"）
     * @param workModeCapable 工作模式（如{@link WorkMode#CBC}）
     * @param fillStyleCapable 填充方式（如{@link FillStyle#PKCS5_PADDING}）
     * @param cipherSpi 自定义Cipher创建函数（用于从转换模式生成Cipher实例）
     * @param cipherInitializer 加解密共用的初始化器
     */
    public CipherCodec(@NonNull String algorithm, @NonNull WorkModeCapable workModeCapable,
            @NonNull FillStyleCapable fillStyleCapable,
            @NonNull ThrowingFunction<String, ? extends Cipher, ? extends GeneralSecurityException> cipherSpi,
            @NonNull CipherInitializer cipherInitializer) {
        this(
            () -> cipherSpi.apply(
                algorithm + "/" + workModeCapable.getWorkModeName() + "/" + fillStyleCapable.getFillStyleName()
            ),
            cipherInitializer
        );
    }

    /**
     * 构造对称加解密编解码器（指定算法、工作模式、填充方式）
     * 
     * <p>使用默认的Cipher创建逻辑（{@link Cipher#getInstance(String)}），适用于标准对称加密算法的快速配置。
     * 
     * @param algorithm 加密算法名称
     * @param workModeCapable 工作模式
     * @param fillStyleCapable 填充方式
     * @param cipherInitializer 加解密共用的初始化器
     */
    public CipherCodec(@NonNull String algorithm, @NonNull WorkModeCapable workModeCapable,
            @NonNull FillStyleCapable fillStyleCapable, @NonNull CipherInitializer cipherInitializer) {
        this(algorithm, workModeCapable, fillStyleCapable, Cipher::getInstance, cipherInitializer);
    }

    /**
     * 构造对称加解密编解码器（基于密钥字节数组和参数规范）
     * 
     * <p>直接使用密钥字节数组创建密钥，并支持指定算法参数（如CBC模式的IV向量），适用于最常见的对称加密场景（如AES-CBC加密）。
     * 
     * @param algorithm 加密算法名称
     * @param workModeCapable 工作模式
     * @param fillStyleCapable 填充方式
     * @param secretKey 密钥字节数组（如AES的128/256位密钥）
     * @param algorithmParameterSpec 算法参数规范（如{@link IvParameterSpec}，CBC模式必需）
     */
    public CipherCodec(@NonNull String algorithm, @NonNull WorkModeCapable workModeCapable,
            @NonNull FillStyleCapable fillStyleCapable, @NonNull byte[] secretKey,
            AlgorithmParameterSpec algorithmParameterSpec) {
        this(algorithm, workModeCapable, fillStyleCapable, (e, opmode) -> {
            Key key = new SecretKeySpec(secretKey, algorithm);
            if (algorithmParameterSpec == null) {
                e.init(opmode, key);
            } else {
                e.init(opmode, key, algorithmParameterSpec);
            }
        });
    }
}