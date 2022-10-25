import java.io.*;
import java.util.*;
public class simRecord {
    private Queue<Word> WordPath;
    private Queue<Queue<Word>> wordLists;
    private WordTree toGame;
    public static void main(String[] args) {
        Queue<Word> stuff = new LinkedList<Word>();
        stuff.add(new Word("bofa"));
        stuff.add(new Word("deez"));
        stuff.add(new Word("nuts"));
        stuff.add(new Word("bruh"));
        simRecord test = new simRecord(stuff, null);
        System.out.println(test.WordPath.poll().getContent());
        test.clearFile();
    }
    public WordTree getToGame() {
        return toGame;
    }
    public void setToGame(WordTree toGame) {
        this.toGame = toGame;
    }
    public simRecord(Queue<Word> inputRecord, Queue<Queue<Word>> potentialMovesThatTurn) {
        WordPath = new LinkedList<Word>();
        WordPath.addAll(inputRecord);
        Queue<Word> copyInputRecord = new LinkedList<Word>();
        copyInputRecord.addAll(inputRecord);
        setToGame(new WordTree(copyInputRecord));
        wordLists = new LinkedList<Queue<Word>>();
        if(potentialMovesThatTurn!=null){
            wordLists.addAll(potentialMovesThatTurn);
        }
        saveToFile();
        WordPath.addAll(inputRecord);
    }
    public simRecord() {
    }
    private void saveToFile() {
        try {
            // Creates a Writer using FileWriter
            FileWriter output = new FileWriter("wordleRecords.txt", true);
            // Writes string to the file
            output.write("*Game starts\n");
            int trueSize = WordPath.size();
            recordGame(output,trueSize);
            output.write("Game took "+ trueSize+" turns to finish\n");
            output.write("*Game end\n");
            // Closes the writer
            output.close();
          }
          catch (Exception e) {
            e.getStackTrace();
          }
    }
    public void clearFile() {
        try {
            // Creates a Writer using FileWriter
            FileWriter output = new FileWriter("wordleRecords.txt");
            // Writes string to the file
            output.write("");
            output.close();
          }
          catch (Exception e) {
            e.getStackTrace();
          }
    }
    public void InsertIntoFile(String data) {
        try {
            // Creates a Writer using FileWriter
            FileWriter output = new FileWriter("wordleRecords.txt",true);
            // Writes string to the file
            output.write(data);
            output.close();
          }
          catch (Exception e) {
            e.getStackTrace();
          }
    }
    private void recordGame(FileWriter output, int trueSize) {
        for(int i = 0; i < trueSize; ++i){
            try {
                if(i == trueSize - 1){
                    output.write("Correct word here -> ");
                }
                output.write("Guess "+(i+1)+": "+WordPath.poll().getContent()+"\n\n");
                output.write("Potential other guesses at this point include: \n");
                if(!wordLists.isEmpty()){
                for(int j = 0; j < wordLists.peek().size(); ++j){
                    if(j < 50){
                        if(wordLists.peek().peek().getPriority() < 10000){
                            output.write("*"+wordLists.peek().poll().getContent()+"\t");
                        } else{
                            output.write(wordLists.peek().poll().getContent()+"\t");
                        }
                        if(j%10 == 9){
                            output.write("\n");
                        }
                    }
                    
                }
                if(wordLists.peek().size() > 50){
                    output.write("\nThere were "+(wordLists.peek().size()-50)+" other guesses not shown.");
                }
                output.write("\nTotal number of other potential guesses: "+(wordLists.poll().size()-1));
                output.write("\n\n\n");
            }else{
                output.write("No other potential guesses...");
            }
            }
            catch (Exception e) {
              e.getStackTrace();
            }
        }
    }
}
