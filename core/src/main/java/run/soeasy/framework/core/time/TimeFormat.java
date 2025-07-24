package run.soeasy.framework.core.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.strings.StringConverter;

/**
 * 时间格式转换器，实现字符串与日期对象的双向转换，支持自定义格式模式和区域设置。
 * <p>
 * 该类提供了线程不安全的日期格式转换功能，建议在需要频繁转换的场景中使用线程安全的替代方案或实例池。
 * 实现了{@link StringConverter<Date>}接口，可集成到框架的类型转换系统中。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>支持自定义日期格式模式（如"yyyy-MM-dd HH:mm:ss"）</li>
 *   <li>支持区域设置（Locale）以处理不同语言环境的日期格式</li>
 *   <li>提供静态预定义格式{@link #DATE}（适用于RFC 822标准日期格式）</li>
 *   <li>异常处理：将{@link ParseException}转换为框架统一的{@link ConversionException}</li>
 * </ul>
 *
 * @author soeasy.run
 * @see DateFormat
 * @see SimpleDateFormat
 * @see StringConverter
 */
@Getter
public class TimeFormat implements StringConverter<Date> {
    
    /**
     * 预定义的RFC 822格式转换器（例如："Thu May 05 14:30:00 CST 2023"）
     * <p>
     * 格式模式："EEE MMM dd HH:mm:ss zzz yyyy"
     * 区域设置：Locale.US
     */
    public static final TimeFormat DATE = new TimeFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    
    /** 日期格式的区域设置，可为null表示使用默认区域 */
    private final Locale locale;
    
    /** 日期格式模式字符串，不可为null */
    @NonNull
    private final String pattern;

    /**
     * 使用指定格式模式创建时间格式转换器（使用默认区域设置）
     * 
     * @param pattern 日期格式模式，不可为null
     * @throws NullPointerException 若pattern为null
     */
    public TimeFormat(@NonNull String pattern) {
        this(pattern, null);
    }

    /**
     * 使用指定格式模式和区域设置创建时间格式转换器
     * 
     * @param pattern 日期格式模式，不可为null
     * @param locale 区域设置，可为null表示使用默认区域
     * @throws NullPointerException 若pattern为null
     */
    public TimeFormat(@NonNull String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
    }

    /**
     * 将日期对象格式化为字符串
     * 
     * @param source 要格式化的日期对象，可为null（返回null）
     * @return 格式化后的字符串，若source为null则返回null
     */
    public final String format(Date source) {
        if (source == null) {
            return null;
        }
        DateFormat dateFormat = getDateFormat();
        return dateFormat.format(source);
    }

    /**
     * 将毫秒时间戳格式化为字符串
     * 
     * @param milliseconds 毫秒时间戳
     * @return 格式化后的字符串
     */
    public final String format(long milliseconds) {
        return format(new Date(milliseconds));
    }

    /**
     * 将字符串转换为日期对象（实现{@link StringConverter}接口）
     * 
     * @param source 要解析的字符串，不可为null
     * @param sourceTypeDescriptor 源类型描述（未使用）
     * @param targetTypeDescriptor 目标类型描述（未使用）
     * @return 解析后的日期对象
     * @throws ConversionException 若解析失败
     * @throws NullPointerException 若source为null
     */
    @Override
    public Date from(String source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        if (source == null) {
            throw new NullPointerException("Source string cannot be null");
        }
        return parse(source);
    }

    /**
     * 获取线程不安全的DateFormat实例
     * <p>
     * 注意：DateFormat不是线程安全的，多线程环境下共享实例可能导致解析错误
     * 
     * @return DateFormat实例
     */
    public DateFormat getDateFormat() {
        return locale == null ? new SimpleDateFormat(pattern) : new SimpleDateFormat(pattern, locale);
    }

    /**
     * 解析字符串为日期对象
     * 
     * @param source 要解析的字符串，不可为null
     * @return 解析后的日期对象
     * @throws ConversionException 若字符串格式不符合模式
     * @throws NullPointerException 若source为null
     */
    public final Date parse(String source) throws ConversionException {
        if (source == null) {
            throw new NullPointerException("Source string cannot be null");
        }
        DateFormat dateFormat = getDateFormat();
        try {
            return dateFormat.parse(source);
        } catch (ParseException e) {
            throw new ConversionException("Failed to parse date: " + source, e);
        }
    }

    /**
     * 将日期对象转换为字符串（实现{@link StringConverter}接口）
     * 
     * @param source 要转换的日期对象，可为null（返回null）
     * @param sourceTypeDescriptor 源类型描述（未使用）
     * @param targetTypeDescriptor 目标类型描述（未使用）
     * @return 转换后的字符串，若source为null则返回null
     * @throws ConversionException 若格式化失败（理论上不会发生）
     */
    @Override
    public String to(Date source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        return format(source);
    }
}