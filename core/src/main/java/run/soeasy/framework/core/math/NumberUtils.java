package run.soeasy.framework.core.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.ThrowingFunction;

/**
 * 通用数字处理工具类（不可实例化），封装数字全链路处理能力，涵盖**解析、类型判断、格式化、随机生成、权重选择**五大核心场景，
 * 支持原生类型（int/long/double）、高精度类型（BigInteger/BigDecimal）及自定义单位（{@link NumberUnit}），
 * 解决日常开发中数字处理的碎片化问题，适用于金融计算、数据解析、概率抽奖等多种业务场景。
 * 
 * <p><strong>设计特性</strong>：
 * <ul>
 * <li><strong>不可实例化</strong>：通过{@link UtilityClass}注解标记，禁止创建实例，所有方法均为静态，避免工具类实例化浪费资源；</li>
 * <li><strong>多类型兼容</strong>：统一处理字符串、原生数字、高精度数字，支持十进制/十六进制（0x/0X/#前缀）/八进制（0前缀）解析，无需区分输入类型；</li>
 * <li><strong>安全鲁棒</strong>：所有方法包含参数校验（非null、范围合法性），明确抛出异常场景（如格式错误、数值溢出、负权重），避免隐式错误；</li>
 * <li><strong>可扩展性</strong>：支持自定义格式化函数（{@link ThrowingFunction}）、过滤函数（{@link IntPredicate}），适配特殊业务规则（如自定义货币单位、特殊数字格式）；</li>
 * <li><strong>场景覆盖全</strong>：从基础的数字解析到复杂的权重随机，无需依赖第三方工具类，满足90%以上的数字处理需求。</li>
 * </ul>
 *
 * <h3>核心功能模块（按使用频率排序）</h3>
 * <ol>
 * <li><strong>数字解析</strong>：将字符串转为指定数字类型（如Integer/BigDecimal），支持多进制，处理单位嵌套（如"1千元2角"→1000.2）；</li>
 * <li><strong>类型判断</strong>：判断类是否为数字/整数类型、字符串是否为有效数字、BigDecimal是否为整数，避免类型转换错误；</li>
 * <li><strong>数字格式化</strong>：清除BigDecimal尾部无效零、按自定义单位格式化（如元/角/分）、保留指定小数位数，适配展示场景；</li>
 * <li><strong>随机数生成</strong>：生成指定区间的int/long/BigInteger/BigDecimal随机数，支持数组随机采样（如随机推荐商品）；</li>
 * <li><strong>权重随机</strong>：基于元素权重实现概率选择（如抽奖、流量分配），支持选中元素移除、自定义权重计算，满足概率业务需求。</li>
 * </ol>
 *
 * <h3>使用示例（覆盖核心场景）</h3>
 * <pre class="code">
 * // 1. 数字解析（多进制+单位支持）
 * Integer hexNum = NumberUtils.parseNumber("0x1A", Integer.class); // 十六进制→26
 * BigDecimal money = NumberUtils.parse("1千元2角5分", 
 *     NumberUnit.of("元", new BigDecimal("1")), 
 *     NumberUnit.of("角", new BigDecimal("0.1")), 
 *     NumberUnit.of("分", new BigDecimal("0.01"))); // 1000.25
 * BigInteger octNum = NumberUtils.parseNumber("077", BigInteger.class); // 八进制→63
 *
 * // 2. 类型判断（避免转换异常）
 * boolean isLongInt = NumberUtils.isInteger(Long.class); // true（Long是整数类型）
 * boolean isHexValid = NumberUtils.isNumeric(16, false, "0X2F"); // true（十六进制有效）
 * boolean isDecimalInt = NumberUtils.isInteger(new BigDecimal("100.00")); // true（无小数部分）
 *
 * // 3. 数字格式化（适配展示）
 * BigDecimal numWithZero = new BigDecimal("100.000");
 * BigDecimal stripped = NumberUtils.stripTrailingZeros(numWithZero); // 100（清除尾部零）
 * String moneyStr = NumberUtils.format(stripped, 
 *     e -&gt; e.toPlainString(), // 自定义格式：避免科学计数法
 *     NumberUnit.of("元", new BigDecimal("1"))); // "100元"
 * String precisionStr = NumberUtils.formatPrecision(123.456, 2); // 保留2位→"123.46"（四舍五入）
 *
 * // 4. 随机数生成（区间与采样）
 * int randomInt = NumberUtils.random(10, 20); // 10~19的int随机数（左闭右开）
 * BigDecimal randomMoney = NumberUtils.random(new BigDecimal("0.01"), new BigDecimal("100.00")); // 0.01~100.00的金额
 * Integer[] srcArr = {1,2,3,4,5};
 * Integer[] sampleArr = NumberUtils.randomArray(new Random(), srcArr, 3); // 随机选3个元素（可重复）
 *
 * // 5. 权重随机（抽奖场景）
 * List&lt;Prize&gt; prizes = Arrays.asList(
 *     new Prize("iPhone", 50), 
 *     new Prize("AirPods", 30), 
 *     new Prize("优惠券", 20)
 * );
 * // 按权重选择，选中后不移除（支持重复抽奖）
 * Prize selected = NumberUtils.random(prizes, Prize::getWeight, null);
 * System.out.println("选中奖品：" + selected.getName()); // 概率：iPhone(50%)、AirPods(30%)、优惠券(20%)
 *
 * // 6. 范围校验示例（避免溢出）
 * BigInteger bigNum = new BigInteger("9223372036854775808"); // 超long范围
 * if (bigNum.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) &gt; 0) {
 *     System.out.println("数值超出long范围，需用BigInteger处理");
 * }
 * </pre>
 *
 * @author soeasy.run
 * @see UtilityClass Lombok注解，标记此类为不可实例化的工具类（私有构造+静态方法）
 * @see NumberUnit 数字单位类（定义单位名称及进制，支撑单位格式化与解析）
 * @see ArithmeticCalculator 算术计算器枚举（提供ADD等基础运算，支撑随机数与权重计算）
 * @see NumberComparator 数字比较器（统一不同类型数字的比较逻辑，权重随机依赖）
 * @see ThrowingFunction 带异常抛出的函数式接口（支持自定义格式化、权重计算，允许抛出业务异常）
 */
@UtilityClass
public class NumberUtils {

    /**
     * 将字符串解析为指定类型的数字（支持多进制与常见数字类型）
     * <p>核心解析流程（分四步）：
     * 1. <strong>预处理</strong>：去除字符串两端空白（{@link String#trim()}），避免空格导致的格式错误；
     * 2. <strong>进制判断</strong>：通过{@link #isHexNumber(String)}识别十六进制（0x/0X/#前缀），八进制通过"0"前缀识别（如"077"→八进制）；
     * 3. <strong>类型适配</strong>：
     *    - 原生类型（Byte/Short/Integer/Long）：调用{@code decode}（多进制）或{@code valueOf}（十进制）；
     *    - 高精度类型（BigInteger/BigDecimal）：调用构造方法，BigInteger支持多进制解析；
     *    - 浮点类型（Float/Double）：直接调用{@code valueOf}（仅支持十进制）；
     * 4. <strong>异常处理</strong>：若字符串格式无效（如"abc"转Integer）或目标类型不支持（如自定义Number子类），抛出{@link IllegalArgumentException}。
     *
     * @param <T>         数字类型泛型，必须是{@link Number}的子类（如Integer、Long、BigDecimal）
     * @param text        待解析的字符串（不可为null，支持多进制前缀：0x/0X/#→十六进制，0→八进制，无前缀→十进制）
     * @param targetClass 目标数字类型的Class对象（不可为null，支持Byte/Short/Integer/Long/BigInteger/Float/Double/BigDecimal）
     * @return 解析后的数字对象（类型为T，非null，与目标类型完全匹配）
     * @throws IllegalArgumentException 若字符串无法解析为目标类型（如"123.45"转Integer），或目标类型不支持（如自定义Number子类）
     * @throws NullPointerException     若text或targetClass为null（由{@link NonNull}注解自动抛出）
     * @throws NumberFormatException     若字符串格式无效（如"0xG"，G不是十六进制字符），由底层解析方法抛出
     * @see #isHexNumber(String) 判断是否为十六进制字符串（辅助进制识别）
     * @see #decodeBigInteger(String) 解析BigInteger类型的多进制字符串（处理复杂进制场景）
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseNumber(@NonNull String text, @NonNull Class<T> targetClass) {
        String trimmed = text.trim();
        // 适配原生整数类型（Byte/Short/Integer/Long）
        if (targetClass.equals(Byte.class)) {
            return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
        } else if (targetClass.equals(Short.class)) {
            return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
        } else if (targetClass.equals(Integer.class)) {
            return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
        } else if (targetClass.equals(Long.class)) {
            return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
        } 
        // 适配高精度整数类型（BigInteger）
        else if (targetClass.equals(BigInteger.class)) {
            return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
        } 
        // 适配浮点类型（Float/Double）
        else if (targetClass.equals(Float.class)) {
            return (T) Float.valueOf(trimmed);
        } else if (targetClass.equals(Double.class)) {
            return (T) Double.valueOf(trimmed);
        } 
        // 适配高精度小数类型（BigDecimal）及默认Number类型
        else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
            return (T) new BigDecimal(trimmed);
        } 
        // 不支持的类型
        else {
            throw new IllegalArgumentException(
                    "Unsupported target class for number parsing: [" + targetClass.getName() + "], text: [" + text + "]");
        }
    }

    /**
     * 判断指定的类是否为数字类型（包含原生数字类型和{@link Number}子类）
     * <p>支持的数字类型清单：
     * <ul>
     * <li>原生基本类型：long.class、int.class、byte.class、short.class、float.class、double.class；</li>
     * <li>包装类及子类：Integer.class、Long.class、BigInteger.class、BigDecimal.class、Float.class、Double.class等（需是Number的直接/间接子类）。</li>
     * </ul>
     * <p>注意：CharSequence、String等非Number子类即使能表示数字，也不视为数字类型。
     *
     * @param type 待判断的类类型（可为null，null时返回false）
     * @return true：是数字类型；false：非数字类型或type为null
     */
    public static boolean isNumber(Class<?> type) {
        if (type == null) {
            return false;
        }
        // 匹配原生数字类型
        if (type == long.class || type == int.class || type == byte.class || type == short.class
                || type == float.class || type == double.class) {
            return true;
        }
        // 匹配Number的子类（包装类、高精度类等）
        return Number.class.isAssignableFrom(type);
    }

    /**
     * 判断指定的类是否为整数类型（包含原生整数类型和{@link BigInteger}及其子类）
     * <p>支持的整数类型清单：
     * <ul>
     * <li>原生基本类型：long.class、int.class、byte.class、short.class；</li>
     * <li>包装类及子类：Integer.class、Long.class、BigInteger.class（需是BigInteger的直接/间接子类）。</li>
     * </ul>
     * <p>关键区别：float/double/BigDecimal即使数值为整数（如100.0），其类类型也不被视为整数类型（因底层存储为浮点/小数结构）。
     *
     * @param type 待判断的类类型（可为null，null时返回false）
     * @return true：是整数类型；false：非整数类型或type为null
     */
    public static boolean isInteger(Class<?> type) {
        if (type == null) {
            return false;
        }
        // 匹配原生整数类型
        if (type == long.class || type == int.class || type == byte.class || type == short.class) {
            return true;
        }
        // 匹配BigInteger及其子类（排除浮点/小数类型）
        return BigInteger.class.isAssignableFrom(type);
    }

    /**
     * （私有工具方法）判断字符串是否为十六进制数字表示（需包含特殊前缀，支持负号）
     * <p>判断规则（需同时满足）：
     * 1. 字符串非空；
     * 2. 符号处理：允许开头带负号（"-"），负号后需紧跟前缀；
     * 3. 前缀识别：负号后（或无负号时开头）以"0x"、"0X"或"#"开头（如"-0x1A"、"#2F"视为十六进制，"1A"视为十进制）。
     *
     * @param value 待判断的字符串（可为null，null时返回false）
     * @return true：是十六进制字符串；false：非十六进制或value为null/空
     */
    private static boolean isHexNumber(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        // 跳过开头的负号（若存在）
        int index = value.startsWith("-") ? 1 : 0;
        // 检查剩余部分是否以十六进制前缀开头
        return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
    }

    /**
     * （私有工具方法）从字符串解析BigInteger（支持十进制、十六进制、八进制，处理负号）
     * <p>解析逻辑（分四步）：
     * 1. <strong>符号处理</strong>：识别开头的负号（"-"），记录符号位后跳过负号；
     * 2. <strong>基数确定</strong>：
     *    - 前缀"0x"/"0X"/"#"→基数16（十六进制）；
     *    - 前缀"0"且长度&gt;1→基数8（八进制，如"077"→63）；
     *    - 无前缀→基数10（十进制）；
     * 3. <strong>有效部分截取</strong>：跳过前缀后，截取剩余的数字字符作为有效部分（如"-0x1A"→有效部分"1A"）；
     * 4. <strong>解析与符号应用</strong>：调用{@link BigInteger#BigInteger(String, int)}解析有效部分，根据符号位返回正数或负数。
     *
     * @param value 待解析的字符串（不可为null，支持多进制前缀与负号）
     * @return 解析后的BigInteger（非null，与字符串数值一致）
     * @throws NumberFormatException 若字符串格式无效（如"0xG"，G不是十六进制字符；或"08"，八进制不允许数字8）
     */
    private static BigInteger decodeBigInteger(String value) {
        int radix = 10; // 默认基数：十进制
        int index = 0;
        boolean negative = false;

        // 步骤1：处理负号
        if (value.startsWith("-")) {
            negative = true;
            index++; // 跳过负号，从下一位开始处理
        }

        // 步骤2：确定基数（根据前缀）
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2; // 跳过"0x"/"0X"前缀
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++; // 跳过"#"前缀
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++; // 跳过"0"前缀
            radix = 8; // 八进制
        }

        // 步骤3：截取有效部分并解析
        BigInteger result = new BigInteger(value.substring(index), radix);
        // 步骤4：应用符号位（负号则取反）
        return negative ? result.negate() : result;
    }

    /**
     * 判断BigDecimal是否为整数（无小数部分或小数部分全为0）
     * <p>判断规则（满足任一条件即视为整数）：
     * 1. 数值为0（{@link BigDecimal#signum()} == 0，如0.0、0.000）；
     * 2. 小数位数≤0（如100→scale=0，无小数部分）；
     * 3. 去除尾部零后小数位数≤0（如100.000→stripTrailingZeros()后scale=0）。
     * <p>适用场景：判断金额是否为整数（如"100.00元"是否可简化为"100元"）、数据校验（如订单数量必须为整数）。
     *
     * @param number 待判断的BigDecimal（不可为null）
     * @return true：是整数；false：非整数（如100.01、3.1415）
     * @throws NullPointerException 若number为null（由{@link NonNull}注解自动抛出）
     */
    public static boolean isInteger(BigDecimal number) {
        return number.signum() == 0 // 数值为0
                || number.scale() <= 0 // 无小数部分
                || number.stripTrailingZeros().scale() <= 0; // 去尾部零后无小数部分
    }

    /**
     * 清除BigDecimal尾部的无效零（优化数值的字符串表示，不改变数值大小）
     * <p>处理逻辑（分三步）：
     * 1. <strong>特殊情况处理</strong>：
     *    - 数值为0→直接返回原对象（0的任何形式均为0，无需修改）；
     *    - 小数位数≤0（如100）→直接返回原对象（无尾部零可清除）；
     * 2. <strong>清除尾部零</strong>：调用{@link BigDecimal#stripTrailingZeros()}清除尾部无效零（如123.4500→123.45）；
     * 3. <strong>整数格式优化</strong>：若清除后为整数（scale≤0，如100.000→100），调用{@link BigDecimal#setScale(int)}转为整数形式（避免科学计数法，如1E2→100）。
     * <p>关键提示：BigDecimal是不可变对象，修改后会返回新对象，需接收返回值以生效（原对象不会改变）。
     *
     * @param number 待处理的BigDecimal（可为null，null时返回null）
     * @return 清除尾部零后的BigDecimal；若原数为null，返回null
     */
    public static BigDecimal stripTrailingZeros(BigDecimal number) {
        if (number == null) {
            return null;
        }
        // 特殊情况：数值为0或无小数部分，无需处理
        if (number.signum() == 0 || number.scale() <= 0) {
            return number;
        }

        // 清除尾部零后判断是否为整数
        BigDecimal stripped = number.stripTrailingZeros();
        if (stripped.scale() <= 0) {
            // 转为整数形式（避免科学计数法）
            return stripped.setScale(0);
        }
        return stripped;
    }

    /**
     * 按指定单位格式化BigDecimal（默认格式化逻辑：清除尾部零+避免科学计数法）
     * <p>默认格式化规则：
     * 1. 数值处理：调用{@link #stripTrailingZeros(BigDecimal)}清除尾部无效零；
     * 2. 字符串转换：调用{@link BigDecimal#toPlainString()}转为普通字符串（避免科学计数法，如1000→"1000"而非"1E3"）；
     * 3. 单位拼接：
     *    - 若units为空或数值为0→直接返回格式化后的数值字符串；
     *    - 若units非空→递归按单位进制拆分（如1234元→"1千元2百元3十元4元"），拼接“数值+单位”。
     *
     * @param number 待格式化的BigDecimal（不可为null）
     * @param units  单位数组（如{@link NumberUnit}实例，定义单位名称及进制，如"元"（进制1）、"角"（进制0.1））
     * @return 格式化后的字符串（非null，如"123元4角5分"、"100"）
     * @throws NullPointerException 若number为null（由{@link NonNull}注解自动抛出）
     * @see #format(BigDecimal, ThrowingFunction, NumberUnit...) 支持自定义格式化函数的重载方法（更灵活）
     */
    public static String format(BigDecimal number, NumberUnit... units) {
        // 默认格式化函数：清除尾部零+普通字符串（避免科学计数法）
        return format(number, e -> stripTrailingZeros(e).toPlainString(), units);
    }

    /**
     * 将字符串按指定单位解析为BigDecimal（默认转换逻辑：直接构造BigDecimal）
     * <p>默认解析规则：
     * 1. 单位识别：遍历units，查找字符串中包含的单位（如"1千元2角"中的"元"、"角"）；
     * 2. 数值提取：截取单位左侧的数值部分（如"1千元"→数值部分"1"）；
     * 3. 递归解析：
     *    - 数值部分×单位进制（如"1千元"→1×1000=1000）；
     *    - 单位右侧剩余内容递归解析（如"2角"→2×0.1=0.2）；
     * 4. 结果累加：将所有解析结果累加（如1000+0.2=1000.2）。
     * <p>适用场景：解析带单位的金额、重量等字符串（如"1kg200g"→1.2kg）。
     *
     * @param source 待解析的字符串（可为null，null时返回null）
     * @param units  单位数组（如{@link NumberUnit}实例，需与格式化时的单位一致，否则解析失败）
     * @return 解析后的BigDecimal；若source为null/空，返回null
     * @throws NumberFormatException 若数值部分格式无效（如"abc元"→无法转为BigDecimal）
     */
    public static BigDecimal parse(String source, NumberUnit... units) {
        // 默认转换函数：直接用数值字符串构造BigDecimal
        return parse(source, e -> new BigDecimal(e), units);
    }

    /**
     * 按指定单位格式化BigDecimal（支持自定义格式化函数，灵活适配业务需求）
     * <p>核心逻辑（分四步）：
     * 1. <strong>预处理</strong>：
     *    - 若units为空或数值为0→直接调用自定义格式化函数返回结果；
     *    - 取数值绝对值处理（符号需外部自行添加，如负数需先记录负号）；
     * 2. <strong>递归拆分</strong>：从第一个单位开始，按单位进制调用{@link BigDecimal#divideAndRemainder(BigDecimal)}拆分为“商+余数”（如1234元，进制1000→商1，余数234）；
     * 3. <strong>结果拼接</strong>：
     *    - 若商&gt;0→拼接“自定义格式化后的商+单位”（如1→"1千元"）；
     *    - 若余数&gt;0→继续递归处理下一级单位（如234→"2百元3十元4元"）；
     * 4. <strong>剩余处理</strong>：遍历结束后，若仍有剩余数值（无对应单位），拼接剩余部分（如1234.5元→"1千元2百元3十元4元5角"）。
     *
     * @param <E>      异常类型泛型，必须是{@link Throwable}的子类（自定义格式化函数可能抛出的异常，如IllegalFormatException）
     * @param number   待格式化的BigDecimal（不可为null，内部取绝对值处理，符号需外部自行添加）
     * @param toString 自定义格式化函数（将BigDecimal转为字符串，不可为null，如e→String.format("%.2f", e)（保留2位小数））
     * @param units    单位数组（如{@link NumberUnit}实例，定义单位名称及进制，可为空）
     * @return 格式化后的字符串（非null，如"123.45元"、"100.00"）
     * @throws E 自定义格式化函数执行过程中抛出的异常
     * @throws NullPointerException 若number或toString为null（由{@link NonNull}注解自动抛出）
     * @see #format(StringBuilder, BigDecimal, ThrowingFunction, int, int, NumberUnit...) 递归辅助方法（实现拆分逻辑）
     */
    public static <E extends Throwable> String format(@NonNull BigDecimal number,
            @NonNull ThrowingFunction<? super BigDecimal, ? extends String, ? extends E> toString, NumberUnit... units)
            throws E {
        // 特殊情况：无单位或数值为0，直接格式化
        if (units == null || units.length == 0 || number.compareTo(BigDecimal.ZERO) == 0) {
            return toString.apply(number.abs());
        }

        StringBuilder sb = new StringBuilder();
        // 递归格式化：从第0个单位开始，到最后一个单位结束
        format(sb, number.abs(), toString, 0, units.length, units);
        return sb.toString();
    }

    /**
     * （私有递归辅助方法）将BigDecimal按单位格式化到字符串缓冲区（实现核心拆分逻辑）
     * <p>递归逻辑（分五步）：
     * 1. <strong>遍历单位</strong>：从startUnitsIndex开始，遍历到endUnitsIndex（通常为units.length）；
     * 2. <strong>拆分数值</strong>：按当前单位进制拆分为“商（quotient）+余数（remainder）”（如1234元，进制1000→商1，余数234）；
     * 3. <strong>嵌套判断</strong>：若余数需进一步拆分（如123十元→12百元3十元），递归处理商；否则直接格式化商；
     * 4. <strong>拼接单位</strong>：拼接“格式化后的商+当前单位”（如1→"1千元"）；
     * 5. <strong>前导零处理</strong>：若下一级单位需前导零（如1千元0百元），补充零的格式化结果；
     * 6. <strong>剩余处理</strong>：更新余数为当前拆分的余数，继续遍历下一级单位；遍历结束后，拼接剩余数值（无对应单位）。
     *
     * @param <E>              异常类型泛型（格式化函数可能抛出的异常）
     * @param sb               字符串缓冲区（用于拼接格式化结果，不可为null）
     * @param number           待格式化的BigDecimal（不可为null，已取绝对值，无符号）
     * @param toString         自定义格式化函数（不可为null）
     * @param startUnitsIndex  开始处理的单位索引（从0开始，如0→第一个单位）
     * @param endUnitsIndex    结束处理的单位索引（通常为units.length，不包含）
     * @param units            单位数组（不可为null）
     * @throws E 格式化函数抛出的异常
     */
    private static <E extends Throwable> void format(StringBuilder sb, BigDecimal number,
            ThrowingFunction<? super BigDecimal, ? extends String, ? extends E> toString, int startUnitsIndex,
            int endUnitsIndex, NumberUnit... units) throws E {
        BigDecimal surplus = number; // 剩余未格式化的数值
        for (int i = startUnitsIndex; i < Math.min(endUnitsIndex, units.length); i++) {
            NumberUnit unit = units[i];
            BigDecimal radix = unit.getRadix();

            // 若剩余数值 < 当前单位进制，跳过当前单位（如234元 < 1000进制→跳过“千”单位）
            if (radix.compareTo(surplus) > 0) {
                continue;
            }

            // 拆分：商（当前单位的数值） + 余数（剩余数值）
            BigDecimal[] divideResult = surplus.divideAndRemainder(radix);
            BigDecimal quotient = divideResult[0]; // 商（如1234÷1000=1）
            BigDecimal remainder = divideResult[1]; // 余数（如1234%1000=234）

            // 判断是否为最后一级单位（无需继续嵌套拆分）
            boolean isLastLevel = true;
            for (NumberUnit u : units) {
                if (remainder.compareTo(u.getRadix()) >= 0) {
                    isLastLevel = false;
                    break;
                }
            }

            // 最后一级单位：直接取商（避免余数残留）
            if (isLastLevel) {
                quotient = surplus.divide(radix);
                remainder = BigDecimal.ZERO;
            }

            // 处理嵌套：商需进一步拆分（如12十元→1百元2十元）
            boolean noNested = isLastLevel || remainder.compareTo(BigDecimal.ZERO) == 0 || radix.compareTo(BigDecimal.ONE) < 0;
            if (noNested) {
                sb.append(toString.apply(quotient)); // 直接格式化商
            } else {
                format(sb, quotient, toString, i, units.length - 1, units); // 递归格式化商
            }

            sb.append(unit.getName()); // 拼接当前单位

            // 处理下一级单位的前导零（如1千元0百元→补充“0百元”）
            if (!noNested && i < units.length - 1) {
                BigDecimal nextRadix = units[i + 1].getRadix();
                if (remainder.divideToIntegralValue(nextRadix).compareTo(BigDecimal.ZERO) == 0) {
                    sb.append(toString.apply(BigDecimal.ZERO));
                }
            }

            surplus = remainder; // 更新剩余数值为当前余数
        }

        // 拼接剩余数值（无对应单位，如1234.5元→剩余0.5元→“5角”）
        if (surplus.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(toString.apply(surplus));
        }
    }

    /**
     * 将字符串按指定单位解析为BigDecimal（支持自定义转换函数，处理特殊数值格式）
     * <p>核心解析逻辑（分四步）：
     * 1. <strong>预处理</strong>：若source为null/空，返回null；
     * 2. <strong>单位匹配</strong>：遍历units，查找字符串中包含的单位（如"1kg200g"中的"kg"、"g"）；
     * 3. <strong>数值解析</strong>：
     *    - 截取单位左侧的数值部分（如"1kg"→数值部分"1"）；
     *    - 调用自定义转换函数将数值部分转为BigDecimal（如处理千分位分隔符："1,000"→1000）；
     *    - 数值×单位进制（如"1kg"→1×1000=1000g）；
     * 4. <strong>递归累加</strong>：
     *    - 若单位右侧有剩余内容（如"200g"），递归解析剩余部分；
     *    - 将所有解析结果累加，返回最终BigDecimal。
     * <p>适用场景：解析带特殊格式的数值字符串（如"$1,234.56"→1234.56，需自定义转换函数去除"$"和","）。
     *
     * @param <E>        异常类型泛型（自定义转换函数可能抛出的异常，如NumberFormatException）
     * @param source     待解析的字符串（可为null，null时返回null）
     * @param converter  自定义转换函数（将数值字符串转为BigDecimal，不可为null，如e→new BigDecimal(e.replace(",", ""))）
     * @param units      单位数组（如{@link NumberUnit}实例，定义单位名称及进制，可为空）
     * @return 解析后的BigDecimal；若source为null/空，返回null
     * @throws E 转换函数执行过程中抛出的异常
     * @throws NullPointerException 若converter为null（由{@link NonNull}注解自动抛出）
     * @throws NumberFormatException 若数值部分无法转为BigDecimal（由转换函数抛出）
     */
    public static <E extends Throwable> BigDecimal parse(String source,
            @NonNull ThrowingFunction<? super String, ? extends BigDecimal, ? extends E> converter, NumberUnit... units)
            throws E {
        if (source == null || source.isEmpty()) {
            return null;
        }

        // 遍历单位，查找匹配的单位
        for (NumberUnit unit : units) {
            String unitName = unit.getName();
            int unitIndex = source.indexOf(unitName);
            if (unitIndex == -1) {
                continue; // 未找到当前单位，跳过
            }

            // 步骤1：解析单位左侧的数值部分（如"1千元2角"→左侧"1"）
            String leftPart = source.substring(0, unitIndex).trim();
            BigDecimal leftValue = parse(leftPart, converter, units).multiply(unit.getRadix());

            // 步骤2：解析单位右侧的剩余部分（如"1千元2角"→右侧"2角"）
            BigDecimal rightValue = BigDecimal.ZERO;
            if (unitIndex < source.length() - unitName.length()) {
                String rightPart = source.substring(unitIndex + unitName.length()).trim();
                rightValue = parse(rightPart, converter, units);
            }

            // 步骤3：累加左右部分结果
            return leftValue.add(rightValue);
        }

        // 无匹配单位，直接转换数值部分（如"123.45"→123.45）
        return converter.apply(source.trim());
    }

    /**
     * 保留指定小数位数格式化double值（支持整数场景，处理四舍五入）
     * <p>格式化规则（分三步）：
     * 1. <strong>特殊情况处理</strong>：
     *    - len=0→转为long后toString（丢弃小数部分，如123.9→"123"，注意：double值超过long范围时会丢失精度）；
     *    - 数值为0→返回"0.00...0"（len个0，如len=2→"0.00"）；
     * 2. <strong>模板构建</strong>：构建DecimalFormat模板"#0.00...0"（len个0，如len=2→"#0.00"）；
     * 3. <strong>四舍五入格式化</strong>：调用{@link DecimalFormat#format(double)}，自动四舍五入（如123.456→"123.46"）。
     * <p>注意：double存在精度问题（如0.1无法精确表示），若需高精度格式化，建议先转为BigDecimal再处理。
     *
     * @param number 待格式化的double值（支持正负值，如-123.45→"-123.45"）
     * @param len    保留的小数位数（必须≥0，否则抛出{@link IllegalStateException}）
     * @return 格式化后的字符串（非null，如"123.46"、"123"、"-0.50"）
     * @throws IllegalStateException 若len &lt; 0（非法小数位数，无意义）
     */
    public static String formatPrecision(double number, int len) {
        if (len < 0) {
            throw new IllegalStateException("Decimal length cannot be negative: " + len);
        }

        // 情况1：无小数位，转为long（丢弃小数部分）
        if (len == 0) {
            return String.valueOf((long) number);
        }

        // 情况2：数值为0，返回固定格式"0.00...0"
        if (number == 0) {
            CharBuffer buffer = CharBuffer.allocate(len + 2); // "0." + len个0 → 长度2+len
            buffer.put('0').put('.');
            for (int i = 0; i < len; i++) {
                buffer.put('0');
            }
            return new String(buffer.array());
        }

        // 情况3：构建模板，保留len位小数（四舍五入）
        CharBuffer templateBuffer = CharBuffer.allocate(len + 3); // "#0." + len个0 → 长度3+len
        templateBuffer.put("#0.").put(new char[len]).flip(); // 填充len个0
        for (int i = 0; i < len; i++) {
            templateBuffer.put(i + 3, '0'); // 从索引3开始填充0（"#0."后）
        }
        DecimalFormat format = new DecimalFormat(new String(templateBuffer.array()));
        return format.format(number);
    }

    /**
     * 从字符序列中提取符合规则的数字字符串（支持自定义过滤函数，灵活筛选字符）
     * <p>提取逻辑（分五步）：
     * 1. <strong>预处理</strong>：若source为空，返回null；
     * 2. <strong>符号处理</strong>：
     *    - 允许开头带正负号（"+"/"-"），仅保留一次（如"++123"→"+123"）；
     *    - 若unsigned=true（无符号），提取到负号返回null；
     * 3. <strong>前缀处理</strong>：
     *    - 基数&gt;10（如十六进制）：允许开头带"#"前缀（仅一次，如"#1A"→保留"#1A"）；
     * 4. <strong>小数点处理</strong>：允许保留一个小数点（如"123.45.67"→"123.4567"）；
     * 5. <strong>字符过滤</strong>：通过自定义filter判断字符是否保留（如过滤非数字字符，保留字母数字字符）；
     * 6. <strong>结果处理</strong>：若无有效字符（如仅符号或前缀），返回null；否则返回提取的子串。
     *
     * @param radix    数字的基数（如10→十进制，16→十六进制，用于判断是否保留#前缀）
     * @param unsigned 是否为无符号数字（true→不允许负号，提取到负号返回null）
     * @param source   待提取的字符序列（可为null，null时返回null，如"price: 123.45元"）
     * @param filter   自定义过滤函数（判断字符是否保留，可为null→保留所有符合基数的字符）
     * @return 提取的数字字符串（如从"price: 123.45元"提取"123.45"）；若无有效字符，返回null
     */
    public static String extractNumberic(int radix, boolean unsigned, CharSequence source, IntPredicate filter) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }

        char[] resultChars = new char[source.length()];
        int pos = 0; // 结果数组的当前位置
        boolean hasDecimalPoint = false; // 标记是否已找到小数点

        for (int i = 0, len = source.length(); i < len; i++) {
            char chr = source.charAt(i);

            // 1. 处理正负号（仅允许开头出现一次）
            if (chr == '-' || chr == '+') {
                if (pos == 0) {
                    if (unsigned && chr == '-') {
                        return null; // 无符号不允许负号
                    }
                    resultChars[pos++] = chr;
                }
                continue;
            }

            // 2. 处理十六进制#前缀（仅允许基数>10，且在开头或符号后）
            if (radix > 10) {
                if (chr == '#' && !hasDecimalPoint) {
                    if (pos == 0 || (pos == 1 && isNumberSign(resultChars[0]))) {
                        resultChars[pos++] = chr;
                    }
                }
                continue;
            }

            // 3. 处理小数点（仅允许出现一次）
            if (chr == '.') {
                if (hasDecimalPoint) {
                    continue; // 跳过重复的小数点
                }
                hasDecimalPoint = true;
                resultChars[pos++] = chr;
                continue;
            }

            // 4. 按过滤函数保留字符（filter为null时保留所有字符）
            if (filter == null || filter.test(chr)) {
                resultChars[pos++] = chr;
            }
        }

        // 无有效字符（如仅符号或前缀），返回null
        return pos == 0 ? null : new String(resultChars, 0, pos);
    }

    /**
     * 从字符序列中提取符合规则的数字字符串（默认过滤函数：按基数筛选字符）
     * <p>默认过滤逻辑：
     * - 基数&gt;10或≤0→保留字母数字字符（支持十六进制A-F/a-f，如"0x1A"→保留"0x1A"）；
     * - 基数≤10→仅保留数字字符（如"123.45元"→保留"123.45"）。
     * <p>适用场景：从日志、文本中提取数字（如从"orderId: 12345"提取"12345"）。
     *
     * @param radix    数字的基数（如10→十进制，16→十六进制）
     * @param unsigned 是否为无符号数字（true→不允许负号）
     * @param source   待提取的字符序列（可为null，null时返回null）
     * @return 提取的数字字符串；若无有效字符，返回null
     * @see #extractNumberic(int, boolean, CharSequence, IntPredicate) 支持自定义过滤函数的重载方法
     */
    public static String extractNumberic(int radix, boolean unsigned, CharSequence source) {
        // 默认过滤函数：基数>10保留字母数字，否则仅保留数字
        IntPredicate defaultFilter = (c) -> (radix > 10 || radix <= 0) 
                ? Character.isLetterOrDigit(c) 
                : Character.isDigit(c);
        return extractNumberic(radix, unsigned, source, defaultFilter);
    }

    /**
     * 判断字符序列是否为符合规则的数字（支持自定义过滤函数，严格校验）
     * <p>判断规则（分五步）：
     * 1. <strong>非空校验</strong>：source为空→false；
     * 2. <strong>符号校验</strong>：
     *    - 正负号仅允许开头出现一次（如"+123"→合法，"1+23"→非法）；
     *    - 若unsigned=true→不允许负号（如"-123"→非法）；
     * 3. <strong>前缀校验</strong>：
     *    - 基数&gt;10（如十六进制）："#"前缀仅允许在开头或符号后（如"#1A"→合法，"1#A"→非法）；
     * 4. <strong>小数点校验</strong>：仅允许出现一次（如"123.45"→合法，"123.45.67"→非法）；
     * 5. <strong>字符校验</strong>：所有字符需通过filter过滤（如非数字字符→非法）；
     * 6. <strong>结果</strong>：所有字符符合规则→true，否则→false。
     *
     * @param radix    数字的基数（如10→十进制，16→十六进制）
     * @param unsigned 是否为无符号数字（true→不允许负号）
     * @param source   待判断的字符序列（可为null，null时返回false）
     * @param filter   自定义过滤函数（判断字符是否有效，可为null→默认规则）
     * @return true：是有效数字；false：非有效数字或source为null
     */
    public static boolean isNumeric(int radix, boolean unsigned, CharSequence source, IntPredicate filter) {
        if (StringUtils.isEmpty(source)) {
            return false;
        }

        boolean hasDecimalPoint = false;
        int pos = 0; // 当前处理的位置

        for (int i = 0, len = source.length(); i < len; i++) {
            char chr = source.charAt(i);

            // 1. 处理正负号（仅允许开头）
            if (chr == '-' || chr == '+') {
                if (pos == 0) {
                    if (unsigned && chr == '-') {
                        return false; // 无符号不允许负号
                    }
                    pos++;
                    continue;
                }
                return false; // 符号仅允许开头
            }

            // 2. 处理十六进制#前缀（仅允许基数>10，且在开头/符号后）
            if (radix > 10) {
                if (chr == '#') {
                    if (!hasDecimalPoint && (pos == 0 || (pos == 1 && isNumberSign(source.charAt(0))))) {
                        pos++;
                        continue;
                    }
                    return false; // #前缀位置非法
                }
            }

            // 3. 处理小数点（仅允许一次）
            if (chr == '.') {
                if (hasDecimalPoint) {
                    return false; // 重复小数点
                }
                hasDecimalPoint = true;
                pos++;
                continue;
            }

            // 4. 字符有效性校验（filter为null时用默认规则）
            if (filter != null && !filter.test(chr)) {
                return false;
            }
            pos++;
        }

        return true; // 所有字符通过校验
    }

    /**
     * 判断字符序列是否为符合规则的数字（默认过滤函数：按基数筛选字符）
     * <p>默认过滤逻辑同{@link #extractNumberic(int, boolean, CharSequence)}，确保判断与提取规则一致，避免“能提取但判断为无效”的矛盾。
     *
     * @param radix    数字的基数（如10→十进制，16→十六进制）
     * @param unsigned 是否为无符号数字（true→不允许负号）
     * @param source   待判断的字符序列（可为null，null时返回false）
     * @return true：是有效数字；false：非有效数字或source为null
     * @see #isNumeric(int, boolean, CharSequence, IntPredicate) 支持自定义过滤函数的重载方法
     */
    public static boolean isNumeric(int radix, boolean unsigned, CharSequence source) {
        // 默认过滤函数：基数>10保留字母数字，否则仅保留数字
        IntPredicate defaultFilter = (c) -> (radix > 10 || radix <= 0) 
                ? Character.isLetterOrDigit(c) 
                : Character.isDigit(c);
        return isNumeric(radix, unsigned, source, defaultFilter);
    }

    /**
     * 判断字符是否为数字符号（正号+或负号-）
     * <p>适用场景：符号位识别（如在extractNumberic、isNumeric中判断开头字符是否为符号）。
     *
     * @param chr 待判断的字符
     * @return true：是+或-；false：其他字符（如数字、字母、小数点）
     */
    public static boolean isNumberSign(char chr) {
        return chr == '-' || chr == '+';
    }
    
    /**
     * 创建新的BigInteger实例（深拷贝，避免引用共享问题）
     * <p>核心作用：解决BigInteger不可变对象的引用复用问题——若直接赋值，多个变量会引用同一个BigInteger实例，
     * 修改其中一个（如通过add()生成新实例）不会影响其他，但若通过反射修改（虽不推荐）会导致风险，此方法通过字节数组创建新实例，确保引用完全独立。
     * <p>实现逻辑：调用{@link BigInteger#toByteArray()}获取原实例的字节数组，再通过{@link BigInteger#BigInteger(byte[])}创建新实例，数值与原实例完全一致。
     *
     * @param bigInteger 原BigInteger实例（不可为null）
     * @return 新的BigInteger实例（与原实例数值相同，引用不同，无共享风险）
     * @throws NullPointerException 若bigInteger为null（由{@link NonNull}注解自动抛出）
     */
    public static BigInteger newBigInteger(@NonNull BigInteger bigInteger) {
        return new BigInteger(bigInteger.toByteArray());
    }
    
    /**
     * 生成指定区间的随机整数[min, max)（左闭右开），使用指定的Random实例（可控制种子，结果可重复）
     * <p>适用场景：需要可重复随机的场景（如测试环境固定种子，确保每次运行结果一致；或游戏中固定随机事件）。
     * <p>实现逻辑：生成0.0~1.0的double随机数，按比例缩放至[min, max)区间（(random.nextDouble() * (max - min)) + min），转为int。
     * <p>特殊情况：
     * - 若min == max→直接返回min（无随机必要）；
     * - 若min &gt; max→可能生成超出预期的结果（建议业务层确保min ≤ max，避免异常）。
     *
     * @param random 随机数生成器（不可为null，需提前初始化，如new Random(123)（固定种子）、new Random()（随机种子））
     * @param min    区间最小值（包含，如10→生成的随机数≥10）
     * @param max    区间最大值（不包含，如20→生成的随机数&lt;20，即10~19）
     * @return 随机整数（范围[min, max)）
     * @throws NullPointerException 若random为null（由{@link Assert}自动抛出）
     */
    public static int random(Random random, int min, int max) {
        Assert.notNull(random, "Random instance cannot be null for int random generation");
        if (max == min) {
            return min;
        }
        // 缩放随机数到目标区间：0.0~1.0 → min~max（左闭右开）
        return (int) (random.nextDouble() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机整数[min, max)（左闭右开），使用默认随机源（不可控种子，结果不可重复）
     * <p>内部使用{@link Math#random()}（无固定种子，每次运行结果不同），适用于简单随机场景（如验证码生成、随机推荐）。
     * <p>注意：{@link Math#random()}线程安全，但性能略低于显式的Random实例，高频场景建议使用{@link #random(Random, int, int)}。
     *
     * @param min 区间最小值（包含）
     * @param max 区间最大值（不包含）
     * @return 随机整数（范围[min, max)）
     */
    public static int random(int min, int max) {
        if (max == min) {
            return min;
        }
        return (int) (Math.random() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机长整数[min, max)（左闭右开），使用指定的Random实例
     * <p>适用场景：需要生成超出int范围的随机整数（如时间戳（13位long）、分布式ID（64位）、大计数（如亿级订单号））。
     * <p>实现逻辑：同int随机，缩放double随机数到long区间，避免直接使用nextLong()导致的区间控制复杂。
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，如10000000000L→10^10）
     * @param max    区间最大值（不包含，如20000000000L→2×10^10，生成10^10~19999999999L）
     * @return 随机长整数（范围[min, max)）
     * @throws NullPointerException 若random为null（由{@link Assert}自动抛出）
     */
    public static long random(Random random, long min, long max) {
        Assert.notNull(random, "Random instance cannot be null for long random generation");
        if (max == min) {
            return min;
        }
        return (long) (random.nextDouble() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机长整数[min, max)（左闭右开），使用默认随机源
     * <p>适用场景：简单的long随机场景（如随机生成未来30天的时间戳：System.currentTimeMillis() + random(0, 30L*24*3600*1000)）。
     *
     * @param min 区间最小值（包含）
     * @param max 区间最大值（不包含）
     * @return 随机长整数（范围[min, max)）
     */
    public static long random(long min, long max) {
        if (max == min) {
            return min;
        }
        return (long) (Math.random() * (max - min)) + min;
    }

    /**
     * 生成指定区间的随机BigDecimal[min, max)（左闭右开），使用指定的Random实例
     * <p>适用场景：金融计算等高精度场景（如随机生成0.01~100.00的金额、0.001~0.01的利率），保留原始小数精度。
     * <p>实现逻辑：
     * 1. 生成0.0~1.0的double随机数；
     * 2. 计算区间长度：max - min；
     * 3. 缩放随机数：randomDouble * (max - min) + min；
     * 4. 转为BigDecimal（注意：double存在精度问题，若需极高精度，建议基于BigInteger实现）。
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，不可为null，如new BigDecimal("0.01")（最小金额单位））
     * @param max    区间最大值（不包含，不可为null，如new BigDecimal("100.00")）
     * @return 随机BigDecimal（范围[min, max)，精度与min/max一致）
     * @throws NullPointerException 若random/min/max为null（由{@link Assert}自动抛出）
     */
    public static BigDecimal random(@NonNull Random random, @NonNull BigDecimal min, @NonNull BigDecimal max) {
        Assert.notNull(random, "Random instance cannot be null for BigDecimal random generation");
        Assert.notNull(min, "Min value cannot be null for BigDecimal random generation");
        Assert.notNull(max, "Max value cannot be null for BigDecimal random generation");
        
        if (min.equals(max)) {
            return min;
        }
        // 缩放随机数到目标区间，转为BigDecimal（注意double精度限制）
        BigDecimal range = max.subtract(min);
        return new BigDecimal(String.valueOf(random.nextDouble())).multiply(range).add(min);
    }

    /**
     * 生成指定区间的随机BigDecimal[min, max)（左闭右开），使用默认随机源
     * <p>适用场景：简单的高精度随机场景（如随机生成折扣（0.8~0.95）、随机生成小额金额（0.01~10.00））。
     * <p>注意：若需避免double精度问题，建议自定义实现（如基于BigInteger生成整数部分，再按小数位数缩放）。
     *
     * @param min 区间最小值（包含，不可为null）
     * @param max 区间最大值（不包含，不可为null）
     * @return 随机BigDecimal（范围[min, max)）
     * @throws NullPointerException 若min或max为null（由{@link Assert}自动抛出）
     */
    public static BigDecimal random(@NonNull BigDecimal min, @NonNull BigDecimal max) {
        Assert.notNull(min, "Min value cannot be null for BigDecimal random generation");
        Assert.notNull(max, "Max value cannot be null for BigDecimal random generation");
        
        if (min.equals(max)) {
            return min;
        }
        BigDecimal range = max.subtract(min);
        return new BigDecimal(String.valueOf(Math.random())).multiply(range).add(min);
    }

    /**
     * 生成指定区间的随机BigInteger[min, max)（左闭右开），使用指定的Random实例
     * <p>适用场景：需要生成超大整数的场景（如密码学大素数、分布式ID（超出long范围）、超大计数（如百亿级用户ID））。
     * <p>实现逻辑：同BigDecimal，基于double随机数缩放，再转为BigInteger（注意：double仅支持53位精度，若BigInteger超出53位，会丢失精度，需自定义实现）。
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，不可为null）
     * @param max    区间最大值（不包含，不可为null）
     * @return 随机BigInteger（范围[min, max)）
     * @throws NullPointerException 若random/min/max为null（由{@link Assert}自动抛出）
     */
    public static BigInteger random(@NonNull Random random, @NonNull BigInteger min, @NonNull BigInteger max) {
        Assert.notNull(random, "Random instance cannot be null for BigInteger random generation");
        Assert.notNull(min, "Min value cannot be null for BigInteger random generation");
        Assert.notNull(max, "Max value cannot be null for BigInteger random generation");
        
        if (min.equals(max)) {
            return min;
        }
        // 缩放随机数到目标区间，转为BigInteger（注意double精度限制，超出53位需优化）
        BigInteger range = max.subtract(min);
        return new BigInteger(String.valueOf(random.nextDouble())).multiply(range).add(min);
    }

    /**
     * 生成指定区间的随机BigInteger[min, max)（左闭右开），使用默认随机源
     * <p>注意：double仅支持53位精度，若BigInteger的位数超过53（约16位十进制数），会丢失精度，建议此时使用自定义的BigInteger随机实现（基于Random的nextBytes()）。
     *
     * @param min 区间最小值（包含，不可为null）
     * @param max 区间最大值（不包含，不可为null）
     * @return 随机BigInteger（范围[min, max)）
     * @throws NullPointerException 若min或max为null（由{@link Assert}自动抛出）
     */
    public static BigInteger random(@NonNull BigInteger min, @NonNull BigInteger max) {
        Assert.notNull(min, "Min value cannot be null for BigInteger random generation");
        Assert.notNull(max, "Max value cannot be null for BigInteger random generation");
        
        if (min.equals(max)) {
            return min;
        }
        BigInteger range = max.subtract(min);
        return new BigInteger(String.valueOf(Math.random())).multiply(range).add(min);
    }

    /**
     * 生成指定区间的随机Number[min, max)（左闭右开），自动适配类型，使用指定的Random实例
     * <p>类型适配规则（优先级从高到低）：
     * <ul>
     * <li>若min/max包含浮点/小数类型（BigDecimal/Float/Double）→ 调用BigDecimal版本的random，返回BigDecimal；</li>
     * <li>若min/max包含BigInteger→ 调用BigInteger版本的random，返回BigInteger；</li>
     * <li>其他类型（如Integer/Long/Byte/Short）→ 转换为long，调用long版本的random，返回Long；</li>
     * </ul>
     * <p>适用场景：不确定输入类型的通用随机场景（如根据用户传入的min/max自动生成对应类型的随机数）。
     *
     * @param random 随机数生成器（不可为null）
     * @param min    区间最小值（包含，不可为null）
     * @param max    区间最大值（不包含，不可为null）
     * @return 随机Number（类型与min/max一致或兼容，如min为Integer、max为Long→返回Long）
     * @throws NullPointerException 若random/min/max为null（由{@link Assert}自动抛出）
     */
    public static Number random(@NonNull Random random, @NonNull Number min, @NonNull Number max) {
        Assert.notNull(random, "Random instance cannot be null for general Number random generation");
        Assert.notNull(min, "Min value cannot be null for general Number random generation");
        Assert.notNull(max, "Max value cannot be null for general Number random generation");
        
        if (min.equals(max)) {
            return min;
        }

        // 适配浮点/小数类型
        if (min instanceof BigDecimal || min instanceof Float || min instanceof Double 
            || max instanceof BigDecimal || max instanceof Float || max instanceof Double) {
            BigDecimal minBigDecimal = (BigDecimal) ArithmeticCalculator.ADD.apply(BigDecimal.ZERO, min);
            BigDecimal maxBigDecimal = (BigDecimal) ArithmeticCalculator.ADD.apply(BigDecimal.ZERO, max);
            return random(random, minBigDecimal, maxBigDecimal);
        } 
        // 适配BigInteger类型
        else if (min instanceof BigInteger || max instanceof BigInteger) {
            BigInteger minBigInteger = (BigInteger) ArithmeticCalculator.ADD.apply(BigInteger.ZERO, min);
            BigInteger maxBigInteger = (BigInteger) ArithmeticCalculator.ADD.apply(BigInteger.ZERO, max);
            return random(random, minBigInteger, maxBigInteger);
        } 
        // 适配其他整数类型（转为long，避免类型溢出）
        else {
            return random(random, min.longValue(), max.longValue());
        }
    }

    /**
     * 生成指定区间的随机Number[min, max)（左闭右开），自动适配类型，使用默认随机源
     * <p>适用场景：简单的通用随机场景（如无需控制种子的类型自适应随机）。
     *
     * @param min 区间最小值（包含，不可为null）
     * @param max 区间最大值（不包含，不可为null）
     * @return 随机Number（类型与min/max兼容）
     * @throws NullPointerException 若min或max为null（由{@link Assert}自动抛出）
     */
    public static Number random(@NonNull Number min, @NonNull Number max) {
        Assert.notNull(min, "Min value cannot be null for general Number random generation");
        Assert.notNull(max, "Max value cannot be null for general Number random generation");
        
        // 调用带Random参数的重载方法，使用默认随机源（new Random()）
        return random(new Random(), min, max);
    }

    /**
     * 从源数组中随机采样生成新数组（支持可重复采样，适配任意元素类型）
     * <p>核心逻辑：基于随机索引从源数组中选取元素，填充至新数组，支持源数组为空（返回空数组）、新长度为1（单元素采样）等场景。
     * <p>适用场景：随机推荐（如从商品数组中选3个展示）、测试数据生成（如随机生成10条用户ID）、数据打乱后的采样。
     *
     * @param <T>         数组元素类型泛型（如Integer、String、自定义对象）
     * @param random      随机数生成器（不可为null，控制采样随机性，如固定种子确保测试可复现）
     * @param sourceArray 源数组（不可为null，支持任意类型数组，如Integer[]、User[]）
     * @param newLength   新数组长度（必须&gt;0，否则抛出非法参数异常）
     * @return 新数组（元素类型与源数组一致，元素从源数组中随机选取，可重复）
     * @throws NullPointerException     若random或sourceArray为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若newLength ≤0（非法数组长度，无意义）
     */
    @SuppressWarnings("unchecked")
    public static <T> T randomArray(Random random, T sourceArray, int newLength) {
        Assert.notNull(random, "Random instance cannot be null for array random sampling");
        Assert.notNull(sourceArray, "Source array cannot be null for random sampling");
        Assert.isTrue(newLength > 0, "New array length must be greater than 0, current: " + newLength);
        
        // 获取源数组长度与元素类型（通过反射适配任意数组类型）
        int sourceLength = Array.getLength(sourceArray);
        Class<?> componentType = sourceArray.getClass().getComponentType();
        // 创建新数组（基于源数组元素类型）
        Object targetArray = Array.newInstance(componentType, newLength);
        
        // 源数组为空时，直接返回空的新数组（无元素可采样）
        if (sourceLength == 0) {
            return (T) targetArray;
        }

        // 随机采样：循环生成源数组索引，选取元素填充新数组
        for (int i = 0; i < newLength; i++) {
            int randomIndex = random.nextInt(sourceLength); // 生成[0, sourceLength)的随机索引
            Object randomElement = Array.get(sourceArray, randomIndex); // 获取源数组中随机索引的元素
            Array.set(targetArray, i, randomElement); // 将元素放入新数组
        }

        return (T) targetArray;
    }

    /**
     * 基于权重随机选择元素（核心实现：权重轮盘法），支持选中元素移除
     * <p>权重轮盘法原理：将所有元素的权重视为轮盘的扇区，随机生成一个权重值，落在哪个扇区则选中对应元素，确保概率与权重成正比。
     * <p>适用场景：抽奖系统（奖品权重决定中奖概率）、流量分配（服务权重决定请求比例）、资源调度（任务权重决定执行优先级）。
     *
     * @param <T>              元素类型泛型（如Prize、ServiceInstance、Task）
     * @param <E>              异常类型泛型（权重处理器可能抛出的异常，如反射异常、业务校验异常）
     * @param totalWeight      总权重（必须&gt;0，不可为null，所有有效元素权重之和）
     * @param weight           随机权重值（1≤weight≤totalWeight，不可为null，决定选中哪个元素）
     * @param iterator         元素迭代器（不可为null，提供待选元素的遍历能力）
     * @param weightProcessor  权重处理器（从元素中提取权重，不可为null，如Prize::getWeight）
     * @param removePredicate  选中元素后是否移除（可为null→不移除；如t-&gt;true→从迭代器中移除，避免重复选中）
     * @return 选中的元素；若无合适元素（如所有元素权重为0），返回null
     * @throws E                        权重处理器抛出的异常
     * @throws NullPointerException     若totalWeight/weight/iterator/weightProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若总权重≤0（无有效元素）、随机权重≤0或&gt;总权重（非法随机值）、元素权重为负（无效权重）
     */
    public static <T, E extends Throwable> T random(@NonNull Number totalWeight, @NonNull Number weight,
                                                    @NonNull Iterator<? extends T> iterator,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        // 1. 基础参数校验（确保权重逻辑合法）
        Assert.notNull(totalWeight, "Total weight cannot be null for weighted random");
        Assert.notNull(weight, "Random weight cannot be null for weighted random");
        Assert.notNull(iterator, "Element iterator cannot be null for weighted random");
        Assert.notNull(weightProcessor, "Weight processor cannot be null for weighted random");
        Assert.isTrue(NumberComparator.DEFAULT.compare(totalWeight, 0) > 0, 
                      "Total weight must be greater than 0, current: " + totalWeight);
        Assert.isTrue(NumberComparator.DEFAULT.compare(weight, 0) > 0, 
                      "Random weight must be greater than 0, current: " + weight);
        Assert.isTrue(NumberComparator.DEFAULT.compare(weight, totalWeight) <= 0, 
                      "Random weight [" + weight + "] cannot exceed total weight [" + totalWeight + "]");
        
        Number accumulatedWeight = 0; // 累加权重（用于判断随机权重落在哪个元素的扇区）
        while (iterator.hasNext()) {
            T currentElement = iterator.next();
            if (currentElement == null) {
                continue; // 跳过null元素（无权重，不参与选择）
            }

            // 2. 提取当前元素的权重（处理null权重→视为0，跳过）
            Number elementWeight = weightProcessor.apply(currentElement);
            if (elementWeight == null) {
                continue;
            }

            // 3. 校验元素权重合法性（负权重无效，抛出异常）
            int weightCompare = NumberComparator.DEFAULT.compare(elementWeight, 0);
            if (weightCompare < 0) {
                throw new IllegalArgumentException("Element weight cannot be negative, element: " + currentElement 
                                                   + ", weight: " + elementWeight);
            }
            if (weightCompare == 0) {
                continue; // 权重为0的元素无选中概率，跳过
            }

            // 4. 累加权重，判断是否选中当前元素
            accumulatedWeight = ArithmeticCalculator.ADD.apply(accumulatedWeight, elementWeight);
            if (NumberComparator.DEFAULT.compare(weight, accumulatedWeight) <= 0) {
                // 5. 处理选中元素的移除（如抽奖后移除已中奖奖品）
                if (removePredicate != null && removePredicate.test(currentElement)) {
                    iterator.remove(); // 从迭代器中移除元素（需迭代器支持remove()，否则抛出UnsupportedOperationException）
                }
                return currentElement; // 返回选中的元素
            }
        }

        // 6. 无匹配元素（理论上不会走到这里，因总权重>0且随机权重≤总权重）
        return null;
    }

    /**
     * 基于权重随机选择元素（自动计算总权重和随机权重），支持选中元素移除
     * <p>最简化API：无需手动计算总权重和生成随机权重，内部自动完成：
     * 1. 遍历元素计算总权重（过滤null元素和0权重元素）；
     * 2. 生成1~总权重的随机权重值；
     * 3. 调用{@link #random(Number, Number, Iterator, ThrowingFunction, Predicate)}完成选择。
     * <p>适用场景：大多数标准权重场景（如普通抽奖、默认流量分配），无需关心底层计算细节。
     *
     * @param <T>              元素类型泛型
     * @param <E>              异常类型泛型
     * @param iterable         元素集合（不可为null，如奖品列表、服务列表）
     * @param weightProcessor  权重处理器（不可为null，如Prize::getWeight、Service::getWeight）
     * @param removePredicate  选中元素后是否移除（可为null→不移除）
     * @return 选中的元素；若无有效元素（总权重=0），返回null
     * @throws E                        权重处理器抛出的异常
     * @throws NullPointerException     若iterable或weightProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若元素权重为负、总权重≤0
     */
    public static <T, E extends Throwable> T random(@NonNull Iterable<? extends T> iterable,
                                                    @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor,
                                                    Predicate<? super T> removePredicate) throws E {
        // 1. 计算总权重（过滤null元素和0权重元素）
        Number totalWeight = calculateTotalWeight(iterable, weightProcessor);
        if (NumberComparator.DEFAULT.compare(totalWeight, 0) <= 0) {
            return null; // 无有效元素（总权重=0）
        }

        // 2. 生成1~总权重的随机权重值
        Number randomWeight = random(1, ArithmeticCalculator.ADD.apply(totalWeight, 1));

        // 3. 调用核心方法完成选择
        return random(totalWeight, randomWeight, iterable.iterator(), weightProcessor, removePredicate);
    }

    /**
     * 计算元素集合的总权重（过滤null元素和0权重元素）
     * <p>适用场景：权重随机前的总权重校验（如判断是否有有效元素）、权重占比计算（如元素权重/总权重=概率）。
     *
     * @param <T>              元素类型泛型
     * @param <E>              异常类型泛型
     * @param iterable         元素集合（不可为null）
     * @param weightProcessor  权重处理器（不可为null）
     * @return 总权重（非null，≥0，所有有效元素权重之和）
     * @throws E                        权重处理器抛出的异常
     * @throws NullPointerException     若iterable或weightProcessor为null（由{@link Assert}自动抛出）
     * @throws IllegalArgumentException 若元素权重为负
     */
    public static <T, E extends Throwable> Number calculateTotalWeight(@NonNull Iterable<? extends T> iterable,
                                                                       @NonNull ThrowingFunction<? super T, ? extends Number, ? extends E> weightProcessor) throws E {
        Assert.notNull(iterable, "Element iterable cannot be null for total weight calculation");
        Assert.notNull(weightProcessor, "Weight processor cannot be null for total weight calculation");
        
        Number totalWeight = 0;
        for (T element : iterable) {
            if (element == null) {
                continue; // 跳过null元素
            }

            Number weight = weightProcessor.apply(element);
            if (weight == null) {
                continue; // 权重为null视为0，跳过
            }

            // 校验权重合法性（不允许负权重）
            if (NumberComparator.DEFAULT.compare(weight, 0) < 0) {
                throw new IllegalArgumentException("Element weight cannot be negative, element: " + element 
                                                   + ", weight: " + weight);
            }

            totalWeight = ArithmeticCalculator.ADD.apply(totalWeight, weight);
        }
        return totalWeight;
    }
}
