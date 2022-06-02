package com.pooch.api.entity.note;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class NoteDAOImp implements NoteDAO {

  @Autowired
  private NoteRepository noteRepository;

  @Override
  public Note save(Note note) {
    // TODO Auto-generated method stub
    return noteRepository.saveAndFlush(note);
  }

  @Override
  public List<Note> save(List<Note> notes) {
    // TODO Auto-generated method stub
    return noteRepository.saveAllAndFlush(notes);
  }
}
