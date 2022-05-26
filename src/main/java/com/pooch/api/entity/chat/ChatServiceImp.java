package com.pooch.api.entity.chat;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.elasticsearch.core.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.google.cloud.firestore.Firestore;
import com.pooch.api.dto.ChatCreateDTO;
import com.pooch.api.dto.ChatDTO;
import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.GroomerChatMessageCreateDTO;
import com.pooch.api.dto.ParentChatMessageCreateDTO;
import com.pooch.api.entity.UserType;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatServiceImp implements ChatService {

  @Autowired
  private ChatValidatorService chatValidatorService;

  @Autowired
  private Firestore firestore;


  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Value("${spring.profiles.active}")
  private String env;


  @Autowired
  private ChatDAO chatDAO;

  @Override
  public ChatDTO create(ChatCreateDTO chatCreateDTO) {
    // TODO Auto-generated method stub

    Pair<Parent, Groomer> pair = chatValidatorService.validateCreate(chatCreateDTO);

    Parent parent = pair.getFirst();
    Groomer groomer = pair.getSecond();

    Optional<Chat> optChat = chatDAO.findByGroomerIdAndParentId(groomer.getId(), parent.getId());

    Chat chat = null;
    if (optChat.isPresent()) {
      chat = optChat.get();
    } else {
      chat = new Chat();
      chat.setGroomer(groomer);
      chat.setParent(parent);
      chat.setUuid("groomer-" + groomer.getId() + "-parent-" + parent.getId() + "-" + env + "-"
          + UUID.randomUUID().toString());

      chat = chatDAO.save(chat);
    }

    String uuid = chat.getUuid();

    try {
      String id = firestore
          .collection("chat").document(uuid).collection("messages").add(Map.of("message",
              "convo starter", "groomerViewed", true, "parentViewed", true, "deleted", true))
          .get().get().get().getId();

      System.out.println("id: " + id);

      ChatDTO chatDTO = entityDTOMapper.mapChatToChatDTO(chat);

      return chatDTO;


    } catch (InterruptedException | ExecutionException e) {
      throw new ApiException(ApiError.DEFAULT_MSG, "firebase error=" + e.getMessage());
    }
  }

  @Override
  public ChatMessage createParentTextMessage(ParentChatMessageCreateDTO parentChatMessageDTO) {
    Chat chat = chatValidatorService.validateCreateParentMessage(parentChatMessageDTO);

    String uuid = chat.getUuid();


    ChatMessage message = ChatMessage.builder().delete(false).type(ChatMessageType.TEXT.name())
        .senderUserType(UserType.Parent.name()).receiverUserType(UserType.Groomer.name())
        .parentViewedAt(LocalDateTime.now().toString()).createdAt(LocalDateTime.now().toString())
        .updateedAt(LocalDateTime.now().toString()).message(parentChatMessageDTO.getMessage())
        .build();

    try {
      String id = firestore.collection("chat").document(uuid).collection("messages").add(message)
          .get().get().get().getId();

      System.out.println("id: " + id);

      message.setUuid(id);

    } catch (InterruptedException | ExecutionException e) {
      throw new ApiException(ApiError.DEFAULT_MSG, "firebase error=" + e.getMessage());
    }
    return message;

  }

  @Override
  public ChatMessage createGroomerTextMessage(GroomerChatMessageCreateDTO groomerChatMessageDTO) {
    Chat chat = chatValidatorService.validateCreateGroomerMessage(groomerChatMessageDTO);

    String uuid = chat.getUuid();


    ChatMessage message = ChatMessage.builder().delete(false).type(ChatMessageType.TEXT.name())
        .senderUserType(UserType.Groomer.name()).receiverUserType(UserType.Parent.name())
        .groomerViewedAt(LocalDateTime.now().toString()).createdAt(LocalDateTime.now().toString())
        .updateedAt(LocalDateTime.now().toString()).message(groomerChatMessageDTO.getMessage())
        .build();

    try {
      String id = firestore.collection("chat").document(uuid).collection("messages").add(message)
          .get().get().get().getId();

      System.out.println("id: " + id);

      message.setUuid(id);

    } catch (InterruptedException | ExecutionException e) {
      throw new ApiException(ApiError.DEFAULT_MSG, "firebase error=" + e.getMessage());
    }
    return message;
  }

}
