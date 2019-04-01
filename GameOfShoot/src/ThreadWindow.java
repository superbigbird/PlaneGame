import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**线程类**/
public class ThreadWindow extends JPanel implements Runnable, KeyListener {
	/*屏幕的宽高,定义成全局变量方便于后期的修改*/
	private int flagCC=0;
	private int mScreenWidth = 320;
	private int mScreenHeight = 480;
	private static final int STATE_GAME = 0;	//游戏主菜单状态,用来做后期的功能
	private int mState = STATE_GAME;       //游戏状态
	private Image mBitMenuBG0 = null;  //做背景的第一张图片
	private Image mBitMenuBG1 = null;   //做背景的第二张图片
	/*因为两张图片的坐标会不停的变化,所以定义为全局变量*/
	private int mBitposY0 = 0;
	private int mBitposY1 = 0;
	final static int ZiDan_POOL_COUNT = 15;   //要初始化的子弹对象的数量,数量不能太少,定义为常量防止程序运行过程中被修改
	final static int PLAN_STEP = 4;      //按方向键时,飞机每次移动的距离
	final static int PLAN_TIME =420;   //没过420毫秒发射一颗子弹
	final static int ENEMY_POOL_COUNT = 5;       //要初始化的敌机对象的数量,定义为常量
	final static int ENEMY_POS_OFF = 65;   //敌人飞机偏移量
	private Thread mThread = null;     //游戏的主线程
	private boolean mIsRunning = false;     //线程循环是否结束的标志,用作暂停用，用来替换suspend/resume方法,因为这两个方法不是线程安全的
	public int mAirPosX =150;   //游戏刚进入时我方飞机在屏幕上显示的位置
	public int mAirPosY =400;
	/*敌机对象数组*/
	EnemyPlane mEnemy[] = null;
	/*子弹对象数组*/
	ZiDan mZidan[] = null;
	public int mSendId = 0;    //初始化发射子弹ID,用来判别每一次线程扫描时子弹该重绘哪一张图片
	public Long mSendTime = 0L;       //上一颗子弹发射的时间
	Image myPlanePic[]; //玩家飞机所有图片
	public int myPlaneID = -1;    //我方飞机当前帧号
	
	public int myPlaneBombID=-1;    /*我方飞机爆炸时，图片滚动编号*/
	public Long myPlaneBombedTime=0L;  /*我方飞机爆炸时的时间*/
    
	/*当同时按下space键和方向键时的条件*/
	private static boolean left=false;
	private static boolean right=false;
	private static boolean up=false;
	private static boolean down=false;
	private static boolean space=false;
	private static boolean enemybaozha=false;
	
	private int EnemyBombedNum=0;       /*被我放打中的敌机数量,就是屏幕左上角歼敌的统计数量*/
	private static int MyPlaneLifeValue=90;        //我方飞机的生命值
	
	GoldCoin[] goldCoin=null;                      /*构建金币对象数组*/
	private final int goldCoinNum=30;                  /*要构建的金币数量*/
	
	private int GoldCoinCount=0;                             /*我方飞机吃掉的金币数量*/
	private Image GoldCoinCountIcon=null;
	private Image[] ImgHeart=null;                  /*构造表明我方飞机生命次数的心形图标*/
	private static int heartNum=2;                  /*心形的数量，在本类的静态方法中要用到该变量，所以声明为静态的*/
	public Sound sound=new Sound();
	
	MyPlane myplane=null;     //构造一个我方飞机对象

	public ThreadWindow() {
		setFocusable(true);
		addKeyListener(this);
		init();
		setGameState(STATE_GAME);
		mIsRunning = true;
		mThread = new Thread(this);       // 实例线程
		mThread.start();
		setVisible(true);

	}

	protected void Draw(){
		switch (mState) {
		case STATE_GAME:
			renderBg();// 绘制游戏界面（包括背景、敌我飞机、子弹)
			updateBg(); // 更新游戏
			break;
		}
	}

	private void init() {
		/*游戏各种对象的初始化*/
		try {
			mBitMenuBG0 = Toolkit.getDefaultToolkit().getImage(              //获取图片
					"images\\map_0.png");

			mBitMenuBG1 = Toolkit.getDefaultToolkit().getImage(
					"images\\map_1.png");
			ImageIO.read(new File("images/map_1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mBitposY0 = 0;                   //第一张图片的位置
		mBitposY1 = -mScreenHeight;  //第二张图片在第一张图片的上方,他们之间是紧贴着的
		myplane=new MyPlane();   //创建我方飞机对象
		mEnemy = new EnemyPlane[ENEMY_POOL_COUNT];   /*创建敌人对象的数量*/

		for (int i = 0; i < ENEMY_POOL_COUNT; i++) {
			mEnemy[i] = new EnemyPlane();
			mEnemy[i].init(i * ENEMY_POS_OFF, i * ENEMY_POS_OFF-300);   //这样每架敌机的初始化位置都不一样，防止重叠
		}

		/*创建子弹类对象*/
		mZidan = new ZiDan[ZiDan_POOL_COUNT];
		for (int i = 0; i < ZiDan_POOL_COUNT; i++) {
			mZidan[i] = new ZiDan();
		}
		
		goldCoin=new GoldCoin[goldCoinNum];    /*初始化50个金币对象*/
		for(int i=0;i<goldCoinNum;i++){
			goldCoin[i]=new GoldCoin();
		}
		
		GoldCoinCountIcon=Toolkit.getDefaultToolkit().getImage("images\\coin.png");    /*窗体上显示获得的金币数量*/
		
		ImgHeart=new Image[3];           /*初始化心形对象*/
		for(int i=0;i<3;i++){
			ImgHeart[i]=Toolkit.getDefaultToolkit().getImage("images\\heart.png");	
		}
		
		mSendTime = System.currentTimeMillis();
		myPlaneBombedTime=System.currentTimeMillis();//初始化我放飞机爆炸时的时间
	}

	private void setGameState(int newState) {
		mState = newState;                                      //这些参数没必要传递,只是考虑到后期的更新修改用的
	}

	public void renderBg() {
		myPlaneID++;                      //我方飞机图片播放帧号
		myPlaneBombID++;
		if (myPlaneID == 6)
			myPlaneID = 0;
		if (myPlaneBombID == 6){
			myPlaneBombID = 0;
		    //myplane.mAnimState=myplane.MyPlane_ALIVE_STATE;
		}
		repaint();                   //重绘，这个方法实际是调用Update()方法的，而Update()又调用paint()，可以利用这点写双缓冲技术
	}

	public void paint(Graphics g) {                        //各种重绘是在这里进行的
		myplane.init(mAirPosX, mAirPosY);         //初始化我方飞机的坐标
		g.drawImage(mBitMenuBG0, 0, mBitposY0, this);             //画背景
		g.drawImage(mBitMenuBG1, 0, mBitposY1, this);
		myplane.DrawMyPlane(g, this,myPlaneBombID,myPlaneID,MyPlaneLifeValue);   /*绘制自己飞机动画*/
		
		/*绘制子弹动画*/
		for (int i = 0; i < ZiDan_POOL_COUNT; i++)
			mZidan[i].DrawZiDan(g, this);

		/*绘制敌方飞机的动画*/
		for (int i = 0; i < ENEMY_POOL_COUNT; i++)
			mEnemy[i].DrawEnemy(g, this);
		
		/**绘制金币动画,当歼敌50架后就会有一次吃金币的机会**/
		if(EnemyBombedNum>5){
		    for(int i=0;i<goldCoinNum;i++)
			    goldCoin[i].draw(g, this);
		    }
		
		g.setColor(Color.GREEN);
		g.drawString("生命值: ", 5, 20);
		g.setColor(new Color(233,56,56));
	    g.fillRect(50, 10, MyPlaneLifeValue, 10);
	    g.drawString("歼敌: "+EnemyBombedNum, 5, 40);
	    
	    for(int i=0,j=3;i<heartNum;i++,j+=27){                           /*用来画心形图标*/
	    	g.drawImage(ImgHeart[i], j, 425, this);
	    }
	    
	    g.drawImage(GoldCoinCountIcon, 260, 8, this);                    //这里不解释
	    g.drawString(GoldCoinCount+"", 290, 20);
	}

	private void updateBg(){
		mBitposY0 += 8;      //更新图片坐标，实现滚动效果
		mBitposY1 += 8;
		if (mBitposY0 == mScreenHeight) {
			mBitposY0 = -mScreenHeight;
		}
		if (mBitposY1 == mScreenHeight) {
			mBitposY1 = -mScreenHeight;
		}

		for (int i = 0; i < ZiDan_POOL_COUNT; i++) {    /*更新子弹动画,也就是让发出去的子弹动起来*/
			mZidan[i].UpdateZiDan();
		}
		
		for (int i = 0; i < ENEMY_POOL_COUNT; i++) {
			mEnemy[i].UpdateEnemy();                                //更新敌机动画，也就是让敌机动起来
			if (mEnemy[i].mAnimState == EnemyPlane.ENEMY_DEATH_STATE     /*敌机死亡 或者 敌机超过屏幕还未死亡重置坐标*/
					&& mEnemy[i].mPlayID == 6
					|| mEnemy[i].m_posY >= mScreenHeight) {
				mEnemy[i].init(SuiJiShu(0, ENEMY_POOL_COUNT) * ENEMY_POS_OFF,
						-75);
			}
			
			IsOrNotSendBuilet();
			LeftMove();
			RigthMove();
			UpMove();
			DownMove();
		}

		CollisionBuiletAndEnemy();          //子弹和敌机的碰撞
		CollisionMyPlaneAndEnemy();   //我方飞机和子弹的碰撞
		CollisionGoldCoinAndMyPlane();   //吃金币的碰撞
		CloseGameWindow();
	}
     
	public void SendBuilet(){                                   //子弹发射方法
		if (mSendId < ZiDan_POOL_COUNT) {
			long now = System.currentTimeMillis();
			if (now - mSendTime >= PLAN_TIME) {
				if(ZiDan.ZiDanType==false)
					mZidan[mSendId].init(mAirPosX-5, mAirPosY-40);
				else
					mZidan[mSendId].init(mAirPosX+8, mAirPosY-16);
				mSendTime = now;
				mSendId++;
			}
		} else {
			mSendId = 0;
		}
	}

	/*当space条件为true时发射子弹*/
	public void IsOrNotSendBuilet(){
		if(space){
			SendBuilet();
			}
	}
	
	public void CloseGameWindow(){                      //我方挂掉后出现的游戏窗口
		if(heartNum==0 && MyPlaneLifeValue==0){
			mIsRunning=false;
			MyPlaneLifeValue=90;
			heartNum=2;
		   GameStopInterface hh=new GameStopInterface();
		}
	}
	
	public void CollisionBuiletAndEnemy() {                          //这里用到的是矩形碰撞，碰撞很粗糙，精度不是很高
		double GoldCoinAndEnemyDistance=0;
		for (int i = 0; i < ZiDan_POOL_COUNT; i++) {
			for (int j = 0; j < ENEMY_POOL_COUNT; j++) {
				GoldCoinAndEnemyDistance=Math.sqrt((Math.pow(mZidan[i].m_posX-mEnemy[j].m_posX, 2))+(Math.pow(mZidan[i].m_posY-mEnemy[j].m_posY, 2)));
				if ((ZiDan.ZiDanType==false) && mZidan[i].m_posX >= mEnemy[j].m_posX
						&& mZidan[i].m_posX <= mEnemy[j].m_posX + 30
						&& mZidan[i].m_posY >= mEnemy[j].m_posY
						&& mZidan[i].m_posY <= mEnemy[j].m_posY + 30

				) {
					mEnemy[j].mAnimState = EnemyPlane.ENEMY_DEATH_STATE;
					enemybaozha=true;
					mZidan[i].m_posX=-200;           //当打到飞机时，子弹立马消失
					mZidan[i].m_posY=-200;
				}
				if(ZiDan.ZiDanType && GoldCoinAndEnemyDistance<=40){
					mEnemy[j].mAnimState = EnemyPlane.ENEMY_DEATH_STATE;
					enemybaozha=true;
				}
			}

		}
		if(enemybaozha==true)
			EnemyBombedNum++;
	}
	
	public void CollisionMyPlaneAndEnemy() {                        //矩形碰撞
		for (int i = 0; i < ENEMY_POOL_COUNT; i++) {
				if (myplane.MyPlane_posX>= mEnemy[i].m_posX
						&&myplane.MyPlane_posX <= mEnemy[i].m_posX + 45
						&& myplane.MyPlane_posY >= mEnemy[i].m_posY
						&& myplane.MyPlane_posY <= mEnemy[i].m_posY + 50

				) {
						myplane.mAnimState =myplane.MyPlane_DEATH_STATE;
						if(MyPlaneLifeValue>0)
							MyPlaneLifeValue-=3;
				}
			}
		//System.out.println("飞机和敌机碰撞函数被调用了");                       /**用来做检测**/
	}
	
	public void CollisionGoldCoinAndMyPlane() {
		double PlaneAndGoldCoinDistance=0;
		int i;                                                          //圆形碰撞
		for (i=0; i <goldCoinNum; i++) {
			PlaneAndGoldCoinDistance=Math.sqrt(Math.pow((goldCoin[i].getX()+8)-(myplane.MyPlane_posX+15), 2)+Math.pow((goldCoin[i].getY()+8)-(myplane.MyPlane_posY+18), 2));
			if(PlaneAndGoldCoinDistance<=25){
				goldCoin[i].GoldCoinIsOrNotAppearance=false;
				GoldCoinCount++;
			}
		}
		//System.out.println("金币和我放飞机碰撞函数被调用了"); 
	}
	
	public static void AddMyPlaneLifeValue(int blood){
		if(heartNum>0)                                /*当所有心的数量消失后，将不会再补充血液*/
		  MyPlaneLifeValue=blood;
		heartNum--;                                //用来判断一个心形是否消失
	}

	private int SuiJiShu(int botton, int top) {
		return ((Math.abs(new Random().nextInt()) % (top - botton)) + botton);
	}

	public void run() {
		while (mIsRunning) {
			Draw();
			try {
				SoundPlay();
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(95);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 在这里检测什么键被按下
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP)// 假如是向上键被按下
			up=true;
		if (key == KeyEvent.VK_DOWN)// 假如是向下键被按下
			down=true;			
		if (key == KeyEvent.VK_SPACE)               /*按下此键是会发射子弹*/
			{ 
			   space=true;
			}	
		if (key == KeyEvent.VK_LEFT)// 假如是向左键被按下
		{
			left=true;
		}
		if (key == KeyEvent.VK_RIGHT)// 假如是向右键被按下
		{
			right=true;
		}
		if(key == KeyEvent.VK_C){
			flagCC++;
			if(flagCC<2){
			ZiDan.ZiDanType=true;               //当按下的是C键时
			   }
			if(flagCC==2){
				ZiDan.ZiDanType=false;
				flagCC--;
				}
		}
		if (key == KeyEvent.VK_S)  
		{ 
			mThread.suspend();                       //这个方法不安全
		}
		if (key == KeyEvent.VK_B)  
		{ 
			mThread.resume();   //这个方法不安全    
		}
	}
	
	public void SoundPlay() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		if(enemybaozha){
			sound.loadSound("musics\\enemybaozha.wav");
			sound.playSound();
		}	
        enemybaozha=false;
	}
	
	public void LeftMove(){
		if(left){
			mAirPosX -= PLAN_STEP;
			if (mAirPosX < 0)
				mAirPosX = 0;
		}
	}
	
	public void RigthMove(){
		if(right){
			mAirPosX += PLAN_STEP;
			if (mAirPosX > mScreenWidth - 30)
				mAirPosX = mScreenWidth - 30;
		}
	}
	
	public void UpMove(){
		if(up){
			mAirPosY -= PLAN_STEP;
		}
	}
	
	public void DownMove(){
		if(down){
			mAirPosY += PLAN_STEP;
		}
	}

	public void keyReleased(KeyEvent arg0) {
		int key = arg0.getKeyCode();
		if (key == KeyEvent.VK_LEFT){
			left=false;
		}
		if (key == KeyEvent.VK_RIGHT)
			right=false;
		if (key == KeyEvent.VK_UP)
			up=false;
		if (key == KeyEvent.VK_DOWN)
			down=false;
		if (key == KeyEvent.VK_SPACE){
			space=false;
		}
	}

	public void keyTyped(KeyEvent e2) {
	}
	
}