package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.SessionDetailContract;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.utils.LocalStorageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class SessionDetailPresenter extends BasePresenter<SessionDetailContract.View> implements SessionDetailContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private SessionModule sessionModule;
    private ChatSession chatSession;

    private ChatSessionDao sessionDao;

    @Override
    public void attachView(@NonNull SessionDetailContract.View mView) {
        super.attachView(mView);
        sessionModule=new SessionModuleImpl(mView.getContext());
        sessionDao= SessionManager.getInstance(mView.getContext()).getSession().getChatSessionDao();
    }

    @Override
    public void initView(String sessionId) {
        chatSession=sessionDao.load(sessionId);

        //Set role
        String currentUserName= LocalStorageUtils.getLocalTokenUserName(getView().getContext());
        List<ChatMember> memberList=chatSession.getChatMembers();
        for(ChatMember member:memberList){
            if(member.getUserName().equals(currentUserName)){
                switch (member.getLevel()){
                    case SessionModule.LEVEL_NORMAL:
                        getView().showNormalUserMode();
                        break;
                    case SessionModule.LEVEL_OPERATOR:
                        getView().showOperatorMode();
                        break;
                    case SessionModule.LEVEL_OWNER:
                        getView().showOwnerMode();
                        break;
                    default:
                        getView().showNormalUserMode();
                        break;
                }
            }
        }

        getView().loadSessionInfo(chatSession);
    }

    @Override
    public void addMember(String memberUserName) {
        getView().showProgress();
        ChatMember member=new ChatMember(chatSession.getSessionId(),memberUserName,SessionModule.LEVEL_NORMAL);
        sessionModule.addChatMember(member)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {
                        if(isViewAttached()){
                            getView().showToast("添加成功");
                            chatSession.getChatMembers().add(member);
                            getView().loadSessionInfo(chatSession);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error(String.format("Failed to add member %s to session %s", memberUserName,chatSession.getSessionId()),e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("添加失败");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                        }
                    }
                });
    }
}
