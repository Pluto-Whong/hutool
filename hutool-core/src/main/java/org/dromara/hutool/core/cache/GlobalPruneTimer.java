/*
 * Copyright (c) 2013-2024 Hutool Team and hutool.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.core.cache;

import org.dromara.hutool.core.thread.ThreadUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局缓存清理定时器池，用于在需要过期支持的缓存对象中超时任务池
 *
 * @author looly
 */
public enum GlobalPruneTimer {
	/**
	 * 单例对象
	 */
	INSTANCE;

	/**
	 * 缓存任务计数
	 */
	private final AtomicInteger cacheTaskNumber = new AtomicInteger(1);

	/**
	 * 定时器
	 */
	private ScheduledExecutorService pruneTimer;

	/**
	 * 构造
	 */
	GlobalPruneTimer() {
		init();
	}

	/**
	 * 启动定时任务
	 *
	 * @param task  任务
	 * @param delay 周期
	 * @return {@link ScheduledFuture}对象，可手动取消此任务
	 */
	public ScheduledFuture<?> schedule(final Runnable task, final long delay) {
		return this.pruneTimer.scheduleAtFixedRate(task, delay, delay, TimeUnit.MILLISECONDS);
	}

	/**
	 * 初始化定时器
	 */
	public void init() {
		if (null != pruneTimer) {
			shutdownNow();
		}
		this.pruneTimer = new ScheduledThreadPoolExecutor(1, r -> ThreadUtil.newThread(r, StrUtil.format("Pure-Timer-{}", cacheTaskNumber.getAndIncrement())));
	}

	/**
	 * 销毁全局定时器
	 */
	public void shutdown() {
		if (null != pruneTimer) {
			pruneTimer.shutdown();
		}
	}

	/**
	 * 销毁全局定时器
	 *
	 * @return 销毁时未被执行的任务列表
	 */
	public List<Runnable> shutdownNow() {
		if (null != pruneTimer) {
			return pruneTimer.shutdownNow();
		}
		return null;
	}
}
