package run.soeasy.framework.core.math;

/**
 * 有理数抽象类，定义有理数的基本运算和属性。
 * 该类继承自{@link NumberValue}，提供了有理数的标准数学运算实现，
 * 所有运算均基于分数形式进行，确保计算过程中的精度不会丢失。
 *
 * <p>核心特性：
 * <ul>
 *   <li>分数表示：所有有理数均以分子/分母的分数形式表示</li>
 *   <li>高精度计算：基于BigInteger实现，避免浮点数精度问题</li>
 *   <li>运算实现：提供加减乘除、取余、幂等基本数学运算</li>
 *   <li>自动转换：支持将其他数值类型自动转换为分数进行计算</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>金融计算：需要精确计算的货币和利率计算</li>
 *   <li>科学计算：需要高精度和无精度丢失的计算场景</li>
 *   <li>游戏开发：需要精确数值的游戏逻辑计算</li>
 *   <li>算法实现：依赖精确数值的算法（如几何计算）</li>
 * </ul>
 *
 * <p>子类实现：
 * 具体的有理数实现类应继承此类，并实现{@link #getAsBigInteger()}和{@link #getAsBigDecimal()}方法，
 * 以提供分子和分母的精确表示。
 *
 * @author soeasy.run
 * @see Fraction
 * @see NumberValue
 */
public abstract class RationalNumber extends NumberValue {
    private static final long serialVersionUID = 1L;

    /**
     * 获取绝对值，返回一个非负的有理数。
     * <p>
     * 实现逻辑：将当前数转换为分数形式，然后调用分数的绝对值方法。
     *
     * @return 绝对值表示的有理数
     */
    @Override
    public NumberValue abs() {
        return toFraction(this).abs();
    }

    /**
     * 加法运算，返回两个有理数的和。
     * <p>
     * 实现逻辑：将当前数和参数转换为分数形式，然后执行分数加法。
     *
     * @param value 加数
     * @return 加法结果
     */
    @Override
    public NumberValue add(NumberValue value) {
        return toFraction(this).add(value);
    }

    /**
     * 除法运算，返回两个有理数的商。
     * <p>
     * 实现逻辑：将当前数和参数转换为分数形式，然后执行分数除法。
     * 若除数为零，将抛出ArithmeticException。
     *
     * @param value 除数
     * @return 除法结果
     * @throws ArithmeticException 如果除数为零
     */
    @Override
    public NumberValue divide(NumberValue value) {
        return toFraction(this).divide(value);
    }

    /**
     * 乘法运算，返回两个有理数的积。
     * <p>
     * 实现逻辑：将当前数和参数转换为分数形式，然后执行分数乘法。
     *
     * @param value 乘数
     * @return 乘法结果
     */
    @Override
    public NumberValue multiply(NumberValue value) {
        return toFraction(this).multiply(value);
    }

    /**
     * 幂运算，返回当前有理数的指定次幂。
     * <p>
     * 实现逻辑：将当前数转换为分数形式，然后执行分数幂运算。
     * 指数可以是整数或分数，但结果可能需要转换为近似值。
     *
     * @param value 指数
     * @return 幂运算结果
     */
    @Override
    public NumberValue pow(NumberValue value) {
        return toFraction(this).pow(value);
    }

    /**
     * 取余运算，返回当前有理数除以另一个数的余数。
     * <p>
     * 实现逻辑：将当前数和参数转换为分数形式，然后执行分数取余。
     *
     * @param value 除数
     * @return 余数
     */
    @Override
    public NumberValue remainder(NumberValue value) {
        return toFraction(this).remainder(value);
    }

    /**
     * 减法运算，返回两个有理数的差。
     * <p>
     * 实现逻辑：将当前数和参数转换为分数形式，然后执行分数减法。
     *
     * @param value 减数
     * @return 减法结果
     */
    @Override
    public NumberValue subtract(NumberValue value) {
        return toFraction(this).subtract(value);
    }

    /**
     * 将数值转换为分数形式，便于进行精确计算。
     * <p>
     * 若数值已经是分数，则直接返回；否则创建一个新的分数，
     * 分子为该数值，分母为1。
     *
     * @param value 待转换的数值
     * @return 分数表示
     */
    private Fraction toFraction(NumberValue value) {
        if (value instanceof Fraction) {
            return (Fraction) value;
        }
        return new Fraction(value, BigIntegerValue.ONE);
    }
}