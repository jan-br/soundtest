package net.labymod.url.memory;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class MemoryURLStreamHandlerFactory implements URLStreamHandlerFactory {

  static {
    String property = System.getProperty("java.protocol.handler.pkgs");
    System.setProperty(
        "java.protocol.handler.pkgs",
        (property == null || property.isEmpty() ? "" : "|") + "net.labymod.url");
    System.out.println(System.getProperty("java.protocol.handler.pkgs"));
  }

  public URLStreamHandler createURLStreamHandler(String protocol) {
    if (protocol.equals("memory")) {
      return new Handler();
    }
    return null;
  }
}
