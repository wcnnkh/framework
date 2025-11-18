package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import run.soeasy.framework.core.NumberUtils;

/**
 * 高精度除法运算实现类，严格遵循 {@link BinaryOperation} 二元运算接口规范，
 * 核心设计理念为「框架层保障计算精准性与无界精度，业务层自主掌控精度取舍」，
 * 彻底解决原生数值类型（double/float/整数）除法的精度丢失、溢出、语义模糊等痛点，
 * 适配财务计算、科学运算、工程计算等对精度要求严苛的场景。
 *
 * <p>
 * <strong>核心设计原则</strong>：
 * <ul>
 * <li><strong>无精度上限</strong>：默认采用 {@link MathContext#UNLIMITED} 无限精度计算上下文，
 * 整除场景返回整数格式结果，除不尽场景完整保留有效数字（含无限循环/非终止小数），不主动截断或舍入；</li>
 * <li><strong>类型全兼容</strong>：覆盖所有标准 Number 子类（BigDecimal/BigInteger/double/float/long/int/short/byte），
 * 无需业务层手动类型转换，自动适配输入类型执行对应语义的除法；</li>
 * <li><strong>语义精准化</strong>：整数除法遵循「向零取整」语义，浮点除法遵循高精度计算语义，
 * 特殊场景（0÷非零、非零÷1、极值溢出）自动优化处理，贴合实际业务使用习惯；</li>
 * <li><strong>安全强保障</strong>：除数为0时抛出含上下文信息的明确异常，避免原生异常无业务含义的问题；
 * 整数除法自动处理溢出场景，通过类型升级（如int→long、long→BigInteger）避免计算错误；</li>
 * <li><strong>轻量低耦合</strong>：仅依赖框架内置 {@link NumberUtils} 工具类实现安全类型转换，
 * 无第三方依赖，可无缝集成至框架任意模块。</li>
 * </ul>
 *
 * <p>
 * <strong>框架层核心职责</strong>：
 * <ol>
 * <li>精度无损计算：所有除法运算底层基于 BigDecimal 高精度逻辑实现，彻底规避原生浮点类型（double/float）的精度丢失问题（如 0.1÷0.3 原生计算误差）；</li>
 * <li>特殊场景自动化处理：
 *   <ul>
 *   <li>0 ÷ 非零数：直接返回对应类型的零值（如 BigDecimal.ZERO、0.0、0L），无冗余计算；</li>
 *   <li>非零数 ÷ 1：直接返回被除数本身，保持原类型与精度格式，提升执行效率；</li>
 *   <li>整数溢出：极值除法（如 Long.MIN_VALUE ÷ (-1)）自动升级为更高精度类型（BigInteger）计算，避免溢出异常；</li>
 *   <li>原生类型兼容：浮点类型（double/float）计算后，若结果可无损转回原类型（误差＜{@link #FLOAT_PRECISION_THRESHOLD}），则返回原类型，否则返回 BigDecimal；</li>
 *   </ul>
 * </li>
 * <li>异常语义化：除数为0时抛出 ArithmeticException，包含被除数、除数上下文信息，便于业务层快速排查问题。</li>
 * </ol>
 *
 * <p>
 * <strong>业务层使用须知（关键）</strong>：
 * <ul>
 * <li>精度控制自主化：框架层不干预业务精度需求，若需限制小数位数（如金额保留2位、百分比保留1位），
 * 业务层需对返回结果手动处理（示例：((BigDecimal) result).setScale(2, RoundingMode.HALF_UP)）；</li>
 * <li>无限精度结果处理：除不尽场景（如 1÷3）返回的 BigDecimal 会存储完整循环节/非终止小数，
 * 序列化或数据库存储时需注意字段长度适配（避免截断导致精度丢失）；</li>
 * <li>返回类型适配：方法返回值为 Number 抽象类型，业务层需根据输入类型和计算结果判断实际类型（如原生类型无精度损失时返回原类型，否则返回 BigDecimal）；</li>
 * <li>整数除法语义：所有整数类型（long/int/short/byte）除法均遵循「向零取整」语义（与 Java 原生整数除法一致），无小数位返回。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see BinaryOperation 二元运算标准接口（定义二元运算的统一执行契约）
 * @see MathContext#UNLIMITED 无限精度计算上下文（框架层默认除法计算模式）
 * @see NumberUtils 框架内置类型转换工具类（提供安全、无精度损失的 Number → BigDecimal 转换）
 * @see BigDecimal#divide(BigDecimal, MathContext) 高精度除法核心方法（底层依赖）
 */
public class DivideOperation implements BinaryOperation {

    /**
     * 浮点精度阈值：用于判断高精度计算结果是否可无损转回原生浮点类型（double/float）
     * <p>误差小于该阈值时，认为结果无精度损失，返回原生类型；否则返回 BigDecimal 保留完整精度</p>
     */
    private static final double FLOAT_PRECISION_THRESHOLD = 1e-10;

    // ====================== 高精度类型除法（核心无限精度实现） ======================

    /**
     * BigDecimal 类型除法：执行无限精度除法，完整保留所有有效数字，适配任意精度场景。
     *
     * <p>处理逻辑（按优先级排序）：
     * <ol>
     * <li>除数为0校验：抛出含运算上下文的 {@link ArithmeticException}，明确异常原因；</li>
     * <li>被除数为0：返回 {@link BigDecimal#ZERO}（自然整数格式，无冗余小数位，贴合业务使用）；</li>
     * <li>除数为1：直接返回被除数本身（保持原 BigDecimal 的精度配置、小数位格式，避免无意义计算）；</li>
     * <li>普通场景：通过 {@link MathContext#UNLIMITED} 执行无限精度除法，
     *   - 整除结果：返回整数格式 BigDecimal（如 6÷2 → 3，而非 3.0）；
     *   - 除不尽结果：完整保留循环节/非终止小数（如 1÷3 → 0.(3)，1÷10 → 0.1）。
     * </li>
     * </ol>
     *
     * @param left 被除数（BigDecimal 类型，支持任意精度的整数/小数，无范围限制）
     * @param right 除数（BigDecimal 类型，支持任意精度的整数/小数，不可为零）
     * @return 无限精度除法结果：整除返回整数格式 BigDecimal，除不尽返回完整精度小数格式 BigDecimal
     * @throws ArithmeticException 当除数（right）为 BigDecimal.ZERO 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(BigDecimal left, BigDecimal right) {
        if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        if (left.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (right.compareTo(BigDecimal.ONE) == 0) {
            return left;
        }

        // 核心：无限精度除法，保留所有有效数字（含循环节/非终止小数）
        return left.divide(right, MathContext.UNLIMITED);
    }

    /**
     * BigInteger 类型除法：遵循整数除法语义（向零取整），无精度限制，适配超大整数场景。
     *
     * <p>处理逻辑（按优先级排序）：
     * <ol>
     * <li>除数为0校验：抛出 {@link ArithmeticException}，明确异常原因；</li>
     * <li>被除数为0：返回 {@link BigInteger#ZERO}（纯整数格式，符合 BigInteger 类型语义）；</li>
     * <li>除数为1：直接返回被除数本身（避免无意义计算，保持原整数精度）；</li>
     * <li>普通场景：执行 BigInteger 原生除法，严格遵循「向零取整」规则（如 7÷3 → 2，-7÷3 → -2），
     * 仅返回商，不包含余数（若需余数需业务层额外通过 BigInteger#remainder 方法获取）。</li>
     * </ol>
     *
     * @param left 被除数（BigInteger 类型，支持任意大小的整数，无范围限制）
     * @param right 除数（BigInteger 类型，支持任意大小的整数，不可为零）
     * @return 整数除法结果（BigInteger 类型，仅包含商，无小数位，符合整数运算语义）
     * @throws ArithmeticException 当除数（right）为 BigInteger.ZERO 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(BigInteger left, BigInteger right) {
        // 除数为0校验
        if (right.compareTo(BigInteger.ZERO) == 0) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景1：0除以非零数返回0
        if (left.compareTo(BigInteger.ZERO) == 0) {
            return BigInteger.ZERO;
        }
        // 特殊场景2：非零数除以1返回原数
        if (right.compareTo(BigInteger.ONE) == 0) {
            return left;
        }

        // BigInteger 固有语义：整数除法（向零取整），仅返回商，无小数位
        return left.divide(right);
    }

    // ====================== 原生浮点类型除法（避免原生精度丢失） ======================

    /**
     * double 类型除法：转高精度计算后智能返回，彻底规避原生 double 精度丢失问题（如 0.1÷0.3 误差）。
     *
     * <p>处理逻辑（按优先级排序）：
     * <ol>
     * <li>除数为0校验：抛出 {@link ArithmeticException}，明确异常原因；</li>
     * <li>被除数为0：返回 0.0（原生 double 格式，贴合业务对原生类型的使用习惯）；</li>
     * <li>除数为1：直接返回被除数本身（原生 double 格式，避免无意义转换）；</li>
     * <li>普通场景：
     *   <ul>
     *   <li>转高精度：通过 {@link NumberUtils#toBigDecimal(Number)} 将两个 double 转为 BigDecimal，避免原生计算误差；</li>
     *   <li>无限精度计算：执行 BigDecimal 无限精度除法，获取完整结果；</li>
     *   <li>智能返回：将高精度结果转回 double，若误差＜{@link #FLOAT_PRECISION_THRESHOLD}（无精度损失），则返回 double；
     *   否则返回 BigDecimal（保留完整精度，避免业务层误用误差结果）。</li>
     *   </ul>
     * </li>
     * </ol>
     *
     * @param left 被除数（double 类型，支持原生 double 所有取值范围）
     * @param right 除数（double 类型，支持原生 double 所有取值范围，不可为 0.0）
     * @return 除法结果：无精度损失时返回 double 类型，存在精度损失时返回无限精度 BigDecimal 类型
     * @throws ArithmeticException 当除数（right）为 0.0 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(double left, double right) {
        // 除数为0校验
        if (right == 0.0) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景1：0除以非零数返回0.0
        if (left == 0.0) {
            return 0.0;
        }
        // 特殊场景2：非零数除以1返回原数
        if (right == 1.0) {
            return left;
        }

        // 核心：转高精度计算，避免原生 double 精度丢失（如 0.1/0.3 原生计算结果为 0.3333333333333333，高精度计算为 0.(3)）
        BigDecimal preciseResult = NumberUtils.toBigDecimal(left)
                .divide(NumberUtils.toBigDecimal(right), MathContext.UNLIMITED);
        double nativeResult = preciseResult.doubleValue();

        // 无精度损失则返回原生类型（贴合业务使用习惯），否则返回高精度结果
        return Math.abs(preciseResult.doubleValue() - nativeResult) < FLOAT_PRECISION_THRESHOLD 
                ? nativeResult
                : preciseResult;
    }

    /**
     * float 类型除法：转高精度计算后智能返回，避免原生 float 低精度（仅 6-7 位有效数字）导致的误差。
     *
     * <p>处理逻辑与 {@link #eval(double, double)} 完全一致，仅输入输出类型适配 float：
     * <ol>
     * <li>除数为0校验：抛出 {@link ArithmeticException}；</li>
     * <li>特殊场景（被除数为0、除数为1）：返回对应 float 类型结果；</li>
     * <li>普通场景：转 BigDecimal 无限精度计算后，若误差＜{@link #FLOAT_PRECISION_THRESHOLD}，返回 float；否则返回 BigDecimal。</li>
     * </ol>
     *
     * @param left 被除数（float 类型，支持原生 float 所有取值范围）
     * @param right 除数（float 类型，支持原生 float 所有取值范围，不可为 0.0f）
     * @return 除法结果：无精度损失时返回 float 类型，存在精度损失时返回无限精度 BigDecimal 类型
     * @throws ArithmeticException 当除数（right）为 0.0f 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(float left, float right) {
        // 除数为0校验
        if (right == 0.0f) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景1：0除以非零数返回0.0f
        if (left == 0.0f) {
            return 0.0f;
        }
        // 特殊场景2：非零数除以1返回原数
        if (right == 1.0f) {
            return left;
        }

        // 核心：转高精度计算，避免原生 float 精度丢失（float 精度仅为 6-7 位有效数字，易产生累积误差）
        BigDecimal preciseResult = NumberUtils.toBigDecimal(left)
                .divide(NumberUtils.toBigDecimal(right), MathContext.UNLIMITED);
        float nativeResult = preciseResult.floatValue();

        // 无精度损失则返回原生类型，否则返回高精度结果
        return Math.abs(preciseResult.floatValue() - nativeResult) < FLOAT_PRECISION_THRESHOLD 
                ? nativeResult
                : preciseResult;
    }

    // ====================== 原生整数类型除法（处理溢出，保持整数语义） ======================

    /**
     * long 类型除法：遵循整数除法语义，自动处理极值溢出场景，适配长整数计算。
     *
     * <p>处理逻辑（按优先级排序）：
     * <ol>
     * <li>除数为0校验：抛出 {@link ArithmeticException}，明确异常原因；</li>
     * <li>被除数为0：返回 0L（原生 long 格式，符合整数语义）；</li>
     * <li>除数为1：直接返回被除数本身（避免无意义计算，保持原 long 类型）；</li>
     * <li>普通场景：执行原生 long 除法，遵循「向零取整」语义（如 9L÷2L → 4L，-9L÷2L → -4L）；</li>
     * <li>溢出处理：当执行极值除法（如 Long.MIN_VALUE ÷ (-1L)）时，原生 long 会抛出溢出异常，
     * 此时自动升级为 BigInteger 类型计算，返回 BigInteger 结果（避免计算错误）。</li>
     * </ol>
     *
     * @param left 被除数（long 类型，取值范围：[-9223372036854775808, 9223372036854775807]）
     * @param right 除数（long 类型，取值范围同上，不可为 0）
     * @return 除法结果：正常场景返回 long 类型，溢出场景返回 BigInteger 类型（保持计算正确性）
     * @throws ArithmeticException 当除数（right）为 0 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(long left, long right) {
        // 除数为0校验
        if (right == 0) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景1：0除以非零数返回0L
        if (left == 0) {
            return 0L;
        }
        // 特殊场景2：非零数除以1返回原数
        if (right == 1) {
            return left;
        }

        // 处理 long 除法溢出（如 Long.MIN_VALUE / (-1) 会抛出 ArithmeticException）
        try {
            return left / right;
        } catch (ArithmeticException e) {
            // 溢出时升级为 BigInteger 计算，确保结果正确
            return BigInteger.valueOf(left).divide(BigInteger.valueOf(right));
        }
    }

    /**
     * int 类型除法：遵循整数除法语义，自动处理极值溢出场景，适配普通整数计算。
     *
     * <p>处理逻辑与 {@link #eval(long, long)} 一致，仅溢出处理适配 int 类型：
     * <ol>
     * <li>除数为0校验：抛出 {@link ArithmeticException}；</li>
     * <li>特殊场景（被除数为0、除数为1）：返回对应 int 类型结果；</li>
     * <li>普通场景：执行原生 int 除法，遵循「向零取整」语义；</li>
     * <li>溢出处理：当执行极值除法（如 Integer.MIN_VALUE ÷ (-1)）时，自动升级为 long 类型计算，返回 long 结果。</li>
     * </ol>
     *
     * @param left 被除数（int 类型，取值范围：[-2147483648, 2147483647]）
     * @param right 除数（int 类型，取值范围同上，不可为 0）
     * @return 除法结果：正常场景返回 int 类型，溢出场景返回 long 类型（保持计算正确性）
     * @throws ArithmeticException 当除数（right）为 0 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(int left, int right) {
        // 除数为0校验
        if (right == 0) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景1：0除以非零数返回0
        if (left == 0) {
            return 0;
        }
        // 特殊场景2：非零数除以1返回原数
        if (right == 1) {
            return left;
        }

        // 处理 int 除法溢出（如 Integer.MIN_VALUE / (-1) 会抛出 ArithmeticException）
        try {
            return left / right;
        } catch (ArithmeticException e) {
            // 溢出时升级为 long 计算，确保结果正确
            return (long) left / right;
        }
    }

    /**
     * short 类型除法：自动升级为 int 运算避免溢出，结果按需转回 short，适配短整数计算。
     *
     * <p>处理逻辑（按优先级排序）：
     * <ol>
     * <li>除数为0校验：抛出 {@link ArithmeticException}，明确异常原因；</li>
     * <li>被除数为0：返回 (short) 0（short 类型，符合业务对原类型的预期）；</li>
     * <li>除数为1：直接返回被除数本身（保持 short 类型）；</li>
     * <li>升级运算：由于 short 取值范围过小（[-32768, 32767]），直接运算易溢出，故先升级为 int 类型执行除法；</li>
     * <li>结果适配：若 int 结果在 short 取值范围内，则转回 short 类型；否则保留 int 类型（避免溢出截断）。</li>
     * </ol>
     *
     * @param left 被除数（short 类型，取值范围：[-32768, 32767]）
     * @param right 除数（short 类型，取值范围同上，不可为 0）
     * @return 除法结果：结果在 short 范围内返回 short 类型，否则返回 int 类型（避免溢出）
     * @throws ArithmeticException 当除数（right）为 0 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(short left, short right) {
        // 除数为0校验
        if (right == 0) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景1：0除以非零数返回0（short 类型）
        if (left == 0) {
            return (short) 0;
        }
        // 特殊场景2：非零数除以1返回原数
        if (right == 1) {
            return left;
        }

        // 升级为 int 运算避免溢出，结果按需转回 short
        int intResult = left / right;
        return (intResult >= Short.MIN_VALUE && intResult <= Short.MAX_VALUE) 
                ? (short) intResult 
                : intResult;
    }

    /**
     * byte 类型除法：自动升级为 int 运算避免溢出，结果按需转回 byte，适配字节型整数计算。
     *
     * <p>处理逻辑与 {@link #eval(short, short)} 一致，仅结果范围适配 byte 类型：
     * <ol>
     * <li>除数为0校验：抛出 {@link ArithmeticException}；</li>
     * <li>特殊场景（被除数为0、除数为1）：返回对应 byte 类型结果；</li>
     * <li>升级运算：先升级为 int 类型执行除法，避免 byte 类型（[-128, 127]）溢出；</li>
     * <li>结果适配：若 int 结果在 byte 取值范围内，则转回 byte 类型；否则保留 int 类型。</li>
     * </ol>
     *
     * @param left 被除数（byte 类型，取值范围：[-128, 127]）
     * @param right 除数（byte 类型，取值范围同上，不可为 0）
     * @return 除法结果：结果在 byte 范围内返回 byte 类型，否则返回 int 类型（避免溢出）
     * @throws ArithmeticException 当除数（right）为 0 时抛出，包含 left 和 right 的具体值
     */
    @Override
    public Number eval(byte left, byte right) {
        // 除数为0校验
        if (right == 0) {
            throw new ArithmeticException(String.format("除法运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景1：0除以非零数返回0（byte 类型）
        if (left == 0) {
            return (byte) 0;
        }
        // 特殊场景2：非零数除以1返回原数
        if (right == 1) {
            return left;
        }

        // 升级为 int 运算避免溢出，结果按需转回 byte
        int intResult = left / right;
        return (intResult >= Byte.MIN_VALUE && intResult <= Byte.MAX_VALUE) 
                ? (byte) intResult 
                : intResult;
    }
}