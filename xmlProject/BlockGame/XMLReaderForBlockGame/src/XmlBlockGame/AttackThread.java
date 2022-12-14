package XmlBlockGame;
import java.awt.*;
import javax.swing.*;
import org.w3c.dom.Node;
/**
 * 	attack의 움직임을 나타내는 Thread
 * 
 * @author 김경미
 */
public class AttackThread extends Thread {
	/**
	 * @param delay 볼이 움직이는 속도
	 * @param ballDelay 볼들 사이 속도 차이
	 * @param moving attack의 x 거리 증가량
	 * @param startX 원점 x좌표
	 * @param startY 원점 y좌표
	 * @param reboundX 튕겨진 점 x좌표
	 * @param reboundY 튕겨진 점 y좌표
	 * @param directionY y가 아래로 떨어지면 -1 위로 올라가면 1
	 * @param formulaY 각도에 따른 x증가량에 따른 y증가량
	 * @param lastAttackBlock 마지막으로 때린 block (똑같은거에 두번 튕기는걸 방지)
	 * @param hit block에 attack이 맞았을 때 나는 사운드 파일 이름
	 * @param remove block에 attack에 맞아 사라질 때 나는 사운드 파일 이름
	 * @param gameState attackThread의 상태를 나타내는 논리 인자(현재 중단됬는지 진행중인지)
	 */
	private int delay, ballDelay, moving = 5;
	private int x, y, startX, startY, reboundX, reboundY;
	private int directionY = 1, lastAttackBlock = -1, i = 0;
	private double formulaY;
	private Attack attack;
	private DontGoneBlock block[] = null;
	private GamePanel gamePanel = null;
	private String hit, remove;
	private boolean gameState = true;
	/**
	 * AttackThread의 생성자
	 * 
	 * @param gamePanelNode attack 정보를 얻기 위한 xml Node
	 * @param attack attack 이미지
	 * @param i 몇번째 attack 공인지 알려주는 정수형 인자
	 * @param block attack으로 때려야하는 block들
	 * @param gamePanel 게임 진행 패널
	 */
	public AttackThread(Node gamePanelNode,Attack attack, int i, DontGoneBlock block[], GamePanel gamePanel) {
		Node attackNode = XMLReader.getNode(gamePanelNode, XMLReader.E_ATTACK);
		this.gamePanel = gamePanel;
		this.attack = attack;
		this.i = i;
		this.block = block;
		this.reboundY = this.startY = this.y = attack.getY();
		this.reboundX = this.startX = this.x = attack.getX();
		
		delay = Integer.parseInt(XMLReader.getAttr(attackNode, "delay"));
		ballDelay = Integer.parseInt(XMLReader.getAttr(attackNode, "ballCountDelay"));
		Node soundNode = XMLReader.getNode(gamePanelNode, XMLReader.E_BALLSOUND);
		hit = XMLReader.getAttr(soundNode, "hitSound");
		remove = XMLReader.getAttr(soundNode, "removeSound");
	}
	/**
	 * attack이 움직이는 각도를 알아내는 함수<br>
	 * : 원점과 포인트 점 사이에 각도를 알아내서 x의 증가 값에 따른 y의 증가 값 구하기
	 * 
	 * @param p 원점부터 각도를 구하고하는 조준점 포인트
	 */
	private void nextXY(Point p) {
		double ratioX, ratioY;
		if(p.y == reboundY || p.x == startX && p.y == startY) {
			// 리바운드의 y좌표랑 조준점의 y좌표랑 같거나 
			// 조준 점이 원점하고 똑같을 경우 각도 변경하지 않고 리바운드 점을 원점으로 변경
			reboundX = x;
			reboundY = y;
		}
		else {
			// 위에 경우가 아닌경우 원점과 조준점 사이의 각도 구하기
			ratioX = p.x - x;
			ratioY = p.y - y;
			formulaY = ratioY/ratioX;
			if(ratioX>0) 
				moving = -moving;
			
			startX = reboundX;
			startY = reboundY;
		}
	}
	/**
	 * 게임을 중단하는 함수<br> 
	 * gameState를 false로 만듦
	 */
	public void gameStop() { gameState = false; }
	/**
	 * 현재 게임 상태(gameState)를 확인하는 함수<br>
	 * gameState가 false일 경우 wait()
	 */
	synchronized private void checkGameState() {
		if(!gameState) {
			try { this.wait(); }
			catch (InterruptedException e) { return; }
		}
	}
	/**
	 * 게임을 재시작하는 함수<br> 
	 * gameState를 true로 만들고 wait한 것들을 notifyAll()
	 */
	synchronized public void startGame() {
		gameState = true;
		notifyAll();
	}
	/**
	 * Thread의 run 함수<br>
	 * attack을 움직인다
	 */
	@Override
	public void run() {
		nextXY(gamePanel.getAimPoint());
		try {
			Thread.sleep(i*ballDelay); // 볼 사이의 딜레이 만들기
		} catch (InterruptedException e1) {
			return;
		}
		
		while(true) {
			checkGameState(); // 게임이 진행 중인지 확인
			try {
				x -= moving;
				if(directionY == 1)
					y = y - (int)(moving*formulaY);
				else
					y = y + (int)(moving*formulaY);
				if(y<=0) 
					directionY = -directionY;
				if(y>=gamePanel.getHeight()) 
					interrupt();
				if(x<=0 || x>=gamePanel.getWidth()) {
					formulaY = -formulaY;
					moving = -moving;
					if(formulaY<0.1 && Math.random()>0.3) {
						formulaY -= 0.3;
					}
				}
				attack.setLocation(x,y);
				
				for(int i=0;i<gamePanel.getBlockCount();i++) {
					if(block[i]!=null && lastAttackBlock!=i && block[i].blockAttack(attack)) {
						// 어택이 블럭에 맞았을 경우
						Music hitSound = new Music(hit,0);
						hitSound.play();
						lastAttackBlock = i;
						
						nextXY(new Point(reboundX,reboundY));
						if(directionY==1)
							directionY = -directionY;
						else {
							formulaY = -formulaY;
							moving = -moving;
						}
						if(block[i] != null && gamePanel != null && 
								block[i] instanceof GoneBlockInterface && ((GoneBlockInterface)block[i]).checkHitCount()) {
							// 어택에 블럭이 맞아 사라졌을 경우
							gamePanel.remove(block[i]);
							block[i] = null;
							Music removeSound = new Music(remove,0);
							removeSound.play();
							gamePanel.decreaseBlockCount();
							gamePanel.repaint();
						}
					}
				}
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				gamePanel.setDownAttack(this.i);
				return;
			}
		}
	}
}
