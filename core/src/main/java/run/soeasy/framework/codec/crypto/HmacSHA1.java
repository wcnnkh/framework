package run.soeasy.framework.codec.crypto;

/**
 * HmacSHA1消息认证码实现类，继承自{@link MAC}，专门用于基于HmacSHA1算法计算消息认证码，
 * 封装了HmacSHA1算法的初始化逻辑，简化使用HmacSHA1进行数据完整性和真实性验证的流程。
 * 
 * <p>HmacSHA1算法结合SHA1哈希函数和密钥，生成160位（20字节）的消息认证码，
 * 适用于需要中等安全强度的消息认证场景（如API接口签名、数据传输校验等）。
 * 
 * @author soeasy.run
 * @see MAC
 * @see javax.crypto.Mac
 */
public class HmacSHA1 extends MAC {

    /**
     * HmacSHA1算法名称常量，对应标准加密算法中的"HmacSHA1"
     */
    public static final String ALGORITHM = "HmacSHA1";

    /**
     * 构造HmacSHA1编码器（基于密钥字节数组）
     * 
     * @param secretKey 密钥字节数组（用于初始化HmacSHA1算法，密钥长度推荐不小于160位）
     */
    public HmacSHA1(byte[] secretKey) {
        super(ALGORITHM, secretKey);
    }
}