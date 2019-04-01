import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

public class GoldCoin {
	private double x, y;
	private final int speed =3;
	private double degree;
	private int width = 16;
	private int height =16;
	private Image ImgCoin=null;
	public boolean GoldCoinIsOrNotAppearance=true;             /**�����жϽ���Ƿ���ʧ**/
	
	public GoldCoin(){
		ImgCoin=Toolkit.getDefaultToolkit().getImage("images\\coin.png");
		degree = Math.random()*Math.PI*2;
		x = 320/2;
		y = 480/2;
	}
	
	public void draw(Graphics g,JPanel i){
		if(GoldCoinIsOrNotAppearance){                                           /*����ñ�����ֵΪfalse�������ʧ*/
		  g.drawImage(ImgCoin, (int)x, (int)y, (ImageObserver)i);
		  x += speed*Math.cos(degree);
		  y += speed*Math.sin(degree);
		}
		if(x >320-width-6 || x < 0)
			degree = Math.PI-degree;
		if(y >480-height-30 || y < 4)
			degree = -degree;
		if(GoldCoinIsOrNotAppearance==false){                         /*��ֹ�ҷ��Ե��Ľ���������������Զ�����*/
			x=-20;
			y=-20;
		}
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
}
