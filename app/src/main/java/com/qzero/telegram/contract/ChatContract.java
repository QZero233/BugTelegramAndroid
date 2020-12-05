package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

import java.util.List;

public class ChatContract {

    public interface Presenter extends IBasePresenter<View>{
        void initSessionInfo(String sessionId);

        void loadMessageList();

        void sendMessage(byte[] content);

        void updateMessageStatus(String messageId,String newStatus);
        void deleteMessage(String messageId,boolean isPhysical);

        void registerMessageBroadcastListener();
        void unregisterMessageBroadcastListener();
    }

    public interface View extends IBaseView{
        void showMessageList(List<ChatMessage> messageList);
        void clearMessageInput();

        void showDeletedMode();
        void loadSessionInfo(ChatSession session);
    }

}
