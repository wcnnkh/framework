package run.soeasy.framework.codec.crypto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 加密算法填充方式枚举，定义块加密算法中数据块的填充方式，
 * 实现{@link FillStyleCapable}接口以统一提供填充方式名称，适配各类需要数据块填充的加密场景。
 * 
 * <p>块加密算法要求输入数据长度为固定块大小的整数倍，当数据长度不足时，需通过填充方式补齐，
 * 不同填充方式适用于不同的加密需求和安全性要求。
 * 
 * @author soeasy.run
 * @see FillStyleCapable
 * @see javax.crypto.Cipher
 */
@RequiredArgsConstructor
@Getter
public enum FillStyle implements FillStyleCapable {

    /**
     * PKCS#5填充方式：每个填充字节的值等于需要填充的字节数，
     * 例如需要填充3个字节时，填充内容为0x03 0x03 0x03，
     * 是应用广泛的标准填充方式，适用于大多数块加密场景。
     */
    PKCS5_PADDING("PKCS5Padding"),

    /**
     * 无填充方式：不进行任何填充，要求输入数据长度必须是块大小的整数倍，
     * 适用于数据长度已知且固定的场景，或配合自定义填充逻辑使用。
     */
    NO_PADDING("NoPadding");

    /**
     * 填充方式名称，与加密算法中使用的填充字符串一致（如Cipher初始化时的"算法/模式/填充"中的填充部分）
     */
    private final String fillStyleName;
}