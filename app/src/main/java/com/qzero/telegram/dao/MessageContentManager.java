package com.qzero.telegram.dao;


import com.qzero.telegram.dao.entity.ChatMessage;

import java.io.IOException;

public interface MessageContentManager {

    void saveMessageContent(ChatMessage chatMessage) throws IOException;

    void deleteMessageContent(String messageId) throws IOException;

    byte[] getMessageContent(String messageId) throws IOException;

}
