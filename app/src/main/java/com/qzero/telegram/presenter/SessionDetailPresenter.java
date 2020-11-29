package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.SessionDetailContract;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.dao.gen.ChatMemberDao;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.dao.gen.UserInfoDao;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.notice.bean.NoticeDataType;
import com.qzero.telegram.utils.LocalStorageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class SessionDetailPresenter extends BasePresenter<SessionDetailContract.View> implements SessionDetailContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private SessionModule sessionModule;
    private BroadcastModule broadcastModule;

    private ChatSession chatSession;

    private ChatSessionDao sessionDao;
    private ChatMemberDao memberDao;

    private UserInfoDao userInfoDao;

    @Override
    public void attachView(@NonNull SessionDetailContract.View mView) {
        super.attachView(mView);

        sessionModule=new SessionModuleImpl(mView.getContext());
        broadcastModule=new BroadcastModuleImpl(mView.getContext());

        sessionDao= SessionManager.getInstance(mView.getContext()).getSession().getChatSessionDao();
        userInfoDao= SessionManager.getInstance(mView.getContext()).getSession().getUserInfoDao();
        memberDao=SessionManager.getInstance(mView.getContext()).getSession().getChatMemberDao();
    }

    @Override
    public void initView(String sessionId) {
        chatSession=sessionDao.load(sessionId);

        if(chatSession.getDeleted()){
            getView().showDeletedMode();
            getView().loadSessionInfo(chatSession);
            return;
        }

        String currentUserName= LocalStorageUtils.getLocalTokenUserName(getView().getContext());
        ChatMember member=memberDao.queryBuilder().where(ChatMemberDao.Properties.UserName.eq(currentUserName),
                ChatMemberDao.Properties.SessionId.eq(sessionId)).uniqueOrThrow();

        switch (member.getLevel()){
            case ChatMember.LEVEL_NORMAL:
                getView().showNormalUserMode();
                break;
            case ChatMember.LEVEL_OPERATOR:
                getView().showOperatorMode();
                break;
            case ChatMember.LEVEL_OWNER:
                getView().showOwnerMode();
                break;
            default:
                getView().showNormalUserMode();
                break;
        }

        getView().loadSessionInfo(chatSession);
    }

    @Override
    public void addMember(String memberUserName) {
        getView().showProgress();
        ChatMember member=new ChatMember(chatSession.getSessionId(),memberUserName,ChatMember.LEVEL_NORMAL);
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

    @Override
    public void deleteMember(String memberUserName) {
        getView().showProgress();
        sessionModule.removeChatMember(chatSession.getSessionId(),memberUserName)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error(String.format("Failed to remove chat member %s in session with id %s", memberUserName,chatSession.getSessionId()));
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("移除失败");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("移除成功");
                        }
                    }
                });

    }

    @Override
    public void updateMember(ChatMember chatMember) {
        getView().showProgress();
        sessionModule.updateChatMemberLevel(chatMember)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error(String.format("Failed to update chat member %s in session with id %s", chatMember+"",chatSession.getSessionId()));
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("提交失败");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("提交成功");
                        }
                    }
                });
    }

    @Override
    public String[] getFriendNames() {
        List<UserInfo> userInfoList=userInfoDao.loadAll();
        if(userInfoList==null)
            return new String[0];

        String myName=LocalStorageUtils.getLocalTokenUserName(getView().getContext());
        List<String> nameList=new ArrayList<>();
        for(UserInfo userInfo:userInfoList){
            if(userInfo.getUserName().equals(myName))
                continue;

            nameList.add(userInfo.getUserName());
        }

        return nameList.toArray(new String[]{});
    }

    @Override
    public void quitSession() {
        getView().showProgress();
        sessionModule.quitSession(chatSession.getSessionId())
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to quit session with id "+chatSession.getSessionId());
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("退出失败");
                        }
                    }

                    @Override
                    public void onComplete() {
                        log.debug("Quited session with id "+chatSession.getSessionId());
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("退出成功");
                            getView().exit();
                        }
                    }
                });
    }

    @Override
    public void deleteSessionRemotely() {
        getView().showProgress();
        sessionModule.deleteSession(chatSession.getSessionId())
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to delete session with id "+chatSession.getSessionId());
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("删除失败");
                        }
                    }

                    @Override
                    public void onComplete() {
                        log.debug("Deleted session with id "+chatSession.getSessionId());
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("删除成功");
                            getView().exit();
                        }
                    }
                });
    }

    @Override
    public void deleteSessionLocally() {
        sessionModule.deleteSessionPhysically(chatSession.getSessionId());
        getView().exit();
    }

    @Override
    public void updateSessionName(String newSessionName) {
        getView().showProgress();
        sessionModule.updateSessionParameter(chatSession.getSessionId(), ChatSessionParameter.NAME_SESSION_NAME,newSessionName)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to update session "+chatSession);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("提交失败");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("提交成功");
                            getView().exit();
                        }
                    }
                });
    }


    @Override
    public void registerListener() {
        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_SESSION, (dataId, actionType) -> {
            if(!isViewAttached())
                return;

            if(actionType== BroadcastModule.ActionType.ACTION_TYPE_DELETE){
                chatSession.setDeleted(true);
                getView().showDeletedMode();
                getView().loadSessionInfo(chatSession);
            }else{
                initView(chatSession.getSessionId());
            }
        });
    }

    @Override
    public void unregisterListener() {
        broadcastModule.unregisterAllReceivers();
    }
}
