package run.soeasy.framework.commons.unit;

import run.soeasy.framework.math.NumberUnit;
import java.math.BigDecimal;

/**
 * 面积单位体系（SI 衍生单位：平方米/m²），支持纯比例转换，适用于面积测量场景。
 * <p>
 * 核心逻辑：面积 = 长度 × 长度 → 基数 = 对应长度单位基数的平方。
 *
 * @author soeasy.run
 * @see NumberUnit 数值单位核心接口
 * @see LengthUnit 长度单位体系（面积单位的基础）
 */
public enum AreaUnit implements NumberUnit {

    SQUARE_METER("m²", new BigDecimal("1.0")),               // 平方米（基本单位）
    SQUARE_CENTIMETER("cm²", new BigDecimal("0.0001")),       // 平方厘米（(0.01m)² = 0.0001m²）
    SQUARE_KILOMETER("km²", new BigDecimal("1e6")),           // 平方千米（(1000m)² = 1e6m²）
    SQUARE_INCH("in²", new BigDecimal("0.00064516")),         // 平方英寸（(0.0254m)² ≈ 0.00064516m²）
    HECTARE("ha", new BigDecimal("10000.0"));                 // 公顷（1ha = 10000m²）

    private final String symbol;
    private final BigDecimal radix;

    AreaUnit(String symbol, BigDecimal radix) {
        this.symbol = symbol;
        this.radix = radix;
    }

    public static AreaUnit of(String symbol) {
        if (symbol == null) return null;
        for (AreaUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol) || 
                (symbol.equalsIgnoreCase("sqm") && unit == SQUARE_METER)) { // 兼容 "sqm"
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