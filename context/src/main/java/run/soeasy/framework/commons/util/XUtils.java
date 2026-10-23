package run.soeasy.framework.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.commons.domain.Result;
import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * 通用工具类集合，提供字符串生成、类路径扫描、资源加载等静态工具方法。
 * 
 * <p>
 * 该类使用 {@link UtilityClass} 注解，确保其为不可实例化的工具类。 主要功能分类：
 * <ul>
 * <li>随机字符串生成（支持普通、数字、易区分字符场景）</li>
 * <li>类路径扫描（获取Bean定义或Class对象）</li>
 * <li>集合映射（过滤null元素并转换为Set）</li>
 * <li>资源加载（获取文件或类路径资源）</li>
 * </ul>
 *
 * @author soeasy.run
 */
@UtilityClass
public class XUtils {

	/**
	 * 大写英文字母常量池，包含 A-Z 共 26 个字符。
	 */
	public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 小写英文字母常量池，通过 {@link #CAPITAL_LETTERS} 转小写生成，包含 a-z 共 26 个字符。
	 */
	public static final String LOWERCASE_LETTERS = CAPITAL_LETTERS.toLowerCase();

	/**
	 * 数字字符常量池，包含 0-9 共 10 个字符。
	 */
	public static final String NUMBERIC_CHARACTER = "0123456789";

	/**
	 * 易区分字符常量池，排除了视觉上易混淆的字符（如 0/O、1/I、l 等），特别适用于验证码、临时密码等用户可视化场景。
	 * <p>
	 * 字符集组成：{@link #NUMBERIC_CHARACTER} + 小写易区分字母（acdefhkmnprstvwy） +
	 * 大写易区分字母（ABCEFGHKMNRSTVWY）。
	 */
	public static final CharSequence EASY_TO_DISTINGUISH = NUMBERIC_CHARACTER + "acdefhkmnprstvwyABCEFGHKMNRSTVWY";

	/**
	 * 全字符常量池，包含所有大小写英文字母和数字，共 62 个字符 (26+26+10)。
	 */
	public static final String ALL = CAPITAL_LETTERS + LOWERCASE_LETTERS + NUMBERIC_CHARACTER;

	/**
	 * 生成一个包含大小写字母和数字的随机字符串（基于 {@link #ALL} 字符集）。
	 *
	 * @param length 随机字符串的目标长度，必须大于等于 0。若长度为 0，则返回空字符串。
	 * @return 一个长度为 {@code length} 的随机字符串。
	 * @see RandomUtils#random(CharSequence, int)
	 */
	public static String randomString(int length) {
		return RandomUtils.random(ALL, length);
	}

	/**
	 * 生成一个纯数字的随机字符串（基于 {@link #NUMBERIC_CHARACTER} 字符集）。
	 * <p>
	 * 适用于生成短信验证码、邮箱验证码、随机编号等场景。请注意，生成的字符串可能以'0'开头。
	 *
	 * @param length 随机数字字符串的目标长度，必须大于等于 0。若长度为 0，则返回空字符串。
	 * @return 一个长度为 {@code length} 的纯数字字符串。
	 */
	public static String randomNumber(int length) {
		return RandomUtils.random(NUMBERIC_CHARACTER, length);
	}

	/**
	 * 生成一个由易区分字符组成的随机字符串（基于 {@link #EASY_TO_DISTINGUISH} 字符集）。
	 * <p>
	 * 该方法生成的字符串排除了易混淆字符，能有效避免用户在肉眼识别时产生错误，非常适合用于：
	 * <ul>
	 * <li>图形验证码、扫码登录验证码</li>
	 * <li>临时登录密码、设备激活码</li>
	 * </ul>
	 *
	 * @param length 随机字符串的目标长度，必须大于等于 0。若长度为 0，则返回空字符串。
	 * @return 一个长度为 {@code length} 的易区分随机字符串。
	 */
	public static String randomCode(int length) {
		return RandomUtils.random(EASY_TO_DISTINGUISH, length);
	}

	/**
	 * 将源集合通过指定映射函数转换为非空Set，并过滤掉所有null元素（包括源元素和映射后的元素）。
	 *
	 * @param source 源集合，可为空（为空时返回空Set）
	 * @param mapper 元素映射函数，用于将源元素转换为目标元素，不能为空
	 * @param <S>    源集合元素类型
	 * @param <T>    目标Set元素类型
	 * @return 包含映射后非null元素的Set（使用LinkedHashSet保持源集合的迭代顺序）
	 */
	public static <S, T> Set<T> mapToNonNullSet(Collection<? extends S> source,
			@NonNull Function<? super S, ? extends T> mapper) {
		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptySet();
		}

		return source.stream().filter((e) -> e != null) // 过滤源集合中的null元素
				.map(mapper) // 应用映射函数
				.filter((e) -> e != null) // 过滤映射后的null元素
				.collect(Collectors.toCollection(LinkedHashSet::new)); // 保持顺序的Set
	}

	/**
	 * 批量执行指定的处理逻辑，针对传入的key集合完成批量处理，并汇总执行失败的key结果
	 *
	 * @param <K>       待处理key的泛型类型
	 * @param keys      待处理的key集合，支持任意非空集合类型（若仅含单个key则直接执行，不进入批量逻辑）
	 * @param processor 单个key的处理逻辑函数，入参为K类型的key，返回该key的处理结果Result
	 * @return 批量执行结果： 1. 若所有key处理成功（或仅单个key且处理成功），返回Result.success()； 2.
	 *         若存在处理失败的key，返回Result.error()，错误信息包含所有失败的key列表（格式：Failure list:[key1,
	 *         key2...]）
	 */
	public static <K> Result batchExecution(Collection<K> keys, Function<? super K, ? extends Result> processor) {
		if (keys.size() == 1) {
			return processor.apply(keys.iterator().next());
		}

		List<K> failList = new ArrayList<>();
		for (K key : keys) {
			Result result = processor.apply(key);
			if (!result.isSuccess()) {
				failList.add(key);
			}
		}
		return failList.isEmpty() ? Result.success() : Result.error("Failure list:" + failList);
	}
}