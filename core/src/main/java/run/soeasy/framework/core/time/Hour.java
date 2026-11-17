package run.soeasy.framework.core.time;

import java.util.Calendar;

import lombok.NonNull;

/**
 * 小时时间单位实现，提供小时级的时间处理能力。
 * <p>
 * 该类继承自{@link PeriodicTimeUnit}，定义了时间单位为小时（60分钟），
 * 下一级时间单位为分钟（{@link Minute}），适用于需要按小时处理时间的场景。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>固定周期：1小时 = 3,600,000毫秒（60分钟 × 60秒 × 1000毫秒）</li>
 * <li>格式模式：支持自定义格式，默认使用"yyyy-MM-dd HH"（精确到小时）</li>
 * <li>层级结构：下一级时间单位为分钟，形成"小时→分钟→秒→毫秒"的完整层级</li>
 * <li>单例实例：通过{@link #DEFAULT}提供全局可用的小时时间单位实例</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PeriodicTimeUnit
 * @see Minute
 * @see Calendar#HOUR_OF_DAY
 */
public class Hour extends PeriodicTimeUnit {

	/** 全局默认的小时时间单位实例，建议优先使用该实例以避免重复创建 */
	public static final Hour DEFAULT = new Hour();

	/**
	 * 构造函数，初始化小时时间单位，使用默认格式模式
	 * <p>
	 * 调用父类构造函数，设置：
	 * <ul>
	 * <li>格式模式："yyyy-MM-dd HH"（精确到小时）</li>
	 * <li>日历字段：{@link Calendar#HOUR_OF_DAY}</li>
	 * <li>下一个时间单位：{@link Minute#DEFAULT}</li>
	 * <li>周期：60分钟（60 × 60 × 1000毫秒）</li>
	 * </ul>
	 */
	public Hour() {
		this("yyyy-MM-dd HH");
	}

	/**
	 * 构造函数，初始化小时时间单位，使用自定义格式模式
	 * 
	 * @param pattern 自定义日期格式模式
	 */
	public Hour(@NonNull String pattern) {
		super(pattern != null ? pattern : "yyyy-MM-dd HH", Calendar.HOUR_OF_DAY, Minute.DEFAULT,
				60 * Minute.DEFAULT.getMillseconds());
	}
}