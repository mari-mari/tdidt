
public class Node {
	private String leafClass;
	private int test;
	private boolean leaf;
	private Node left;
	private Node right;
	public Node(){
		test = -1;
		leaf = false;
		left = null;
		right = null;
	}
	
	public void setLeafClass(String leafClass) {
		this.leafClass = leafClass;
	}
	
	public void setTest(int testAttribute) {
		this.test = testAttribute;
	}
	
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	public void setLeft(Node left) {
		this.left = left;
	}
	
	public void setRight(Node right) {
		this.right = right;
	}
	
	public String getLeafClass() {
		return leafClass;
	}
	
	public Node getLeft() {
		return left;
	}
	
	public Node getRight() {
		return right;
	}
	
	public int getTest() {
		return test;
	}
	public boolean isLeaf() {
		return leaf;
	}
}
