package run.soeasy.framework.core.time;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;

/**
 * 时间单位处理器，支持获取时间单位的最小和最大值，形成时间单位层级结构。
 * <p>
 * 该抽象类继承自{@link TimeDiscrete}，扩展了时间离散化能力，
 * 提供了获取时间单位最小值和最大值的功能，并通过`nextTimeUnit`形成时间单位层级，
 * 适用于需要处理时间范围（如一天的开始和结束）的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>时间范围获取：支持获取指定时间在当前时间单位下的最小值和最大值</li>
 *   <li>单位层级结构：通过`nextTimeUnit`形成时间单位层级（如天->小时->分钟）</li>
 *   <li>递归处理：利用层级结构递归处理更小的时间单位，实现精细的时间范围控制</li>
 *   <li>日历操作：基于{@link Calendar}实现时间范围的计算和设置</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TimeDiscrete
 * @see Calendar
 */
@Getter
public abstract class TimeUnit extends TimeDiscrete {
    
    /** 下一个更小的时间单位，形成时间单位层级结构（如天的下一个单位是小时） */
    private final TimeUnit nextTimeUnit;

    /**
     * 构造函数，初始化时间单位处理器
     * 
     * @param pattern 日期格式模式，不可为null
     * @param calendarField 日历字段，如{@link Calendar#DAY_OF_MONTH}
     * @param nextTimeUnit 下一个更小的时间单位，可为null（表示没有更小单位）
     * @throws NullPointerException 若pattern为null
     */
    public TimeUnit(@NonNull String pattern, int calendarField, TimeUnit nextTimeUnit) {
        super(pattern, calendarField);
        this.nextTimeUnit = nextTimeUnit;
    }

    /**
     * 获取指定日期在当前时间单位下的最小值
     * <p>
     * 例如：对于天单位，返回当天的00:00:00；对于小时单位，返回当前小时的00分00秒。
     * 
     * @param value 基准日期，不可为null
     * @return 当前时间单位下的最小时间点
     * @throws NullPointerException 若value为null
     */
    public final Date minValue(@NonNull Date value) {
        Calendar calendar = getCalendar(false);
        calendar.setTimeInMillis(value.getTime());
        setMinValue(calendar);
        return calendar.getTime();
    }

    /**
     * 获取指定日期在当前时间单位下的最大值
     * <p>
     * 例如：对于天单位，返回当天的23:59:59；对于小时单位，返回当前小时的59分59秒。
     * 
     * @param value 基准日期，不可为null
     * @return 当前时间单位下的最大时间点
     * @throws NullPointerException 若value为null
     */
    public final Date maxValue(@NonNull Date value) {
        Calendar calendar = getCalendar(false);
        calendar.setTimeInMillis(value.getTime());
        setMaxValue(calendar);
        return calendar.getTime();
    }

    /**
     * 设置Calendar实例为当前时间单位的最小值
     * <p>
     * 递归设置下一个时间单位的最小值，并将当前时间单位设置为最小值。
     * 例如：设置天时，先设置小时、分钟、秒为最小值，再将天设置为最小值。
     * 
     * @param value 要设置的Calendar实例，不可为null
     * @throws NullPointerException 若value为null
     */
    public void setMinValue(Calendar value) {
        if (value == null) {
            throw new NullPointerException("Calendar value cannot be null");
        }
        if (nextTimeUnit == null) {
            return;
        }
        nextTimeUnit.setMinValue(value);
        value.set(nextTimeUnit.getCalendarField(), value.getActualMinimum(nextTimeUnit.getCalendarField()));
    }

    /**
     * 设置Calendar实例为当前时间单位的最大值
     * <p>
     * 递归设置下一个时间单位的最大值，并将当前时间单位设置为最大值。
     * 例如：设置天时，先设置小时、分钟、秒为最大值，再将天设置为最大值。
     * 
     * @param value 要设置的Calendar实例，不可为null
     * @throws NullPointerException 若value为null
     */
    public void setMaxValue(Calendar value) {
        if (value == null) {
            throw new NullPointerException("Calendar value cannot be null");
        }
        if (nextTimeUnit == null) {
            return;
        }
        nextTimeUnit.setMaxValue(value);
        value.set(nextTimeUnit.getCalendarField(), value.getActualMaximum(nextTimeUnit.getCalendarField()));
    }
}