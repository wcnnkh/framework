package run.soeasy.framework.core.type;

import java.lang.reflect.Executable;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.NonNull;

/**
 * 组合式类型变量解析器（基于组合设计模式）
 * <p>
 * 核心职责：聚合多个 {@link TypeVariableResolver} 实现，按「添加顺序」作为优先级依次调度解析， 返回第一个非空、非
 * {@link ResolvableType#NONE} 的有效结果，解决单一解析器无法覆盖多场景的泛型解析需求。
 * <p>
 * 特性说明： 1. 优先级机制：先添加的解析器优先执行，解析成功立即返回，避免无效遍历 2.
 * 不可变性：实例创建后内部解析器列表不可修改，确保线程安全和状态稳定 3. 兼容性：支持任意实现 {@link TypeVariableResolver}
 * 接口的解析器（如类级、方法级、自定义解析器） 4. 容错性：所有子解析器均失败时返回 null，遵循接口约定，便于上层统一兜底处理
 *
 * @author soeasy.run
 * @see TypeVariableResolver
 * @see ResolvableType
 */
public class CompositeTypeVariableResolver implements TypeVariableResolver {

	/**
	 * 存储子解析器的不可变列表，按添加顺序维护优先级（先添加优先级更高）
	 */
	private final List<TypeVariableResolver> resolvers;

	/**
	 * 便捷构造器：接收可变参数形式的子解析器
	 * <p>
	 * 示例：
	 * 
	 * <pre>
	 * // 优先级：methodResolver > classResolver > customResolver
	 * CompositeTypeVariableResolver resolver = new CompositeTypeVariableResolver(methodResolver, classResolver,
	 * 		customResolver);
	 * </pre>
	 *
	 * @param typeVariableResolvers 子解析器数组（不可为 null，支持空数组）
	 */
	public CompositeTypeVariableResolver(@NonNull TypeVariableResolver... typeVariableResolvers) {
		this(Arrays.asList(typeVariableResolvers));
	}

	/**
	 * 灵活构造器：接收列表形式的子解析器
	 * <p>
	 * 注意：内部会对传入列表进行防御性拷贝并转为不可变列表，外部修改原列表不会影响内部状态
	 *
	 * @param resolvers 子解析器列表（不可为 null，支持空列表）
	 */
	public CompositeTypeVariableResolver(@NonNull List<TypeVariableResolver> resolvers) {
		// 防御性拷贝：避免外部列表修改影响内部状态
		List<TypeVariableResolver> copy = new ArrayList<>(resolvers);
		// 不可变封装：确保实例创建后无法修改解析器列表，保障线程安全
		this.resolvers = Collections.unmodifiableList(copy);
	}

	/**
	 * 核心解析逻辑：按优先级依次调用子解析器，返回第一个有效结果
	 * <p>
	 * 执行流程： 1. 按解析器列表顺序（优先级递减）遍历子解析器 2. 调用每个子解析器的
	 * {@link TypeVariableResolver#resolveTypeVariable(TypeVariable)} 方法 3. 若返回结果非
	 * null 且非 {@link ResolvableType#NONE}，立即返回该结果（短路执行） 4. 所有子解析器遍历完毕仍无有效结果，返回 null
	 *
	 * @param typeVariable 待解析的类型变量（不可为 null）
	 * @return 解析后的 {@link ResolvableType}，所有子解析器失败时返回 null
	 */
	@Override
	public ResolvableType resolveTypeVariable(@NonNull TypeVariable<?> typeVariable) {
		// 遍历不可变列表，避免并发修改异常
		for (TypeVariableResolver resolver : resolvers) {
			ResolvableType resolvedType = resolver.resolveTypeVariable(typeVariable);
			// 校验结果有效性：非 null 且非 NONE 视为有效解析结果
			if (resolvedType != null && resolvedType != ResolvableType.NONE) {
				return resolvedType;
			}
		}
		// 所有子解析器均未解析成功，返回 null 交由上层处理
		return null;
	}

	/**
	 * 获取当前聚合的所有子解析器（不可修改）
	 * <p>
	 * 用于日志打印、调试或解析器列表校验，返回的列表不支持 add/remove 等修改操作
	 *
	 * @return 不可变的子解析器列表（不会为 null）
	 */
	public List<TypeVariableResolver> getResolvers() {
		return Collections.unmodifiableList(resolvers);
	}

	/**
	 * 获取子解析器数量
	 *
	 * @return 子解析器个数（≥ 0）
	 */
	public int getResolverCount() {
		return resolvers.size();
	}

	/**
	 * 判断是否包含指定子解析器
	 *
	 * @param resolver 待判断的子解析器（不可为 null）
	 * @return true 表示包含该解析器，false 表示不包含
	 */
	public boolean containsResolver(@NonNull TypeVariableResolver resolver) {
		return resolvers.contains(resolver);
	}

	/**
	 * 为可执行元素（方法/构造器）创建组合式类型变量解析器
	 * <p>
	 * 核心设计：按「方法/构造器泛型优先，类泛型兜底」的优先级聚合解析能力，
	 * 确保可执行元素参数/返回值中的泛型变量能被完整解析（覆盖方法级泛型和类级泛型场景）。
	 * <p>
	 * 解析器优先级（自上而下）：
	 * 1. 方法/构造器自身泛型解析器（{@link TypeParameterTypeVariableResolver}）：解析可执行元素声明的泛型变量（如 {@code <T> T method(T param)} 中的 T）
	 * 2. 声明类泛型解析器（{@link ResolvableType}）：解析可执行元素所在类的泛型变量（如 {@code class Service<T> { T method() {} }} 中的 T）
	 *
	 * @param executable 目标可执行元素（支持 {@link Method} 方法和 {@link Constructor} 构造器，不可为 {@code null}）
	 * @return 聚合了「可执行元素泛型+类泛型」解析能力的 {@link CompositeTypeVariableResolver}，非 {@code null}
	 * @see TypeParameterTypeVariableResolver
	 * @see ResolvableType#forType(Class)
	 * @see CompositeTypeVariableResolver
	 */
	public static CompositeTypeVariableResolver forExecutable(Executable executable) {
	    TypeVariableResolver[] typeVariableResolvers = new TypeVariableResolver[2];
	    // 优先级1：解析可执行元素自身声明的泛型参数（方法/构造器级泛型）
	    typeVariableResolvers[0] = new TypeParameterTypeVariableResolver(executable.getTypeParameters());
	    // 优先级2：解析可执行元素所在类的泛型参数（类级泛型，兜底）
	    typeVariableResolvers[1] = ResolvableType.forType(executable.getDeclaringClass());
	    return new CompositeTypeVariableResolver(typeVariableResolvers);
	}
}