package cn.hutool.http;

import cn.hutool.core.codec.BaseN.Base64;
import cn.hutool.core.io.file.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.client.HttpDownloader;
import cn.hutool.http.client.Request;
import cn.hutool.http.client.engine.ClientEngineFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * 下载单元测试
 *
 * @author looly
 */
public class DownloadTest {

	@Test
	@Ignore
	public void downloadPicTest() {
		final String url = "http://wx.qlogo.cn/mmopen/vKhlFcibVUtNBVDjcIowlg0X8aJfHXrTNCEFBukWVH9ta99pfEN88lU39MKspCUCOP3yrFBH3y2NbV7sYtIIlon8XxLwAEqv2/0";
		HttpDownloader.downloadFile(url, new File("e:/pic/t3.jpg"));
		Console.log("ok");
	}

	@Test
	@Ignore
	public void downloadSizeTest() {
		final String url = "https://res.t-io.org/im/upload/img/67/8948/1119501/88097554/74541310922/85/231910/366466 - 副本.jpg";
		ClientEngineFactory.get().send(Request.of(url)).body().write("e:/pic/366466.jpg");
		//HttpRequest.get(url).setSSLProtocol("TLSv1.2").executeAsync().body().write("e:/pic/366466.jpg");
	}

	@Test
	@Ignore
	public void downloadTest1() {
		final File size = HttpDownloader.downloadFile("http://explorer.bbfriend.com/crossdomain.xml", new File("e:/temp/"));
		System.out.println("Download size: " + size);
	}

	@Test
	@Ignore
	public void downloadTest() {
		// 带进度显示的文件下载
		HttpDownloader.downloadFile("http://mirrors.sohu.com/centos/7/isos/x86_64/CentOS-7-x86_64-DVD-2009.iso", FileUtil.file("d:/"), -1, new StreamProgress() {

			final long time = System.currentTimeMillis();

			@Override
			public void start() {
				Console.log("开始下载。。。。");
			}

			@Override
			public void progress(final long contentLength, final long progressSize) {
				final long speed = progressSize / (System.currentTimeMillis() - time) * 1000;
				Console.log("总大小:{}, 已下载：{}, 速度：{}/s", FileUtil.readableFileSize(contentLength), FileUtil.readableFileSize(progressSize), FileUtil.readableFileSize(speed));
			}

			@Override
			public void finish() {
				Console.log("下载完成！");
			}
		});
	}

	@Test
	@Ignore
	public void downloadFileFromUrlTest1() {
		final File file = HttpDownloader.downloadFile("http://groovy-lang.org/changelogs/changelog-3.0.5.html", new File("d:/download/temp"));
		Assert.assertNotNull(file);
		Assert.assertTrue(file.isFile());
		Assert.assertTrue(file.length() > 0);
	}

	@Test
	@Ignore
	public void downloadFileFromUrlTest2() {
		File file = null;
		try {
			file = HttpDownloader.downloadFile("https://repo1.maven.org/maven2/cn/hutool/hutool-all/5.4.0/hutool-all-5.4.0-sources.jar", FileUtil.file("d:/download/temp"), 1, new StreamProgress() {
				@Override
				public void start() {
					System.out.println("start");
				}

				@Override
				public void progress(final long contentLength, final long progressSize) {
					System.out.println("download size:" + progressSize);
				}

				@Override
				public void finish() {
					System.out.println("end");
				}
			});

			Assert.assertNotNull(file);
			Assert.assertTrue(file.exists());
			Assert.assertTrue(file.isFile());
			Assert.assertTrue(file.length() > 0);
			Assert.assertTrue(file.getName().length() > 0);
		} catch (final Exception e) {
			Assert.assertTrue(e instanceof IORuntimeException);
		} finally {
			FileUtil.del(file);
		}
	}

	@Test
	@Ignore
	public void downloadFileFromUrlTest3() {
		File file = null;
		try {
			file = HttpDownloader.downloadFile("https://repo1.maven.org/maven2/cn/hutool/hutool-all/5.4.0/hutool-all-5.4.0-sources.jar", FileUtil.file("d:/download/temp"), -1, new StreamProgress() {
				@Override
				public void start() {
					System.out.println("start");
				}

				@Override
				public void progress(final long contentLength, final long progressSize) {
					System.out.println("contentLength:" + contentLength + "download size:" + progressSize);
				}

				@Override
				public void finish() {
					System.out.println("end");
				}
			});

			Assert.assertNotNull(file);
			Assert.assertTrue(file.exists());
			Assert.assertTrue(file.isFile());
			Assert.assertTrue(file.length() > 0);
			Assert.assertTrue(file.getName().length() > 0);
		} finally {
			FileUtil.del(file);
		}
	}

	@Test
	@Ignore
	public void downloadFileFromUrlTest4() {
		File file = null;
		try {
			file = HttpDownloader.downloadFile("http://groovy-lang.org/changelogs/changelog-3.0.5.html", FileUtil.file("d:/download/temp"), 1);

			Assert.assertNotNull(file);
			Assert.assertTrue(file.exists());
			Assert.assertTrue(file.isFile());
			Assert.assertTrue(file.length() > 0);
			Assert.assertTrue(file.getName().length() > 0);
		} catch (final Exception e) {
			Assert.assertTrue(e instanceof IORuntimeException);
		} finally {
			FileUtil.del(file);
		}
	}


	@Test
	@Ignore
	public void downloadFileFromUrlTest5() {
		File file = null;
		try {
			file = HttpDownloader.downloadFile("http://groovy-lang.org/changelogs/changelog-3.0.5.html", FileUtil.file("d:/download/temp", UUID.randomUUID().toString()));

			Assert.assertNotNull(file);
			Assert.assertTrue(file.exists());
			Assert.assertTrue(file.isFile());
			Assert.assertTrue(file.length() > 0);
		} finally {
			FileUtil.del(file);
		}

		File file1 = null;
		try {
			file1 = HttpDownloader.downloadFile("http://groovy-lang.org/changelogs/changelog-3.0.5.html", FileUtil.file("d:/download/temp"));

			Assert.assertNotNull(file1);
			Assert.assertTrue(file1.exists());
			Assert.assertTrue(file1.isFile());
			Assert.assertTrue(file1.length() > 0);
		} finally {
			FileUtil.del(file1);
		}
	}

	@Test
	@Ignore
	public void downloadTeamViewerTest() throws IOException {
		// 此URL有3次重定向, 需要请求4次
		final String url = "https://download.teamviewer.com/download/TeamViewer_Setup_x64.exe";
		HttpGlobalConfig.setMaxRedirectCount(20);
		final Path temp = Files.createTempFile("tmp", ".exe");
		final File file = HttpDownloader.downloadFile(url, temp.toFile());
		Console.log(file.length());
	}

	@Test
	@Ignore
	public void downloadToStreamTest() {
		String url2 = "http://storage.chancecloud.com.cn/20200413_%E7%B2%A4B12313_386.pdf";
		final ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		HttpDownloader.download(url2, os2, false, null);

		url2 = "http://storage.chancecloud.com.cn/20200413_粤B12313_386.pdf";
		HttpDownloader.download(url2, os2, false, null);
	}

	@Test
	@Ignore
	public void downloadStringTest() {
		final String url = "https://www.baidu.com";
		// 从远程直接读取字符串，需要自定义编码，直接调用JDK方法
		final String content2 = HttpDownloader.downloadString(url, CharsetUtil.UTF_8, null);
		Console.log(content2);
	}

	@Test
	@Ignore
	public void gimg2Test(){
		final byte[] bytes = HttpDownloader.downloadBytes("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic.jj20.com%2Fup%2Fallimg%2F1114%2F0H320120Z3%2F200H3120Z3-6-1200.jpg&refer=http%3A%2F%2Fpic.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621996490&t=8c384c2823ea453da15a1b9cd5183eea");
		Console.log(Base64.encode(bytes));
	}
}
