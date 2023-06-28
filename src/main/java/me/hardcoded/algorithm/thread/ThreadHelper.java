package me.hardcoded.algorithm.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadHelper<T> implements AutoCloseable {
	private ExecutorService service;
	private List<Future<T>> futures;
	
	public ThreadHelper(int nThreads) {
		this.service = Executors.newFixedThreadPool(nThreads);
		this.futures = new ArrayList<>();
	}
	
	public void submit(Callable<T> callable) {
		futures.add(service.submit(callable));
	}
	
	/**
	 * Get all results from all submitted callables.
	 * This might block
	 */
	public List<T> getResults() throws ExecutionException {
		// Might be blocking
		List<T> results = new ArrayList<>();
		try {
			for (var future : futures) {
				results.add(future.get());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		return results;
	}
	
	@Override
	public void close() {
		service.shutdown();
	}
}
