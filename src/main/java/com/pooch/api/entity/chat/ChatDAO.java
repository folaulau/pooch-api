package com.pooch.api.entity.chat;

import java.util.Optional;

public interface ChatDAO {

  Chat save(Chat chat);

  Optional<Chat> findByUuid(String uuid);

  Optional<Chat> findByGroomerIdAndParentId(long groomerId, long parentId);
}
