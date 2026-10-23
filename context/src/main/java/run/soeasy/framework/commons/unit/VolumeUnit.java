package run.soeasy.framework.commons.unit;

import run.soeasy.framework.math.NumberUnit;
import java.math.BigDecimal;

/**
 * 体积单位体系（SI 衍生单位：立方米/m³），支持纯比例转换，适用于体积/容积测量场景。
 * <p>
 * 核心逻辑：
 * - 立方类单位：基数 = 对应长度单位基数的立方（如 1cm³ = (0.01m)³ = 1e-6m³）；
 * - 容积类单位：1升（L）= 1立方分米（dm³）= 0.001m³。
 *
 * @author soeasy.run
 * @see NumberUnit 数值单位核心接口
 * @see LengthUnit 长度单位体系（体积单位的基础）
 */
public enum VolumeUnit implements NumberUnit {

    CUBIC_METER("m³", new BigDecimal("1.0")),                // 立方米（基本单位）
    CUBIC_CENTIMETER("cm³", new BigDecimal("1e-6")),          // 立方厘米（(0.01m)³ = 1e-6m³）
    LITER("L", new BigDecimal("0.001")),                     // 升（1L = 1dm³ = 0.001m³）
    MILLILITER("mL", new BigDecimal("1e-6")),                // 毫升（1mL = 1cm³ = 1e-6m³）
    CUBIC_INCH("in³", new BigDecimal("1.6387064e-5"));       // 立方英寸（(0.0254m)³ ≈ 1.6387e-5m³）

    private final String symbol;
    private final BigDecimal radix;

    VolumeUnit(String symbol, BigDecimal radix) {
        this.symbol = symbol;
        this.radix = radix;
    }

    public static VolumeUnit of(String symbol) {
        if (symbol == null) return null;
        for (VolumeUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol) || 
                (symbol.equalsIgnoreCase("cc") && unit == CUBIC_CENTIMETER)) { // 兼容 "cc"
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