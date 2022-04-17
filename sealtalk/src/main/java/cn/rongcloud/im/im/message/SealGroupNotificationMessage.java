package cn.rongcloud.im.im.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.message.GroupNotificationMessage;

@MessageTag(value = "QC:GroupNotice", flag = MessageTag.ISPERSISTED)
public class SealGroupNotificationMessage extends GroupNotificationMessage {


    public SealGroupNotificationMessage(Parcel in) {
        super(in);
    }

    public SealGroupNotificationMessage(byte[] data) {
        super(data);
    }
}
