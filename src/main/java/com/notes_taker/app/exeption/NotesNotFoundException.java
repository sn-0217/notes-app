package com.notes_taker.app.exeption;

public class NotesNotFoundException extends RuntimeException {
    public NotesNotFoundException(String msg){
        super(msg);
    }
}
