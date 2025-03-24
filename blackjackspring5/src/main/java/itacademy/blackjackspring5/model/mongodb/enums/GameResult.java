package itacademy.blackjackspring5.model.mongodb.enums;

public enum GameResult{
    WIN("win"),
    LOSE("lose"),
    DRAW("draw");
    private final String result;

    GameResult(String result){
        this.result= result;
    }
    public String getResult(){
        return result;
    }
}

