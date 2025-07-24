package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.Getter;
import lombok.NonNull;

/**
 * 周期性时间单位处理器，支持基于固定毫秒周期的时间离散化和距离计算。
 * <p>
 * 该类继承自{@link TimeUnit}，通过指定周期毫秒数（millseconds）定义时间单位，
 * 适用于需要处理固定时间间隔（如秒、分钟、小时）的场景，提供了统一的时间距离计算和范围获取能力。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>固定周期定义：通过毫秒数精确定义时间单位周期（如1000ms=1秒）</li>
 *   <li>距离计算：基于毫秒差和周期计算时间单位距离（如两个时间点间隔多少秒）</li>
 *   <li>范围获取：继承自父类的时间范围能力（获取当前时间单位的最小/最大值）</li>
 *   <li>层级支持：通过`nextTimeUnit`形成时间单位层级（如秒→毫秒）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TimeUnit
 * @see TimeDiscrete
 */
@Getter
public class PeriodicTimeUnit extends TimeUnit {
    
    /** 时间单位的周期毫秒数，如1000表示1秒 */
    private final long millseconds;

    /**
     * 构造函数，初始化周期性时间单位处理器
     * 
     * @param pattern 日期格式模式，不可为null
     * @param calendarField 日历字段，如{@link Calendar#SECOND}
     * @param nextTimeUnit 下一个更小的时间单位，可为null
     * @param millseconds 周期毫秒数，必须大于0
     * @throws NullPointerException 若pattern为null
     * @throws IllegalArgumentException 若millseconds≤0
     */
    public PeriodicTimeUnit(@NonNull String pattern, int calendarField, TimeUnit nextTimeUnit, long millseconds) {
        super(pattern, calendarField, nextTimeUnit);
        if (millseconds <= 0) {
            throw new IllegalArgumentException("Milliseconds must be greater than 0");
        }
        this.millseconds = millseconds;
    }

    /**
     * 计算两个Calendar实例在当前时间单位下的距离
     * <p>
     * 距离计算方式：|startMillis - endMillis| / millseconds
     * 例如：millseconds=1000时，返回两个时间点的秒数差
     * 
     * @param start 起始Calendar实例，不可为null
     * @param end 结束Calendar实例，不可为null
     * @return 时间单位距离（向下取整）
     * @throws NullPointerException 若start或end为null
     */
    @Override
    public long distance(Calendar start, Calendar end) {
        if (start == null || end == null) {
            throw new NullPointerException("Start and end calendars cannot be null");
        }
        long startMillis = start.getTimeInMillis();
        long endMillis = end.getTimeInMillis();
        return Math.abs(startMillis - endMillis) / millseconds;
    }
}