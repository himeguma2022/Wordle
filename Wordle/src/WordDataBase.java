import java.io.*;
import java.util.*;
public class WordDataBase {
    private Queue<Word> SuperDataBase;
    private Queue<Word> Common;
    public static void main(String[] args) {
        WordDataBase demo = new WordDataBase();
        System.out.println(demo.Common.peek().getContent());
        System.out.println(demo.SuperDataBase.peek().getContent());
        System.out.println("Word with five letters and highest priority score is: "+demo.highestPriority(5));
        System.out.println("Random word chosen: "+demo.RandomWord(demo.Common).getContent());
        System.out.println("brain is in the list: "+demo.WordInDataBase(new Word("brain")));
        System.out.println("sugma is in the list: "+demo.WordInDataBase(new Word("sugma")));
    }
    public String highestPriority(int length){
        int scoreToBeat = 0;
        String out = "";
        for(int i = 0; i < SuperDataBase.size(); ++i){
            if(SuperDataBase.peek().getContent().length() == length&&SuperDataBase.peek().getPriority()>=scoreToBeat){
                if(SuperDataBase.peek().getPriority()==scoreToBeat){
                    System.out.println("Tying with "+SuperDataBase.peek().getContent());
                } else{
                    out = SuperDataBase.peek().getContent();
                    scoreToBeat = SuperDataBase.peek().getPriority();
                }
            }
            Word TakenOut = SuperDataBase.poll();
            SuperDataBase.add(TakenOut);
        }
        return out;
    }
    public WordDataBase(){
        try {  
            // Create f1 object of the file to read data
            File f1 = new File("Wordle/src/oneThousandMostCommon.txt");    
            Scanner dataReader = new Scanner(f1);
            Common = new LinkedList<Word>();
            while (dataReader.hasNextLine()) {  
                Common.add(new Word(dataReader.nextLine().toLowerCase()));  
            }  
            dataReader.close(); 
        } catch (FileNotFoundException exception) {  
            System.out.println("Unexcpected error occurred!");  
            exception.printStackTrace();  
        }
        try {  
            // Create f1 object of the file to read data  
            File f1 = new File("Wordle/src","Scrable.txt");    
            Scanner dataReader = new Scanner(f1);
            SuperDataBase = new LinkedList<Word>();
            while (dataReader.hasNextLine()) {  
                SuperDataBase.add(new Word(dataReader.nextLine().toLowerCase())); 
            }  
            dataReader.close(); 

        } catch (FileNotFoundException exception) {  
            System.out.println("Unexcpected error occurred!");  
            exception.printStackTrace();  
        }
    }
    public Word RandomWord(Queue<Word> fromSet){
        int magicNumber = (int)Math.floor(Math.random()*(fromSet.size()-1+1));
        for(int i = 0; i < magicNumber; ++i){
            fromSet.add(fromSet.poll());
        }
        return fromSet.peek();
    }
    public Queue<Word> getSuperDataBase(){
        return SuperDataBase;
    }

    public Queue<Word> getCommon(){
        return Common;
    }
    public void findFile(String name,File file)
    {
        File[] list = file.listFiles();
        if(list!=null)
        for (File fil : list)
        {
            if (fil.isDirectory())
            {
                findFile(name,fil);
            }
            else if (name.equalsIgnoreCase(fil.getName()))
            {
                System.out.println(fil.getParentFile());
            }
        }
    }
    public Word randomSelectAllWords(){
        return RandomWord(SuperDataBase);
    }
    public Word randomSelectCommonWords(){
        return RandomWord(Common);
    }

    public Word takeOutFromCommon(){
        Word out = Common.poll();
        AddBackToCommon(out);
        return out;
    }

    public Word takeAndAddSuper(){
        Word out = SuperDataBase.poll();
        AddBackToSuper(out);
        return out;
    }
    public void AddBackToCommon(Word removed){
        Common.add(removed);
    }

    public void AddBackToSuper(Word removed){
        SuperDataBase.add(removed);
    }

    public boolean WordInDataBase(Word target){
        for(int i = 0; i < SuperDataBase.size(); ++i){
            if(target.equals(takeAndAddSuper())){
                return true;
            }
        }
        return false;
    }
    public boolean WordInCommon(Word target){
        for(int i = 0; i < Common.size(); ++i){
            if(target.equals(takeOutFromCommon())){
                return true;
            }
        }
        return false;
    }
}