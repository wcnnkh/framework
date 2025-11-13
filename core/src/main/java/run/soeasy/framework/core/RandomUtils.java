package run.soeasy.framework.core;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * 线程安全的随机工具类（不可实例化），基于 {@link ThreadLocalRandom} 实现高效、线程安全的随机操作，
 * 核心提供**随机字符流、指定长度随机字符串、随机对象流**三大功能，适用于随机密码生成、测试数据构造、随机采样等场景。
 * 
 * <p>
 * <strong>设计特性</strong>：
 * <ul>
 * <li><strong>不可实例化</strong>：通过 {@link UtilityClass} 注解标记，自动生成私有构造方法，禁止创建实例，所有方法均为静态；</li>
 * <li><strong>线程安全</strong>：依赖 {@link ThreadLocalRandom}（JDK 8+ 线程安全随机类），避免多线程竞争冲突，性能优于 {@link java.util.Random}；</li>
 * <li><strong>轻量高效</strong>：无额外依赖，基于原生流 API 和数组操作，执行效率高；</li>
 * <li><strong>泛型支持</strong>：{@link #objects(Object[])} 方法支持任意类型数组，灵活适配不同场景；</li>
 * <li><strong>参数安全</strong>：核心参数通过 {@link NonNull} 注解强制非空校验，避免空指针异常。</li>
 * </ul>
 *
 * <h3>核心功能模块</h3>
 * <ol>
 * <li><strong>随机字符流</strong>：从源字符序列中随机抽取字符，生成 IntStream（包含字符的 ASCII 码值）；</li>
 * <li><strong>随机字符串</strong>：从源字符序列中随机选取字符，拼接成指定长度的字符串；</li>
 * <li><strong>随机对象流</strong>：从任意类型数组中随机抽取元素，生成 Stream 流（支持重复抽取）。</li>
 * </ol>
 *
 * <h3>使用示例</h3>
 * 
 * <pre class="code">
 * // 1. 生成随机字符流（从字母序列中抽取5个字符并打印）
 * CharSequence charSource = "ABCDEFG";
 * RandomUtils.chars(charSource)
 *            .limit(5)
 *            .mapToObj(c -> (char) c)
 *            .forEach(System.out::print); // 输出示例：B E G A D（随机顺序）
 *
 * // 2. 生成6位随机密码（包含大小写字母和数字）
 * CharSequence pwdSource = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
 * String randomPwd = RandomUtils.random(pwdSource, 6);
 * System.out.println("随机密码：" + randomPwd); // 输出示例：x7Q2z9
 *
 * // 3. 生成随机对象流（从商品数组中随机抽取3个商品并打印）
 * String[] goods = {"手机", "电脑", "耳机", "平板"};
 * RandomUtils.objects(goods)
 *            .limit(3)
 *            .forEach(System.out::println); // 输出示例：耳机、手机、平板（随机顺序，可能重复）
 * </pre>
 *
 * @author soeasy.run
 * @see UtilityClass Lombok 注解，标记此类为不可实例化工具类
 * @see ThreadLocalRandom JDK 原生线程安全随机类，当前工具类的核心依赖
 * @see IntStream 整数流，用于承载随机字符的 ASCII 码值
 * @see Stream 通用流，用于承载随机抽取的对象元素
 */
@UtilityClass
public class RandomUtils {

    /**
     * 从源字符序列中随机抽取字符，生成包含字符 ASCII 码值的 IntStream
     * <p>
     * 核心逻辑：
     * 1. 通过 {@link ThreadLocalRandom#ints(int, int)} 生成 0 到源字符序列长度（左闭右开）的随机索引流；
     * 2. 将随机索引映射为源字符序列中对应的字符，并转为 ASCII 码值（int 类型）；
     * 3. 流的长度无默认限制，可通过 {@link IntStream#limit(long)} 控制抽取数量。
     *
     * @param source 源字符序列（不可为 null，若长度为 0 会生成空流）
     * @return 包含随机字符 ASCII 码值的 IntStream，线程安全且可无限流（需手动限制长度）
     * @throws NullPointerException 若 source 为 null，由 {@link NonNull} 注解自动触发
     */
    public static IntStream chars(@NonNull CharSequence source) {
        return ThreadLocalRandom.current()
                                .ints(0, source.length()) // 生成 [0, source.length()) 区间的随机索引
                                .map((e) -> source.charAt(e)); // 索引映射为字符，转为 ASCII 码值
    }

    /**
     * 从源字符序列中随机选取字符，拼接成指定长度的随机字符串
     * <p>
     * 核心逻辑：
     * 1. 初始化指定长度的字符数组，用于存储随机字符；
     * 2. 循环 length 次，每次通过 {@link ThreadLocalRandom#nextInt(int)} 生成随机索引，从源序列中取字符；
     * 3. 将字符数组转为字符串返回，支持重复抽取同一字符（索引随机，可能重复）。
     *
     * @param source 源字符序列（不可为 null，长度需 ≥1，否则返回空字符串）
     * @param length 目标字符串长度（需 ≥0，若为 0 返回空字符串；若为负数可能抛出 {@link IllegalArgumentException}）
     * @return 随机生成的字符串，长度与指定的 length 一致
     * @throws NullPointerException 若 source 为 null，由 {@link NonNull} 注解自动触发
     */
    public static String random(@NonNull CharSequence source, int length) {
        int randomBound = source.length();
        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(randomBound); // 生成 [0, randomBound) 随机索引
            array[i] = source.charAt(index);
        }
        return new String(array);
    }

    /**
     * 从指定数组中随机抽取元素，生成包含随机元素的 Stream 流
     * <p>
     * 核心逻辑：
     * 1. 通过 {@link ThreadLocalRandom#ints(int, int)} 生成 0 到数组长度（左闭右开）的随机索引流；
     * 2. 将随机索引映射为数组中对应的元素，生成泛型 Stream；
     * 3. 流的长度无默认限制，可通过 {@link Stream#limit(long)} 控制抽取数量，支持重复抽取同一元素。
     *
     * @param array 源数组（不可为 null，若长度为 0 会生成空流）
     * @param <T>   数组元素类型
     * @return 包含随机元素的 Stream<T>，线程安全且可无限流（需手动限制长度）
     * @throws NullPointerException 若 array 为 null，由 {@link NonNull} 注解自动触发
     */
    public static <T> Stream<T> objects(@NonNull T[] array) {
        return ThreadLocalRandom.current()
                                .ints(0, array.length) // 生成 [0, array.length()) 区间的随机索引
                                .mapToObj((e) -> array[e]); // 索引映射为数组元素
    }
}