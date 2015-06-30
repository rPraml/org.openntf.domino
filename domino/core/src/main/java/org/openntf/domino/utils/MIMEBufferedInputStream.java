package org.openntf.domino.utils;

import java.io.IOException;
import java.io.InputStream;

import org.openntf.domino.Stream;

public class MIMEBufferedInputStream extends InputStream {
	private static final int DEFAULT_BUFFER_SIZE = 16384;

	private static final boolean FORCE_READ = false;
	//		private static int instanceCount = 0;

	private Stream is;
	private byte[] buffer;
	private int length;
	private int buffered;
	private int bufferPos;

	public static InputStream get(final Stream source) {
		return source != null ? new MIMEBufferedInputStream(source) : null;
	}

	public MIMEBufferedInputStream(final Stream is, final int size) {
		this.is = is;
		this.buffer = new byte[size];
		this.length = is.getBytes();
	}

	public MIMEBufferedInputStream(final Stream is) {
		this(is, DEFAULT_BUFFER_SIZE);
	}

	public boolean isEOF() throws IOException {
		return getNextByteCount() == 0;
	}

	@Override
	public int read() throws IOException {
		if (bufferPos == buffered) {
			//we've read to the end of the buffer byte-by-byte.
			//time to refill...
			buffered = getNextByteCount();
			buffer = is.read(buffered);

			bufferPos = 0;
			if (buffered <= 0) {
				buffered = 0;
				return -1;
			}
		}

		return buffer[bufferPos++] & 0xFF;
	}

	@Override
	public int read(final byte[] array) throws IOException {
		return read(array, 0, array.length);
	}

	@Override
	public int read(final byte[] array, int off, int length) throws IOException {
		if (FORCE_READ) {
			int read = 0;
			while (read < length) {
				int r = _read(array, off, length);
				if (r < 0) {
					return read > 0 ? read : r;
				}
				off += r;
				length -= r;
				read += r;
			}
			return read;
		} else {
			return _read(array, off, length);
		}
	}

	private int getNextByteCount() {
		int remaining;
		int position = is.getPosition();
		if (position < 0) {
			return 0;
		}

		remaining = length - position;
		return (remaining < buffer.length) ? remaining : buffer.length;

	}

	private int _read(final byte[] array, final int off, final int length) throws IOException {
		int avail = buffered - bufferPos;
		if (avail == 0) {
			buffered = getNextByteCount();
			buffer = is.read(buffered);
			avail = buffered;
			bufferPos = 0;
			if (buffered <= 0) {
				buffered = 0;
				return -1;
			}
		}
		int toRead = length < avail ? length : avail;
		System.arraycopy(buffer, bufferPos, array, off, toRead);
		bufferPos += toRead;
		return toRead;
	}

	@Override
	public long skip(long n) throws IOException {
		if (FORCE_READ) {
			long skip = 0;
			while (skip < n) {
				long r = _skip(n);
				if (r < 0) {
					return skip > 0 ? skip : r;
				}
				n -= r;
				skip += r;
			}
			return skip;
		} else {
			return _skip(n);
		}
	}

	private long _skip(final long n) throws IOException {
		int avail = buffered - bufferPos;
		if (avail > 0) {
			if (n < avail) {
				bufferPos += n;
				return n;
			}
			bufferPos = buffered;
			return avail;
		}
		final int position = is.getPosition();
		if (position < 0) {
			throw new IOException("Unable to get Stream Position");
		}

		long newPos = position + n;
		if (newPos > length) {
			return 0;
		} else {
			int intPos = Integer.parseInt(Long.toString(newPos));
			is.setPosition(intPos);
			return n;
		}
	}

	@Override
	public int available() throws IOException {
		final int position = is.getPosition();
		if (position < 0) {
			throw new IOException("Unable to get Stream Position");
		}
		return length - position;
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public void mark(final int int0) {
	}

	@Override
	public void reset() throws IOException {
	}

	@Override
	public boolean markSupported() {
		return false;
	}
}