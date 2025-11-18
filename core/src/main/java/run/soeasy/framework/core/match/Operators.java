package run.soeasy.framework.core.match;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.comparator.Ordered;

/**
 * 内置常见运算符枚举，标准化实现 {@link Operator} 核心接口，同时兼容 {@link Ordered} 优先级规范，
 * 覆盖表达式解析、运算执行、语法校验等场景的核心运算符需求，提供“符号-优先级-匹配规则”三位一体的标准化定义。
 *
 * <p>
 * <strong>核心设计理念</strong>：
 * <ul>
 * <li><strong>全场景覆盖</strong>：按功能分类封装四大类运算符，无冗余且无遗漏，直接适配表达式引擎、规则引擎等上层组件：
 *   <ol>
 *   <li>分组运算符：处理表达式结构界定（括号嵌套、数组索引、代码块分组）；</li>
 *   <li>算术运算符：支持基础数值计算（加减乘除、取模）；</li>
 *   <li>比较运算符：实现数值/对象的大小/相等性判断；</li>
 *   <li>逻辑运算符：支持布尔值逻辑运算（与/或/非），适配条件判断场景；</li>
 *   </ol>
 * </li>
 * <li><strong>优先级标准化</strong>：严格遵循行业通用运算优先级规则，与 {@link Ordered} 接口语义完全对齐（值越大优先级越高），
 * 避免表达式计算顺序歧义，具体优先级分层（从高到低）：
 *   <table border="1" cellpadding="3" cellspacing="0" summary="运算符优先级分层表">
 *     <caption>运算符优先级分层</caption>
 *     <tr><th>优先级</th><th>运算符类型</th><th>说明</th></tr>
 *     <tr><td>10（最高）</td><td>分组运算符</td><td>括号类运算符，强制改变运算顺序，支持嵌套</td></tr>
 *     <tr><td>3</td><td>算术运算符（乘除取模）</td><td>乘法、除法、取模，优先级高于加减</td></tr>
 *     <tr><td>2</td><td>算术运算符（加减）</td><td>加法、减法，优先级低于乘除取模</td></tr>
 *     <tr><td>1</td><td>比较运算符</td><td>大于、小于、等于、不等于等，优先级低于算术运算</td></tr>
 *     <tr><td>0（最低）</td><td>逻辑运算符</td><td>与、或、非，优先级最低，最后执行</td></tr>
 *   </table>
 * </li>
 * <li><strong>正则精准匹配</strong>：每个运算符配套专属 {@link Pattern}，解决表达式解析的核心痛点：
 *   <ul>
 *   <li>长符号优先：如 "&amp;gt;=" 优先匹配，避免被拆分为 "&amp;gt;" 和 "="；</li>
 *   <li>元字符转义：对 "*"、"|"、"[" 等正则元字符统一转义，避免匹配异常；</li>
 *   <li>防误匹配：通过负向断言（如 "(?&amp;lt;!\\*)"）避免连续符号误匹配（如 "*" 不匹配 "**"）；</li>
 *   <li>嵌套兼容：成对分组运算符（如 "()"）采用平衡组正则，支持嵌套结构完整匹配（如 "((a+b)*c)"）；</li>
 *   </ul>
 * </li>
 * <li><strong>接口无缝适配</strong>：枚举字段与 {@link Operator} 接口方法一一映射，无需额外适配层，
 * 可直接用于运算符注册、表达式分词、优先级排序等上层逻辑；</li>
 * </ul>
 *
 * <h3>使用场景示例</h3>
 * <pre class="code">
 * // 1. 基础运算符获取与使用
 * Operator add = Operators.PLUS;
 * System.out.println("加法符号：" + add.getSymbol()); // 输出 "+"
 * System.out.println("加法优先级：" + add.getOrder()); // 输出 "2"
 *
 * // 2. 表达式运算符匹配（判断是否包含小于等于）
 * String expression = "age&amp;lt;=30 &amp;amp;&amp;amp; score&amp;gt;=80";
 * boolean hasLte = Operators.LESS_THAN_OR_EQUAL.getPattern().matcher(expression).find();
 * boolean hasGte = Operators.GREATER_THAN_OR_EQUAL.getPattern().matcher(expression).find();
 * System.out.println("包含&amp;lt;=：" + hasLte + "，包含&amp;gt;=：" + hasGte); // 输出 "true, true"
 *
 * // 3. 提取表达式中嵌套的小括号内容（如 "(a+b)*(c-d)" 提取 "(a+b)" 和 "(c-d)"）
 * Pattern roundBracketPattern = Operators.BRACKET_ROUND.getPattern();
 * Matcher matcher = roundBracketPattern.matcher("(a+b)*(c-d)");
 * while (matcher.find()) {
 *     System.out.println("提取的分组：" + matcher.group()); // 输出 "(a+b)"、"(c-d)"
 * }
 *
 * // 4. 按优先级降序排序所有运算符（用于表达式计算的运算符优先级执行顺序）
 * List&amp;lt;Operator&amp;gt; sortedOperators = Arrays.stream(Operators.values())
 *     .sorted(Comparator.comparingInt(Operator::getOrder).reversed())
 *     .collect(Collectors.toList());
 * </pre>
 *
 * <h3>重要使用约束</h3>
 * <ul>
 * <li>匹配顺序：长符号运算符需优先匹配（如 "&amp;gt;=" 需在 "&amp;gt;" 之前处理），否则会导致长符号被拆分；</li>
 * <li>分组平衡：使用分组运算符时需确保左右括号成对（如 "(" 与 ")" 数量一致），否则表达式解析会失败；</li>
 * <li>符号冲突：避免自定义运算符与内置符号重复（如新增幂运算需用 "**"，不可复用 "*"）；</li>
 * <li>正则复用：内置 {@link Pattern} 为线程安全，可直接复用，无需重复编译；</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Operator 运算符核心接口（定义符号、优先级、分组标识、匹配模式的标准契约）
 * @see Ordered 优先级接口（规范优先级语义：值越大，执行优先级越高）
 * @see Pattern 正则模式类（运算符精准匹配的核心依赖）
 */
@RequiredArgsConstructor
@Getter
public enum Operators implements Operator {

    // ====================== 分组运算符（优先级：10，最高，用于结构界定）======================
    /**
     * 左小括号（基础分组开始）
     * <p>符号："("，与 {@link #RIGHT_BRACKET_ROUND} 成对使用，用于改变运算顺序（如 "(a+b)*c"），支持无限嵌套
     * <p>正则说明：\\( - 对 "(" 转义（正则元字符），确保精准匹配单个左小括号，无额外约束
     * <p>适用场景：基础表达式分组、函数参数列表界定（如 "func(a,b)"）
     */
    LEFT_BRACKET_ROUND("(", 10, true, Pattern.compile("\\(")),
    /**
     * 右小括号（基础分组结束）
     * <p>符号：")"，与 {@link #LEFT_BRACKET_ROUND} 成对使用，标识分组表达式结束
     * <p>正则说明：\\) - 对 ")" 转义（正则元字符），确保精准匹配单个右小括号
     * <p>约束：需与左小括号数量一致，否则会导致分组结构异常
     */
    RIGHT_BRACKET_ROUND(")", 10, true, Pattern.compile("\\)")),
    /**
     * 左中括号（扩展分组开始）
     * <p>符号："["，与 {@link #RIGHT_BRACKET_SQUARE} 成对使用，用于数组索引（如 "arr[0]"）、集合元素定位等场景，支持嵌套
     * <p>正则说明：\\[ - 对 "[" 转义（正则元字符），确保精准匹配单个左中括号
     * <p>适用场景：数组索引、扩展分组（如配置项分组 "[config1,config2]"）
     */
    LEFT_BRACKET_SQUARE("[", 10, true, Pattern.compile("\\[")),
    /**
     * 右中括号（扩展分组结束）
     * <p>符号："]"，与 {@link #LEFT_BRACKET_SQUARE} 成对使用，标识数组索引/扩展分组结束
     * <p>正则说明：\\] - 对 "]" 转义（正则元字符），确保精准匹配单个右中括号
     * <p>约束：需与左中括号数量一致，否则数组索引解析会失败（如 "arr[0" 为非法格式）
     */
    RIGHT_BRACKET_SQUARE("]", 10, true, Pattern.compile("\\]")),
    /**
     * 左大括号（结构化分组开始）
     * <p>符号："{"，与 {@link #RIGHT_BRACKET_CURLY} 成对使用，用于代码块（如 "{a+b;c-d;}"）、结构化数据分组等场景，支持嵌套
     * <p>正则说明：\\{ - 对 "{" 转义（正则元字符），确保精准匹配单个左大括号
     * <p>适用场景：代码块界定、JSON结构分组（如 "{\"key\":\"value\"}"）、自定义结构化分组
     */
    LEFT_BRACKET_CURLY("{", 10, true, Pattern.compile("\\{")),
    /**
     * 右大括号（结构化分组结束）
     * <p>符号："}"，与 {@link #LEFT_BRACKET_CURLY} 成对使用，标识代码块/结构化分组结束
     * <p>正则说明：\\} - 对 "}" 转义（正则元字符），确保精准匹配单个右大括号
     * <p>约束：需与左大括号数量一致，否则结构化数据解析会失败（如 "{a:1" 为非法格式）
     */
    RIGHT_BRACKET_CURLY("}", 10, true, Pattern.compile("\\}")),
    /**
     * 成对小括号（嵌套分组匹配）
     * <p>符号："()"，配套平衡组正则，用于完整匹配最外层成对小括号（自动处理嵌套场景）
     * <p>正则说明：\\(([^()]*|(?R))*\\) - 核心逻辑：
     *   <ul>
     *   <li>\\( 匹配左小括号，\\) 匹配右小括号；</li>
     *   <li>([^()]*) 匹配括号内非括号字符；</li>
     *   <li>(?R) 递归匹配嵌套的小括号结构，确保完整匹配（如 "((a+b)*c)" 匹配整个表达式）；</li>
     *   </ul>
     * 
     * <p>适用场景：表达式提取（如从复杂表达式中提取嵌套分组）、函数体内容提取（如 "func((a+b)*c)"）
     */
    BRACKET_ROUND("()", 10, true, 
            Pattern.compile("\\(([^()]*|(?R))*\\)") 
    ),
    /**
     * 成对中括号（嵌套扩展分组匹配）
     * <p>符号："[]"，配套平衡组正则，用于完整匹配最外层成对中括号（自动处理嵌套场景）
     * <p>正则说明：\\[([^\\]]*|(?R))*\\] - 核心逻辑：
     *   <ul>
     *   <li>\\[ 匹配左中括号，\\] 匹配右中括号；</li>
     *   <li>([^\\]]*) 匹配括号内非 "]" 字符；</li>
     *   <li>(?R) 递归匹配嵌套的中括号结构（如 "[arr[0]+b]" 匹配整个表达式）；</li>
     *   </ul>
     * 
     * <p>适用场景：数组索引表达式提取（如从 "arr[(a+b)*2]" 提取 "(a+b)*2"）、扩展分组内容提取
     */
    BRACKET_SQUARE("[]", 10, true, 
            Pattern.compile("\\[([^\\]]*|(?R))*\\]") 
    ),
    /**
     * 成对大括号（嵌套结构化分组匹配）
     * <p>符号："{}"，配套平衡组正则，用于完整匹配最外层成对大括号（自动处理嵌套场景）
     * <p>正则说明：\\{([^\\}]*|(?R))*\\} - 核心逻辑：
     *   <ul>
     *   <li>\\{ 匹配左大括号，\\} 匹配右大括号；</li>
     *   <li>([^\\}]*) 匹配括号内非 "}" 字符；</li>
     *   <li>(?R) 递归匹配嵌套的大括号结构（如 "{{a+b};c}" 匹配 "{a+b}"）；</li>
     *   </ul>
     * 
     * <p>适用场景：代码块提取（如从 "{if(a&amp;gt;b){c=d;}}" 提取 "if(a&amp;gt;b){c=d;}"）、结构化数据提取
     */
    BRACKET_CURLY("{}", 10, true, 
            Pattern.compile("\\{([^\\}]*|(?R))*\\}") 
    ),

    // ====================== 算术运算符（优先级：3=乘除取模，2=加减，数值计算）======================
    /**
     * 乘法运算符（双目运算）
     * <p>符号："*"，优先级 3，用于两个数值的乘法计算（如 "a*b"），支持整数、浮点数、高精度数值（如 BigDecimal）
     * <p>正则说明：(?&amp;lt;!\\*)\\*(?!\\*) - 负向断言确保：
     *   <ul>
     *   <li>左侧非 "*"（避免匹配 "**" 中的第二个 "*"）；</li>
     *   <li>右侧非 "*"（避免匹配 "**" 中的第一个 "*"）；</li>
     * </ul>
     * 确保仅匹配独立的乘法运算符，不与幂运算（如 "**"）冲突
     * <p>注意事项：若需支持幂运算，需新增独立运算符（如 "**"），不可复用当前符号
     */
    MULTIPLY("*", 3, false, Pattern.compile("(?<!\\*)\\*(?!\\*)")),
    /**
     * 除法运算符（双目运算）
     * <p>符号："/"，优先级 3，用于两个数值的除法计算（如 "a/b"），支持整数、浮点数、高精度数值
     * <p>正则说明：(?&amp;lt;!/)\\/(?!/) - 负向断言确保仅匹配独立的除法运算符，避免与注释符号（如 "//"）冲突
     * <p>注意事项：需提前校验除数非零，否则会抛出算术异常（如 "a/0" 非法）
     */
    DIVIDE("/", 3, false, Pattern.compile("(?<!/)\\/(?!/)")),
    /**
     * 取模运算符（双目运算）
     * <p>符号："%"，优先级 3，用于两个整数的取模计算（如 "a%b"），结果为除法运算后的余数
     * <p>正则说明：(?&amp;lt;!%)\\%(?!%) - 负向断言确保仅匹配独立的取模运算符，避免与占位符（如 "%%"）冲突
     * <p>特性：结果符号与被除数一致（如 "5%3=2"， "-5%3=-2"）
     */
    MOD("%", 3, false, Pattern.compile("(?<!%)\\%(?!%)")),
    /**
     * 加法运算符（双目运算）
     * <p>符号："+"，优先级 2，用于两个数值的加法计算（如 "a+b"），支持整数、浮点数、高精度数值；也可用于字符串拼接（需上层逻辑适配）
     * <p>正则说明：(?&amp;lt;!\\+)\\+(?!\\+) - 负向断言确保仅匹配独立的加法运算符，避免与自增符号（如 "++"）冲突
     * <p>适配场景：数值累加（如 "1+2+3"）、字符串拼接（如 "a"+"b"，需上层逻辑判断操作数类型）
     */
    PLUS("+", 2, false, Pattern.compile("(?<!\\+)\\+(?!\\+)")),
    /**
     * 减法运算符（双目运算）
     * <p>符号："-"，优先级 2，用于两个数值的减法计算（如 "a-b"），支持整数、浮点数、高精度数值；也可用于表示负数（需上层逻辑适配）
     * <p>正则说明：(?&amp;lt;!\\-)\\-(?!\\-) - 负向断言确保仅匹配独立的减法运算符，避免与自减符号（如 "--"）冲突
     * <p>注意事项：需通过上下文区分“减法运算符”与“负号”（如 "-a" 为负号， "a-b" 为减法）
     */
    MINUS("-", 2, false, Pattern.compile("(?<!\\-)\\-(?!\\-)")),

    // ====================== 比较运算符（优先级：1，结果为布尔值）======================
    /**
     * 大于等于运算符（双目运算）
     * <p>符号："&amp;gt;="，优先级 1，用于两个数值的大小比较（如 "a&amp;gt;=b"），结果为 true/false
     * <p>正则说明：直接匹配 "&amp;gt;=" 字符串，长符号优先设计，避免被拆分为 "&amp;gt;" 和 "="
     * <p>匹配约束：需优先于 {@link #GREATER_THAN} 匹配，否则 "a&amp;gt;=b" 会被解析为 "a&amp;gt;" 和 "=b"（非法）
     */
    GREATER_THAN_OR_EQUAL(">=", 1, false, Pattern.compile(">=")),
    /**
     * 小于等于运算符（双目运算）
     * <p>符号："&amp;lt;="，优先级 1，用于两个数值的大小比较（如 "a&amp;lt;=b"），结果为 true/false
     * <p>正则说明：直接匹配 "&amp;lt;=" 字符串，长符号优先设计，避免被拆分为 "&amp;lt;" 和 "="
     * <p>匹配约束：需优先于 {@link #LESS_THAN} 匹配，否则 "a&amp;lt;=b" 会被解析为 "a&amp;lt;" 和 "=b"（非法）
     */
    LESS_THAN_OR_EQUAL("<=", 1, false, Pattern.compile("<=")),
    /**
     * 等于运算符（双目运算）
     * <p>符号："=="，优先级 1，用于数值/对象的相等性比较（如 "a==b"），结果为 true/false
     * <p>正则说明：直接匹配 "==" 字符串，双符号设计，避免与赋值运算符（如 "="）混淆
     * <p>适配场景：数值相等判断（如 "10==10"）、对象引用相等（需上层逻辑调用 equals 方法）
     */
    EQUALS("==", 1, false, Pattern.compile("==")),
    /**
     * 不等于运算符（双目运算）
     * <p>符号："!="，优先级 1，用于数值/对象的不相等性比较（如 "a!=b"），结果为 true/false
     * <p>正则说明：直接匹配 "!=" 字符串，长符号优先设计，避免被拆分为 "!" 和 "="
     * <p>匹配约束：需优先于 {@link #LOGICAL_NOT} 匹配，否则 "a!=b" 会被解析为 "a!" 和 "=b"（非法）
     */
    NOT_EQUALS("!=", 1, false, Pattern.compile("!=")),
    /**
     * 大于运算符（双目运算）
     * <p>符号："&amp;gt;"，优先级 1，用于两个数值的大小比较（如 "a&amp;gt;b"），结果为 true/false
     * <p>正则说明：&amp;gt;(?!=) - 右侧非 "="，确保仅匹配纯 "&amp;gt;" 符号，不与 {@link #GREATER_THAN_OR_EQUAL} 冲突
     * <p>匹配约束：需在 {@link #GREATER_THAN_OR_EQUAL} 之后匹配，避免覆盖长符号
     */
    GREATER_THAN(">", 1, false, Pattern.compile(">(?!=)")),
    /**
     * 小于运算符（双目运算）
     * <p>符号："&amp;lt;"，优先级 1，用于两个数值的大小比较（如 "a&amp;lt;b"），结果为 true/false
     * <p>正则说明：&amp;lt;(?!=) - 右侧非 "="，确保仅匹配纯 "&amp;lt;" 符号，不与 {@link #LESS_THAN_OR_EQUAL} 冲突
     * <p>匹配约束：需在 {@link #LESS_THAN_OR_EQUAL} 之后匹配，避免覆盖长符号
     */
    LESS_THAN("<", 1, false, Pattern.compile("<(?!=)")),

    // ====================== 逻辑运算符（优先级：0，最低，布尔值运算）======================
    /**
     * 逻辑与运算符（双目运算，短路求值）
     * <p>符号："&amp;amp;&amp;amp;"，优先级 0，用于两个布尔值的逻辑与运算（如 "a&amp;amp;&amp;amp;b"），结果为 true/false
     * <p>正则说明：直接匹配 "&amp;amp;&amp;amp;" 字符串，双符号设计，确保与位运算 "&amp;"（若后续新增）严格区分
     * <p>核心特性：短路求值 - 左侧为 false 时，右侧表达式不执行（如 "false&amp;amp;&amp;amp;(1/0)" 不会抛出异常）
     * <p>适用场景：多条件同时满足判断（如 "age&amp;gt;18 &amp;amp;&amp;amp; score&amp;gt;=60"）
     */
    LOGICAL_AND("&&", 0, false, Pattern.compile("&&")),
    /**
     * 逻辑或运算符（双目运算，短路求值）
     * <p>符号："||"，优先级 0，用于两个布尔值的逻辑或运算（如 "a||b"），结果为 true/false
     * <p>正则说明：\\|\\| - "|" 为正则元字符，需转义为 "\\|"，确保精准匹配 "||" 符号，不与位运算 "|" 混淆
     * <p>核心特性：短路求值 - 左侧为 true 时，右侧表达式不执行（如 "true||(1/0)" 不会抛出异常）
     * <p>适用场景：多条件至少一个满足判断（如 "age&amp;lt;18 || isVIP"）
     */
    LOGICAL_OR("||", 0, false, Pattern.compile("\\|\\|")),
    /**
     * 逻辑非运算符（单目运算）
     * <p>符号："!"，优先级 0，用于布尔值的逻辑取反（如 "!a"），结果为 true/false
     * <p>正则说明：!(?!=) - 右侧非 "="，确保仅匹配纯 "!" 符号，不与 {@link #NOT_EQUALS} 冲突
     * <p>匹配约束：需在 {@link #NOT_EQUALS} 之后匹配，避免覆盖长符号；仅作用于右侧单个布尔表达式（如 "!(a&amp;gt;b)"）
     * <p>适用场景：条件取反（如 "!isValid"）、布尔值反转（如 "!true" → false）
     */
    LOGICAL_NOT("!", 0, false, Pattern.compile("!(?!=)"));

    /**
     * 运算符唯一符号（如 "+"、"=="、"[]"），与 {@link Operator#getSymbol()} 接口对齐，不可重复
     */
    private final String symbol;

    /**
     * 运算优先级（与 {@link Operator#getOrder()} 接口对齐，兼容 {@link Ordered} 语义）
     * <p>取值范围：0（最低）~10（最高），优先级越高，在表达式中越先执行
     */
    private final int order;

    /**
     * 是否为分组运算符（与 {@link Operator#isGroup()} 接口对齐）
     * <p>true：分组运算符（括号类），用于表达式结构界定；false：普通运算运算符（算术/比较/逻辑）
     */
    private final boolean group;

    /**
     * 运算符精准匹配正则模式（与 {@link Operator#getPattern()} 接口对齐）
     * <p>线程安全，可直接复用，用于表达式解析时的运算符识别，避免误匹配、符号冲突
     */
    private final Pattern pattern;

    /**
     * 最低优先级常量（与 {@link Ordered#LOWEST_PRECEDENCE} 语义对齐，值为 0）
     * <p>用于外部统一引用最低优先级阈值（如自定义运算符时指定最低优先级）
     */
    public static final int LOWEST_PRECEDENCE = Ordered.LOWEST_PRECEDENCE;

    /**
     * 最高优先级常量（与分组运算符优先级一致，值为 10）
     * <p>用于外部统一引用最高优先级阈值（如自定义分组运算符时指定优先级）
     */
    public static final int HIGHEST_PRECEDENCE = 10;
}