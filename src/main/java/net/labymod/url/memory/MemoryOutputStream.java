package net.labymod.url.memory;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class MemoryOutputStream extends ByteArrayOutputStream {

  private final URL url;
  private final MemoryInputStream memoryInputStream;

  protected MemoryOutputStream(URL url, MemoryInputStream memoryInputStream) {
    this.url = url;
    this.memoryInputStream = memoryInputStream;
  }

  public void flush() throws IOException {
    super.flush();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(IOUtils.toByteArray(memoryInputStream));
    byteArrayOutputStream.write(this.toByteArray());
    byte[] bytes = byteArrayOutputStream.toByteArray();
    MemoryURLConnection.getData().get(url.getPath()).value = bytes;
    this.memoryInputStream.update(new ByteArrayInputStream(bytes));
    this.buf = new byte[]{};
    this.count = 0;
  }
}
