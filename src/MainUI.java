
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.List;

import javax.swing.JList;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.SystemColor;

import javax.swing.JButton;

import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainUI extends JFrame {

	private JPanel contentPane;
	private String selectedFriend;   //选中的好友的学号
	private List list = new List();    //列表
	private JButton button = new JButton("\u767B\u51FA");   //登出按钮
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public MainUI() throws IOException {
		super("欢迎");  //这句不要动，貌似只能放在这里		
		init();  //默认是对的初始化			
		getList();  //得到列表的内容
		buttonAction();  //按下登出按钮的事件		
		closeWindow();  //点击小红叉的相应事件
	}
	
	
	/**点击小红叉的相应事件*/
	public void closeWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				/**
				//添加点击小红叉响应事件在这里
				 * 
				 */	
			}
		});
	}
	
	/**按下登出按钮的事件*/
	public void buttonAction() {
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {     //登出响应事件
				/**
				//添加按登出响应事件在这里
				 * 
				 */				
			}
		});
	}
	
	
	/**得到列表的内容*/
	public void getList() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("S.txt"));  //读文本，文本的名字是S.txt,"\"比较特殊，
                                                                               //所以不是123.txt.
        String data = br.readLine();  //从S。txt里面读出来的一行
		while( data!=null){    //while用于把列表内容从文本里面全读出来
		      list.add(data);      //加到列表里面
		      data = br.readLine(); //接着读下一行  
		}	
	}
	
	
	/**默认是对的初始化*/
	public void init(){                      
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 321, 318);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){               //设置双击事件
					selectedFriend = list.getSelectedItem();   //可以得到双击后选中的好友的学号
					System.out.println(selectedFriend);
				}
			}
		});		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setText("\u597D\u53CB\u5217\u8868");
		editorPane.setBackground(SystemColor.menu);
		editorPane.setEditable(false);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(list, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
					.addComponent(button)
					.addGap(41))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(33)
					.addComponent(editorPane, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(203, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(button)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(editorPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(list, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
