import java.io.*;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

public final class ResourceProvider implements Runnable {

	private boolean crcMatches(int j, byte abyte0[]) {
		if (abyte0 == null || abyte0.length < 2)
			return false;
		int k = abyte0.length - 2;
		crc32.reset();
		crc32.update(abyte0, 0, k);
		int i1 = (int) crc32.getValue();
		return i1 == j;
	}

	private void readData() {
		try {
			int j = inputStream.available();
			if (expectedSize == 0 && j >= 6) {
				waiting = true;
				for (int k = 0; k < 6; k += inputStream.read(ioBuffer, k, 6 - k))
					;
				int l = ioBuffer[0] & 0xff;
				int j1 = ((ioBuffer[1] & 0xff) << 8) + (ioBuffer[2] & 0xff);
				int l1 = ((ioBuffer[3] & 0xff) << 8) + (ioBuffer[4] & 0xff);
				int i2 = ioBuffer[5] & 0xff;
				current = null;
				for (Resource onDemandData = (Resource) requested.reverseGetFirst(); onDemandData != null; onDemandData = (Resource) requested
						.reverseGetNext()) {
					if (onDemandData.dataType == l && onDemandData.ID == j1)
						current = onDemandData;
					if (current != null)
						onDemandData.loopCycle = 0;
				}
				if (current != null) {
					loopCycle = 0;
					if (l1 == 0) {
						System.err.println("Rej: " + l + "," + j1);
						current.buffer = null;
						if (current.incomplete)
							synchronized (aClass19_1358) {
								aClass19_1358.insertHead(current);
							}
						else
							current.unlink();
						current = null;
					} else {
						if (current.buffer == null && i2 == 0)
							current.buffer = new byte[l1];
						if (current.buffer == null && i2 != 0)
							throw new IOException("missing start of file");
					}
				}
				completedSize = i2 * 500;
				expectedSize = 500;
				if (expectedSize > l1 - i2 * 500)
					expectedSize = l1 - i2 * 500;
			}
			if (expectedSize > 0 && j >= expectedSize) {
				waiting = true;
				byte abyte0[] = ioBuffer;
				int i1 = 0;
				if (current != null) {
					abyte0 = current.buffer;
					i1 = completedSize;
				}
				for (int k1 = 0; k1 < expectedSize; k1 += inputStream.read(abyte0, k1 + i1, expectedSize - k1))
					;
				if (expectedSize + completedSize >= abyte0.length && current != null) {
					if (clientInstance.decompressors[0] != null)
						clientInstance.decompressors[current.dataType + 1].method234(abyte0.length, abyte0, current.ID);
					if (!current.incomplete && current.dataType == 3) {
						current.incomplete = true;
						current.dataType = 93;
					}
					if (current.incomplete)
						synchronized (aClass19_1358) {
							aClass19_1358.insertHead(current);
						}
					else
						current.unlink();
				}
				expectedSize = 0;
			}
		} catch (IOException ioexception) {
			try {
				socket.close();
			} catch (Exception _ex) {
			}
			socket = null;
			inputStream = null;
			outputStream = null;
			expectedSize = 0;
		}
	}

	/**
	 * Grabs the checksum of a file from the cache.
	 * 
	 * @param type
	 *            The type of file (0 = model, 1 = anim, 2 = midi, 3 = map).
	 * @param id
	 *            The id of the file.
	 * @return
	 */
	public int getChecksum(int type, int id) {
		int crc = -1;
		byte[] data = clientInstance.decompressors[type + 1].decompress(id);
		if (data != null) {
			int length = data.length - 2;
			crc32.reset();
			crc32.update(data, 0, length);
			crc = (int) crc32.getValue();
		}
		return crc;
	}

	/**
	 * Writes the checksum list for the specified archive type and length.
	 * 
	 * @param type
	 *            The type of archive (0 = model, 1 = anim, 2 = midi, 3 = map).
	 * @param length
	 *            The number of files in the archive.
	 */
	public void writeChecksumList(int type) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(Signlink.cacheLocation() + type + "_crc.dat"));
			for (int index = 0; index < clientInstance.decompressors[type + 1].getFileCount(); index++) {
				out.writeInt(getChecksum(type, index));
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(CacheArchive streamLoader, RSClient client1) {
		String as1[] = { "model_crc", "anim_crc", "midi_crc", "map_crc" };
		for (int k = 0; k < 4; k++) {
			byte abyte1[] = streamLoader.getDataForName(as1[k]);
			int i1 = abyte1.length / 4;
			RSBuffer stream_1 = new RSBuffer(abyte1);
			crcs[k] = new int[i1];
			for (int l1 = 0; l1 < i1; l1++)
				crcs[k][l1] = stream_1.readInt();
		}
		byte abyte2[] = streamLoader.getDataForName("map_index");
		// Reading custom map index to test it
		// FileOperations.ReadFile(signlink.cacheLocation() + "map_index.dat");
		RSBuffer stream2 = new RSBuffer(abyte2);
		int j1 = abyte2.length / 7;
		// int maxFiles = 1397;
		// region
		mapIndices1 = new int[j1];
		// floor
		mapIndices2 = new int[j1];
		// object
		mapIndices3 = new int[j1];
		// members
		mapIndices4 = new int[j1];
		for (int i2 = 0; i2 < j1; i2++) {
			mapIndices1[i2] = stream2.readUShort();
			mapIndices2[i2] = stream2.readUShort();
			mapIndices3[i2] = stream2.readUShort();
			mapIndices4[i2] = stream2.readUByte();
		}
		/*
		 * try { BufferedWriter bw = new BufferedWriter(new
		 * FileWriter(signlink.cacheLocation() + "map_index.txt")); for (int i2
		 * = 0; i2 < j1; i2++) {
		 * bw.write("[REG]"+mapIndices1[i2]+"[FLO]"+mapIndices2
		 * [i2]+"[OBJ]"+mapIndices3[i2]+ "[MEM]"+mapIndices4[i2]); bw.newLine();
		 * } bw.flush(); } catch (IOException e) { e.printStackTrace(); }
		 */
		clientInstance = client1;
		running = true;
		clientInstance.startRunnable(this, 6);
	}

	public int getNodeCount() {
		synchronized (nodeSubList) {
			return nodeSubList.getNodeCount();
		}
	}

	private void closeRequest(Resource onDemandData) {
		try {
			if (socket == null) {
				long l = System.currentTimeMillis();
				if (l - openSocketTime < 4000L)
					return;
				openSocketTime = l;
				socket = clientInstance.openSocket(43593);
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				outputStream.write(15);
				for (int j = 0; j < 8; j++)
					inputStream.read();
				loopCycle = 0;
			}
			ioBuffer[0] = (byte) onDemandData.dataType;
			ioBuffer[1] = (byte) (onDemandData.ID >> 8);
			ioBuffer[2] = (byte) onDemandData.ID;
			// if (onDemandData.incomplete)
			ioBuffer[3] = 2;
			// else if (!clientInstance.loggedIn)
			// ioBuffer[3] = 1;
			// else
			// ioBuffer[3] = 0;
			outputStream.write(ioBuffer, 0, 4);
			anInt1349 = -10000;
			return;
		} catch (IOException ioexception) {
		}
		try {
			socket.close();
		} catch (Exception _ex) {
		}
		socket = null;
		inputStream = null;
		outputStream = null;
		expectedSize = 0;
		anInt1349++;
	}

	public void method558(int i, int j) {
		if (j == -1 || i == -1)
			return;
		synchronized (nodeSubList) {
			for (Resource onDemandData = (Resource) nodeSubList.reverseGetFirst(); onDemandData != null; onDemandData = (Resource) nodeSubList
					.reverseGetNext())
				if (onDemandData.dataType == i && onDemandData.ID == j)
					return;
			Resource onDemandData_1 = new Resource();
			onDemandData_1.dataType = i;
			onDemandData_1.ID = j;
			onDemandData_1.incomplete = true;
			synchronized (aClass19_1370) {
				aClass19_1370.insertHead(onDemandData_1);
			}
			nodeSubList.insertHead(onDemandData_1);
		}
	}

	public void run() {
		try {
			Thread.currentThread().setName("ResourceProvider");
			while (running) {
				onDemandCycle++;
				int i = 20;
				if (anInt1332 == 0 && clientInstance.decompressors[0] != null)
					i = 50;
				try {
					Thread.sleep(i);
				} catch (Exception _ex) {
				}
				waiting = true;
				for (int j = 0; j < 100; j++) {
					if (!waiting)
						break;
					waiting = false;
					checkReceived();
					handleFailed();
					if (uncompletedCount == 0 && j >= 5)
						break;
					method568();
					if (inputStream != null)
						readData();
				}
				boolean flag = false;
				for (Resource onDemandData = (Resource) requested.reverseGetFirst(); onDemandData != null; onDemandData = (Resource) requested
						.reverseGetNext())
					if (onDemandData.incomplete) {
						flag = true;
						onDemandData.loopCycle++;
						if (onDemandData.loopCycle > 50) {
							onDemandData.loopCycle = 0;
							closeRequest(onDemandData);
						}
					}
				if (!flag) {
					for (Resource onDemandData_1 = (Resource) requested.reverseGetFirst(); onDemandData_1 != null; onDemandData_1 = (Resource) requested
							.reverseGetNext()) {
						flag = true;
						onDemandData_1.loopCycle++;
						if (onDemandData_1.loopCycle > 50) {
							onDemandData_1.loopCycle = 0;
							closeRequest(onDemandData_1);
						}
					}
				}
				if (flag) {
					loopCycle++;
					if (loopCycle > 750) {
						try {
							socket.close();
						} catch (Exception _ex) {
						}
						socket = null;
						inputStream = null;
						outputStream = null;
						expectedSize = 0;
					}
				} else {
					loopCycle = 0;
				}
			}
		} catch (Exception exception) {
			System.err.println("od_ex " + exception.getMessage());
		}
	}

	public void method560(int i, int j) {
		if (clientInstance.decompressors[0] == null)
			return;
		if (anInt1332 == 0)
			return;
		Resource onDemandData = new Resource();
		onDemandData.dataType = j;
		onDemandData.ID = i;
		onDemandData.incomplete = false;
		synchronized (aClass19_1344) {
			aClass19_1344.insertHead(onDemandData);
		}
	}

	public Resource getNextNode() {
		Resource onDemandData;
		synchronized (aClass19_1358) {
			onDemandData = (Resource) aClass19_1358.popHead();
		}
		if (onDemandData == null)
			return null;
		synchronized (nodeSubList) {
			onDemandData.unlinkSub();
		}
		if (onDemandData.buffer == null)
			return onDemandData;
		int i = 0;
		try {
			GZIPInputStream gzipinputstream = new GZIPInputStream(new ByteArrayInputStream(onDemandData.buffer));
			do {
				if (i == gzipInputBuffer.length)
					throw new RuntimeException("buffer overflow!");
				int k = gzipinputstream.read(gzipInputBuffer, i, gzipInputBuffer.length - i);
				if (k == -1)
					break;
				i += k;
			} while (true);
		} catch (IOException _ex) {
			throw new RuntimeException("error unzipping");
		}
		onDemandData.buffer = new byte[i];
		System.arraycopy(gzipInputBuffer, 0, onDemandData.buffer, 0, i);
		return onDemandData;
	}

	public int method562(int i, int k, int l) {
		int i1 = (l << 8) + k;
		for (int j1 = 0; j1 < mapIndices1.length; j1++)
			if (mapIndices1[j1] == i1)
				if (i == 0)
					return mapIndices2[j1];
				else
					return mapIndices3[j1];
		return -1;
	}

	/*
	 * public void method548(int i) { method558(0, i); }
	 */

	public void method563(byte byte0, int i, int j) {
		if (clientInstance.decompressors[0] == null)
			return;
		byte abyte0[] = clientInstance.decompressors[i + 1].decompress(j);
		if (crcMatches(crcs[i][j], abyte0))
			return;
		if (byte0 > anInt1332)
			anInt1332 = byte0;
	}

	public boolean method564(int i) {
		for (int k = 0; k < mapIndices1.length; k++)
			if (mapIndices3[k] == i)
				return true;
		return false;
	}

	private void handleFailed() {
		uncompletedCount = 0;
		completedCount = 0;
		for (Resource onDemandData = (Resource) requested.reverseGetFirst(); onDemandData != null; onDemandData = (Resource) requested
				.reverseGetNext())
			if (onDemandData.incomplete)
				uncompletedCount++;
			else
				completedCount++;
		while (uncompletedCount < 10) {
			Resource onDemandData_1 = (Resource) aClass19_1368.popHead();
			if (onDemandData_1 == null)
				break;
			requested.insertHead(onDemandData_1);
			uncompletedCount++;
			closeRequest(onDemandData_1);
			waiting = true;
		}
	}

	public void method566() {
		synchronized (aClass19_1344) {
			aClass19_1344.removeAll();
		}
	}

	private void checkReceived() {
		Resource onDemandData;
		synchronized (aClass19_1370) {
			onDemandData = (Resource) aClass19_1370.popHead();
		}
		while (onDemandData != null) {
			waiting = true;
			byte abyte0[] = null;
			if (clientInstance.decompressors[0] != null)
				abyte0 = clientInstance.decompressors[onDemandData.dataType + 1].decompress(onDemandData.ID);
			if (!crcMatches(crcs[onDemandData.dataType][onDemandData.ID], abyte0))
				abyte0 = null;
			synchronized (aClass19_1370) {
				if (abyte0 == null) {
					aClass19_1368.insertHead(onDemandData);
				} else {
					onDemandData.buffer = abyte0;
					synchronized (aClass19_1358) {
						aClass19_1358.insertHead(onDemandData);
					}
				}
				onDemandData = (Resource) aClass19_1370.popHead();
			}
		}
	}

	private void method568() {
		while (uncompletedCount == 0 && completedCount < 10) {
			if (anInt1332 == 0)
				break;
			Resource onDemandData;
			synchronized (aClass19_1344) {
				onDemandData = (Resource) aClass19_1344.popHead();
			}
			while (onDemandData != null) {
				synchronized (aClass19_1344) {
					onDemandData = (Resource) aClass19_1344.popHead();
				}
			}
			anInt1332--;
		}
	}

	public ResourceProvider() {
		requested = new Deque();
		crc32 = new CRC32();
		ioBuffer = new byte[500];
		aClass19_1344 = new Deque();
		running = true;
		waiting = false;
		aClass19_1358 = new Deque();
		gzipInputBuffer = new byte[0x71868];
		nodeSubList = new NodeSubList();
		crcs = new int[4][];
		aClass19_1368 = new Deque();
		aClass19_1370 = new Deque();
	}

	private final Deque requested;
	private int anInt1332;
	private long openSocketTime;
	public int[] mapIndices3;
	private final CRC32 crc32;
	private final byte[] ioBuffer;
	public int onDemandCycle;
	private RSClient clientInstance;
	private final Deque aClass19_1344;
	private int completedSize;
	private int expectedSize;
	public int anInt1349;
	private int[] mapIndices2;
	private boolean running;
	private OutputStream outputStream;
	private int[] mapIndices4;
	private boolean waiting;
	private final Deque aClass19_1358;
	private final byte[] gzipInputBuffer;
	private final NodeSubList nodeSubList;
	private InputStream inputStream;
	private Socket socket;
	private final int[][] crcs;
	private int uncompletedCount;
	private int completedCount;
	private final Deque aClass19_1368;
	private Resource current;
	private final Deque aClass19_1370;
	private int[] mapIndices1;
	private int loopCycle;
}
