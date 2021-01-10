package com.qzero.telegram.presenter.session;

import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.utils.AESUtils;
import com.qzero.telegram.utils.SHA256Utils;

public class SecretSessionChatPresenter extends BaseChatPresenter {

    private String clearKey;

    public void setClearKey(String key){
        clearKey=key;
        if(!SHA256Utils.getHexEncodedSHA256(clearKey).toLowerCase().equals(session.getSessionParameter(ChatSessionParameter.NAME_SESSION_SECRET_KEY))){
            getView().showToast("密码错误");
            getView().exit();
            return;
        }

        loadMessageList();
    }

    @Override
    public void loadMessageList() {
        if(clearKey==null)
            return;

        messageList=messageModule.getAllMessagesBySessionIdLocally(session.getSessionId());
        if(messageList!=null && !messageList.isEmpty()){
            for(ChatMessage message:messageList){
                if(message.getMessageType()!=null && message.getMessageType().equals(ChatMessage.TYPE_SYSTEM_NOTICE))
                    continue;//It's local message, ignore it
                byte[] content=message.getContent();
                content= AESUtils.aesDecrypt(content,clearKey);
                message.setContent(content);
            }
        }

        getView().showMessageList(messageList);
    }

    @Override
    public void sendMessage(byte[] content,String messageType) {
        content=AESUtils.aesEncrypt(content,clearKey);
        super.sendMessage(content,messageType);
    }
}
