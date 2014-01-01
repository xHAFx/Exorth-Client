import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public final class Signlink {

	public static final String cacheLocation() {
		File file = null;
//		String home = System.getProperty("user.home");
//		String separator = System.getProperty("file.separator");
//		String cacheName = ".rs2cache";
//		StringBuilder builder = new StringBuilder(home + separator + cacheName + separator);
		String cacheDir = "./cache/";
		file = new File(cacheDir);
		if (file.exists() || file.mkdir()) {
			return cacheDir;
		}
		return null;
	}

	public Signlink() {
		String s = cacheLocation();
		try {
			cache_dat = new RandomAccessFile(s + "main_file_cache.dat", "rw");
			for (int j = 0; j < 5; j++)
				cache_idx[j] = new RandomAccessFile(s + "main_file_cache.idx" + j, "rw");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static int getUID() {
		try {
			File file = new File(cacheLocation() + "uid.dat");
			if (!file.exists() || file.length() < 4L) {
				DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(cacheLocation() + "uid.dat"));
				dataoutputstream.writeInt((int) (Math.random() * 99999999D));
				dataoutputstream.close();
			}
		} catch (Exception _ex) {
		}
		try {
			DataInputStream datainputstream = new DataInputStream(new FileInputStream(cacheLocation() + "uid.dat"));
			int i = datainputstream.readInt();
			datainputstream.close();
			return i + 1;
		} catch (Exception _ex) {
			return 0;
		}
	}

	public static int uid;
	public static RandomAccessFile cache_dat = null;
	public static final RandomAccessFile[] cache_idx = new RandomAccessFile[5];
}