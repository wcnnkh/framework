package run.soeasy.framework.commons.unit;

import run.soeasy.framework.math.NumberUnit;
import java.math.BigDecimal;

/**
 * 时间单位体系（SI 基本单位：秒/s），支持纯比例转换，适用于时间测量场景。
 * <p>
 * 核心约定：
 * - 基本单位：秒（s），基数=1.0，偏移=0；
 * - 分钟、小时、天为工程常用衍生单位，基数基于秒推导（如 1分钟=60秒）。
 *
 * @author soeasy.run
 * @see NumberUnit 数值单位核心接口
 */
public enum TimeUnit implements NumberUnit {

    SECOND("s", new BigDecimal("1.0")),                  // 秒（基本单位）
    MILLISECOND("ms", new BigDecimal("0.001")),          // 毫秒（1ms = 0.001s）
    MINUTE("min", new BigDecimal("60.0")),               // 分钟（1min = 60s）
    HOUR("h", new BigDecimal("3600.0")),                 // 小时（1h = 3600s）
    DAY("d", new BigDecimal("86400.0"));                 // 天（1d = 86400s，按24小时计）

    private final String symbol;
    private final BigDecimal radix;

    TimeUnit(String symbol, BigDecimal radix) {
        this.symbol = symbol;
        this.radix = radix;
    }

    public static TimeUnit of(String symbol) {
        if (symbol == null) return null;
        for (TimeUnit unit : values()) {
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