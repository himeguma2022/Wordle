public class GuessAndResult {
    private Word Correct;
    private Word Attempt;
    private boolean sameLength;
    private char[] CharShared;
    private char[] CharNotPresent;
    private boolean[] CorrectPos;
    private char[] CorrectCharsArray;
    private boolean Solved;
    

    public static void main(String[] args) {
        test("train", "brain");
        test("white", "night");
        test("fight", "black");
        test("caitlyn", "seraphine");
        test("unit","board");
        test("query","white");
        test("drink","white");
        test("banana","bland");
        test("flower","flower");
        test("tea","ate");
        test("note","tone");
        test("stone", "tones");
        test("mass","bass");
        test("green","teeny");
        
    }

    private static void test(String string, String string2) {
        GuessAndResult attempt = new GuessAndResult(new Word(string), new Word(string2));
        System.out.println("Correct word: "+attempt.Correct.getContent());
        System.out.println("Word Guessed: "+attempt.Attempt.getContent());
        System.out.println("Both words are the same length? -> "+attempt.sameLength);
        attempt.CharInfo();
        System.out.print("This attempt is a solved attempt -> "+attempt.Solved+"\n\n\n");
    }

    public GuessAndResult(Word lookingFor, Word Guessed){
        Correct = lookingFor;
        Attempt = Guessed;
        sameLength = (lookingFor.getChars().length == Guessed.getChars().length);
        CharShared = CheckChars(lookingFor, Guessed);
        CharNotPresent = CheckNoChars(lookingFor, Guessed);
        CorrectPos = CheckWords(lookingFor,Guessed);
        CorrectCharsArray = CorrectCharArray();
        Solved = CheckData();
    }

   private boolean CheckData() {
        if(CorrectPos == null){
            return false;
        }
        for(int i = 0; i < CorrectPos.length; ++i){
            if(CorrectPos[i] == false){
                return false;
            }
        }
        return true;
    }

 private boolean[] CheckWords(Word lookingFor, Word guessed) {
        if(!sameLength){
            return null;
        }
        else{
            boolean[] correctData = new boolean[lookingFor.getChars().length];
            for(int i = 0; i < correctData.length; ++i){
                if(lookingFor.getChars()[i] == guessed.getChars()[i]){
                    correctData[i] = true;
                } else{
                    correctData[i] = false;
                }
            }
            return correctData;
        }
    }

    private char[] CheckNoChars(Word lookingFor, Word guessed){
        int trueSize = 0;
        if(guessed.getContains().equals(CharShared)){
            return null;
        }
        trueSize = guessed.getContains().length - CharShared.length;
        char[] result = new char[trueSize];
        boolean found = false;
        int InsertInto = 0;
        for(int i = 0; i < guessed.getContains().length; ++i){
            for(int j = 0; j < CharShared.length; ++j){
                if(CharShared[j] == guessed.getContains()[i]){
                    found = true;
                } 
            }
            if(!found){
                result[InsertInto] = guessed.getContains()[i];
                ++InsertInto;
            }
            found = false;
        }
        return result;
    }
    public char[] CorrectCharArray(){
        int trueSize = 0;
        if(CorrectPos != null){
            for(int i = 0; i < CorrectPos.length; ++i){
                if(CorrectPos[i]){
                    ++trueSize;
                }
            }
        }
        if(trueSize == 0){
            return null;
        }
        char[] result = new char[trueSize];
        int InsertInto = 0;
        for(int i = 0; i < CorrectPos.length; ++i){
            if(CorrectPos[i]){
                result[InsertInto] = Correct.getChars()[i];
                ++InsertInto;
            }
        }
        return result;
    }

    private char[] CheckChars(Word lookingFor, Word guessed) {
        boolean found = false;
        int insertInto = 0;
        char[] temp = new char[lookingFor.getContains().length];
        for(int i = 0; i < lookingFor.getContains().length; ++i){
            for(int j = 0; j < guessed.getContains().length; ++j){
                if(!found && lookingFor.getContains()[i] == guessed.getContains()[j]){
                    found = true;
                    temp[insertInto] = lookingFor.getContains()[i];
                    ++insertInto; 
                }
            }
            found = false;
        }
        for(int i = 0; i < temp.length; ++i){
            if(i >= insertInto){
                temp[i] = '0';
            }
        }
        int trueSize = 0;
        for(int i = 0; i < temp.length; ++i){
            if(temp[i] != '0'){
                trueSize++;
            }
        }
        char[] result = new char[trueSize]; 
        for(int i = 0; i < result.length; ++i){
            result[i] = temp[i];
        }
        return result;
    }

    public void CharInfo(){
        System.out.println("Characters present in correct and guessed words:");
        for(int i = 0; i < CharShared.length; ++i){
            System.out.print("["+CharShared[i]+"]\t");
        }
        System.out.print("\n");
        System.out.println("Guess certainly doesn't contain: ");
        for(int i = 0; i < CharNotPresent.length; ++i){
            System.out.print("["+CharNotPresent[i]+"]\t");
        }
        System.out.print("\n");
    }
    public boolean[] getCorrectPos(){
        return CorrectPos;
    }

    public boolean getSolved() {
        return Solved;
    }

    public char[] getCorrectCharsArray(){
        return CorrectCharsArray;
    }

    public char[] getCharShared(){
        return CharShared;
    }

    public char[] getCharNotPresent(){
        return CharNotPresent;
    }
}