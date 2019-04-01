import javax.sound.sampled.*;
import java.io.*;

public class Sound{
	File file;
	AudioInputStream stream;
	AudioFormat format;
	DataLine.Info info;
	Clip clip;
	Sound(){
	}
	public void loadSound(String fileName) throws UnsupportedAudioFileException, IOException{
		file=new File(fileName);
		stream=AudioSystem.getAudioInputStream(file);
		format=stream.getFormat();
	}
	public void playSound() throws LineUnavailableException, IOException{
		info=new DataLine.Info(Clip.class, format);
		clip=(Clip)AudioSystem.getLine(info);
		clip.open(stream);
		clip.start();
	}
}
