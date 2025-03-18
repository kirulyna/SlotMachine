import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BackgroundMusic {
    private Clip clip;
    private FloatControl volumeControl;

    //play music with loop
    public void play(String filePath) {
        try {
            stopIfPlaying();
            loadClip(filePath);

            //volume control
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(-10.0f); // Default volume
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop music
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    //play music once (no loop)
    public void playOnce(String filePath) {
        try {
            stopIfPlaying();
            loadClip(filePath);

            // Volume control
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(-10.0f); //default volume
            }

            clip.start(); //play once
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    //stop music
    public void stop() {
        stopIfPlaying();
    }

    //set volume
    public void setVolume(float decibels) {
        if (volumeControl != null) {
            volumeControl.setValue(decibels);
        }
    }

    //helper to stop and release the clip if it's playing
    private void stopIfPlaying() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
    }

    //helper to load the audio file into the clip
    private void loadClip(String filePath) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        File soundFile = new File(filePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
    }
}
