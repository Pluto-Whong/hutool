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

package org.dromara.hutool.core.net.url;

import org.dromara.hutool.core.codec.PercentCodec;

/**
 * <a href="https://www.ietf.org/rfc/rfc3986.html">RFC3986</a> 编码实现<br>
 * 定义见：<a href="https://www.ietf.org/rfc/rfc3986.html#appendix-A">https://www.ietf.org/rfc/rfc3986.html#appendix-A</a>
 *
 * @author looly
 * @since 5.7.16
 */
public class RFC3986 {

	/**
	 * 通用URI组件分隔符<br>
	 * gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"
	 */
	public static final PercentCodec GEN_DELIMS = PercentCodec.Builder.of(":/?#[]@").build();

	/**
	 * sub-delims = "!" / "$" / "{@code &}" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
	 */
	public static final PercentCodec SUB_DELIMS = PercentCodec.Builder.of("!$&'()*+,;=").build();

	/**
	 * reserved = gen-delims / sub-delims<br>
	 * see：<a href="https://www.ietf.org/rfc/rfc3986.html#section-2.2">https://www.ietf.org/rfc/rfc3986.html#section-2.2</a>
	 */
	public static final PercentCodec RESERVED = PercentCodec.Builder.of(GEN_DELIMS).or(SUB_DELIMS).build();

	/**
	 * 非保留字符，即URI中不作为分隔符使用的字符<br>
	 * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"<br>
	 * see: <a href="https://www.ietf.org/rfc/rfc3986.html#section-2.3">https://www.ietf.org/rfc/rfc3986.html#section-2.3</a>
	 */
	public static final PercentCodec UNRESERVED = PercentCodec.Builder.of(unreservedChars()).build();

	/**
	 * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
	 */
	public static final PercentCodec PCHAR = PercentCodec.Builder.of(UNRESERVED).or(SUB_DELIMS).addSafes(":@").build();

	/**
	 * segment  = pchar<br>
	 * see: <a href="https://www.ietf.org/rfc/rfc3986.html#section-3.3">https://www.ietf.org/rfc/rfc3986.html#section-3.3</a>
	 */
	public static final PercentCodec SEGMENT = PCHAR;
	/**
	 * segment-nz-nc  = SEGMENT ; non-zero-length segment without any colon ":"
	 */
	public static final PercentCodec SEGMENT_NZ_NC = PercentCodec.Builder.of(SEGMENT).removeSafe(':').build();

	/**
	 * path = segment / "/"
	 */
	public static final PercentCodec PATH = PercentCodec.Builder.of(SEGMENT).addSafe('/').build();

	/**
	 * query = pchar / "/" / "?"
	 */
	public static final PercentCodec QUERY = PercentCodec.Builder.of(PCHAR).addSafes("/?").build();

	/**
	 * fragment     = pchar / "/" / "?"
	 */
	public static final PercentCodec FRAGMENT = QUERY;

	/**
	 * query中的value<br>
	 * value不能包含"{@code &}"，可以包含 "="
	 */
	public static final PercentCodec QUERY_PARAM_VALUE = PercentCodec.Builder.of(QUERY).removeSafe('&').build();

	/**
	 * query中的value编码器，严格模式，value中不能包含任何分隔符。
	 *
	 * @since 6.0.0
	 */
	public static final PercentCodec QUERY_PARAM_VALUE_STRICT = UNRESERVED;

	/**
	 * query中的key<br>
	 * key不能包含"{@code &}" 和 "="
	 */
	public static final PercentCodec QUERY_PARAM_NAME = PercentCodec.Builder.of(QUERY_PARAM_VALUE).removeSafe('=').build();

	/**
	 * query中的key编码器，严格模式，key中不能包含任何分隔符。
	 *
	 * @since 6.0.0
	 */
	public static final PercentCodec QUERY_PARAM_NAME_STRICT = UNRESERVED;

	/**
	 * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
	 *
	 * @return unreserved字符
	 */
	private static StringBuilder unreservedChars() {
		final StringBuilder sb = new StringBuilder();

		// ALPHA
		for (char c = 'A'; c <= 'Z'; c++) {
			sb.append(c);
		}
		for (char c = 'a'; c <= 'z'; c++) {
			sb.append(c);
		}

		// DIGIT
		for (char c = '0'; c <= '9'; c++) {
			sb.append(c);
		}

		// "-" / "." / "_" / "~"
		sb.append("_.-~");

		return sb;
	}
}
