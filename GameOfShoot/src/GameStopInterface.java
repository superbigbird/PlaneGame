import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GameStopInterface implements ActionListener{
    private JButton jbtPlayAgain=null;
    private JButton jbtExit=null;
    private JLabel lblTxt=null;
    public static MainWindow mainwindow=null;       /**用来结束游戏窗口**/
    JFrame frame=new JFrame("界面");
    public GameStopInterface(){
    	frame.setLayout(null);
    	frame.setBounds(200, 150, 300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    	jbtPlayAgain=new JButton("重玩");
    	jbtExit=new JButton("退出");
    	lblTxt=new JLabel("你挂了,任务失败!");
    	lblTxt.setBounds(80, 50,100, 30);
    	jbtPlayAgain.setBounds(70, 100, 80, 30);
    	jbtExit.setBounds(170, 100, 80, 30);
    	
    	jbtPlayAgain.addActionListener(this);
    	jbtExit.addActionListener(this);
    	
    	frame.add(lblTxt);
    	frame.add(jbtPlayAgain);
    	frame.add(jbtExit);
    	
    	mainwindow.dispose();
    }
	public void actionPerformed(ActionEvent e) {
		String ac=e.getActionCommand();
		if(ac.equals("重玩")){
			try {
				frame.dispose();
				MainWindow main=new MainWindow();
				main.gameStopInterface(main);
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			}
			
		}
		if(ac.equals("退出")){
            System.exit(0);
		}
	}
}
