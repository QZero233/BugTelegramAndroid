package com.qzero.telegram.dao.impl;

import android.content.Context;

import com.qzero.telegram.dao.MessageContentManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.utils.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageContentManagerImpl implements MessageContentManager {
    private Context context;

    public MessageContentManagerImpl(Context context) {
        this.context = context;
        new File(context.getFilesDir(),"message_content/").mkdirs();
    }

    private File getFile(String messageId){
        return new File(context.getFilesDir(),"message_content/"+messageId+".content");
    }

    @Override
    public void saveMessageContent(ChatMessage chatMessage) throws IOException {
        FileOutputStream outputStream=new FileOutputStream(getFile(chatMessage.getMessageId()));
        outputStream.write(chatMessage.getContent());
        outputStream.close();
    }

    @Override
    public void deleteMessageContent(String messageId) throws IOException {
        File file=getFile(messageId);
        if(file.exists())
            file.delete();
    }

    @Override
    public byte[] getMessageContent(String messageId) throws IOException {
        File file=getFile(messageId);
        if(!file.exists())
            return null;

        byte[] content=StreamUtils.readFile(file);
        return content;
    }
}
