package net.labymod.soundtest;

import org.apache.commons.io.IOUtils;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SoundTest {

  public static void main(String[] args) throws SoundSystemException, IOException {
    SoundSystemConfig.addLibrary(LibraryJavaSound.class);
    SoundSystemConfig.setCodec("wav", CodecWav.class);
    SoundSystemConfig.setNumberStreamingChannels(4);
    SoundSystemConfig.setNumberNormalChannels(28);

    SoundSystem soundSystem = new SoundSystem();
    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000, 16, 1, 2, 48000, false);


    soundSystem.rawDataStream(audioFormat, true, "rawtest1", 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);
    soundSystem.rawDataStream(audioFormat, true, "rawtest2", 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, 0);

    byte[] data = IOUtils.toByteArray(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("audio/raw/the_chainsmokers_closer.raw")));
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0);

    soundSystem.feedRawAudioData("rawtest1", new byte[]{}); //This is necessary - whyever - if it was removed, the sound will only play for ~ 1 second.
    soundSystem.feedRawAudioData("rawtest1", data);
    soundSystem.play("rawtest1");

    executorService.schedule(() -> {
      System.out.println("Second song should start playing - but its not. It waits til rawtest1 has finished.");
      soundSystem.feedRawAudioData("rawtest2", new byte[]{}); //This is necessary - whyever - if it was removed, the sound will only play for ~ 1 second.
      soundSystem.feedRawAudioData("rawtest2", data);
      System.out.println("I dont know why, but I cannot reproduce the behavior of a blocking command queue..");
      soundSystem.play("rawtest2");
    }, 5, TimeUnit.SECONDS);

  }

}
