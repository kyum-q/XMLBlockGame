package XMLWritingTool;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import XmlBlockGame.Music;
import XmlBlockGame.XMLReader;

/**
 * Block �������� Frame�� �����ϴ� class
 * 
 * @author ����
 */
public class XmlWritingToolsFrame extends JFrame {
	private File file = null;
	private XMLReader xml = null;
	private BasicToolsPanel basicPanel = null;
	private GameGroundPanel groundPanel = null;
	private BlockToolsPanel blockToolsPanel = null;
	private MusicToolsPanel musicToolsPanel = null;
	private GameInitToolsPanel initToolsPanel = new GameInitToolsPanel();
	private FileDialog fileDialog = new FileDialog(this,"save file set");
	private JFrame frame;
	
	private Music music = new Music();
	private XmlString gameBgObj = null;

	private int width, height;
	private String fileName;
	
	private JSplitPane hPane = new JSplitPane();
	private JTabbedPane pane = new JTabbedPane();
	
	private JSlider xSlider = null, ySlider = null;
	/**
	 * XmlWritingToolsFrame ������
	 */
	public XmlWritingToolsFrame() {
		width=600; height=500;
		xSlider = new JSlider(JSlider.HORIZONTAL, 100, 600, 600);
		ySlider = new JSlider(JSlider.HORIZONTAL, 100, 500, 500);
		setWritingToolsFrame();
	}
	/**
	 * XmlWritingToolsFrame ������
	 * @param filePath xml ���� �̸� ���ڿ�
	 */
	public XmlWritingToolsFrame(String filePath) {
		xml = new XMLReader(filePath); // XMLReader ���� -> XML�� �о�Ͷ�
		Node blockGameNode = xml.getBlockGameElement(); 
		Node sizeNode = XMLReader.getNode(blockGameNode, XMLReader.FRAME_SIZE);
		int w = Integer.parseInt(XMLReader.getAttr(sizeNode, "w"));
		int h = Integer.parseInt(XMLReader.getAttr(sizeNode, "h"));
		this.width = w;
		this.height = h;
		if(width>600)
			width = 600;
		if(height>500)
			height = 500;
		if(width<100)
			width = 100;
		if(height<100)
			height = 100;
		xSlider = new JSlider(JSlider.HORIZONTAL, 100, 600, width);
		ySlider = new JSlider(JSlider.HORIZONTAL, 100, 500, height);
		setWritingToolsFrame();
		setXml();
	}
	/**
	 * xml ������ ������ ��� ��� �г��� xml ���Ͽ� ���缭 �����ϴ� �Լ�
	 */
	private void setXml() {
		Node bgNode = XMLReader.getNode(xml.getGamePanelElement(), XMLReader.GAME_BG);
		gameBgObj.setObj(bgNode.getTextContent());
		groundPanel.setXmlBlock(xml.getGamePanelElement());
		basicPanel.setXmlBasic(xml.getGamePanelElement());
		musicToolsPanel.setXmlMusic(xml.getSoundElement());
		initToolsPanel.setXmlInit(xml.getInitPanelElement());
	}
	/**
	 * �⺻ component ���� �Լ�
	 */
	private void setWritingToolsFrame() {
		frame = this;
		setSize(912,586);
		
		groundPanel = new GameGroundPanel(music);
		basicPanel = new BasicToolsPanel(groundPanel);
		blockToolsPanel = new BlockToolsPanel(groundPanel);
		musicToolsPanel = new MusicToolsPanel(groundPanel);
		gameBgObj = new XmlString("GameBg", groundPanel, 0);
		
		pane.addTab("Basic",basicPanel);
		pane.addTab("Music",musicToolsPanel);
		pane.addTab("Block",blockToolsPanel);
		pane.addTab("Init",initToolsPanel);
		
		makeToolBar();
		setGroundPanel(width,height);
		setLocationRelativeTo(null);
		setResizable(false); // âũ�� ���� (���� �Ұ�)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Frame â ������ �����Ű��
		setVisible(true);
		groundPanel.setUserAndAttack();
	}
	/**
	 * ����ȭ�� �г��� �������ִ� �Լ�
	 * @param w ����ȭ�� �г� ����
	 * @param h ����ȭ�� �г� ����
	 */
	private void setGroundPanel(int w, int h) {
		getContentPane().add(hPane, BorderLayout.CENTER);
		
		hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		hPane.setDividerLocation(601);
		hPane.setEnabled(false); // splitPane ��ġ ����
		hPane.setDividerSize(0);
		
		JSplitPane topPane = new JSplitPane();
		topPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		topPane.setDividerLocation(250-h/2);
		topPane.setEnabled(false); // splitPane ��ġ ����
		topPane.setDividerSize(0);
		topPane.setBorder(null);
		
		JSplitPane leftPane = new JSplitPane();
		leftPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		leftPane.setDividerLocation(300-w/2);
		leftPane.setEnabled(false); // splitPane ��ġ ����
		leftPane.setDividerSize(0);
		leftPane.setBorder(null);
		
		JSplitPane rightPane = new JSplitPane();
		rightPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		rightPane.setDividerLocation(w);
		rightPane.setEnabled(false); // splitPane ��ġ ����
		rightPane.setDividerSize(0);
		rightPane.setBorder(null);
		
		JSplitPane bottomPane = new JSplitPane();
		bottomPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		bottomPane.setDividerLocation(h);
		bottomPane.setEnabled(false); // splitPane ��ġ ����
		bottomPane.setDividerSize(0);
		bottomPane.setBorder(null);
		
		bottomPane.setTopComponent(groundPanel);
		bottomPane.setBottomComponent(new BackGroundPanel());
		
		rightPane.setRightComponent(new BackGroundPanel());
		rightPane.setLeftComponent(bottomPane);
		
		leftPane.setRightComponent(rightPane);
		leftPane.setLeftComponent(new BackGroundPanel());
		
		topPane.setTopComponent(new BackGroundPanel());
		topPane.setBottomComponent(leftPane);
		
		hPane.setRightComponent(pane);
		hPane.setLeftComponent(topPane);
		
		groundPanel.setGroundSize();
	}
	/**
	 * ToolBar�� �����ϴ� �Լ�<br>
	 * 
	 * (�޴� ��� : ���� ����, ����/ BGM on, off/ ���ȭ�� ����/ ���� ����� )
	 */
	private void makeToolBar() {
		JToolBar tBar = new JToolBar();

		JButton openBtn = new JButton("FileOpen");
		JButton saveBtn = new JButton("SAVE");
		JButton bgmBtn = new JButton("BGM on/off");
		JButton gameBgBtn = new JButton("BackGround");
		JButton removeBtn = new JButton("Remove");
		
		saveBtn.setToolTipText("���� ����");
		gameBgBtn.setToolTipText("������ ����̹��� ����");
		removeBtn.setToolTipText("���� �� ������ Ŭ���ϸ� ������ �����˴ϴ�");
		
		tBar.add(openBtn);
		
		tBar.add(saveBtn);
		tBar.addSeparator();
		tBar.add(bgmBtn);
		tBar.addSeparator();
		tBar.add(gameBgBtn);
		tBar.addSeparator();
		tBar.add(removeBtn);
		tBar.addSeparator();
		
		xSlider.setPaintLabels(true);
		xSlider.setPaintTicks(true);
		xSlider.setPaintTrack(true);
		xSlider.setOpaque(false);
		xSlider.setMajorTickSpacing(100);
		xSlider.setMinorTickSpacing(50); 
		xSlider.setToolTipText("���� �г��� ���� ��");
		xSlider.addChangeListener(new ChangeListener(){
	    @Override
	    	public void stateChanged(ChangeEvent e) {
	    		JSlider source=(JSlider)e.getSource();
	    		int val = (int) source.getValue();
	    		width = val;
	    		setGroundPanel(width,height);
	    	}
	    });
	    tBar.add(xSlider);
	    tBar.addSeparator();
	    
	    ySlider.setPaintLabels(true);
	    ySlider.setPaintTicks(true);
	    ySlider.setPaintTrack(true);
	    ySlider.setOpaque(false);
	    ySlider.setMajorTickSpacing(100);
	    ySlider.setMinorTickSpacing(50); 
	    ySlider.setToolTipText("���� �г��� ���� ��");
	    ySlider.addChangeListener(new ChangeListener(){
	    @Override
	    	public void stateChanged(ChangeEvent e) {
	    		JSlider source=(JSlider)e.getSource();
	    		int val = (int) source.getValue();
	    		height = val;
	    		setGroundPanel(width,height);
	    	}
	    });
	    tBar.add(ySlider);
	    tBar.addSeparator();
	    tBar.addSeparator();
		getContentPane().add(tBar,BorderLayout.NORTH); // ������ BorderLayout�̾���Ѵ�
		
		
		openBtn.addActionListener(new ActionListener() {
			private JFileChooser chooser;
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("C:\\Users\\user\\OneDrive\\���� ȭ��\\���� �н� ������Ʈ\\xmlProject"));
				FileNameExtensionFilter filter = 
						new FileNameExtensionFilter("xml", "xml");
				chooser.setFileFilter(filter);
				
				int ret = chooser.showOpenDialog(null);
				if(ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "������ �������� �ʾҽ��ϴ�.",
							"���",JOptionPane.WARNING_MESSAGE);
					return;
				}
				String filePath = chooser.getSelectedFile().getPath();
				if(music!=null)
					music.stop();
				new XmlWritingToolsFrame(filePath);
				dispose();
			}
		});
		
		bgmBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				if(music.checkMusicState()) {
					music.stop();
					music.setMusicState(false);
				}
				else
					music.play();
			}	
		});
		gameBgBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectImageDialog selectImageFrame = new SelectImageDialog(gameBgObj, 
						((JButton)e.getSource()).getX()+frame.getX(),55+frame.getY());			
			}
		});
		removeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				if(groundPanel.getRemoveClick()) {
					groundPanel.setRemoveClick(false);
					((JButton)e.getSource()).setBackground(null);
				}
				else {
					groundPanel.setRemoveClick(true);
					((JButton)e.getSource()).setBackground(Color.lightGray);
				}
			}	
		});
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				String check = checkAllObj();
				if(check.equals(" ")) {
					fileDialog.setVisible(true);
					
					fileName = fileDialog.getFileName();
					
					if(fileName==null)
						return;
					
					writingXml();
					JOptionPane.showMessageDialog(groundPanel,"����Ǿ����ϴ�",
							"Save",JOptionPane.INFORMATION_MESSAGE);
				}
				else
					JOptionPane.showMessageDialog(groundPanel,check+"�� ����� �ۼ����� �ʾҽ��ϴ�",
							"Dont Save",JOptionPane.ERROR_MESSAGE);
			}	
		});
	}
	/**
	 * ������ �� �г��� ���� ���ۿ� �ʿ��� ������ �� �Ǿ����� Ȯ���ϴ� �Լ�
	 * @return �ʿ��� ������ ��� �Ǿ������� " " �ƴϸ� ������ �������� ���� �г� ���ڿ� ����
	 */
	private String checkAllObj() {
		if(gameBgObj.getObj() == null)
			return "BackGround";
		if(basicPanel.checkBasicXml())
			return "Basic";
		if(musicToolsPanel.checkMusicXml())
			return "BGM";
		if(initToolsPanel.checkInitXml())
			return "Init";	
		return " ";
	}
	/**
	 * �� �г��� ����ϴ� �κ��� xml ���Ͽ� �ۼ��ϴ� �Լ�
	 */
	private void writingXml() {
		file = new File(fileName);
		try{
            //���� ��ü ����
            
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            
            if(file.isFile() && file.canWrite()){
                //����
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n"
                		+ "\r\n"
                		+ "<BlockGame>\r\n"
                		+ "    <Screen>\r\n"
                		+ "        <Size w=\""+width+"\" h=\""+height+"\"/>\r\n"
                		+ "    </Screen>\r\n");
                bufferedWriter.close();
            }
        }catch (IOException e) {
            return;
        }
		initToolsPanel.xmlInitWriting(file);
		musicToolsPanel.xmlMusicWriting(file);
		basicPanel.xmlBasicWriting(gameBgObj, file);
		groundPanel.xmlBlockWriting(file);
	}
	/**
	 * ����ȭ�� �г� ����
	 * @author ����
	 */
	private class BackGroundPanel extends JPanel {
		public BackGroundPanel() {
			this.setBackground(Color.gray);
		}
	}
	/**
	 * main
	 * @param args args
	 */
	public static void main(String [] args) {
		new XmlWritingToolsFrame();
	}
}
/**
 * ���ڸ� �Է��� �� �ִ� JTextField
 * 
 * @author ����
 */
class NumberField extends JTextField implements KeyListener {
	private static final long serialVersionUID = 1;
	/**
	 * NumberField�� ������
	 */
	public NumberField() {
		super();
		super.setText("0");
		super.setHorizontalAlignment(JTextField.CENTER);
		addKeyListener(this);
	}
	/**
	 * NumberField�� ������
	 * @param i JTextField(int i) �� i�� ������ ����
	 */
	public NumberField(int i) {
		super(i);
		super.setText("0");
		super.setHorizontalAlignment(JTextField.CENTER);
		addKeyListener(this);
	}
	/**
	 * TextField �ȿ� text�� ��ȯ�ϴ� �Լ�
	 * @param text ��ȯ�ϰ��� �ϴ� ������ ����
	 */
	public void setText(int text) {
		super.setText(Integer.toString(text));
	}
	/**
	 * X
	 */
	public void keyPressed(KeyEvent e) {}
	/**
	 *  X
	 */
	public void keyReleased(KeyEvent e) {}
	/**
	 * key�� Ÿ���� �� �� �ִ� ���� ���ڷθ� �����ϴ� �Լ�
	 */
	public void keyTyped(KeyEvent e) {
		 char c = e.getKeyChar();
		 
		 if (!Character.isDigit(c)) {
			 e.consume();
			 return;
		 }
	 }
	/**
	 * JTextField�� ������ �ִ� ���� �����ϴ� �Լ�
	 * @return ���� ���ڷ� ��ȯ���� ����
	 */
	 public int getNumber() {
		 return Integer.parseInt(super.getText());
	 }
}