package com.pooch.api.entity.chat;

import com.pooch.api.dto.ChatCreateDTO;
import com.pooch.api.dto.ChatDTO;
import com.pooch.api.dto.GroomerChatMessageCreateDTO;
import com.pooch.api.dto.ParentChatMessageCreateDTO;

public interface ChatService {

  ChatDTO create(ChatCreateDTO chatCreateDTO);

  ChatMessage createParentTextMessage(ParentChatMessageCreateDTO parentChatMessageDTO);

  ChatMessage createGroomerTextMessage(GroomerChatMessageCreateDTO groomerChatMessageDTO);

}
