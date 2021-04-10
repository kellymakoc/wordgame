import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class gui_exercise extends JFrame implements ActionListener{
	//intro panel and labels 
	JPanel intro = new JPanel(new GridLayout());
	JLabel welcome = new JLabel("Welcome. The FBI needs your help! Check out the 'How to play' button to solve the case. ", JLabel.CENTER);
	
	// grid for the word game 
	final static int ROW = 6;
	final static int COL = 6;
	JPanel gridPanel = new JPanel(new GridLayout(ROW,COL));
	JLabel [][] gridLabel = new JLabel[ROW][COL];
	ArrayList<ArrayList<Character>> grid;

	//bottom panel for instructions and quit buttons
	JPanel panel = new JPanel(new FlowLayout());
	JButton instruction = new JButton("How to play");
	JButton quit = new JButton("Quit");
	JButton resetGrid = new JButton("Reset Grid");
	
	// panel, labels, text field for input from keyboard/user
	JPanel input = new JPanel(new FlowLayout());
	JLabel prompt = new JLabel("Please enter a word.");
	JLabel checkGrid = new JLabel();
	JLabel checkTxt = new JLabel();
	JLabel usedLabel = new JLabel();
	JTextField word = new JTextField("", 20);
	ArrayList <String> usedWords = new ArrayList<String>();
	
	//arraylist for words from text file 
	ArrayList <String> wordlist = new ArrayList<String>();
	/**
	 * readFile is used to read a text file and save all the words to an arraylist
	 * @param wordlist <String> use to store the words from text file
	 * @throws IOException - throw in exception for file errors.
	 */
	public void readFile(ArrayList <String> wordlist) throws IOException {
		Scanner sc = new Scanner (new FileReader("wordlist.txt"));  //scanner for reading the text file
		while(sc.hasNext()) {
			wordlist.add(sc.next().trim().toLowerCase()); // remove any extra space, change all letters to lower cases and add word into the arraylist 
		}
		sc.close();  // close file reader
	}

	// get start time once the application starts
	long start = Calendar.getInstance().getTimeInMillis();
	
	//panel, label for how to quit the game 
	JPanel bottom = new JPanel(new FlowLayout());
	JLabel end = new JLabel("Press Quit button to quit game.", JLabel.CENTER);
	int score = 0;
	JLabel scoreboard = new JLabel(" ", JLabel.RIGHT);

	/**
	 * generateChars is used to produce 9-12 vowels and 36-(vowels) consonants for the grid.
	 * This method will first produce the vowels for the grid by using a random generator for the number of vowels on the grid that is between 9 to 12.
	 * Then, it will subtract the number of vowels used from the total number of characters in the grid (36).
	 * By 36- nVowels, we will randomly generate consonants to the grid. 
	 * @return chars array list 
	 */
	public static ArrayList <Character> generateChars(){
		ArrayList <Character> chars = new ArrayList<Character>();
		Random rand = new Random();
		char[] vowels = {'a','e','i','o','u'};
		int nVowels = rand.nextInt(4) + 9;  //produce 9-12 vowels for the grid
		for (int i = 0; i < nVowels; i++){
			int randIndex = rand.nextInt(5);  // get one from vowels array
			chars.add(vowels[randIndex]);
		}
		char[] consonants = {'b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','y','z'};
		int nConsonants = 36 - nVowels; // produce (36-vowels) consonants
		for (int i = 0; i < nConsonants; i++){
			int randIndex = rand.nextInt(21);  // get one from consonants array
			chars.add(consonants[randIndex]);
		}
		Collections.shuffle(chars); // put the characters in random order
		return chars;
	}

	/**
	 * generateGrid is used to generate the word grid from the chars array list. 
	 * @param chars - used to initialize elements in the 2d array list.
	 * @return grid 
	 */
	public static ArrayList<ArrayList <Character>> generateGrid(ArrayList <Character> chars){
		ArrayList<ArrayList <Character>> grid = new ArrayList<ArrayList<Character>>();
		int curIdx = 0;  // current index
		for(int i = 0;i< ROW;i++) {
			ArrayList<Character> row = new ArrayList<Character>();
			for (int j = 0; j < COL; j++) {
				row.add(chars.get(curIdx)); // generate row by initializing elements
				curIdx++;
			}
			grid.add(row);
		}
		return grid;
	}

	/**
	 * generateChoices is used to generate all the possibilities of words from the grid 
	 * @param grid - used to generate the possible word combinations
	 * @param input - used as a reference where all the combinations form
	 * @return 2d arraylist of choices (8 directions)
	 */
	public static ArrayList<ArrayList <Character>> generateChoices(ArrayList<ArrayList <Character>> grid, String input){
		ArrayList<ArrayList <Character>> choices = new ArrayList<ArrayList<Character>>();
		for (int i = 0; i < ROW;i++){
			for(int j = 0; j < COL;j++){
				if (grid.get(i).get(j).equals(input.charAt(0))){
					// [0 - i] [j] up
					ArrayList <Character> up = new ArrayList<>();
					for(int k = i - 1; k >= 0;k--){
						up.add(grid.get(k).get(j));
					}
					if(up.size() != 0){
						choices.add(up);
					}
					// [i - 6] [j] down
					ArrayList <Character> down = new ArrayList<>();
					for(int k = i+1; k < 6;k++){
						down.add(grid.get(k).get(j));
					}

					if(down.size() != 0){
						choices.add(down);
					}

					// [i] [0 - j] left
					ArrayList <Character> left = new ArrayList<>();
					for(int k = j-1; k >= 0;k--){
						left.add(grid.get(i).get(k));
					}
					if(left.size() != 0) {
						choices.add(left);
					}
					// [i] [j - 6] right
					ArrayList <Character> right = new ArrayList<>();
					for(int k = j+1; k < 6;k++){
						right.add(grid.get(i).get(k));
					}
					if(right.size() != 0) {
						choices.add(right);
					}
					int curI = i - 1;
					int curJ = j - 1;
					ArrayList <Character> upLeft = new ArrayList<>();
					while (curI >= 0 && curJ >= 0){	// top left
						upLeft.add(grid.get(curI).get(curJ));
						curI --;
						curJ --;
					}
					if(upLeft.size() != 0) {
						choices.add(upLeft);
					}
					curI = i + 1;
					curJ = j + 1;
					ArrayList <Character> downRight = new ArrayList<>();
					while (curI < 6 && curJ < 6){	// down right
						downRight.add(grid.get(curI).get(curJ));
						curI ++;
						curJ ++;
					}
					if(downRight.size() != 0) {
						choices.add(downRight);
					}
					curI = i - 1;
					curJ = j + 1;
					ArrayList <Character> upRight = new ArrayList<>();
					while (curI >= 0 && curJ < 6){	// up right
						upRight.add(grid.get(curI).get(curJ));
						curI --;
						curJ ++;
					}
					if(upRight.size() != 0) {
						choices.add(upRight);
					}
					curI = i + 1;
					curJ = j - 1;
					ArrayList <Character> downLeft = new ArrayList<>();
					while (curI < 6 && curJ >= 0){	// down left
						downLeft.add(grid.get(curI).get(curJ));
						curI ++;
						curJ --;
					}
					if(downLeft.size() != 0) {
						choices.add(downLeft);
					}
				}
			}
		}
		return choices;
	}

	/**
	 * checkGridWord is used to check if the input word is on the grid
	 * @param choices - check each of the combinations if the word is formed from there
	 * @param input - receive the "specific item" to find in the 2d arraylist of choices
	 * @return boolean value
	 */
	public static boolean checkGridWord(ArrayList<ArrayList <Character>> choices, String input){
		 ArrayList<ArrayList <Character>> newChoices = new ArrayList<ArrayList<Character>>();
		 for (ArrayList <Character> choice : choices) {
			 if(!choice.isEmpty()) {
				 if (choice.get(0) == input.charAt(0)) {
				     choice.remove(0);
				     newChoices.add(choice);
				   }
			 }
		  }
		  if (newChoices.size() == 0) {
			  if (input.length() >= 1){
				  return false;
			  }
		  }
		  if (input.length() == 1){
			  if (newChoices.size() >=0){
				  return true;
			  }
		  }
		 return checkGridWord(newChoices,input.substring(1));
		 }

	/**
	 * consolePrint is used to print out the same grid on GUI to console.
	 * @param grid used to print the rows of characters to console
	 */
	public static void consolePrint(ArrayList<ArrayList<Character>> grid) {
		System.out.println("Welcome. The FBI needs your help! \n");
		for (ArrayList<Character>row : grid) {
			System.out.println(row.toString());
		}
		System.out.println();
	}
	/**
	 * gui_exercise is used to set up the panels in the frame and add in features
	 * @throws IOException - throws in exception for reading file errors
	 */
	public gui_exercise() throws IOException {
		readFile(wordlist); //invoke readFile method
		// set frame 
		setTitle("FBI needs your help!");
		setSize(800,600);
		setLayout(new GridLayout(5,1));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// set intro panel (welcome words and prompt user
		intro.add(welcome);
		
		//set up grid panel and label for the word grid 
		grid = generateGrid(generateChars());
		consolePrint(grid);  //print the grid in the console as well
		for (int row = 0; row < ROW; row++) {
			for (int col = 0; col < COL; col++) {
				JLabel charLabel = new JLabel(grid.get(row).get(col).toString());
				gridLabel[row][col] = charLabel;
				gridPanel.add(charLabel);
			}
		}
		
		//set up panel for buttons (quit and instructions)
		//take action when button is pressed
		/*
		  actionPerformed method is used for instruction button.
		  It shows the instructions of the game in both GUI and console interface.
		  It has pop up window for GUI interface when the button is pressed.
		 */
		StringBuffer rules = new StringBuffer();
		rules.append("How to play: \n"
				+ "The FBI needs your help! They are trying to solve a mystery case. \n"
				+ "However, they are having trouble on figuring out the hidden words from the grid. \n"
				+ "Can you help the agents to find the words? \n"
				+ "The words need to be connected in a row where it can be horizontally, vertically or diagonally connected. \n"
				+ "You need to find words that are at least 2 character long in length. \n"
				+ "The longer character length you find, the more points you get! \n");
		instruction.addActionListener(e -> {
			System.out.println("How to play button was pressed. \n");
			System.out.println(rules.toString());
			// pop up a window for the instructions
			JOptionPane.showMessageDialog(null, rules.toString());
		});

		/*
		  actionPerformed method is used for Quit button.
		  It shows the running time, and goodbye message to player on both GUI and console interface.
		  It has pop up window for GUI interface when the button is pressed.
		 */
		quit.addActionListener(e -> {
			System.out.println("Quit button was pressed. \n"); // print the received action on console
			long end = Calendar.getInstance().getTimeInMillis();  // set the end time for the program/game
			long result = (end - start) / 1000;  // find the running time

			// quit program application
			if (result >= 60) {  // if the running time is greater than 60
				int min = (int) result / 60;  // change seconds to minutes, seconds
				int sec = (int) result % 60;
				JOptionPane.showMessageDialog(null, "You took " + min + "min" + sec + "s on this game. \n"
						+ "(: Thank you for playing this game. Hope you had fun! :)");  //pop up window for farewell

				System.out.println("You took " + min + "min" + sec + "s on this game. \n"
						+ "(: Thank you for playing this game. Hope you had fun! :)"); //print in console
			}
			else {  // if running time is shorter than 60 seconds
				JOptionPane.showMessageDialog(quit, "You took " + result + " seconds on this game. \n"
						+ "(: Thank you for playing this game. Hope you had fun! :)"); // pop up window for farewell
				System.out.println("You took " + result + " seconds on this game. \n"
						+ "(: Thank you for playing this game. Hope you had fun! :)");  //print in console
			}
			dispose();  // quit program application
		});

		/*
		  actionPerformed method is used for resetting the grid when the button is pressed.
		  It calls generateChars and generateGrid to recreate another word grid.
		  Then, it will update the JLabels from GUI.
		 */
		resetGrid.addActionListener(e -> {
			System.out.println("Reset Grid Button was pressed. \n");
			grid = generateGrid(generateChars());  //regenerate grid
			consolePrint(grid);  //print the grid in the console as well
			//update grid on GUI
			for (int row = 0; row < ROW; row++) {
				for (int col = 0; col < COL; col++) {
					gridLabel[row][col].setText(grid.get(row).get(col).toString());
				}
			}
		});

		//add buttons to the panel 
		panel.add(instruction);  //"How to play" button
		panel.add(resetGrid);  // "Reset Grid" button
		panel.add(quit);  // "Quit" button
		
		//set up panel for text input
		word.addActionListener(this);
		input.add(prompt); // prompt user instruction
		input.add(word);  //add text field
		input.add(checkGrid);  // add label to indicate if the input word is on the grid
		input.add(checkTxt); // add label to indicate if the input word is valid
		input.add(usedLabel);  //add label if user input a duplicated word

		//set up bottom panel
		bottom.add(end);  // prompt user how to quit game
		bottom.add(scoreboard);  //show scoreboard

		//add panels to the frame 
		add(intro); // welcome panel 
		add(gridPanel);  // word search grid panel 
		add(panel); // instruction and quit button panel 
		add(input); // words enter panel 
		add(bottom); // bottom panel for how to quit game, scores

		setVisible(true);
	}
	/*
	 * ActionPerformed is used to detect any actions happened in the application such as clicks, press and inputs. 
	 * It will detect the action it receives and outputs the corresponding action. 
	 */
	public void actionPerformed(ActionEvent e) {
		checkTxt.setText("");
		usedLabel.setText("");
		String input = word.getText();  // get input text from user/keyboard
		//get 8 directions possible combinations of words from grid
		ArrayList<ArrayList<Character>> choices = generateChoices(grid,input);
		if (input.length() < 2){
			checkTxt.setText("Please enter a longer character length. (Check out the 'How to play' button for the instructions)");
			word.setText("");
		}
		else {
			if (checkGridWord(choices, input.substring(1))) { // if exists on grid
				//show word on grid
				checkGrid.setText("'" + input + "' is on the grid.");
				System.out.println("'" + input + "' is on the grid.");
				// checkGrid if the input exists in the arraylist
				if (wordlist.contains(input)) {
					if (!usedWords.contains(input)) { // check if it has been typed
						usedWords.add(input);
						if (input.length() <= 3) {  // when the user typed in a word with a length less than or equal to 3
							score++;// add 1 to the score
						} else if (input.length() == 4) {// when the user typed in a word with a length equal to 4
							score += 2;  // add 3 to the score
						} else if (input.length() == 5) {// when the user typed in a word with a length equal to 5
							score += 3; // add 3 to the score
						} else if (input.length() == 6) {// when the user typed in a word with a length equal to 6
							score += 5; // add 5 to the score
						} else { // when the user typed in a word with a length greater than 6
							score += 8;  // add 8 to the score
						}
						checkTxt.setText("'" + input + "' is a valid word. ");  // output exist
						scoreboard.setText("Score: " + score);  // show the score
						word.setText("");  //reset text field
						//print in console
						System.out.println("'" + input + "' is a valid word. \n" + "Score: " + score + "\n");
					} else { // if user has typed the word before
						usedLabel.setText("'" + input + "' has been used. Please try another word.");
						System.out.println("'" + input + "' has been used. Please try another word.");
						word.setText("");
					}
				} else {  // if the word is not valid
					checkTxt.setText("'" + input + "' is not a valid word.");
					word.setText("");  //reset text field
					System.out.println("'" + input + "' is not a valid word. \n");  // print in console
				}
			} else {  // if it does not exist on grid
				//show word is not in grid
				checkGrid.setText("'" + input + "' is not on the grid.");
				System.out.println("'" + input + "' is not on the grid.");
				word.setText("");  //reset text field
			}
		}
	}
/**
	 * This is the main method that calls the GUI application
	 * @param args - calls gui_exercise method
	 * @throws IOException - used for file handling
	 */
	public static void main(String[] args) throws IOException {
		new gui_exercise();
	}
}
