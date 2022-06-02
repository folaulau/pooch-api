package com.pooch.api.entity.note;

import java.util.List;
import java.util.Set;

public interface NoteDAO {


  Note save(Note note);

  List<Note> save(List<Note> notes);
}
