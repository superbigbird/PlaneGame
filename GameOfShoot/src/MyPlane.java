import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
public class MyPlane {
	 /*我机存活状态*/
	   public static final int MyPlane_ALIVE_STATE = 0;  
	    /*我机死亡状态*/
	   public static final int MyPlane_DEATH_STATE = 1;    	 
	    /* 我机的XY坐标 */
	    public int MyPlane_posX = 0;
	    public int MyPlane_posY = 0;     
	    /*我机状态*/
	    public  int mAnimState = MyPlane_ALIVE_STATE;  //我机最初为存活状态
	    private Image enemyExplorePic[] = new Image[6]; // 我机爆炸图片数组
	    private Image MyPlanePic[]=new Image[6];    //我方飞机的6帧图片
	    /* 当前帧的ID */
	   // public int mPlayID = 0;
	    
	    private long MyPlaneBomnedLastTime=0L;
	    
	    private Image MyPlaneBombedLater=MyPlanePic[0];
	    
	    private  boolean flag1=false;            //当我机爆炸后，复活时闪烁次数的标志
	    private  static int flag2=0;
	    	    
	    public MyPlane() {
	    	for (int i = 0; i < 6; i++) {
	    		enemyExplorePic[i] = Toolkit.getDefaultToolkit().getImage(
	    					"images\\bomb_enemy_" + i + ".png");
	    		MyPlanePic[i]=Toolkit.getDefaultToolkit().getImage("images\\plan_" + i + ".png");
	    	}
	    	MyPlaneBomnedLastTime=System.currentTimeMillis();
	    }
	    
	    /*初始化坐标*/
	    public void init(int x, int y) {
	    	MyPlane_posX = x;
	    	MyPlane_posY = y;
	    }
	    
	    public void DrawMyPlane(Graphics g,JPanel i,int id1,int id2,int lifeValue)
		{
		    //当我机状态为死亡并且死亡动画播放完毕 不在绘制敌机
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
		    		//System.out.println("flag2自加了");       用来做检测用
		    	}
		  }
	      if(flag2==8){
	    	  ThreadWindow.AddMyPlaneLifeValue(90);
	    	  mAnimState = MyPlane_ALIVE_STATE;
	    	  flag1=false;
	    	  flag2=0;
	    	  //System.out.println("血液重新恢复");             用来做检测用
	      }
		  if(mAnimState == MyPlane_ALIVE_STATE || lifeValue>0){    
			    g.drawImage(MyPlanePic[id2],MyPlane_posX,MyPlane_posY,(ImageObserver)i);
			    //return;
		  }
       }
}
