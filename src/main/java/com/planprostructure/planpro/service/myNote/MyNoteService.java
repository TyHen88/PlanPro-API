package com.planprostructure.planpro.service.myNote;

import com.planprostructure.planpro.payload.myNotes.MyNoteRequest;

public interface MyNoteService {
    void createMyNote(MyNoteRequest myNoteRequest) throws Throwable;

    void updateMyNote(Long noteId, MyNoteRequest myNoteRequest) throws Throwable;

    void deleteMyNote(Long id) throws Throwable;

    Object getMyNoteById(Long id) throws Throwable;

    Object getAllMyNotes() throws Throwable;

}
