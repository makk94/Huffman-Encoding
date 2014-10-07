
/**
 * CLass that is the data type for the tree
 * @author BenRush and Mahina Kaholokula
 * For PS-4 CS10 Fall 2013
 */
public class TreeData{
	private int frequency; //frequency of character
	private char charac; //character
	
	/**
	 * Constructor for the data type
	 * @param invalid the character stored
	 * @param freq the frequency of the specified character key
	 */
	public TreeData(char key, int freq){
		frequency = freq;
		charac = key;
	}
	
	/**
	 * 
	 * @return returns the character that is stored
	 */
	public char getKey(){
		return this.charac;
	}
	
	/**
	 * 
	 * @return returns the frequency of the specific character
	 */
	public int getFrequency(){
		return this.frequency;
	}
}