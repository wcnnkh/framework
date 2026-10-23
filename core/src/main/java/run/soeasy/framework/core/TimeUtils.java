package run.soeasy.framework.core;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 时间处理工具类，提供LocalDateTime的格式化、解析、时间戳与LocalDateTime互转等核心功能
 *
 * @author soeasy.run
 */
@UtilityClass
public class TimeUtils {

    /**
     * 将LocalDateTime格式化为字符串，依次尝试指定的格式化器，返回第一个格式化成功的结果
     * <p>若所有指定格式化器均失败，将使用ISO_LOCAL_DATE_TIME格式尝试格式化，仍失败则返回空Optional</p>
     *
     * @param localDateTime      待格式化的本地日期时间（非空）
     * @param dateTimeFormatters 格式化器数组（非空，内部会过滤null元素）
     * @return 格式化后的字符串Optional，格式化失败时返回空
     */
    public static Optional<String> format(@NonNull LocalDateTime localDateTime,
                                          @NonNull DateTimeFormatter... dateTimeFormatters) {
        return format(localDateTime, Streamable.array(dateTimeFormatters));
    }

    /**
     * 将LocalDateTime格式化为字符串，依次尝试Streamable中的格式化器，返回第一个格式化成功的结果
     * <p>若所有指定格式化器均失败，将使用ISO_LOCAL_DATE_TIME格式尝试格式化，仍失败则返回空Optional</p>
     *
     * @param localDateTime      待格式化的本地日期时间（非空）
     * @param dateTimeFormatters 包含格式化器的Streamable（非空，内部会过滤null元素）
     * @return 格式化后的字符串Optional，格式化失败时返回空
     */
    public static Optional<String> format(@NonNull LocalDateTime localDateTime,
                                          @NonNull Streamable<? extends DateTimeFormatter> dateTimeFormatters) {
        Optional<String> optional = dateTimeFormatters.filter((e) -> e != null).map((formatter) -> {
            try {
                return localDateTime.format(formatter);
            } catch (DateTimeException e) {
                return null;
            }
        }).filter((e) -> e != null).findFirst();
        if (optional.isPresent()) {
            return optional;
        }

        try {
            return Optional.ofNullable(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (DateTimeException e) {
            return Optional.empty();
        }
    }

    /**
     * 将毫秒级时间戳转换为指定时区的LocalDateTime
     *
     * @param timestamp 毫秒级Unix时间戳
     * @param zoneId    目标时区（非空）
     * @return 转换后的本地日期时间
     * @throws DateTimeException 若时间戳无效或时区不合法时抛出
     */
    public static LocalDateTime of(long timestamp, @NonNull ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
    }

    /**
     * 解析时间字符串为LocalDateTime，依次尝试指定的格式化器，返回第一个解析成功的结果
     * <p>若所有指定格式化器均失败，将使用默认的ISO_LOCAL_DATE_TIME格式尝试解析，仍失败则返回空Optional</p>
     *
     * @param formattedTime      待解析的时间字符串（非空）
     * @param dateTimeFormatters 包含格式化器的Streamable（非空，内部会过滤null元素）
     * @return 解析后的LocalDateTime Optional，解析失败时返回空
     */
    public static Optional<LocalDateTime> parse(@NonNull String formattedTime,
                                                @NonNull Streamable<? extends DateTimeFormatter> dateTimeFormatters) {
        Optional<LocalDateTime> optional = dateTimeFormatters.filter((e) -> e != null).map((formatter) -> {
            try {
                return LocalDateTime.parse(formattedTime, formatter);
            } catch (DateTimeParseException e) {
                return null;
            }
        }).filter((e) -> e != null).findFirst();
        if (optional.isPresent()) {
            return optional;
        }
        try {
            return Optional.ofNullable(LocalDateTime.parse(formattedTime));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * 将毫秒级时间戳转换为系统默认时区的LocalDateTime，再使用指定的模式字符串格式化
     * <p>依次尝试指定的模式，返回第一个格式化成功的结果，失败则按ISO格式尝试，仍失败返回空Optional</p>
     *
     * @param timestamp 毫秒级Unix时间戳
     * @param patterns  格式化模式字符串数组（非空，如"yyyy-MM-dd HH:mm:ss"）
     * @return 格式化后的字符串Optional，格式化失败时返回空
     */
    public static Optional<String> format(long timestamp, @NonNull String... patterns) {
        LocalDateTime localDateTime = of(timestamp, ZoneId.systemDefault());
        return format(localDateTime, Streamable.array(patterns).map(DateTimeFormatter::ofPattern));
    }

    /**
     * 解析时间字符串为Instant（系统默认时区），依次尝试指定的模式字符串，返回第一个解析成功的结果
     * <p>解析成功后将LocalDateTime转换为系统默认时区的Instant，失败则返回空Optional</p>
     *
     * @param formattedTime 待解析的时间字符串（非空）
     * @param patterns      解析使用的模式字符串数组（非空，如"yyyy-MM-dd HH:mm:ss"）
     * @return 解析后的Instant Optional，解析失败时返回空
     */
    public static Optional<Instant> parse(@NonNull String formattedTime, @NonNull String... patterns) {
        return parse(formattedTime, Streamable.array(patterns).map(DateTimeFormatter::ofPattern))
                .map((e) -> e.atZone(ZoneId.systemDefault()).toInstant());
    }
}