package run.soeasy.framework.core.time;

import java.util.Calendar;
import lombok.NonNull;

/**
 * 年时间单位实现，提供年份级的时间处理能力。
 * <p>
 * 该类继承自{@link TimeUnit}，定义了时间单位为年，
 * 下一级时间单位为月（{@link Month}），适用于需要按年份处理时间的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>年份差计算：精确计算两个日期之间的年份间隔，考虑月份和日期的影响</li>
 *   <li>格式模式：支持自定义格式，默认使用"yyyy"（精确到年份）</li>
 *   <li>层级结构：下一级时间单位为月，形成"年→月→日→小时→分钟→秒→毫秒"的完整层级</li>
 *   <li>单例实例：通过{@link #DEFAULT}提供全局可用的年时间单位实例</li>
 * </ul>
 *
 * <p><b>年份差计算规则：</b>
 * <ol>
 *   <li>基本年份差 = 结束年份 - 开始年份</li>
 *   <li>若结束月份小于开始月份，年份差减1</li>
 *   <li>若月份相同但结束日期小于开始日期，年份差减1</li>
 * </ol>
 *
 * @author soeasy.run
 * @see TimeUnit
 * @see Month
 * @see Calendar#YEAR
 */
public class Year extends TimeUnit {
    
    /** 全局默认的年时间单位实例，建议优先使用该实例以避免重复创建 */
    public static final Year DEFAULT = new Year();

    /**
     * 构造函数，初始化年时间单位，使用默认格式模式
     * <p>
     * 调用父类构造函数，设置：
     * <ul>
     *   <li>格式模式："yyyy"（精确到年份）</li>
     *   <li>日历字段：{@link Calendar#YEAR}</li>
     *   <li>下一个时间单位：{@link Month#DEFAULT}</li>
     * </ul>
     */
    public Year() {
        this("yyyy");
    }
    
    /**
     * 构造函数，初始化年时间单位，使用自定义格式模式
     * 
     * @param pattern 自定义日期格式模式，不可为null
     */
    public Year(@NonNull String pattern) {
        super(pattern, Calendar.YEAR, Month.DEFAULT);
    }

    /**
     * 计算两个Calendar实例之间的年份差
     * <p>
     * 计算逻辑：
     * <ol>
     *   <li>自动调整开始日期和结束日期的顺序，确保开始日期在结束日期之前</li>
     *   <li>计算基本年份差 = 结束年份 - 开始年份</li>
     *   <li>若结束月份小于开始月份，年份差减1</li>
     *   <li>若月份相同但结束日期小于开始日期，年份差减1</li>
     * </ol>
     * 
     * @param start 起始日期
     * @param end 结束日期
     * @return 两个日期之间的年份差
     */
    @Override
    public long distance(Calendar start, Calendar end) {
        // 确保开始日期在结束日期之前
        if (start.after(end)) {
            Calendar temp = start;
            start = end;
            end = temp;
        }

        int startYear = start.get(Calendar.YEAR);
        int endYear = end.get(Calendar.YEAR);
        int startMonth = start.get(Calendar.MONTH);
        int endMonth = end.get(Calendar.MONTH);
        int startDay = start.get(Calendar.DAY_OF_MONTH);
        int endDay = end.get(Calendar.DAY_OF_MONTH);

        // 计算初始年份差
        int years = endYear - startYear;

        // 如果结束月份小于开始月份，年份差减1
        if (endMonth < startMonth) {
            years--;
        }
        // 如果月份相同，但结束日期小于开始日期，年份差减1
        else if (endMonth == startMonth && endDay < startDay) {
            years--;
        }

        return years;
    }
}