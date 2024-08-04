/*
 * Copyright (c) 2024 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.core.codec.binary;

import org.dromara.hutool.core.codec.Decoder;
import org.dromara.hutool.core.codec.Encoder;
import org.dromara.hutool.core.text.StrUtil;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Base58编码器<br>
 * 此编码器不包括校验码、版本等信息
 *
 * @author lin， looly
 * @since 5.7.22
 */
public class Base58Codec implements Encoder<byte[], String>, Decoder<CharSequence, byte[]>, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单例
	 */
	public static Base58Codec INSTANCE = new Base58Codec();

	/**
	 * Base58编码
	 *
	 * @param data 被编码的数据，不带校验和。
	 * @return 编码后的字符串
	 */
	@Override
	public String encode(final byte[] data) {
		return Base58Encoder.ENCODER.encode(data);
	}

	/**
	 * 解码给定的Base58字符串
	 *
	 * @param encoded Base58编码字符串
	 * @return 解码后的bytes
	 * @throws IllegalArgumentException 非标准Base58字符串
	 */
	@Override
	public byte[] decode(final CharSequence encoded) throws IllegalArgumentException {
		return Base58Decoder.DECODER.decode(encoded);
	}

	/**
	 * Base58编码器
	 *
	 * @since 5.8.0
	 */
	public static class Base58Encoder implements Encoder<byte[], String> {
		private static final String DEFAULT_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

		/**
		 * 编码器
		 */
		public static final Base58Encoder ENCODER = new Base58Encoder(DEFAULT_ALPHABET.toCharArray());

		private final char[] alphabet;
		private final char alphabetZero;

		/**
		 * 构造
		 *
		 * @param alphabet 编码字母表
		 */
		public Base58Encoder(final char[] alphabet) {
			this.alphabet = alphabet;
			alphabetZero = alphabet[0];
		}

		@Override
		public String encode(byte[] data) {
			if (null == data) {
				return null;
			}
			if (data.length == 0) {
				return StrUtil.EMPTY;
			}
			// 计算开头0的个数
			int zeroCount = 0;
			while (zeroCount < data.length && data[zeroCount] == 0) {
				++zeroCount;
			}
			// 将256位编码转换为58位编码
			data = Arrays.copyOf(data, data.length); // since we modify it in-place
			final char[] encoded = new char[data.length * 2]; // upper bound
			int outputStart = encoded.length;
			for (int inputStart = zeroCount; inputStart < data.length; ) {
				encoded[--outputStart] = alphabet[divmod(data, inputStart, 256, 58)];
				if (data[inputStart] == 0) {
					++inputStart; // optimization - skip leading zeros
				}
			}
			// Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
			while (outputStart < encoded.length && encoded[outputStart] == alphabetZero) {
				++outputStart;
			}
			while (--zeroCount >= 0) {
				encoded[--outputStart] = alphabetZero;
			}
			// Return encoded string (including encoded leading zeros).
			return new String(encoded, outputStart, encoded.length - outputStart);
		}
	}

	/**
	 * Base58解码器
	 *
	 * @since 5.8.0
	 */
	public static class Base58Decoder implements Decoder<CharSequence, byte[]> {

		/**
		 * 解码器
		 */
		public static Base58Decoder DECODER = new Base58Decoder(Base58Encoder.DEFAULT_ALPHABET);

		private final byte[] lookupTable;

		/**
		 * 构造
		 *
		 * @param alphabet 编码字符表
		 */
		public Base58Decoder(final String alphabet) {
			final byte[] lookupTable = new byte['z' + 1];
			Arrays.fill(lookupTable, (byte) -1);

			final int length = alphabet.length();
			for (int i = 0; i < length; i++) {
				lookupTable[alphabet.charAt(i)] = (byte) i;
			}
			this.lookupTable = lookupTable;
		}

		@Override
		public byte[] decode(final CharSequence encoded) {
			if (encoded.length() == 0) {
				return new byte[0];
			}
			// Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
			final byte[] input58 = new byte[encoded.length()];
			for (int i = 0; i < encoded.length(); ++i) {
				final char c = encoded.charAt(i);
				final int digit = c < 128 ? lookupTable[c] : -1;
				if (digit < 0) {
					throw new IllegalArgumentException(StrUtil.format("Invalid char '{}' at [{}]", c, i));
				}
				input58[i] = (byte) digit;
			}
			// Count leading zeros.
			int zeros = 0;
			while (zeros < input58.length && input58[zeros] == 0) {
				++zeros;
			}
			// Convert base-58 digits to base-256 digits.
			final byte[] decoded = new byte[encoded.length()];
			int outputStart = decoded.length;
			for (int inputStart = zeros; inputStart < input58.length; ) {
				decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
				if (input58[inputStart] == 0) {
					++inputStart; // optimization - skip leading zeros
				}
			}
			// Ignore extra leading zeroes that were added during the calculation.
			while (outputStart < decoded.length && decoded[outputStart] == 0) {
				++outputStart;
			}
			// Return decoded data (including original number of leading zeros).
			return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
		}
	}

	/**
	 * Divides a number, represented as an array of bytes each containing a single digit
	 * in the specified base, by the given divisor. The given number is modified in-place
	 * to contain the quotient, and the return value is the remainder.
	 *
	 * @param number     the number to divide
	 * @param firstDigit the index within the array of the first non-zero digit
	 *                   (this is used for optimization by skipping the leading zeros)
	 * @param base       the base in which the number's digits are represented (up to 256)
	 * @param divisor    the number to divide by (up to 256)
	 * @return the remainder of the division operation
	 */
	private static byte divmod(final byte[] number, final int firstDigit, final int base, final int divisor) {
		// this is just long division which accounts for the base of the input digits
		int remainder = 0;
		for (int i = firstDigit; i < number.length; i++) {
			final int digit = (int) number[i] & 0xFF;
			final int temp = remainder * base + digit;
			number[i] = (byte) (temp / divisor);
			remainder = temp % divisor;
		}
		return (byte) remainder;
	}
}
