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

package org.dromara.hutool.http.client.cookie;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * 线程隔离的Cookie存储。多线程环境下Cookie隔离使用，防止Cookie覆盖<br>
 *
 * 见：https://stackoverflow.com/questions/16305486/cookiemanager-for-multiple-threads
 *
 * @author looly
 * @since 4.1.18
 */
public class ThreadLocalCookieStore implements CookieStore {

	private final static ThreadLocal<CookieStore> STORES = new ThreadLocal<CookieStore>() {
		@Override
		protected synchronized CookieStore initialValue() {
			/* InMemoryCookieStore */
			return (new CookieManager()).getCookieStore();
		}
	};

	/**
	 * 获取本线程下的CookieStore
	 *
	 * @return CookieStore
	 */
	public CookieStore getCookieStore() {
		return STORES.get();
	}

	/**
	 * 移除当前线程的Cookie
	 *
	 * @return this
	 */
	public ThreadLocalCookieStore removeCurrent() {
		STORES.remove();
		return this;
	}

	@Override
	public void add(final URI uri, final HttpCookie cookie) {
		getCookieStore().add(uri, cookie);
	}

	@Override
	public List<HttpCookie> get(final URI uri) {
		return getCookieStore().get(uri);
	}

	@Override
	public List<HttpCookie> getCookies() {
		return getCookieStore().getCookies();
	}

	@Override
	public List<URI> getURIs() {
		return getCookieStore().getURIs();
	}

	@Override
	public boolean remove(final URI uri, final HttpCookie cookie) {
		return getCookieStore().remove(uri, cookie);
	}

	@Override
	public boolean removeAll() {
		return getCookieStore().removeAll();
	}
}
