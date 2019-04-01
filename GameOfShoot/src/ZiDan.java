import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

/**�ӵ���**/
public class ZiDan {   
    static final int ZiDan_STEP_Y = 15; //�ӵ���Y�᷽����ÿ�ε��ƶ�λ��
    public int m_posX =-10;            //�ӵ��ĳ�ʼ������X
    public int m_posY =-10;    //�ӵ��ĳ�ʼ������Y
    boolean mFacus = true;                    //�����ж��Ƿ���»����ӵ�
    private Image pic[] = null;   // �ӵ�ͼƬ����
    
    private Image goldCoinZiDanPic=null;          //����ӵ�ͼƬ
    /*�ӵ���ǰ֡��ID*/
    private int mPlayID = 0;
    
    public static boolean ZiDanType=false;             /*�ж��Ƿ����������ӵ����ǽ�����ӵ�*/

    public ZiDan() {                                   /*��ȡ��ص�ͼƬ*/
        pic = new Image[4];
    	for (int i = 0; i < 4; i++) 
    			pic[i] = Toolkit.getDefaultToolkit().getImage(
    					"images\\bullet_" + i + ".png");
    	
    	goldCoinZiDanPic=Toolkit.getDefaultToolkit().getImage("images\\coin.png");
    }
    
    /*��ʼ���ӵ�������*/
    public void init(int x, int y) {
	m_posX = x;
	m_posY = y;
	mFacus = true;
    }
    
    /*���������ӵ����ӵ������������κͽ��������,�����������жϻ�����һ���ӵ�*/
    public void DrawZiDan(Graphics g,JPanel i)
	{  
    	if(ZiDanType==false){      /*���Ƶ��������ε��ӵ�*/
    	    g.drawImage(pic[mPlayID++],m_posX,m_posY,(ImageObserver)i);
    	    if(mPlayID==4)mPlayID=0;}
    	
        if(ZiDanType)                    /*��������ʱ,���Ƶ��ǽ���ӵ�*/
    	    g.drawImage(goldCoinZiDanPic,m_posX,m_posY,(ImageObserver)i);
    }
    
    /*�����ӵ��������*/
    public void UpdateZiDan() {
	    if (mFacus && ZiDanType==false){
	        m_posY -= ZiDan_STEP_Y;                                
	  }
	    if(mFacus && ZiDanType){
	    	m_posY -= ZiDan_STEP_Y+5;   /*�������ӵ��ͽ���ӵ�ÿ���ƶ��ľ����ǲ�һ����*/
	    }
    }
}
