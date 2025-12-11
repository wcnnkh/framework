package run.soeasy.framework.core;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * 对象工具类，提供对象操作的通用静态方法，包括对象相等性比较、哈希计算、
 * 对象转字符串以及资源关闭等功能。该类遵循空安全设计原则，所有方法都能妥善处理null输入。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>空安全：所有方法均支持null参数输入，不会抛出NullPointerException</li>
 * <li>深度处理：支持深度比较、深度哈希计算和深度字符串转换</li>
 * <li>数组支持：自动处理数组类型，提供数组元素级别的操作</li>
 * <li>资源管理：提供安全关闭AutoCloseable资源的方法</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>对象相等性比较，尤其是包含数组的复杂对象</li>
 * <li>集合元素的哈希计算，确保数组元素的正确哈希</li>
 * <li>对象调试信息输出，生成包含数组内容的字符串表示</li>
 * <li>资源释放，安全关闭多个AutoCloseable资源</li>
 * </ul>
 *
 * @see ArrayUtils
 * @see CollectionUtils
 */
@UtilityClass
public class ObjectUtils {
	private static Logger logger = Logger.getLogger(ObjectUtils.class.getName());

	/**
	 * 空对象数组常量，用于避免重复创建空数组实例。
	 */
	public static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * 判断对象是否为数组类型。
	 * <p>
	 * 该方法会检查对象是否非空且其Class是否表示数组类型。
	 *
	 * @param obj 待检查的对象
	 * @return 如果对象是数组返回true，否则返回false
	 */
	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}

	/**
	 * 将对象转换为字符串表示，支持深度转换。
	 * <p>
	 * 处理逻辑：
	 * <ul>
	 * <li>如果对象为null，返回null</li>
	 * <li>如果对象是数组，调用{@link ArrayUtils#toString}进行深度转换</li>
	 * <li>否则，调用对象自身的toString()方法</li>
	 * </ul>
	 *
	 * @param source 待转换的对象
	 * @param deep   是否进行深度转换（对数组元素递归处理）
	 * @return 对象的字符串表示，可能为null
	 */
	public static String toString(Object source, boolean deep) {
		if (source == null) {
			return null;
		} else if (source.getClass().isArray()) {
			return ArrayUtils.toString(source, deep);
		} else {
			return source.toString();
		}
	}

	/**
	 * 将对象转换为字符串表示，默认启用深度转换。
	 * <p>
	 * 等价于调用{@link #toString(Object, boolean)}并传入true。
	 *
	 * @param source 待转换的对象
	 * @return 对象的字符串表示，可能为null
	 */
	public static String toString(Object source) {
		return toString(source, true);
	}

	/**
	 * 计算对象的哈希值，支持深度计算。
	 * <p>
	 * 处理逻辑：
	 * <ul>
	 * <li>如果对象为null，返回0</li>
	 * <li>如果对象是数组，调用{@link ArrayUtils#hashCode}进行深度计算</li>
	 * <li>否则，调用对象自身的hashCode()方法</li>
	 * </ul>
	 *
	 * @param source 待计算哈希值的对象
	 * @param deep   是否进行深度计算（对数组元素递归处理）
	 * @return 对象的哈希值
	 */
	public static int hashCode(Object source, boolean deep) {
		if (source == null) {
			return 0;
		} else if (source.getClass().isArray()) {
			return ArrayUtils.hashCode(source, deep);
		} else {
			return source.hashCode();
		}
	}

	/**
	 * 计算对象的哈希值，默认启用深度计算。
	 * <p>
	 * 等价于调用{@link #hashCode(Object, boolean)}并传入true。
	 *
	 * @param source 待计算哈希值的对象
	 * @return 对象的哈希值
	 */
	public static int hashCode(Object source) {
		return hashCode(source, true);
	}

	/**
	 * 判断两个对象是否相等，支持深度比较。
	 * <p>
	 * 比较逻辑：
	 * <ol>
	 * <li>如果两个对象引用相同，返回true</li>
	 * <li>如果任一对象为null，返回false</li>
	 * <li>调用对象的equals()方法，如果返回true则相等</li>
	 * <li>如果两个对象都是数组，调用{@link ArrayUtils#equals}进行深度比较</li>
	 * <li>否则返回false</li>
	 * </ol>
	 *
	 * @param left  左操作数
	 * @param right 右操作数
	 * @param deep  是否进行深度比较（对数组元素递归处理）
	 * @return 如果对象相等返回true，否则返回false
	 */
	public static boolean equals(Object left, Object right, boolean deep) {
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		if (left.equals(right)) {
			return true;
		}

		if (left.getClass().isArray() && right.getClass().isArray()) {
			return ArrayUtils.equals(left, right, deep);
		}
		return false;
	}

	/**
	 * 判断两个对象是否相等，默认启用深度比较。
	 * <p>
	 * 等价于调用{@link #equals(Object, Object, boolean)}并传入true。
	 *
	 * @param left  左操作数
	 * @param right 右操作数
	 * @return 如果对象相等返回true，否则返回false
	 */
	public static boolean equals(Object left, Object right) {
		return equals(left, right, true);
	}

	/**
	 * 安全关闭多个AutoCloseable资源，遇到异常时抛出。
	 * <p>
	 * 该方法会按顺序关闭所有资源，若任一资源关闭失败， 则抛出异常并继续尝试关闭后续资源。
	 *
	 * @param autoCloseables 待关闭的资源数组
	 * @throws Exception 如果关闭过程中发生异常
	 */
	public static void close(AutoCloseable... autoCloseables) throws Exception {
		CollectionUtils.acceptAll(Arrays.asList(autoCloseables), (e) -> {
			if (e == null) {
				return;
			}
			e.close();
		});
	}

	/**
	 * 安静地关闭多个AutoCloseable资源，忽略所有异常。
	 * <p>
	 * 该方法会按顺序关闭所有资源，若任一资源关闭失败， 会捕获异常并继续尝试关闭后续资源。
	 *
	 * @param autoCloseables 待关闭的资源数组
	 */
	public static void closeQuietly(AutoCloseable... autoCloseables) {
		if (autoCloseables == null) {
			return;
		}

		for (AutoCloseable autoCloseable : autoCloseables) {
			if (autoCloseable == null) {
				continue;
			}

			try {
				autoCloseable.close();
			} catch (final Throwable e) {
				logger.log(Level.FINEST, e, () -> autoCloseable.toString());
			}
		}
	}
}