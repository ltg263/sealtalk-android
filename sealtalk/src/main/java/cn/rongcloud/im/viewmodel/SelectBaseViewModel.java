package cn.rongcloud.im.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.rongcloud.im.R;
import cn.rongcloud.im.db.model.FriendShipInfo;
import cn.rongcloud.im.db.model.FriendStatus;
import cn.rongcloud.im.model.GroupMember;
import cn.rongcloud.im.model.Resource;
import cn.rongcloud.im.model.Status;
import cn.rongcloud.im.task.FriendTask;
import cn.rongcloud.im.task.GroupTask;
import cn.rongcloud.im.ui.adapter.models.CharacterTitleInfo;
import cn.rongcloud.im.ui.adapter.models.CheckType;
import cn.rongcloud.im.ui.adapter.models.CheckableContactModel;
import cn.rongcloud.im.ui.adapter.models.ContactModel;
import cn.rongcloud.im.utils.CharacterParser;
import cn.rongcloud.im.utils.SingleSourceMapLiveData;
import cn.rongcloud.im.utils.log.SLog;

public class SelectBaseViewModel extends AndroidViewModel {

    private static final String TAG = "SelectBaseViewModel";
    private FriendTask friendTask;
    private GroupTask groupTask;
    protected SingleSourceMapLiveData<Resource<List<FriendShipInfo>>, List<ContactModel>> friendsLiveData;
    protected SingleSourceMapLiveData<List<FriendShipInfo>, List<ContactModel>> groupFriendsLiveData;
    protected SingleSourceMapLiveData<List<FriendShipInfo>, List<ContactModel>> excludeGroupLiveData;
    protected SingleSourceMapLiveData<List<GroupMember>, List<ContactModel>> allGroupMemberLiveData;
    private MutableLiveData<Integer> selectedCount = new MutableLiveData<>();
    private MutableLiveData<List<ContactModel>> currentLiveData;
    private MutableLiveData<CheckableContactModel> checkedChangeData = new MutableLiveData<>();
    private ArrayList<String> uncheckableContactIdList;
    private ArrayList<String> excludeContactIdList;
    private ArrayList<String> checkedContactIdList;
    private ArrayList<String> checkedGroupList;

    public SelectBaseViewModel(@NonNull Application application) {
        super(application);
        friendTask = new FriendTask(application);
        groupTask = new GroupTask(application);
        friendsLiveData = new SingleSourceMapLiveData<>(new Function<Resource<List<FriendShipInfo>>, List<ContactModel>>() {
            @Override
            public List<ContactModel> apply(Resource<List<FriendShipInfo>> input) {
                return convert(input.data);
            }
        });

        groupFriendsLiveData = new SingleSourceMapLiveData<>(new Function<List<FriendShipInfo>, List<ContactModel>>() {
            @Override
            public List<ContactModel> apply(List<FriendShipInfo> input) {
                return convert(input);
            }
        });


        excludeGroupLiveData = new SingleSourceMapLiveData<>(new Function<List<FriendShipInfo>, List<ContactModel>>() {
            @Override
            public List<ContactModel> apply(List<FriendShipInfo> input) {
                return convert(input);
            }
        });

        allGroupMemberLiveData = new SingleSourceMapLiveData<>(new Function<List<GroupMember>, List<ContactModel>>() {
            @Override
            public List<ContactModel> apply(List<GroupMember> input) {
                return convertGroupMember(input);
            }
        });
    }

    public void loadFriendShip(ArrayList<String> uncheckableIdList, ArrayList<String> checkedUsers, ArrayList<String> checkedGroups) {
        uncheckableContactIdList = uncheckableIdList;
        checkedContactIdList = checkedUsers;
        checkedGroupList = checkedGroups;
        if (checkedContactIdList != null) {
            selectedCount.setValue(checkedContactIdList.size());
        } else {
            selectedCount.setValue(0);
        }
        friendsLiveData.setSource(friendTask.getAllFriends());
        currentLiveData = friendsLiveData;
    }

    public void loadFriendShip() {
        friendsLiveData.setSource(friendTask.getAllFriends());
        currentLiveData = friendsLiveData;
    }

    /**
     * ???????????????
     *
     * @param keyword
     */
    public void searchFriend(String keyword) {
        LiveData<List<FriendShipInfo>> searchDbLiveData = friendTask.searchFriendsFromDB(keyword);
        // ???????????????????????????
        LiveData<Resource<List<FriendShipInfo>>> resourceLiveData = Transformations.switchMap(searchDbLiveData,
                new Function<List<FriendShipInfo>, LiveData<Resource<List<FriendShipInfo>>>>() {
                    @Override
                    public LiveData<Resource<List<FriendShipInfo>>> apply(List<FriendShipInfo> input) {
                        return new MutableLiveData<>(Resource.success(input));
                    }
                });
        friendsLiveData.setSource(resourceLiveData);
    }


    /**
     * ?????????????????????????????????
     *
     * @param groupId
     */
    public void loadFriendShipExclude(String groupId, ArrayList<String> uncheckableIdList) {
        SLog.i(TAG, "loadFriendShipExclude groupId:" + groupId);
        uncheckableContactIdList = uncheckableIdList;
        if (checkedContactIdList != null) {
            selectedCount.setValue(checkedContactIdList.size());
        } else {
            selectedCount.setValue(0);
        }
        excludeGroupLiveData.setSource(friendTask.getAllFriendsExcludeGroup(groupId));
        currentLiveData = excludeGroupLiveData;
    }

    /**
     * ??????????????????
     *
     * @param groupId
     */
    public void loadFriendShipExclude(String groupId) {
        SLog.i(TAG, "loadFriendShipInclude groupId:" + groupId);
        groupFriendsLiveData.setSource(friendTask.getAllFriendsExcludeGroup(groupId));
        currentLiveData = groupFriendsLiveData;
    }

    /**
     * ??????????????????
     *
     * @param groupId
     * @param keyword
     */
    public void searchFriendshipExclude(String groupId, String keyword) {
        groupFriendsLiveData.setSource(friendTask.searchFriendsExcludeGroup(groupId, keyword));
    }

    /**
     * ??????????????????
     *
     * @param groupId
     */
    public void loadGroupMemberExclude(String groupId, ArrayList<String> excludeList, ArrayList<String> includeList) {
        SLog.i(TAG, "loadGroupMemberExclude groupId:" + groupId);
        excludeContactIdList = excludeList;
        checkedContactIdList = includeList;
        if (checkedContactIdList != null) {
            selectedCount.setValue(checkedContactIdList.size());
        } else {
            selectedCount.setValue(0);
        }
        loadGroupMemberExclude(groupId);
    }

    /**
     * ??????????????????
     *
     * @param groupId
     */
    public void loadGroupMemberExclude(String groupId) {
        SLog.i(TAG, "loadGroupMemberExclude groupId:" + groupId);
        MediatorLiveData<List<GroupMember>> groupMemberListLiveData = new MediatorLiveData<>();
        groupMemberListLiveData.addSource(groupTask.getGroupMemberInfoList(groupId), new Observer<Resource<List<GroupMember>>>() {
            @Override
            public void onChanged(Resource<List<GroupMember>> resource) {
                if (resource.status != Status.LOADING) {
                    groupMemberListLiveData.setValue(resource.data);
                }
            }
        });
        allGroupMemberLiveData.setSource(groupMemberListLiveData);
        currentLiveData = allGroupMemberLiveData;
    }

    /**
     * ?????????????????????
     *
     * @param groupId
     * @param keyword
     */
    public void searchGroupMemberExclude(String groupId, String keyword) {
        allGroupMemberLiveData.setSource(groupTask.searchGroupMemberInDB(groupId, keyword));
    }


    public LiveData<List<ContactModel>> getFriendShipLiveData() {
        return friendsLiveData;
    }

    public int getIndex(String s) {
        if (currentLiveData.getValue() == null) return 0;
        for (int i = 0; i < currentLiveData.getValue().size(); i++) {
            Object o = currentLiveData.getValue().get(i).getBean();
            if (o instanceof CharacterTitleInfo) {
                CharacterTitleInfo characterParser = (CharacterTitleInfo) o;
                if (s.equals(characterParser.getCharacter())) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * @param input
     * @return
     */
    private List<ContactModel> convert(List<FriendShipInfo> input) {
        if (input == null) return null;
        SLog.i(TAG, "convert input.size()" + input.size());
        List<ContactModel> output = new ArrayList<>();
        ContactModel model = null;
        String temp = "";
        sortByFirstChar(input);
        for (FriendShipInfo friendShipInfo : input) {
            if (excludeContactIdList != null) {
                if (excludeContactIdList.contains(friendShipInfo.getId())) {
                    continue;
                }
            }
            // ???????????????????????????
            if (friendShipInfo.getFriendshipStatus() != FriendStatus.IS_FRIEND.getStatusCode()) {
                continue;
            }

            String firstChar = getFirstChar(friendShipInfo);
            if (TextUtils.isEmpty(firstChar)) {
                model = new ContactModel(new CharacterTitleInfo("#"), R.layout.contact_contact_title);
                temp = "#";
                output.add(model);
            } else if (!temp.equals(firstChar)) {
                model = new ContactModel(new CharacterTitleInfo(firstChar), R.layout.contact_contact_title);
                temp = firstChar;
                output.add(model);
            }
            CheckableContactModel<FriendShipInfo> checkableContactModel = new CheckableContactModel(friendShipInfo, R.layout.select_fragment_contact_item);
            if (uncheckableContactIdList != null && uncheckableContactIdList.contains(checkableContactModel.getBean().getId())) {
                checkableContactModel.setCheckType(CheckType.DISABLE);
            }
            SLog.i(TAG, "checkableContactModel.getBean().getId(): " + checkableContactModel.getBean().getId());
            if (checkedContactIdList != null && checkedContactIdList.contains(checkableContactModel.getBean().getId())) {
                checkableContactModel.setCheckType(CheckType.CHECKED);
            }
            output.add(checkableContactModel);
        }
        return output;
    }

    private List<ContactModel> convertGroupMember(List<GroupMember> input) {
        if (input == null) return null;
        SLog.i(TAG, "convert input.size()" + input.size());
        List<ContactModel> output = new ArrayList<>();
        ContactModel model = null;
        String temp = "";
        sortGroupMemberByFirstChar(input);
        for (GroupMember groupMember : input) {
            if (excludeContactIdList != null) {
                if (excludeContactIdList.contains(groupMember.getUserId())) {
                    continue;
                }
            }

            String firstChar = getFirstChar(groupMember);
            if (TextUtils.isEmpty(firstChar)) {
                model = new ContactModel<>(new CharacterTitleInfo("#"), R.layout.contact_contact_title);
                temp = "#";
                output.add(model);
            } else if (!temp.equals(firstChar)) {
                model = new ContactModel<>(new CharacterTitleInfo(firstChar), R.layout.contact_contact_title);
                temp = firstChar;
                output.add(model);
            }
            CheckableContactModel<GroupMember> checkableContactModel = new CheckableContactModel<>(groupMember, R.layout.select_fragment_contact_item);
            if (uncheckableContactIdList != null && uncheckableContactIdList.contains(checkableContactModel.getBean().getUserId())) {
                checkableContactModel.setCheckType(CheckType.DISABLE);
            }
            SLog.i(TAG, "checkableContactModel.getBean().getId(): " + checkableContactModel.getBean().getUserId());
            if (checkedContactIdList != null && checkedContactIdList.contains(checkableContactModel.getBean().getUserId())) {
                checkableContactModel.setCheckType(CheckType.CHECKED);
            }
            output.add(checkableContactModel);
        }
        return output;
    }

    /**
     * ??????????????????
     *
     * @param
     */
    public void onItemClicked(CheckableContactModel model) {
        //???????????????????????????
    }

    public ArrayList<String> getCheckedGroupList() {
        return checkedGroupList;
    }

    public ArrayList<String> getCheckedList() {
        ArrayList<String> strings = new ArrayList<>();
        List<ContactModel> contactModels = currentLiveData.getValue();

        if (contactModels == null) return strings;

        for (ContactModel model : contactModels) {
            if (model.getType() == R.layout.select_fragment_contact_item) {
                CheckableContactModel checkableContactModel = (CheckableContactModel) model;
                if (checkableContactModel.getCheckType() == CheckType.CHECKED) {
                    if (checkableContactModel.getBean() instanceof FriendShipInfo) {
                        FriendShipInfo info = (FriendShipInfo) checkableContactModel.getBean();
                        strings.add(info.getId());
                    } else if (checkableContactModel.getBean() instanceof GroupMember) {
                        GroupMember groupMember = (GroupMember) checkableContactModel.getBean();
                        strings.add(groupMember.getUserId());
                    }
                }
            }
        }
        return strings;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public ArrayList<String> getCheckedFriendIdList() {
        return checkedContactIdList;
    }

    public void cancelAllCheck() {
        List<ContactModel> ContactModels = currentLiveData.getValue();
        for (ContactModel model : ContactModels) {
            if (model.getType() == R.layout.select_fragment_contact_item) {
                CheckableContactModel<FriendShipInfo> checkableContactModel = (CheckableContactModel<FriendShipInfo>) model;
                checkableContactModel.setCheckType(CheckType.NONE);
            }
        }
        if (checkedContactIdList != null) {
            checkedContactIdList.clear();
        }
    }

    public SingleSourceMapLiveData<List<FriendShipInfo>, List<ContactModel>> getGroupFriendsLiveData() {
        return groupFriendsLiveData;
    }

    public SingleSourceMapLiveData<List<FriendShipInfo>, List<ContactModel>> getExcludeGroupLiveData() {
        return excludeGroupLiveData;
    }

    public LiveData<List<ContactModel>> getAllGroupMemberLiveData() {
        return allGroupMemberLiveData;
    }

    /**
     * ?????????????????????
     */
    public void addToCheckedList(CheckableContactModel contactModel) {
        if (checkedContactIdList == null) {
            checkedContactIdList = new ArrayList<>();
        }
        checkedChangeData.setValue(contactModel);
        Object bean = contactModel.getBean();
        if (bean instanceof FriendShipInfo) {
            FriendShipInfo friendShipInfo = (FriendShipInfo) bean;
            String id = friendShipInfo.getId();
            if (!checkedContactIdList.contains(id)) {
                checkedContactIdList.add(id);
                selectedCount.setValue(checkedContactIdList.size());
            }
        } else if (bean instanceof GroupMember) {
            GroupMember groupMember = (GroupMember) bean;
            String userId = groupMember.getUserId();
            if (!checkedContactIdList.contains(userId)) {
                checkedContactIdList.add(userId);
                selectedCount.setValue(checkedContactIdList.size());
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void removeFromCheckedList(CheckableContactModel contactModel) {
        if (checkedContactIdList == null) return;
        checkedChangeData.setValue(contactModel);
        Object bean = contactModel.getBean();
        if (bean instanceof FriendShipInfo) {
            FriendShipInfo friendShipInfo = (FriendShipInfo) bean;
            String id = friendShipInfo.getId();
            boolean removed = checkedContactIdList.remove(id);
            if (removed) {
                selectedCount.setValue(checkedContactIdList.size());
            }
        } else if (bean instanceof GroupMember) {
            GroupMember groupMember = (GroupMember) bean;
            String userId = groupMember.getUserId();
            boolean removed = checkedContactIdList.remove(userId);
            if (removed) {
                selectedCount.setValue(checkedContactIdList.size());
            }
        }
    }

    public LiveData<CheckableContactModel> getCheckedChangeData(){
        return checkedChangeData;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public LiveData<Integer> getSelectedCount() {
        return selectedCount;
    }

    /**
     * ?????????????????????
     *
     * @param models
     */
    private void sortByFirstChar(List<FriendShipInfo> models) {
        Collections.sort(models, new Comparator<FriendShipInfo>() {
            @Override
            public int compare(FriendShipInfo lhs, FriendShipInfo rhs) {
                if (TextUtils.isEmpty(getFirstChar(lhs))) {
                    return -1;
                }
                if (TextUtils.isEmpty(getFirstChar(rhs))) {
                    return 1;
                }
                return getFirstChar(lhs).compareTo(getFirstChar(rhs));
            }
        });
    }

    private void sortGroupMemberByFirstChar(List<GroupMember> models) {
        Collections.sort(models, new Comparator<GroupMember>() {
            @Override
            public int compare(GroupMember lhs, GroupMember rhs) {
                if (TextUtils.isEmpty(getFirstChar(lhs))) {
                    return -1;
                }
                if (TextUtils.isEmpty(getFirstChar(rhs))) {
                    return 1;
                }
                return getFirstChar(lhs).compareTo(getFirstChar(rhs));
            }
        });
    }

    // ???????????????
    private String getFirstChar(FriendShipInfo info) {
        String firstChar;
        String groupDisplayName = info.getAccountName();
        String displayName = info.getDisplayName();
        String nameFirstChar = info.getFirstCharacter();
        if (!TextUtils.isEmpty(groupDisplayName)) {
            firstChar = CharacterParser.getInstance().getSpelling(groupDisplayName).substring(0, 1).toUpperCase();
        } else if (!TextUtils.isEmpty(displayName)) {
            firstChar = CharacterParser.getInstance().getSpelling(displayName).substring(0, 1).toUpperCase();
        } else {
            firstChar = nameFirstChar;
        }
        if (TextUtils.isEmpty(firstChar)) {
            firstChar = "#";
        }
        return firstChar;
    }

    // ???????????????
    private String getFirstChar(GroupMember info) {
        String firstChar;
        String groupDisplayName = info.getGroupNickName();
        String displayName = info.getName();
        if (!TextUtils.isEmpty(groupDisplayName)) {
            firstChar = CharacterParser.getInstance().getSpelling(groupDisplayName).substring(0, 1).toUpperCase();
        } else {
            firstChar = CharacterParser.getInstance().getSpelling(displayName).substring(0, 1).toUpperCase();
        }
        if (TextUtils.isEmpty(firstChar)) {
            firstChar = "#";
        }
        return firstChar;
    }


}
