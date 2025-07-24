package run.soeasy.framework.core;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * 断言工具类，用于验证方法参数和程序状态，在运行时尽早识别程序错误。
 * 提供一系列静态方法用于检查参数合法性，当条件不满足时抛出相应异常，
 * 帮助开发者在开发阶段明确识别契约违反和程序错误。
 *
 * <p>核心特性：
 * <ul>
 *   <li>参数验证：验证方法参数的合法性，如非空、长度、类型等</li>
 *   <li>状态检查：验证程序状态不变量，确保系统状态合法</li>
 *   <li>清晰报错：抛出带有明确错误信息的异常，便于问题定位</li>
 *   <li>静态易用：所有方法均为静态方法，可直接通过类名调用</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>公共方法参数验证，确保方法契约得到遵守</li>
 *   <li>类内部状态检查，维护对象不变量</li>
 *   <li>配置初始化验证，确保系统配置合法</li>
 *   <li>控制流断言，验证程序执行路径合法性</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 验证参数非空
 * Assert.notNull(parameter, "参数不能为null");
 * 
 * // 验证字符串长度
 * Assert.hasLength(name, "名称不能为空");
 * 
 * // 验证类型兼容性
 * Assert.isAssignable(Number.class, myClass, "类型不兼容");
 * </pre>
 *
 * @see CollectionUtils
 */
@UtilityClass
public class Assert {

    /**
     * 断言字符串非空：检查字符串既不为null也不为空字符串。
     * <p>
     * 等价于调用{@link #hasLength(String, String)}并使用默认错误信息。
     *
     * @param text 待检查的字符串
     * @throws IllegalArgumentException 如果字符串为null或空字符串
     * @see #hasLength(String, String)
     */
    public static void hasLength(String text) {
        hasLength(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * 断言字符串非空：检查字符串既不为null也不为空字符串。
     *
     * @param text    待检查的字符串
     * @param message 断言失败时抛出的异常信息
     * @throws IllegalArgumentException 如果字符串为null或空字符串
     * @see StringUtils#isEmpty(CharSequence)
     */
    public static void hasLength(String text, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串包含有效文本：检查字符串既不为null，也不为空或仅包含空白字符。
     * <p>
     * 等价于调用{@link #hasText(String, String)}并使用默认错误信息。
     *
     * @param text 待检查的字符串
     * @throws IllegalArgumentException 如果字符串为null、空或仅包含空白字符
     * @see #hasText(String, String)
     * @see StringUtils#hasText
     */
    public static void hasText(String text) {
        hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 断言字符串包含有效文本：检查字符串既不为null，也不为空或仅包含空白字符。
     *
     * @param text    待检查的字符串
     * @param message 断言失败时抛出的异常信息
     * @throws IllegalArgumentException 如果字符串为null、空或仅包含空白字符
     * @see StringUtils#hasText
     */
    public static void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言类型可赋值：检查子类是否可以赋值给父类（{@code superType.isAssignableFrom(subType)}为true）。
     *
     * @param superType 父类型
     * @param subType   子类型
     * @throws IllegalArgumentException 如果子类型为null或不可赋值给父类型
     */
    public static void isAssignable(Class<?> superType, Class<?> subType) {
        isAssignable(superType, subType, "");
    }

    /**
     * 断言类型可赋值：检查子类是否可以赋值给父类（{@code superType.isAssignableFrom(subType)}为true）。
     *
     * @param superType      父类型，不可为null
     * @param subType        子类型
     * @param messageSupplier 错误信息供应者，用于生成动态错误信息
     * @throws IllegalArgumentException 如果子类型为null或不可赋值给父类型
     */
    public static void isAssignable(@NonNull Class<?> superType, Class<?> subType,
                                    java.util.function.Supplier<String> messageSupplier) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(messageSupplier.get() + subType + " is not assignable to " + superType);
        }
    }

    /**
     * 断言类型可赋值：检查子类是否可以赋值给父类（{@code superType.isAssignableFrom(subType)}为true）。
     *
     * @param superType 父类型，不可为null
     * @param subType   子类型
     * @param message   错误信息前缀，用于提供上下文
     * @throws IllegalArgumentException 如果子类型为null或不可赋值给父类型
     */
    public static void isAssignable(@NonNull Class<?> superType, Class<?> subType, String message) {
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
        }
    }

    /**
     * 断言对象是指定类型的实例：检查对象是否是指定类的实例（{@code clazz.isInstance(obj)}为true）。
     *
     * @param clazz 目标类型
     * @param obj   待检查的对象
     * @throws IllegalArgumentException 如果对象不是指定类型的实例
     * @see Class#isInstance
     */
    public static void isInstanceOf(Class<?> clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }

    /**
     * 断言对象是指定类型的实例：检查对象是否是指定类的实例（{@code type.isInstance(obj)}为true）。
     *
     * @param type    目标类型，不可为null
     * @param obj     待检查的对象
     * @param message 错误信息前缀，用于提供上下文
     * @throws IllegalArgumentException 如果对象不是指定类型的实例
     * @see Class#isInstance
     */
    public static void isInstanceOf(@NonNull Class<?> type, Object obj, String message) {
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException((StringUtils.isEmpty(message) ? message + " " : "") + "Object of class ["
                    + (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
        }
    }

    /**
     * 断言条件为真：如果条件为false则抛出异常。
     * <p>
     * 等价于调用{@link #isTrue(boolean, String)}并使用默认错误信息。
     *
     * @param expression 待验证的布尔表达式
     * @throws IllegalArgumentException 如果表达式为false
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * 断言条件为真：如果条件为false则抛出异常。
     *
     * @param expression 待验证的布尔表达式
     * @param message    断言失败时抛出的异常信息
     * @throws IllegalArgumentException 如果表达式为false
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言条件为真：如果条件为false则抛出异常，错误信息由供应者动态生成。
     *
     * @param expression       待验证的布尔表达式
     * @param messageSupplier 错误信息供应者
     * @throws IllegalArgumentException 如果表达式为false
     */
    public static void isTrue(boolean expression, @NonNull Supplier<? extends String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

    /**
     * 断言集合非空：检查集合既不为null也不为空（至少包含一个元素）。
     * <p>
     * 等价于调用{@link #notEmpty(Collection, String)}并使用默认错误信息。
     *
     * @param collection 待检查的集合
     * @throws IllegalArgumentException 如果集合为null或为空
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Collection collection) {
        notEmpty(collection,
                "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    /**
     * 断言集合非空：检查集合既不为null也不为空（至少包含一个元素）。
     *
     * @param collection 待检查的集合
     * @param message    断言失败时抛出的异常信息
     * @throws IllegalArgumentException 如果集合为null或为空
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Collection collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言Map非空：检查Map既不为null也不为空（至少包含一个条目）。
     * <p>
     * 等价于调用{@link #notEmpty(Map, String)}并使用默认错误信息。
     *
     * @param map 待检查的Map
     * @throws IllegalArgumentException 如果Map为null或为空
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Map map) {
        notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    /**
     * 断言Map非空：检查Map既不为null也不为空（至少包含一个条目）。
     *
     * @param map     待检查的Map
     * @param message 断言失败时抛出的异常信息
     * @throws IllegalArgumentException 如果Map为null或为空
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Map map, String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象非空：检查对象不为null，否则抛出异常并返回对象本身。
     * <p>
     * 等价于调用{@link #notNull(Object, String)}并使用默认错误信息。
     *
     * @param object 待检查的对象
     * @param <T>    对象类型
     * @return 非空的对象本身
     * @throws IllegalArgumentException 如果对象为null
     */
    public static <T> T notNull(T object) {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    /**
     * 断言对象非空：检查对象不为null，否则抛出异常并返回对象本身。
     *
     * @param object  待检查的对象
     * @param message 断言失败时抛出的异常信息
     * @param <T>     对象类型
     * @return 非空的对象本身
     * @throws IllegalArgumentException 如果对象为null
     */
    public static <T> T notNull(T object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
        return object;
    }

    /**
     * 断言程序状态合法：如果条件为false则抛出IllegalStateException。
     * <p>
     * 与{@link #isTrue(boolean)}的区别在于抛出不同的异常类型，
     * 用于验证程序内部状态而非方法参数。
     *
     * @param expression 待验证的布尔表达式
     * @throws IllegalStateException 如果表达式为false
     */
    public static void state(boolean expression) {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }

    /**
     * 断言程序状态合法：如果条件为false则抛出IllegalStateException。
     *
     * @param expression 待验证的布尔表达式
     * @param message    断言失败时抛出的异常信息
     * @throws IllegalStateException 如果表达式为false
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
}