package com.pooch.api.entity.chat;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ChatDAOImp implements ChatDAO {

  @Autowired
  private ChatRepository chatRepository;

  @Override
  public Chat save(Chat chat) {
    return chatRepository.save(chat);
  }

  @Override
  public Optional<Chat> findByUuid(String uuid) {
    // TODO Auto-generated method stub
    return chatRepository.findByUuid(uuid);
  }

  @Override
  public Optional<Chat> findByGroomerIdAndParentId(long groomerId, long parentId) {
    // TODO Auto-generated method stub
    return chatRepository.findByGroomerIdAndParentId(groomerId, parentId);
  }
}
