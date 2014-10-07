import java.util.Comparator;

/**
 * TreeComparator class used to compare the frequencies of singleton Binary Trees
 * @author BenRush and Mahina Kaholokula
 * CS10 PS4 Fall 2013
 *
 */
public class TreeComparator implements Comparator<BinaryTree<TreeData>> {

	/**
	 * compare 2 node frequencies
	 * @param node1, node2 the 2 nodes to compare
	 * returns -1 if node1's freq < node2's freq
	 * returns 0 if the frequencies are equal
	 * returns 1 if node1's freq > node2's freq
	 */
	public int compare(BinaryTree<TreeData> node1, BinaryTree<TreeData> node2) {
		int node1Freq = node1.getValue().getFrequency();
		int node2Freq = node2.getValue().getFrequency();
		if(node1Freq <  node2Freq)
			return -1;
		else if(node1Freq > node2Freq)
			return 1;
		else
			return 0; //Returns 0 as a default, or if they have the same frequency
		
	}
}
