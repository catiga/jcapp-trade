package com.jeancoder.trade.ready.util

class SynchronizedOutputStream extends OutputStream {
	private OutputStream out;
	private Object       lock;

	SynchronizedOutputStream(OutputStream out) {
		this(out, out);
	}

	SynchronizedOutputStream(OutputStream out, Object lock) {
		this.out = out;
		this.lock = lock;
	}

	public void write(int datum) throws IOException {
		synchronized (lock) {
			out.write(datum);
		}
	}

	public void write(byte[] data) throws IOException {
		synchronized (lock) {
			out.write(data);
		}
	}

	public void write(byte[] data, int offset, int length) throws IOException {
		synchronized (lock) {
			out.write(data, offset, length);
		}
	}

	public void flush() throws IOException {
		synchronized (lock) {
			out.flush();
		}
	}

	public void close() throws IOException {
		synchronized (lock) {
			out.close();
		}
	}
}
