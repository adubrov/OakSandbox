package com.bluevinyl.player;

import davaguine.jeq.spi.EqualizerInputStream;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

public class JavaSoundSimpleAudioPlayer {
    /**
     * Plays audio from given file names.
     */
    public static void main(String[] args) {
        // Check for given sound file names.
        if (args.length < 1) {
            System.out.println("Play usage:");
            System.out.println("\tjava Play <sound file names>*");
            System.exit(0);
        }
//
//        Optional<Mixer.Info> mixInfo = findMixer("DubrovVinyl");
//        Mixer mixer = AudioSystem.getMixer(mixInfo.get());
//        Arrays.stream(mixer.getTargetLineInfo()).forEach(info -> {
//            System.out.println(info);
//        });

        // Process arguments.
        playAudioFile(args[0], findMixer(args[1]));

        // Must exit explicitly since audio creates non-daemon threads.
        System.exit(0);
    } // main

    protected static Optional<Mixer.Info> findMixer(String name) {
        return Arrays.stream(AudioSystem.getMixerInfo())
                .filter(mixInfo -> mixInfo.getName().contains(name))
                .findFirst();
    }

    /**
     * Play audio from the given file name.
     */
    public static void playAudioFile(String fileName, Optional<Mixer.Info> mixer) {
        try {
            AudioInputStream audioInputStream;
            try {
                URL url = new URL(fileName);
                audioInputStream = AudioSystem.getAudioInputStream(url);
            } catch (MalformedURLException e) {
                File soundFile = new File(fileName);
                audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            }

            // Create a stream from the given file.
            // Throws IOException or UnsupportedAudioFileException
            playAudioStream(audioInputStream, mixer);
        } catch (Exception e) {
            System.out.println("Problem with file " + fileName + ":");
            e.printStackTrace();
        }
    } // playAudioFile

    /**
     * Plays audio from the given audio input stream.
     */
    public static void playAudioStream(AudioInputStream audioInputStream, Optional<Mixer.Info> mixerInfo) {
        // Audio format provides information like sample rate, size, channels.
        AudioFormat audioFormat = audioInputStream.getFormat();

        // Convert compressed audio data to uncompressed PCM format.
        if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            AudioFormat newFormat = new AudioFormat(audioFormat.getSampleRate(),
                    audioFormat.getSampleSizeInBits(),
                    audioFormat.getChannels(),
                    true,
                    false);
            System.out.println("Converting audio format to " + newFormat);
            AudioInputStream newStream = AudioSystem.getAudioInputStream(newFormat, audioInputStream);
            audioFormat = newFormat;
            audioInputStream = newStream;
        }

        try {
            // Create a SourceDataLine for play back (throws LineUnavailableException).
            SourceDataLine dataLine = getDataLineFromMixer(audioFormat, mixerInfo);
            System.out.println("SourceDataLine class=" + dataLine.getClass());

            // The line acquires system resources (throws LineAvailableException).
            dataLine.open(audioFormat);

            // Adjust the volume on the output line.
            if (dataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volume = (FloatControl) dataLine.getControl(FloatControl.Type.MASTER_GAIN);
                volume.setValue((volume.getMaximum() - volume.getMinimum()) * 0.7f + volume.getMinimum());
            }

            // Allows the line to move data in and out to a port.
            dataLine.start();

            EqualizerInputStream eq = new EqualizerInputStream(audioInputStream, 15);
            eq.getControls().setBandValue(14, 0, -0.2f);
            eq.getControls().setBandValue(14, 1, -0.2f);
            eq.getControls().setBandValue(13, 0, -0.2f);
            eq.getControls().setBandValue(13, 1, -0.2f);
            eq.getControls().setBandValue(2, 0, 0.4f);
            eq.getControls().setBandValue(2, 1, 0.4f);
            eq.getControls().setBandValue(3, 0, 0.4f);
            eq.getControls().setBandValue(3, 1, 0.4f);

            // Create a buffer for moving data from the audio stream to the line.
            int bufferSize = (int) audioFormat.getSampleRate() * audioFormat.getFrameSize();
            byte[] buffer = new byte[bufferSize];

            // Move the data until done or there is an error.
            try {
                int bytesRead = 0;
                while (bytesRead >= 0) {
                    bytesRead = eq.read(buffer, 0, buffer.length);
                    if (bytesRead >= 0)
                        dataLine.write(buffer, 0, bytesRead);
                } // while
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Play.playAudioStream draining line.");
            // Continues data line I/O until its buffer is drained.
            dataLine.drain();

            System.out.println("Play.playAudioStream closing line.");
            // Closes the data line, freeing any resources such as the audio device.
            dataLine.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    } // playAudioStream

    private static SourceDataLine getDataLineFromMixer(AudioFormat audioFormat, Optional<Mixer.Info> mixerOpt) {
        return mixerOpt.map(mixer -> {
            try {
              return AudioSystem.getSourceDataLine(audioFormat, mixer);
            } catch (Exception ex) {
                System.out.println("Failed to get source data line due to error: '" + ex.getMessage() + "'");

                return getDefaultSource(audioFormat);
            }
        }).orElse(getDefaultSource(audioFormat));
    }

    private static SourceDataLine getDefaultSource(AudioFormat audioFormat) {
        DataLine.Info dataInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

        if (!AudioSystem.isLineSupported(dataInfo)) {
            System.out.println("Play.playAudioStream does not handle this type of audio on this system.");
            return null;
        }

        SourceDataLine result = null;
        try {
            result = (SourceDataLine) AudioSystem.getLine(dataInfo);
        } catch (Exception ex) {
            System.out.println("Failed to get source data line due to error: '" + ex.getMessage() + "'");
        }

        return result;
    }
}