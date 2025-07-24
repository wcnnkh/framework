package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.NonNull;

/**
 * 月份时间单位实现，提供月份级的时间处理能力。
 * <p>
 * 该类继承自{@link TimeUnit}，定义了时间单位为月份，
 * 下一级时间单位为天（{@link Day}），适用于需要按月份处理时间的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>月份差计算：精确计算两个日期之间的月份间隔，考虑日期部分的影响</li>
 *   <li>格式模式：支持自定义格式，默认使用"yyyy-MM"（精确到月份）</li>
 *   <li>层级结构：下一级时间单位为天，形成"月份→天→小时→分钟→秒→毫秒"的完整层级</li>
 *   <li>单例实例：通过{@link #DEFAULT}提供全局可用的月份时间单位实例</li>
 * </ul>
 *
 * <p><b>月份差计算规则：</b>
 * <ul>
 *   <li>年份差 × 12 + 月份差</li>
 *   <li>若结束日期的日期部分小于开始日期，月份差减1（例如：2023-01-31到2023-02-28，月份差为0而非1）</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see TimeUnit
 * @see Day
 * @see Calendar#MONTH
 */
public class Month extends TimeUnit {
    
    /** 全局默认的月份时间单位实例，建议优先使用该实例以避免重复创建 */
    public static final Month DEFAULT = new Month();

    /**
     * 构造函数，初始化月份时间单位，使用默认格式模式
     * <p>
     * 调用父类构造函数，设置：
     * <ul>
     *   <li>格式模式："yyyy-MM"（精确到月份）</li>
     *   <li>日历字段：{@link Calendar#MONTH}</li>
     *   <li>下一个时间单位：{@link Day#DEFAULT}</li>
     * </ul>
     */
    public Month() {
        this("yyyy-MM");
    }
    
    /**
     * 构造函数，初始化月份时间单位，使用自定义格式模式
     * 
     * @param pattern 自定义日期格式模式，不可为null
     * @throws NullPointerException 若pattern为null
     */
    public Month(@NonNull String pattern) {
        super(pattern, Calendar.MONTH, Day.DEFAULT);
    }

    /**
     * 计算两个Calendar实例之间的月份差
     * <p>
     * 计算逻辑：
     * <ol>
     *   <li>确保开始日期在结束日期之前</li>
     *   <li>计算年份差 × 12 + 月份差</li>
     *   <li>若结束日期的日期部分小于开始日期，月份差减1</li>
     * </ol>
     * 
     * @param startDate 起始日期，不可为null
     * @param endDate 结束日期，不可为null
     * @return 两个日期之间的月份差
     * @throws NullPointerException 若startDate或endDate为null
     */
    @Override
    public long distance(Calendar startDate, Calendar endDate) {
        if (startDate == null || endDate == null) {
            throw new NullPointerException("Start and end dates cannot be null");
        }

        // 确保开始日期在结束日期之前
        if (startDate.after(endDate)) {
            Calendar temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        int startYear = startDate.get(Calendar.YEAR);
        int endYear = endDate.get(Calendar.YEAR);
        int startMonth = startDate.get(Calendar.MONTH);
        int endMonth = endDate.get(Calendar.MONTH);

        // 计算年份差转换为月份数
        long months = (endYear - startYear) * 12;
        // 加上月份差
        months += (endMonth - startMonth);

        // 处理日期部分的影响：若结束日期的日期小于开始日期，月份差减1
        if (endDate.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH)) {
            months--;
        }

        return months;
    }
}