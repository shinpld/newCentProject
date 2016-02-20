import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;



import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;














import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class frameV3 extends JFrame  implements Runnable{

	private JPanel contentPane;
	JLabel inputField;
	JLabel ansField;
	String fieldValue="";
	//Timer timer;
	private int counter = 11;
	   private DataInputStream input;
	   private DataOutputStream output;
	
	 
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private ObjectInputStream inStream = null;
	private ObjectOutputStream outStream = null;
	private boolean flag = false;
	private boolean whoseturn = false;
	private JTextField port_field;
	private JTextField ip_field;
	private JTextField name_field;
	private boolean tick = false;
	private boolean symbol = false;
	private String safeSing = "SWAG";
	private String message;
	private JLabel player1;
	private JLabel player2;
	private JLabel hostTurn;
	private JButton button1;
	private JButton button2;
	private JButton button3;
	private JButton button4;
	private JButton button5;
	private JButton parathesis_left;
	private JButton parathesis_right;
	private  JButton plus;
	private JButton minus;
	private JButton divide;
	private JButton multiply;
	private JButton host;
	private JButton join;
	private JButton submit;
	private String p1name = "Player 1 :";
	private String p2name = "Player 2 :";
	private String TimeLeft = "Time Left :";
	private String tt = "'s turn";
	private String target;
	private String answer;
	private JLabel time;
	JButton[] buttons = new JButton[5];
	private JButton conn;
	private JTextArea nameArea;
	private boolean submitted = true;
	private JTextArea portArea ;
	private JTextArea ipField;
	private boolean first = true;
	private BufferedReader in;
	private PrintWriter out;
	static frameV3 frame;
	
	private JLabel messageLabel;
	private final String S_ADDR = "localhost";
	private Thread outputThread;
	private Socket connection;
	public int[] question = new int[5];
	private char myMark;
	private boolean myTurn;
	private boolean done ;
	JLabel resultP1;
	JLabel resultP2;
	ScriptEngineManager mgr;
	//private TimerTask timerTask;
	

	public void start()
	   {
	      try {
	         connection = new Socket(
	            InetAddress.getByName( "127.0.0.1" ), 5000 );
	         input = new DataInputStream(
	                        connection.getInputStream() );
	         output = new DataOutputStream(
	                        connection.getOutputStream() );
	      }
	      catch ( IOException e ) {
	         e.printStackTrace();         
	      }
	 
	      outputThread = new Thread(this );
	      outputThread.start();
	   }
	
	public void run()
	   {
		
		
	      // First get player's mark (X or O)
	      try {
	         myMark = input.readChar();
	         if(myMark == 'X'){
	        	 p1name = nameArea.getText();
	        	 player1.setText("player 1 : " + p1name);
	        	 output.writeUTF("NNMM"+p1name);
	        	 
	         }else{
	        	 p2name = nameArea.getText();
	        	 player2.setText("player 2 : " + p2name);
	        	output.writeUTF("NNMM"+p2name);
	         }
	        	 
	         
	         myTurn = ( myMark == 'X' ? true  : false );
	    
	      }
	      catch ( IOException e ) {
	         e.printStackTrace();         
	      }
	 
	      // Receive messages sent to client
	      while ( true ) {
	         try {
	            String s = input.readUTF();
	            processMessage( s );
	         }
	         catch ( IOException e ) {
	            e.printStackTrace();         
	         }
	      }
	   }
	
	
	public void processMessage( String s ) throws IOException
	   {
	      if ( s.equals( "Valid move." ) ) {
	        myTurn = false;
	      }
	      else if ( s.equals( "Invalid move, try again" ) ) {
	       
	         myTurn = true;
	      }
	   
	  

	      else if ( s.equals( "Opponent moved" ) ) {
	          //display.append(
			  //  "Opponent moved. Your turn.\n" );
			 myTurn = true;
	       }
	      else if(s.equals("END")){
	    	  fieldValue="";
	    	  answer="";
	    	  for(int i=0;i<buttons.length;i++){
          		buttons[i].setEnabled(false);
          		
          	}
	      }
	      
	      
	      else if(s.equals("SHOW")){
	    	  
	    		  if(myTurn==false){
	    			  hostTurn.setText("Opponent's Turn");
	    		  }else{
	    			  hostTurn.setText("Your Turn");
	    		  }
	    	  startTurn();
	    	 
	    	 
	      }
	      else if ( s.equals( "Opponent moved" ) ) {
	         
	             
	             myTurn = true;
	          
	          
	       }
	     
	      else if(s.equals("TRUE")){
	    	  myTurn=true;
          	
          } 
	      else if(s.equals("FALSE")){
	    	  myTurn=false;
          	
          }
	      
	      else if(s.startsWith("QQ")){
	    	  setQuestion(s.substring(2));
	    	  
	      }
	      else if(s.startsWith("XXX")){
	    	  resultP1.setText(s.substring(3));
	    	  JOptionPane.showMessageDialog(frame,p1name +" wins \n" + "Current Score : " + 
	    	 p1name+ resultP1.getText()+" - "+resultP2.getText() +p2name);
	    	  if (JOptionPane.OK_OPTION == 0){
	    		  output.writeUTF("BUSY");
	    		  output.writeUTF("READY");
	    	  }
	      }else if(s.startsWith("OOO")){
	    	  resultP2.setText(s.substring(3));
	    	  JOptionPane.showMessageDialog(frame,p2name +" wins \n" + "Current Score : " + 
	    	    	  p1name+ resultP1.getText()+" - "+resultP2.getText() +p2name);
	    	  if (JOptionPane.OK_OPTION == 0){
	    		  output.writeUTF("BUSY");
	    		  output.writeUTF("READY");
	    	  }
	      }    else if(s.startsWith("DDD")){
	    	  
	    	  JOptionPane.showMessageDialog(frame,"DRAW !! \n" + "Current Score : " + 
	    	    	  p1name+ resultP1.getText()+" - "+resultP2.getText() +p2name);
	    	  if (JOptionPane.OK_OPTION == 0){
	    		  output.writeUTF("BUSY");
	    		  output.writeUTF("READY");
	    	  }
	      }
	      
	      else if(s.startsWith("OPPN0")){
	    	  p1name=s.substring(5);
	    	  player1.setText("Player1: "+p1name);
	      }else if(s.startsWith("OPPN1")){
	    	  p2name=s.substring(5);
	    	  player2.setText("Player2: "+p2name);
	      }
	      
	      else{
	    	  time.setText("Time left: " + s);
	      }
	      
	      
	      
	   }
	
	
	
		private void Clear()
	{
		try { outStream.flush(); 		} catch (Exception e) { }
		try { outStream.close(); 		} catch (Exception e) { }
		try { inStream.close(); 		} catch (Exception e) { }
		try { serverSocket.close();	} catch (Exception e) { }
		try { socket.close(); 	} catch (Exception e) { }
	} 
	
	public void generateAnswer(){
		answer = ""+(int)(Math.random()*30);
	}
	
	
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					 frame = new frameV3();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				frame.run();
				
				
			}
			
		});
		
	}

	/**
	 * Create the frame.
	 */
 	public frameV3() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		player1 = new JLabel(p1name);
		player1.setFont(new Font("Angsana New", Font.BOLD, 20));
		player1.setBounds(153, 120, 200, 40);
		contentPane.add(player1);
		
		player2 = new JLabel(p2name);
		player2.setFont(new Font("Angsana New", Font.BOLD, 20));
		player2.setBounds(437, 120, 200, 40);
		contentPane.add(player2);
		
		 hostTurn = new JLabel(tt);
		hostTurn.setFont(new Font("Angsana New", Font.PLAIN, 26));
		hostTurn.setBounds(99, 160, 100, 40);
		contentPane.add(hostTurn);
		
		resultP1 = new JLabel("O");
		resultP1.setFont(new Font("Angsana New", Font.PLAIN, 26));
		resultP1.setBounds(260, 160, 66, 40);
		contentPane.add(resultP1);
		resultP2 = new JLabel("O");
		resultP2.setFont(new Font("Angsana New", Font.PLAIN, 26));
		resultP2.setBounds(400, 160, 66, 40);
		contentPane.add(resultP2);

		 time = new JLabel(TimeLeft);
		time.setFont(new Font("Angsana New", Font.PLAIN, 20));
		time.setBounds(512, 160, 186, 40);
		contentPane.add(time);	
		
		nameArea = new JTextArea("Input Name");
		nameArea.setFont(new Font("Angsana New", Font.PLAIN, 20));
		nameArea.setBounds(60, 20, 400, 30);
		contentPane.add(nameArea);
	
		genButton();
		
		 conn = new JButton("Enter");
		conn.setFont(new Font("Angsana New", Font.PLAIN, 30));
		conn.setBounds(700,20 , 80, 50);
		contentPane.add(conn);
		conn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				conn.setEnabled(false);
				start();
			
				p1name = nameArea.getText();
				JOptionPane.showMessageDialog(frame, "Welcome " + p1name);
				
				
			}
		});
		
		
		
		generateAnswer();
		inputField = new JLabel();
		inputField.setFont(new Font("Angsana New", Font.PLAIN, 30));
		inputField.setBounds(99, 330, 295, 35);
		contentPane.add(inputField);
		
		portArea = new JTextArea();
		portArea.setFont(new Font("Angsana New", Font.PLAIN, 20));
		portArea.setBounds(60, 60, 60, 30);
		contentPane.add(portArea);
		
		ipField = new JTextArea();
		ipField.setFont(new Font("Angsana New", Font.PLAIN, 20));
		ipField.setBounds(160, 60, 300, 30);
		contentPane.add(ipField);
		
		
		JLabel equal = new JLabel("=");
		equal.setFont(new Font("Angsana New", Font.BOLD, 30));
		equal.setBounds(463, 330, 64, 38);
		contentPane.add(equal);
		
		ansField = new JLabel("");
		ansField.setFont(new Font("Angsana New", Font.PLAIN, 30));
		ansField.setBounds(536, 330, 92, 35);
		contentPane.add(ansField);
		ansField.setVisible(false);
		 plus = new JButton("+");
		plus.setFont(new Font("Angsana New", Font.BOLD, 30));
		plus.setBounds(60, 400, 60, 40);
		contentPane.add(plus);
		
		plus.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(symbol)
				{
				symbol = false;
				fieldValue += plus.getText();
				inputField.setText(fieldValue);
				}
			}
		});
		parathesis_left = new JButton("(");
		parathesis_left.setBounds(460, 400, 60, 40);
		parathesis_left.setFont(new Font("Angsana New", Font.BOLD, 30));
		contentPane.add(parathesis_left);
		parathesis_left.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fieldValue += parathesis_left.getText();
				inputField.setText(fieldValue);
			}
		});
		parathesis_right = new JButton(")");
		parathesis_right.setBounds(560, 400, 60, 40);
		parathesis_right.setFont(new Font("Angsana New", Font.BOLD, 30));
		contentPane.add(parathesis_right);
		parathesis_right.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fieldValue += parathesis_right.getText();
				inputField.setText(fieldValue);
			}
		});
		 minus = new JButton("-");
		minus.setFont(new Font("Angsana New", Font.BOLD, 30));
		minus.setBounds(160, 400, 60, 40);
		contentPane.add(minus);
		
		minus.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(symbol)
				{
				symbol = false;
				fieldValue += minus.getText();
				inputField.setText(fieldValue);
				}
			}
		});
		
		multiply = new JButton("*");
		minus.setFont(new Font("Angsana New", Font.BOLD, 30));
		multiply.setBounds(260, 400, 60, 40);
		contentPane.add(multiply);
		
		
		
		multiply.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(symbol)
				{
				symbol = false;
				fieldValue += multiply.getText();
				inputField.setText(fieldValue);
				}
			}
		});
		
		 divide = new JButton("/");
		divide.setFont(new Font("Angsana New", Font.BOLD, 30));
		divide.setBounds(360, 400, 60, 40);
		contentPane.add(divide);
		
		divide.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(symbol)
				{
				symbol = false;
				fieldValue += divide.getText();
				inputField.setText(fieldValue);
				} 
			}
		});
		/*timerTask = new TimerTask() {
			 
            @Override
            public void run() {
               if(tick){
                counter--;//increments the counter
                time.setText(TimeLeft+counter);
                if(counter <= 0 ){
                	for(int i=0;i<buttons.length;i++){
                		buttons[i].setEnabled(false);
                		
                	}
                	
                		
						if ( myTurn )
							
							try {
								
								 output.writeUTF( "SHOW" );
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					
               
                }
            }}
        };
        
        timer = new Timer("MyTimer");
		*/
		submit = new JButton("Submit/Clear");
		submit.setFont(new Font("Angsana New", Font.BOLD, 30));
		submit.setBounds(650, 330, 140, 40);
		contentPane.add(submit);
		
		
		
		submit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event) 
			{
				String ans = inputField.getText();
					
				 mgr = new ScriptEngineManager();
					    ScriptEngine engine = mgr.getEngineByName("JavaScript");
					   try {
						   
						  
						if(AllUsed()){
							
							 
						   if(engine.eval(ans).toString().equals(engine.eval(answer).toString())){ 
							   
							
							  submit.setEnabled(false);
							  	
								output.writeUTF("END");
					
						   }
						   else{
							   
							clear();
						
							
							
						   }
						} else clear();
						
						
					} catch (ScriptException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
			}
		});
		
		
		
 	}
 		
 	public boolean checkAnswer(String S)
 		{
 			int paracount = 0;
 			for(int i = 0; i < S.length(); i++)
 			{
 				if(S.charAt(0) == ('(') || S.charAt(0) == ')')
 				{
 					paracount++;
 				}
 			}
 			if(paracount%2 == 0)
 			{
 				return true;
 			}
 			else
 			{
 				return false;
 			}
 			
 		}
 	

 	
 	
 	public void clear(){
 		for(int i=0;i<buttons.length;i++){
 			buttons[i].setEnabled(true);
 		}
 		inputField.setText("");
 		fieldValue="";
 		symbol = false;
 	}
 	
 	public void startTick() throws IOException{
 		
 		 output.writeUTF("START");
 		 

	        
	        
	       
 	}
 	
 	public void stopTick(){
 		tick = false;
 		
 	}
 	
 	public void startTurn() throws IOException{
 		if(myTurn){
 			
 		JOptionPane.showMessageDialog(frame, "Your turn, Click OK to start");
			if (JOptionPane.OK_OPTION == 0) {
				 output.writeUTF("START");
				 
				ansField.setVisible(true);
				
				for(int i=0;i<buttons.length;i++){
					buttons[i].setVisible(true);
					buttons[i].setEnabled(true);
				}
			}
			ansField.setVisible(true);
			
 		}else{
 			for(int i=0;i<buttons.length;i++){
				buttons[i].setVisible(false);
				buttons[i].setEnabled(false);
			}
 			
 		}
	  
  
 	}
 	public boolean AllUsed(){
 		for(int i=0;i<buttons.length;i++){
 			if (buttons[i].isEnabled()==true ) return false;
 			
 		}return true;
 	}
 	public void genButton(){
 		
 		for(int i=0;i<buttons.length;i++){
			buttons[i] = new JButton(""+ ((int)(Math.random()*9)+1));
			buttons[i].setFont(new Font("Angsana New", Font.PLAIN, 30));
			buttons[i].setBounds(60+70*i, 240, 50, 50);
			contentPane.add(buttons[i]);
			buttons[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(symbol == false)
					{
						symbol = true;
						((JButton) e.getSource()).setEnabled(false);
					fieldValue += ((JButton) e.getSource()).getText();
					inputField.setText(fieldValue);
					
					}
				}
			});
			buttons[i].setVisible(false);
		}
 	}
 	

 	public void setQuestion(String s){
 		for(int i=0;i<buttons.length;i++){
 			buttons[i].setText(""+s.charAt(i));
 		}
 		answer=s.substring(5);
 		ansField.setText(answer);
 		submit.setEnabled(true);
 	}

 	
 
 	
}







