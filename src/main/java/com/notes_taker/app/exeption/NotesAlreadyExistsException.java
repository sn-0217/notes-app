package com.notes_taker.app.exeption;

public class NotesAlreadyExistsException extends RuntimeException{
    public NotesAlreadyExistsException(String msg){
        super(msg);
    }
}
