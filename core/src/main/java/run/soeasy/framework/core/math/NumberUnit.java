package run.soeasy.framework.core.math;

import java.math.BigDecimal;

/**
 * 数字单位接口，表示具有名称和进制基数的计量单位。
 * 该接口定义了获取单位名称和进制基数的方法，用于构建数字单位体系，
 * 支持不同进制单位间的转换和计算。
 *
 * <p>典型应用场景包括：
 * <ul>
 *   <li>数据存储单位（如Byte、KB、MB、GB等）</li>
 *   <li>货币单位（如元、角、分等）</li>
 *   <li>物理单位（如米、厘米、毫米等）</li>
 *   <li>时间单位（如秒、毫秒、微秒等）</li>
 * </ul>
 *
 * <p>实现要求：
 * <ul>
 *   <li>单位名称应返回该单位的标准名称（如"KB"、"元"）</li>
 *   <li>进制基数应表示该单位相对于基本单位的转换系数</li>
 *   <li>基本单位的进制基数应为1（如Byte的基数为1）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.math.BigDecimal
 */
public interface NumberUnit {

    /**
     * 获取单位名称
     * 
     * @return 单位的标准名称，如"KB"、"元"等，不可为null
     */
    String getName();

    /**
     * 获取单位的进制基数
     * 
     * @return 该单位相对于基本单位的转换系数，使用BigDecimal确保精度，不可为null
     */
    BigDecimal getRadix();
}