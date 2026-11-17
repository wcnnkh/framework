package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.NonNull;

/**
 * 秒时间单位实现，提供秒级的时间处理能力。
 * <p>
 * 该类继承自{@link PeriodicTimeUnit}，定义了时间单位为秒（1000毫秒），
 * 下一级时间单位为毫秒（{@link Millisecond}），适用于需要按秒处理时间的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>固定周期：1秒 = 1,000毫秒</li>
 *   <li>格式模式：支持自定义格式，默认使用"yyyy-MM-dd HH:mm:ss"（精确到秒）</li>
 *   <li>层级结构：下一级时间单位为毫秒，形成"秒→毫秒"的层级</li>
 *   <li>单例实例：通过{@link #DEFAULT}提供全局可用的秒时间单位实例</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PeriodicTimeUnit
 * @see Millisecond
 * @see Calendar#SECOND
 */
public class Second extends PeriodicTimeUnit {
    
    /** 全局默认的秒时间单位实例，建议优先使用该实例以避免重复创建 */
    public static final Second DEFAULT = new Second();

    /**
     * 构造函数，初始化秒时间单位，使用默认格式模式
     * <p>
     * 调用父类构造函数，设置：
     * <ul>
     *   <li>格式模式："yyyy-MM-dd HH:mm:ss"（精确到秒）</li>
     *   <li>日历字段：{@link Calendar#SECOND}</li>
     *   <li>下一个时间单位：{@link Millisecond#DEFAULT}</li>
     *   <li>周期：1000毫秒</li>
     * </ul>
     */
    public Second() {
        this("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 构造函数，初始化秒时间单位，使用自定义格式模式
     * 
     * @param pattern 自定义日期格式模式，不可为null
     * @throws NullPointerException 若pattern为null
     */
    public Second(@NonNull String pattern) {
        super(pattern, Calendar.SECOND, Millisecond.DEFAULT, 1000L);
    }
}