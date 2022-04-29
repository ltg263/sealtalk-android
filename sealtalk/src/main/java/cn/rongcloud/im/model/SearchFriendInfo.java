package cn.rongcloud.im.model;

public class SearchFriendInfo {

    /**
     * id : cXA2vcS4a
     * accountName : gaga123
     * nickname : 嘎嘎
     * portraitUri : http://static-test.qchat.tech/FimKdcb7jjn-608P1wKta0_besGi
     * gender : 2
     */

    private String id;
    private String nickname;
    private String accountName;
    private String portraitUri;
    private String gender;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
