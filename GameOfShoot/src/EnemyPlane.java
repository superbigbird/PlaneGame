import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**敌机类**/
public class EnemyPlane {
   public static final int ENEMY_ALIVE_STATE = 0;   //敌机存活状态
   public static final int ENEMY_DEATH_STATE = 1;     //敌机死亡状态
    static final int ENEMY_STEP_Y = 3;    //敌机行走的Y轴速度 
    /*敌机的XY坐标*/
    public int m_posX = 0;
    public int m_posY = 0;  
    public int mAnimState = ENEMY_ALIVE_STATE;  //敌机最初为存活状态
    private Image enemyExplorePic[] = new Image[6]; // 敌机爆炸图片数组
    /*当前帧的ID*/
    public int mPlayID = 0;
    
    public EnemyPlane() {                                  //用来获取图片
    	for (int i = 0; i < 6; i++) 
    		enemyExplorePic[i] = Toolkit.getDefaultToolkit().getImage(
    					"images\\bomb_enemy_" + i + ".png");
    }
    
    /*初始化敌方飞机的坐标*/
    public void init(int x, int y) {
	m_posX = x;
	m_posY = y;
	mAnimState = ENEMY_ALIVE_STATE;
	mPlayID = 0;
    }
    
    /*绘制敌机动画*/
    public void DrawEnemy(Graphics g,JFrame i)
	{
    	Image pic;
		try {
			pic = ImageIO.read(new File("images/e1_0.png"));
			g.drawImage(pic,m_posX,m_posY,(ImageObserver)i);
		} catch (IOException e) {
			e.printStackTrace();
		}
					
	}
    public void DrawEnemy(Graphics g,JPanel i)
	{
	    //当敌机状态为死亡并且死亡动画播放完毕 不在绘制敌机
	    if(mAnimState == ENEMY_DEATH_STATE && mPlayID<6) {
	    	g.drawImage(enemyExplorePic[mPlayID],m_posX,m_posY,(ImageObserver)i);
	    	mPlayID++;
	    	return;        //不能去掉，用来退出函数
	    }
	    //当敌机状态为存活状态
    	Image pic = Toolkit.getDefaultToolkit().getImage("images/e1_0.png");
		g.drawImage(pic,m_posX,m_posY,(ImageObserver)i);					
	}
    
    /*更新敌方飞机的状态*/
    public void UpdateEnemy() {
	    m_posY += ENEMY_STEP_Y;    //敌机每次移动的距离
    }
}
