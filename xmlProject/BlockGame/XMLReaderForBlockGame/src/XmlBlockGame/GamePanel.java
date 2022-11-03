package XmlBlockGame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Block ���� ���� Panel
 * 
 * @author ����
 */
public class GamePanel extends JPanel {
	private ImageIcon bgImg;
	private User user = null;
	private Node gamePanelNode = null;
	private JLabel life = null;
	private Attack attack[] = null;
	private AttackThread attackThread[] = null; 
	private DontGoneBlock block[] = null;
	private BlockGameFrame gameFrame = null;
	private Color aimColor = null;
	private Point aimPoint;
	private int ready=-1, mouseOn = 0, gameValue = 0, score, blockMaxCount = 0, blockCount = 0;
	private int attackCount = 1, firstDownAttack=0, downAttack=0, startCheck = 0; // �����ϸ� 1 �ƴϸ� 0
	/**
	 * GamePanel ������
	 * 
	 * @param gamePanelNode gamePanel ������ ��� ���� xml Node
	 * @param gameFrame gameFrame
	 */
	public GamePanel(Node gamePanelNode, BlockGameFrame gameFrame) {
		setLayout(null);
		this.gamePanelNode = gamePanelNode;
		this.gameFrame = gameFrame;
		Node bgNode = XMLReader.getNode(gamePanelNode, XMLReader.GAME_BG);
		bgImg = new ImageIcon(bgNode.getTextContent());
	}
	/**
	 * startCheck�� �����ϴ� �Լ� <br>
	 * startCheck : �����ϸ� 1 �ƴϸ� 0
	 * @return startCheck
	 */
	public int getStartCheck() { return startCheck; }
	/**
	 * startCheck �����ϴ� �Լ�
	 * @param i startCheck�� �����ϴ� ��
	 */
	public void setStartCheck(int i) { startCheck = i; }
	/**
	 * blockMaxCount�� �����ϴ� �Լ�
	 * @return blockMaxCount (block�� �ִ� ����)
	 */
	public int getBlockCount() { return blockMaxCount; }
	/**
	 * blockCount�� �ϳ� ���̴� �Լ�
	 */
	public void decreaseBlockCount() { 
		blockCount--; 
		if(blockCount<=0)
			gameOver();
	}
	/**
	 * ���� �����ϴ� �Լ�
	 * @param nodeList block ������ ��� ���� xml Node
	 */
	private void setBlock(NodeList nodeList) {
		blockCount = 0;
		block = new DontGoneBlock[nodeList.getLength()];
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			// found!!, <Obj> tag
			if(node.getNodeName().equals(XMLReader.E_OBJ)) {
				
				String type = XMLReader.getAttr(node, "type");
				
				if(type.equals("dontGone"))
					block[blockCount] = new DontGoneBlock(node);
				else if(type.equals("move"))
					block[blockCount] = new SideMoveBlock(node);
				else if(type.equals("gone"))
					block[blockCount] = new GoneBlock(node,gameFrame);
				else if(type.equals("moveAndGone"))
					block[blockCount] = new SideMoveAndGoneBlock(node,gameFrame);
				else
					continue;
				add(block[blockCount]);
				blockCount++;
			}
		}
		blockMaxCount = blockCount;
	}
	/**
	 * ������ �Ͻ� �ߴ��ϴ� �Լ�
	 */
	public void gameStop() {
		for(int i=0;i<attackThread.length;i++) 
			if(attackThread[i]!=null) 
				attackThread[i].gameStop();
		for(int i=0;i<blockMaxCount;i++)
			if(block[i]!=null && block[i] instanceof SideMoveBlock) {
				((SideMoveBlock)block[i]).gameStop();
			}
		if(ready == 0) {
			ready = 1;
			gameValue = 1;
		}
	}
	/**
	 * ������ ������ϴ� �Լ�
	 */
	public void gameRePlay() {
		for(int i=0;i<attackThread.length;i++) 
			if(attackThread[i]!=null) 
				attackThread[i].startGame();
		
		for(int i=0;i<blockMaxCount;i++)
			if(block[i]!=null && block[i] instanceof SideMoveBlock) 
				((SideMoveBlock)block[i]).startGame();
		if(gameValue == 1) {
			ready = 0;
			gameValue = 0;
		}
	}
	/**
	 * ������ ������ �� ������ �����ϴ� �Լ�
	 */
	private void gameRun() {
		// read <Fish><Obj>s from the XML parse tree, make Food objects, and add them to the FishBowl panel. 
		startCheck = 1;
		Node UserNode = XMLReader.getNode(gamePanelNode, XMLReader.E_USER);
		ImageIcon icon = new ImageIcon(XMLReader.getAttr(UserNode, "img"));
		ImageIcon attackIcon = new ImageIcon(XMLReader.getAttr(UserNode, "attackImg"));

		int x = this.getWidth()/2;
		int y = this.getHeight();
		int w = Integer.parseInt(XMLReader.getAttr(UserNode, "w"));
		int h = Integer.parseInt(XMLReader.getAttr(UserNode, "h"));
		this.user = new User( x-(w/2),y-h,w,h, icon, attackIcon);
		add(user);

		addMouseListener(new AimMouseListener());
		addMouseMotionListener(new AimMouseMotionListener());
		
		Node aimNode = XMLReader.getNode(gamePanelNode, XMLReader.E_AIM);
		int r = Integer.parseInt(XMLReader.getAttr(aimNode, "r"));
		int g = Integer.parseInt(XMLReader.getAttr(aimNode, "g"));
		int b = Integer.parseInt(XMLReader.getAttr(aimNode, "b"));
		aimColor = new Color(r,g,b);
		
		Node attackNode = XMLReader.getNode(gamePanelNode, XMLReader.E_ATTACK);
		attackCount = Integer.parseInt(XMLReader.getAttr(attackNode, "count"));
		attack = new Attack[attackCount];
		attackThread = new AttackThread[attackCount];
		icon = new ImageIcon(XMLReader.getAttr(attackNode, "img"));
		
		for(int i=0; i<attackCount;i++) {
			attack[i] = new Attack(user.getX()+user.getWidth()/2,user.getY()-20,icon);
			add(attack[i]);
		}
		Node blockNode = XMLReader.getNode(gamePanelNode, XMLReader.E_BLOCK);
		NodeList nodeList = blockNode.getChildNodes();
		setBlock(nodeList);
		
		repaint();
		gameFrame.startGameThread();
	}
	/**
	 * attack�� �����̰� �ϴ� �Լ�<br>
	 * attackThread�� ������ ������ ���� ����
	 */
	public void attack() {
		ready = 0;
		downAttack=0;
		for(int i=0; i<attackCount;i++)
			attackThread[i] = new AttackThread(gamePanelNode, attack[i], i, block, this);
	}
	/**
	 * attack ���� ������ ���� Ȯ���Ͽ� 
	 * �� ó�� ������ �Լ��� ���� ���� ���� ��ġ�� ����
	 * �������� �Լ��� �������� �ٽ� �غ� �������� �ϴ� �Լ�
	 * @param i ������ ���� attack index
	 */
	public void setDownAttack(int i) {
		if(downAttack==0)
			firstDownAttack = i;
		if(downAttack==attackCount-1) {
			setUser();
			setReady(-1);
		}
		downAttack++;
	}
	/**
	 * ������ �����Ű�� �Լ�
	 */
	public void gameOver() {
		for(int i=0;i<blockMaxCount;i++)
			if(block[i]!=null) 
				remove(block[i]);
		remove(user);
		for(int i=0; i<attackCount;i++)
			remove(attack[i]);
		ready = -1;
		startCheck = 0;
		blockMaxCount = 0;
		repaint();
		gameFrame.setGameSentence(1);
	}
	/**
	 * ������ �� ����� �������� ������ Ȯ���ϰ�
	 * �׷� ��� ���� �����̴� �Լ�
	 */
	public void setBlockDown() {
		for(int i=0;i<blockMaxCount;i++) {
			if(block[i]!=null && block[i].checkBlockDown()) {
				block[i].setLocation(block[i].getX(),block[i].getY()+block[i].getHeight());
				if(block[i].getY()>=getHeight()-user.getHeight()-20-block[i].getHeight()) {
					if(block[i] instanceof GoneBlockInterface) {
						Node soundNode = XMLReader.getNode(gamePanelNode, XMLReader.E_BALLSOUND);
						String die = XMLReader.getAttr(soundNode, "dieSound");
						Music dieSound = new Music(die,0);
						dieSound.play();
						gameFrame.decreaseLife();
					}
					remove(block[i]);
					block[i] = null;
					decreaseBlockCount();
					repaint();
					
				}
			}	
		}
	}
	/**
	 * user�� ��ġ�� ���� �� attack�� �� ó������ ������ ��ġ�� �̵���Ű�� �Լ�
	 */
	private void setUser() { 
		user.setImg(0); 
		for(int i=0; i<attackCount;i++)
			attack[i].setLocation(attack[firstDownAttack].getX(),getHeight()-user.getHeight()-attack[firstDownAttack].getHeight());
		user.setLocation(attack[firstDownAttack].getX()-user.getWidth()/2,attack[firstDownAttack].getY()+attack[firstDownAttack].getHeight()); 
	}
	/**
	 * ������ �����̴� ����� �ٸ� ��ϰ� ������ �� �ݴ������� �����̰� �ϴ� �Լ�
	 * 
	 * @param direction ���� �����̴� ����� ����
	 * @param myBlock �ٸ� ��ϰ� �������� Ȯ���ϰ��� �ϴ� �����̴� ���
	 * @param lastAttackBlock �����̴� ����� ���������� ���� ���
	 * @return ����� �ٸ� ��ϰ� ������ ��� true
	 */
	public boolean checkBlockSide(int direction, SideMoveBlock myBlock, Point lastAttackBlock) { //direction<0�̸� <-���� || >0�̸� ->����
		for(int i=0;i<blockMaxCount;i++) {
			if(block[i]!=null && !(block[i].equals(myBlock)) && block[i].checkBlockMit(myBlock, 2)) {
				if(lastAttackBlock==null || !(block[i].getLocation().equals(lastAttackBlock)) ) {
					if(direction<0 &&  block[i].checkBlockMit(myBlock, 1)) {
						myBlock.setLastAttackBlockPoint(block[i].getLocation());
						return true;
					}
					if(direction>0 &&  block[i].checkBlockMit(myBlock, 1)) {
						myBlock.setLastAttackBlockPoint(block[i].getLocation());
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * ready ���� ���ڸ� �����ϴ� �Լ� <br>
	 * ready : 0�� ��� attack ������ ���, 1�� ��� attack �����̴�, -1�� ��� ������ ��� �� game set 
	 * 
	 * @return ready ����
	 */
	public int getReady() { return ready; }
	/**
	 * ready�� ����
	 * @param ready �����ϰ��� �ϴ� ready ��
	 */
	private void setReady(int ready) { this.ready = ready; }
	/**
	 * aimPoint�� �˾Ƴ���
	 * @return aimPoint ����
	 */
	public Point getAimPoint() { return aimPoint; }
	/**
	 * ���콺�� �����϶����� ���콺 ��ġ�� aimPoint ����
	 * 
	 * @author ����
	 */
	public class AimMouseMotionListener extends MouseMotionAdapter  {
		@Override
		public void mouseMoved(MouseEvent e) { //�ش� ������Ʈ������ ���콺�� �����϶� �߻�
			aimPoint = e.getPoint();
			mouseOn = 1;
			repaint();
		}
	}
	/**
	 * ���콺�� Ŭ���ϸ� �� ��ġ�� ���� aimPoint�� �����ϴ� �Լ�
	 * 
	 * @author ����
	 */
	public class AimMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) { 
			if(ready==0) {
				aimPoint = e.getPoint();
				
				user.setImg(1);
				mouseOn = 0;
				repaint();
				for(int i=0; i<attackCount;i++)
					if(attackThread[i] != null && attackThread[i].getState() == Thread.State.NEW)
						attackThread[i].start();
				ready=1;
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {//���콺�� �ش� ������Ʈ ���� ������ ������ �߻�
			mouseOn = 0;
		}
	}
	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(bgImg.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
		if(startCheck==-1)
			gameRun();
		if(ready==0 && mouseOn==1) {
			g.setColor(aimColor);
			g.drawLine(attack[0].getX()+attack[0].getWidth()/2, attack[0].getY()+attack[0].getHeight()/2, aimPoint.x, aimPoint.y);
		}
	}
}
