package run.soeasy.framework.commons.unit;

import run.soeasy.framework.math.NumberUnit;
import java.math.BigDecimal;

/**
 * 质量单位体系（SI 基本单位：千克/kg），支持纯比例转换，适用于重量、质量测量场景。
 * <p>
 * 核心约定：
 * - 基本单位：千克（kg），基数=1.0，偏移=0；
 * - 注意："克（g）" 是衍生单位（基数=0.001），SI 基本单位为千克（而非克）。
 *
 * @author soeasy.run
 * @see NumberUnit 数值单位核心接口
 */
public enum MassUnit implements NumberUnit {

    KILOGRAM("kg", new BigDecimal("1.0")),              // 千克（基本单位）
    GRAM("g", new BigDecimal("0.001")),                 // 克（1g = 0.001kg）
    MILLIGRAM("mg", new BigDecimal("1e-6")),             // 毫克（1mg = 1e-6kg）
    TON("t", new BigDecimal("1000.0")),                  // 吨（1t = 1000kg）
    POUND("lb", new BigDecimal("0.45359237"));           // 磅（1lb = 0.45359237kg，国际 avoirdupois 磅）

    private final String symbol;
    private final BigDecimal radix;

    MassUnit(String symbol, BigDecimal radix) {
        this.symbol = symbol;
        this.radix = radix;
    }

    public static MassUnit of(String symbol) {
        if (symbol == null) return null;
        for (MassUnit unit : values()) {
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