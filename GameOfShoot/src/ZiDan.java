import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

/**子弹类**/
public class ZiDan {   
    static final int ZiDan_STEP_Y = 15; //子弹的Y轴方向上每次的移动位移
    public int m_posX =-10;            //子弹的初始化坐标X
    public int m_posY =-10;    //子弹的初始化坐标Y
    boolean mFacus = true;                    //用来判断是否更新绘制子弹
    private Image pic[] = null;   // 子弹图片数组
    
    private Image goldCoinZiDanPic=null;          //金币子弹图片
    /*子弹当前帧的ID*/
    private int mPlayID = 0;
    
    public static boolean ZiDanType=false;             /*判断是发射月牙形子弹还是金币形子弹*/

    public ZiDan() {                                   /*获取相关的图片*/
        pic = new Image[4];
    	for (int i = 0; i < 4; i++) 
    			pic[i] = Toolkit.getDefaultToolkit().getImage(
    					"images\\bullet_" + i + ".png");
    	
    	goldCoinZiDanPic=Toolkit.getDefaultToolkit().getImage("images\\coin.png");
    }
    
    /*初始化子弹的坐标*/
    public void init(int x, int y) {
	m_posX = x;
	m_posY = y;
	mFacus = true;
    }
    
    /*用来绘制子弹，子弹类型有月牙形和金币形两种,根据条件来判断绘制哪一种子弹*/
    public void DrawZiDan(Graphics g,JPanel i)
	{  
    	if(ZiDanType==false){      /*绘制的是月牙形的子弹*/
    	    g.drawImage(pic[mPlayID++],m_posX,m_posY,(ImageObserver)i);
    	    if(mPlayID==4)mPlayID=0;}
    	
        if(ZiDanType)                    /*满足条件时,绘制的是金币子弹*/
    	    g.drawImage(goldCoinZiDanPic,m_posX,m_posY,(ImageObserver)i);
    }
    
    /*更新子弹的坐标点*/
    public void UpdateZiDan() {
	    if (mFacus && ZiDanType==false){
	        m_posY -= ZiDan_STEP_Y;                                
	  }
	    if(mFacus && ZiDanType){
	    	m_posY -= ZiDan_STEP_Y+5;   /*月牙形子弹和金币子弹每次移动的距离是不一样的*/
	    }
    }
}
