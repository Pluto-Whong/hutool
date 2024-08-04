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

package org.dromara.hutool.json.test.bean.report;

import java.util.Collection;

/**
 * 测试环境信息
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class EnvSettingInfo {

	public static boolean DEV_MODE = true;

	private boolean remoteMode;

	private String hubRemoteUrl;

	private String reportFolder = "/report";
	private String screenshotFolder = "/screenshot";

	private String elementFolder = "/config/element/";
	private String suiteFolder = "/config/suite/";

	private String chromeDriverPath = "/src/main/resources/chromedriver.exe";
	private String ieDriverPath = "/src/main/resources/IEDriverServer.exe";
	private String operaDriverPath = "/src/main/resources/operadriver.exe";
	private String firefoxDriverPath = "/src/main/resources/geckodriver.exe";

	private Double defaultSleepSeconds;

	private Integer elementLocationRetryCount;
	private Double elementLocationTimeouts;

	/**
	 * 收件人列表
	 */
	private Collection<String> tos;
	/**
	 * 抄送人列表
	 */
	private Collection<String> ccs;
	/**
	 * 密送人列表
	 */
	private Collection<String> bccs;

	/**
	 * 是否可以开启定时任务
	 */
	private boolean cronEnabled = false;

	/**
	 * 定时执行：suite文件
	 */
	private String cronSuite;

	/**
	 * 定时执行：cron表达式，支持linux crontab格式(5位)和Quartz的cron格式(6位)
	 */
	private String cronExpression;

	/**
	 * 存储测试报告数据的轻量级数据库，路径
	 */
	private String sqlitePath;

	public EnvSettingInfo() {
	}

	public void setSqlitePath(final String sqlitePath) {
		this.sqlitePath = sqlitePath;
	}

	public String getSqlitePath() {
		return sqlitePath;
	}

	public void setCronEnabled(final boolean cronEnabled) {
		this.cronEnabled = cronEnabled;
	}

	public boolean isCronEnabled() {
		return cronEnabled;
	}

	public String getCronSuite() {
		return cronSuite;
	}

	public void setCronSuite(final String cronSuite) {
		this.cronSuite = cronSuite;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(final String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Integer getElementLocationRetryCount() {
		return elementLocationRetryCount;
	}

	public void setElementLocationRetryCount(final Integer elementLocationRetryCount) {
		this.elementLocationRetryCount = elementLocationRetryCount;
	}

	public Double getElementLocationTimeouts() {
		return elementLocationTimeouts;
	}

	public void setElementLocationTimeouts(final Double elementLocationTimeouts) {
		this.elementLocationTimeouts = elementLocationTimeouts;
	}

	public String getElementFolder() {
		return elementFolder;
	}

	public void setElementFolder(final String elementFolder) {
		this.elementFolder = elementFolder;
	}

	public String getSuiteFolder() {
		return suiteFolder;
	}

	public void setSuiteFolder(final String suiteFolder) {
		this.suiteFolder = suiteFolder;
	}

	public boolean isRemoteMode() {
		return remoteMode;
	}

	public void setRemoteMode(final boolean remoteMode) {
		this.remoteMode = remoteMode;
	}

	public String getHubRemoteUrl() {
		return hubRemoteUrl;
	}

	public void setHubRemoteUrl(final String hubRemoteUrl) {
		this.hubRemoteUrl = hubRemoteUrl;
	}

	public String getReportFolder() {
		return reportFolder;
	}

	public void setReportFolder(final String reportFolder) {
		this.reportFolder = reportFolder;
	}

	public String getScreenshotFolder() {
		return screenshotFolder;
	}

	public void setScreenshotFolder(final String screenshotFolder) {
		this.screenshotFolder = screenshotFolder;
	}

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(final String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public String getIeDriverPath() {
		return ieDriverPath;
	}

	public void setIeDriverPath(final String ieDriverPath) {
		this.ieDriverPath = ieDriverPath;
	}

	public String getOperaDriverPath() {
		return operaDriverPath;
	}

	public void setOperaDriverPath(final String operaDriverPath) {
		this.operaDriverPath = operaDriverPath;
	}

	public String getFirefoxDriverPath() {
		return firefoxDriverPath;
	}

	public void setFirefoxDriverPath(final String firefoxDriverPath) {
		this.firefoxDriverPath = firefoxDriverPath;
	}

	public Double getDefaultSleepSeconds() {
		return defaultSleepSeconds;
	}

	public void setDefaultSleepSeconds(final Double defaultSleepSeconds) {
		this.defaultSleepSeconds = defaultSleepSeconds;
	}

	public Collection<String> getTos() {
		return tos;
	}

	public void setTos(final Collection<String> tos) {
		this.tos = tos;
	}

	public Collection<String> getCcs() {
		return ccs;
	}

	public void setCcs(final Collection<String> ccs) {
		this.ccs = ccs;
	}

	public Collection<String> getBccs() {
		return bccs;
	}

	public void setBccs(final Collection<String> bccs) {
		this.bccs = bccs;
	}

	@Override
	public String toString() {
		return "EnvSettingInfo [remoteMode=" + remoteMode + ", hubRemoteUrl=" + hubRemoteUrl + ", reportFolder="
				+ reportFolder + ", screenshotFolder=" + screenshotFolder + ", elementFolder=" + elementFolder
				+ ", suiteFolder=" + suiteFolder + ", chromeDriverPath=" + chromeDriverPath + ", ieDriverPath="
				+ ieDriverPath + ", operaDriverPath=" + operaDriverPath + ", firefoxDriverPath=" + firefoxDriverPath
				+ ", defaultSleepSeconds=" + defaultSleepSeconds + ", elementLocationRetryCount="
				+ elementLocationRetryCount + ", elementLocationTimeouts=" + elementLocationTimeouts + ", mailAccount="
				+ 1 + ", tos=" + tos + ", ccs=" + ccs + ", bccs=" + bccs + ", cronEnabled=" + cronEnabled
				+ ", cronSuite=" + cronSuite + ", cronExpression=" + cronExpression + "]";
	}
}
