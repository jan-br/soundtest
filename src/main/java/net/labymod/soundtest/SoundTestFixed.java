package net.labymod.soundtest;

import net.labymod.url.memory.Handler;
import org.apache.commons.io.IOUtils;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * I wrote a custom url protocol to be able to assign byte arrays via an URL and use normal sources.
 * I know this is not how it is supposed to be used, but I need options.
 * Sure it would be possible to write the data to little PCM files.. But this would kill every SSD.
 */
public class SoundTestFixed {

  public static void main(String[] args) throws IOException, SoundSystemException {
    URL url = new URL(null, "memory:testaudio", new Handler());
    URLConnection urlConnection = url.openConnection();
//    URL url = new URL("http://localhost/the_chainsmokers_closer.wav");

    OutputStream outputStream = urlConnection.getOutputStream();

    outputStream.write(rawToWave(IOUtils.toByteArray(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("audio/raw/the_chainsmokers_closer.raw"))), 48000, 1, 16));
    outputStream.flush();

    SoundSystemConfig.addLibrary(LibraryJavaSound.class);
    SoundSystemConfig.setCodec("wav", CodecWav.class);
    SoundSystemConfig.setNumberStreamingChannels(4);
    SoundSystemConfig.setNumberNormalChannels(28);

    SoundSystem soundSystem = new SoundSystem();
    soundSystem.quickPlay(true, url, "test.wav", false, 0, 0, 0, 0, 0);

//    soundSystem.newSource(
//            true, "test1", url, "test1.wav", false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
//
//    soundSystem.newSource(
//            true, "test2", url, "test2.wav", false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
//
//    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0);
//
//    soundSystem.play("test1");
//
//    executorService.schedule(() -> {
//      System.out.println("Start playing second song.");
//      soundSystem.play("test2");
//    }, 5, TimeUnit.SECONDS);

  }

  private static byte[] rawToWave(byte[] rawData, int sampleRate, int channels, int bitSize)
          throws IOException {

    ByteArrayOutputStream output = null;
    try {
      output = new ByteArrayOutputStream();
      // WAVE header
      // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
      writeString(output, "RIFF"); // chunk id
      writeInt(output, 36 + rawData.length); // chunk size
      writeString(output, "WAVE"); // format
      writeString(output, "fmt "); // subchunk 1 id
      writeInt(output, bitSize); // subchunk 1 size
      writeShort(output, (short) 1); // audio format (1 = PCM)
      writeShort(output, (short) channels); // number of channels
      writeInt(output, sampleRate); // sample rate
      writeInt(output, sampleRate * 2); // byte rate
      writeShort(output, (short) 2); // block align
      writeShort(output, (short) 16); // bits per sample
      writeString(output, "data"); // subchunk 2 id
      writeInt(output, rawData.length); // subchunk 2 size
      // Audio data (conversion big endian -> little endian)
      short[] shorts = new short[rawData.length / 2];
      ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
      ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
      for (short s : shorts) {
        bytes.putShort(s);
      }

      output.write(rawData);

      return output.toByteArray();
    } finally {
      if (output != null) {
        output.close();
      }
    }
  }

  private static void writeInt(final OutputStream output, final int value) throws IOException {
    output.write(value >> 0);
    output.write(value >> 8);
    output.write(value >> 16);
    output.write(value >> 24);
  }

  private static void writeShort(final OutputStream output, final short value) throws IOException {
    output.write(value >> 0);
    output.write(value >> 8);
  }

  private static void writeString(final OutputStream output, final String value)
          throws IOException {
    for (int i = 0; i < value.length(); i++) {
      output.write(value.charAt(i));
    }
  }

}
