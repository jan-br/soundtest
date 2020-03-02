package net.labymod.url.memory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This has to be named like this. Stupid java conventions..
 */
public class Handler extends URLStreamHandler {

  private Map<URL, URLConnection> connectionMap = new ConcurrentHashMap<>();

  protected URLConnection openConnection(URL u) throws IOException {
    if (!connectionMap.containsKey(u)) {
      connectionMap.put(u, new MemoryURLConnection(u));
      System.out.println("New");
    }
    return connectionMap.get(u);
  }
}
