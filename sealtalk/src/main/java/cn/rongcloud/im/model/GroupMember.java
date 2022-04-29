package cn.rongcloud.im.model;

import android.util.Log;

import androidx.room.ColumnInfo;

public class GroupMember {
    //群内角色@(0:普通成员,1:创建者,2:管理员)
    public enum Role {
        GROUP_OWNER(1),
        MEMBER(0),
        MANAGEMENT(2);

        int value;
        Role(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Role getRole(int value) {
            if (value == 1){
                return GROUP_OWNER;
            }
            if(value == 2){
                return MANAGEMENT;
            }
            return MEMBER;
        }
    }

    @ColumnInfo(name = "group_id")
    private String groupId;
    @ColumnInfo(name = "user_id")
    private String userId;
    @ColumnInfo(name = "portrait_uri")
    private String portraitUri;
    @ColumnInfo(name = "nickname")
    private String groupNickName;
    @ColumnInfo(name = "nickname_spelling")
    private String groupNickNameSpelling;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "name_spelling")
    private String nameSpelling;
    @ColumnInfo(name = "role")
    private int role;
    @ColumnInfo(name = "join_time")
    private long joinTime;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getGroupNickName() {
        return groupNickName;
    }

    public void setGroupNickName(String groupNickName) {
        this.groupNickName = groupNickName;
    }

    public String getGroupNickNameSpelling() {
        return groupNickNameSpelling;
    }

    public void setGroupNickNameSpelling(String groupNickNameSpelling) {
        this.groupNickNameSpelling = groupNickNameSpelling;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Role getMemberRole() {
        Log.w("--->>>","role:"+role);
        return Role.getRole(role);
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }
}
