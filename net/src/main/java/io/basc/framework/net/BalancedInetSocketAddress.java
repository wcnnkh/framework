package io.basc.framework.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import io.basc.framework.util.function.Weighted;

public class BalancedInetSocketAddress extends InetSocketAddress implements Weighted{
	private static final long serialVersionUID = 1L;
	private final int weight;

	public BalancedInetSocketAddress(InetAddress addr, int port) {
		this(addr, port, 1);
	}

	public BalancedInetSocketAddress(int port) {
		this(port, 1);
	}

	public BalancedInetSocketAddress(String hostname, int port) {
		this(hostname, port, 1);
	}

	public BalancedInetSocketAddress(InetAddress addr, int port, int weight) {
		super(addr, port);
		this.weight = weight;
	}

	public BalancedInetSocketAddress(int port, int weight) {
		super(port);
		this.weight = weight;
	}

	public BalancedInetSocketAddress(String hostname, int port, int weight) {
		super(hostname, port);
		this.weight = weight;
	}

	public final int getWeight() {
		return weight;
	}
}
