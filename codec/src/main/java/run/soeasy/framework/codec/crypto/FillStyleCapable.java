package run.soeasy.framework.codec.crypto;

/**
 * 具备填充方式能力的接口，定义获取填充方式名称的规范，
 * 适用于块加密算法等需要处理数据块填充的组件，用于标识和获取当前使用的填充方式（如PKCS#5填充、无填充等）。
 * 
 * <p>块加密算法（如AES、DES）处理的数据需为固定长度的块，当数据长度不足时需通过填充方式补齐，
 * 该接口提供统一的填充方式名称获取方式，便于加密组件间的适配与配置。
 * 
 * @author soeasy.run
 */
public interface FillStyleCapable {

    /**
     * 获取当前的填充方式名称
     * 
     * @return 填充方式名称（如加密算法中使用的"PKCS5Padding"、"NoPadding"等标准填充方式名称）
     */
    String getFillStyleName();
}