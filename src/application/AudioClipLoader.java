package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javafx.scene.media.AudioClip;

public class AudioClipLoader {
	
	public static AudioClip loadAudioClipFromInputStream(InputStream inputStream) {
        File tempFile = null;

        try {
            // Créer un fichier temporaire
            tempFile = File.createTempFile("tempAudio", ".wav");
            tempFile.deleteOnExit();

            // Lire l'InputStream et écrire dans le fichier temporaire
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            // Charger l'AudioClip à partir du fichier temporaire
            return new AudioClip(tempFile.toURI().toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
