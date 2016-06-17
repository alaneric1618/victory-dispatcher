import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public enum AudioPlayer {
  OPENER("opener.wav"), SELECT_HI1("select_hi.wav"), SELECT_HI2("select_hi.wav"), SELECT_LOW1(
      "select_low.wav"), SELECT_LOW2("select_low.wav"), FIRE("fire.wav"), EXPLOSION("explode.wav");

  private Clip clip;

  AudioPlayer(String soundFileName) {
    soundFileName = "/media/" + soundFileName;
    try {
      // File soundFile = new File(soundFileName);
      System.out.println(soundFileName);
      AudioInputStream ais =
          AudioSystem.getAudioInputStream(new BufferedInputStream(VD.class
              .getResourceAsStream(soundFileName)));
      clip = AudioSystem.getClip();
      clip.open(ais);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void play() {
    Clip c = clip;
    if (c.isRunning()) {
      c.stop();
      c.setFramePosition(0);
      c.start();
    } else {
      c.setFramePosition(0);
      c.start();
    }

  }

  public boolean isPlaying() {
    Clip c = clip;
    return c.isRunning();
  }

  public void stop() {
    Clip c = clip;
    c.stop();
  }

  public void loop(int start, int end) {
    Clip c = clip;
    c.setLoopPoints(start, end);
    c.loop(c.LOOP_CONTINUOUSLY);
  }

  static void stopAll() {
    AudioPlayer sounds[] = AudioPlayer.values();
    for (AudioPlayer sound : sounds) {
      sound.stop();
    }
  }

  static boolean isInitialized = false;

  static void init() {
    // Construct All Enums
    if (!isInitialized) {
      isInitialized = true;
      values();
    }
  }
}
