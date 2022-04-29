/**
 * Copyright 2019 bejson.com
 */
package cn.rongcloud.im.db.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.umeng.commonsdk.debug.D;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FriendShipInfo implements Parcelable {
    //接口内容
    @ColumnInfo(name = "id")
    private String id;//好友ID
    @ColumnInfo(name = "region")
    private String region;//手机号码区域
    @ColumnInfo(name = "phone_number")
    private String phone;//手机号码
    @ColumnInfo(name= "nickname")
    private String accountName;//趣品谈账号
    @ColumnInfo(name = "name")
    private String nickname;//昵称
    @ColumnInfo(name = "alias")
    private String displayName;//好友显示名称
    @ColumnInfo(name = "portrait_uri")
    private String portraitUri;//头像地址
    //gender   性别@(0:女,1:男,2:未知)
    @ColumnInfo(name = "friend_status")
    private int friendshipStatus;//好友关系状态@(10=待发送请求, 11=已发送请求, 20=同意请求, 21=忽略请求, 30=删除好友, 31=黑名单)
    @ColumnInfo(name = "updateAt")
    private String modifyTime;//记录修改时间
    @ColumnInfo(name= "nickname_spelling")

    private String groupDisplayNameSpelling;
    @ColumnInfo(name = "order_spelling")
    private String orderSpelling;
    @ColumnInfo(name = "name_spelling")
    private String nameSpelling;
    @ColumnInfo(name = "message")
    private String message;
    @ColumnInfo(name = "alias_spelling")
    private String disPlayNameSpelling;
    @Ignore
    private String firstCharacter;

    public String getFirstCharacter() {
        return firstCharacter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public int getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(int friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }

    public Date getModifyTime1() {
        return new Date(getMsToTime(modifyTime,"yyyy-MM-dd HH:mm:ss"));
    }

    public String getModifyTime() {
        return modifyTime;
    }

    /**
     * 描述: 将字符串转成毫秒数 格式年月日
     */
    public static long getMsToTime(String time, String layout) {
        try {
            Calendar c = Calendar.getInstance();
            if(time.contains("-")){
                c.setTime(new SimpleDateFormat(layout).parse(time));
            }else{
                c.setTime(new Date(Long.parseLong(time)));
            }
            return c.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getGroupDisplayNameSpelling() {
        return groupDisplayNameSpelling;
    }

    public void setGroupDisplayNameSpelling(String groupDisplayNameSpelling) {
        this.groupDisplayNameSpelling = groupDisplayNameSpelling;
    }

    public String getOrderSpelling() {
        return orderSpelling;
    }

    public void setOrderSpelling(String orderSpelling) {
        this.orderSpelling = orderSpelling;
        if (!TextUtils.isEmpty(orderSpelling)) {
            firstCharacter = orderSpelling.substring(0, 1).toUpperCase();
        }
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDisPlayNameSpelling() {
        return disPlayNameSpelling;
    }

    public void setDisPlayNameSpelling(String disPlayNameSpelling) {
        this.disPlayNameSpelling = disPlayNameSpelling;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.region);
        dest.writeString(this.phone);
        dest.writeString(this.accountName);
        dest.writeString(this.nickname);
        dest.writeString(this.displayName);
        dest.writeString(this.portraitUri);
        dest.writeInt(this.friendshipStatus);
        dest.writeString(this.modifyTime);
        dest.writeString(this.groupDisplayNameSpelling);
        dest.writeString(this.orderSpelling);
        dest.writeString(this.nameSpelling);
        dest.writeString(this.message);
        dest.writeString(this.disPlayNameSpelling);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.region = source.readString();
        this.phone = source.readString();
        this.accountName = source.readString();
        this.nickname = source.readString();
        this.displayName = source.readString();
        this.portraitUri = source.readString();
        this.friendshipStatus = source.readInt();
        this.modifyTime = source.readString();
        this.groupDisplayNameSpelling = source.readString();
        this.orderSpelling = source.readString();
        this.nameSpelling = source.readString();
        this.message = source.readString();
        this.disPlayNameSpelling = source.readString();
    }

    public FriendShipInfo() {
    }

    protected FriendShipInfo(Parcel in) {
        this.id = in.readString();
        this.region = in.readString();
        this.phone = in.readString();
        this.accountName = in.readString();
        this.nickname = in.readString();
        this.displayName = in.readString();
        this.portraitUri = in.readString();
        this.friendshipStatus = in.readInt();
        this.modifyTime = in.readString();
        this.groupDisplayNameSpelling = in.readString();
        this.orderSpelling = in.readString();
        this.nameSpelling = in.readString();
        this.message = in.readString();
        this.disPlayNameSpelling = in.readString();
    }

    public static final Parcelable.Creator<FriendShipInfo> CREATOR = new Parcelable.Creator<FriendShipInfo>() {
        @Override
        public FriendShipInfo createFromParcel(Parcel source) {
            return new FriendShipInfo(source);
        }

        @Override
        public FriendShipInfo[] newArray(int size) {
            return new FriendShipInfo[size];
        }
    };

    @Override
    public String toString() {
        return "FriendShipInfo{" +
                "id='" + id + '\'' +
                ", region='" + region + '\'' +
                ", phone='" + phone + '\'' +
                ", accountName='" + accountName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", portraitUri='" + portraitUri + '\'' +
                ", friendshipStatus=" + friendshipStatus +
                ", modifyTime=" + modifyTime +
                ", groupDisplayNameSpelling='" + groupDisplayNameSpelling + '\'' +
                ", orderSpelling='" + orderSpelling + '\'' +
                ", nameSpelling='" + nameSpelling + '\'' +
                ", message='" + message + '\'' +
                ", disPlayNameSpelling='" + disPlayNameSpelling + '\'' +
                '}';
    }
}