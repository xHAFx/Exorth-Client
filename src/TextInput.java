final class TextInput {

	public static String method525(int i, RSBuffer stream) {
		int j = 0;
		for (int l = 0; l < i; l++) {
			int i1 = stream.readUByte();
			aCharArray631[j++] = validChars[i1];
		}
		int skip = 0;
		String modify = new String(aCharArray631, 0, j);
		for (int i1 = 0; i1 < modify.length(); i1++) {
			if (skip == 2) {
				skip = 0;
				modify = String.format("%s%s%s", modify.subSequence(0, i1), Character.toUpperCase(modify.charAt(i1)),
						modify.substring(i1 + 1));
				continue;
			}
			if (skip == 1) {
				skip = 0;
				if (Character.isUpperCase(modify.charAt(i1))) {
					continue;
				}
			}
			if (Character.isUpperCase(modify.charAt(i1))) {
				modify = String.format("%s%s%s", modify.subSequence(0, i1), Character.toLowerCase(modify.charAt(i1)),
						modify.substring(i1 + 1));
			}
			if (modify.charAt(i1) == (char) '.' || modify.charAt(i1) == (char) '?' || modify.charAt(i1) == (char) '!') {
				skip = 2;
				continue;
			}
			if (Character.isWhitespace(modify.charAt(i1)) || !Character.isLetter(modify.charAt(i1)))
				skip = 1;
			if (i1 == 0) {
				modify = String.format("%s%s", Character.toUpperCase(modify.charAt(0)), modify.substring(1));
			}
		}
		return modify;
	}

	/**
	 * 
	 * @param string
	 * @param stream
	 */
	public static void method526(String string, RSBuffer stream) {
		if (string.length() > 80)
			string = string.substring(0, 80);
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			int k = 0;
			for (int l = 0; l < validChars.length; l++) {
				if (c != validChars[l])
					continue;
				k = l;
				break;
			}
			stream.writeByte(k);
		}
	}

	public static String processText(String s) {
		stream.pointer = 0;
		method526(s, stream);
		int j = stream.pointer;
		stream.pointer = 0;
		String s1 = method525(j, stream);
		return s1;
	}

	private static final char[] aCharArray631 = new char[100];
	private static final RSBuffer stream = new RSBuffer(new byte[100]);
	private static char validChars[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g',
			'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', 228, 229, 246, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '?', '.', ',',
			':', ';', '(', ')', '-', '&', '*', '\'', '@', '#', '+', '=', '$', 163, '%', '"', '[', ']', '>', '<', '^', '/', '_', '|', 189,
			180, '`', 167, 8364, '}', 168, '~', 164, '{', 'E', 'T', 'A', 'O', 'I', 'H', 'N', 'S', 'R', 'D', 'L', 'U', 'M', 'W', 'C', 'Y',
			'F', 'G', 'P', 'B', 'V', 'K', 'X', 'J', 'Q', 'Z', 196, 197, 214 };
}
