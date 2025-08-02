package run.soeasy.framework.aop;

import java.lang.reflect.Method;

import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * AOP代理工具类，提供代理过程中对基础方法（hashCode、toString、equals）的特殊处理逻辑，
 * 包括方法忽略判断、默认实现调用等，避免代理逻辑干扰对象的基础行为，确保代理对象的基础方法符合预期。
 * 
 * <p>该工具类聚焦于代理场景下的基础方法处理，通过判断方法类型并提供默认实现，
 * 解决代理增强可能导致的hashCode/toString/equals方法异常，保障代理对象的正常使用。
 * 
 * @author soeasy.run
 * @see Method
 * @see ReflectionUtils
 */
@UtilityClass
public class ProxyUtils {

    /**
     * 判断方法是否为需要忽略的基础方法（hashCode、toString、equals）
     * 
     * <p>代理通常无需增强这些基础方法，避免破坏对象的默认行为（如集合中的相等性判断、哈希计算等）。
     * 判断逻辑：方法是否为hashCode、toString或equals中的任意一个。
     * 
     * @param method 待判断的方法
     * @return 是需要忽略的基础方法则返回true，否则返回false
     */
    public static boolean isIgnoreMethod(Method method) {
        return ReflectionUtils.isHashCodeMethod(method) 
                || ReflectionUtils.isToStringMethod(method)
                || ReflectionUtils.isEqualsMethod(method);
    }

    /**
     * 调用hashCode方法的默认实现（代理场景下）
     * 
     * <p>使用{@link System#identityHashCode(Object)}获取对象的身份哈希值，
     * 确保代理对象的hashCode与原始对象的身份哈希一致，避免代理逻辑影响哈希计算。
     * 
     * @param instance 代理的目标实例
     * @param method hashCode方法（通常为{@link Object#hashCode()}）
     * @return 实例的身份哈希值
     */
    public static int invokeHashCode(Object instance, Method method) {
        return System.identityHashCode(instance);
    }

    /**
     * 调用toString方法的默认实现（代理场景下）
     * 
     * <p>生成类似{@link Object#toString()}的字符串：类名@哈希值十六进制，
     * 确保代理对象的toString输出格式统一，避免代理逻辑导致的toString异常。
     * 
     * @param instance 代理的目标实例
     * @param method toString方法（通常为{@link Object#toString()}）
     * @return 包含类名和哈希值的字符串
     */
    public static String invokeToString(Object instance, Method method) {
        return instance.getClass().getName() + "@" + Integer.toHexString(invokeHashCode(instance, method));
    }

    /**
     * 调用equals方法的默认实现（代理场景下）
     * 
     * <p>判断参数对象是否与当前实例为同一对象（引用相等），确保代理对象的equals行为符合基础语义，
     * 避免代理逻辑干扰对象相等性判断。
     * 
     * @param instance 代理的目标实例
     * @param method equals方法（通常为{@link Object#equals(Object)}）
     * @param args 方法参数（应为单个对象参数，代表比较的目标）
     * @return 参数对象与当前实例引用相等则返回true，否则返回false
     */
    public static boolean invokeEquals(Object instance, Method method, Object[] args) {
        Object value = ArrayUtils.isEmpty(args) ? null : args[0];
        if (value == null) {
            return false;
        }
        return value.equals(instance);
    }

    /**
     * 根据方法类型调用对应的忽略方法实现
     * 
     * <p>根据方法是否为hashCode、toString或equals，分别调用对应的默认实现；
     * 若为其他方法，抛出{@link UnsupportedOperationException}。
     * 
     * @param instance 代理的目标实例
     * @param method 待调用的方法
     * @param args 方法参数
     * @return 方法调用结果（hashCode返回int，toString返回String，equals返回boolean）
     * @throws UnsupportedOperationException 当方法不是需要忽略的基础方法时抛出
     */
    public static Object invokeIgnoreMethod(Object instance, Method method, Object[] args) {
        if (ReflectionUtils.isHashCodeMethod(method)) {
            return invokeHashCode(instance, method);
        }

        if (ReflectionUtils.isToStringMethod(method)) {
            return invokeToString(instance, method);
        }

        if (ReflectionUtils.isEqualsMethod(method)) {
            return invokeEquals(instance, method, args);
        }

        throw new UnsupportedOperationException("Unsupported ignore method: " + method.toString());
    }
}