import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import javax.swing.JFileChooser;

/**
 * Class used to compress and decompress text files
 * @author BenRush and Mahina Kaholokula
 * November 2, 2013
 * CS 10 Fall 2013 PS 4
 */
public class Compression {
	
	/**
	  * Puts up a fileChooser and gets path name for file to be opened.
	  * Returns an empty string if the user clicks "cancel".
	  * @return path name of the file chosen	
	  */
	public static String getFilePath() {
	  //Create a file chooser
	  JFileChooser fc = new JFileChooser();
	   
	  int returnVal = fc.showOpenDialog(null);
	  if(returnVal == JFileChooser.APPROVE_OPTION)  {
	    File file = fc.getSelectedFile();
	    String pathName = file.getAbsolutePath();
	    return pathName;
	  }
	  else
	    return "";
	 }

	/**
	 * Class that creates a frequency table of the characters in the text file
	 * @param inputFile the file you want to compress
	 * @return returns a frequency table as a Map of integers with character keys
	 */
	public static Map<Character, Integer> makeFrequencyTable(BufferedReader inputFile){
		Map<Character, Integer> freqMap = new HashMap<Character, Integer>();
		BufferedReader input= inputFile;
		try{ 
				//Reads through the file and creates a frequency table using a HashMap of Characters (key) and Integers (value)
				while(true){
					char nextChar;
					int newFreq;
					int nextCharInt = input.read();
					
					if(nextCharInt == -1){
						break; //Breaks at the end of the text file
					}
					
					nextChar = (char)(nextCharInt);
					
					if(freqMap.containsKey(nextChar)){
						newFreq = freqMap.get(nextChar) + 1;
						freqMap.put(nextChar, newFreq);
					}
					else
						freqMap.put(nextChar, 1);
				}
			}
		catch(IOException ex){
			System.out.println("No input");
		}
		return freqMap;
	}
	
	 /**
	 * Method used to make a singleton tree
	 * @param key The character stored in the singleton Tree
	 * @param frequency The frequency of the specified character
	 * @return returns a singleton tree that stores the included data
	 */
	public static BinaryTree<TreeData> makeSingleton(Character key, int frequency){
		TreeData datum = new TreeData(key, frequency);
		BinaryTree<TreeData> singleton = new BinaryTree<TreeData>(datum);
		return singleton;
	}
		
	/**
	 * Method used to make a priority queue of singletons from a given frequency table
	 * @param freqTable the frequency table that will be put in to the priority queue
	 * @return returns a priority queue full of singletons
	 */
	public static PriorityQueue<BinaryTree<TreeData>> makePriorityQueue(Map<Character, Integer> freqTable){
		TreeComparator compare = new TreeComparator();
		//ACCOUNTS FOR AN EMPTY DOCUMENT
		if(freqTable.isEmpty()){
			System.out.println("Empty frequency table error. Empty Priority Queue will be made.");
			return new PriorityQueue<BinaryTree<TreeData>>();
		}
		PriorityQueue<BinaryTree<TreeData>> priorq = new PriorityQueue<BinaryTree<TreeData>>(11, compare); //Priority queue that will contain binary trees
		Set<Character> freqKeys = freqTable.keySet(); //Set of all of the characters in the document
		Iterator<Character> iter = freqKeys.iterator(); //iterator to go through characters in the set
		//Iterate through the frequency list and create singletons, then add them to a priority queue
		while(iter.hasNext()){
			Character charKey = iter.next();
			int freq = freqTable.get(charKey);
			BinaryTree<TreeData> singleton = makeSingleton(charKey, freq);
			priorq.add(singleton);
		}
		return priorq;
	}
	
	/**
	 * Method used to make a binary tree from a priority queue of singletons
	 * @param priorQ The priority queue that the binary tree will be based on
	 * @return returns a binary tree sorted for Huffman encoding
	 */
	public static BinaryTree<TreeData> makeTree(PriorityQueue<BinaryTree<TreeData>> priorQ){
		//ACCOUNTS FOR AN EMPTY DOCUMENT by creating 1 node with no information so that it does not give an error
		if(priorQ.isEmpty()){
			System.out.println("Empty priority queue. Empty tree will be returned.");
			TreeData root = new TreeData('\u0000', 0);
			return new BinaryTree<TreeData>(root, null, null);
		}
		
		BinaryTree<TreeData> temp1; //Temporary Binary Tree - lowest frequency tree
		BinaryTree<TreeData> temp2; //Temporary Binary Tree - second lowest frequency tree
		PriorityQueue<BinaryTree<TreeData>> finalQ = priorQ;
		
		//Goes through priority queue joining temporary trees and re-adding them until only 1 Binary Tree with all data included is left
		while(finalQ.size() > 1)
		{
			temp1 = finalQ.poll();
			temp2 = finalQ.poll();
			int temp1Freq = temp1.getValue().getFrequency();
			int temp2Freq = temp2.getValue().getFrequency();
			int newFreq = temp1Freq + temp2Freq;
			TreeData root = new TreeData('\u0000', newFreq); // \u0000 used as a null char 
			BinaryTree<TreeData> comboTree = new BinaryTree<TreeData>(root, temp1, temp2);
			finalQ.add(comboTree);
		}
		return finalQ.poll();
	}
	
	/**
	 * Method that makes the Code Map with character keys and their associated bit codes
	 * @param thisTree Binary Tree that holds the bit codes and the characters
	 * @param thisCode String that holds the growing bit code as we traverse the tree
	 * @param thisCodeMap Code Map that we are creating to hold the character keys and their associated bit codes
	 * @return Returns a Code Map with character keys and associated bit codes
	 */
	public static Map<Character, String> makeCode(BinaryTree<TreeData> thisTree, String thisCode, Map<Character, String> thisCodeMap){ 
		Map<Character, String> codeMap = thisCodeMap;
		BinaryTree<TreeData> codeTree = thisTree;
		String code = thisCode;
		
		if(codeTree.hasLeft()){
			code += "0";
			makeCode(codeTree.getLeft(), code, codeMap);
		}
		if(codeTree.hasRight()){
			code = code.substring(0, code.length() - 1);
			code += "1";
			makeCode(codeTree.getRight(), code, codeMap);
		}
		else if(codeTree.isLeaf()){
			char key = codeTree.getValue().getKey();
			//Accounts for if there is ONLY one type of character in the document
			if(code.length() == 0){
				codeMap.put(key, "0");
			}
			
			codeMap.put(key, code);
		}
		return codeMap;
	}

	/**
	 * Method used to compress a file into bits given a frequency map of the characters
	 * @param thisMap The Map of Characters and Integer frequencies
	 */
	public static void compress(Map<Character, String> thisMap, BufferedReader inputFile, BufferedBitWriter output){
		Map<Character, String> codeMap = thisMap;
		BufferedReader input =  inputFile;
		BufferedBitWriter bitOutput = output;
		//Reads through the text file, converting every character to its bitCode and then writing the bitCode to a new (compressed) file
		while(true){
			try{
				char nextChar;
				String nextBit;
				int bit; 
				int nextCharInt = input.read();
				
				//Determines if we have hit the end of the file, breaks if so
				if(nextCharInt == -1){
					break;
				}
				
				nextChar = (char)(nextCharInt); //Next character from input
				nextBit = codeMap.get(nextChar);
				
				//Converts the character to its bitCode and writes it in the new file
				for(int i = 0; i < nextBit.length(); i++){
					bit = Character.digit(nextBit.charAt(i), 10);
					bitOutput.writeBit(bit);
				}
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
		}	
	}

	/**
	 * Method used to decompress the compressed text file consisting of bits, will create a new file
	 * @param thisTree the Binary Tree that holds the bit keys and will be traversed
	 * 
	 */
	public static void decompress(BinaryTree<TreeData> thisTree, BufferedBitReader bitFile, BufferedWriter bitOutput){		
		BinaryTree<TreeData> codeTree = thisTree;
		BufferedBitReader bitInput = bitFile;
		BufferedWriter output = bitOutput;
		try{
			int bit;
			BinaryTree<TreeData> current = codeTree;
			char outputChar;
			//BOUNDARY CASE CHECK
			//Checks for if the file contains just a single type of character and accounts for this
			if(thisTree.isLeaf()){
				outputChar = current.getValue().getKey();
				for(int i = 0; i < current.getValue().getFrequency(); i++){
					output.write(outputChar);
				}
			}
			//Goes through the compressed file, reading the bits in and traversing our binary code tree, determining
			//which characters are associated with these bitCodes and writing the characters into the new (decompressed) file
			while(true){
				bit = bitInput.readBit(); //Reads bit from compressed file
				if(bit == -1){ //Breaks if at the end
					break;
				}
				else if(bit == 0){
					current = current.getLeft();
				}
				else if(bit == 1){
					current = current.getRight();
				}
					
				if(current.isLeaf()){
					outputChar = current.getValue().getKey();
					output.write(outputChar);
					current = codeTree; //Resets current to codeTree
				}	
			}
		}
		catch(IOException ex){
			System.out.print("No input");
		}
	}
	
	public static void main(String[] args) throws IOException{
		Map<Character, Integer> thisMap = null;
		BufferedReader input = null;
		BufferedBitReader bitInput = null;
		BufferedWriter bitOutput = null;
		BufferedBitWriter bitOutput_read = null;
		
		System.out.println("Choose the file you would like to compress");
		String pathName = getFilePath(); //Gets the name of the text file to be analyzed
		//DOES NOT ALLOW NON-TEXT FILES TO BE COMPRESSED
		if(!pathName.substring(pathName.length() - 4, pathName.length()).equals(".txt"))
		{
				System.out.println("This is not a text-file. No compression will be performed. Exiting now.");
				System.exit(0);
		}
		String subName = pathName.substring(0, pathName.length() - 4); //Accounts for .txt at the end of pathName
		try{
			input =  new BufferedReader(new FileReader(pathName));
			thisMap = makeFrequencyTable(input);
		}
		catch(FileNotFoundException ex){
			System.out.println("File not found");
		}
		finally{
			input.close();
		}
	
		PriorityQueue<BinaryTree<TreeData>> q	 = makePriorityQueue(thisMap);
		BinaryTree<TreeData> thisTree = makeTree(q);
		Map<Character, String> thisCodeMap = new HashMap<Character, String>(); 
		Map<Character, String> codeMap = makeCode(thisTree, "", thisCodeMap);
		
		try{
			input =  new BufferedReader(new FileReader(pathName));
			bitOutput_read = new BufferedBitWriter(subName + "_Compressed.txt");
			compress(codeMap, input, bitOutput_read);//Compresses the file
		}
		catch(FileNotFoundException ex){
			System.out.println("File not found");
		}
		finally{
			bitOutput_read.close();
			input.close();
		}
		
		System.out.println("Choose the file you are looking to decompress");
		String decompressedPathName = getFilePath();
		
		try{
			bitInput = new BufferedBitReader(decompressedPathName);
			bitOutput = new BufferedWriter(new FileWriter(subName + "_Decompressed.txt"));
			decompress(thisTree, bitInput, bitOutput);
		}
		catch(FileNotFoundException ex){
			System.out.println("File not found");
		}
		finally{
			bitInput.close();
			bitOutput.close();
		}
	}
}
