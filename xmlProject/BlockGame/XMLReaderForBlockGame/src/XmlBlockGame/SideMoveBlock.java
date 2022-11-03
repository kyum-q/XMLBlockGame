package XmlBlockGame;
import java.awt.Point;
import javax.swing.JLabel;
import org.w3c.dom.Node;

/**
 * ������ �¿�� �����̰� ������� �ʴ� ��� �̹��� ���̺� <br>
 * (extends DontGoneBlock)
 * 
 * @author ����
 */
public class SideMoveBlock extends DontGoneBlock implements Runnable {
	private int moveDelay, moveX, moveDirection = 5, moveValue = 1;
	private boolean gameState = true;
	private Point lastAttackBlock = null;
	private Thread th = null;
	/**
	 * SideMoveBlock ������
	 * 
	 * @param node block ������ ��� ���� xml Node
	 */
	public SideMoveBlock(Node node) {
		super(node);
		moveDelay = Integer.parseInt(XMLReader.getAttr(node, "moveDelay"));
		moveDirection = Integer.parseInt(XMLReader.getAttr(node, "moveDirection"));
		moveX = moveDirection;
		if(moveDirection<0)
			moveValue = -1;
		th = new Thread(this);
		th.start();
	}
	/**
	 * �¿�� �����̸鼭 ���������� ���� ����� �����ϴ� �Լ�
	 * 
	 * @param lastAttackBlock �����ϰ��� �ϴ� ���������� ���� ���
	 */
	public void setLastAttackBlockPoint(Point lastAttackBlock) {
		this.lastAttackBlock = lastAttackBlock;
	}
	/**
	 * ������ �ߴ��ϴ� �Լ�<br> 
	 * gameState�� false�� ����
	 */
	public void gameStop() {gameState = false;}
	/**
	 * ���� ���� ����(gameState)�� Ȯ���ϴ� �Լ�<br>
	 * gameState�� false�� ��� wait()
	 */
	synchronized private void checkGameState() {
		if(!gameState) {
			try { wait();}
			catch (InterruptedException e) { return; }
		}
	}
	/**
	 * ������ ������ϴ� �Լ�<br> 
	 * gameState�� true�� ����� wait�� �͵��� notifyAll()
	 */
	synchronized public void startGame() {
		gameState = true;
		notifyAll();
	}
	/**
	 * Thread�� run �Լ�<br>
	 * block�� �����δ�
	 */
	@Override
	public void run() {
		while(true) {
			checkGameState();
			try {
				Thread.sleep(moveDelay);
				
				if(getParent() != null && getX()+getWidth()>=getParent().getWidth()) {
					moveX = -(moveDirection*moveValue);
					lastAttackBlock = null;
				}
				else if(getX()<=0) {
					moveX = moveDirection*moveValue;
					lastAttackBlock = null;
				}
				setLocation(getX() + moveX, getY());
				
				if(getParent()!=null && ((GamePanel) getParent()).checkBlockSide(moveX,this,lastAttackBlock)) {
					if(moveX>0)
						moveX = -(moveDirection*moveValue);
					else
						moveX = moveDirection*moveValue;
				}
				
			} catch (InterruptedException e) { return; }	
		}
	}
}
