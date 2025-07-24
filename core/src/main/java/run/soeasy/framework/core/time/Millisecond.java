package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.NonNull;

/**
 * 毫秒时间单位实现，提供毫秒级的时间处理能力。
 * <p>
 * 该类继承自{@link PeriodicTimeUnit}，定义了时间单位为毫秒（1毫秒），
 * 是时间单位层级中的最小单位（无更小时间单位），适用于需要精确到毫秒的时间计算场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>精确到毫秒：时间单位周期为1毫秒（1ms）</li>
 *   <li>格式模式：支持自定义格式，默认使用"yyyy-MM-dd HH:mm:ss,SSS"</li>
 *   <li>单例实例：通过{@link #DEFAULT}提供全局可用的毫秒时间单位实例</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PeriodicTimeUnit
 * @see Calendar#MILLISECOND
 */
public class Millisecond extends PeriodicTimeUnit {
    
    /** 全局默认的毫秒时间单位实例，建议优先使用该实例以避免重复创建 */
    public static final Millisecond DEFAULT = new Millisecond();

    /**
     * 构造函数，初始化毫秒时间单位，使用默认格式模式
     * <p>
     * 调用父类构造函数，设置：
     * <ul>
     *   <li>格式模式："yyyy-MM-dd HH:mm:ss,SSS"（包含毫秒）</li>
     *   <li>日历字段：{@link Calendar#MILLISECOND}</li>
     *   <li>下一个时间单位：null（毫秒为最小时间单位）</li>
     *   <li>周期：1毫秒</li>
     * </ul>
     */
    public Millisecond() {
        this("yyyy-MM-dd HH:mm:ss,SSS");
    }
    
    /**
     * 构造函数，初始化毫秒时间单位，使用自定义格式模式
     * 
     * @param pattern 自定义日期格式模式，不可为null
     * @throws NullPointerException 若pattern为null
     */
    public Millisecond(@NonNull String pattern) {
        super(pattern, Calendar.MILLISECOND, null, 1L);
    }
}