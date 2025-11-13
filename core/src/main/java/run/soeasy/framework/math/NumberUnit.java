package run.soeasy.framework.math;

import lombok.NonNull;
import run.soeasy.framework.core.NumberUtils;

/**
 * 数值单位接口：定义统一的单位转换核心规范，支持「纯比例转换」（长度、重量）和「偏移+比例转换」（温度、角度），
 * 核心设计目标是「通用、高精度、低耦合」，依赖 {@link Calculator} 实现运算逻辑，避免浮点误差。
 * <p>
 * 核心约定（实现类与调用方必须遵守）：
 * 1. 单位体系一致性：所有实现类需归属同一单位体系（如长度：米/厘米；温度：℃/℉），跨体系转换（如长度→重量）无效；
 * 2. 基数规则：
 *    - {@link #getRadix()} 返回「相对于体系基本单位的比例系数」，不可为 null/0/负数；
 *    - 基本单位的基数建议为 1（如“米”“摄氏度”）；
 * 3. 偏移量规则：
 *    - {@link #getOffset()} 返回「相对于基本单位的固定偏移量」，默认值为 0（纯比例单位无需修改）；
 *    - 偏移量仅用于特殊单位（如华氏度偏移=32，开尔文偏移=273.15）；
 * 4. 符号规则：{@link #getSymbol()} 返回单位标准符号（如“m”“℃”），不可为空或重复。
 * <p>
 * 统一转换公式（核心）：
 * 目标单位数值 = (原始数值 × 当前单位基数 + 当前单位偏移量 - 目标单位偏移量) ÷ 目标单位基数
 * <p>
 * 示例：
 * 1. 纯比例（米→厘米）：米（基数=1，偏移=0）→ 厘米（基数=0.01，偏移=0）
 *    1米 = (1×1 + 0 - 0) ÷ 0.01 = 100 厘米；
 * 2. 偏移比例（℃→℉）：℃（基数=1，偏移=0）→ ℉（基数=5/9，偏移=32）
 *    0℃ = (0×1 + 0 - 32) ÷ (5/9) = 32 ℉；
 * 3. 偏移比例（℉→℃）：℉（基数=5/9，偏移=32）→ ℃（基数=1，偏移=0）
 *    32℉ = (32×5/9 + 32 - 0) ÷ 1 = 0 ℃。
 *
 * @author soeasy.run
 * @see Calculator 高精度运算核心依赖
 * @see Operators 运算类型枚举（依赖 DIVIDE/MULTIPLY/PLUS/MINUS 运算符）
 */
public interface NumberUnit {

    /**
     * 获取单位的标准符号标识（用于区分单位、日志输出或前端展示）
     * <p>
     * 约束：不可为 null/空字符串，同一单位体系内唯一（如“m”仅对应“米”）
     *
     * @return 单位标准符号（例：“m”“cm”“℃”“℉”）
     */
    @NonNull
    String getSymbol();

    /**
     * 获取单位的比例基数（相对于体系基本单位的转换系数）
     * <p>
     * 约束：不可为 null/0/负数，基本单位建议设为 1（如“米”“摄氏度”）
     *
     * @return 比例基数（例：厘米=0.01，华氏度=5/9）
     */
    @NonNull
    Number getRadix();

    /**
     * 获取单位的固定偏移量（相对于体系基本单位的偏移值）
     * <p>
     * 约束：不可为 null，纯比例单位无需修改（默认返回 0），特殊单位（如温度）覆写为具体值
     *
     * @return 固定偏移量（例：华氏度=32，开尔文=273.15，米=0）
     */
    default Number getOffset() {
        return 0;
    }

    /**
     * 便捷转换方法：使用默认单例 {@link Calculator}，将当前单位数值转换为目标单位数值
     *
     * @param value      原始数值（不可为 null，当前单位下的有效数值）
     * @param targetUnit 目标单位（不可为 null，需与当前单位属于同一体系）
     * @return 目标单位下的高精度数值（如 BigDecimal）
     * @throws NullPointerException  若参数为 null
     * @throws ArithmeticException   若基数为 0/负数，或运算异常
     * @throws IllegalStateException 若 Calculator 未注册所需运算符
     */
    default Number convertTo(@NonNull Number value, @NonNull NumberUnit targetUnit) {
        return convertTo(value, targetUnit, Calculator.getInstance());
    }

    /**
     * 完整转换方法：使用自定义 {@link Calculator}，基于「基数+偏移量」统一公式实现转换
     * <p>
     * 转换步骤：
     * 1. 计算原始数值×当前基数：缩放原始数值到基本单位比例；
     * 2. 加当前偏移量：对齐基本单位的偏移基准；
     * 3. 减目标偏移量：转换为目标单位的偏移基准；
     * 4. 除以目标基数：缩放为目标单位的最终数值。
     *
     * @param value      原始数值（不可为 null，支持任意 Number 类型）
     * @param targetUnit 目标单位（不可为 null，需与当前单位属于同一体系）
     * @param calculator 高精度计算器（不可为 null，需注册 DIVIDE/MULTIPLY/PLUS/MINUS 运算符）
     * @return 目标单位下的高精度数值（无浮点误差）
     * @throws NullPointerException  若任一参数为 null
     * @throws ArithmeticException   若当前/目标单位基数为 0/负数，或运算异常
     * @throws IllegalStateException 若 Calculator 未注册所需运算符
     */
    default Number convertTo(@NonNull Number value, @NonNull NumberUnit targetUnit, @NonNull Calculator calculator) {
        // 1. 获取当前单位和目标单位的核心参数（基数+偏移量）
        Number currentRadix = this.getRadix();
        Number currentOffset = this.getOffset();
        Number targetRadix = targetUnit.getRadix();
        Number targetOffset = targetUnit.getOffset();

        // 2. 直接校验当前单位基数有效性（非0/非负数）
        if (NumberUtils.isZero(currentRadix)) {
            throw new ArithmeticException(String.format("当前单位[%s]的基数不可为 0", this.getSymbol()));
        }
        if (NumberUtils.isNegative(currentRadix)) {
            throw new ArithmeticException(String.format("当前单位[%s]的基数不可为负数（当前：%s）", this.getSymbol(), currentRadix));
        }

        // 3. 直接校验目标单位基数有效性（非0/非负数）
        if (NumberUtils.isZero(targetRadix)) {
            throw new ArithmeticException(String.format("目标单位[%s]的基数不可为 0", targetUnit.getSymbol()));
        }
        if (NumberUtils.isNegative(targetRadix)) {
            throw new ArithmeticException(String.format("目标单位[%s]的基数不可为负数（当前：%s）", targetUnit.getSymbol(), targetRadix));
        }

        // 4. 按统一公式分步运算（高精度，避免中间误差）
        Number step1 = calculator.calculateBinary(value, currentRadix, Operators.MULTIPLY); // 原始值 × 当前基数
        Number step2 = calculator.calculateBinary(step1, currentOffset, Operators.PLUS);    // + 当前偏移
        Number step3 = calculator.calculateBinary(step2, targetOffset, Operators.MINUS);   // - 目标偏移
        Number step4 = calculator.calculateBinary(step3, targetRadix, Operators.DIVIDE);   // ÷ 目标基数

        return step4;
    }
}