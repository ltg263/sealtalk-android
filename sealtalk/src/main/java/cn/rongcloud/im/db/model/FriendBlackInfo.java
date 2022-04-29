package cn.rongcloud.im.db.model;


public class FriendBlackInfo {
    /**
     * modifyTime : 2022-04-23T07:59:54.347Z
     * friendId : string
     * nickname : string
     * portraitUri : string
     * gender : 0
     * region : string
     * phone : string
     */

    private String modifyTime;
    private String friendId;
    private String nickname;
    private String portraitUri;
    private int gender;
    private String region;
    private String phone;


    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}