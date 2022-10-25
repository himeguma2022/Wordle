import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class PlayGame {
    private boolean[] settings;
    public static void main(String[] args) {
        PlayGame play = new PlayGame();
    }
    public PlayGame(){
        settings = new boolean[3];
        settings[0] = true;
        settings[1] = true;
        menu();
    }
    public void menu(){
        System.out.println("Choose an option based on the keys.");
        System.out.println("P -> play");
        System.out.println("O -> options");
        System.out.println("S -> simulate (let AI play while you watch)");
        System.out.println("V -> solver (Use this AI to solve!)");
        System.out.println("R -> generate random word for solving wordles");
        System.out.println("T -> test an AI on a set correct word");
        System.out.println("I -> Test an initial guess against a whole database");
        System.out.println("Q -> quit");
        char[] key = {'P','O','S','Q','V','R','T','I'};
        char choice = LookForInput(key); 
        switch(choice){
            case 'P':
            if(settings[1]){
                System.out.println("How long do you want the word to guess to be? Between 2 to 9 characters");
                Wordle play = new Wordle(settings,0,LookForInput(2,9));
            } else{
                System.out.println("How long do you want the word to guess to be? Between 2 to 15 characters");
                Wordle play = new Wordle(settings,0,LookForInput(2,15));
            }
            break;
            case 'O':
            options();
            break;
            case 'S':
            settings[2] = true;
            settings[0] = false;
            simulate(settings);
            settings[2] = false;
            settings[0] = true;
            break;
            case 'Q':
            System.out.println("Bye bye!");
            System.exit(0);
            case 'V':
            solver();
            break;
            case 'R':
            randomWordGenerator();
            break;
            case 'T':
            testBot();
            break;
            case 'I':
            initialGuessTest();
            break;
        }
        menu();
    }
    private void initialGuessTest() {
        System.out.println("test a first guess answer.");
        Word input = new Word(acceptHumanInput());
        simRecord recordInsert = new simRecord();
        int totalGuesses = 0;
        int testsPassed = 0;
        WordDataBase wholeBase = new WordDataBase();
        Queue<Word> correctAnsToUse = newDataBase(wholeBase, input.getLength());
        int setLimit = correctAnsToUse.size();
        WordTree displayTree = new WordTree();
        displayTree = displayTree.importTree(input.getContent()+"Tree.txt");
        if(displayTree == null){
            displayTree = new WordTree(input);
        }
        if(setLimit > 10){
            setLimit = 10;
        }
        recordInsert.InsertIntoFile("*TEST FOR "+input.getContent()+" AS FIRST GUESS\n");
        for(int i = 0; i < setLimit; ++i){
            int magicNumber = (int)Math.floor(Math.random()*(correctAnsToUse.size()-1+1));
            for(int j = 0; j < magicNumber; ++j){
                correctAnsToUse.add(correctAnsToUse.poll());
            }
            Word Chosen = correctAnsToUse.poll();
            recordInsert.InsertIntoFile("FOR CORRECT ANSER BEING: "+Chosen.getContent()+" \n");
            if(correctAnsToUse != null){
                Wordle test = new Wordle(-1, Chosen,input);
                displayTree.AddBranch(test.getGameStateHistory().getToGame());
                if(test.getTurnCount() < 7){
                    ++testsPassed;
                    recordInsert.InsertIntoFile("TEST #"+(i+1)+ " PASSED\n");
                } else{
                    recordInsert.InsertIntoFile("TEST #"+(i+1)+ " FAILED\n");
                }
                totalGuesses = totalGuesses + test.getTurnCount();
                correctAnsToUse = takeOutNGWord(Chosen, correctAnsToUse);
                for(int j = 1; j < 101; ++j){
                    if(i == (int)(j*setLimit*0.01)){
                        System.out.println(j+"% done!");
                    }
                }
            }
        }
        //displayTree.displayTree(0);
        //displayTree.printTreeByLine(0);
        displayTree.sendToFile();
        displayTree.printTree(0);
        recordInsert.InsertIntoFile("\n");
        double passRate = (double)(100*testsPassed/setLimit);
        double averageTurns = (double)(totalGuesses/setLimit);
        System.out.println("AI passed this test "+passRate+"% of the time.");
        System.out.println("AI took an average of "+averageTurns+" to solve.");
        recordInsert.InsertIntoFile("AI passed this test "+passRate+"% of the time.\n");
        recordInsert.InsertIntoFile("AI took an average of "+averageTurns+" to solve.\n");
        recordInsert.InsertIntoFile("*TEST END\n");
    }
    private void testBot() {
        System.out.println("test a correct answer.");
        Word input = new Word(acceptHumanInput());
        System.out.println("Choose a bot to use");
        int AIMode = LookForInput(11);
        System.out.println("How many times we testing "+input.getContent()+" on this bot?");
        simRecord recordInsert = new simRecord();
        int testingTimes = LookForInput(10000);
        int totalGuesses = 0;
        int testsPassed = 0;
        recordInsert.InsertIntoFile("*TEST FOR "+input.getContent()+" WITH "+testingTimes+" TESTS\n");
        for(int i = 0; i < testingTimes; ++i){
            Wordle test = new Wordle(AIMode, input);
            if(test.getTurnCount() < 7){
                ++testsPassed;
                recordInsert.InsertIntoFile("TEST #"+(i+1)+ " PASSED\n");
            } else{
                recordInsert.InsertIntoFile("TEST #"+(i+1)+ " FAILED\n");
            }
            totalGuesses = totalGuesses + test.getTurnCount();
        }
        double passRate = (double)(100*testsPassed/testingTimes);
        double averageTurns = (double)(totalGuesses/testingTimes);
        System.out.println("AI passed this test "+passRate+"% of the time.");
        System.out.println("AI took an average of "+averageTurns+" to solve.");
        recordInsert.InsertIntoFile("AI passed this test "+passRate+"% of the time.\n");
        recordInsert.InsertIntoFile("AI took an average of "+averageTurns+" to solve.\n");
        recordInsert.InsertIntoFile("*TEST END\n");
    }
    private void randomWordGenerator() {
        System.out.println("Need to specify length? Y(yes)/N(no)");
        char[] choices = {'Y','N'};
        char input = LookForInput(choices);
        Word chosen = null;
        switch(input){
            case 'Y':
            System.out.println("How many letters will be in this word?");
            WordleAI ChooseWord = new WordleAI(1,LookForInput(2,15));
            ChooseWord.actions();
            chosen = ChooseWord.getGuess();
            break;
            case 'N':
            WordDataBase choosingFrom = new WordDataBase();
            chosen = choosingFrom.randomSelectAllWords();
            break;
        }
        System.out.println("Word being used is: "+chosen.getContent());
        System.out.println("Want to use in solver? Y(yes)/N(no)");
        input = LookForInput(choices);
        switch(input){
            case 'Y':
            solver(chosen);
            case 'N':
            System.out.println("Choose another word? Y(yes)/N(no)");
            input = LookForInput(choices);
            switch(input){
                case 'Y':
                randomWordGenerator();
                break;
            }
            break;
        }
    }    
    private void solver(Word chosen) {
        Scanner sc = new Scanner(System.in);
        String input;
        Word lastInput = chosen;
        System.out.println("Letters not in target word, type as a word.");
        input = acceptHumanInput();
        Word nonPresentLetters = new Word(input);
        System.out.println("Letters in target word...");
        input = acceptHumanInput();
        Word PresentLetters = new Word(input);
        System.out.println("Letters guessed correctly with positions, unknown letters in word type *.");
        input = acceptHumanInput();
        Word LettersInWord = new Word(input);
        LetterBoard clues = new LetterBoard();
        int trueSize = 0;
        boolean[] pos = new boolean[input.length()];
        for(int i = 0 ; i < input.length(); ++i){
            pos[i] = false;
            if(LettersInWord.getChars()[i]!='*'){
                pos[i] = true;
            }
        }
        for(int i = 0; i < LettersInWord.getContains().length; ++i){
            if(LettersInWord.getContains()[i]!='*'){
                trueSize++;
            }
        }
        char[] knownCorrectPosLetters;
        if(trueSize == 0){
            knownCorrectPosLetters = null;
        } else{
            knownCorrectPosLetters = new char[trueSize];
        int InsertInto = 0;
            for(int i = 0; i < LettersInWord.getContains().length; ++i){
                if(LettersInWord.getContains()[i] != '*'){
                    knownCorrectPosLetters[InsertInto] = LettersInWord.getContains()[i];
                    ++InsertInto;
                }
            }
        }
        clues.Update(knownCorrectPosLetters, PresentLetters.getContains(), nonPresentLetters.getContains());
        WordleAI predicter = new WordleAI(9,lastInput.getLength());
        predicter.record(clues, lastInput, pos);
        predicter.actions();
        predicter.commonUpdate();
        Queue<Word> possibleWords = predicter.getSmallDict();
        trueSize = possibleWords.size();
        System.out.println("Possible word at this point ("+trueSize+")");
        for(int i = 0; i < trueSize; ++i){
            System.out.print(possibleWords.poll().getContent()+"\t");
            if(i%10 == 9){
                System.out.print("\n");
            }
        }
        System.out.println("\nPossible word at this point ("+trueSize+")");
        predicter.deepClear();
        System.out.println("solved? -> Y(yes)/N(no)");
        String[] choices = {"Y","N"};
        input = acceptHumanInput(choices);
        switch(input.charAt(0)){
            case 'N':
            solver();
            break;
        }
    }
    private void solver() {
        Scanner sc = new Scanner(System.in);
        String input;
        System.out.println("Last input?");
        WordDataBase Using = new WordDataBase();
        input = acceptHumanInput(Using);
        Word lastInput = new Word(input);
        System.out.println("Letters not in target word, type as a word.");
        input = acceptHumanInput();
        Word nonPresentLetters = new Word(input);
        System.out.println("Letters in target word...");
        input = acceptHumanInput();
        Word PresentLetters = new Word(input);
        System.out.println("Letters guessed correctly with positions, unknown letters in word type *.");
        input = acceptHumanInput();
        Word LettersInWord = new Word(input);
        LetterBoard clues = new LetterBoard();
        int trueSize = 0;
        boolean[] pos = new boolean[input.length()];
        for(int i = 0 ; i < input.length(); ++i){
            pos[i] = false;
            if(LettersInWord.getChars()[i]!='*'){
                pos[i] = true;
            }
        }
        for(int i = 0; i < LettersInWord.getContains().length; ++i){
            if(LettersInWord.getContains()[i]!='*'){
                trueSize++;
            }
        }
        char[] knownCorrectPosLetters;
        if(trueSize == 0){
            knownCorrectPosLetters = null;
        } else{
            knownCorrectPosLetters = new char[trueSize];
        int InsertInto = 0;
            for(int i = 0; i < LettersInWord.getContains().length; ++i){
                if(LettersInWord.getContains()[i] != '*'){
                    knownCorrectPosLetters[InsertInto] = LettersInWord.getContains()[i];
                    ++InsertInto;
                }
            }
        }
        clues.Update(knownCorrectPosLetters, PresentLetters.getContains(), nonPresentLetters.getContains());
        WordleAI predicter = new WordleAI(9,lastInput.getLength());
        predicter.record(clues, lastInput, pos);
        predicter.actions();
        predicter.commonUpdate();
        Queue<Word> possibleWords = predicter.getSmallDict();
        trueSize = possibleWords.size();
        System.out.println("Possible word at this point ("+trueSize+")");
        for(int i = 0; i < trueSize; ++i){
            System.out.print(possibleWords.poll().getContent()+"\t");
            if(i%10 == 9){
                System.out.print("\n");
            }
        }
        System.out.println("\nPossible word at this point ("+trueSize+")");
        predicter.deepClear();
        System.out.println("solved? -> Y(yes)/N(no)");
        String[] choices = {"Y","N"};
        input = acceptHumanInput(choices);
        switch(input.charAt(0)){
            case 'N':
            solver();
            break;
        }
    }
    private String acceptHumanInput(String[] choices) {
        Scanner sc = new Scanner(System.in);
        String check = sc.nextLine();
        for(int i = 0; i < choices.length; ++i){
            if(check.equals(choices[i])){
                return check;
            }
        }
        return acceptHumanInput(choices);
    }
    private String acceptHumanInput() {
        Scanner sc = new Scanner(System.in);
        String check = sc.nextLine();
        for(int i = 0; i < check.length(); ++i){
            if(!Character.isLowerCase(check.charAt(i))&&!(check.charAt(i)=='*')){
                return acceptHumanInput();
            }
        }
        return check;
    }
    private String acceptHumanInput(WordDataBase using) {
        Scanner sc = new Scanner(System.in);
        String check = sc.nextLine();
        for(int i = 0; i < check.length(); ++i){
            if(!Character.isLowerCase(check.charAt(i))){
                return acceptHumanInput(using);
            }
        }
        if(using.WordInDataBase(new Word(check))){
            return check;
        }
        return acceptHumanInput(using);
    }
    private void simulate(boolean[] simSet) {
        System.out.println("Choose AI mode: ");
        int mode = LookForInput(11);
        System.out.println("Games to simualate: (won't display correct words for simulations with more than fifty games)");
        int gamesToPlay = LookForInput(10000);
        System.out.println("For words what length? ");
        System.out.println("Minimum word length: 2");
        System.out.println("Maximum word length: 10 (easy), 15 (not easy)");
        int wordLength;
        if(settings[1]){
            wordLength = LookForInput(2,10);
        }
        else{
            wordLength = LookForInput(2,15);
        }
        int solves = 0;
        int lessThanSix = 0;
        int turnCountSum = 0;
        for(int i = 0; i < gamesToPlay; ++i){
            Wordle sim = new Wordle(simSet, mode, wordLength);
            if(gamesToPlay < 51){
                System.out.println("Correct word: "+sim.getCorrect().getContent()+" -> been solved by AI: "+sim.getSolved()+ " in "+sim.getTurnCount()+ " turns.");
            }
            turnCountSum = turnCountSum+sim.getTurnCount();
            if(sim.getSolved()){
                solves++;
            }
            if(sim.getTurnCount() < 7){
                lessThanSix++;
            }
            sim.resetTurnCount();
            if(gamesToPlay > 50){
                for(int j = 1; j < 101; ++j){
                    if(i == (int)(j*gamesToPlay*0.01)){
                        System.out.println(j+"% done!");
                    }
                }
            }
            
        }
        double solveRate = 100*solves/gamesToPlay;
        double lessThanSixRate = 100*lessThanSix/gamesToPlay;
        System.out.println("Reasonable solve rate = "+solveRate+"%");
        System.out.println("Six or less rate = "+lessThanSixRate+"%");
        if(solves == gamesToPlay){
            double mean = (turnCountSum/gamesToPlay);
            System.out.println("Average number of turns to solve: "+ mean);
        }
    }
    private int LookForInput(int maxNum) {
        int input;
        try{Scanner sc = new Scanner(System.in);
            input = sc.nextInt();
        } catch(InputMismatchException e){
            return LookForInput(maxNum);
        }
        
        if(input > 0 && input < maxNum + 1){
            return input;
        }
        return LookForInput(maxNum);
    }
    private int LookForInput(int minNum, int maxNum) {
        int input;
        try{Scanner sc = new Scanner(System.in);
            input = sc.nextInt();
        } catch(InputMismatchException e){
            return LookForInput(maxNum);
        }
        
        if(input > minNum-1 && input < maxNum + 1){
            return input;
        }
        return LookForInput(maxNum);
    }
    private void options() {
        System.out.println("Right now, ");
        System.out.println("Easy mode: "+settings[1]);
        System.out.println("Human mode: " +settings[0]);
        System.out.println("Press E to change difficulty, and press H to change between computer and human player.");
        System.out.println("Press C to clear records");
        System.out.println("Press B to go back to menu");
        char[] chooseFrom = {'E','H','B','C'};
        char choose =  LookForInput(chooseFrom);
        switch(choose){
            case 'E':
            this.settings[1] = !settings[1];
            options();
            break;
            case 'H':
            this.settings[0] = !settings[0];
            options();
            break;
            case 'C':
            clearFile();
            options();
            break;
        }
        
    }
    private void clearFile() {
        simRecord delete = new simRecord();
        delete.clearFile();
    }
    public char LookForInput(char[] keys){
        char input;
        try{Scanner sc = new Scanner(System.in);
            input = sc.next().charAt(0);
        } catch(InputMismatchException e){
            return LookForInput(keys);
        }
        
        for(int i = 0; i < keys.length; ++i){
            if(keys[i] == input){
                return input;
            }
        }
        return LookForInput(keys);
    }
    private Queue<Word> newDataBase(WordDataBase origin, int goalLength) {
        Queue<Word> superDataBase;
        Queue<Word> portionBase = new LinkedList<Word>();
        if(!settings[1]){
            superDataBase = origin.getSuperDataBase();
        } else{
            superDataBase = origin.getCommon();
        }
        for(int i = 0; i < superDataBase.size(); ++i){
            if(superDataBase.peek().getLength() == goalLength){
                portionBase.add(superDataBase.peek());
            }
            superDataBase.add(superDataBase.poll());
        }
        return portionBase;
    }
    public Queue<Word> takeOutNGWord(Word target, Queue<Word> outBase){
        for(int i = 0; i < outBase.size(); ++i){
            if(outBase.peek().equals(target)){
                outBase.poll();
            } else{
                outBase.add(outBase.poll());
            }
        }
        return outBase;
    }
}
