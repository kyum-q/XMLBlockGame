package XmlBlockGame;

/**
 * 	game�� �����̴� Thread
 * 
 * @author ����
 */
public class GameThread extends Thread {
	private GamePanel gamePanel = null;
	private boolean gameState = true;
	/**
	 * GameThread ������
	 * 
	 * @param gamePanel gamePanel
	 */
	public GameThread(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	/**
	 * ������ �ߴ��ϴ� �Լ�<br> 
	 * gameState�� false�� ����
	 */
	public void gameStop() { gameState = false; }
	/**
	 * ���� ���� ����(gameState)�� Ȯ���ϴ� �Լ�<br>
	 * gameState�� false�� ��� wait()
	 */
	synchronized private void checkGameState() {
		if(!gameState) {
			try { this.wait(); }
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
	 * Thread�� run �Լ�
	 */
	@Override
	public void run() {
		gamePanel.attack();
		while(true) {
			checkGameState();
			try {
				if(gamePanel.getReady()==-1) {
					gamePanel.attack();
					gamePanel.setBlockDown();
				}
				sleep(600);
			} catch (InterruptedException e) {
				gamePanel.gameOver();
				return;
			}
		}
	}
}
