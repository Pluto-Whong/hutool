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

package org.dromara.hutool.poi.excel.cell.values;

import org.dromara.hutool.core.text.CharUtil;
import org.dromara.hutool.poi.excel.ExcelDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.time.LocalDateTime;

/**
 * 数字类型单元格值<br>
 * 单元格值可能为Long、Double、Date
 *
 * @author looly
 * @since 5.7.8
 */
public class NumericCellValue implements CellValue<Object> {

	private final Cell cell;

	/**
	 * 构造
	 *
	 * @param cell {@link Cell}
	 */
	public NumericCellValue(final Cell cell) {
		this.cell = cell;
	}

	@Override
	public Object getValue() {
		final double value = cell.getNumericCellValue();

		final CellStyle style = cell.getCellStyle();
		if (null != style) {
			// 判断是否为日期
			if (ExcelDateUtil.isDateFormat(cell)) {
				final LocalDateTime date = cell.getLocalDateTimeCellValue();
				// 1899年写入会导致数据错乱，读取到1899年证明这个单元格的信息不关注年月日
				if(1899 == date.getYear()){
					return date.toLocalTime();
				}
				return date;
			}

			final String format = style.getDataFormatString();
			// 普通数字
			if (null != format && format.indexOf(CharUtil.DOT) < 0) {
				final long longPart = (long) value;
				if (((double) longPart) == value) {
					// 对于无小数部分的数字类型，转为Long
					return longPart;
				}
			}
		}

		// 某些Excel单元格值为double计算结果，可能导致精度问题，通过转换解决精度问题。
		return Double.parseDouble(NumberToTextConverter.toText(value));
	}
}
