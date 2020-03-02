package net.labymod.url.memory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * This has to be named like this. Stupid java conventions..
 */
public class Handler extends URLStreamHandler {


  protected URLConnection openConnection(URL u) throws IOException {
    return new MemoryURLConnection(u);
  }
}

