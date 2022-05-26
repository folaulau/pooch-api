package com.pooch.api.entity.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.pooch.api.dto.ChatCreateDTO;
import com.pooch.api.dto.GroomerChatMessageCreateDTO;
import com.pooch.api.dto.ParentChatMessageCreateDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatValidatorServiceImp implements ChatValidatorService {


  @Autowired
  private ParentDAO parentDAO;

  @Autowired
  private GroomerDAO groomerDAO;

  @Autowired
  private ChatDAO chatDAO;

  @Override
  public Pair<Parent, Groomer> validateCreate(ChatCreateDTO chatCreateDTO) {

    String groomerUuid = chatCreateDTO.getGroomerUuid();

    if (groomerUuid == null || groomerUuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty, uuid=" + groomerUuid);
    }

    Groomer groomer = groomerDAO.getByUuid(groomerUuid).orElseThrow(
        () -> new ApiException("Groomer not found", "groomer not found for uuid=" + groomerUuid));



    String parentUuid = chatCreateDTO.getParentUuid();


    if (parentUuid == null || parentUuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty, uuid=" + parentUuid);
    }

    Parent parent = parentDAO.getByUuid(parentUuid).orElseThrow(
        () -> new ApiException("Parent not found", "parent not found for uuid=" + parentUuid));


    return Pair.of(parent, groomer);
  }

  @Override
  public Chat validateCreateParentMessage(ParentChatMessageCreateDTO parentChatMessageDTO) {

    String uuid = parentChatMessageDTO.getUuid();

    if (uuid == null || uuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty, uuid=" + uuid);
    }

    Chat chat = chatDAO.findByUuid(uuid)
        .orElseThrow(() -> new ApiException("Chat not found", "chat not found for uuid=" + uuid));

    return chat;
  }

  @Override
  public Chat validateCreateGroomerMessage(GroomerChatMessageCreateDTO groomerChatMessageDTO) {

    String uuid = groomerChatMessageDTO.getUuid();

    if (uuid == null || uuid.trim().isEmpty()) {
      throw new ApiException(ApiError.DEFAULT_MSG, "uuid is empty, uuid=" + uuid);
    }

    Chat chat = chatDAO.findByUuid(uuid)
        .orElseThrow(() -> new ApiException("Chat not found", "chat not found for uuid=" + uuid));

    return chat;
  }

}
