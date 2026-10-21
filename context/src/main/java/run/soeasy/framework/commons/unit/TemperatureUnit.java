package run.soeasy.framework.commons.unit;

import run.soeasy.framework.math.NumberUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 温度单位体系（基准单位：摄氏度/℃），支持偏移+比例转换，适用于温度测量场景。
 * <p>
 * 核心公式推导（符合 NumberUnit 统一公式）：
 * 目标单位值 = (原始值 × 源基数 + 源偏移 - 目标偏移) ÷ 目标基数
 * <p>
 * 关键参数说明：
 * - 摄氏度（℃）：基本单位，基数=1.0，偏移=0.0；
 * - 开尔文（K）：℃ = K - 273.15 → 基数=1.0，偏移=-273.15；
 * - 华氏度（℉）：℃ = (℉ - 32) × 5/9 → 基数=5/9，偏移=-160/9（≈-17.78）。
 *
 * @author soeasy.run
 * @see NumberUnit 数值单位核心接口
 */
public enum TemperatureUnit implements NumberUnit {

    CELSIUS("℃", new BigDecimal("1.0"), new BigDecimal("0.0")), // 摄氏度（基准单位）
    KELVIN("K", new BigDecimal("1.0"), new BigDecimal("-273.15")), // 开尔文
    FAHRENHEIT("℉",
            new BigDecimal("5").divide(new BigDecimal("9"), 10, RoundingMode.HALF_UP), // 基数=5/9
            new BigDecimal("-160").divide(new BigDecimal("9"), 10, RoundingMode.HALF_UP) // 偏移=-160/9
    );

    private final String symbol;
    private final BigDecimal radix;
    private final BigDecimal offset;

    TemperatureUnit(String symbol, BigDecimal radix, BigDecimal offset) {
        this.symbol = symbol;
        this.radix = radix;
        this.offset = offset;
    }

    public static TemperatureUnit of(String symbol) {
        if (symbol == null) return null;
        for (TemperatureUnit unit : values()) {
            if (unit.symbol.equalsIgnoreCase(symbol) || 
                (symbol.equalsIgnoreCase("F") && unit == FAHRENHEIT)) { // 兼容 "F" 符号
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

    @Override
    public BigDecimal getOffset() {
        return offset;
    }
}