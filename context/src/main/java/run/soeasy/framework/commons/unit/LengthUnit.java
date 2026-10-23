package run.soeasy.framework.commons.unit;

import java.math.BigDecimal;

import run.soeasy.framework.math.NumberUnit;

/**
 * 长度单位体系（SI 基本单位：米/m），支持纯比例转换，适用于距离、长度测量场景。
 * <p>
 * 核心约定：
 * - 基本单位：米（m），基数=1.0，偏移=0；
 * - 所有单位偏移量均为 0（纯比例转换）；
 * - 基数 = 单位实际值 / 基本单位值（如 1cm = 0.01m → 基数=0.01）。
 *
 * @author soeasy.run
 * @see NumberUnit 数值单位核心接口
 */
public enum LengthUnit implements NumberUnit {

    METER("m", new BigDecimal("1.0")),                  // 米（基本单位）
    MILLIMETER("mm", new BigDecimal("0.001")),          // 毫米（1mm = 0.001m）
    CENTIMETER("cm", new BigDecimal("0.01")),           // 厘米（1cm = 0.01m）
    DECIMETER("dm", new BigDecimal("0.1")),             // 分米（1dm = 0.1m）
    KILOMETER("km", new BigDecimal("1000.0")),          // 千米（1km = 1000m）
    INCH("in", new BigDecimal("0.0254")),               // 英寸（1in = 0.0254m，国际标准）
    FOOT("ft", new BigDecimal("0.3048"));               // 英尺（1ft = 0.3048m，国际标准）

    private final String symbol;   // 国际标准符号
    private final BigDecimal radix; // 相对于基本单位（米）的基数

    LengthUnit(String symbol, BigDecimal radix) {
        this.symbol = symbol;
        this.radix = radix;
    }

    /**
     * 快速获取长度单位（通过符号匹配，忽略大小写）
     * @param symbol 单位符号（如 "m"、"CM"）
     * @return 对应的 LengthUnit，无匹配返回 null
     */
    public static LengthUnit of(String symbol) {
        if (symbol == null) return null;
        for (LengthUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        return null;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public BigDecimal getRadix() {
        return radix;
    }
}