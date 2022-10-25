import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

public class WordTree {
    private Word content;
    private ArrayList<WordTree> branches;
    private FileWriter writeInto;
    private FileWriter perma;

    public static void main(String[] args) {
        WordTree test = new WordTree(new Word("mummy"), true);
        test.AddBranch(new Word("ghost"));
        test.AddBranch(new Word("toast"));
        test.AddBranch(new Word("ghost"));
        test.branches.get(1).AddBranch(new Word("robot"));
        test.branches.get(0).AddBranch(new Word("train"));
        test.branches.get(1).AddBranch(new Word("spike"));
        test.branches.get(1).AddBranch(new Word("robot"));
        test.AddBranch(new Word("slime"));
        test.displayTree(0);
        System.out.println("Tree 1 has cakes: "+test.contains(new Word("cakes")));
        System.out.println("=============");
        WordTree test2 = new WordTree(new Word("claim"));
        test2.AddBranch(new Word("cakes"));
        test2.AddBranch(new Word("spice"));
        test2.branches.get(0).AddBranch(new Word("sweet"));
        WordTree test3 = new WordTree(new Word("mummy"));
        test3.AddBranch(new Word("smite"));
        test3.AddBranch(new Word("ghost"));
        test3.branches.get(1).AddBranch(new Word("swine"));
        test.AddBranch(test2);
        test.AddBranch(test3);
        test.displayTree(0);
        System.out.println("Tree 1 has sweet: "+test.contains(new Word("sweet")));
        System.out.println("Found by going through, "+test.findPath(new Word("sweet")));
        System.out.println("Tree 1 has "+test.size()+" words.");
        test.saveToFile();
        WordTree copyTest = new WordTree();
        copyTest = copyTest.importTree("mummyTree.txt");
        copyTest.content = new Word("pizza");
        System.out.println("New tree 1 has sweet: "+copyTest.contains(new Word("sweet")));
        System.out.println("Found by going through, "+copyTest.findPath(new Word("sweet")));
        copyTest.saveToFile();
        copyTest.branches.get(3).saveToFile();
        copyTest.saveToFinal();
    }
    
    public WordTree(){}
    public WordTree(Word stuff,boolean createFile){
        content = stuff;
        branches = new ArrayList<WordTree>(0);
        if(createFile){
            try{
                writeInto = new FileWriter(content.getContent()+"Tree.txt");
                perma = new FileWriter(content.getContent()+"TreeFINAL.txt");
            }catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
    public WordTree(Word stuff){
        content = stuff;
        branches = new ArrayList<WordTree>(0);
    }

    public WordTree(Queue<Word> list){
        if(list != null && !list.isEmpty()){
            content = list.poll();
            branches = new ArrayList<WordTree>(0);
            if(!list.isEmpty()){
                branches.add(new WordTree(list));
            }
        }
    }
    public void AddBranch(Word stuff){
        if(!inBranches(stuff)){
            WordTree inserting = new WordTree(stuff);
            branches.add(inserting);
        } 
    }
    public void AddBranch(WordTree stuff){
        boolean Added = false;
        if(!contains(stuff.content)){
            branches.add(stuff);
            Added = true;
        } 
        if(!Added){
            if(!stuff.branches.isEmpty()){
                for(int i = 0; i < stuff.branches.size(); ++i){
                    int whichBranch = InWhichChild(stuff.branches.get(i).content);
                    if(whichBranch > -1){
                        branches.get(whichBranch).AddBranch(stuff.branches.get(i));
                    } else{
                        AddBranch(stuff.branches.get(i));
                    }
                }
            }
        }
    }
    private boolean inBranches(Word stuff) {
        if(branches.isEmpty()){
            return false;
        }
        for(int i = 0; i < branches.size(); ++i){
            if(branches.get(i).content.getContent().equals(stuff.getContent())){
                return true;
            }
        }
        return false;
    }
    public void commonUpdate() {
        WordDataBase knownWords = new WordDataBase();
        if(knownWords.WordInCommon(content)){
            content.addScore();
        }
    }
    public void displayTree(int levels) {
        for(int i = 0; i < levels; ++i){
            System.out.print(">\t");
        }
        commonUpdate();
        if(content.getPriority() > 9999){
            System.out.println(content.getContent());
        } else{
            System.out.println("*"+content.getContent());
        }
        if(!branches.isEmpty()){
            for(int i = 0; i < branches.size(); ++i){
                branches.get(i).displayTree(levels + 1);
            }
        }
    }
    public String printTreeFinal(int levels) {
        String out = "";
        for(int i = 0; i < levels; ++i){
            out = out + "-";
        }
        commonUpdate();
        if(content.getPriority() > 9999){
            out = out + content.getContent()+" ("+this.size()+")";
        } else{
            out = out +"*"+content.getContent()+" ("+this.size()+")";
        }
        if(!branches.isEmpty()){
            for(int i = 0; i < branches.size(); ++i){
                out = out + "\n"+branches.get(i).printTreeFinal(levels + 1);
            }
        }
        return out;
    }
    public String printTree(int levels) {
        String out = "";
        for(int i = 0; i < levels; ++i){
            out = out + "-";
        }
        commonUpdate();
        if(content.getPriority() > 9999){
            out = out + content.getContent();
        } else{
            out = out +"*"+content.getContent();
        }
        if(!branches.isEmpty()){
            for(int i = 0; i < branches.size(); ++i){
                out = out + "\n"+branches.get(i).printTree(levels + 1);
            }
        }
        return out;
    }
    public String printTreeRoot(int levels){
        String out = "";
        for(int i = 0; i < levels; ++i){
            out = out + "-";
        }
        commonUpdate();
        if(content.getPriority() < 9999){
            out = out +"*";
        }
        out = out + content.getContent();
        return out;
    }
    public ArrayList<WordTree> getBranches(){
        return branches;
    }
    public void sendToFile(){
        simRecord recording = new simRecord();
        recording.InsertIntoFile(printTree(0));
    }
    public boolean contains(Word target){
        if(content == null){
            return false;
        }
        if(content.getContent().equals(target.getContent())){
            return true;
        } else if(!branches.isEmpty()){
            for(int i = 0; i < branches.size(); ++i){
                if(branches.get(i).contains(target)){
                    return true;
                }
            }
        }
        return false; 
    }
    public int InWhichChild(Word target){
        for(int i = 0; i < branches.size(); ++i){
            if(branches.get(i).contains(target)){
                return i;
            }
        }
        return -1;
    }
    public void saveToFile() {
        try {
            WholeTreeToFile(0, writeInto);
            // Closes the writer
            writeInto.close();
          }
          catch (Exception e) {
            e.getStackTrace();
          }
    }
    public void saveToFinal() {
        try {
            WholeTreeToFileFinal(0, perma);
            // Closes the writer
            perma.close();
          }
          catch (Exception e) {
            e.getStackTrace();
          }
    }
    public void WholeTreeToFile(int level, FileWriter target) {
        try {
            if(target == null){
                target = new FileWriter(content.getContent()+"Tree.txt");
            }
            target.write(printTreeRoot(level) + "\n");
            if(!branches.isEmpty()){
                ++level;
                for(int i = 0; i < branches.size(); ++i){
                    branches.get(i).WholeTreeToFile(level,target);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
          }

    }
    public void WholeTreeToFileFinal(int level, FileWriter target) {
        try {
            if(target == null){
                target = new FileWriter(content.getContent()+"TreeFINAL.txt");
            }
            target.write(printTreeRoot(level)+" ("+this.size()+")\n");
            if(!branches.isEmpty()){
                ++level;
                for(int i = 0; i < branches.size(); ++i){
                    branches.get(i).WholeTreeToFileFinal(level,target);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
          }

    }

    public int size(){
        if(content == null){
            return 0;
        }
        if(branches.isEmpty()){
            return 1;
        }
        else{
            int size = 1;
            for(int i = 0; i < branches.size(); ++i){
                size = size + branches.get(i).size();
            }
            return size;
        }
    }
    public String findPath(Word target){
        if(!contains(target)){
            return target.getContent()+" not in this tree with root of "+content.getContent();
        }
        if(target.equals(content)){
            return content.getContent();
        }
        for(int i = 0; i < branches.size();++i){
            if(branches.get(i).contains(target)){
                return content.getContent()+" -> "+branches.get(i).findPath(target);
            }
        }
        return null;
    }
    public WordTree importTree(String fileName){
        try{
            FileReader readFrom = new FileReader(fileName); 
            Scanner scanner = new Scanner(readFrom);
            WordTree copy = new WordTree();
            while (scanner.hasNextLine()) {
            String importing = scanner.nextLine();
            // process the line
                importing = removeStar(importing);
                int dashCheck = dashNumber(importing);
                copy.addToTree(importing,dashCheck);
            }
            scanner.close();
            
          return copy;


        }catch (Exception e) {
            e.getStackTrace();
            return null;
          }
    }

    private int dashNumber(String importing) {
        if(!importing.contains("-")){
            return 0;
        }
        return 1 + dashNumber(importing.substring(1));
    }

    public void addToTree(String importing,int level) {
        if(level == 0){
            content = new Word(importing);
            branches = new ArrayList<WordTree>();
        }
        else if(level == 1){
            branches.add(new WordTree(new Word(importing.substring(1))));
        }
        else{
            branches.get(branches.size()-1).addToTree(importing.substring(1), --level);
        }
    }

    private String removeStar(String in) {
        if(in.contains("*")){
            if(in.length() - 1 > in.indexOf("*")){
                return in.substring(0,in.indexOf("*"))+in.substring(in.indexOf("*")+1);
            }
            return in.substring(0,in.indexOf("*"));
        }
        return in;
    }
}
