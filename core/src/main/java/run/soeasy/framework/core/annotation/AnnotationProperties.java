package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 注解属性接口，继承自{@link PropertyMapping}和{@link InvocationHandler}，
 * 提供注解属性的动态访问能力和合成动态注解的功能，是框架中处理动态注解的核心抽象。
 * <p>
 * 该接口通过代理模式将属性操作转换为注解的方法调用，支持基于属性集合动态生成注解实例，
 * 适用于需要在运行时动态构造注解的场景，如测试框架、动态代理、注解处理器等。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>属性-注解映射：将属性集合转换为注解的方法调用</li>
 * <li>动态合成：通过{@link #synthesize()}方法生成代理注解实例</li>
 * <li>类型安全：通过泛型约束确保合成注解的类型一致性</li>
 * <li>代理处理：实现InvocationHandler接口，处理注解方法的调用</li>
 * </ul>
 *
 * <p>
 * <b>泛型说明：</b>
 * <ul>
 * <li>{@code A}：合成的注解类型，必须是{@link Annotation}的子类</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>动态注解生成：在运行时根据配置生成自定义注解</li>
 * <li>测试框架：动态构造测试所需的注解</li>
 * <li>代理增强：为代理对象添加动态注解</li>
 * <li>注解处理器：动态处理和生成注解实例</li>
 * <li>元编程：基于属性集合动态生成注解元数据</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMapping
 * @see InvocationHandler
 * @see Proxy
 */
public interface AnnotationProperties<A extends Annotation>
		extends PropertyMapping<PropertyAccessor>, InvocationHandler {

	/**
	 * 获取合成注解的类型
	 * 
	 * @return 注解的Class对象，如{@code MyAnnotation.class}
	 */
	Class<A> getType();

	/**
	 * 处理代理方法的调用（InvocationHandler实现）
	 * <p>
	 * 该方法实现了注解代理的核心逻辑，处理以下类型的调用：
	 * <ol>
	 * <li>Object类方法：equals/hashCode/toString</li>
	 * <li>注解元方法：annotationType()</li>
	 * <li>注解属性方法：通过属性名称获取对应值</li>
	 * </ol>
	 * 
	 * @param proxy  代理实例
	 * @param method 被调用的方法
	 * @param args   方法参数
	 * @return 方法调用结果
	 * @throws Throwable 调用过程中抛出的异常
	 */
	@Override
	default Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (ReflectionUtils.isEqualsMethod(method)) {
			return equals(args[0]);
		}
		if (ReflectionUtils.isHashCodeMethod(method)) {
			return hashCode();
		}
		if (ReflectionUtils.isToStringMethod(method)) {
			return toString();
		}

		if (method.getName().equals("annotationType") && method.getParameterCount() == 0) {
			return getType();
		}

		if (hasKey(method.getName())) {
			return getValues(method.getName()).findFirst()
					.map((typedValue) -> typedValue
							.map(TypeDescriptor.forExecutableReturnType(method), Converter.assignable()).get())
					.orElse(null);
		}
		throw new IllegalArgumentException(
				String.format("Method [%s] is unsupported for synthesized annotation type [%s]", method, getType()));
	}

	/**
	 * 合成动态注解实例
	 * <p>
	 * 该方法使用Java动态代理生成实现指定注解类型的代理实例， 代理的方法调用将委托给当前{@link AnnotationProperties}实现，
	 * 从而实现基于属性集合的动态注解。
	 * 
	 * @return 动态合成的注解实例
	 */
	@SuppressWarnings("unchecked")
	default A synthesize() {
		Class<A> type = getType();
		ClassLoader classLoader = type.getClassLoader();
		return (A) Proxy.newProxyInstance(classLoader, new Class<?>[] { type, SynthesizedAnnotation.class }, this);
	}
}