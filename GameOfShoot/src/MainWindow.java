import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MainWindow extends JFrame {
	private Sound sound2=new Sound();
	private Image ImgSystemIcon=null;
	public MainWindow() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		// 获得我们自定义面板[地图面板]的实例
		ThreadWindow panel = new ThreadWindow();
		Container contentPane = getContentPane();
		contentPane.add(panel);
		ImgSystemIcon=Toolkit.getDefaultToolkit().getImage("images\\heart.png");
		
		setTitle("雷霆战机游戏");
		setSize(320,480);
	    setLocationRelativeTo(null);
		setIconImage();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		sound2.loadSound("musics\\雷霆曲.wav");
		sound2.playSound();
	}
	
	public void gameStopInterface(MainWindow xx){
		GameStopInterface.mainwindow=xx;
	}
	
	public void setIconImage(){                         //重载系统图标设置函数
		super.setIconImage(ImgSystemIcon);
	}
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		MainWindow main=new MainWindow();
		main.gameStopInterface(main);
	}
}
