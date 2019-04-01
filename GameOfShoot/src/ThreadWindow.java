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

/**�߳���**/
public class ThreadWindow extends JPanel implements Runnable, KeyListener {
	/*��Ļ�Ŀ��,�����ȫ�ֱ��������ں��ڵ��޸�*/
	private int flagCC=0;
	private int mScreenWidth = 320;
	private int mScreenHeight = 480;
	private static final int STATE_GAME = 0;	//��Ϸ���˵�״̬,���������ڵĹ���
	private int mState = STATE_GAME;       //��Ϸ״̬
	private Image mBitMenuBG0 = null;  //�������ĵ�һ��ͼƬ
	private Image mBitMenuBG1 = null;   //�������ĵڶ���ͼƬ
	/*��Ϊ����ͼƬ������᲻ͣ�ı仯,���Զ���Ϊȫ�ֱ���*/
	private int mBitposY0 = 0;
	private int mBitposY1 = 0;
	final static int ZiDan_POOL_COUNT = 15;   //Ҫ��ʼ�����ӵ����������,��������̫��,����Ϊ������ֹ�������й����б��޸�
	final static int PLAN_STEP = 4;      //�������ʱ,�ɻ�ÿ���ƶ��ľ���
	final static int PLAN_TIME =420;   //û��420���뷢��һ���ӵ�
	final static int ENEMY_POOL_COUNT = 5;       //Ҫ��ʼ���ĵл����������,����Ϊ����
	final static int ENEMY_POS_OFF = 65;   //���˷ɻ�ƫ����
	private Thread mThread = null;     //��Ϸ�����߳�
	private boolean mIsRunning = false;     //�߳�ѭ���Ƿ�����ı�־,������ͣ�ã������滻suspend/resume����,��Ϊ���������������̰߳�ȫ��
	public int mAirPosX =150;   //��Ϸ�ս���ʱ�ҷ��ɻ�����Ļ����ʾ��λ��
	public int mAirPosY =400;
	/*�л���������*/
	EnemyPlane mEnemy[] = null;
	/*�ӵ���������*/
	ZiDan mZidan[] = null;
	public int mSendId = 0;    //��ʼ�������ӵ�ID,�����б�ÿһ���߳�ɨ��ʱ�ӵ����ػ���һ��ͼƬ
	public Long mSendTime = 0L;       //��һ���ӵ������ʱ��
	Image myPlanePic[]; //��ҷɻ�����ͼƬ
	public int myPlaneID = -1;    //�ҷ��ɻ���ǰ֡��
	
	public int myPlaneBombID=-1;    /*�ҷ��ɻ���ըʱ��ͼƬ�������*/
	public Long myPlaneBombedTime=0L;  /*�ҷ��ɻ���ըʱ��ʱ��*/
    
	/*��ͬʱ����space���ͷ����ʱ������*/
	private static boolean left=false;
	private static boolean right=false;
	private static boolean up=false;
	private static boolean down=false;
	private static boolean space=false;
	private static boolean enemybaozha=false;
	
	private int EnemyBombedNum=0;       /*���ҷŴ��еĵл�����,������Ļ���ϽǼߵе�ͳ������*/
	private static int MyPlaneLifeValue=90;        //�ҷ��ɻ�������ֵ
	
	GoldCoin[] goldCoin=null;                      /*������Ҷ�������*/
	private final int goldCoinNum=30;                  /*Ҫ�����Ľ������*/
	
	private int GoldCoinCount=0;                             /*�ҷ��ɻ��Ե��Ľ������*/
	private Image GoldCoinCountIcon=null;
	private Image[] ImgHeart=null;                  /*��������ҷ��ɻ���������������ͼ��*/
	private static int heartNum=2;                  /*���ε��������ڱ���ľ�̬������Ҫ�õ��ñ�������������Ϊ��̬��*/
	public Sound sound=new Sound();
	
	MyPlane myplane=null;     //����һ���ҷ��ɻ�����

	public ThreadWindow() {
		setFocusable(true);
		addKeyListener(this);
		init();
		setGameState(STATE_GAME);
		mIsRunning = true;
		mThread = new Thread(this);       // ʵ���߳�
		mThread.start();
		setVisible(true);

	}

	protected void Draw(){
		switch (mState) {
		case STATE_GAME:
			renderBg();// ������Ϸ���棨�������������ҷɻ����ӵ�)
			updateBg(); // ������Ϸ
			break;
		}
	}

	private void init() {
		/*��Ϸ���ֶ���ĳ�ʼ��*/
		try {
			mBitMenuBG0 = Toolkit.getDefaultToolkit().getImage(              //��ȡͼƬ
					"images\\map_0.png");

			mBitMenuBG1 = Toolkit.getDefaultToolkit().getImage(
					"images\\map_1.png");
			ImageIO.read(new File("images/map_1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mBitposY0 = 0;                   //��һ��ͼƬ��λ��
		mBitposY1 = -mScreenHeight;  //�ڶ���ͼƬ�ڵ�һ��ͼƬ���Ϸ�,����֮���ǽ����ŵ�
		myplane=new MyPlane();   //�����ҷ��ɻ�����
		mEnemy = new EnemyPlane[ENEMY_POOL_COUNT];   /*�������˶��������*/

		for (int i = 0; i < ENEMY_POOL_COUNT; i++) {
			mEnemy[i] = new EnemyPlane();
			mEnemy[i].init(i * ENEMY_POS_OFF, i * ENEMY_POS_OFF-300);   //����ÿ�ܵл��ĳ�ʼ��λ�ö���һ������ֹ�ص�
		}

		/*�����ӵ������*/
		mZidan = new ZiDan[ZiDan_POOL_COUNT];
		for (int i = 0; i < ZiDan_POOL_COUNT; i++) {
			mZidan[i] = new ZiDan();
		}
		
		goldCoin=new GoldCoin[goldCoinNum];    /*��ʼ��50����Ҷ���*/
		for(int i=0;i<goldCoinNum;i++){
			goldCoin[i]=new GoldCoin();
		}
		
		GoldCoinCountIcon=Toolkit.getDefaultToolkit().getImage("images\\coin.png");    /*��������ʾ��õĽ������*/
		
		ImgHeart=new Image[3];           /*��ʼ�����ζ���*/
		for(int i=0;i<3;i++){
			ImgHeart[i]=Toolkit.getDefaultToolkit().getImage("images\\heart.png");	
		}
		
		mSendTime = System.currentTimeMillis();
		myPlaneBombedTime=System.currentTimeMillis();//��ʼ���ҷŷɻ���ըʱ��ʱ��
	}

	private void setGameState(int newState) {
		mState = newState;                                      //��Щ����û��Ҫ����,ֻ�ǿ��ǵ����ڵĸ����޸��õ�
	}

	public void renderBg() {
		myPlaneID++;                      //�ҷ��ɻ�ͼƬ����֡��
		myPlaneBombID++;
		if (myPlaneID == 6)
			myPlaneID = 0;
		if (myPlaneBombID == 6){
			myPlaneBombID = 0;
		    //myplane.mAnimState=myplane.MyPlane_ALIVE_STATE;
		}
		repaint();                   //�ػ棬�������ʵ���ǵ���Update()�����ģ���Update()�ֵ���paint()�������������д˫���弼��
	}

	public void paint(Graphics g) {                        //�����ػ�����������е�
		myplane.init(mAirPosX, mAirPosY);         //��ʼ���ҷ��ɻ�������
		g.drawImage(mBitMenuBG0, 0, mBitposY0, this);             //������
		g.drawImage(mBitMenuBG1, 0, mBitposY1, this);
		myplane.DrawMyPlane(g, this,myPlaneBombID,myPlaneID,MyPlaneLifeValue);   /*�����Լ��ɻ�����*/
		
		/*�����ӵ�����*/
		for (int i = 0; i < ZiDan_POOL_COUNT; i++)
			mZidan[i].DrawZiDan(g, this);

		/*���Ƶз��ɻ��Ķ���*/
		for (int i = 0; i < ENEMY_POOL_COUNT; i++)
			mEnemy[i].DrawEnemy(g, this);
		
		/**���ƽ�Ҷ���,���ߵ�50�ܺ�ͻ���һ�γԽ�ҵĻ���**/
		if(EnemyBombedNum>5){
		    for(int i=0;i<goldCoinNum;i++)
			    goldCoin[i].draw(g, this);
		    }
		
		g.setColor(Color.GREEN);
		g.drawString("����ֵ: ", 5, 20);
		g.setColor(new Color(233,56,56));
	    g.fillRect(50, 10, MyPlaneLifeValue, 10);
	    g.drawString("�ߵ�: "+EnemyBombedNum, 5, 40);
	    
	    for(int i=0,j=3;i<heartNum;i++,j+=27){                           /*����������ͼ��*/
	    	g.drawImage(ImgHeart[i], j, 425, this);
	    }
	    
	    g.drawImage(GoldCoinCountIcon, 260, 8, this);                    //���ﲻ����
	    g.drawString(GoldCoinCount+"", 290, 20);
	}

	private void updateBg(){
		mBitposY0 += 8;      //����ͼƬ���꣬ʵ�ֹ���Ч��
		mBitposY1 += 8;
		if (mBitposY0 == mScreenHeight) {
			mBitposY0 = -mScreenHeight;
		}
		if (mBitposY1 == mScreenHeight) {
			mBitposY1 = -mScreenHeight;
		}

		for (int i = 0; i < ZiDan_POOL_COUNT; i++) {    /*�����ӵ�����,Ҳ�����÷���ȥ���ӵ�������*/
			mZidan[i].UpdateZiDan();
		}
		
		for (int i = 0; i < ENEMY_POOL_COUNT; i++) {
			mEnemy[i].UpdateEnemy();                                //���µл�������Ҳ�����õл�������
			if (mEnemy[i].mAnimState == EnemyPlane.ENEMY_DEATH_STATE     /*�л����� ���� �л�������Ļ��δ������������*/
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

		CollisionBuiletAndEnemy();          //�ӵ��͵л�����ײ
		CollisionMyPlaneAndEnemy();   //�ҷ��ɻ����ӵ�����ײ
		CollisionGoldCoinAndMyPlane();   //�Խ�ҵ���ײ
		CloseGameWindow();
	}
     
	public void SendBuilet(){                                   //�ӵ����䷽��
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

	/*��space����Ϊtrueʱ�����ӵ�*/
	public void IsOrNotSendBuilet(){
		if(space){
			SendBuilet();
			}
	}
	
	public void CloseGameWindow(){                      //�ҷ��ҵ�����ֵ���Ϸ����
		if(heartNum==0 && MyPlaneLifeValue==0){
			mIsRunning=false;
			MyPlaneLifeValue=90;
			heartNum=2;
		   GameStopInterface hh=new GameStopInterface();
		}
	}
	
	public void CollisionBuiletAndEnemy() {                          //�����õ����Ǿ�����ײ����ײ�ֲܴڣ����Ȳ��Ǻܸ�
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
					mZidan[i].m_posX=-200;           //���򵽷ɻ�ʱ���ӵ�������ʧ
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
	
	public void CollisionMyPlaneAndEnemy() {                        //������ײ
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
		//System.out.println("�ɻ��͵л���ײ������������");                       /**���������**/
	}
	
	public void CollisionGoldCoinAndMyPlane() {
		double PlaneAndGoldCoinDistance=0;
		int i;                                                          //Բ����ײ
		for (i=0; i <goldCoinNum; i++) {
			PlaneAndGoldCoinDistance=Math.sqrt(Math.pow((goldCoin[i].getX()+8)-(myplane.MyPlane_posX+15), 2)+Math.pow((goldCoin[i].getY()+8)-(myplane.MyPlane_posY+18), 2));
			if(PlaneAndGoldCoinDistance<=25){
				goldCoin[i].GoldCoinIsOrNotAppearance=false;
				GoldCoinCount++;
			}
		}
		//System.out.println("��Һ��ҷŷɻ���ײ������������"); 
	}
	
	public static void AddMyPlaneLifeValue(int blood){
		if(heartNum>0)                                /*�������ĵ�������ʧ�󣬽������ٲ���ѪҺ*/
		  MyPlaneLifeValue=blood;
		heartNum--;                                //�����ж�һ�������Ƿ���ʧ
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

	// ��������ʲô��������
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP)// ���������ϼ�������
			up=true;
		if (key == KeyEvent.VK_DOWN)// ���������¼�������
			down=true;			
		if (key == KeyEvent.VK_SPACE)               /*���´˼��ǻᷢ���ӵ�*/
			{ 
			   space=true;
			}	
		if (key == KeyEvent.VK_LEFT)// �����������������
		{
			left=true;
		}
		if (key == KeyEvent.VK_RIGHT)// ���������Ҽ�������
		{
			right=true;
		}
		if(key == KeyEvent.VK_C){
			flagCC++;
			if(flagCC<2){
			ZiDan.ZiDanType=true;               //�����µ���C��ʱ
			   }
			if(flagCC==2){
				ZiDan.ZiDanType=false;
				flagCC--;
				}
		}
		if (key == KeyEvent.VK_S)  
		{ 
			mThread.suspend();                       //�����������ȫ
		}
		if (key == KeyEvent.VK_B)  
		{ 
			mThread.resume();   //�����������ȫ    
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