
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import javax.swing.*;
 
public class Server180 extends JFrame {
 
   private int board[];
   private boolean xMove;
   private JTextArea output;
   private Player players[];
   private ServerSocket server;
   private int currentPlayer;
   private String command;
   private String question;
   private TimerTask timerTask;
   private boolean tick = false;
		  private Timer timer;
		private int counter=10;
		private int gameNumber=0;
		
   
   
   public Server180()
   {
      super( "Server 180" );
 
      
      xMove = true;
      players = new Player[ 2 ];
      currentPlayer = 0;
  
      // set up ServerSocket
      try {
         server = new ServerSocket( 5000, 2 );
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
 
      output = new JTextArea();
      getContentPane().add( output, BorderLayout.CENTER );
      output.setText( "Server awaiting connections\n" );
 
      setSize( 300, 300 );
      show();
      
      timerTask = new TimerTask() {
			 
          @Override
          public void run() {
             if(tick){
              counter--;//increments the counter
              players[currentPlayer].send(""+counter);
              if(counter <= 0 ){
            	  tick = false;
            	  endTurn();
            	 	
              	}
           
             
              }
          }
      };

      timer = new Timer("MyTimer");
      timer.scheduleAtFixedRate(timerTask,0, 1000);
   }
   
   public void startTick(){
	   if(gameNumber==0){
		   players[0].send("OPPN1"+players[1].getN())	;
		   players[1].send("OPPN0"+players[0].getN())	;
	   }
	   pushQ();
	   gameNumber+=1;
	   tick = true;
	   counter = 60;
	   output.append("s");
	   output.append(question + "\n");
   }
   
   public void endTurn(){
	   tick = false;
	   
	   
	   if(gameNumber%2==0){
		   	
	       players[currentPlayer].send("SHOW");	
	       
	       
	   }else{
	   players[currentPlayer].send("FALSE");	
       players[currentPlayer].send("SHOW");	
       
       currentPlayer = ( currentPlayer + 1 ) % 2;
       players[currentPlayer].send("TRUE");
       players[currentPlayer].send("SHOW");	
       tick = false;
	   }
       if(gameNumber%2==0){
    	   tick = false;
    	   
    	   if(players[0].getT()>players[1].getT()){
    		   players[0].setScore(players[0].getScore()+1);
    		   players[0].send("XXX"+players[0].getScore());
    		   players[1].send("XXX"+players[0].getScore());
    		   
    	   }else if(players[0].getT()==players[1].getT()){
    		   players[0].send("DDD"+players[0].getScore());
    		   players[1].send("DDD"+players[0].getScore());
    	   }
    	   
    	   else{
    		   players[1].setScore(players[1].getScore()+1);
    		   players[0].send("OOO"+players[1].getScore());
    		   players[1].send("OOO"+players[1].getScore());
    	   }
    	   
    	   
       }
   }
  
   public void pushQ(){
	   if(gameNumber%2==0){
		   setQ();
		   getQ();
		  
		   }
	   players[currentPlayer].send("QQ"+question);
   }
 
   // wait for two connections so game can be played
   public void execute()
   {
      for ( int i = 0; i < players.length; i++ ) {
         try {
            players[ i ] =
               new Player( server.accept(), this, i );
            players[ i ].start();
         }
         catch( IOException e ) {
            e.printStackTrace();
            System.exit( 1 );
         }
      }
 
      // Player X is suspended until Player O connects.
      // Resume player X now.          
      synchronized ( players[ 0 ] ) {
         players[ 0 ].threadSuspended = false;   
         players[ 0 ].notify();
      }
   
   }
    
   public void display( String s )
   {
      output.append( s + "\n" );
   }
  
   public void setQ(){
	   question = "";
	   for(int i=0;i<5;i++){
		   question += ((int)(Math.random()*9)+1);
	   }
	   question += ((int)(Math.random()*30)+1);
	  
   }
   public String getQ(){
	   return question;
   }
   
   // Determine if a move is valid.
   // This method is synchronized because only one move can be
   // made at a time.
   

   public synchronized boolean validMove( String loc,
                                          int player )
   {
      boolean moveDone = false;
 
      while ( player != currentPlayer ) {
         try {
            wait();
         }
         catch( InterruptedException e ) {
            e.printStackTrace();
         }
      }
 
      if ( true) {
     
         currentPlayer = ( currentPlayer + 1 ) % 2;
         players[ currentPlayer ].otherPlayerMoved( 1 );
         notify();    // tell waiting player to continue
         return true;
         
      }
      else
         return false;
   }
 
   
   
 
   
 
   public boolean gameOver()
   {
      
      return false;
   }
 
   public static void main( String args[] )
   {
      Server180 game = new Server180();
 
      game.addWindowListener( new WindowAdapter() {
        public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         }
      );
 
      game.execute();
   }
   public void TickStop(){
	   tick = false;
   }

public int getCounter() {
	
	return counter;
}

public void Tick(Player n) {
	if(players[currentPlayer]==n){
		tick =true;
		counter = 60;
	}
	
	
}
}
 
// Player class to manage each Player as a thread
class Player extends Thread {
   private Socket connection;
   private DataInputStream input;
   private DataOutputStream output;
   private Server180 control;
   private int number;
   private char mark;
   private String name;
   protected boolean threadSuspended = true;
   private int timeLeft=0;
   private int score=0;
   private boolean ready;
   
   public Player( Socket s, Server180 t, int num )
   {
      mark = ( num == 0 ? 'X' : 'O' );
 
      connection = s;
       
      try {
         input = new DataInputStream(
                    connection.getInputStream() );
         output = new DataOutputStream(
                    connection.getOutputStream() );
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
 
      control = t;
      number = num;
   }
   public int getT(){
	   return timeLeft;
   }
   public boolean isReady(){
	   return ready;
   }
   public int getScore(){
	   return this.score;
   }
   public String getN(){
	   return this.name;
   }
   public void setScore(int s){
	   this.score = s;
   }
   public void otherPlayerMoved( int score )
   {
      try {
         output.writeUTF( "Opponent moved" );
         output.writeInt( score );
      }
      catch ( IOException e ) { e.printStackTrace(); }
   }
   
   public void send(String s){
	   try {
		output.writeUTF(s);
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   
   public void run()
   {
      boolean done = false;
 
      try {
         control.display( "Player " +
            ( number == 0 ? '1' : '2' ) + " connected" );
         output.writeChar( mark );
        
      
         
         // wait for another player to arrive
         if ( mark == 'X' ) {
  
        	 
        	
            try {
               synchronized( this ) {   
                  while ( threadSuspended )
                     wait();  
               }
            } 
            catch ( InterruptedException e ) {
               e.printStackTrace();
            }
 
 
           

           
         }else{
        	  
         }
         control.pushQ();
         output.writeUTF("SHOW");
         // Play game
        
        
         
         while ( !done ) {
        	 
            String res = input.readUTF();
            if(res == "BUSY"){
            	control.TickStop();
           
            }else if(res.equals("SHOW")){
            	
            	output.writeUTF(res);
            }else if(res.equals("OPPEND")){
            	output.writeUTF(res);
            }else if(res.equals("END")){
            	this.timeLeft= control.getCounter();
            	control.endTurn();
            }else if(res.equals("READY")){	
            	
            	this.ready = true;
            	
            	control.Tick(this);
            	
            }
            
            else if(res.equals("START")){
            	control.startTick();
            }else if(res.equals("SET")){
            	control.setQ();
            	
            }
            else if(res.startsWith("NNMM")){
            	this.name = res.substring(4);
            }
            
            	
         
          //  if ( control.gameOver() )
            //   done = true;
         }         
 
         connection.close();
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
   }
}        


