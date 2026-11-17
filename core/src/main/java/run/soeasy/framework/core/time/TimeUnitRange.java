package run.soeasy.framework.core.time;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;

/**
 * 时间单位范围处理器，专注于时间单位的边界（最小值/最大值）计算与层级化管理。
 * <p>
 * 该抽象类继承自{@link TimeDiscrete}，扩展了时间离散化能力，核心价值在于：
 * 为指定时间单位（如天、小时、分钟）提供精准的边界计算（起始/结束时间），并通过{@code nextRangeUnit}
 * 构建时间单位层级结构（如 天→小时→分钟→秒），支持递归式精细时间范围控制。
 * <p>
 * 与JDK标准库{@link java.util.concurrent.TimeUnit}（专注时间单位转换与延时）不同，
 * 本类聚焦“时间单位的范围边界”，适用于需要获取某时间单位起始/结束点的场景（如统计当日数据、小时级缓存）。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>边界计算：获取指定时间在当前单位下的最小值（起始边界，如当日00:00:00）和最大值（结束边界，如当日23:59:59）</li>
 *   <li>层级联动：通过{@code nextRangeUnit}形成嵌套层级，支持递归处理更小单位（如天→小时→分钟的链式边界设置）</li>
 *   <li>日历适配：基于{@link Calendar}实现跨时区、跨历法的边界计算，兼容实际最小/最大值（如2月的天数）</li>
 *   <li>离散扩展：继承{@link TimeDiscrete}的日期格式化能力，支持时间单位与字符串格式的绑定</li>
 * </ul>
 *
 * <p><b>层级结构示例：</b>
 * <pre>
 * // 构建 天→小时→分钟→秒 的层级
 * TimeUnitRange secondRange = new SecondTimeUnitRange("ss", Calendar.SECOND, null);
 * TimeUnitRange minuteRange = new MinuteTimeUnitRange("mm", Calendar.MINUTE, secondRange);
 * TimeUnitRange hourRange = new HourTimeUnitRange("HH", Calendar.HOUR_OF_DAY, minuteRange);
 * TimeUnitRange dayRange = new DayTimeUnitRange("yyyy-MM-dd", Calendar.DAY_OF_MONTH, hourRange);
 *
 * // 获取当前时间的当日边界
 * Date todayStart = dayRange.minValue(new Date()); // 结果：当前日期 00:00:00
 * Date todayEnd = dayRange.maxValue(new Date());   // 结果：当前日期 23:59:59
 * </pre>
 *
 * @author soeasy.run
 * @see TimeDiscrete 时间离散化基础类
 * @see Calendar 日历操作核心依赖
 * @see java.util.concurrent.TimeUnit JDK时间单位类（注意功能差异）
 */
@Getter
public abstract class TimeUnitRange extends TimeDiscrete {
    
    /**
     * 下一个更小的时间单位范围处理器，构成层级结构的核心（如“天”的下一级是“小时”）
     * <p>若为null，表示当前是最小时间单位（无更小层级可递归）
     */
    private final TimeUnitRange nextRangeUnit;

    /**
     * 构造函数，初始化时间单位范围处理器
     * 
     * @param pattern 日期格式模式（与当前时间单位匹配，如“yyyy-MM-dd”对应天单位），不可为null
     * @param calendarField 对应的Calendar字段（如{@link Calendar#DAY_OF_MONTH}对应天单位）
     * @param nextRangeUnit 下一个更小的时间单位范围处理器，可为null（表示无更小单位）
     * @throws NullPointerException 若pattern为null
     */
    public TimeUnitRange(@NonNull String pattern, int calendarField, TimeUnitRange nextRangeUnit) {
        super(pattern, calendarField);
        this.nextRangeUnit = nextRangeUnit;
    }

    /**
     * 获取指定基准日期在当前时间单位下的最小值（起始边界）
     * <p>
     * 示例：
     * <ul>
     *   <li>天单位（DAY_OF_MONTH）：返回基准日期的 00:00:00</li>
     *   <li>小时单位（HOUR_OF_DAY）：返回基准日期当前小时的 00分00秒</li>
     *   <li>分钟单位（MINUTE）：返回基准日期当前分钟的 00秒</li>
     * </ul>
     * 
     * @param baseDate 基准日期（作为计算边界的参照时间），不可为null
     * @return 当前时间单位的起始边界时间
     * @throws NullPointerException 若baseDate为null
     */
    public final Date minValue(@NonNull Date baseDate) {
        Calendar calendar = getCalendar(false);
        calendar.setTimeInMillis(baseDate.getTime());
        setMinBoundary(calendar);
        return calendar.getTime();
    }

    /**
     * 获取指定基准日期在当前时间单位下的最大值（结束边界）
     * <p>
     * 示例：
     * <ul>
     *   <li>天单位（DAY_OF_MONTH）：返回基准日期的 23:59:59</li>
     *   <li>小时单位（HOUR_OF_DAY）：返回基准日期当前小时的 59分59秒</li>
     *   <li>分钟单位（MINUTE）：返回基准日期当前分钟的 59秒</li>
     * </ul>
     * 
     * @param baseDate 基准日期（作为计算边界的参照时间），不可为null
     * @return 当前时间单位的结束边界时间
     * @throws NullPointerException 若baseDate为null
     */
    public final Date maxValue(@NonNull Date baseDate) {
        Calendar calendar = getCalendar(false);
        calendar.setTimeInMillis(baseDate.getTime());
        setMaxBoundary(calendar);
        return calendar.getTime();
    }

    /**
     * 递归设置Calendar实例为当前时间单位的起始边界（最小值）
     * <p>
     * 逻辑：先递归处理下一级更小单位（设置为其起始边界），再将当前级的下一级字段设为实际最小值。
     * 示例（天单位）：先递归设置小时→分钟→秒为起始边界，最后确保小时字段为0（实际最小值）。
     * 
     * @param calendar 要设置的Calendar实例（需已绑定基准时间），不可为null
     * @throws NullPointerException 若calendar为null
     */
    public void setMinBoundary(Calendar calendar) {
        if (calendar == null) {
            throw new NullPointerException("Calendar instance cannot be null");
        }
        if (nextRangeUnit == null) {
            return;
        }
        nextRangeUnit.setMinBoundary(calendar);
        calendar.set(nextRangeUnit.getCalendarField(), calendar.getActualMinimum(nextRangeUnit.getCalendarField()));
    }

    /**
     * 递归设置Calendar实例为当前时间单位的结束边界（最大值）
     * <p>
     * 逻辑：先递归处理下一级更小单位（设置为其结束边界），再将当前级的下一级字段设为实际最大值。
     * 示例（天单位）：先递归设置小时→分钟→秒为结束边界，最后确保小时字段为23（实际最大值）。
     * 
     * @param calendar 要设置的Calendar实例（需已绑定基准时间），不可为null
     * @throws NullPointerException 若calendar为null
     */
    public void setMaxBoundary(Calendar calendar) {
        if (calendar == null) {
            throw new NullPointerException("Calendar instance cannot be null");
        }
        if (nextRangeUnit == null) {
            return;
        }
        nextRangeUnit.setMaxBoundary(calendar);
        calendar.set(nextRangeUnit.getCalendarField(), calendar.getActualMaximum(nextRangeUnit.getCalendarField()));
    }
}