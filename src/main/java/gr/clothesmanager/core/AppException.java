package gr.clothesmanager.core;

import lombok.Getter;

@Getter
public class AppException extends Exception{
    private final String code;

    public AppException(String code, String message){
        super(message);
        this.code = code;
    }
}
