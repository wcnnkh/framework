package run.soeasy.framework.commons.unit;

import run.soeasy.framework.math.NumberUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 速度单位体系（SI 衍生单位：米/秒/m/s），支持纯比例转换，适用于速度测量场景。
 * <p>
 * 核心逻辑：速度 = 长度 / 时间 → 基数 = 长度单位基数 ÷ 时间单位基数。
 * 例：1km/h = 1000m / 3600s → 基数=1000/3600 = 5/18 ≈ 0.2778。
 *
 * @author soeasy.run
 * @see NumberUnit 数值单位核心接口
 * @see LengthUnit 长度单位体系
 * @see TimeUnit 时间单位体系
 */
public enum SpeedUnit implements NumberUnit {

    METER_PER_SECOND("m/s", new BigDecimal("1.0")),          // 米/秒（基本单位）
    KILOMETER_PER_HOUR("km/h", 
            new BigDecimal("1000").divide(new BigDecimal("3600"), 10, RoundingMode.HALF_UP) // 基数=5/18≈0.2778
    ),
    MILE_PER_HOUR("mph", new BigDecimal("0.44704")),          // 英里/小时（1mph = 0.44704m/s）
    KNOT("kn", new BigDecimal("0.514444"));                  // 节（1kn = 1海里/小时 ≈ 0.514444m/s）

    private final String symbol;
    private final BigDecimal radix;

    SpeedUnit(String symbol, BigDecimal radix) {
        this.symbol = symbol;
        this.radix = radix;
    }

    public static SpeedUnit of(String symbol) {
        if (symbol == null) return null;
        for (SpeedUnit unit : values()) {
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