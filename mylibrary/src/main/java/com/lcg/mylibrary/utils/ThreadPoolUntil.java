package com.lcg.mylibrary.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 线程管理工具
 */
public class ThreadPoolUntil {
	public final ArrayList<Future<?>> futureList = new ArrayList<>();
	private ThreadPoolExecutor server;

	public ThreadPoolExecutor getExecutor() {
		return server;
	}

	public void setExecutor(ThreadPoolExecutor executor) {
		server = executor;
	}

	private ExecutorService singleThreadPool;

	private ThreadPoolUntil() {
		int cpuNumCores = getCpuNumCores();
		server = new ThreadPoolExecutor(4, 4 + cpuNumCores, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
				4 * cpuNumCores),
				new ThreadPoolExecutor.CallerRunsPolicy());
		singleThreadPool = Executors.newSingleThreadExecutor();
	}

	/**
	 * 获取CPU核心数
	 *
	 * @return
	 */
	private int getCpuNumCores() {
		int cpuNumCores = 1;
		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					if (Pattern.matches("cpu[0-9]", filename)) {
						return true;
					}
					return false;
				}
			});
			cpuNumCores = files.length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cpuNumCores;
	}

	private final static ThreadPoolUntil threadPool = new ThreadPoolUntil();

	public static ThreadPoolUntil getInstance() {
		return threadPool;
	}

	/**
	 * 加入线程池队列，并不一定立刻执行。
	 *
	 * @param runnable
	 * @return 如果加入成功，返回一个Future。
	 */
	public Future<?> run(Runnable runnable) {
		Future<?> submit = server.submit(runnable);
		futureList.add(submit);
		return submit;

	}

	/**
	 * 加入单一线程去排队执行，并不一定立刻执行。
	 *
	 * @param runnable
	 * @return 如果加入成功，返回一个Future。
	 */
	public void runSingle(Runnable runnable) {
		singleThreadPool.submit(runnable);
	}

	public void cancelAll(boolean mayInterruptIfRunning) {
		for (int i = 0; i < futureList.size(); i++) {
			futureList.get(i).cancel(mayInterruptIfRunning);
		}
		futureList.clear();
	}
}