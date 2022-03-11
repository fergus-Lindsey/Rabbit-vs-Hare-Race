/**
 * @(#)HomeAssignment4.java
 * @author Lindsey Ferguson
 * @version 1.00 2022/2/14
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.*; 
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.io.*; 
import javax.imageio.*;

public class HomeAssignment4 extends JFrame{
	private Timer turtleTimer,rabbitTimer,sleepTimer;
	private int turtleDelay=60,rabbitDelay=60,tick =0,sleepAmountMS=1000;
	private Game game;
	
    public HomeAssignment4(){
    	super("Tortise and the Hare Race");    	
		game = new Game(this);
		add(game);
    	turtleTimer = new Timer(turtleDelay,new turtleTickListener());
		rabbitTimer = new Timer(rabbitDelay,new rabbitTickListener());	
		sleepTimer = new Timer(1,new sleepTickListener());	
		turtleTimer.start();
		rabbitTimer.start();	
    }
    
    //getters and setters to get values from game
    //the delay the user can set using JTextField
	public void setTurtleDelay(int delay){
		turtleDelay = delay;
		turtleTimer.setDelay((Math.abs(turtleDelay-1000)));
	}
	public int getTurtleDelay(){
		return turtleDelay;
	}
	
	public void setRabbitDelay(int delay){
		rabbitDelay = delay;
		rabbitTimer.setDelay((Math.abs(rabbitDelay-1000)));
	}
	public int getRabbitDelay(){
		return rabbitDelay;
	}
	
	public void setSleepAmountMS(int ms){
		sleepAmountMS = ms;
	}
	public int getSleepAmountMS(){
		return sleepAmountMS;
	}
	
	//turtle ticklistener
    class turtleTickListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			game.repaint();
			if(game.getStart()==true){
				game.turtleMove();
			}
		}			
	}
	//rabbit ticklistener
	class rabbitTickListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			game.repaint();
			if(game.getStart()==true){
				game.rabbitMove();	
			}
			if(game.getHitPoint() == true){
				tick =0;
				sleepTimer.start();	
			}
		}			
	}
	//sleep listener, did this to keep MS consistant instead
	//of doing it directly in rabbitTickListener, as the value can change 
	//depending on user.
	class sleepTickListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			tick++;
			if(game.getHitPoint() == true){
				rabbitTimer.stop();
				if(tick ==(sleepAmountMS/10)){
					game.setHitPoint(false);
					rabbitTimer.start();
					sleepTimer.stop();
				}
			}
		}			
	}
	
    public static void main(String[] args){
    	HomeAssignment4 frame = new HomeAssignment4();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      	frame.setSize(510, 450); 
      	frame.setVisible(true);
      	frame.setResizable(false);
    }
}

class Game extends JPanel{
	private HomeAssignment4 parent; //allows me to access methods in parent JFrame
	private JPanel menuPanel,menuPanelFormat,menuPanelContents,tallyPanel; 
	private JLabel turtleSpeedLabel,rabbitSpeedLabel,rabbitSleepLabel,rabbitSleepTimeLabel,rabbitLabel,turtleLabel,tallyLabel;
	private JTextField turtleSpeedText,rabbitSpeedText,rabbitSleepTimeText;
	private String[] sleepOptions= {"Zero","One","Two"};
	private JComboBox<String> sleepOptionComboBox = new JComboBox<String>(sleepOptions); 
	private ArrayList<Point> sleepPoints = new ArrayList<Point>(); 
	private JButton startRace,restartRace; 
	private boolean start = false,turtleWin = false,rabbitWin=false,hitPoint=false;
	private Turtle turtle;
	private Rabbit rabbit;
	private int turtleWins=0,rabbitWins=0,clicks=0,sleepNum=0; 
	private BufferedImage backgroundLAYER;
	
	public Game(HomeAssignment4 home){
		parent = home;

		try{//gets my background pic, had to do this so later i can use .getRGB()
	  		backgroundLAYER = ImageIO.read(new File("pictures/background.png"));
	  	}
	  	catch (IOException e){
			System.out.println(e);
	    }
		setLayout(new BorderLayout());	
		//jpanels													
		menuPanel = new JPanel(new GridLayout(1,2));
		menuPanelFormat = new JPanel(new BorderLayout());
		menuPanelContents = new JPanel(new GridLayout(5,2,3,1));
		menuPanelContents.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));//added a border to match the jtext boxes
		
		//creates a little tally section keeping track of wins
		tallyPanel = new JPanel(){//so it paints on top of the panel and not behind it 
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(5,5,75,110);
				g.setColor(new Color(169,169,169));
				g.drawRect(5,5,75,110);
				g.setColor(Color.BLACK);
				g.drawLine(20,22,62,22);
				g.drawLine(41,30,41,100);
				g.drawLine(5,42,80,42);
				
				//tally update //prob better way to do this
				if(turtleWins<=4){
					for(int i=0;i<turtleWins;i++){
						g.drawLine(8+(i*4),45,8+(i*4),60);
					}
				}
				if(turtleWins ==5){
					for(int i=0;i<turtleWins;i++){
						g.drawLine(8+(i*4),45,8+(i*4),60);
					}
					g.drawLine(8,42,28,40);//diagonal 5th line 
				}
				
				if(rabbitWins<=4){
					for(int i=0;i<rabbitWins;i++){
						g.drawLine(45+(i*4),45,45+(i*4),60);
					}
				}
				if(rabbitWins ==5){
					for(int i=0;i<rabbitWins;i++){
						g.drawLine(45+(i*4),45,45+(i*4),60);
					}
					g.drawLine(45,42,65,40);//diagonal 5th line 
				}				
			}
		};
		
		//LABELS
		turtleSpeedLabel = new JLabel("Turtle Speed (M/S): ");
		rabbitSpeedLabel = new JLabel("Rabbit Speed (M/S): ");
		rabbitSleepLabel = new JLabel("Rabbit Sleep #:");
		rabbitSleepTimeLabel = new JLabel("Sleep Time (M/S): ");
		tallyLabel = new JLabel("SCORE");
		rabbitLabel = new JLabel("Rabbit");
		turtleLabel = new JLabel("Turtle");
		//label fonts
		rabbitLabel.setFont(new Font("Dialog", Font.BOLD,11));//made font slightly smaller to fit better
		turtleLabel.setFont(new Font("Dialog", Font.BOLD,11));
	
		//JTEXTFIELD
		turtleSpeedText = new JTextField(10);
		rabbitSpeedText = new JTextField(10);
		rabbitSleepTimeText = new JTextField(10);
		//text handler
		TextFieldHandler textHandler = new TextFieldHandler();
		turtleSpeedText.addActionListener(textHandler);
		rabbitSpeedText.addActionListener(textHandler);
		rabbitSleepTimeText.addActionListener(textHandler);
		
		//BUTTONS
		startRace = new JButton("START");
		restartRace = new JButton("RESTART");
		//button handler
		ButtonHandler buttonHandler = new ButtonHandler();
		startRace.addActionListener(buttonHandler);
		restartRace.addActionListener(buttonHandler);
		
		//MENU PANEL FORMATTING
		menuPanel.setOpaque(false);//making these panels transparent to format the control panel for spacing
		menuPanelFormat.setOpaque(false);
		
		//tally chart formatting
		tallyPanel.setBackground(new Color(232,211,185));
		tallyPanel.setPreferredSize(new Dimension(84,120));
		tallyPanel.add(tallyLabel,BorderLayout.NORTH);
		tallyPanel.add(turtleLabel,BorderLayout.EAST);
		tallyPanel.add(rabbitLabel,BorderLayout.EAST);
		tallyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
		menuPanelFormat.add(tallyPanel,BorderLayout.EAST); 
		
		menuPanelContents.setBackground(new Color(232,211,185));
		menuPanelContents.setPreferredSize(new Dimension(55,120));
		menuPanel.add(menuPanelContents);
		menuPanel.add(menuPanelFormat);
		
		//CONTROL PANEL FORMATTING
		//turtle speed
		menuPanelContents.add(turtleSpeedLabel);
		menuPanelContents.add(turtleSpeedText);
		//rabbit speed
		menuPanelContents.add(rabbitSpeedLabel);
		menuPanelContents.add(rabbitSpeedText);
		//rabbit sleep comboBox
		menuPanelContents.add(rabbitSleepLabel);
		menuPanelContents.add(sleepOptionComboBox);
		//rabbit sleep time
		menuPanelContents.add(rabbitSleepTimeLabel);
		menuPanelContents.add(rabbitSleepTimeText);
		//buttons
		menuPanelContents.add(startRace);
		menuPanelContents.add(restartRace);
		
		//main
		add(menuPanel,BorderLayout.SOUTH);
		turtle = new Turtle(10,150);//adding one turtle and one rabbit
		rabbit = new Rabbit(10,120);//dont have to add component as they get painted on 
		
		//LISTENERS
		addMouseListener(
			new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent event){
			    	if(sleepNum == 1||sleepNum ==2){//if the rabbit sleeps, then get click
				    	Color path = new Color (112,77,25);//the colour of my race path
				    	Color clicked = new Color(backgroundLAYER.getRGB(event.getX(),event.getY()));//get the coloru of where the user clicked
				    	if(path.equals(clicked)){//if the path and where the user clicked are the same colour, that means the user clicked on the path, and not else where
				    		sleepPoints.add(event.getPoint());
							clicks+=1;
				    	}
				    	else{
				    		JOptionPane.showMessageDialog(parent, "Sleeping spots must be on the race path, and in range of rabbit! Try Again!");
				    	}
			   		}
				}		
			}
		);
			
		sleepOptionComboBox.addItemListener(//my combobox itemlistener, allows user to select from 3 options
			new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent event){
				if (event.getStateChange() == ItemEvent.SELECTED)
					System.out.println(sleepOptions[sleepOptionComboBox.getSelectedIndex()]);
					if(sleepOptions[sleepOptionComboBox.getSelectedIndex()] == "Zero"){
						sleepNum=0;
					}
					if(sleepOptions[sleepOptionComboBox.getSelectedIndex()]== "One"){
						sleepNum=1;
					}
					if(sleepOptions[sleepOptionComboBox.getSelectedIndex()]== "Two"){
						sleepNum=2;
					}
				} 
			}
		);
	}
	
	private class ButtonHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent event){//starts ticklisteners
			if(event.getActionCommand()== "START"){
				start= true;
			}
			if(event.getActionCommand()== "RESTART"){//restarts 
				start = false;
				turtle.restart(10,150);
				rabbit.restart(10,120);
				sleepPoints.clear();
				hitPoint = false;
				clicks=0;
			}
		}
	} 

	private class TextFieldHandler implements ActionListener{//texthandler
		@Override
		public void actionPerformed(ActionEvent event){
			if (event.getSource()==turtleSpeedText){
				int delay = Integer.parseInt(event.getActionCommand());//gets an int from user
				parent.setTurtleDelay(delay);//sets the delay in the parent clas (HomeAssignment4)
			}
			if (event.getSource()==rabbitSpeedText){
				int delay = Integer.parseInt(event.getActionCommand());
				parent.setRabbitDelay(delay);
			}
			if (event.getSource()==rabbitSleepTimeText){
				int ms = Integer.parseInt(event.getActionCommand());
				parent.setSleepAmountMS(ms);
			}
		}
    }	
	
	public boolean getStart(){//so i can start the tickListeners in the parent class
		return start;
	}
	
	public boolean getHitPoint(){//so i can start/stop the tickListeners in the parent class
		return hitPoint;
	}
	public void setHitPoint(boolean hit){//once hit and time has passed, in the tickListener i then set to false
		hitPoint= hit;
	}
	
	public void turtleMove(){
		turtle.move();//turtle moves
		winnerCheck();//sees if turtle or rabbit wins
	}	
	public void rabbitMove(){
		 if(sleepNum>0){//if the rabbit sleeps
			for(int i =0;i<sleepNum;i++){
				//gets distance from rabbit and point to know where to stop
				if(Math.abs(rabbit.getX()-sleepPoints.get(i).x) <=25&&Math.abs(rabbit.getY()-sleepPoints.get(i).y) <=25){
					rabbit.move();
					hitPoint = true;//the rabbit hit the point
					sleepPoints.get(i).setLocation(-50,-50); //out of range so rabbit doesnt hit it twice and arrayloop stays the same length
				}
			}
		 }	
		rabbit.move();//if rabbit doesnt sleep it just moves
		winnerCheck();
	}
		
	public void winnerCheck(){
		if (turtle.getX() >= 325&&turtle.getY()==392){//if crosses the red line
			turtleWin=true;
			if(rabbitWin== false){//makes sure rabbit hasent already crossed
				turtleWins+=1;
				turtle.restart(10,150);//restarting
				rabbit.restart(10,120);
				start= false;
				JOptionPane.showMessageDialog(this.getParent(), "Turtle Won!");//display message
			}
		}
		if (rabbit.getX() >= 360&&rabbit.getY()==390){
			rabbitWin=true;
			if(turtleWin== false){
				rabbitWins+=1;
				turtle.restart(10,150);
				rabbit.restart(10,120);
				start= false;
				JOptionPane.showMessageDialog(this.getParent(), "Rabbit Won!");
			
			}
		}
	}

	public void paintComponent(Graphics g){
		g.drawImage(backgroundLAYER,0, 0, null);//painting my background and rabbit and turtle
		rabbit.paint(g);
		turtle.paint(g);
		g.setColor(Color.BLUE);
		if(sleepNum==1&&clicks >=1){//paints one sleep spot (ONLY THE FIRST CLICK)(click was made to stay in range)
			g.fillOval(sleepPoints.get(0).x,sleepPoints.get(0).y,20,20);
		}
		if(sleepNum==2&&clicks >=2){//same as one sleep, but with 2
			g.fillOval(sleepPoints.get(0).x,sleepPoints.get(0).y,20,20);
			g.fillOval(sleepPoints.get(1).x,sleepPoints.get(1).y,20,20);
		}
	}
}

class Turtle extends JPanel{// same for both rabbit and turtle
	private int x,y;
	private Image[] turtleRunTwordsPics,turtleRunForwardPics,turtleRunLeftPics,turtleRunRightPics;//holds all the pictures 
	private int LEFT =0, RIGHT=1, FORWARD =2, TWORDS=3,WAIT=3,direction=RIGHT,frame,delay;
	private boolean turn=false;//when they travel, they cross the same coords twice, so turns true when it gets passed the first time
	
	public Turtle(int x,int y){
		this.x=x;
		this.y=y;
		//all the photos loaded into their arrays, so i can loop through and "animate" them
		turtleRunTwordsPics = new Image[3];
		for(int i=0;i<3;i++){
			turtleRunTwordsPics[i] = new ImageIcon("pictures/turtleRunTwordsPics/turtleRunTwords"+i+".png").getImage();
		}
		turtleRunForwardPics = new Image[3];
		for(int i=0;i<3;i++){
			turtleRunForwardPics[i] = new ImageIcon("pictures/turtleRunForwardPics/turtleRunForward"+i+".png").getImage();
		}
		turtleRunLeftPics = new Image[3];
		for(int i=0;i<3;i++){
			turtleRunLeftPics[i] = new ImageIcon("pictures/turtleRunLeftPics/turtleRunLeft"+i+".png").getImage();
		}
		turtleRunRightPics = new Image[3];
		for(int i=0;i<3;i++){
			turtleRunRightPics[i] = new ImageIcon("pictures/turtleRunRightPics/turtleRunRight"+i+".png").getImage();
		}
		
	}
	public void restart(int dx,int dy){
		x = dx;
		y = dy;
		direction = RIGHT;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	
	public void move(){//moves the character in the path
		delay+=1;//delay, countes every tick from listener
		if(delay%WAIT==0){//if delay % wait == 0 then frame increases
			frame = (frame+1)%3;//frame is used to paint 
		}
		if(x<72&&y==150){
			direction =RIGHT;
			x+=2;
		}
		if(x == 72&& y<=150){
			direction =FORWARD;
			y-=2;
		}
		if(y == 24&&x<=430){
			direction =RIGHT;
			x+=2;
		}
		if(x == 430&&y<=300&&turn==false){
			direction =TWORDS;
			y+=2;
		}
		if(y==220&&x>=330){
			direction =LEFT;
			turn= true;
			x-=2;
		}
		if(x==330&&y<=390&&turn==true){
			direction =TWORDS;
			y+=2;
		}
	}
	public void paint(Graphics g){
    	if (direction == TWORDS){//each direction has a different animation
			g.drawImage(turtleRunTwordsPics[frame],x, y, null);//paints at the frame
    	} 
    	else if (direction == FORWARD){
			g.drawImage(turtleRunForwardPics[frame],x, y, null);
    	} 
    	else if (direction == RIGHT){
			g.drawImage(turtleRunRightPics[frame],x, y, null);
	    } 
    	else if (direction == LEFT){
			g.drawImage(turtleRunLeftPics[frame],x, y, null);
	    }
	}
}

class Rabbit extends JPanel{
	private int x,y;
	private Image[] bunnyRunTwordsPics,bunnyRunForwardPics,bunnyRunLeftPics,bunnyRunRightPics;
	private int LEFT =0, RIGHT=1, FORWARD =2, TWORDS=3,WAIT=3,direction=RIGHT,frame,delay;
	private boolean turn=false;
	
	public Rabbit(int x,int y){
		this.x=x;
		this.y=y;
		
		bunnyRunTwordsPics = new Image[3];
		for(int i=0;i<3;i++){
			bunnyRunTwordsPics[i] = new ImageIcon("pictures/bunnyRunTwordsPics/bunnyRunTwords"+i+".png").getImage();
		}
		bunnyRunForwardPics = new Image[3];
		for(int i=0;i<3;i++){
			bunnyRunForwardPics[i] = new ImageIcon("pictures/bunnyRunForwardPics/bunnyRunForward"+i+".png").getImage();
		}
		bunnyRunLeftPics = new Image[3];
		for(int i=0;i<3;i++){
			bunnyRunLeftPics[i] = new ImageIcon("pictures/bunnyRunLeftPics/bunnyRunLeft"+i+".png").getImage();
		}
		bunnyRunRightPics = new Image[3];
		for(int i=0;i<3;i++){
			bunnyRunRightPics[i] = new ImageIcon("pictures/bunnyRunRightPics/bunnyRunRight"+i+".png").getImage();
		}
	}
	public void restart(int dx,int dy){
		x = dx;
		y = dy;
		direction = RIGHT;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	
	public void move(){
		delay+=1;
		if(delay%WAIT==0){
			frame = (frame+1)%3;
		}		
		if(x<52&&y==120){
			direction =RIGHT;
			x+=3;
		}
		if(x == 52&& y<=150){
			direction =FORWARD;
			y-=3;
		}
		if(y == 3&&x<=461){
			direction =RIGHT;
			x+=3;
		}
		if(x == 463&&y<=300&&turn==false){
			direction =TWORDS;
			y+=3;
		}
		if(y==243&&x>=363){
			direction =LEFT;
			turn= true;
			x-=3;
		}
		if(x==361&&y<=389&&turn==true){
			direction =TWORDS;
			y+=3;
		}
	}
	public void paint(Graphics g){
    	if (direction == FORWARD){
			g.drawImage(bunnyRunTwordsPics[frame],x, y, null);
    	} 
    	else if (direction == TWORDS){
			g.drawImage(bunnyRunForwardPics[frame],x, y, null);
    	} 
    	else if (direction == RIGHT){
			g.drawImage(bunnyRunRightPics[frame],x, y, null);
	    } 
    	else if (direction == LEFT){
			g.drawImage(bunnyRunLeftPics[frame],x, y, null);
	    }
	}
}
