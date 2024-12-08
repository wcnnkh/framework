package io.basc.framework.data.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.util.io.IOUtils;

public class BinaryTreeNode<T extends Comparable<T>> {
	private final T value;
	private BinaryTreeNode<T> left;
	private BinaryTreeNode<T> right;

	public BinaryTreeNode(T value) {
		this.value = value;
	}

	public BinaryTreeNode<T> getLeft() {
		return left;
	}

	public void setLeft(BinaryTreeNode<T> left) {
		this.left = left;
	}

	public BinaryTreeNode<T> getRight() {
		return right;
	}

	public void setRight(BinaryTreeNode<T> right) {
		this.right = right;
	}

	public T getValue() {
		return value;
	}

	// 以下为toString实现

	private static final String BASE_EMPTY_LINK = "\t";

	private static final String BASE_LINE_LINK = "____";

	private static final String V_LINE = "|";

	private Map<BinaryTreeNode<T>, IndexAndLen> nodesCoo = new HashMap<>();

	class IndexAndLen {
		private int index;// index坐标
		private int length;// 数字长度

		public IndexAndLen(int index, int length) {
			this.index = index;
			this.length = length;
		}
	}

	void calculateCoordinate() {
		List<BinaryTreeNode<T>> nodes = new ArrayList<>();
		midOrder(this, nodes);
		if (nodes.isEmpty())
			return;
		int offset = 0;
		for (int i = 0; i < nodes.size(); i++) {
			BinaryTreeNode<T> node = nodes.get(i);
			int len = numberLength(node);
			nodesCoo.put(node, new IndexAndLen(i + offset, len));
			offset += len;
		}
	}

	void midOrder(BinaryTreeNode<T> cur, List<BinaryTreeNode<T>> list) {
		if (cur == null)
			return;
		midOrder(cur.left, list);
		list.add(cur);
		midOrder(cur.right, list);
	}

	int numberLength(BinaryTreeNode<T> node) {
		if (node == null)
			return 0;
		else
			return node.value.toString().length() / 4;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		print(sb);
		return sb.toString();
	}

	protected void print(StringBuilder out) {
		calculateCoordinate();
		List<BinaryTreeNode<T>> nodes = new ArrayList<>();
		nodes.add(this);
		printRow(nodes, out);
	}

	class PrintRowNodes {
		private static final String INIT = "";
		/**
		 * 数据头顶上的竖线行
		 */
		StringBuilder vLineInDataHead = new StringBuilder(INIT);
		/**
		 * 数据行
		 */
		StringBuilder dataLine = new StringBuilder(INIT);
		/**
		 * 连接子节点的线段行
		 */
		StringBuilder linkSonLine = new StringBuilder(INIT);
		/**
		 *
		 * 前一个节点
		 */
		BinaryTreeNode<T> prev;
		/**
		 *
		 * 连接子节点线段之间计算相邻线段距离时的上一个线段的右节点；
		 */
		BinaryTreeNode<T> lastRight;

		@Override
		public String toString() {
			return vLineInDataHead.toString() + "\n" + dataLine.toString() + "\n" + linkSonLine.toString();
		}
	}

	void printRow(List<BinaryTreeNode<T>> nodes, StringBuilder out) {
		if (nodes.size() == 0)
			return;
		List<BinaryTreeNode<T>> children = new ArrayList<>();
		PrintRowNodes printNode = new PrintRowNodes();
		for (BinaryTreeNode<T> node : nodes) {
			printNode(node, printNode, children);
		}

		out.append(printNode).append(IOUtils.LINE_SEPARATOR);
		printRow(children, out);
	}

	void printNode(BinaryTreeNode<T> node, PrintRowNodes printNode, List<BinaryTreeNode<T>> children) {
		String VLineEmpty = nodeEmpty(nodeDistance(node, printNode.prev, 0));
		String numberEmpty = nodeEmpty(nodeDistance(node, printNode.prev, prevLength(printNode.prev)));
		printNode.vLineInDataHead.append(VLineEmpty + V_LINE);
		printNode.dataLine.append(numberEmpty + value(node));
		if (node.left == null && node.right == null) {
			printNode.prev = node;
			return;
		}
		String linkSon = linkSonLine(node, children);
		String sonLineEmpty = nodeEmpty(nodeDistance(leftNode(node), printNode.lastRight, 0));
		printNode.linkSonLine.append(sonLineEmpty + linkSon);
		printNode.prev = node;
		printNode.lastRight = lastRight(node);
	}

	String linkSonLine(BinaryTreeNode<T> cur, List<BinaryTreeNode<T>> children) {
		BinaryTreeNode<T> left = cur.left;
		BinaryTreeNode<T> right = cur.right;
		String leftLinkSonLine = "";
		String rightLinkSonLine = "";
		if (left != null) {
			children.add(left);
			leftLinkSonLine = linkSonNodeLine(nodeDistance(cur, left, 0));
		}
		if (right != null) {
			children.add(right);
			rightLinkSonLine = linkSonNodeLine(nodeDistance(right, cur, 0));
		}
		return leftLinkSonLine + V_LINE + rightLinkSonLine;
	}

	int prevLength(BinaryTreeNode<T> node) {
		return node == null ? 0 : nodesCoo.get(node).length;
	}

	BinaryTreeNode<T> lastRight(BinaryTreeNode<T> prev) {
		if (prev == null)
			return null;
		else
			return prev.right == null ? prev : prev.right;
	}

	BinaryTreeNode<T> leftNode(BinaryTreeNode<T> node) {
		return node.left != null ? node.left : node;
	}

	int nodeIndex(BinaryTreeNode<T> node) {
		return node == null ? 0 : nodesCoo.get(node).index;
	}

	int nodeDistance(BinaryTreeNode<T> cur, BinaryTreeNode<T> prev, int prevNumLen) {
		return nodeIndex(cur) - nodeIndex(prev) - prevNumLen;
	}

	String linkLine(int dis, String linkStr) {
		String str = "";
		for (int i = 0; i < dis; i++) {
			str += linkStr;
		}
		return str;
	}

	String nodeEmpty(int dis) {
		return linkLine(dis, BASE_EMPTY_LINK);
	}

	String linkSonNodeLine(int dis) {
		return linkLine(dis, BASE_LINE_LINK);
	}

	String value(BinaryTreeNode<T> node) {
		// if (node.red)
		// return "\033[91;1m" + node.value.toString() + "\033[0m";// 打印红色
		// else
		return node.value.toString();// 打印黑色
	}
}
