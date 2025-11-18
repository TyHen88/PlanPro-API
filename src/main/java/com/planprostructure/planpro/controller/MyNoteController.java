package com.planprostructure.planpro.controller;

import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.payload.myNotes.MyNoteRequest;
import com.planprostructure.planpro.service.myNote.MyNoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wb/v1/my-notes")
@RequiredArgsConstructor
@Tag(name = "My Notes", description = "My Notes API")
public class MyNoteController extends ProPlanRestController {

    private final MyNoteService myNoteService;

    @GetMapping
    public Object getAllMyNotes() throws Throwable {
        return ok(myNoteService.getAllMyNotes());
    }

    @GetMapping("/{noteId}")
    public Object getMyNoteById(@PathVariable  Long noteId) throws Throwable {
        return ok(myNoteService.getMyNoteById(noteId));
    }

    @PostMapping
    public Object createMyNote(@RequestBody MyNoteRequest myNoteRequest) throws Throwable {
        myNoteService.createMyNote(myNoteRequest);
        return ok();
    }

    @PutMapping("/{noteId}")
    public Object updateMyNote(@PathVariable Long noteId, @RequestBody MyNoteRequest myNoteRequest) throws Throwable {
        myNoteService.updateMyNote(noteId, myNoteRequest);
        return ok();
    }

    @PutMapping("/{noteId}/delete")
    public Object deleteMyNote(@PathVariable Long noteId) throws Throwable {
        myNoteService.deleteMyNote(noteId);
        return ok();
    }

}
