public class LetterBoard {
    private char[] haventGuessed;
    private char[] knowPosition;
    private char[] inWord;
    private char[] notInWord;
    

    public static void main(String[] args) {
        LetterBoard demo = new LetterBoard();
        demo.displayBoard();
        char[] weKnowPos = {'t', 'a'};
        char[] weInWord = {'e', 'r','m'};
        char[] defNotInWord = {'g', 'k'};
        demo.Update(weKnowPos, weInWord, defNotInWord);
        demo.displayBoard();
    }
    public LetterBoard(){
        haventGuessed = new char[26];
        for(int i = 0; i < haventGuessed.length; ++i){
            haventGuessed[i] = (char)(i+97);
        }
        knowPosition = allBlank();
        inWord = allBlank();
        notInWord = allBlank();
    }
        
    public char[] allBlank(){
        char[] out =  new char[26];
        for(int i = 0; i < out.length; ++i){
            out[i] = '*';
        }
        return out;
    }
    public void Update(char[] correctPos, char[] inGuess, char[] notInGuess){
        for(int i = 0; i < haventGuessed.length; ++i){
            if(correctPos != null){
                for(int j = 0; j < correctPos.length; ++j){
                    if(haventGuessed[i] == correctPos[j]||inWord[i] == correctPos[j]){
                        knowPosition[i] = correctPos[j];
                        inWord[i] = correctPos[j];
                        haventGuessed[i] = '*';
                        }
                    }
                }
            if(inGuess != null){
                for(int j = 0; j < inGuess.length; ++j){
                    if(haventGuessed[i] == inGuess[j]){
                        inWord[i] = inGuess[j];
                        haventGuessed[i] = '*';
                    }
                }
            }
            if(notInGuess != null){
                for(int j = 0; j < notInGuess.length; ++j){
                    if(haventGuessed[i] == notInGuess[j]){
                        notInWord[i] = notInGuess[j];
                        haventGuessed[i] = '*';
                    }
                }
            }
        }
    }
    public void displayBoard(){
        displayArray(haventGuessed);
        displayArray(knowPosition);
        displayArray(inWord);
        displayArray(notInWord);
        
        System.out.print("\n\n\n");
    }
    private void displayArray(char[] displaying) {
        for(int i = 0; i < displaying.length; ++i){
            System.out.print("["+displaying[i]+"]");
        }
        System.out.print("\n");
    }

    public char[] getHaventGuessed(){
        return haventGuessed;
    }

    public char[] getKnowPosition(){
        return knowPosition;
    }

    public char[] getInWord(){
        return inWord;
    }

    public char[] getNotInWord(){
        return notInWord;
    }
}
