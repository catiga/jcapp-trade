package com.jeancoder.trade.ready.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;


class StreamUtil {
	private static final int DEFAULT_BUFFER_SIZE = 8192;

	public static void iout(InputStream ins, OutputStream out) throws IOException {
		iout(ins, out, -1);
	}

	public static void iout(InputStream ins, OutputStream out, int bufferSize) throws IOException {
		if (bufferSize == -1) {
			bufferSize = DEFAULT_BUFFER_SIZE;
		}

		byte[] buffer = new byte[bufferSize];
		int amount;

		while ((amount = ins.read(buffer)) >= 0) {
			out.write(buffer, 0, amount);
		}
	}

	public static void iout(Reader ins, Writer out) throws IOException {
		iout(ins, out, -1);
	}

	public static void iout(Reader ins, Writer out, int bufferSize) throws IOException {
		if (bufferSize == -1) {
			bufferSize = DEFAULT_BUFFER_SIZE >> 1;
		}

		char[] buffer = new char[bufferSize];
		int amount;

		while ((amount = ins.read(buffer)) >= 0) {
			out.write(buffer, 0, amount);
		}
	}

	public static OutputStream synchronizedOutputStream(OutputStream out) {
		return new SynchronizedOutputStream(out);
	}

	public static OutputStream synchronizedOutputStream(OutputStream out, Object lock) {
		return new SynchronizedOutputStream(out, lock);
	}

	public static String readText(InputStream ins) throws IOException {
		return readText(ins, null, -1);
	}

	public static String readText(InputStream ins, String encoding) throws IOException {
		return readText(ins, encoding, -1);
	}

	public static String readText(InputStream ins, String encoding, int bufferSize)
	throws IOException {
		Reader reader = (encoding == null) ? new InputStreamReader(ins) : new InputStreamReader(ins,
		encoding);

		return readText(reader, bufferSize);
	}

	public static String readText(Reader reader) throws IOException {
		return readText(reader, -1);
	}

	public static String readText(Reader reader, int bufferSize) throws IOException {
		StringWriter writer = new StringWriter();

		iout(reader, writer, bufferSize);
		return writer.toString();
	}
}