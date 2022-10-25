public class analysisBoard {
    private int wordLength;
    private boolean[] lettersInCorrectPos;
    private Word input;
    private int[][] negLetterBoard;
    private LetterBoard letterClues;
    private char[] alphabet;

    public static void main(String[] args) {
        boolean[] correctPos = {false, false, true, false, false};
        LetterBoard letters = new LetterBoard();
        Word badWord = new Word("clunk");
        char[] InTheCorrectPos = toArray("u");
        char[] inGuess = toArray("cu");
        char[] notInGuess = toArray("raiselnk");
        letters.Update(InTheCorrectPos, inGuess, notInGuess);
        analysisBoard test = new analysisBoard(badWord, correctPos, letters,null);
        test.displayNegBoard();
    }
    private void displayNegBoard() {
        for(int i = -1; i < negLetterBoard.length; ++i){
            for(int j = 0; j < alphabet.length; ++j){
                if(i == -1){
                    System.out.print("["+alphabet[j]+"]");
                } else{
                    System.out.print("["+negLetterBoard[i][j]+"]");
                }
            }
            System.out.print("\n");
        }
        
    }
    public analysisBoard(Word record, boolean[] correctPos, LetterBoard letters, analysisBoard prevAnalysisBoard){
        input = record;
        lettersInCorrectPos = correctPos;
        wordLength = record.getLength();
        letterClues = letters;
        alphabet = listLetters();
        if(prevAnalysisBoard != null){
            negLetterBoard = prevAnalysisBoard.getNegLetterBoard();
        }
        if(negLetterBoard == null){
            negLetterBoard = new int[wordLength][26];
            for(int i = 0; i < negLetterBoard.length; ++i){
                for(int j = 0; j < negLetterBoard[i].length; ++j){
                    negLetterBoard[i][j]=0;
                }
            }
        }
        fillOut();
    }

    private void fillOut() {
        for(int i = 0; i < negLetterBoard.length; ++i){
            if(removeStar(letterClues.getNotInWord())!=null){
                for(int j = 0 ; j < removeStar(letterClues.getNotInWord()).length; ++j){
                    for(int k = 0; k < alphabet.length; ++k){
                        if(removeStar(letterClues.getNotInWord())[j] == alphabet[k]){
                            negLetterBoard[i][k] = 2;
                        }
                    }
                }
            }
            for(int j = 0; j < alphabet.length; ++j){
                if(input.getContent().charAt(i) == alphabet[j]){
                    negLetterBoard[i][j] = 2;
                }
            }
            
            if(lettersInCorrectPos[i]){
                for(int j = 0; j < alphabet.length; ++j){
                    if(alphabet[j]==input.getContent().charAt(i)){
                        negLetterBoard[i][j] = 1;
                    }
                    else{
                        negLetterBoard[i][j] = 2;
                    }
                }
            }
        }
    }

    private char[] listLetters() {
        char[] out = new char[26];
        for(int i = 0; i < out.length; ++i){
            out[i] = (char)(i+97);
        }
        return out;
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
    public static char[] toArray(String target){
        char[] out = new char[target.length()];
        for(int i = 0; i < out.length; ++i){
            out[i] = target.charAt(i);
        }
        return out;
    }
    public char[] getAlphabet(){
        return alphabet;
    }
    public int[][] getNegLetterBoard(){
        return negLetterBoard;
    }
    
}
