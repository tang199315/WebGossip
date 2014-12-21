import java.io.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPasswordField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JTextPane;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.UIManager;
import javax.swing.JEditorPane;

public class Login extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JPasswordField textField_3;
	private JButton button;      //登录按钮
	private String userName;     //按下登陆之后得到的用户名
	private String password;     //按下登陆之后得到的密码
	private boolean waitingJudge = false;
	private JEditorPane textField_4;

	public Login() {
		setBackground(Color.GRAY);
		init();   //你默认是对的初始化
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		
		//Window Closing handler
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				System.exit(1);
			}
		});
	}
	
	
	/**返回waitingJudge的值，如果是false，就是输入有问题，反之*/
	public boolean getJudge(){
		return (waitingJudge);
	}
	
	public void showError(){
		textField_4.setText("用户名或密码错误");
	}
	
	public String getUsername() throws UnsupportedEncodingException {
		return userName;
//		byte[] bs = userName.getBytes();
//		return new String(bs,"US-ASCII");
	}
	
	public String getPWD() throws UnsupportedEncodingException {
		return password;
//		byte[] bs = password.getBytes();
//		return new String(bs,"US-ASCII");
	}
	
	public void reset(){
		waitingJudge = false;
		textField.setText(""); //clear
		textField_3.setText("");
		//TODO
		
	}
	
	//GUI init
	public void init(){
		setTitle("\u767B\u5F55\u754C\u9762");
		setBounds(100, 100, 301, 235);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setForeground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		{
			textField = new JTextField();
			textField.setColumns(10);
		}
		
		//initial of four JTextFields
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		textField_1.setText("\u5B66\u53F7");
		textField_2 = new JTextField();
		textField_2.setEditable(false);
		textField_2.setColumns(10);
		textField_2.setText("\u5BC6\u7801");
		textField_3 = new JPasswordField();
		textField_3.setColumns(10);		
		textField_4 = new JEditorPane();
		textField_4.setBackground(SystemColor.menu);
		textField_4.setEditable(false);
		textField_4.setText("请输入用户名和密码");
		button = new JButton("\u767B\u5F55"); 
		button.addActionListener(new ActionListener() {    //按钮相应事件，读取用户名和密码
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				userName = textField.getText();
				password = textField_3.getText();
				waitingJudge = true;
				
				//TODO: Delete the following lines before production
				userName="2011011437";
				password = "net2014";
			}
		});	
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(46)
					.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(83)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(46)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(textField_4, Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(18)
					.addComponent(textField_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(27)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(button))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}
	}

}
