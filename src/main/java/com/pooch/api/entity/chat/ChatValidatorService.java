package com.pooch.api.entity.chat;

import org.springframework.data.util.Pair;
import com.pooch.api.dto.ChatCreateDTO;
import com.pooch.api.dto.GroomerChatMessageCreateDTO;
import com.pooch.api.dto.ParentChatMessageCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;

public interface ChatValidatorService {

  Pair<Parent, Groomer> validateCreate(ChatCreateDTO chatCreateDTO);

  Chat validateCreateParentMessage(ParentChatMessageCreateDTO parentChatMessageDTO);

  Chat validateCreateGroomerMessage(GroomerChatMessageCreateDTO groomerChatMessageDTO);
}
