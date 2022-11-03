package XmlBlockGame;
import java.awt.*;
import javax.swing.*;
import org.w3c.dom.Node;
/**
 * Block ������ Ÿ��Ʋ Ȥ�� ���� ���� �� Panel
 * 
 * @author ����
 */
public class GameInitPanel extends JPanel {
	private JLabel initText = new JLabel("");
	private Image img;
	private String text[] = new String[3];
	/**
	 * GameInitPanel ������
	 * 
	 * @param initGameNode initPanel ������ ��� ���� xml Node
	 */
	public GameInitPanel(Node initGameNode) {
		Node fontNode = XMLReader.getNode(initGameNode, XMLReader.E_FONT);
		Node sentenceNode = XMLReader.getNode(initGameNode, XMLReader.E_GAMESENTENCE);
		int r = Integer.parseInt(XMLReader.getAttr(fontNode, "r"));
		int g = Integer.parseInt(XMLReader.getAttr(fontNode, "g"));
		int b = Integer.parseInt(XMLReader.getAttr(fontNode, "b"));
		Color color = new Color(r,g,b);
		Font font = new Font(XMLReader.getAttr(fontNode, "font"),Font.BOLD, 
				Integer.parseInt(XMLReader.getAttr(fontNode, "fontSize")));
		Node initBgNode = XMLReader.getNode(initGameNode, XMLReader.INIT_BG);
		ImageIcon initBgIcon = new ImageIcon(initBgNode.getTextContent());
		img = initBgIcon.getImage();
		
		text[0] = XMLReader.getAttr(sentenceNode, "start");
		text[1] = XMLReader.getAttr(sentenceNode, "win");
		text[2] = XMLReader.getAttr(sentenceNode, "lose");
		
		initText.setText(" ");
		initText.setFont(font); // font ����
		initText.setForeground(color);
		
		setLayout(new BorderLayout());
		initText.setHorizontalAlignment(JLabel.CENTER);
		add(initText);
	}
	/**
	 * initPanel�� ��Ÿ�� Text �����ϴ� �Լ�
	 * @param select ������ ������ ���° index ������ ����
	 */
	public void setInitText(int select) {
		initText.setText(text[select]);
	}
	@Override
	public void paintComponent(Graphics g) { //call back �Լ�
		super.paintComponent(g); // �г��� ��� ����� -> ������ ĥ�Ѵ�
		g.drawImage(img, 0,0, this.getWidth(), this.getHeight(), null);
	}
}