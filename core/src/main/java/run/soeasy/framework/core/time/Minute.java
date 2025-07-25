package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.NonNull;

/**
 * 分钟时间单位实现，提供分钟级的时间处理能力。
 * <p>
 * 该类继承自{@link PeriodicTimeUnit}，定义了时间单位为分钟（60秒），
 * 下一级时间单位为秒（{@link Second}），适用于需要按分钟处理时间的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>固定周期：1分钟 = 60,000毫秒</li>
 *   <li>格式模式：支持自定义格式，默认使用"yyyy-MM-dd HH:mm"</li>
 *   <li>层级结构：下一级时间单位为秒，形成"分钟→秒→毫秒"的层级</li>
 *   <li>单例实例：通过{@link #DEFAULT}提供全局可用的分钟时间单位实例</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PeriodicTimeUnit
 * @see Second
 * @see Calendar#MINUTE
 */
public class Minute extends PeriodicTimeUnit {
    
    /** 全局默认的分钟时间单位实例，建议优先使用该实例以避免重复创建 */
    public static final Minute DEFAULT = new Minute();

    /**
     * 构造函数，初始化分钟时间单位，使用默认格式模式
     * <p>
     * 调用父类构造函数，设置：
     * <ul>
     *   <li>格式模式："yyyy-MM-dd HH:mm"（精确到分钟）</li>
     *   <li>日历字段：{@link Calendar#MINUTE}</li>
     *   <li>下一个时间单位：{@link Second#DEFAULT}</li>
     *   <li>周期：60秒（60 * 1000毫秒）</li>
     * </ul>
     */
    public Minute() {
        this("yyyy-MM-dd HH:mm");
    }
    
    /**
     * 构造函数，初始化分钟时间单位，使用自定义格式模式
     * 
     * @param pattern 自定义日期格式模式，不可为null
     * @throws NullPointerException 若pattern为null
     */
    public Minute(@NonNull String pattern) {
        super(pattern, Calendar.MINUTE, Second.DEFAULT, 60 * Second.DEFAULT.getMillseconds());
    }
}