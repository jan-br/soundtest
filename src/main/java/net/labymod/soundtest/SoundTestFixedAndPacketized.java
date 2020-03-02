package net.labymod.soundtest;

import net.labymod.url.memory.Handler;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Purpose of this test is to split the whole pcm data into many packets, to simulate the behavior of my voicechat.
 * This is work in progress and does not function.. But you get the idea
 */
public class SoundTestFixedAndPacketized {

  public static void main(String[] args) throws IOException, SoundSystemException, NoSuchMethodException {
    InputStream data = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("audio/raw/the_chainsmokers_closer.raw"));

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0);
    SoundSystemConfig.addLibrary(LibraryJavaSound.class);
    SoundSystemConfig.setCodec("wav", CodecWav.class);
    SoundSystemConfig.setNumberStreamingChannels(4);
    SoundSystemConfig.setNumberNormalChannels(28);

    SoundSystem soundSystem = new SoundSystem();
    AtomicInteger i = new AtomicInteger();

    URL url = new URL(null, "memory:voicedata", new Handler());

    URLConnection urlConnection = url.openConnection();
    OutputStream outputStream = urlConnection.getOutputStream();

    System.out.println("Length " + data.available());
    executorService.scheduleWithFixedDelay(() -> {
      try {
        byte[] chunk = new byte[Math.min(data.available(), 48000)];

        data.read(chunk);
        outputStream.write(rawToWave(chunk, 48000, 1, 16));
        outputStream.flush();

        soundSystem.unloadSound("test.wav");
        soundSystem.quickPlay(true, url, "test.wav", false, 0, 0, 0, 0, 0);

      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }, 0, 500, TimeUnit.MILLISECONDS);

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
