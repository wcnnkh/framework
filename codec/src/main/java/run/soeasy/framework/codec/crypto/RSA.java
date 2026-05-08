package run.soeasy.framework.codec.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA非对称加密算法实现类，继承自{@link CipherCodec}，专门用于处理RSA加密解密操作，
 * 封装了RSA算法的块大小限制处理，支持通过私钥/公钥或密钥字节数组初始化，适配非对称加密场景。
 * 
 * <p>RSA算法对单次加密的明文长度有严格限制，具体为：
 * 明文最大长度（字节）= 密钥长度（bit）/8 - 11（如1024bit密钥支持最大117字节明文，2048bit支持245字节）。
 * 该类通过{@link #setMaxBlock(int)}方法设置块大小，确保加密解密过程符合长度限制。
 * 
 * @author soeasy.run
 * @see CipherCodec
 * @see java.security.spec.PKCS8EncodedKeySpec
 * @see java.security.spec.X509EncodedKeySpec
 */
public class RSA extends CipherCodec {

    /**
     * RSA算法名称常量，对应标准加密算法中的"RSA"
     */
    public static final String RSA = "RSA";

    /**
     * 构造RSA加密解码器（基于私钥和公钥）
     * 
     * @param maxBlock 最大处理块大小（字节，需根据密钥长度计算，如1024bit密钥对应128字节）
     * @param privateKey 私钥（用于加密或解密，取决于使用场景，通常私钥用于解密）
     * @param publicKey 公钥（用于加密或解密，通常公钥用于加密）
     */
    public RSA(int maxBlock, PrivateKey privateKey, PublicKey publicKey) {
        super(RSA, privateKey, publicKey);
        setMaxBlock(maxBlock);
    }

    /**
     * 构造RSA加密解码器（基于字节数组形式的密钥）
     * 
     * <p>私钥字节数组将转换为{@link PKCS8EncodedKeySpec}，公钥字节数组转换为{@link X509EncodedKeySpec}。
     * 
     * @param maxBlock 最大处理块大小（字节）
     * @param privateKey 私钥字节数组（PKCS8格式）
     * @param publicKey 公钥字节数组（X509格式）
     */
    public RSA(int maxBlock, byte[] privateKey, byte[] publicKey) {
        super(RSA, 
              privateKey == null ? null : new PKCS8EncodedKeySpec(privateKey), 
              publicKey == null ? null : new X509EncodedKeySpec(publicKey));
        setMaxBlock(maxBlock);
    }

    /**
     * 设置RSA加密解密的块大小（核心配置）
     * 
     * <p>RSA算法明文长度限制公式：<br>
     * 加密最大明文长度（字节）= 密钥长度(bit)/8 - 11（如1024bit密钥：1024/8 -11 = 117字节）<br>
     * 解密块大小为最大块大小（通常等于密钥长度/8，如1024bit密钥对应128字节）
     * 
     * @param maxBlock 最大块大小（通常为密钥长度/8，单位：字节）
     */
    public void setMaxBlock(int maxBlock) {
        // 加密块大小 = 最大块大小 - 11（预留RSA加密的填充空间）
        getEncodeTransferrer().setChunkSize(maxBlock - 11);
        // 解密块大小 = 最大块大小（密文长度通常等于密钥长度/8）
        getDecodeTransferrer().setChunkSize(maxBlock);
    }
}