package run.soeasy.framework.core.comparator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.type.ClassUtils;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 多参数类型匹配比较器
 * 核心能力：基于参数类型匹配度排序，支持泛型、数组、基本类型/包装类、高精度数值等复杂场景
 * 核心优势：通用兼容所有 List 实现、无性能隐患、线程安全、支持动态列表长度
 */
public class MultiParameterMatchComparator implements Comparator<List<ResolvableType>> {

    /** 目标参数类型列表（不可变包装，保证线程安全和动态长度准确性） */
    private final List<ResolvableType> targetParameterTypes;

    /** 单个类型匹配得分计算器 */
    private final SingleTypeMatchScorer typeMatchScorer;

    // ------------------------------ 构造器 ------------------------------
    /**
     * 构造器：指定目标参数类型列表
     * @param targetParameterTypes 期望匹配的参数类型列表（不可为null，支持空列表=无参场景）
     */
    public MultiParameterMatchComparator(@NonNull List<ResolvableType> targetParameterTypes) {
        this.targetParameterTypes = Collections.unmodifiableList(targetParameterTypes);
        this.typeMatchScorer = new SingleTypeMatchScorer();
    }

    /**
     * 构造器：支持自定义单个类型匹配规则
     * @param targetParameterTypes 目标参数类型列表
     * @param typeMatchScorer 自定义得分计算器
     */
    public MultiParameterMatchComparator(
            @NonNull List<ResolvableType> targetParameterTypes,
            @NonNull SingleTypeMatchScorer typeMatchScorer) {
        this.targetParameterTypes = Collections.unmodifiableList(targetParameterTypes);
        this.typeMatchScorer = typeMatchScorer;
    }

    // ------------------------------ 核心比较逻辑 ------------------------------
    @Override
    public int compare(List<ResolvableType> candidateParamsA, List<ResolvableType> candidateParamsB) {
        // 空列表安全处理：null转为空不可变列表，避免空指针
        candidateParamsA = safeImmutableList(candidateParamsA);
        candidateParamsB = safeImmutableList(candidateParamsB);

        // 计算两个候选列表的匹配信息（得分、数量匹配状态等）
        ParameterMatchInfo matchInfoA = calculateMatchInfo(candidateParamsA);
        ParameterMatchInfo matchInfoB = calculateMatchInfo(candidateParamsB);

        // 1. 优先级1：参数数量完全匹配（完全匹配 > 不匹配）
        if (matchInfoA.isParamCountMatch() != matchInfoB.isParamCountMatch()) {
            return matchInfoA.isParamCountMatch() ? -1 : 1;
        }

        // 2. 优先级2：整体匹配得分（得分越高越靠前）
        if (matchInfoA.getTotalScore() != matchInfoB.getTotalScore()) {
            return Integer.compare(matchInfoB.getTotalScore(), matchInfoA.getTotalScore());
        }

        // 3. 优先级3：匹配均衡性（最低分越高越靠前，避免单个参数拖垮整体）
        if (matchInfoA.getMinParamScore() != matchInfoB.getMinParamScore()) {
            return Integer.compare(matchInfoB.getMinParamScore(), matchInfoA.getMinParamScore());
        }

        // 4. 兜底：参数类型名字典序（保证排序稳定性，避免同等匹配度时的随机顺序）
        String typeStrA = buildTypeSignature(candidateParamsA);
        String typeStrB = buildTypeSignature(candidateParamsB);
        return typeStrA.compareTo(typeStrB);
    }

    /**
     * 计算单个候选参数列表的匹配信息
     * 核心优化：双迭代器并行遍历，兼容所有List实现（ArrayList/LinkedList等），保证O(n)性能
     */
    private ParameterMatchInfo calculateMatchInfo(List<ResolvableType> candidateParams) {
        int targetParamCount = targetParameterTypes.size();
        int candidateParamCount = candidateParams.size();
        boolean paramCountMatch = targetParamCount == candidateParamCount;

        List<Integer> paramScores = new ArrayList<>();
        // 双迭代器并行遍历：同步遍历目标列表和候选列表，仅处理重叠部分（索引对齐）
        Iterator<ResolvableType> targetIt = targetParameterTypes.iterator();
        Iterator<ResolvableType> candidateIt = candidateParams.iterator();

        while (targetIt.hasNext() && candidateIt.hasNext()) {
            ResolvableType targetType = targetIt.next();
            ResolvableType candidateType = candidateIt.next();
            paramScores.add(calculateSingleParamScore(targetType, candidateType));
        }

        // 补充多余参数的0分（候选参数数 > 目标参数数：多余参数无匹配对象）
        int extraCandidateCount = candidateParamCount - paramScores.size();
        for (int i = 0; i < extraCandidateCount; i++) {
            paramScores.add(0);
        }

        // 补充缺少参数的0分（候选参数数 < 目标参数数：缺少参数无法匹配）
        int missingParamCount = targetParamCount - paramScores.size();
        for (int i = 0; i < missingParamCount; i++) {
            paramScores.add(0);
        }

        // 计算总得分和最低分（Java 8 Stream 兼容，空列表返回0）
        int totalScore = paramScores.stream().mapToInt(Integer::intValue).sum();
        int minParamScore = paramScores.stream().mapToInt(Integer::intValue).min().orElse(0);

        return new ParameterMatchInfo(paramCountMatch, totalScore, minParamScore, paramScores);
    }

    /**
     * 计算单个参数的匹配得分（委托给得分计算器，补充空值校验）
     */
    private int calculateSingleParamScore(ResolvableType targetType, ResolvableType candidateType) {
        if (targetType == null || candidateType == null) {
            return 0;
        }
        return typeMatchScorer.calculateMatchScore(candidateType, targetType);
    }

    /**
     * 构建参数类型签名（用于兜底排序，保证稳定性）
     */
    private String buildTypeSignature(List<ResolvableType> paramTypes) {
        return paramTypes.stream()
                .map(type -> type != null ? type.getTypeName() : "")
                .collect(Collectors.joining(","));
    }

    // ------------------------------ 内部辅助类 ------------------------------
    /**
     * 多参数匹配信息封装（不可变，线程安全）
     * 存储单个候选列表的匹配核心数据，避免重复计算
     */
    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ParameterMatchInfo {
        private boolean paramCountMatch; // 参数数量是否完全匹配
        private int totalScore; // 整体匹配得分（所有参数得分之和）
        private int minParamScore; // 单个参数最低得分（衡量匹配均衡性）
        private List<Integer> paramScores; // 每个参数的具体得分列表
    }

    /**
     * 单个类型匹配得分计算器（独立逻辑，支持自定义扩展）
     */
    public static class SingleTypeMatchScorer {
        /** 基本类型拓宽顺序：byte < short < char < int < long < float < double */
        private static final Map<Class<?>, Integer> PRIMITIVE_WIDENING_ORDER;

        /** 高精度数值类型（BigDecimal/BigInteger），语义优先级高于普通数值 */
        private static final Class<?>[] HIGH_PRECISION_NUMBER_TYPES = {BigDecimal.class, BigInteger.class};

        // 静态初始化基本类型拓宽顺序（Java 8 兼容）
        static {
            PRIMITIVE_WIDENING_ORDER = new HashMap<>();
            PRIMITIVE_WIDENING_ORDER.put(byte.class, 0);
            PRIMITIVE_WIDENING_ORDER.put(short.class, 1);
            PRIMITIVE_WIDENING_ORDER.put(char.class, 2);
            PRIMITIVE_WIDENING_ORDER.put(int.class, 3);
            PRIMITIVE_WIDENING_ORDER.put(long.class, 4);
            PRIMITIVE_WIDENING_ORDER.put(float.class, 5);
            PRIMITIVE_WIDENING_ORDER.put(double.class, 6);
        }

        /**
         * 计算单个类型匹配得分（规则优先级：100→90→80→70→50→0）
         * 100分：完全匹配（类型+泛型一致，或基本类型与包装类互认）
         *  90分：基本类型拓宽匹配（如 int→long，仅基本类型生效）
         *  80分：赋值兼容（继承/实现/泛型兼容，依赖 ResolvableType 原生能力）
         *  70分：普通数值→高精度数值（如 int→BigDecimal，语义优先级高于普通赋值）
         *  50分：强制转换兼容（无继承关系但语义可转换，如 Object→String、数组→Iterable）
         *   0分：不匹配（无任何兼容关系）
         */
        public int calculateMatchScore(ResolvableType sourceType, ResolvableType targetType) {
            if (isExactMatch(sourceType, targetType)) {
                return 100;
            }
            if (isPrimitiveWideningMatch(sourceType, targetType)) {
                return 90;
            }
            if (isAssignableCompatible(sourceType, targetType)) {
                return isNumberToHighPrecisionMatch(sourceType, targetType) ? 70 : 80;
            }
            if (isForceConvertibleMatch(sourceType, targetType)) {
                return 50;
            }
            return 0;
        }

        /** 完全匹配判断（类型+泛型一致，或基本类型与包装类互认） */
        private boolean isExactMatch(ResolvableType sourceType, ResolvableType targetType) {
            // 1. 类型+泛型完全一致（如 List<String> == List<String>、String[] == String[]）
            if (sourceType.equals(targetType)) {
                return true;
            }

            // 2. 基本类型与包装类互认（如 int ↔ Integer、double ↔ Double）
            Class<?> sourceRaw = sourceType.getRawType();
            Class<?> targetRaw = targetType.getRawType();
            if (sourceRaw == null || targetRaw == null) {
                return false;
            }
            return (sourceRaw.isPrimitive() && ClassUtils.getPrimitiveWrapper(sourceRaw).equals(targetRaw))
                    || (targetRaw.isPrimitive() && ClassUtils.getPrimitiveWrapper(targetRaw).equals(sourceRaw));
        }

        /** 基本类型拓宽匹配判断（仅适用于基本类型，遵循Java拓宽规则） */
        private boolean isPrimitiveWideningMatch(ResolvableType sourceType, ResolvableType targetType) {
            Class<?> sourceRaw = sourceType.getRawType();
            Class<?> targetRaw = targetType.getRawType();
            // 必须都是基本类型（排除包装类、引用类型）
            if (sourceRaw == null || targetRaw == null || !sourceRaw.isPrimitive() || !targetRaw.isPrimitive()) {
                return false;
            }
            // 检查拓宽顺序：source 顺序 ≤ target 顺序（如 int(3) → long(4) 符合）
            Integer sourceOrder = PRIMITIVE_WIDENING_ORDER.get(sourceRaw);
            Integer targetOrder = PRIMITIVE_WIDENING_ORDER.get(targetRaw);
            return sourceOrder != null && targetOrder != null && sourceOrder <= targetOrder;
        }

        /** 赋值兼容判断（依赖 ResolvableType 原生能力，支持继承/实现/泛型兼容） */
        private boolean isAssignableCompatible(ResolvableType sourceType, ResolvableType targetType) {
            if (sourceType == ResolvableType.NONE || targetType == ResolvableType.NONE) {
                return false;
            }
            // 直接复用 ResolvableType.isAssignableFrom，无需手动处理泛型/数组逻辑
            return targetType.isAssignableFrom(sourceType);
        }

        /** 普通数值→高精度数值匹配判断（语义优先级高于普通赋值兼容） */
        private boolean isNumberToHighPrecisionMatch(ResolvableType sourceType, ResolvableType targetType) {
            Class<?> sourceRaw = sourceType.getRawType();
            Class<?> targetRaw = targetType.getRawType();
            if (sourceRaw == null || targetRaw == null) {
                return false;
            }

            // 源类型：普通数值类型（基本类型/包装类/Number子类，排除高精度数值）
            if (!ClassUtils.isNumber(sourceRaw) || BigDecimal.class.equals(sourceRaw) || BigInteger.class.equals(sourceRaw)) {
                return false;
            }

            // 目标类型：高精度数值类型（BigDecimal/BigInteger）
            for (Class<?> highPrecisionType : HIGH_PRECISION_NUMBER_TYPES) {
                if (highPrecisionType.equals(targetRaw)) {
                    return true;
                }
            }
            return false;
        }

        /** 强制转换兼容判断（无继承关系，但语义可转换，依赖业务约定） */
        private boolean isForceConvertibleMatch(ResolvableType sourceType, ResolvableType targetType) {
            Class<?> sourceRaw = sourceType.getRawType();
            Class<?> targetRaw = targetType.getRawType();
            if (sourceRaw == null || targetRaw == null
                    || sourceType == ResolvableType.NONE || targetType == ResolvableType.NONE) {
                return false;
            }

            // 排除基本类型（基本类型无法强制转换为非拓宽基本类型）
            if (sourceRaw.isPrimitive() || targetRaw.isPrimitive()) {
                return false;
            }

            // 排除已被赋值兼容覆盖的场景（避免重复计分）
            if (targetType.isAssignableFrom(sourceType)) {
                return false;
            }

            // 支持三种强制转换场景：
            // 1. 引用类型双向强转（如 Object→String、String→CharSequence）
            // 2. 数值类型↔String 互转（如 Integer→String、String→BigDecimal）
            // 3. 数组→Iterable 转换（如 String[]→Iterable<String>）
            return (ClassUtils.isAssignable(targetRaw, sourceRaw) || ClassUtils.isAssignable(sourceRaw, targetRaw))
                    || ((ClassUtils.isNumber(sourceRaw) && String.class.equals(targetRaw))
                            || (String.class.equals(sourceRaw) && ClassUtils.isNumber(targetRaw)))
                    || (sourceType.isArray() && Iterable.class.isAssignableFrom(targetRaw)
                            && sourceType.getComponentType() != ResolvableType.NONE);
        }
    }

    // ------------------------------ 静态工厂方法（快速创建） ------------------------------
    /**
     * 快速创建比较器（可变参数入参，简化无泛型场景使用）
     * @param targetParameterTypes 目标参数类型可变参数（支持 null 入参=无参场景）
     */
    public static MultiParameterMatchComparator of(ResolvableType... targetParameterTypes) {
        List<ResolvableType> paramList = targetParameterTypes == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(java.util.Arrays.asList(targetParameterTypes));
        return new MultiParameterMatchComparator(paramList);
    }

    /**
     * 快速创建比较器（列表入参，支持复杂泛型/数组类型）
     * @param targetParameterTypes 目标参数类型列表
     */
    public static MultiParameterMatchComparator of(List<ResolvableType> targetParameterTypes) {
        return new MultiParameterMatchComparator(safeImmutableList(targetParameterTypes));
    }

    /**
     * 静态工具方法：安全处理列表（供工厂方法使用，统一逻辑）
     */
    private static List<ResolvableType> safeImmutableList(List<ResolvableType> list) {
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }
}