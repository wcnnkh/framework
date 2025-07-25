package run.soeasy.framework.core.time;

import java.util.Calendar;

/**
 * 天时间单位实现，提供天级的时间处理能力。
 * <p>
 * 该类继承自{@link PeriodicTimeUnit}，定义了时间单位为天（24小时），
 * 下一级时间单位为小时（{@link Hour}），适用于需要按天处理时间的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>固定周期：1天 = 86,400,000毫秒（24小时 × 3,600,000毫秒/小时）</li>
 *   <li>格式模式：支持自定义格式，默认使用"yyyy-MM-dd"（精确到天）</li>
 *   <li>层级结构：下一级时间单位为小时，形成"天→小时→分钟→秒→毫秒"的完整层级</li>
 *   <li>单例实例：通过{@link #DEFAULT}提供全局可用的天时间单位实例</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PeriodicTimeUnit
 * @see Hour
 * @see Calendar#DAY_OF_MONTH
 */
public class Day extends PeriodicTimeUnit {
    
    /** 全局默认的天时间单位实例，建议优先使用该实例以避免重复创建 */
    public static final Day DEFAULT = new Day();

    /**
     * 构造函数，初始化天时间单位，使用默认格式模式
     * <p>
     * 调用父类构造函数，设置：
     * <ul>
     *   <li>格式模式："yyyy-MM-dd"（精确到天）</li>
     *   <li>日历字段：{@link Calendar#DAY_OF_MONTH}</li>
     *   <li>下一个时间单位：{@link Hour#DEFAULT}</li>
     *   <li>周期：24小时（24 × 3,600,000毫秒）</li>
     * </ul>
     */
    public Day() {
        this("yyyy-MM-dd");
    }

    /**
     * 构造函数，初始化天时间单位，使用自定义格式模式
     * 
     * @param pattern 自定义日期格式模式，允许为null时使用默认格式
     */
    public Day(String pattern) {
        super(pattern != null ? pattern : "yyyy-MM-dd", 
              Calendar.DAY_OF_MONTH, 
              Hour.DEFAULT, 
              24L * Hour.DEFAULT.getMillseconds());
    }
}