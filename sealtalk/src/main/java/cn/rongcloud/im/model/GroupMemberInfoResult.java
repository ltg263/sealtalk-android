package cn.rongcloud.im.model;

import androidx.room.ColumnInfo;


public class GroupMemberInfoResult {

    /**
     * id : Vi5adfg4M
     * createTime : 2022-04-26 13:28:38
     * modifyTime : 2022-04-26 13:28:38
     * userNickname : 啦啦
     * groupNickname :
     * groupRole : 0
     * displayName :
     * region :
     * phone :
     * wechat :
     * alipay :
     * memberDesc :
     * portraitUri : http://static-test.qchat.tech/Fvk59GNIbpbLlN8Rn6jLfqtU7o3T
     * gender : 2
     * rongCloudToken : ibxaptzifgoc8uTb72i9c7TDlCFMWZczvnYwSk/RGQE=@5fhy.cn.rongnav.com;5fhy.cn.rongcfg.com
     */

    private String id;
    private String createTime;
    private String modifyTime;
    private String userNickname;
    private String groupNickname;
    private int groupRole;
    private String displayName;
    private String region;
    private String phone;
    private String wechat;
    private String alipay;
    private String memberDesc;
    @ColumnInfo(name = "portrait_uri")
    private String portraitUri;
    private String gender;
    private String rongCloudToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getGroupNickname() {
        return groupNickname;
    }

    public void setGroupNickname(String groupNickname) {
        this.groupNickname = groupNickname;
    }

    public int getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(int groupRole) {
        this.groupRole = groupRole;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayNameX) {
        this.displayName = displayNameX;
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

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getMemberDesc() {
        return memberDesc;
    }

    public void setMemberDesc(String memberDesc) {
        this.memberDesc = memberDesc;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRongCloudToken() {
        return rongCloudToken;
    }

    public void setRongCloudToken(String rongCloudToken) {
        this.rongCloudToken = rongCloudToken;
    }
}
