import java.util.*;
import java.io.*;
import java.io.File;

public class TreeNode {
  public TreeNode parent;
  public LinkedList<TreeNode> children;
  public String word;
  public int wordCount;

  public static String getInput(){
    //I modified some basic file-reading syntax from http://javarevisited.blogspot.cz/2015/09/how-to-read-file-into-string-in-java-7.html because this is my first Java program. Ever.
    //Hopefully that doesn't disqualify me for using too much third party code.  :-)
    //----
    //Just about everything else in this program is original code, though I consulted a couple tutorials to learn about individual functions like equals().
    //(It took about 45 minutes of testing to realize that in Java, the == operator compares references, rather than values. That was certainly a big "aha" moment!)
    String content = "";
    try{
      InputStream is = new FileInputStream("input.txt");
      BufferedReader buf = new BufferedReader(new InputStreamReader(is));
      String line = buf.readLine();
      StringBuilder sb = new StringBuilder();
      while(line != null){
        sb.append(line).append("\n");
        line = buf.readLine();
      }
      content = sb.toString();
    }catch(FileNotFoundException fileError){
      System.out.println("Input file not found.");
      System.out.println("Exiting.");
      System.exit(1);
    }catch(IOException ioExc){
      System.out.println("IO Exception.");
      System.out.println("Exiting.");
      System.exit(1);
    }
    return(content);
  }

  public static LinkedList<TreeNode> sortNodes(LinkedList<TreeNode> nodeList){
    //The only way I could have implemented something like PHP's usort functionality seems to have involved lots of pasted code.
    //So I opted to write my own sorting algorithm instead. It's not the greatest, but it should work for this purpose.
    LinkedList<TreeNode> sorted = nodeList;
    int i = 0;
    while(checkNodeList(sorted) != true){
      nodeList = swapSinglePair(nodeList);
    }
    return(sorted);
  }

  public static boolean checkNodeList(LinkedList<TreeNode> nodeList){
    //This method helps our sortNodes method to sense whether it needs to keep sorting or not.
    //See the caveat on that sortNodes function definition describing why this stuff is necessary.
    boolean result = true;
    int i = 0;
    //for(i=0;i<nodeList.size();i++){
    for(i=0;i<nodeList.size()-1;i++){
      if(nodeList.get(i).wordCount > nodeList.get(i+1).wordCount){
        result = false;
      }
    }
    return(result);
  }

  public static LinkedList<TreeNode> swapSinglePair(LinkedList<TreeNode> nodeList){
    //This is another helper for our sortNodes method. It swaps a single pair of nodes to marginally improve the list.    
    //See the caveat on that sortNodes function definition describing why all of this stuff is necessary.
    LinkedList<TreeNode> sorted = nodeList;
    int i = 0;
    TreeNode temporaryNode;
    for(i=0;i<nodeList.size()-1;i++){
      if(nodeList.get(i).wordCount > nodeList.get(i+1).wordCount){
        temporaryNode = nodeList.get(i+1);
        nodeList.set((i+1), nodeList.get(i));
        nodeList.set((i), temporaryNode);
      }
    }
    return(nodeList);
  }

  public static LinkedList<TreeNode> mergeNodes(LinkedList<TreeNode> nodeList){
    //This is a recursive function to merge nodes on our tree.
    //It takes in a list of nodes, then merges the two nodes with the lowest word counts.
    //If needed, it then calls itself.
    //It returns a list of all "survivor" nodes, including the new node.
    LinkedList<TreeNode> children = new LinkedList<TreeNode>();
    LinkedList<TreeNode> survivors = new LinkedList<TreeNode>();
    TreeNode parent = new TreeNode();
    int i = 0;
    LinkedList<TreeNode> sorted;
    //Sort our list by wordCount value, from lowest to highest ...
    sorted = sortNodes(nodeList);
    //... and create a parent node out of the two lowest-word-count nodes.
    children.add(sorted.get(0));
    children.add(sorted.get(1));
    parent.children = children;
    //This new parent node will have a wordCount value equivalent to the sum of the children.
    parent.wordCount = sorted.get(0).wordCount + sorted.get(1).wordCount;
    parent.word = sorted.get(0).word + "," + sorted.get(1).word;
    //Don't forget to assign the parent to the children!
    for(TreeNode child : children){
      child.parent = parent;
    }
    //The function now populates our new "survivors" nodeList containing the remaining words alongside the new parent node.
    //(Our counter starts at 2 in order to skip the two intentionally discarded nodes.)
    survivors.add(parent);
    for(i=2;i<sorted.size();i++){
      survivors.add(sorted.get(i));
    }
    if(survivors.size() > 1){
      survivors = mergeNodes(survivors);
    }
    return(survivors);
  }

  public static void printNodeList(LinkedList<TreeNode> nodeList, int nestLevel){
    //This function prints a node list, then calls itself to print any children.
    //In this way, it's a tiny bit like PHP's print_r function, albeit for our specialized purpose.
    int i = 0;
    String tab = "";
    for(i=0;i<nestLevel;i++){
      tab += "   ";
    }
    System.out.println("");
    for(i=0;i<nodeList.size();i++){
      System.out.println(tab+"Nesting level:");
      System.out.println(tab+nestLevel);
      System.out.println(tab+"Self:");
      System.out.println(tab+nodeList.get(i));
      System.out.println(tab+"Parent:");
      System.out.println(tab+nodeList.get(i).parent);
      System.out.println(tab+"Word(s):");
      System.out.println(tab+nodeList.get(i).word);
      System.out.println(tab+"Word Count:");
      System.out.println(tab+nodeList.get(i).wordCount);
      System.out.println(tab+"Children:");
      try{
        if(nodeList.get(i).children.size() > 0){
          printNodeList(nodeList.get(i).children,nestLevel+1);
        }
      }catch(NullPointerException myException){
        System.out.println(tab+"No children.");
      }
      System.out.println("");
    }
  }

  public static void main(String[] args) {
    //Finally, our main() method glues it all together.
    //It reads in data, assigns that data to a nodeList and sends it off to be processed.
    //It then prints the results.
    int i = 0;
    int j = 0;
    LinkedList<String> wordList = new LinkedList<String>();
    LinkedList<TreeNode> nodeList = new LinkedList<TreeNode>();
    LinkedList<TreeNode> result = new LinkedList<TreeNode>();
    //First read in our file
    String input = getInput();
    //Now replace all newline characters with spaces so we can make a flat array
    input = input.replace("\n"," ");
    //Now split with spaces as delimiters, giving us a bunch of words
    String[] inputArr = input.split(" ");
    //For each of our bunch of words ...
    for(i=0;i<inputArr.length;i++){
      TreeNode temporaryNode = new TreeNode();
      temporaryNode.word = inputArr[i];
      temporaryNode.wordCount = 1;
      if(wordList.contains(inputArr[i])){
        //If the word is in our existing list, increment the counter for the word
        for(TreeNode node : nodeList){
          if(node.word.equals(inputArr[i])){
            node.wordCount++;
          }
        }
      }else{
        //Otherwise, add the word to the existing list
        wordList.add(inputArr[i]);
        nodeList.add(temporaryNode);
      }
      temporaryNode = null;
    }
    //Now we use our function that takes in a list of nodes and recursively combines them, as shown in the diagram.
    nodeList = mergeNodes(nodeList);
    //Display the output ...
    printNodeList(nodeList,1);
    //Sign off ...
    System.out.print("\nAll done. Scroll back to the top of the output to see the final node. (Be sure your scroll buffer is long enough to show it all!) \n");
  }
}
