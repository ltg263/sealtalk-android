package cn.rongcloud.im.task;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.rongcloud.im.common.ThreadManager;
import cn.rongcloud.im.contact.PhoneContactManager;
import cn.rongcloud.im.db.DBManager;
import cn.rongcloud.im.db.dao.FriendDao;
import cn.rongcloud.im.db.dao.GroupMemberDao;
import cn.rongcloud.im.db.dao.UserDao;
import cn.rongcloud.im.db.model.FriendDescription;
import cn.rongcloud.im.db.model.FriendInfo;
import cn.rongcloud.im.db.model.FriendShipInfo;
import cn.rongcloud.im.db.model.FriendStatus;
import cn.rongcloud.im.db.model.PhoneContactInfoEntity;
import cn.rongcloud.im.db.model.UserInfo;
import cn.rongcloud.im.file.FileManager;
import cn.rongcloud.im.im.IMManager;
import cn.rongcloud.im.model.AddFriendResult;
import cn.rongcloud.im.model.GetContactInfoResult;
import cn.rongcloud.im.model.PhoneContactInfo;
import cn.rongcloud.im.model.Resource;
import cn.rongcloud.im.model.Result;
import cn.rongcloud.im.model.SearchFriendInfo;
import cn.rongcloud.im.model.SimplePhoneContactInfo;
import cn.rongcloud.im.model.Status;
import cn.rongcloud.im.net.HttpClientManager;
import cn.rongcloud.im.net.RetrofitUtil;
import cn.rongcloud.im.net.service.FriendService;
import cn.rongcloud.im.utils.CharacterParser;
import cn.rongcloud.im.utils.NetworkBoundResource;
import cn.rongcloud.im.utils.NetworkOnlyResource;
import cn.rongcloud.im.utils.RongGenerate;
import cn.rongcloud.im.utils.SearchUtils;
import cn.rongcloud.im.utils.log.SLog;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imlib.model.Conversation;
import okhttp3.RequestBody;

public class FriendTask {
    private static final String TAG = "FriendTask";
    private Context context;
    private FriendService friendService;
    private DBManager dbManager;
    private FileManager fileManager;

    public FriendTask(Context context) {
        this.context = context.getApplicationContext();
        friendService = HttpClientManager.getInstance(this.context).getClient().createService(FriendService.class);
        dbManager = DBManager.getInstance(this.context);
        fileManager = new FileManager(context);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public LiveData<Resource<List<FriendShipInfo>>> getAllFriends() {
        SLog.i(TAG, "getAllFriends()");

        return new NetworkBoundResource<List<FriendShipInfo>, Result<List<FriendShipInfo>>>() {
            @Override
            protected void saveCallResult(@NonNull Result<List<FriendShipInfo>> item) {
                List<FriendShipInfo> list = item.getResult();
                SLog.i(TAG, "saveCallResult() list.size() :" + list.size());
                UserInfo userInfo = null;
                FriendInfo friendInfo = null;
                List<UserInfo> userInfoList = new ArrayList<>();
                List<FriendInfo> friendInfoList = new ArrayList<>();
                for (FriendShipInfo friendShipInfo : list) {
                    userInfo = new UserInfo();
                    friendInfo = new FriendInfo();
                    userInfo.setId(friendShipInfo.getId());
                    userInfo.setName(friendShipInfo.getNickname());

                    String portraitUri = friendShipInfo.getPortraitUri();
                    // ????????????????????????????????????
                    if (TextUtils.isEmpty(portraitUri)) {
                        portraitUri = RongGenerate.generateDefaultAvatar(context, friendShipInfo.getId(), friendShipInfo.getNickname());
                    }
                    userInfo.setPortraitUri(portraitUri);
                    userInfo.setAlias(friendShipInfo.getDisplayName());
                    userInfo.setFriendStatus(friendShipInfo.getFriendshipStatus());
                    userInfo.setPhoneNumber(friendShipInfo.getPhone());
                    userInfo.setRegion(friendShipInfo.getRegion());
                    userInfo.setAliasSpelling(SearchUtils.fullSearchableString(friendShipInfo.getDisplayName()));
                    userInfo.setAliasSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getDisplayName()));

                    userInfo.setNameSpelling(SearchUtils.fullSearchableString(friendShipInfo.getNickname()));
                    userInfo.setNameSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getNickname()));

                    if (!TextUtils.isEmpty(friendShipInfo.getDisplayName())) {
                        userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getDisplayName()));
                    } else {
                        userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getNickname()));
                    }

                    friendInfo.setId(friendShipInfo.getId());
                    friendInfo.setMessage(friendShipInfo.getMessage());
                    friendInfo.setUpdatedAt(friendShipInfo.getModifyTime1());
                    userInfoList.add(userInfo);
                    friendInfoList.add(friendInfo);

                    // ?????? IMKit ????????????
                    IMManager.getInstance().updateUserInfoCache(userInfo.getId(), userInfo.getName(), Uri.parse(userInfo.getPortraitUri()), userInfo.getAlias());
                }

                UserDao userDao = dbManager.getUserDao();
                if (userDao != null) {
                    userDao.insertUserList(userInfoList);
                }

                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    friendDao.insertFriendShipList(friendInfoList);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<FriendShipInfo>> loadFromDb() {
                SLog.i(TAG, "getAllFriends() loadFromDb()");
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    return friendDao.getAllFriendListDB();
                } else {
                    return new MutableLiveData<>(null);
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<List<FriendShipInfo>>> createCall() {
                SLog.i(TAG, "getAllFriends() createCall()");
                return friendService.getAllFriendList();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<FriendShipInfo> data) {
                return true;
            }
        }.asLiveData();
    }

    /**
     * ??????????????????
     *
     * @param userId
     * @return
     */
    public LiveData<Resource<FriendShipInfo>> getFriendInfo(String userId) {
        return new NetworkBoundResource<FriendShipInfo, Result<FriendShipInfo>>() {
            @Override
            protected void saveCallResult(@NonNull Result<FriendShipInfo> item) {
                UserDao userDao = dbManager.getUserDao();
                FriendDao friendDao = dbManager.getFriendDao();
                if (userDao == null || friendDao == null) return;

                FriendShipInfo friendShipInfo = item.getResult();
                if (friendShipInfo == null) return;

                UserInfo userInfo = new UserInfo();
                FriendInfo friendInfo = new FriendInfo();
                userInfo.setId(friendShipInfo.getId());
                userInfo.setName(friendShipInfo.getNickname());
                String portraitUri = friendShipInfo.getPortraitUri();
                // ????????????????????????????????????
                if (TextUtils.isEmpty(portraitUri)) {
                    portraitUri = RongGenerate.generateDefaultAvatar(context, friendShipInfo.getId(), friendShipInfo.getNickname());
                }
                userInfo.setPortraitUri(portraitUri);
                userInfo.setAlias(friendShipInfo.getDisplayName());
                userInfo.setFriendStatus(FriendStatus.IS_FRIEND.getStatusCode());
                userInfo.setPhoneNumber(friendShipInfo.getPhone());
                userInfo.setRegion(friendShipInfo.getRegion());
                userInfo.setAliasSpelling(SearchUtils.fullSearchableString(friendShipInfo.getDisplayName()));
                userInfo.setAliasSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getDisplayName()));
                userInfo.setNameSpelling(SearchUtils.fullSearchableString(friendShipInfo.getNickname()));
                userInfo.setNameSpellingInitial(SearchUtils.initialSearchableString(friendShipInfo.getNickname()));
                if (!TextUtils.isEmpty(friendShipInfo.getDisplayName())) {
                    userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getDisplayName()));
                } else {
                    userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(friendShipInfo.getNickname()));
                }

                friendInfo.setId(friendShipInfo.getId());
                friendInfo.setMessage(friendShipInfo.getMessage());
                friendInfo.setUpdatedAt(friendShipInfo.getModifyTime1() == null ? friendShipInfo.getModifyTime1() : friendShipInfo.getModifyTime1());

                userDao.insertUser(userInfo);
                friendDao.insertFriendShip(friendInfo);

                // ?????? IMKit ????????????
                IMManager.getInstance().updateUserInfoCache(userInfo.getId(), userInfo.getName(), Uri.parse(userInfo.getPortraitUri()), userInfo.getAlias());
            }

            @NonNull
            @Override
            protected LiveData<FriendShipInfo> loadFromDb() {
                FriendDao friendDao = dbManager.getFriendDao();
                LiveData<FriendShipInfo> friendInfo;
                if (friendDao == null) {
                    friendInfo = new MutableLiveData<>(null);
                } else {
                    friendInfo = friendDao.getFriendInfo(userId);
                }
                return friendInfo;
            }

            @NonNull
            @Override
            protected LiveData<Result<FriendShipInfo>> createCall() {
                return friendService.getFriendInfo(userId);
            }
        }.asLiveData();
    }

    public FriendShipInfo getFriendShipInfoFromDBSync(String userId) {
        if (dbManager.getFriendDao() != null) {
            return dbManager.getFriendDao().getFriendInfoSync(userId);
        }
        return null;
    }

    public List<FriendShipInfo> getFriendShipInfoListFromDBSync(String[] userIds) {
        return dbManager.getFriendDao().getFriendInfoListSync(userIds);
    }

    public LiveData<List<FriendShipInfo>> getFriendShipInfoListFromDB(String[] userIds) {
        return dbManager.getFriendDao().getFriendInfoList(userIds);
    }

    /**
     * ??????????????????
     *
     * @param friendId
     * @return
     */
    public LiveData<Resource<Boolean>> agree(String friendId) {
        return new NetworkOnlyResource<Boolean, Result<Boolean>>() {

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("friendId", friendId);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return friendService.agreeFriend(body);
            }
        }.asLiveData();
    }

    /**
     * ??????????????????
     *
     * @param friendId
     * @return
     */
    public LiveData<Resource<Void>> ingore(String friendId) {
        return new NetworkOnlyResource<Void, Result<Void>>() {

            @NonNull
            @Override
            protected LiveData<Result<Void>> createCall() {
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("friendId", friendId);
                RequestBody body = RetrofitUtil.createJsonRequest(paramsMap);
                return friendService.ingoreFriend(body);
            }
        }.asLiveData();
    }

    public LiveData<List<FriendShipInfo>> searchFriendsFromDB(String match) {
        if (dbManager == null || dbManager.getFriendDao() == null) {
            return new MediatorLiveData<>();
        }
        return dbManager.getFriendDao().searchFriendShip(match);
    }

    /**
     * ?????????????????????
     *
     * @param friendId
     * @param alias
     * @return
     */
    public LiveData<Resource<Boolean>> setFriendAliasName(String friendId, String alias) {
        return new NetworkOnlyResource<Boolean, Result>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                UserDao userDao = dbManager.getUserDao();
                if (userDao != null) {
                    String aliasSpelling = CharacterParser.getInstance().getSpelling(alias);
                    userDao.updateAlias(friendId, alias, aliasSpelling);

                    UserInfo userInfo = userDao.getUserByIdSync(friendId);
                    // ?????? IMKit ????????????
                    String name = userInfo.getAlias();
                    if (TextUtils.isEmpty(name)) {
                        name = userInfo.getName();
                    }
                    IMManager.getInstance().updateUserInfoCache(userInfo.getId(), userInfo.getName(), Uri.parse(userInfo.getPortraitUri()), userInfo.getAlias());
                    // ??????????????????????????????????????????????????? ?????????????????????????????????????????????
                    // ?????????????????????????????????????????? ??????????????????
                    GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
                    List<String> groupIds = groupMemberDao.getGroupIdListByUserId(friendId);
                    if (groupIds != null && groupIds.size() > 0) {
                        for (String groupId : groupIds) {
                            //???????????????????????????????????????????????????
                            if (TextUtils.isEmpty(groupMemberDao.getGroupMemberInfoDes(groupId, friendId).getGroupNickname())) {
                                IMManager.getInstance().updateGroupMemberInfoCache(groupId, friendId, name);
                            }
                        }
                    }
                }
            }

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("friendId", friendId);
                bodyMap.put("displayName", alias);
                return friendService.setFriendAlias(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }


    /**
     * ??????????????????
     *
     * @param friendId
     * @param inviteMsg
     * @return
     */
    public LiveData<Resource<AddFriendResult>> inviteFriend(String friendId, String inviteMsg) {
        return new NetworkOnlyResource<AddFriendResult, Result<AddFriendResult>>() {
            @NonNull
            @Override
            protected LiveData<Result<AddFriendResult>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("friendId", friendId);
                bodyMap.put("message", inviteMsg);
                return friendService.inviteFriend(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    /**
     * ????????????
     *
     * @param friendId
     * @return
     */
    public LiveData<Resource<Boolean>> deleteFriend(String friendId) {
        return new NetworkOnlyResource<Boolean, Result>() {
            @Override
            protected void saveCallResult(@NonNull Boolean item) {
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    friendDao.deleteFriend(friendId);
                    friendDao.removeFromBlackList(friendId);
                }
                UserDao userDao = dbManager.getUserDao();
                if (userDao != null) {
                    userDao.updateFriendStatus(friendId, FriendStatus.DELETE_FRIEND.getStatusCode());
                }
                IMManager.getInstance().clearConversationAndMessage(friendId, Conversation.ConversationType.PRIVATE);
            }

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("friendId", friendId);
                return friendService.deleteFriend(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    /**
     * ??????????????????
     *
     * @param friendIdList
     * @return
     */
    public LiveData<Resource<Object>> deleteFriends(List<String> friendIdList) {
        return new NetworkOnlyResource<Object, Result>() {
            @Override
            protected void saveCallResult(@NonNull Object item) {
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    friendDao.deleteFriends(friendIdList);
                    friendDao.removeFromBlackList(friendIdList);
                }
                UserDao userDao = dbManager.getUserDao();
                if (userDao != null) {
                    userDao.updateFriendsStatus(friendIdList, FriendStatus.DELETE_FRIEND.getStatusCode());
                }
                for (String friendId : friendIdList) {
                    IMManager.getInstance().clearConversationAndMessage(friendId, Conversation.ConversationType.PRIVATE);
                }
            }

            @NonNull
            @Override
            protected LiveData<Result> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("friendIds", friendIdList);
                return friendService.deleteMultiFriend(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    public LiveData<List<FriendShipInfo>> getAllFriendsExcludeGroup(String excludeGroupId) {
        return dbManager.getFriendDao().getAllFriendsExcludeGroup(excludeGroupId);
    }

    public LiveData<List<FriendShipInfo>> searchFriendsExcludeGroup(String excludeGroupId, String matchSearch) {
        return dbManager.getFriendDao().searchFriendsExcludeGroup(excludeGroupId, matchSearch);
    }

    public LiveData<List<FriendShipInfo>> getAllFriendsIncludeGroup(String includeGroupId) {
        return dbManager.getFriendDao().getFriendsIncludeGroup(includeGroupId);
    }

    public LiveData<List<FriendShipInfo>> searchFriendsIncludeGroup(String includeGroupId, String matchSearch) {
        return dbManager.getFriendDao().searchFriendsIncludeGroup(includeGroupId, matchSearch);
    }

    public LiveData<Resource<SearchFriendInfo>> searchFriendFromServer(String stAccount, String region, String phone) {
        return new NetworkOnlyResource<SearchFriendInfo, Result<SearchFriendInfo>>() {

            @NonNull
            @Override
            protected LiveData<Result<SearchFriendInfo>> createCall() {
                HashMap<String, String> queryMap = new HashMap<>();
                if (!TextUtils.isEmpty(stAccount)) {
                    queryMap.put("account", stAccount);
                } else {
                    queryMap.put("region", region);
                    queryMap.put("phone", phone);
                }
                return friendService.searchFriend(queryMap);
            }
        }.asLiveData();
    }

    public LiveData<FriendShipInfo> getFriendShipInfoFromDB(String userId) {
        return dbManager.getFriendDao().getFriendInfo(userId);
    }

    /**
     * ????????????????????????????????????????????????????????????????????? SealTalk ?????????
     *
     * @return
     */
    public LiveData<Resource<List<PhoneContactInfo>>> getPhoneContactInfo() {
        MediatorLiveData<Resource<List<PhoneContactInfo>>> result = new MediatorLiveData<>();
        MutableLiveData<List<String>> phoneNumList = new MutableLiveData<>();
        ThreadManager.getInstance().runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                phoneNumList.postValue(PhoneContactManager.getInstance().getAllContactPhoneNumber());
            }
        });

        result.addSource(phoneNumList, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> phoneNumberList) {
                result.removeSource(phoneNumList);
                LiveData<Resource<List<PhoneContactInfo>>> phoneContactInfo = getPhoneContactInfo(phoneNumberList);
                result.addSource(phoneContactInfo, new Observer<Resource<List<PhoneContactInfo>>>() {
                    @Override
                    public void onChanged(Resource<List<PhoneContactInfo>> listResource) {
                        result.setValue(listResource);
                    }
                });
            }
        });

        return result;
    }

    /**
     * ????????????????????????????????????????????????????????????????????? SealTalk ?????????
     *
     * @param phoneNumberList
     * @return
     */
    public LiveData<Resource<List<PhoneContactInfo>>> getPhoneContactInfo(List<String> phoneNumberList) {
        return new NetworkBoundResource<List<PhoneContactInfo>, Result<List<GetContactInfoResult>>>() {
            @Override
            protected void saveCallResult(@NonNull Result<List<GetContactInfoResult>> item) {
                List<GetContactInfoResult> contactInfoList = item.getResult();
                if (contactInfoList == null || contactInfoList.size() == 0) return;

                UserDao userDao = dbManager.getUserDao();
                FriendDao friendDao = dbManager.getFriendDao();
                if (userDao == null || friendDao == null) return;

                List<PhoneContactInfoEntity> phoneContactInfoEntityList = new ArrayList<>();

                // ???????????????????????????????????????
                List<SimplePhoneContactInfo> allContactInfo = PhoneContactManager.getInstance().getAllContactInfo();
                HashMap<String, String> phoneNameMap = new HashMap();
                for (SimplePhoneContactInfo simplePhoneContactInfo : allContactInfo) {
                    phoneNameMap.put(simplePhoneContactInfo.getPhone(), simplePhoneContactInfo.getName());
                }

                for (GetContactInfoResult contactInfo : contactInfoList) {
                    int registered = contactInfo.getRegistered();
                    if (registered == 0) {
                        // ????????????????????????
                        continue;
                    }

                    String id = contactInfo.getId();
                    UserInfo userInfo = userDao.getUserByIdSync(id);
                    if (userInfo == null) {
                        userInfo = new UserInfo();
                        userInfo.setId(contactInfo.getId());
                    }
                    userInfo.setStAccount(contactInfo.getStAccount());
                    userInfo.setName(contactInfo.getNickname());
                    String portraitUri = contactInfo.getPortraitUri();
                    // ????????????????????????????????????
                    if (TextUtils.isEmpty(portraitUri)) {
                        portraitUri = RongGenerate.generateDefaultAvatar(context, contactInfo.getId(), contactInfo.getNickname());
                    }
                    userInfo.setPortraitUri(portraitUri);
                    userInfo.setPhoneNumber(contactInfo.getPhone());
                    userInfo.setNameSpelling(SearchUtils.fullSearchableString(contactInfo.getNickname()));
                    userInfo.setNameSpellingInitial(SearchUtils.initialSearchableString(contactInfo.getNickname()));
                    if (TextUtils.isEmpty(userInfo.getAlias())) {
                        userInfo.setOrderSpelling(CharacterParser.getInstance().getSpelling(contactInfo.getNickname()));
                    }
                    userInfo.setStAccount(contactInfo.getStAccount());

                    userDao.insertUser(userInfo);
                    // ?????? IMKit ????????????
                    IMManager.getInstance().updateUserInfoCache(userInfo.getId(), userInfo.getName(), Uri.parse(userInfo.getPortraitUri()), userInfo.getAlias());

                    // ?????????????????????
                    PhoneContactInfoEntity phoneContactInfoEntity = new PhoneContactInfoEntity();
                    phoneContactInfoEntity.setPhoneNumber(contactInfo.getPhone());
                    phoneContactInfoEntity.setContactName(phoneNameMap.get(contactInfo.getPhone()));
                    phoneContactInfoEntity.setUserId(contactInfo.getId());
                    phoneContactInfoEntity.setRelationship(contactInfo.getRelationship());
                    phoneContactInfoEntityList.add(phoneContactInfoEntity);
                }

                friendDao.insertPhoneContactInfo(phoneContactInfoEntityList);
            }

            @NonNull
            @Override
            protected LiveData<List<PhoneContactInfo>> loadFromDb() {
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    return friendDao.getPhoneContactInfo();
                } else {
                    return new MutableLiveData<>(null);
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<List<GetContactInfoResult>>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("contactList", phoneNumberList);
                return friendService.getContactsInfo(RetrofitUtil.createJsonRequest(bodyMap));
            }
        }.asLiveData();
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????? SealTalk ???????????????
     *
     * @param keyword
     * @return
     */
    public LiveData<List<PhoneContactInfo>> searchPhoneContactInfo(String keyword) {
        LiveData<List<PhoneContactInfo>> searchPhoneContactInfo;
        FriendDao friendDao = dbManager.getFriendDao();
        if (friendDao != null) {
            searchPhoneContactInfo = friendDao.searchPhoneContactInfo(keyword);
        } else {
            searchPhoneContactInfo = new MutableLiveData<>(null);
        }

        return searchPhoneContactInfo;
    }

    /**
     * ????????????????????????
     *
     * @param friendId
     * @return
     */
    public LiveData<Resource<FriendDescription>> getFriendDescription(String friendId) {
        return new NetworkBoundResource<FriendDescription, Result<FriendDescription>>() {
            @Override
            protected void saveCallResult(@NonNull Result<FriendDescription> item) {
                FriendDescription friendDescription = item.getResult();
                if (friendDescription == null) return;
                friendDescription.setId(friendId);
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    friendDao.insertFriendDescription(friendDescription);
                }
                if (!TextUtils.isEmpty(friendDescription.getDisplayName())) {
                    updateAlias(friendId, friendDescription.getDisplayName());
                }
            }

            @NonNull
            @Override
            protected LiveData<FriendDescription> loadFromDb() {
                FriendDao friendDao = dbManager.getFriendDao();
                if (friendDao != null) {
                    return friendDao.getFriendDescription(friendId);
                } else {
                    return new MutableLiveData<>(null);
                }
            }

            @NonNull
            @Override
            protected LiveData<Result<FriendDescription>> createCall() {
                return friendService.getFriendDescription(friendId);
            }
        }.asLiveData();
    }

    /**
     * ????????????????????????
     *
     * @param friendId
     * @param displayName
     * @param region
     * @param phone
     * @param description
     * @param imageUri
     * @return
     */
    public LiveData<Resource<Void>> setFriendDescription(String friendId, String displayName, String region
            , String phone, String description, String imageUri) {
        if (!TextUtils.isEmpty(imageUri) && !(imageUri.toLowerCase().startsWith("http://")
                || imageUri.toLowerCase().startsWith("https://"))) {
            String uriStr = imageUri;
//            if (!uriStr.toLowerCase().startsWith("file://")){
//                uriStr = "file://"+uriStr;
//            }
            return setDesAndUploadImage(friendId, displayName, region
                    , phone, description, Uri.parse(uriStr));
        }
        return new NetworkOnlyResource<Void, Result<Boolean>>() {

            @NonNull
            @Override
            protected LiveData<Result<Boolean>> createCall() {
                HashMap<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("friendId", friendId);
                if (displayName != null) {
                    bodyMap.put("displayName", displayName);
                }
                if (region != null) {
                    bodyMap.put("region", region);
                }
                if (phone != null) {
                    bodyMap.put("phone", phone);
                }
                if (description != null) {
                    bodyMap.put("description", description);
                }
                if (imageUri != null) {
                    bodyMap.put("imageUri", imageUri);
                }
                return friendService.setFriendDescription(RetrofitUtil.createJsonRequest(bodyMap));
            }

            @Override
            protected void saveCallResult(@NonNull Void item) {
                super.saveCallResult(item);
                FriendDescription friendDescription = new FriendDescription();
                friendDescription.setId(friendId);
                if (displayName != null) {
                    friendDescription.setDisplayName(displayName);
                    //?????????????????? ??????????????????
                    updateAlias(friendId, displayName);
                }
                if (region != null) {
                    friendDescription.setRegion(region);
                }
                if (phone != null) {
                    friendDescription.setPhone(phone);
                }
                if (description != null) {
                    friendDescription.setDescription(description);
                }
                if (imageUri != null) {
                    friendDescription.setImageUri(imageUri);
                }
                FriendDao friendDao = dbManager.getFriendDao();
                friendDao.insertFriendDescription(friendDescription);
            }
        }.asLiveData();
    }

    /**
     * ?????? ?????????
     *
     * @param friendId
     * @param displayName
     */
    private void updateAlias(String friendId, String displayName) {
        UserDao userDao = dbManager.getUserDao();
        if (userDao != null) {
            String aliasSpelling = CharacterParser.getInstance().getSpelling(displayName);
            userDao.updateAlias(friendId, displayName, aliasSpelling);

            UserInfo userInfo = userDao.getUserByIdSync(friendId);
            String name = userInfo.getAlias();
            if (TextUtils.isEmpty(name)) {
                name = userInfo.getName();
            }
            // ?????? IMKit ????????????
            IMManager.getInstance().updateUserInfoCache(userInfo.getId(), name, Uri.parse(userInfo.getPortraitUri()), userInfo.getAlias());
            // ??????????????????????????????????????????????????? ?????????????????????????????????????????????
            // ?????????????????????????????????????????? ??????????????????
            GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
            List<String> groupIds = groupMemberDao.getGroupIdListByUserId(friendId);
            if (groupIds != null && groupIds.size() > 0) {
                for (String groupId : groupIds) {
                    //???????????????????????????????????????????????????
                    if (TextUtils.isEmpty(groupMemberDao.getGroupMemberInfoDes(groupId, friendId).getGroupNickname())) {
                        IMManager.getInstance().updateGroupMemberInfoCache(groupId, friendId, name);
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param friendId
     * @param displayName
     * @param region
     * @param phone
     * @param description
     * @param imageUri
     * @return
     */
    public LiveData<Resource<Void>> setDesAndUploadImage(String friendId, String displayName, String region
            , String phone, String description, Uri imageUri) {
        MediatorLiveData<Resource<Void>> result = new MediatorLiveData<>();
        // ?????????????????????
        Log.e("setDesAndUploadImage", imageUri.toString());
        LiveData<Resource<String>> uploadResource = fileManager.uploadCompressImage(imageUri);
        result.addSource(uploadResource, resource -> {
            if (resource.status != Status.LOADING) {
                result.removeSource(uploadResource);
            }

            if (resource.status == Status.ERROR) {
                result.setValue(Resource.error(resource.code, null));
                return;
            }

            if (resource.status == Status.SUCCESS) {
                String uploadUrl = resource.data;

                // ??????????????????????????????????????????
                LiveData<Resource<Void>> setFriendDescription = setFriendDescription(friendId, displayName
                        , region, phone, description, uploadUrl);
                result.addSource(setFriendDescription, portraitResultResource -> {
                    if (portraitResultResource.status != Status.LOADING) {
                        result.removeSource(setFriendDescription);
                    }

                    if (portraitResultResource.status == Status.ERROR) {
                        result.setValue(Resource.error(portraitResultResource.code, null));
                        return;
                    }

                    if (portraitResultResource.status == Status.SUCCESS) {
                        result.setValue(Resource.success(null));
                    }
                });
            }
        });

        return result;
    }

}
