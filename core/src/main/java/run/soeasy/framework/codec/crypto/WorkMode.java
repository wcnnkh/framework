package run.soeasy.framework.codec.crypto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 加密算法工作模式枚举，定义加密算法（如AES、DES等）支持的工作模式，
 * 实现{@link WorkModeCapable}接口以统一提供工作模式名称，适配各类基于块加密的算法场景。
 * 
 * <p>工作模式决定了块加密算法如何处理数据块之间的关联，不同模式具有不同的安全性和性能特性，
 * 包含CBC和ECB等常见模式，可根据需求扩展其他模式（如CTR、GCM等）。
 * 
 * @author soeasy.run
 * @see WorkModeCapable
 * @see javax.crypto.Cipher
 */
@RequiredArgsConstructor
@Getter
public enum WorkMode implements WorkModeCapable {

    /**
     * CBC模式：Cipher Block Chaining（密码块链接模式），
     * 每个明文块在加密前会与前一个密文块进行异或运算，需使用初始化向量（IV），
     * 安全性高于ECB，是常用的块加密工作模式之一。
     */
    CBC("CBC"),

    /**
     * ECB模式：Electronic Codebook（电子密码本模式），
     * 每个数据块独立加密，相同的明文块会生成相同的密文块，安全性较低，
     * 适用于对安全性要求不高的场景或作为其他模式的基础参考。
     */
    ECB("ECB");

    /**
     * 工作模式名称，与加密算法中使用的模式字符串一致（如Cipher初始化时的"算法/模式/填充"中的模式部分）
     */
    private final String workModeName;
}