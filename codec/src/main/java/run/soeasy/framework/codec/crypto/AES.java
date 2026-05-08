package run.soeasy.framework.codec.crypto;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.IvParameterSpec;

import lombok.NonNull;

/**
 * AES（Advanced Encryption Standard，高级加密标准）对称加密算法实现类，继承自{@link CipherCodec}，
 * 专门用于基于AES算法的加密与解密操作，封装了AES的初始化逻辑，支持多种工作模式（如CBC、GCM）和填充方式，
 * 适用于对安全性要求较高的对称加密场景（如数据存储加密、敏感信息传输等）。
 * 
 * <p>AES为块加密算法，块大小固定为16字节，支持密钥长度为128位（16字节）、192位（24字节）、256位（32字节），
 * 安全性远高于DES，是目前应用最广泛的对称加密算法之一。
 * 
 * @author soeasy.run
 * @see CipherCodec
 * @see WorkMode
 * @see FillStyle
 * @see javax.crypto.Cipher
 */
public class AES extends CipherCodec {

    /**
     * AES算法名称常量，对应标准加密算法中的"AES"
     */
    public static final String ALGORITHM = "AES";

    /**
     * 构造AES加密解码器（默认CBC模式+PKCS5填充，指定密钥和IV向量）
     * 
     * @param secreKey 密钥字节数组（支持16字节[128位]、24字节[192位]、32字节[256位]）
     * @param ivKey IV向量字节数组（CBC模式下必须为16字节，与AES块大小一致；若为null，适用于无需IV的模式如ECB）
     */
    public AES(@NonNull byte[] secreKey, byte[] ivKey) {
        this(FillStyle.PKCS5_PADDING, secreKey, ivKey);
    }

    /**
     * 构造AES加密解码器（指定填充方式、密钥和IV向量）
     * 
     * @param fillStyle 填充方式（如{@link FillStyle#PKCS5_PADDING}、{@link FillStyle#NO_PADDING}）
     * @param secreKey 密钥字节数组（16/24/32字节）
     * @param ivKey IV向量字节数组（16字节，CBC等模式必需；null则不设置IV）
     */
    public AES(FillStyleCapable fillStyle, @NonNull byte[] secreKey, byte[] ivKey) {
        this(fillStyle, secreKey, ivKey == null ? null : new IvParameterSpec(ivKey));
    }

    /**
     * 构造AES加密解码器（指定填充方式、密钥和算法参数）
     * 
     * @param fillStyle 填充方式
     * @param secreKey 密钥字节数组（16/24/32字节）
     * @param algorithmParameterSpec 算法参数规范（如CBC模式的{@link IvParameterSpec}、GCM模式的{@link javax.crypto.spec.GCMParameterSpec}）
     */
    public AES(FillStyleCapable fillStyle, @NonNull byte[] secreKey, AlgorithmParameterSpec algorithmParameterSpec) {
        this(WorkMode.CBC, fillStyle, secreKey, algorithmParameterSpec);
    }

    /**
     * 构造AES加密解码器（指定工作模式、填充方式、密钥和算法参数）
     * 
     * @param workMode 工作模式（如{@link WorkMode#CBC}、{@link WorkMode#GCM}等，推荐使用CBC或GCM）
     * @param fillStyle 填充方式（块模式下需填充，流模式如GCM可无需填充）
     * @param secretKey 密钥字节数组（16/24/32字节，必须与AES密钥长度匹配）
     * @param algorithmParameterSpec 算法参数规范（根据工作模式确定，如CBC需IV，GCM需IV和认证标签长度）
     */
    public AES(WorkModeCapable workMode, FillStyleCapable fillStyle, @NonNull byte[] secretKey,
            AlgorithmParameterSpec algorithmParameterSpec) {
        super(ALGORITHM, workMode, fillStyle, secretKey, algorithmParameterSpec);
    }
}