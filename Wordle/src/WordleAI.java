import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class WordleAI {
    //memory
    private WordDataBase iKnowTheseWords;
    //inputs
    private char[] haventGuessed;
    private char[] notInWord;
    private char[] inWord;
    private char[] partialWordKnown;
    private int wordLength;
    private Word lastInput;
    private Queue<Word> smallDict;
    private analysisBoard letterMap;
    private analysisBoard prevAnalysisBoard;
    private Word userInput;
    //outputs
    private String guess;
    //states
    private int mode;
    private boolean initialGuessMade = false;
    private int turnNum = 0;
    //devModeStats
    private int chooseWordCalls = 0;
    public static void main(String[] args) {
        for(int i = 0; i < 30; ++i){
            WordleAI test = new WordleAI(9,5);
            LetterBoard clues = new LetterBoard();
            Word testInput = new Word("greed");
            char[] correctFromGuess = test.fromString("e");
            char[] inTheWord = test.fromString("er");
            char[] notInTarget = test.fromString("whitpomsckangd");
            boolean[] rightLettersInWord = {false, false, true, false, false};
            clues.Update(correctFromGuess, inTheWord, notInTarget);
            test.record(clues, testInput, rightLettersInWord);
            test.actions();
            System.out.println("Guess "+(i+1)+": "+test.guess+", this means AI working is: "+test.checkString());
            test.clearMem();
        }
    }
    public boolean checkString(){
        if(mode > 2){
            for(int i = 0; i < guess.length(); ++i){
                for(int j = 0; j < notInWord.length; ++j){
                    if(notInWord[j]==guess.charAt(i)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public WordleAI(){
        iKnowTheseWords = new WordDataBase();
        mode = 0;
    }

    public WordleAI(int mode, int length){
        iKnowTheseWords = new WordDataBase();
        this.wordLength = length;
        smallDict = newDataBase();
        this.mode = mode;
    }

    private Queue<Word> newDataBase() {
        Queue<Word> superDataBase = iKnowTheseWords.getSuperDataBase();
        Queue<Word> portionBase = new PriorityQueue<Word>(1, new Comparator<Word>(){
        public int compare(Word word1, Word word2){
            if(word1.getPriority() > word2.getPriority()){
                return -1;
            }
            if(word1.getPriority() < word2.getPriority()){
                return 1;
            }
            return 0;
        }});
        for(int i = 0; i < superDataBase.size(); ++i){
            if(superDataBase.peek().getLength() == wordLength){
                portionBase.add(superDataBase.peek());
            }
            superDataBase.add(superDataBase.poll());
        }
        return portionBase;
    }
    public WordleAI copy(WordleAI using){
        return this;
    }
    public int getWordLength() {
        return wordLength;
    }
    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public Word getGuess() {
        return new Word(guess);
    }

    public void actions() {
        if(mode != 0){
            switch(mode){
                case 1:
                RandomAI();
                break;
                case 2:
                CommonRandomAI();
                break;
                case 3:
                ChooseFromLettersPossible();
                break;
                case 4:
                UnknownPositions();
                break;
                case 5:
                UseKnownPositionsAndMemory();
                break;
                case 6:
                InitialGuess();
                break;
                case 7:
                InitialGuessAndLetterRecord();
                break;
                case 8:
                LetterRecord();
                break;
                case 9:
                guess = priorityGuess().getContent();
                break;
                case 10:
                randomInitalCommonGuess(initialGuessMade);
                break;
                case 11:
                guess = facetbourdkingslymph().getContent();
                break;
                case -1:
                guess = userInput().getContent();
            }
        }
        chooseWordCalls = 0;
    }
    private Word userInput() {
        if(!initialGuessMade){
            initialGuessMade = true;
            commonUpdate();
            return userInput;
        }
        return priorityGuess();
    }

    public void setUserInput(Word in){
        userInput = in;
    }
    private void randomInitalCommonGuess(boolean executedOnce) {
        if(!executedOnce){
            guess = chooseCommonWord().getContent();
            smallDict = newDataBase();
            takeOutNGWord(new Word(guess));
            commonUpdate();
            initialGuessMade = true;
        }
        else{
            guess = priorityGuess().getContent();
            resetCommon();
        }
    }
    private void commonWordOnly() {
        Queue<Word> smallDictCopy = new LinkedList<Word>();
        smallDictCopy.addAll(smallDict);
        int timesToX = smallDict.size();
        for(int i = 0; i < timesToX; ++i){
            smallDict.poll();
            Word lookAt = smallDictCopy.poll();
            for(int j = 0; j < iKnowTheseWords.getCommon().size(); ++j){
                Word compareWith = iKnowTheseWords.getCommon().poll();
                if(lookAt.equals(compareWith)){
                    smallDictCopy.add(lookAt);
                }
                iKnowTheseWords.AddBackToCommon(compareWith);
            }
        }
        smallDict.addAll(smallDictCopy);
    }
    private void resetCommon() {
        for(int i = 0; i < smallDict.size(); ++i){
            if(smallDict.peek().getPriority()>9999){
                Word reInsert = smallDict.poll();
                reInsert.bringDown();
                smallDict.add(reInsert);
            }
        }
    }
    private void LetterRecord() {
        guess = RecordLetterGuess().getContent();
    }
    private void InitialGuessAndLetterRecord() {
        if(lastInput == null){
            switch(wordLength){
                case 2: 
                guess = "es";
                break;
                case 3:
                guess = "sea";
                break;
                case 4: 
                guess = "rise";
                break;
                case 5:
                guess = "raise";
                break;
                case 6:
                guess = "senior";
                break;
                case 7:
                guess = "english";
                break;
                case 8:
                guess = "includes";
                break;
                case 9:
                guess = "published";
                break;
                case 10:
                guess = "previously";
                break;
                case 11:
                guess = "temperature";
                break;
                default:
                guess = RecordLetterGuess().getContent();
            };
        } else{
            guess = RecordLetterGuess().getContent();
        }

    }
    private Word RecordLetterGuess() {
        intelligentUpdateList();
        return chooseWord();
        
    }
    private void InitialGuess() {
        if(lastInput == null){
            switch(wordLength){
                case 2: 
                guess = "es";
                break;
                case 3:
                guess = "sea";
                break;
                case 4: 
                guess = "rise";
                break;
                case 5:
                guess = "raise";
                break;
                case 6:
                guess = "senior";
                break;
                case 7:
                guess = "english";
                break;
                case 8:
                guess = "includes";
                break;
                case 9:
                guess = "published";
                break;
                case 10:
                guess = "previously";
                break;
                case 11:
                guess = "temperature";
                break;
                default:
                guess = Mem().getContent();
            };
        } else{
            guess = Mem().getContent();
        }

    }

    private void UseKnownPositionsAndMemory() {
        guess = Mem().getContent();
    }

    private Word Mem() {
        Word chosen = withPos();
        if(chosen == null||lastInput == null){
            return null;
        }
        if(chosen.equals(lastInput)){
            return Mem();
        }
        return chosen;
    }

    private Word withPos() {
        Word chosen = algoOnlyKnowLetters();
        if(chosen == null){
            return null;
        }
        boolean passing = true;
        for(int i = 0; i < wordLength; ++i){
            if(partialWordKnown !=null){
            if(partialWordKnown[i]!='*'){
                if(partialWordKnown[i]!=chosen.getChars()[i]){
                    passing = false;
                }
            }
        }
        }
        if(passing){
            return chosen;
        }
        takeOutNGWord(chosen);
        return withPos();
    }

    private void UnknownPositions() {
        guess = algoOnlyKnowLetters().getContent();
    }

    private Word algoOnlyKnowLetters() {
        filterOutWords();
        return algoFromLettersNotInWord();
    }

    private void ChooseFromLettersPossible() {
        guess = algoFromLettersNotInWord().getContent();
    }

    private Word algoFromLettersNotInWord() {
        filterOutWords();
        return chooseWord();
    }
    public void takeOutNGWord(Word target){
        for(int i = 0; i < smallDict.size(); ++i){
            if(smallDict.peek().equals(target)){
                smallDict.poll();
            } else{
                smallDict.add(smallDict.poll());
            }
        }
    }
    public void filterOutWords(){
        for(int i = 0; i < smallDict.size(); ++i){
            Word examine = smallDict.poll();
            if(doesWordHaveAllLegalLetters(examine)&&doesWordHaveNeededLetters(examine)){
                smallDict.add(examine);
            }
        }
    }

    private void CommonRandomAI() {
        commonWordOnly();
        guess = chooseWord().getContent();
    }

    private void RandomAI() {
        guess = chooseWord().getContent();
    }

    public void setMode(int num){
        mode = num;
    }
    private Word chooseWord() {
        Queue<Word> Arrangeable = new LinkedList<Word>();
        Arrangeable.addAll(smallDict);
        int magicNumber = (int)Math.floor(Math.random()*(Arrangeable.size()-1+1));
        for(int i = 0; i < magicNumber; ++i){
            Arrangeable.add(Arrangeable.poll());
        }
        return Arrangeable.peek();
    }
    private Word chooseCommonWord() {
        commonWordOnly();
        return chooseWord();
    }

    public void record(LetterBoard letters, Word lastInput2, boolean[] correctPos) {
        notInWord = removeStar(letters.getNotInWord());
        inWord = removeStar(letters.getInWord());
        lastInput = lastInput2;
        partialWordKnown = rightAssign(correctPos);
        letterMap = new analysisBoard(lastInput2, correctPos, letters, prevAnalysisBoard);
        prevAnalysisBoard = letterMap;
    }

    private char[] rightAssign(boolean[] correctPos) {
        char[] wordShape = new char[wordLength];
        for(int i = 0; i < wordLength; ++i){
            wordShape[i] = '*';
            if(correctPos[i]){
                wordShape[i] = lastInput.getChars()[i];
            }
        }
        return wordShape;
    }

    public void clearMem() {
        lastInput = null;
        notInWord = null;
        inWord = null;
        partialWordKnown = null;
        smallDict = newDataBase();
        prevAnalysisBoard = letterMap;
        letterMap = null;
    }

    public char[] removeStar(char[] before){
        if(before == null){
            return null;
        }
        int trueSize = 0;
        for(int i = 0; i < before.length; ++i){
            if(before[i]!='*'){
                trueSize++;
            }
        }
        if(trueSize == 0){
            return null;
        }
        char[] result = new char[trueSize];
        int InsertInto = 0;
        for(int i = 0; i < before.length; ++i){
            if(before[i] != '*'){
                result[InsertInto] = before[i];
                ++InsertInto;
            }

        }
        return result;
    }
    
    public char[] fromString(String using){
        Word in = new Word(using);
        return in.getContains();
    }

    public boolean doesWordHaveAllLegalLetters(Word smallDict2){
        for(int i = 0; i < smallDict2.getLength(); ++i){
            if(notInWord != null){
                for(int j = 0; j < notInWord.length; ++j){
                    if(smallDict2.getChars()[i]==notInWord[j]){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean doesWordHaveNeededLetters(Word smallDict2){
        if(inWord != null){
            int LettersFound = 0;
            for(int i = 0; i < inWord.length; ++i){
                if(inWord[i] != '*'){
                    LettersFound++;
                }
            }
            if(LettersFound > 0){
                boolean[] letterCheck = new boolean[LettersFound];
                int checkIndex = 0;
                for(int i = 0; i < inWord.length; ++i){
                    if(inWord[i] != '*'){
                        letterCheck[checkIndex] = false;
                        for(int j = 0; j < smallDict2.getLength(); ++j){
                            if(smallDict2.getChars()[j] == inWord[i]){
                                letterCheck[checkIndex] = true;
                            }
                        }
                        checkIndex++;
                    }
                }
                for(int i = 0; i < letterCheck.length; ++i){
                    if(letterCheck[i] == false){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean doesWordHaveLetterInCorrectPlace(Word smallDict2){
        if(letterMap == null){
            return true;
        }
        char[] alphabet = letterMap.getAlphabet();
        int[][] superBoard = letterMap.getNegLetterBoard();
        for(int i = 0; i < smallDict2.getLength(); ++i){
            for(int j = 0; j < alphabet.length; ++j){
                switch(superBoard[i][j]){
                    case 1:
                    if(smallDict2.getChars()[i] != alphabet[j]){
                        return false;
                    }
                    break;
                    case 2:
                    if(smallDict2.getChars()[i] == alphabet[j]){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void intelligentUpdateList(){
        Queue <Word> smallDictCopy = new LinkedList<Word>();
        smallDictCopy.addAll(smallDict);
        int timesToX = smallDict.size();
        for(int i = 0; i < timesToX; ++i){
            smallDict.poll();
            Word examine = smallDictCopy.poll();
            if(doesWordHaveLetterInCorrectPlace(examine)&&doesWordHaveNeededLetters(examine)){
                smallDictCopy.add(examine);
            }
        }
        smallDict.addAll(smallDictCopy);

    }
    public void deepClear(){
        clearMem();
        prevAnalysisBoard = null;
    }
    public Queue<Word> getSmallDict() {
        return smallDict;
    }
    public Word priorityGuess(){
        intelligentUpdateList();
        commonUpdate();
        return smallDict.peek();
    }
    public void commonUpdate() {
        Queue <Word> smallDictCopy = new LinkedList<Word>();
        smallDictCopy.addAll(smallDict);
        int timesToX = smallDict.size();
      for(int i = 0; i < timesToX; ++i){
          smallDict.poll();
          for(int j = 0; j < iKnowTheseWords.getCommon().size(); ++j){
              if(smallDictCopy.peek().equals(iKnowTheseWords.takeOutFromCommon())){
                  Word changing = smallDictCopy.poll();
                  changing.addScore();
                  smallDictCopy.add(changing);
              } else{
                  smallDictCopy.add(smallDictCopy.poll());
              }
          }
      }
      smallDict.addAll(smallDictCopy);
    }
    
    public Word facetbourdkingslymph(){
        if(wordLength != 5){
            return priorityGuess();
        }
        ++turnNum;
        switch(turnNum){
            case 1:
            return new Word("facet");
            case 2:
            return new Word("bourd");
            case 3:
            return new Word("kings");
            case 4:
            return new Word("lymph");
            default:
            return priorityGuess();
        }
    }
}