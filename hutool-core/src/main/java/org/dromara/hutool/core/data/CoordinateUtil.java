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

package org.dromara.hutool.core.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 坐标系转换相关工具类，主流坐标系包括：<br>
 * <ul>
 *     <li>WGS84坐标系：即地球坐标系，中国外谷歌地图</li>
 *     <li>GCJ02坐标系：即火星坐标系，高德、腾讯、阿里等使用</li>
 *     <li>BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系。百度、搜狗等使用</li>
 * </ul>
 * <p>
 * 坐标转换相关参考: <a href="https://tool.lu/coordinate/">https://tool.lu/coordinate/</a><br>
 * 参考：<a href="https://github.com/JourWon/coordinate-transform">https://github.com/JourWon/coordinate-transform</a>
 *
 * @author hongzhe.qin(qin462328037at163.com), looly
 * @since 5.7.16
 */
public class CoordinateUtil {

	/**
	 * 坐标转换参数：(火星坐标系与百度坐标系转换的中间量)
	 */
	public static final double X_PI = 3.1415926535897932384626433832795 * 3000.0 / 180.0;

	/**
	 * 坐标转换参数：π
	 */
	public static final double PI = 3.1415926535897932384626433832795D;

	/**
	 * 地球半径（Krasovsky 1940）
	 */
	public static final double RADIUS = 6378245.0D;

	/**
	 * 修正参数（偏率ee）
	 */
	public static final double CORRECTION_PARAM = 0.00669342162296594323D;

	/**
	 * 判断坐标是否在国外<br>
	 * 火星坐标系 (GCJ-02)只对国内有效，国外无需转换
	 *
	 * @param lng 经度
	 * @param lat 纬度
	 * @return 坐标是否在国外
	 */
	public static boolean outOfChina(final double lng, final double lat) {
		return (lng < 72.004 || lng > 137.8347) || (lat < 0.8293 || lat > 55.8271);
	}

	//----------------------------------------------------------------------------------- WGS84
	/**
	 * WGS84 转换为 火星坐标系 (GCJ-02)
	 *
	 * @param lng 经度值
	 * @param lat 纬度值
	 * @return 火星坐标 (GCJ-02)
	 */
	public static Coordinate wgs84ToGcj02(final double lng, final double lat) {
		return new Coordinate(lng, lat).offset(offset(lng, lat, true));
	}

	/**
	 * WGS84 坐标转为 百度坐标系 (BD-09) 坐标
	 *
	 * @param lng 经度值
	 * @param lat 纬度值
	 * @return bd09 坐标
	 */
	public static Coordinate wgs84ToBd09(final double lng, final double lat) {
		final Coordinate gcj02 = wgs84ToGcj02(lng, lat);
		return gcj02ToBd09(gcj02.lng, gcj02.lat);
	}

	//----------------------------------------------------------------------------------- GCJ-02
	/**
	 * 火星坐标系 (GCJ-02) 转换为 WGS84
	 *
	 * @param lng 经度坐标
	 * @param lat 维度坐标
	 * @return WGS84 坐标
	 */
	public static Coordinate gcj02ToWgs84(final double lng, final double lat) {
		return new Coordinate(lng, lat).offset(offset(lng, lat, false));
	}

	/**
	 * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
	 *
	 * @param lng 经度值
	 * @param lat 纬度值
	 * @return BD-09 坐标
	 */
	public static Coordinate gcj02ToBd09(final double lng, final double lat) {
		final double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
		final double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
		final double bd_lng = z * Math.cos(theta) + 0.0065;
		final double bd_lat = z * Math.sin(theta) + 0.006;
		return new Coordinate(bd_lng, bd_lat);
	}

	//----------------------------------------------------------------------------------- BD-09
	/**
	 * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
	 * 即 百度 转 谷歌、高德
	 *
	 * @param lng 经度值
	 * @param lat 纬度值
	 * @return GCJ-02 坐标
	 */
	public static Coordinate bd09ToGcj02(final double lng, final double lat) {
		final double x = lng - 0.0065;
		final double y = lat - 0.006;
		final double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
		final double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
		final double gg_lng = z * Math.cos(theta);
		final double gg_lat = z * Math.sin(theta);
		return new Coordinate(gg_lng, gg_lat);
	}

	/**
	 * 百度坐标系 (BD-09) 与 WGS84 的转换
	 *
	 * @param lng 经度值
	 * @param lat 纬度值
	 * @return WGS84坐标
	 */
	public static Coordinate bd09toWgs84(final double lng, final double lat) {
		final Coordinate gcj02 = bd09ToGcj02(lng, lat);
		return gcj02ToWgs84(gcj02.lng, gcj02.lat);
	}

	/**
	 * WGS84 坐标转为 墨卡托投影
	 *
	 * @param lng 经度值
	 * @param lat 纬度值
	 * @return 墨卡托投影
	 */
	public static Coordinate wgs84ToMercator(final double lng, final double lat) {
		final double x = lng * 20037508.342789244 / 180;
		double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
		y = y * 20037508.342789244 / 180;
		return new Coordinate(x, y);
	}

	/**
	 * 墨卡托投影 转为 WGS84 坐标
	 *
	 * @param mercatorX 墨卡托X坐标
	 * @param mercatorY 墨卡托Y坐标
	 * @return WGS84 坐标
	 */
	public static Coordinate mercatorToWgs84(final double mercatorX, final double mercatorY) {
		final double x = mercatorX / 20037508.342789244 * 180;
		double y = mercatorY / 20037508.342789244 * 180;
		y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2);
		return new Coordinate(x, y);
	}

	//----------------------------------------------------------------------------------- Private methods begin

	/**
	 * WGS84 与 火星坐标系 (GCJ-02)转换的偏移算法（非精确）
	 *
	 * @param lng 经度值
	 * @param lat 纬度值
	 * @param isPlus 是否正向偏移：WGS84转GCJ-02使用正向，否则使用反向
	 * @return 偏移坐标
	 */
	private static Coordinate offset(final double lng, final double lat, final boolean isPlus) {
		double dlng = transLng(lng - 105.0, lat - 35.0);
		double dlat = transLat(lng - 105.0, lat - 35.0);

		double magic = Math.sin(lat / 180.0 * PI);
		magic = 1 - CORRECTION_PARAM * magic * magic;
		final double sqrtMagic = Math.sqrt(magic);

		dlng = (dlng * 180.0) / (RADIUS / sqrtMagic * Math.cos(lat / 180.0 * PI) * PI);
		dlat = (dlat * 180.0) / ((RADIUS * (1 - CORRECTION_PARAM)) / (magic * sqrtMagic) * PI);

		if(!isPlus){
			dlng = - dlng;
			dlat = - dlat;
		}

		return new Coordinate(dlng, dlat);
	}

	/**
	 * 计算经度坐标
	 *
	 * @param lng 经度坐标
	 * @param lat 纬度坐标
	 * @return ret 计算完成后的
	 */
	private static double transLng(final double lng, final double lat) {
		double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
		return ret;
	}

	/**
	 * 计算纬度坐标
	 *
	 * @param lng 经度
	 * @param lat 纬度
	 * @return ret 计算完成后的
	 */
	private static double transLat(final double lng, final double lat) {
		double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat
				+ 0.2 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}
	//----------------------------------------------------------------------------------- Private methods end

	/**
	 * 坐标经纬度
	 *
	 * @author looly
	 */
	public static class Coordinate implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * 经度
		 */
		private double lng;
		/**
		 * 纬度
		 */
		private double lat;

		/**
		 * 构造
		 *
		 * @param lng 经度
		 * @param lat 纬度
		 */
		public Coordinate(final double lng, final double lat) {
			this.lng = lng;
			this.lat = lat;
		}

		/**
		 * 获取经度
		 *
		 * @return 经度
		 */
		public double getLng() {
			return lng;
		}

		/**
		 * 设置经度
		 *
		 * @param lng 经度
		 * @return this
		 */
		public Coordinate setLng(final double lng) {
			this.lng = lng;
			return this;
		}

		/**
		 * 获取纬度
		 *
		 * @return 纬度
		 */
		public double getLat() {
			return lat;
		}

		/**
		 * 设置纬度
		 *
		 * @param lat 纬度
		 * @return this
		 */
		public Coordinate setLat(final double lat) {
			this.lat = lat;
			return this;
		}

		/**
		 * 当前坐标偏移指定坐标
		 *
		 * @param offset 偏移量
		 * @return this
		 */
		public Coordinate offset(final Coordinate offset){
			this.lng += offset.lng;
			this.lat += offset.lat;
			return this;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			final Coordinate that = (Coordinate) o;
			return Double.compare(that.lng, lng) == 0 && Double.compare(that.lat, lat) == 0;
		}

		@Override
		public int hashCode() {
			return Objects.hash(lng, lat);
		}

		@Override
		public String toString() {
			return "Coordinate{" +
					"lng=" + lng +
					", lat=" + lat +
					'}';
		}
	}
}
