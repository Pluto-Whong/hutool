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

package org.dromara.hutool.core.io.buffer;

import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.lang.Assert;

/**
 * 代码移植自<a href="https://github.com/biezhi/blade">blade</a><br>
 * 快速缓冲，将数据存放在缓冲集中，取代以往的单一数组
 *
 * @author biezhi, looly
 * @since 1.0
 */
public class FastByteBuffer {

	/**
	 * 缓冲集
	 */
	private byte[][] buffers = new byte[16][];
	/**
	 * 缓冲数
	 */
	private int buffersCount;
	/**
	 * 当前缓冲索引
	 */
	private int currentBufferIndex = -1;
	/**
	 * 当前缓冲
	 */
	private byte[] currentBuffer;
	/**
	 * 当前缓冲偏移量
	 */
	private int offset;
	/**
	 * 缓冲字节数
	 */
	private int size;

	/**
	 * 一个缓冲区的最小字节数
	 */
	private final int minChunkLen;

	/**
	 * 构造
	 */
	public FastByteBuffer() {
		this(IoUtil.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * 构造
	 *
	 * @param size 一个缓冲区的最小字节数
	 */
	public FastByteBuffer(int size) {
		if (size <= 0) {
			size = IoUtil.DEFAULT_BUFFER_SIZE;
		}
		this.minChunkLen = Math.abs(size);
	}

	/**
	 * 分配下一个缓冲区，不会小于1024
	 *
	 * @param newSize 理想缓冲区字节数
	 */
	private void needNewBuffer(final int newSize) {
		final int delta = newSize - size;
		final int newBufferSize = Math.max(minChunkLen, delta);

		currentBufferIndex++;
		currentBuffer = new byte[newBufferSize];
		offset = 0;

		// add buffer
		if (currentBufferIndex >= buffers.length) {
			final int newLen = buffers.length << 1;
			final byte[][] newBuffers = new byte[newLen][];
			System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
			buffers = newBuffers;
		}
		buffers[currentBufferIndex] = currentBuffer;
		buffersCount++;
	}

	/**
	 * 向快速缓冲加入数据
	 *
	 * @param array 数据
	 * @param off   偏移量
	 * @param len   字节数
	 * @return 快速缓冲自身 @see FastByteBuffer
	 */
	public FastByteBuffer append(final byte[] array, final int off, final int len) {
		final int end = off + len;
		if ((off < 0) || (len < 0) || (end > array.length)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return this;
		}
		final int newSize = size + len;
		int remaining = len;

		if (currentBuffer != null) {
			// first try to fill current buffer
			final int part = Math.min(remaining, currentBuffer.length - offset);
			System.arraycopy(array, end - remaining, currentBuffer, offset, part);
			remaining -= part;
			offset += part;
			size += part;
		}

		if (remaining > 0) {
			// still some data left
			// ask for new buffer
			needNewBuffer(newSize);

			// then copy remaining
			// but this time we are sure that it will fit
			final int part = Math.min(remaining, currentBuffer.length - offset);
			System.arraycopy(array, end - remaining, currentBuffer, offset, part);
			offset += part;
			size += part;
		}

		return this;
	}

	/**
	 * 向快速缓冲加入数据
	 *
	 * @param array 数据
	 * @return 快速缓冲自身 @see FastByteBuffer
	 */
	public FastByteBuffer append(final byte[] array) {
		return append(array, 0, array.length);
	}

	/**
	 * 向快速缓冲加入一个字节
	 *
	 * @param element 一个字节的数据
	 * @return 快速缓冲自身 @see FastByteBuffer
	 */
	public FastByteBuffer append(final byte element) {
		if ((currentBuffer == null) || (offset == currentBuffer.length)) {
			needNewBuffer(size + 1);
		}

		currentBuffer[offset] = element;
		offset++;
		size++;

		return this;
	}

	/**
	 * 将另一个快速缓冲加入到自身
	 *
	 * @param buff 快速缓冲
	 * @return 快速缓冲自身 @see FastByteBuffer
	 */
	public FastByteBuffer append(final FastByteBuffer buff) {
		if (buff.size == 0) {
			return this;
		}
		for (int i = 0; i < buff.currentBufferIndex; i++) {
			append(buff.buffers[i]);
		}
		append(buff.currentBuffer, 0, buff.offset);
		return this;
	}

	/**
	 * 长度
	 *
	 * @return 长度
	 */
	public int size() {
		return size;
	}

	/**
	 * 是否为空
	 *
	 * @return 是否为空
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * 当前缓冲位于缓冲区的索引位
	 *
	 * @return {@link #currentBufferIndex}
	 */
	public int index() {
		return currentBufferIndex;
	}

	/**
	 * 获取当前缓冲偏移量
	 *
	 * @return 当前缓冲偏移量
	 */
	public int offset() {
		return offset;
	}

	/**
	 * 根据索引位返回缓冲集中的缓冲
	 *
	 * @param index 索引位
	 * @return 缓冲
	 */
	public byte[] array(final int index) {
		return buffers[index];
	}

	/**
	 * 复位缓冲
	 */
	public void reset() {
		size = 0;
		offset = 0;
		currentBufferIndex = -1;
		currentBuffer = null;
		buffersCount = 0;
	}

	/**
	 * 返回快速缓冲中的数据，如果缓冲区中的数据长度固定，则直接返回原始数组<br>
	 * 注意此方法共享数组，不能修改数组内容！
	 *
	 * @return 快速缓冲中的数据
	 */
	public byte[] toArrayZeroCopyIfPossible() {
		if(1 == currentBufferIndex){
			final int len = buffers[0].length;
			if(len == size){
				return buffers[0];
			}
		}

		return toArray();
	}

	/**
	 * 返回快速缓冲中的数据
	 *
	 * @return 快速缓冲中的数据
	 */
	public byte[] toArray() {
		return toArray(0, this.size);
	}

	/**
	 * 返回快速缓冲中的数据
	 *
	 * @param start 逻辑起始位置
	 * @param len   逻辑字节长
	 * @return 快速缓冲中的数据
	 */
	public byte[] toArray(int start, int len) {
		Assert.isTrue(start >= 0, "Start must be greater than zero!");
		Assert.isTrue(len >= 0, "Length must be greater than zero!");

		if(start >= this.size || len == 0){
			return new byte[0];
		}
		if(len > (this.size - start)){
			len = this.size - start;
		}
		int remaining = len;
		int pos = 0;
		final byte[] result = new byte[len];

		int i = 0;
		while (start >= buffers[i].length) {
			start -= buffers[i].length;
			i++;
		}

		while (i < buffersCount) {
			final int bufLen = Math.min(buffers[i].length - start, remaining);
			System.arraycopy(buffers[i], start, result, pos, bufLen);
			pos += bufLen;
			remaining -= bufLen;
			if (remaining == 0) {
				break;
			}
			start = 0;
			i++;
		}
		return result;
	}

	/**
	 * 根据索引位返回一个字节
	 *
	 * @param index 索引位
	 * @return 一个字节
	 */
	public byte get(int index) {
		if ((index >= size) || (index < 0)) {
			throw new IndexOutOfBoundsException();
		}
		int ndx = 0;
		while (true) {
			final byte[] b = buffers[ndx];
			if (index < b.length) {
				return b[index];
			}
			ndx++;
			index -= b.length;
		}
	}

}
