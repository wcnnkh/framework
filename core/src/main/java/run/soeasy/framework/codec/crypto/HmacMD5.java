package run.soeasy.framework.codec.crypto;

/**
 * HmacMD5消息认证码实现类，继承自{@link MAC}，专门用于基于HmacMD5算法计算消息认证码，
 * 封装了HmacMD5算法的初始化逻辑，简化使用HmacMD5进行数据完整性和真实性验证的流程。
 * 
 * <p>HmacMD5算法结合MD5哈希函数和密钥，生成128位（16字节）的消息认证码，
 * 适用于对安全性要求不高但需要快速计算的消息认证场景（如 legacy 系统兼容、简单数据校验等）。
 * 
 * @author soeasy.run
 * @see MAC
 * @see javax.crypto.Mac
 */
public class HmacMD5 extends MAC {

    /**
     * HmacMD5算法名称常量，对应标准加密算法中的"HmacMD5"
     */
    public static final String ALGORITHM = "HmacMD5";

    /**
     * 构造HmacMD5编码器（基于密钥字节数组）
     * 
     * @param secretKey 密钥字节数组（用于初始化HmacMD5算法）
     */
    public HmacMD5(byte[] secretKey) {
        super(ALGORITHM, secretKey);
    }
}