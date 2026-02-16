package com.notes_taker.app.exeption;

public class NotesAlreadyArchivedException extends RuntimeException{
    public NotesAlreadyArchivedException(String msg){
        super(msg);
    }
}
