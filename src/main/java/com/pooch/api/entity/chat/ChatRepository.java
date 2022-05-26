package com.pooch.api.entity.chat;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

  Optional<Chat> findByUuid(String uuid);

  Optional<Chat> findByGroomerIdAndParentId(Long groomerId, Long parentId);
}
