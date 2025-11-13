package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import run.soeasy.framework.core.NumberUtils;

/**
 * 除法运算实现类（框架层核心组件）：遵循 {@link BinaryOperation} 接口规范，
 * 核心设计理念为「框架层不限制精度，仅保障计算精准性；精度控制完全交由业务层自主实现」。
 * <p>
 * 框架层核心职责：
 * 1. 精准计算：所有类型除法均基于高精度逻辑实现，避免原生类型（double/float）精度丢失；
 * 2. 无精度约束：默认使用 {@link MathContext#UNLIMITED} 执行无限精度除法，保留所有有效数字（整除返回整数，除不尽返回无限循环/非终止小数）；
 * 3. 全类型兼容：支持 {@link BigDecimal}、{@link BigInteger}、double、float、long、int、short、byte 所有标准 Number 子类；
 * 4. 特殊场景优化：自动处理「0÷非零返回0」「非零÷1返回原数」「整数溢出自动升级高精度类型」等通用场景；
 * 5. 安全保障：除数为0时抛出明确异常，包含上下文信息便于排查；
 * 6. 轻量无依赖：仅依赖框架内置 {@link NumberUtils} 工具类做类型转换，无额外第三方依赖。
 * <p>
 * 业务层使用须知（重要）：
 * - 精度控制：框架层不干预业务精度，若需限制小数位（如金额保留2位），业务层需对返回结果手动处理（例：((BigDecimal) result).setScale(2, RoundingMode.HALF_UP)）；
 * - 结果处理：无限循环小数（如1÷3）返回 {@link BigDecimal} 时会存储完整循环节，需注意序列化/数据库存储时的字段长度适配；
 * - 类型适配：原生类型（double/float）计算后，若无精度损失则返回原原生类型，否则返回 {@link BigDecimal}，业务层需做好类型判断。
 *
 * @author soeasy.run
 * @see BinaryOperation 二元运算标准接口
 * @see MathContext#UNLIMITED 无限精度计算上下文（框架层默认）
 * @see NumberUtils 框架内置类型转换工具类（提供安全的 Number → BigDecimal 转换）
 */
public class DivideOperation implements BinaryOperation {

    /** 浮点精度阈值：用于判断高精度结果是否可无损转回原生浮点类型（double/float） */
    private static final double FLOAT_PRECISION_THRESHOLD = 1e-10;

    // ====================== 高精度类型除法（核心无限精度实现） ======================

    /**
     * BigDecimal 类型除法：执行无限精度除法，保留所有有效数字。
     * <p>
     * 处理逻辑：
     * - 除数为0：抛出 {@link ArithmeticException}，包含运算上下文；
     * - 被除数为0：返回 {@link BigDecimal#ZERO}（自然整数格式，无冗余小数位）；
     * - 除数为1：返回被除数本身（保持原精度格式）；
     * - 普通场景：使用 {@link MathContext#UNLIMITED} 执行无限精度除法，支持无限循环/非终止小数。
     *
     * @param left 被除数（BigDecimal 类型，支持任意精度）
     * @param right 除数（BigDecimal 类型，支持任意精度）
     * @return 无限精度除法结果：整除返回整数格式 BigDecimal，除不尽返回无限精度小数格式 BigDecimal
     * @throws ArithmeticException 当除数为0时抛出
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

        // 核心：无限精度除法，保留所有有效数字
        return left.divide(right, MathContext.UNLIMITED);
    }

    /**
     * BigInteger 类型除法：遵循整数除法语义（向零取整），无精度限制。
     * <p>
     * 处理逻辑：
     * - 除数为0：抛出 {@link ArithmeticException}；
     * - 被除数为0：返回 {@link BigInteger#ZERO}；
     * - 除数为1：返回被除数本身；
     * - 普通场景：执行 BigInteger 原生除法（向零取整，无小数位，符合整数运算规则）。
     *
     * @param left 被除数（BigInteger 类型，任意整数精度）
     * @param right 除数（BigInteger 类型，任意整数精度）
     * @return 整数除法结果（BigInteger 类型，仅包含商，无余数）
     * @throws ArithmeticException 当除数为0时抛出
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

        // BigInteger 固有语义：整数除法（向零取整），无小数位
        return left.divide(right);
    }

    // ====================== 原生浮点类型除法（避免原生精度丢失） ======================

    /**
     * double 类型除法：转高精度计算后返回，避免原生 double 精度误差。
     * <p>
     * 处理逻辑：
     * - 除数为0：抛出 {@link ArithmeticException}；
     * - 被除数为0：返回 0.0（原生 double 格式）；
     * - 除数为1：返回被除数本身（原生 double 格式）；
     * - 普通场景：通过 {@link NumberUtils#toBigDecimal(double)} 转为 BigDecimal 执行无限精度除法，
     *   若结果可无损转回 double（误差小于 {@link #FLOAT_PRECISION_THRESHOLD}）则返回 double，否则返回 BigDecimal。
     *
     * @param left 被除数（double 类型）
     * @param right 除数（double 类型）
     * @return 除法结果：无精度损失返回 double，否则返回无限精度 BigDecimal
     * @throws ArithmeticException 当除数为0.0时抛出
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

        // 核心：转高精度计算，避免原生 double 精度丢失（如 0.1 + 0.2 问题）
        BigDecimal preciseResult = NumberUtils.toBigDecimal(left)
                .divide(NumberUtils.toBigDecimal(right), MathContext.UNLIMITED);
        double nativeResult = preciseResult.doubleValue();

        // 无精度损失则返回原生类型（贴合业务使用习惯），否则返回高精度结果
        return Math.abs(preciseResult.doubleValue() - nativeResult) < FLOAT_PRECISION_THRESHOLD 
                ? nativeResult
                : preciseResult;
    }

    /**
     * float 类型除法：转高精度计算后返回，避免原生 float 精度误差。
     * <p>
     * 处理逻辑与 {@link #eval(double, double)} 一致，仅类型适配 float。
     *
     * @param left 被除数（float 类型）
     * @param right 除数（float 类型）
     * @return 除法结果：无精度损失返回 float，否则返回无限精度 BigDecimal
     * @throws ArithmeticException 当除数为0.0f时抛出
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

        // 核心：转高精度计算，避免原生 float 精度丢失（float 精度仅为 6-7 位有效数字）
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
     * long 类型除法：遵循整数除法语义，自动处理溢出场景。
     * <p>
     * 处理逻辑：
     * - 除数为0：抛出 {@link ArithmeticException}；
     * - 被除数为0：返回 0L；
     * - 除数为1：返回被除数本身；
     * - 普通场景：执行原生 long 除法（向零取整）；
     * - 溢出场景：当 long 极值除法（如 Long.MIN_VALUE ÷ (-1)）时，自动升级为 BigInteger 计算并返回。
     *
     * @param left 被除数（long 类型）
     * @param right 除数（long 类型）
     * @return 除法结果：正常场景返回 long，溢出场景返回 BigInteger
     * @throws ArithmeticException 当除数为0时抛出
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
            return BigInteger.valueOf(left).divide(BigInteger.valueOf(right));
        }
    }

    /**
     * int 类型除法：遵循整数除法语义，自动处理溢出场景。
     * <p>
     * 处理逻辑与 {@link #eval(long, long)} 一致，溢出时升级为 long 类型返回。
     *
     * @param left 被除数（int 类型）
     * @param right 除数（int 类型）
     * @return 除法结果：正常场景返回 int，溢出场景返回 long
     * @throws ArithmeticException 当除数为0时抛出
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
            return (long) left / right;
        }
    }

    /**
     * short 类型除法：自动升级为 int 运算，避免溢出，结果按需转回 short。
     * <p>
     * 处理逻辑：
     * - 先升级为 int 类型执行除法（避免 short 本身范围过小导致的溢出）；
     * - 若结果在 short 取值范围内（[-32768, 32767]），则转回 short，否则保留 int 类型返回。
     *
     * @param left 被除数（short 类型）
     * @param right 除数（short 类型）
     * @return 除法结果：范围适配返回 short，否则返回 int
     * @throws ArithmeticException 当除数为0时抛出
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
     * byte 类型除法：自动升级为 int 运算，避免溢出，结果按需转回 byte。
     * <p>
     * 处理逻辑与 {@link #eval(short, short)} 一致，结果范围适配 byte（[-128, 127]）时转回，否则返回 int。
     *
     * @param left 被除数（byte 类型）
     * @param right 除数（byte 类型）
     * @return 除法结果：范围适配返回 byte，否则返回 int
     * @throws ArithmeticException 当除数为0时抛出
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