import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**�л���**/
public class EnemyPlane {
   public static final int ENEMY_ALIVE_STATE = 0;   //�л����״̬
   public static final int ENEMY_DEATH_STATE = 1;     //�л�����״̬
    static final int ENEMY_STEP_Y = 3;    //�л����ߵ�Y���ٶ� 
    /*�л���XY����*/
    public int m_posX = 0;
    public int m_posY = 0;  
    public int mAnimState = ENEMY_ALIVE_STATE;  //�л����Ϊ���״̬
    private Image enemyExplorePic[] = new Image[6]; // �л���ըͼƬ����
    /*��ǰ֡��ID*/
    public int mPlayID = 0;
    
    public EnemyPlane() {                                  //������ȡͼƬ
    	for (int i = 0; i < 6; i++) 
    		enemyExplorePic[i] = Toolkit.getDefaultToolkit().getImage(
    					"images\\bomb_enemy_" + i + ".png");
    }
    
    /*��ʼ���з��ɻ�������*/
    public void init(int x, int y) {
	m_posX = x;
	m_posY = y;
	mAnimState = ENEMY_ALIVE_STATE;
	mPlayID = 0;
    }
    
    /*���Ƶл�����*/
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
	    //���л�״̬Ϊ����������������������� ���ڻ��Ƶл�
	    if(mAnimState == ENEMY_DEATH_STATE && mPlayID<6) {
	    	g.drawImage(enemyExplorePic[mPlayID],m_posX,m_posY,(ImageObserver)i);
	    	mPlayID++;
	    	return;        //����ȥ���������˳�����
	    }
	    //���л�״̬Ϊ���״̬
    	Image pic = Toolkit.getDefaultToolkit().getImage("images/e1_0.png");
		g.drawImage(pic,m_posX,m_posY,(ImageObserver)i);					
	}
    
    /*���µз��ɻ���״̬*/
    public void UpdateEnemy() {
	    m_posY += ENEMY_STEP_Y;    //�л�ÿ���ƶ��ľ���
    }
}
