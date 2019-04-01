import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
public class MyPlane {
	 /*�һ����״̬*/
	   public static final int MyPlane_ALIVE_STATE = 0;  
	    /*�һ�����״̬*/
	   public static final int MyPlane_DEATH_STATE = 1;    	 
	    /* �һ���XY���� */
	    public int MyPlane_posX = 0;
	    public int MyPlane_posY = 0;     
	    /*�һ�״̬*/
	    public  int mAnimState = MyPlane_ALIVE_STATE;  //�һ����Ϊ���״̬
	    private Image enemyExplorePic[] = new Image[6]; // �һ���ըͼƬ����
	    private Image MyPlanePic[]=new Image[6];    //�ҷ��ɻ���6֡ͼƬ
	    /* ��ǰ֡��ID */
	   // public int mPlayID = 0;
	    
	    private long MyPlaneBomnedLastTime=0L;
	    
	    private Image MyPlaneBombedLater=MyPlanePic[0];
	    
	    private  boolean flag1=false;            //���һ���ը�󣬸���ʱ��˸�����ı�־
	    private  static int flag2=0;
	    	    
	    public MyPlane() {
	    	for (int i = 0; i < 6; i++) {
	    		enemyExplorePic[i] = Toolkit.getDefaultToolkit().getImage(
	    					"images\\bomb_enemy_" + i + ".png");
	    		MyPlanePic[i]=Toolkit.getDefaultToolkit().getImage("images\\plan_" + i + ".png");
	    	}
	    	MyPlaneBomnedLastTime=System.currentTimeMillis();
	    }
	    
	    /*��ʼ������*/
	    public void init(int x, int y) {
	    	MyPlane_posX = x;
	    	MyPlane_posY = y;
	    }
	    
	    public void DrawMyPlane(Graphics g,JPanel i,int id1,int id2,int lifeValue)
		{
		    //���һ�״̬Ϊ����������������������� ���ڻ��Ƶл�
	     long MyPlaneBombedNowTime=System.currentTimeMillis();
		 if(mAnimState == MyPlane_DEATH_STATE && lifeValue==0 && flag1==false) {
		    	g.drawImage(enemyExplorePic[id1],MyPlane_posX,MyPlane_posY,(ImageObserver)i);
		        if(id1==5){
		        	flag1=true;
			    }
		  }
	      if(flag1){
		    	if(MyPlaneBombedNowTime-MyPlaneBomnedLastTime>400){
		    		if(MyPlaneBombedLater!=null){
		    			MyPlaneBombedLater=null;
		    			g.drawImage(MyPlaneBombedLater,MyPlane_posX,MyPlane_posY,(ImageObserver)i);
		    		}
		    		if(MyPlaneBombedLater==null){
		    			MyPlaneBombedLater=MyPlanePic[0];
		    			g.drawImage(MyPlaneBombedLater,MyPlane_posX,MyPlane_posY,(ImageObserver)i);
		    		}
		    		flag2+=1;
			    	MyPlaneBomnedLastTime=MyPlaneBombedNowTime;
		    		//System.out.println("flag2�Լ���");       �����������
		    	}
		  }
	      if(flag2==8){
	    	  ThreadWindow.AddMyPlaneLifeValue(90);
	    	  mAnimState = MyPlane_ALIVE_STATE;
	    	  flag1=false;
	    	  flag2=0;
	    	  //System.out.println("ѪҺ���»ָ�");             �����������
	      }
		  if(mAnimState == MyPlane_ALIVE_STATE || lifeValue>0){    
			    g.drawImage(MyPlanePic[id2],MyPlane_posX,MyPlane_posY,(ImageObserver)i);
			    //return;
		  }
       }
}
