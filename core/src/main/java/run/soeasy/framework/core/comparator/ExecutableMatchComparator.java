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
 * <p>
 * 核心功能：对 {@link Method} 或 {@link Constructor} 按「参数与目标类型的匹配度」进行排序，匹配度越高的 Executable 排序越靠前。
 * 排序优先级（从高到低）：
 * 1. 空值兜底：null 始终排在最后
 * 2. 参数匹配度：核心优先级，由 {@link MultiParameterMatchComparator} 计算匹配得分
 * 3. 构造器优先：参数匹配度相同时，构造器优先级高于普通方法
 * 4. 方法名排序：同为方法时，按方法名字典序排序
 * 5. 参数类型名排序：最终兜底，按参数类型名字典序排序，保证排序稳定性
 * </p>
 * 支持场景：
 * - 简单类型匹配（基本类型、包装类、普通Class）
 * - 复杂类型匹配（泛型、数组、嵌套泛型等，依赖 {@link ResolvableType} 解析）
 * - 自定义匹配规则（通过 {@link MultiParameterMatchComparator.SingleTypeMatchScorer} 扩展）
 *
 * @author soeasy.run
 * @param <E> 泛型约束，必须是 {@link Executable} 子类（Method 或 Constructor）
 */
public class ExecutableMatchComparator<E extends Executable> implements Comparator<E> {

    /**
     * 目标参数类型列表（用于匹配 Executable 的参数，支持泛型、数组等复杂类型）
     */
    private final List<ResolvableType> targetParameterTypes;

    /**
     * 复用参数列表比较器（核心匹配逻辑，负责计算参数列表与目标类型的匹配度）
     */
    private final MultiParameterMatchComparator paramListComparator;

    // ------------------------------ 构造器 ------------------------------

    /**
     * 构造器：指定目标参数类型（可变参数，兼容无参场景）
     * <p>适用于简单类型匹配场景，无需处理泛型、数组等复杂类型</p>
     *
     * @param targetParameterTypes 期望匹配的参数类型数组（如 String.class, int.class, List.class），支持空数组（无参匹配）
     */
    public ExecutableMatchComparator(Class<?>... targetParameterTypes) {
        this.targetParameterTypes = convertToResolvableTypes(targetParameterTypes);
        this.paramListComparator = MultiParameterMatchComparator.of(this.targetParameterTypes);
    }

    /**
     * 构造器：指定目标参数类型（ResolvableType 列表，支持泛型/数组等复杂类型）
     * <p>适用于需要精确匹配泛型、数组的场景（如 List&lt;String&gt;、String[]、Map&lt;String, List&lt;Integer&gt;&gt;）</p>
     *
     * @param targetParameterTypes 期望匹配的参数类型列表，通过 {@link ResolvableType#forClassWithGenerics(Class, java.lang.reflect.Type...)} 构建
     *                             示例：ResolvableType.forClassWithGenerics(List.class, String.class) 表示 List&lt;String&gt;
     * @throws NullPointerException 若 targetParameterTypes 为 null
     */
    public ExecutableMatchComparator(List<ResolvableType> targetParameterTypes) {
        this.targetParameterTypes = new ArrayList<>(targetParameterTypes);
        this.paramListComparator = MultiParameterMatchComparator.of(this.targetParameterTypes);
    }

    /**
     * 构造器：支持自定义单个类型匹配规则（扩展用）
     * <p>适用于需要自定义类型匹配逻辑的场景（如枚举类型优先、子类优先于父类等）</p>
     *
     * @param targetParameterTypes 期望匹配的参数类型列表（支持复杂类型）
     * @param typeMatchScorer      自定义单个类型匹配得分计算器，实现 {@link MultiParameterMatchComparator.SingleTypeMatchScorer} 接口
     *                             用于自定义单个参数类型的匹配规则（返回得分越高，匹配度越高）
     * @throws NullPointerException 若 targetParameterTypes 或 typeMatchScorer 为 null
     */
    public ExecutableMatchComparator(List<ResolvableType> targetParameterTypes,
                                     MultiParameterMatchComparator.SingleTypeMatchScorer typeMatchScorer) {
        this.targetParameterTypes = new ArrayList<>(targetParameterTypes);
        this.paramListComparator = new MultiParameterMatchComparator(targetParameterTypes, typeMatchScorer);
    }

    // ------------------------------ 核心比较逻辑 ------------------------------

    /**
     * 比较两个 Executable 的优先级，按「排序优先级」规则返回比较结果
     *
     * @param executableA 待比较的第一个 Executable（Method/Constructor）
     * @param executableB 待比较的第二个 Executable（Method/Constructor）
     * @return 负整数：executableA 优先级高于 executableB（排在前面）；
     * 零：两者优先级相同；
     * 正整数：executableA 优先级低于 executableB（排在后面）
     */
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

        // 2. 提取两个 Executable 的参数类型列表（转为 ResolvableType，统一处理泛型/数组）
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
     * <p>处理逻辑：</p>
     * 1. 先获取参数的原始 Class 数组（保证基本类型、普通类的兼容性）
     * 2. 再获取参数的泛型信息（GenericParameterTypes），转为 ResolvableType 数组
     * 3. 优先使用泛型解析结果，无泛型信息时使用原始 Class 构建 ResolvableType
     *
     * @param executable 待提取参数类型的 Executable（Method/Constructor）
     * @return 参数类型对应的 ResolvableType 列表，空参数返回空列表
     */
    private List<ResolvableType> extractExecutableParamTypes(Executable executable) {
        List<ResolvableType> paramTypes = new ArrayList<>();
        // 1. 获取参数类型 Class 数组（基本类型、普通类均适用）
        Class<?>[] paramClasses = executable.getParameterTypes();
        // 2. 获取参数泛型信息（Method 支持泛型，Constructor 也支持泛型）
        ResolvableType[] genericParamTypes = ResolvableType.forExecutableParameterTypes(executable);

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
     * <p>用于简化简单类型场景的参数转换，无需手动构建 ResolvableType</p>
     *
     * @param targetClasses 待转换的 Class 数组（可null或空数组）
     * @return 转换后的 ResolvableType 列表，输入为 null 或空数组时返回空列表
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
     * <p>同 {@link #ExecutableMatchComparator(Class...)}，提供更简洁的创建方式</p>
     *
     * @param targetParameterTypes 期望匹配的参数类型数组（支持简单类型）
     * @param <T>                  泛型约束，Executable 子类（Method/Constructor）
     * @return ExecutableMatchComparator 实例
     */
    public static <T extends Executable> ExecutableMatchComparator<T> of(Class<?>... targetParameterTypes) {
        return new ExecutableMatchComparator<>(targetParameterTypes);
    }

    /**
     * 快速创建：目标参数类型为 ResolvableType 列表（支持复杂类型）
     * <p>同 {@link #ExecutableMatchComparator(List)}，提供更简洁的创建方式</p>
     *
     * @param targetParameterTypes 期望匹配的参数类型列表（支持泛型、数组等复杂类型）
     * @param <T>                  泛型约束，Executable 子类（Method/Constructor）
     * @return ExecutableMatchComparator 实例
     * @throws NullPointerException 若 targetParameterTypes 为 null
     */
    public static <T extends Executable> ExecutableMatchComparator<T> of(List<ResolvableType> targetParameterTypes) {
        return new ExecutableMatchComparator<>(targetParameterTypes);
    }

    /**
     * 快速创建：支持自定义类型匹配规则
     * <p>同 {@link #ExecutableMatchComparator(List, MultiParameterMatchComparator.SingleTypeMatchScorer)}，提供更简洁的创建方式</p>
     *
     * @param targetParameterTypes 期望匹配的参数类型列表（支持复杂类型）
     * @param typeMatchScorer      自定义单个类型匹配得分计算器
     * @param <T>                  泛型约束，Executable 子类（Method/Constructor）
     * @return ExecutableMatchComparator 实例
     * @throws NullPointerException 若 targetParameterTypes 或 typeMatchScorer 为 null
     */
    public static <T extends Executable> ExecutableMatchComparator<T> of(List<ResolvableType> targetParameterTypes,
                                                                        MultiParameterMatchComparator.SingleTypeMatchScorer typeMatchScorer) {
        return new ExecutableMatchComparator<>(targetParameterTypes, typeMatchScorer);
    }
}