import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CacheDownloader {

	private RSClient client;
	private String[] cacheLink = { 
			"http://www.directlinkupload.com/uploads/85.157.160.229/cache.zip",
			"http://www.directlinkupload.com/uploads/85.157.160.229/cache_2lGDD2.zip",
			"http://www.directlinkupload.com/uploads/85.157.160.229/cache_WBt2FU.zip",
			"http://www.directlinkupload.com/uploads/85.157.160.229/cache_mpoQdz.zip",
			"http://www.directlinkupload.com/uploads/85.157.160.229/cache_ueQyNI.zip",
			"http://www.directlinkupload.com/uploads/85.157.160.229/cache_vyXzNx.zip",
			"http://www.directlinkupload.com/uploads/85.157.160.229/cache_xXm33v.zip" };

	private int cache_link_index = 0;
	private String fileToExtract = Signlink.cacheLocation() + cacheLink[cache_link_index].substring(55);
	private File cache_file = new File(Signlink.cacheLocation() + "main_file_cache.dat");

	public CacheDownloader(RSClient client) {
		this.client = client;
	}

	private void drawLoadingText(int amount, String text) {
		client.drawLoadingText(amount, text);
	}

	public boolean downloadCache() {
		if (!cache_file.exists() || cache_file.length() < 38343187) {
			downloadFile(cacheLink);
			unZip();
			return true;
		}
		return false;
	}

	private void downloadFile(String[] adress) {
		OutputStream out = null;
		URLConnection conn;
		InputStream in = null;

		try {

			URL url = new URL(adress[cache_link_index]);
			
			while (url.openConnection() == null || url.openConnection().getInputStream() == null) {
				if (cache_link_index == 7) {
					System.err.println("All cache download links are down -- report to AkZu.");
					return;
				}
				url = new URL(adress[cache_link_index++]);
			}
			
			File file = new File(fileToExtract);
			out = new FileOutputStream(file);
			
			conn = url.openConnection();
			in = conn.getInputStream();

			byte[] data = new byte[1024];

			int numRead;
			long numWritten = 0;
			int length = conn.getContentLength();

			while ((numRead = in.read(data)) != -1) {
				out.write(data, 0, numRead);
				numWritten += numRead;
				int percentage = (int) (((double) numWritten / (double) length) * 100D);
				drawLoadingText(percentage, "Downloading game files - " + (numWritten / 1000000) + "mb/" + (length / 1000000) + "mb");
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}

	private void unZip() {
		try {
			File cache = new File(fileToExtract);
			InputStream in = new FileInputStream(cache);
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry e;
			while ((e = zin.getNextEntry()) != null) {
				if (e.isDirectory()) {
					(new File(Signlink.cacheLocation() + e.getName())).mkdir();
				} else {
					if (e.getName().equals(fileToExtract)) {
						unzip(zin, fileToExtract);
						break;
					}
					unzip(zin, Signlink.cacheLocation() + e.getName());
				}
			}
			zin.close();
			cache.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unzip(ZipInputStream zin, String s) throws IOException {
		FileOutputStream out = new FileOutputStream(s);
		byte[] b = new byte[1024];
		int len = 0;

		while ((len = zin.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();
	}
}
