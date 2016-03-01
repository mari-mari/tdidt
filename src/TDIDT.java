import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class TDIDT {
	private List<String[]> data;
	private Node root;
	private Graph graph;

	public TDIDT() {
		data = new ArrayList<>();
		root = new Node();
		graph = new SingleGraph("Tree");
	}

	public List<String[]> getData() {
		return data;
	}

	public void readData(String fileName) throws IOException {
		Scanner sc = new Scanner(Paths.get(fileName));
		while (sc.hasNext()) {
			data.add(sc.next().split(","));
		}
		sc.close();
	}

	private double log2(double value) {
		if (value != 0)
			return Math.log10(value) / Math.log10(2);
		else
			return 0.0;
	}

	private double entropy(double pos, double neg) {
		return -pos / (pos + neg) * log2(pos / (pos + neg)) - neg / (pos + neg) * log2(neg / (pos + neg));
	}

	public double calculateEntropy(double leftPos, double leftNeg, double rightPos, double rightNeg) {
		double entropy = 0.0;
		double n = leftPos + leftNeg + rightPos + rightNeg;
		entropy = (leftPos + leftNeg) / n * entropy(leftPos, leftNeg)
				+ (rightPos + rightNeg) / n * entropy(rightPos, rightNeg);
		return entropy;
	}

	public int chooseAttribute(List<String[]> dataSet) {
		double min = Double.MAX_VALUE;
		int minAttr = -1;
		for (int j = 1; j < dataSet.get(0).length; j++) {
			double leftPositives = 0.0;
			double leftNegatives = 0.0;
			double rightPositives = 0.0;
			double rightNegatives = 0.0;
			for (int i = 0; i < dataSet.size(); i++) {
				if (dataSet.get(i)[j].equals("0")) {
					if (dataSet.get(i)[0].equals("1"))
						leftPositives++;
					else
						leftNegatives++;
				} else {
					if (dataSet.get(i)[0].equals("1"))
						rightPositives++;
					else
						rightNegatives++;
				}
			}
			double entropy = calculateEntropy(leftPositives, leftNegatives, rightPositives, rightNegatives);
			if (min > entropy) {
				min = entropy;
				minAttr = j;
			}
		}
		return minAttr;
	}

	public boolean isPerfectSplit(List<String[]> dataSet, Node currentNode) {

		String firstResult = dataSet.get(0)[0];
		for (String[] line : dataSet) {
			if (!line[0].equals(firstResult))
				return false;
		}
		currentNode.setLeaf(true);
		currentNode.setLeafClass(firstResult);
		return true;
	}

	public void dealWithNoSplit(List<String[]> dataSet, Node currentNode) {
		double countPos = 0.0;
		double countNeg = 0.0;
		for (String[] line : dataSet)
			if (line[0].equals("0"))
				countNeg++;
			else
				countPos++;
		currentNode.setLeaf(true);
		if (countNeg > countPos)
			currentNode.setLeafClass("0");
		else
			currentNode.setLeafClass("1");
	}

	public Node tdidt(List<String[]> dataSet, Node root, Node currentNode) {
		if (isPerfectSplit(dataSet, currentNode))
			return root;
		int testAttr = chooseAttribute(dataSet);
		if (testAttr == -1) {
			dealWithNoSplit(dataSet, currentNode);
			return root;
		}
		currentNode.setTest(testAttr);
		LinkedList<String[]> leftData = new LinkedList<>();
		LinkedList<String[]> rightData = new LinkedList<>();
		for (String[] line : dataSet) {
			if (line[testAttr].equals("0"))
				leftData.add(line);
			else
				rightData.add(line);
		}
		Node leftNode = new Node();
		Node rightNode = new Node();
		currentNode.setLeft(leftNode);
		currentNode.setRight(rightNode);
		tdidt(leftData, root, leftNode);
		tdidt(rightData, root, rightNode);
		return root;
	}

	public void drawTree() {
		graph.addNode("0");
		graph.addAttribute("ui.antialias");
		drawTreeRecursively(this.root, "0");
		graph.display();
	}

	public void drawTreeRecursively(Node parent, String parentName) {
		if (parent.getLeft() != null) {
			graph.addNode(parentName + "0");
			graph.addEdge(parentName + parentName + "0", parentName, parentName + "0");
			drawTreeRecursively(parent.getLeft(), parentName + "0");
		}
		if (parent.getRight() != null) {
			graph.addNode(parentName + "1");
			graph.addEdge(parentName + parentName + "1", parentName, parentName + "1");
			drawTreeRecursively(parent.getRight(), parentName + "1");
		}
	}

	public void runTest() {
		Double error = 0.0;
		for (String[] line : data) {
			if (!line[0].equals(test(this.root, line)))
				error++;
		}
		System.out.println(error);
	}

	public String test(Node currentNode, String[] pattern) {
		if (currentNode.isLeaf() == false) {
			int index = currentNode.getTest();
			if (pattern[index].equals("0"))
				return test(currentNode.getLeft(), pattern);
			else
				return test(currentNode.getRight(), pattern);
		}
		return currentNode.getLeafClass();
	}

	public void runTDIDT() {
		tdidt(data, root, root);
		drawTree();
	}

	public static void main(String[] args) throws IOException {
		TDIDT tdidt = new TDIDT();
		tdidt.readData("SPECT.train.txt");
		tdidt.runTDIDT();

	}
}
