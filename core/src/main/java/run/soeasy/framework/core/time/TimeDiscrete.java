package run.soeasy.framework.core.time;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Discrete;

/**
 * 离散时间单位处理器，支持基于日历字段的时间离散化操作。
 * <p>
 * 该抽象类继承自{@link TimeFormat}并实现{@link Discrete&lt;Date&gt;}接口，
 * 提供了对日期时间的离散化操作能力，如计算时间距离、获取相邻时间点等，
 * 适用于需要按固定时间单位（如天、小时、分钟）处理时间的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>日历字段驱动：基于{@link Calendar}的字段（如年、月、日）进行离散化操作</li>
 *   <li>时间距离计算：支持计算两个时间点在指定日历字段上的距离</li>
 *   <li>相邻时间点获取：可获取指定时间点的下一个或前一个离散时间点</li>
 *   <li>格式转换能力：继承自{@link TimeFormat}，支持日期与字符串的格式转换</li>
 * </ul>
 *
 * @param <Date> 时间类型，此处固定为Java原生{@link Date}
 * 
 * @author soeasy.run
 * @see TimeFormat
 * @see Discrete
 * @see Calendar
 */
@Getter
public abstract class TimeDiscrete extends TimeFormat implements Discrete<Date> {
    
    /** 日历字段标识符，用于指定离散化操作的时间单位（如{@link Calendar#DAY_OF_MONTH}） */
    private final int calendarField;

    /**
     * 构造函数，初始化离散时间处理器
     * 
     * @param pattern 日期格式模式，不可为null
     * @param calendarField 日历字段，如{@link Calendar#YEAR}、{@link Calendar#HOUR_OF_DAY}等
     * @throws NullPointerException 若pattern为null
     */
    public TimeDiscrete(@NonNull String pattern, int calendarField) {
        super(pattern);
        this.calendarField = calendarField;
    }

    /**
     * 获取Calendar实例
     * <p>
     * 该方法返回Calendar实例，可选择是否强制创建新实例。
     * 子类可重写此方法以实现自定义的Calendar实例管理（如线程安全的Calendar池）。
     * 
     * @param forceCreate 是否强制创建新的Calendar实例
     * @return Calendar实例
     */
    public Calendar getCalendar(boolean forceCreate) {
        return Calendar.getInstance();
    }

    /**
     * 计算两个日期在指定日历字段上的距离
     * <p>
     * 该方法将日期转换为Calendar实例后，调用抽象的{@link #distance(Calendar, Calendar)}方法计算距离。
     * 
     * @param start 起始日期，不可为null
     * @param end 结束日期，不可为null
     * @return 两个日期在指定日历字段上的距离
     * @throws NullPointerException 若start或end为null
     */
    @Override
    public long distance(Date start, Date end) {
        if (start == null || end == null) {
            throw new NullPointerException("Start and end dates cannot be null");
        }
        Calendar startCalendar = getCalendar(false);
        startCalendar.setTime(start);
        Calendar endCalendar = getCalendar(true);
        endCalendar.setTime(end);
        return distance(startCalendar, endCalendar);
    }

    /**
     * 获取指定日期的下一个离散时间点
     * <p>
     * 通过操作Calendar实例，在指定日历字段上增加1个单位，获取下一个时间点。
     * 
     * @param value 基准日期，不可为null
     * @return 下一个离散时间点
     * @throws NullPointerException 若value为null
     */
    @Override
    public Date next(Date value) {
        if (value == null) {
            throw new NullPointerException("Value date cannot be null");
        }
        Calendar calendar = getCalendar(false);
        calendar.setTimeInMillis(value.getTime());
        next(calendar, 1);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的前一个离散时间点
     * <p>
     * 通过操作Calendar实例，在指定日历字段上减少1个单位，获取前一个时间点。
     * 
     * @param value 基准日期，不可为null
     * @return 前一个离散时间点
     * @throws NullPointerException 若value为null
     */
    @Override
    public Date previous(Date value) {
        if (value == null) {
            throw new NullPointerException("Value date cannot be null");
        }
        Calendar calendar = getCalendar(false);
        calendar.setTimeInMillis(value.getTime());
        previous(calendar, 1);
        return calendar.getTime();
    }

    /**
     * 向前移动Calendar实例的时间
     * <p>
     * 在指定日历字段上增加指定的单位数，更新Calendar实例。
     * 
     * @param value 要操作的Calendar实例，不可为null
     * @param unit 要增加的单位数
     * @throws NullPointerException 若value为null
     */
    public void next(Calendar value, int unit) {
        if (value == null) {
            throw new NullPointerException("Calendar value cannot be null");
        }
        value.add(calendarField, unit);
    }

    /**
     * 向后移动Calendar实例的时间
     * <p>
     * 在指定日历字段上减少指定的单位数，更新Calendar实例。
     * 
     * @param value 要操作的Calendar实例，不可为null
     * @param unit 要减少的单位数
     * @throws NullPointerException 若value为null
     */
    public void previous(Calendar value, int unit) {
        if (value == null) {
            throw new NullPointerException("Calendar value cannot be null");
        }
        value.add(calendarField, -unit);
    }

    /**
     * 抽象方法：计算两个Calendar实例在指定日历字段上的距离
     * <p>
     * 由子类实现具体的距离计算逻辑，例如：
     * <ul>
     *   <li>按天计算：两个日期之间的天数差</li>
     *   <li>按小时计算：两个时间点之间的小时差</li>
     * </ul>
     * 
     * @param start 起始Calendar实例，不可为null
     * @param end 结束Calendar实例，不可为null
     * @return 两个Calendar实例在指定日历字段上的距离
     * @throws NullPointerException 若start或end为null
     */
    public abstract long distance(Calendar start, Calendar end);
}