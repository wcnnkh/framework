package run.soeasy.framework.core.comparator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import run.soeasy.framework.core.type.ResolvableType;

/**
 * Executable 排序器（基于参数类型匹配优先级，复用 MultiParameterMatchComparator 逻辑）
 * 支持：方法（Method）、构造器（Constructor）的排序，核心按「参数与目标类型的匹配度」排序
 * 
 * @author soeasy.run
 */
public class ExecutableMatchComparator<E extends Executable> implements Comparator<E> {

	/**
	 * 目标参数类型列表（用于匹配 Executable 的参数）
	 */
	private final List<ResolvableType> targetParameterTypes;

	/**
	 * 复用参数列表比较器（核心匹配逻辑）
	 */
	private final MultiParameterMatchComparator paramListComparator;

	// ------------------------------ 构造器 ------------------------------
	/**
	 * 构造器：指定目标参数类型（可变参数，兼容无参场景）
	 * 
	 * @param targetParameterTypes 期望匹配的参数类型（如 String.class, int.class）
	 */
	public ExecutableMatchComparator(Class<?>... targetParameterTypes) {
		this.targetParameterTypes = convertToResolvableTypes(targetParameterTypes);
		this.paramListComparator = MultiParameterMatchComparator.of(this.targetParameterTypes);
	}

	/**
	 * 构造器：指定目标参数类型（ResolvableType 列表，支持泛型/数组等复杂类型）
	 * 
	 * @param targetParameterTypes 期望匹配的参数类型列表（如 ResolvableType.forClass(List.class,
	 *                             String.class)）
	 */
	public ExecutableMatchComparator(List<ResolvableType> targetParameterTypes) {
		this.targetParameterTypes = new ArrayList<>(targetParameterTypes);
		this.paramListComparator = MultiParameterMatchComparator.of(this.targetParameterTypes);
	}

	/**
	 * 构造器：支持自定义单个类型匹配规则（扩展用）
	 * 
	 * @param targetParameterTypes 目标参数类型列表
	 * @param typeMatchScorer      自定义单个类型匹配得分计算器
	 */
	public ExecutableMatchComparator(List<ResolvableType> targetParameterTypes,
			MultiParameterMatchComparator.SingleTypeMatchScorer typeMatchScorer) {
		this.targetParameterTypes = new ArrayList<>(targetParameterTypes);
		this.paramListComparator = new MultiParameterMatchComparator(targetParameterTypes, typeMatchScorer);
	}

	// ------------------------------ 核心比较逻辑 ------------------------------
	@Override
	public int compare(E executableA, E executableB) {
		// 1. 空值处理（null 排在最后）
		if (executableA == null && executableB == null) {
			return 0;
		}
		if (executableA == null) {
			return 1;
		}
		if (executableB == null) {
			return -1;
		}

		// 2. 提取两个 Executable 的参数类型列表（转为 ResolvableType）
		List<ResolvableType> paramsA = extractExecutableParamTypes(executableA);
		List<ResolvableType> paramsB = extractExecutableParamTypes(executableB);

		// 3. 核心：用已有比较器比较参数列表（决定排序优先级）
		int paramCompareResult = paramListComparator.compare(paramsA, paramsB);
		if (paramCompareResult != 0) {
			return paramCompareResult;
		}

		// 4. 兜底排序：保证稳定性（参数匹配度相同时的排序规则）
		// 4.1 先按「是否是构造器」排序（构造器 > 方法，可选，可根据业务调整）
		boolean isConstructorA = executableA instanceof Constructor;
		boolean isConstructorB = executableB instanceof Constructor;
		if (isConstructorA != isConstructorB) {
			return isConstructorA ? -1 : 1; // 构造器优先
		}

		// 4.2 再按方法名排序（构造器无名字，跳过）
		if (executableA instanceof Method && executableB instanceof Method) {
			String methodNameA = ((Method) executableA).getName();
			String methodNameB = ((Method) executableB).getName();
			int nameCompare = methodNameA.compareTo(methodNameB);
			if (nameCompare != 0) {
				return nameCompare;
			}
		}

		// 4.3 最后按参数类型名字典序（最终兜底，避免排序不稳定）
		String paramStrA = paramsA.stream().map(type -> type != null ? type.getTypeName() : "")
				.reduce((s1, s2) -> s1 + "," + s2).orElse("");
		String paramStrB = paramsB.stream().map(type -> type != null ? type.getTypeName() : "")
				.reduce((s1, s2) -> s1 + "," + s2).orElse("");
		return paramStrA.compareTo(paramStrB);
	}

	// ------------------------------ 工具方法 ------------------------------
	/**
	 * 提取 Executable 的参数类型，转为 ResolvableType 列表（支持泛型、数组、基本类型）
	 */
	private List<ResolvableType> extractExecutableParamTypes(Executable executable) {
		List<ResolvableType> paramTypes = new ArrayList<>();
		// 1. 获取参数类型 Class 数组
		Class<?>[] paramClasses = executable.getParameterTypes();
		// 2. 获取参数泛型信息（Method 支持泛型，Constructor 也支持）
		ResolvableType[] genericParamTypes = ResolvableType.toResolvableTypes(null,
				executable.getGenericParameterTypes());

		// 3. 转换为 ResolvableType（优先用泛型信息，无泛型则用 Class）
		for (int i = 0; i < paramClasses.length; i++) {
			ResolvableType paramType = (i < genericParamTypes.length) ? genericParamTypes[i]
					: ResolvableType.forType(paramClasses[i]);
			paramTypes.add(paramType);
		}
		return paramTypes;
	}

	/**
	 * 将 Class 数组转为 ResolvableType 列表（支持基本类型、包装类、普通类）
	 */
	private List<ResolvableType> convertToResolvableTypes(Class<?>... targetClasses) {
		List<ResolvableType> resolvableTypes = new ArrayList<>();
		if (targetClasses == null || targetClasses.length == 0) {
			return resolvableTypes;
		}
		for (Class<?> clazz : targetClasses) {
			resolvableTypes.add(ResolvableType.forType(clazz));
		}
		return resolvableTypes;
	}

	// ------------------------------ 静态工厂方法（快速创建） ------------------------------
	/**
	 * 快速创建：目标参数类型为 Class 数组（简化无泛型场景）
	 */
	public static <T extends Executable> ExecutableMatchComparator<T> of(Class<?>... targetParameterTypes) {
		return new ExecutableMatchComparator<>(targetParameterTypes);
	}

	/**
	 * 快速创建：目标参数类型为 ResolvableType 列表（支持复杂类型）
	 */
	public static <T extends Executable> ExecutableMatchComparator<T> of(List<ResolvableType> targetParameterTypes) {
		return new ExecutableMatchComparator<>(targetParameterTypes);
	}

	/**
	 * 快速创建：支持自定义类型匹配规则
	 */
	public static <T extends Executable> ExecutableMatchComparator<T> of(List<ResolvableType> targetParameterTypes,
			MultiParameterMatchComparator.SingleTypeMatchScorer typeMatchScorer) {
		return new ExecutableMatchComparator<T>(targetParameterTypes, typeMatchScorer);
	}
}