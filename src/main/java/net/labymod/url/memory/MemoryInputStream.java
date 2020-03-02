package net.labymod.url.memory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MemoryInputStream extends FilterInputStream {

  private final Queue<Byte> data = new ConcurrentLinkedQueue<>();

  /**
   * Creates a <code>FilterInputStream</code>
   * by assigning the  argument <code>in</code>
   * to the field <code>this.in</code> so as
   * to remember it for later use.
   *
   * @param in the underlying input stream, or <code>null</code> if
   *           this instance is to be created without an underlying stream.
   */
  protected MemoryInputStream(InputStream in) {
    super(in);
  }


  public int read(byte[] b, int off, int len) throws IOException {
    for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
      if (element.getClassName().contains("Test"))
        return super.read(b, off, len);
    }
    int read = super.read(b, off, len);
    return read;
  }

  protected synchronized void update(InputStream in) {
    this.in = in;
  }


}