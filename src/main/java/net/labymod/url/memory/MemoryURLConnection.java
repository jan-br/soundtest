package net.labymod.url.memory;

import com.google.common.collect.Maps;
import net.labymod.util.ObjectHolder;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class MemoryURLConnection extends URLConnection {

  private static final Map<String, ObjectHolder<byte[]>> data = Maps.newConcurrentMap();
  private MemoryInputStream memoryInputStream;
  private MemoryOutputStream memoryOutputStream;

  /**
   * Constructs a URL connection to the specified URL. A connection to the object referenced by the
   * URL is not created.
   *
   * @param url the specified URL.
   */
  protected MemoryURLConnection(URL url) {
    super(url);
  }

  public void connect() {
    if (this.connected) return;
    if (!data.containsKey(this.getURL().getPath())) {
      data.put(this.getURL().getPath(), new ObjectHolder<>(new byte[]{}));
    }
    this.memoryInputStream = new MemoryInputStream(new ByteArrayInputStream(data.get(this.getURL().getPath()).value));
    this.memoryOutputStream = new MemoryOutputStream(this.getURL(), this.memoryInputStream);
    this.connected = true;
  }

  public MemoryInputStream getInputStream() {
    if (!this.connected) this.connect();
    return this.memoryInputStream;
  }

  public MemoryOutputStream getOutputStream() {
    if (!this.connected) this.connect();
    return this.memoryOutputStream;
  }

  protected static Map<String, ObjectHolder<byte[]>> getData() {
    return data;
  }
}
