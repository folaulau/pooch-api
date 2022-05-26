package com.pooch.api.entity.chat;

import static org.springframework.http.HttpStatus.OK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pooch.api.dto.AuthenticationResponseDTO;
import com.pooch.api.dto.AuthenticatorDTO;
import com.pooch.api.dto.ChatCreateDTO;
import com.pooch.api.dto.ChatDTO;
import com.pooch.api.dto.GroomerChatMessageCreateDTO;
import com.pooch.api.dto.ParentChatMessageCreateDTO;
import com.pooch.api.entity.groomer.GroomerRestController;
import com.pooch.api.utils.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Chats", description = "Chat Operations")
@Slf4j
@RestController
@RequestMapping("/chats")
public class ChatRestController {

  @Autowired
  private ChatService chatService;

  @Operation(summary = "Create Chat", description = "create chat")
  @PostMapping(value = "/start")
  public ResponseEntity<ChatDTO> createChat(
      @RequestHeader(name = "token", required = true) String token,
      @RequestBody ChatCreateDTO chatCreateDTO) {
    log.info("createChat, chatCreateDTO={}", ObjectUtils.toJson(chatCreateDTO));

    ChatDTO chatDTO = chatService.create(chatCreateDTO);

    return new ResponseEntity<>(chatDTO, OK);
  }

  @Operation(summary = "Create Parent Text Message", description = "create parent text message and send to groomer")
  @PostMapping(value = "/parent/text")
  public ResponseEntity<ChatMessage> createParentMessage(
      @RequestHeader(name = "token", required = true) String token,
      @RequestBody ParentChatMessageCreateDTO parentChatMessageDTO) {
    log.info("createParentMessage, chatCreateDTO={}", ObjectUtils.toJson(parentChatMessageDTO));

    ChatMessage msg = chatService.createParentTextMessage(parentChatMessageDTO);


    return new ResponseEntity<>(msg, OK);
  }

  @Operation(summary = "Create Groomer Text Message", description = "create groomer text message and send to parent")
  @PostMapping(value = "/groomer/text")
  public ResponseEntity<ChatMessage> createGroomerMessage(
      @RequestHeader(name = "token", required = true) String token,
      @RequestBody GroomerChatMessageCreateDTO groomerChatMessageDTO) {
    log.info("createGroomerMessage, groomerChatMessageDTO={}",
        ObjectUtils.toJson(groomerChatMessageDTO));

    ChatMessage msg = chatService.createGroomerTextMessage(groomerChatMessageDTO);

    return new ResponseEntity<>(msg, OK);
  }
}
