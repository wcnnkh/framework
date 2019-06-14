package scw.core.utils;

import java.util.concurrent.LinkedBlockingQueue;

import scw.core.BlockingQueue;

public class DefaultBlockingQueue<E> extends LinkedBlockingQueue<E> implements BlockingQueue<E> {
	private static final long serialVersionUID = 1L;
}
