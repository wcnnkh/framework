package run.soeasy.framework.codec.crypto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 加密算法工作模式枚举，定义块加密算法（如AES、DES、SM4等）支持的工作模式，
 * 实现{@link WorkModeCapable}接口以统一提供工作模式名称，适配各类基于块加密的算法场景。
 * 
 * <p>工作模式决定了块加密算法如何处理数据块之间的关联关系，不同模式具有不同的安全性、性能特性和使用场景：
 * <ul>
 * <li>安全性：部分模式提供认证能力，可检测数据篡改</li>
 * <li>并行性：影响加密/解密过程的并行处理能力</li>
 * <li>初始化要求：是否需要初始化向量(IV)或其他参数</li>
 * <li>错误传播：一个块的错误对后续数据的影响范围</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see WorkModeCapable 工作模式能力接口
 * @see javax.crypto.Cipher JDK加密算法实现类
 */
@RequiredArgsConstructor
@Getter
public enum WorkMode implements WorkModeCapable {

    /**
     * CBC模式：Cipher Block Chaining（密码块链接模式）
     * 
     * <p>特点：
     * <ul>
     * <li>每个明文块加密前与前一个密文块进行异或运算</li>
     * <li>需要初始化向量(IV)，IV应随机且不可预测</li>
     * <li>相同明文在不同IV下产生不同密文，安全性高于ECB</li>
     * <li>加密过程不支持并行处理，解密过程支持并行处理</li>
     * <li>一个块错误会影响后续所有块的解密结果</li>
     * </ul>
     * <p>适用场景：通用加密场景，如文件加密、数据传输等
     */
    CBC("CBC"),

    /**
     * ECB模式：Electronic Codebook（电子密码本模式）
     * 
     * <p>特点：
     * <ul>
     * <li>每个数据块独立加密，相同明文块生成相同密文块</li>
     * <li>不需要初始化向量(IV)</li>
     * <li>加密和解密过程都支持并行处理</li>
     * <li>安全性较低，不推荐用于敏感数据加密</li>
     * <li>一个块错误仅影响该块的解密结果</li>
     * </ul>
     * <p>适用场景：加密短数据、测试场景，不建议用于生产环境
     */
    ECB("ECB"),
	
	/**
	 * GCM模式：Galois/Counter Mode（伽罗瓦/计数器模式）
	 * 
	 * <p>特点：
	 * <ul>
	 * <li>基于计数器的认证加密模式，同时提供加密和消息认证能力</li>
	 * <li>需要初始化向量(IV)，推荐长度为12字节</li>
	 * <li>支持并行加密和解密操作，性能优异</li>
	 * <li>可以检测数据完整性和真实性，防止篡改</li>
	 * <li>支持附加数据(AD)的认证，不加密但参与认证</li>
	 * </ul>
	 * <p>适用场景：需要同时保证机密性和完整性的场景，如网络通信、存储加密等
	 */
	GCM("GCM");

    /**
     * 工作模式名称，与JCE规范中的模式字符串完全一致，
     * 用于{@link javax.crypto.Cipher}初始化时的"算法/模式/填充"参数中的模式部分
     */
    private final String workModeName;
}
    