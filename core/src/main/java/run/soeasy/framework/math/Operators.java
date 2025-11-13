package run.soeasy.framework.math;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.comparator.Ordered;

/**
 * 内置常见运算符枚举实现
 * <p>涵盖分组（小/中/大括号）、算术、比较、逻辑四大类核心运算符，优先级遵循行业标准：
 * 分组运算符 > 算术运算符（乘除取模 > 加减） > 比较运算符 > 逻辑运算符
 * <p>正则模式（pattern）设计原则：
 * 1. 避免部分匹配（如 "+" 不匹配 "++"、">=" 不被拆分为 ">" 和 "="）；
 * 2. 转义正则特殊字符（如 "*"、"["、"]" 等）；
 * 3. 适配表达式无空格场景（如 "a+b"、"x>=y"、"[a+b]"）。
 *
 * @author soeasy.run
 */
@RequiredArgsConstructor
@Getter
public enum Operators implements Operator {

    // ====================== 分组运算符（优先级最高：10）======================
    /** 左小括号（分组开始）- 与 RIGHT_BRACKET_ROUND 成对使用 */
    LEFT_BRACKET_ROUND("(", 10, true, Pattern.compile("\\(")),
    /** 右小括号（分组结束）- 与 LEFT_BRACKET_ROUND 成对使用 */
    RIGHT_BRACKET_ROUND(")", 10, true, Pattern.compile("\\)")),
    /** 左中括号（分组开始）- 与 RIGHT_BRACKET_SQUARE 成对使用（支持数组索引/分组） */
    LEFT_BRACKET_SQUARE("[", 10, true, Pattern.compile("\\[")),
    /** 右中括号（分组结束）- 与 LEFT_BRACKET_SQUARE 成对使用 */
    RIGHT_BRACKET_SQUARE("]", 10, true, Pattern.compile("\\]")),
    /** 左大括号（分组开始）- 与 RIGHT_BRACKET_CURLY 成对使用（支持代码块/自定义分组） */
    LEFT_BRACKET_CURLY("{", 10, true, Pattern.compile("\\{")),
    /** 右大括号（分组结束）- 与 LEFT_BRACKET_CURLY 成对使用 */
    RIGHT_BRACKET_CURLY("}", 10, true, Pattern.compile("\\}")),
    /** 小括号（成对分组运算符）- 符号 "()"，支持嵌套匹配，用于基础分组运算 */
    BRACKET_ROUND("()", 10, true, 
            Pattern.compile("\\(([^()]*|(?R))*\\)") // 平衡组正则：匹配最外层成对小括号（支持嵌套）
    ),
    /** 中括号（成对分组运算符）- 符号 "[]"，支持嵌套匹配，用于数组索引/扩展分组 */
    BRACKET_SQUARE("[]", 10, true, 
            Pattern.compile("\\[(\\[^\\]]*|(?R))*\\]") // 平衡组正则：匹配最外层成对中括号（支持嵌套）
    ),
    /** 大括号（成对分组运算符）- 符号 "{}"，支持嵌套匹配，用于代码块/自定义分组 */
    BRACKET_CURLY("{}", 10, true, 
            Pattern.compile("\\{(\\[^\\}]*|(?R))*\\}") // 平衡组正则：匹配最外层成对大括号（支持嵌套）
    ),

    // ====================== 算术运算符（优先级：3=乘除取模，2=加减）======================
    /** 乘法运算符（*） */
    MULTIPLY("*", 3, false, Pattern.compile("(?<!\\*)\\*(?!\\*)")), // 匹配独立的 "*"，排除 "**"
    /** 除法运算符（/） */
    DIVIDE("/", 3, false, Pattern.compile("(?<!/)\\/(?!/)")), // 匹配独立的 "/"，排除 "//"
    /** 取模运算符（%） */
    MOD("%", 3, false, Pattern.compile("(?<!%)\\%(?!%)")), // 匹配独立的 "%"，排除 "%%"
    /** 加法运算符（+） */
    PLUS("+", 2, false, Pattern.compile("(?<!\\+)\\+(?!\\+)")), // 匹配独立的 "+"，排除 "++"
    /** 减法运算符（-） */
    MINUS("-", 2, false, Pattern.compile("(?<!\\-)\\-(?!\\-)")), // 匹配独立的 "-"，排除 "--"

    // ====================== 比较运算符（优先级：1）======================
    /** 大于等于（>=）- 先匹配长符号，避免被 ">" 误匹配 */
    GREATER_THAN_OR_EQUAL(">=", 1, false, Pattern.compile(">=")),
    /** 小于等于（<=）- 先匹配长符号，避免被 "<" 误匹配 */
    LESS_THAN_OR_EQUAL("<=", 1, false, Pattern.compile("<=")),
    /** 等于（==）- 匹配双等号，避免被 "=" 误匹配（若后续加单等号赋值运算符） */
    EQUALS("==", 1, false, Pattern.compile("==")),
    /** 不等于（!=）- 匹配不等号，避免被 "!" 误匹配 */
    NOT_EQUALS("!=", 1, false, Pattern.compile("!=")),
    /** 大于（>）- 排除后续跟 "=" 的情况（避免匹配 ">="） */
    GREATER_THAN(">", 1, false, Pattern.compile(">(?!=)")),
    /** 小于（<）- 排除后续跟 "=" 的情况（避免匹配 "<="） */
    LESS_THAN("<", 1, false, Pattern.compile("<(?!=)")),

    // ====================== 逻辑运算符（优先级：0，最低）======================
    /** 逻辑与（&&） */
    LOGICAL_AND("&&", 0, false, Pattern.compile("&&")),
    /** 逻辑或（||） */
    LOGICAL_OR("||", 0, false, Pattern.compile("\\|\\|")), // "|" 是正则特殊字符，需转义
    /** 逻辑非（!）- 排除后续跟 "=" 的情况（避免匹配 "!="） */
    LOGICAL_NOT("!", 0, false, Pattern.compile("!(?!=)"));

    /** 运算符符号（与接口 {@link Operator#getSymbol()} 对应） */
    private final String symbol;
    /** 运算优先级（与接口 {@link Operator#getOrder()} 对应，值越小优先级越高） */
    private final int order;
    /** 是否为分组运算符（与接口 {@link Operator#isGroup()} 对应） */
    private final boolean group;
    /** 匹配正则模式（与接口 {@link Operator#getPattern()} 对应） */
    private final Pattern pattern;

    // 框架优先级常量（与 Ordered 接口对齐，方便外部使用）
    public static final int LOWEST_PRECEDENCE = Ordered.LOWEST_PRECEDENCE;
    public static final int HIGHEST_PRECEDENCE = 10;
}
