package run.soeasy.framework.codec.crypto;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.IvParameterSpec;

/**
 * DES对称加密算法实现类，继承自{@link CipherCodec}，专门用于基于DES（Data Encryption Standard）算法的加密与解密操作，
 * 封装了DES算法的初始化逻辑，支持多种工作模式（如CBC）和填充方式（如PKCS5Padding），适配对称加密场景。
 * 
 * <p>DES算法为块加密算法，块大小为8字节，密钥长度固定为8字节（64位，含1位奇偶校验位，实际有效密钥长度为56位），
 * 安全性较低，适用于对兼容性要求较高但安全性要求不高的场景，建议优先使用AES等更安全的算法。
 * 
 * @author soeasy.run
 * @see CipherCodec
 * @see WorkMode
 * @see FillStyle
 * @see javax.crypto.Cipher
 */
public class DES extends CipherCodec {

    /**
     * DES算法名称常量，对应标准加密算法中的"DES"
     */
    public static final String ALGORITHM = "DES";

    /**
     * 构造DES加密解码器（默认CBC模式+PKCS5填充，指定密钥和IV向量）
     * 
     * @param secreKey 密钥字节数组（必须为8字节，DES算法固定密钥长度）
     * @param ivKey IV向量字节数组（CBC模式下需8字节，与块大小一致）
     */
    public DES(byte[] secreKey, byte[] ivKey) {
        this(FillStyle.PKCS5_PADDING, secreKey, ivKey);
    }

    /**
     * 构造DES加密解码器（指定填充方式、密钥和IV向量）
     * 
     * @param fillStyle 填充方式（如{@link FillStyle#PKCS5_PADDING}）
     * @param secreKey 密钥字节数组（8字节）
     * @param ivKey IV向量字节数组（8字节，CBC模式必需）
     */
    public DES(FillStyleCapable fillStyle, byte[] secreKey, byte[] ivKey) {
        this(fillStyle, secreKey, new IvParameterSpec(ivKey));
    }

    /**
     * 构造DES加密解码器（指定填充方式、密钥和算法参数）
     * 
     * @param fillStyle 填充方式
     * @param secreKey 密钥字节数组（8字节）
     * @param algorithmParameterSpec 算法参数规范（如CBC模式的{@link IvParameterSpec}）
     */
    public DES(FillStyleCapable fillStyle, byte[] secreKey, AlgorithmParameterSpec algorithmParameterSpec) {
        this(WorkMode.CBC, fillStyle, secreKey, algorithmParameterSpec);
    }

    /**
     * 构造DES加密解码器（指定工作模式、填充方式、密钥和算法参数）
     * 
     * @param workMode 工作模式（如{@link WorkMode#CBC}、{@link WorkMode#ECB}等）
     * @param fillStyle 填充方式（如{@link FillStyle#PKCS5_PADDING}）
     * @param secretKey 密钥字节数组（8字节，必需）
     * @param algorithmParameterSpec 算法参数规范（如CBC模式的IV向量，ECB模式可省略）
     */
    public DES(WorkModeCapable workMode, FillStyleCapable fillStyle, byte[] secretKey,
            AlgorithmParameterSpec algorithmParameterSpec) {
        super(ALGORITHM, workMode, fillStyle, secretKey, algorithmParameterSpec);
    }
}