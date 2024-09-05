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

package org.dromara.hutool.http.client;

import org.dromara.hutool.http.HttpGlobalConfig;
import org.dromara.hutool.http.proxy.HttpProxy;
import org.dromara.hutool.http.ssl.SSLInfo;

/**
 * Http客户端配置
 *
 * @author looly
 */
public class ClientConfig {

	/**
	 * 创建新的 ClientConfig
	 *
	 * @return ClientConfig
	 */
	public static ClientConfig of() {
		return new ClientConfig();
	}

	/**
	 * 默认连接超时
	 */
	private int connectionTimeout;
	/**
	 * 默认读取超时
	 */
	private int readTimeout;
	/**
	 * SSL相关配置
	 */
	private SSLInfo sslInfo;
	/**
	 * 是否禁用缓存
	 */
	private boolean disableCache;
	/**
	 * 代理
	 */
	private HttpProxy proxy;
	/**
	 * 是否遇到响应状态码3xx时自动重定向请求
	 */
	private boolean followRedirects;

	/**
	 * 构造
	 */
	public ClientConfig() {
		connectionTimeout = HttpGlobalConfig.getTimeout();
		readTimeout = HttpGlobalConfig.getTimeout();
		sslInfo = HttpGlobalConfig.isTrustAnyHost() ? SSLInfo.TRUST_ANY : SSLInfo.DEFAULT;
	}

	/**
	 * 设置超时，单位：毫秒<br>
	 * 超时包括：
	 *
	 * <pre>
	 * 1. 连接超时
	 * 2. 读取响应超时
	 * </pre>
	 *
	 * @param milliseconds 超时毫秒数
	 * @return this
	 * @see #setConnectionTimeout(int)
	 * @see #setReadTimeout(int)
	 */
	public ClientConfig setTimeout(final int milliseconds) {
		setConnectionTimeout(milliseconds);
		setReadTimeout(milliseconds);
		return this;
	}

	/**
	 * 获取连接超时，单位：毫秒
	 *
	 * @return 连接超时，单位：毫秒
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * 设置连接超时，单位：毫秒
	 *
	 * @param connectionTimeout 超时毫秒数
	 * @return this
	 */
	public ClientConfig setConnectionTimeout(final int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	/**
	 * 获取读取超时，单位：毫秒
	 *
	 * @return 读取超时，单位：毫秒
	 */
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * 设置读取超时，单位：毫秒
	 *
	 * @param readTimeout 读取超时，单位：毫秒
	 * @return this
	 */
	public ClientConfig setReadTimeout(final int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	/**
	 * 获取SSLInfo
	 *
	 * @return SSLInfo
	 */
	public SSLInfo getSslInfo() {
		return this.sslInfo;
	}

	/**
	 * 设置SSLInfo<br>
	 * 只针对HTTPS请求，如果不设置，使用默认的{@link SSLInfo#TRUST_ANY}<br>
	 *
	 * @param sslInfo SSLInfo
	 * @return this
	 */
	public ClientConfig setSSLInfo(final SSLInfo sslInfo) {
		this.sslInfo = sslInfo;
		return this;
	}

	/**
	 * 打开SSL验证，即使用引擎默认的SSL验证方式
	 *
	 * @return this
	 */
	public ClientConfig enableSSLVerify() {
		return setSSLInfo(SSLInfo.DEFAULT);
	}

	/**
	 * 是否禁用缓存
	 *
	 * @return 是否禁用缓存
	 */
	public boolean isDisableCache() {
		return disableCache;
	}

	/**
	 * 设置是否禁用缓存
	 *
	 * @param disableCache 是否禁用缓存
	 */
	public void setDisableCache(final boolean disableCache) {
		this.disableCache = disableCache;
	}

	/**
	 * 获取代理
	 *
	 * @return 代理
	 */
	public HttpProxy getProxy() {
		return proxy;
	}

	/**
	 * 设置Http代理
	 *
	 * @param host 代理 主机
	 * @param port 代理 端口
	 * @return this
	 */
	public ClientConfig setHttpProxy(final String host, final int port) {
		return setProxy(new HttpProxy(host, port));
	}

	/**
	 * 设置代理
	 *
	 * @param proxy 代理 {@link HttpProxy}
	 * @return this
	 */
	public ClientConfig setProxy(final HttpProxy proxy) {
		this.proxy = proxy;
		return this;
	}

	/**
	 * 是否遇到响应状态码3xx时自动重定向请求<br>
	 * 注意：当打开客户端级别的自动重定向，则{@link Request#maxRedirects()}无效
	 *
	 * @return 是否遇到响应状态码3xx时自动重定向请求
	 */
	public boolean isFollowRedirects() {
		return followRedirects;
	}

	/**
	 * 设置是否遇到响应状态码3xx时自动重定向请求<br>
	 * 注意：当打开客户端级别的自动重定向，则{@link Request#maxRedirects()}无效
	 *
	 * @param followRedirects 是否遇到响应状态码3xx时自动重定向请求
	 * @return this
	 */
	public ClientConfig setFollowRedirects(final boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}
}
