import java.util.*;
public class Wordle {
    private Word correct;
    private int turnCount;
    private LetterBoard letters;
    private boolean solved;
    private boolean humanInput;
    private boolean easyMode;
    private boolean simulating;
    private WordleAI compy;
    private Queue<Word> inputRecord;
    private Queue<Queue<Word>> potentialMovesThatTurn;
    private Word userInitialGuess;
    private simRecord gameStateHistory;

    public static void main(String[] args) {
        Wordle demo = new Wordle();
        WordDataBase lookthru = new WordDataBase();
        demo.correct = new Word("train");
        System.out.println("Correct answer is five letters long");
        Word testInput = demo.acceptHumanInput(lookthru);
        System.out.println("You inputted: "+testInput.getContent());
    }
    public simRecord getGameStateHistory() {
        return gameStateHistory;
    }
    public void setGameStateHistory(simRecord gameStateHistory) {
        this.gameStateHistory = gameStateHistory;
    }
    public Wordle(){
        
    }
    public Word getUserInitialGuess() {
        return userInitialGuess;
    }
    public void setUserInitialGuess(Word userInitialGuess) {
        this.userInitialGuess = userInitialGuess;
    }
    public Queue<Word> getInputRecord() {
        return inputRecord;
    }
    public void setInputRecord(Queue<Word> inputRecord) {
        this.inputRecord = inputRecord;
    }
    public Wordle(boolean[] Settings, int SimSet, int wordLength){
        humanInput = true;
        easyMode = true;
        simulating = false;
        updateSetting(Settings);
        WordDataBase possibleAnswers = new WordDataBase();
        if(!humanInput || simulating){
            if(!simulating){
                compy = new WordleAI(9, wordLength);
            } else{
                compy = new WordleAI(SimSet, wordLength);
            }
        } 
        inputRecord = new LinkedList<Word>();
        potentialMovesThatTurn = new LinkedList<Queue<Word>>();        
        startUp(possibleAnswers, wordLength);
    }
    public Wordle(int SimSet, Word correct){
        this.correct = correct;
        humanInput = false;
        easyMode = false;
        simulating = true;
        WordDataBase possibleAnswers = new WordDataBase();
        compy = new WordleAI(SimSet, correct.getLength());
        inputRecord = new LinkedList<Word>();
        potentialMovesThatTurn = new LinkedList<Queue<Word>>();        
        startGame(correct,possibleAnswers);
    }
    public Wordle(int SimSet, Word correct, Word initialGuess){
        this.correct = correct;
        humanInput = false;
        easyMode = false;
        simulating = true;
        WordDataBase possibleAnswers = new WordDataBase();
        compy = new WordleAI(SimSet, correct.getLength());
        inputRecord = new LinkedList<Word>();
        potentialMovesThatTurn = new LinkedList<Queue<Word>>();
        userInitialGuess = initialGuess;
        compy.setUserInput(userInitialGuess);        
        startGame(correct,possibleAnswers);
    }
    private void turn(WordDataBase using) {
        Word input;
        if(!simulating){
            System.out.println("Attempt " +(turnCount+1));
            System.out.println("Guess a word that is "+correct.getLength()+" letters long, or type GIVE UP to quit.");
        }
        if(humanInput){
            input = acceptHumanInput(using);
        } else{
            //System.out.println("AI not ready yet :(");
            //input = "AI NO WORK";
            compy.setUserInput(userInitialGuess);
            compy.actions();
            input = compy.getGuess();
            if(!simulating){
                System.out.println(input.getContent());
            }
        }
        inputRecord.add(input);
        if(input == null){
            System.out.println("The compy AI gave up after "+(turnCount + 1)+" turns.");
            System.out.println("The word we were looking for was "+correct.getContent()+".");
            System.out.println("Damn AI is such an idiot sandwich");
        }
        else if(input.getContent().equals("GIVE UP")){
            System.out.println("You gave up after "+(turnCount + 1)+" turns.");
            System.out.println("The word we were looking for was "+correct.getContent()+".");
        }
        //else if(input.equals("AI NO WORK")){}
        else{
            GuessAndResult attempt = new GuessAndResult(correct, input);
            ++turnCount;
            if(!simulating){
                attempt.CharInfo();
                for(int i = 0; i < attempt.getCorrectPos().length; ++i){
                    System.out.print("Letter in position " + (i+1) + " is ");
                    if(attempt.getCorrectPos()[i]){
                        System.out.print("Correct. ");
                    } else{
                        System.out.print("Wrong. ");
                    }
                    System.out.print("\n");
                }
            }
            
            letters.Update(attempt.getCorrectCharsArray(), attempt.getCharShared(), attempt.getCharNotPresent());
            if(simulating || !humanInput){
                compy.record(letters, input,attempt.getCorrectPos());
                Queue<Word> recordThis = new LinkedList<Word>();
                recordThis.addAll(compy.getSmallDict());
                potentialMovesThatTurn.add(recordThis);
            }
            if(!simulating){
                letters.displayBoard();
            }
            
            if(attempt.getSolved()){
                if(!simulating){
                    System.out.println("You solved it after "+ turnCount+" turns.");
                }
                solved = true;
            } else{
                if(!simulating){
                System.out.println("Try again...");
                }
                if(!simulating || turnCount < 100){
                    turn(using);
                }else{
                    solved = false;
                }
            }
            
        }
    }
    private Word acceptHumanInput(WordDataBase using) {
        Scanner sc = new Scanner(System.in);
        String check = sc.nextLine();
        if(check.equals("GIVE UP")){
            return new Word("GIVE UP");
        }
        Word in = new Word(check);
        if(in.getLength() != correct.getLength()){
            System.out.println("Guess a word that is "+correct.getLength()+" letters long...");
            return acceptHumanInput(using);
        }
        for(int i = 0; i < in.getLength(); ++i){
            if(!Character.isLowerCase(check.charAt(i))){
                System.out.println("Guess a word that is "+correct.getLength()+" letters long...");
                return acceptHumanInput(using);
            }
        }
        if(using.WordInDataBase(in)){
            return in;
        }
        System.out.println("Guess a word that is "+correct.getLength()+" letters long...");
        return acceptHumanInput(using);
    }
    public void startGame(Word choose, WordDataBase using){
        correct = choose;
        letters = new LetterBoard();
        turn(using);
        letters = new LetterBoard();
        simRecord game = new simRecord(inputRecord, potentialMovesThatTurn);
        gameStateHistory = game;
        if(simulating||!humanInput){
            compy.deepClear();
        }
        if(!simulating){
            System.out.println("Play again? Type N to quit.");
            resetTurnCount();
            Scanner sc = new Scanner(System.in);
            String check = sc.nextLine();
            if(!check.equals("N")){
                startUp(using, choose.getLength());
            }
        }
    }
    private void startUp(WordDataBase using, int chosenNumber) { 
        Word choose;
        WordleAI correctChooser;
        if(easyMode){
            correctChooser = new WordleAI(2,chosenNumber);
        } else{
            correctChooser = new WordleAI(1,chosenNumber);
        }
        correctChooser.actions();
        choose = correctChooser.getGuess();
        startGame(choose, using);
    }
    public void updateSetting(boolean[] update){
        humanInput = update[0];
        easyMode = update[1];
        simulating = update[2];
    }
    public Word getCorrect(){
        return correct;
    }
    public boolean getSolved() {
        return solved;
    }
    public int getTurnCount() {
        return turnCount;
    }
    public void resetTurnCount(){
        turnCount = 0;
    }
}
