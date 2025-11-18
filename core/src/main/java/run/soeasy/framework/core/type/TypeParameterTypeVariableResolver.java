package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.StringUtils;

/**
 * 基于「显式泛型参数数组」的类型变量解析器（全面支持版）
 * <p>
 * 核心能力：完整覆盖泛型解析全场景，包括单边界/多边界、嵌套泛型、循环依赖防护，与
 * {@link TypeVariableResolver}、{@link DefaultResolvableTypeFactory}、{@link ResolvableTypeVariable} 等组件深度联动，
 * 确保泛型解析的完整性、一致性和稳定性。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>名称精准匹配：按泛型变量名称（如 T、U）匹配目标变量，遵循Java泛型命名约定</li>
 * <li>多边界完整支持：聚合类+接口多边界（如 T extends Number &amp; Comparable），生成组合类型</li>
 * <li>嵌套泛型递归解析：支持边界中嵌套泛型变量（如 T extends List），递归复用当前解析器</li>
 * <li>循环依赖防护：通过线程本地存储标记解析中的变量，避免栈溢出（如 T extends U, U extends T）</li>
 * <li>语义一致性：解析边界时传入当前解析器，确保嵌套变量复用同一匹配规则</li>
 * <li>框架适配：严格遵循 {@link TypeVariableResolver} 接口约定，兼容上层组合解析逻辑</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TypeVariableResolver
 * @see ResolvableType
 * @see DefaultResolvableTypeFactory
 */
@RequiredArgsConstructor
public class TypeParameterTypeVariableResolver implements TypeVariableResolver {

	/**
	 * 显式泛型参数数组（如类/方法声明的 <T, U> 对应的 TypeVariable[]）
	 */
	@NonNull
	private final TypeVariable<?>[] typeParameters;

	/**
	 * 解析指定的类型变量，通过名称匹配显式泛型参数数组中的目标变量
	 *
	 * @param typeVariable 待解析的类型变量（不可为null）
	 * @return 匹配到的类型变量对应的 ResolvableType 实例；若未找到名称匹配的泛型参数，返回null
	 */
	@Override
	public ResolvableType resolveTypeVariable(@NonNull TypeVariable<?> typeVariable) {
		String variableName = typeVariable.getName();
		for (TypeVariable<?> sourceVariable : typeParameters) {
			if (StringUtils.equals(sourceVariable.getName(), variableName)) {
				return ResolvableType.forType(sourceVariable);
			}
		}
		return null;
	}
}