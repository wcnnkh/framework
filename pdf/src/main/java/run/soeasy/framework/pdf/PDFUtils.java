package run.soeasy.framework.pdf;

import java.io.IOException;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.domain.IntValue;

/**
 * PDF解析与合并工具类，基于Apache PDFBox开源组件实现，整合两大核心能力：
 * <ul>
 * <li>PDF解析：支持全页遍历解析、指定页码精准解析，提供解析全生命周期回调监听</li>
 * <li>多文档合并：将多个已加载的PDDocument实例合并到目标文档，按顺序拼接页面</li>
 * </ul>
 * 
 * <p>
 * 核心设计亮点：
 * <ul>
 * <li>工具类设计：不可实例化，所有方法均为静态，直接通过类名调用，降低使用成本</li>
 * <li>资源安全：明确文档生命周期约定，由调用方手动管理PDDocument的加载与关闭，适配自定义持久化场景</li>
 * <li>回调驱动：解析功能基于PDFReadListener回调模式，支持初始化、逐页处理、异常、完成全生命周期事件，灵活扩展业务逻辑</li>
 * <li>容错性强：解析时支持异常捕获与流程控制（非IO异常不中断整体流程），提升稳定性</li>
 * <li>计数适配：合并计数采用IntValue可变容器，绕开Lambda表达式外部变量不可修改的语法限制，兼顾简洁性与兼容性</li>
 * </ul>
 * 
 * <p>
 * 依赖说明：
 * <ul>
 * <li>核心依赖：Apache PDFBox（负责PDF解析与合并底层实现）</li>
 * <li>辅助依赖：lombok（简化非空校验与工具类注解）、soeasy-framework（可变容器IntValue）</li>
 * </ul>
 */
@UtilityClass
public class PDFUtils {

	/**
	 * 将多个已加载的PDDocument实例（源文档）合并到目标PDDocument实例，适用于自定义文档持久化场景。
	 * 
	 * 合并逻辑说明：
	 * <ol>
	 * <li>校验输入参数有效性（目标文档、源文档流均不可为null）</li>
	 * <li>遍历所有源文档，获取每个文档的页面树（PDPageTree）</li>
	 * <li>通过「页面引用机制」将源文档页面添加到目标文档，不复制页面数据，提升合并效率</li>
	 * <li>使用IntValue可变容器统计合并页面总数，适配Lambda语法限制</li>
	 * </ol>
	 * 
	 * 核心特性：
	 * <ul>
	 * <li>合并顺序：按源文档流的遍历顺序拼接，页面顺序为各源文档内页顺序的自然延续</li>
	 * <li>性能优化：页面引用机制避免数据复制，合并大文件时效率更高，且不修改源文档内容</li>
	 * <li>容错处理：仅跳过空的源文档（无页面），源文档加载异常需由调用方提前处理</li>
	 * </ul>
	 * 
	 * 注意事项（关键风险提示）：
	 * <ul>
	 * <li>文档生命周期：目标文档需由调用方手动保存（调用PDDocument.save方法）和关闭；源文档在合并期间不可关闭，否则会导致「COSStream已关闭」错误</li>
	 * <li>线程安全：不支持多线程合并，PDDocument与PDPage均非线程安全，多线程操作会导致文档损坏或并发异常</li>
	 * <li>页面依赖：因采用引用机制，源文档关闭后，目标文档中引用的页面会失效（无法保存或打开），需确保合并完成后再关闭源文档</li>
	 * <li>参数约束：源文档流（documents）中的元素可为null，遇到null时会自动跳过，不影响后续文档合并</li>
	 * </ul>
	 *
	 * @param output    目标PDDocument实例，不可为null，需处于未关闭状态
	 * @param documents 源PDDocument实例的流，不可为null；流中元素可为null（会自动跳过），每个非null元素需是已加载的未关闭文档
	 * @return 成功合并的总页面数，即所有有效源文档的页面总数之和
	 * @throws IOException 当源文档页面读取失败、目标文档添加页面异常（如文档已关闭）时抛出
	 */
	public static int merge(@NonNull PDDocument output, @NonNull Iterator<? extends PDDocument> documents)
			throws IOException {
		// 可变计数容器：绕开Lambda外部变量不可修改的语法限制
		IntValue count = new IntValue(0);
		while (documents.hasNext()) {
			PDDocument document = documents.next();
			// 跳过null源文档，避免空指针异常
			if (document == null) {
				continue;
			}

			PDPageTree sourcePageTree = document.getPages();
			// 遍历源文档所有页面，通过引用方式添加到目标文档
			for (PDPage page : sourcePageTree) {
				output.addPage(page);
				count.increment(); // 合并成功，计数递增
			}
		}
		return count.intValue();
	}

	/**
	 * 基于已加载的PDDocument实例执行全页解析，通过PDFReadListener回调处理解析全生命周期事件。
	 * 
	 * 详细解析流程：
	 * <ol>
	 * <li>初始化阶段：触发onParseStart回调，若返回false则直接终止解析，不触发后续回调</li>
	 * <li>逐页解析阶段：遍历文档所有页面，触发onPageParsed回调；若回调返回false则终止后续页面解析</li>
	 * <li>异常处理阶段：页面解析发生异常时，触发onParseError回调；若为IO异常则直接抛出，中断整个解析流程</li>
	 * <li>完成阶段：无论解析成功、主动终止或异常终止，均触发onParseComplete回调，传入总处理页数与成功页数</li>
	 * </ol>
	 * 
	 * 注意事项：
	 * <ul>
	 * <li>文档管理：文档需由调用方手动加载和关闭，本方法仅负责解析逻辑，不处理资源释放</li>
	 * <li>页码索引：回调中传入的页码为0-based索引（第1页对应索引0），与文档内部页码一致</li>
	 * <li>异常控制：非IO异常（如业务逻辑异常）由监听器处理，不中断解析流程；IO异常会直接抛出，中断后续页面处理</li>
	 * </ul>
	 *
	 * @param document     已加载的PDDocument实例，不可为null，需处于未关闭状态
	 * @param readListener PDF解析监听器，不可为null，用于接收解析各阶段回调事件
	 * @throws IOException 当文档读取、页面处理或监听器执行过程中发生IO异常时抛出
	 */
	public static void read(@NonNull PDDocument document, @NonNull PDFReadListener readListener) throws IOException {
		// 触发解析开始回调，若返回false则终止解析
		if (!readListener.onParseStart(document)) {
			return;
		}
		int totalCount = 0; // 总处理页面数（结束时等于文档总页数）
		int successCount = 0; // 成功解析的页面数量
		try {
			PDPageTree pageTree = document.getPages(); // 获取文档页面树
			Iterator<PDPage> iterator = pageTree.iterator();
			while (iterator.hasNext()) {
				PDPage page = iterator.next();
				try {
					// 触发页面解析回调，若返回false则终止后续页面解析
					if (!readListener.onPageParsed(document, page, totalCount)) {
						break;
					}
					successCount++; // 页面解析成功，计数递增
				} catch (Throwable e) {
					// 触发异常回调，若为IO异常则继续抛出
					readListener.onParseError(document, page, totalCount, e);
					if (e instanceof IOException) {
						throw (IOException) e;
					}
				} finally {
					totalCount++; // 无论当前页是否成功，索引均递增（保证与实际页数一致）
				}
			}
		} finally {
			// 触发解析完成回调，传入总处理页数和成功页数
			readListener.onParseComplete(document, totalCount, successCount);
		}
	}

	/**
	 * 基于已加载的PDDocument实例解析指定页码，通过PDFReadListener回调处理解析事件。
	 * 
	 * 详细解析流程：
	 * <ol>
	 * <li>初始化阶段：触发onParseStart回调，若返回false则直接终止解析，不触发后续回调</li>
	 * <li>指定页解析阶段：遍历传入的页码数组，按索引获取页面并触发onPageParsed回调；若回调返回false则终止后续页码解析</li>
	 * <li>异常处理阶段：页面解析发生异常时，触发onParseError回调；非IO异常不抛出，避免中断后续页码解析</li>
	 * <li>完成阶段：无论解析结果如何，均触发onParseComplete回调，传入总处理的指定页码数与成功页数</li>
	 * </ol>
	 * 
	 * 核心特性：
	 * <ul>
	 * <li>页码支持：传入0-based索引（范围0~文档总页数-1），支持多个页码、重复页码（重复页码会重复解析）</li>
	 * <li>容错性：单个页码解析异常（非IO）不影响其他页码，仅触发异常回调，解析流程继续执行</li>
	 * </ul>
	 * 
	 * 注意事项：
	 * <ul>
	 * <li>文档管理：文档需由调用方手动加载和关闭，本方法不处理资源释放</li>
	 * <li>索引有效性：传入的页码超出文档范围时，会抛出IndexOutOfBoundsException</li>
	 * <li>异常控制：IO异常会直接抛出，中断解析流程；非IO异常由监听器处理，不中断后续页码解析</li>
	 * </ul>
	 *
	 * @param document     已加载的PDDocument实例，不可为null，需处于未关闭状态
	 * @param readListener PDF解析监听器，不可为null，用于接收解析各阶段回调事件
	 * @param pageIndexs   待解析的页码数组（0-based索引），支持多个页码传入，不可为null（可传入空数组）
	 * @throws IOException               当文档读取、页面处理或监听器执行过程中发生IO异常时抛出
	 * @throws IndexOutOfBoundsException 当传入的页码超出文档页码范围（无效索引）时抛出
	 */
	public static void read(@NonNull PDDocument document, @NonNull PDFReadListener readListener, int... pageIndexs)
			throws IOException {
		if (!readListener.onParseStart(document)) {
			return;
		}

		int totalCount = 0; // 总处理的指定页码数
		int successCount = 0; // 成功解析的指定页码数
		try {
			PDPageTree pageTree = document.getPages();
			for (int pageIndex : pageIndexs) {
				// 根据索引获取指定页面（无效索引会抛出IndexOutOfBoundsException）
				PDPage page = pageTree.get(pageIndex);
				try {
					// 触发页面解析回调，若返回false则终止后续页码解析
					if (!readListener.onPageParsed(document, page, pageIndex)) {
						break;
					}
					successCount++;
				} catch (Throwable e) {
					// 触发异常回调，非IO异常不转发（避免中断后续页码解析）
					readListener.onParseError(document, page, pageIndex, e);
				} finally {
					totalCount++;
				}
			}
		} finally {
			// 触发解析完成回调，传入总处理的指定页码数和成功页数
			readListener.onParseComplete(document, totalCount, successCount);
		}
	}
}