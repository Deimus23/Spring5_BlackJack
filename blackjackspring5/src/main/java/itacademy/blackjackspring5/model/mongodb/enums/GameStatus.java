package itacademy.blackjackspring5.model.mongodb.enums;

public enum GameStatus {
    IN_PROGRES("in progres"),
    FINISHED("finished");
    private final String status;

    GameStatus(String status){
        this.status= status;
    }
    public String getStatus(){
        return status;
    }

}
