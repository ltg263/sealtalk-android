package cn.rongcloud.im.net;

import cn.rongcloud.im.BuildConfig;

public class SealTalkUrl {
    public static final String DOMAIN = BuildConfig.SEALTALK_SERVER;
    //用户登录
    public static final String LOGIN = "user/login";
    //获取融云token
    public static final String GET_TOKEN = "user/rongcloud/token";
    //获取用户信息
    public static final String GET_USER_INFO = "user/find/by_id";
    //向手机发送验证码
    public static final String SEND_CODE = "sms/login_code/send";

    public static final String VERIFY_CODE = "user/verify_code_yp";
    //注册新用户
    public static final String REGISTER = "user/register/create";

    public static final String REGION_LIST = "user/regionlist";
    //检查手机号是否可以注册
    public static final String CHECK_PHONE_AVAILABLE = "user/register/phone/validate";
    //重置密码
    public static final String RESET_PASSWORD = "user/password/reset";
    //获取七牛云存储token
    public static final String GET_IMAGE_UPLOAD_TOKEN = "user/get_image_token";
    //创建群组
    public static final String GROUP_CREATE = "group/create";
    //添加群成员
    public static final String GROUP_ADD_MEMBER = "group/memeber/add";
    //用户加入群组
    public static final String GROUP_JOIN = "group/join";
    //群主或群管理将群成员移出群组
    public static final String GROUP_KICK_MEMBER = "group/memeber/kick";
    //退出群组
    public static final String GROUP_QUIT = "group/quit";
    //解散群组
    public static final String GROUP_DISMISS = "group/dismiss";
    //转让群主
    public static final String GROUP_TRANSFER = "group/owner/transfer";
    //群组重命名
    public static final String GROUP_RENAME = "group/name/set";
    //设置群定时清理状态
    public static final String GROUP_SET_REGULAR_CLEAR = "group/message/clean/set";

    public static final String GROUP_GET_REGULAR_CLEAR_STATE = "group/get_regular_clear";
    //发布群公告
    public static final String GROUP_SET_BULLETIN = "group/bulletin/set";

    public static final String GROUP_GET_BULLETIN = "group/get_bulletin";
    //设置群头像
    public static final String GROUP_SET_PORTRAIT_URL = "group/portrait/set";
    //设置自己的群名片
    public static final String GROUP_SET_DISPLAY_NAME = "group/display_name/set";
    //获取群信息
    public static final String GROUP_GET_INFO = "group/info";
    //获取群成员列表
    public static final String GROUP_GET_MEMBER_INFO = "group/members";
    //删除群组通讯录
    public static final String GROUP_SAVE_TO_CONTACT_REMOVE = "group/fav/remove";
    //保存群组至通讯录
    public static final String GROUP_SAVE_TO_CONTACT_ADD = "group/fav/add";

    public static final String GROUP_GET_ALL_IN_CONTACT = "user/favgroups";
    //获取群验证通知消息
    public static final String GROUP_GET_NOTICE_INFO = "group/notice/list";
    //同意群邀请
    public static final String GROUP_SET_NOTICE_STATUS = "group/invite/agree";

    public static final String GROUP_CLEAR_NOTICE = "group/clear_notice";
    //复制群组
    public static final String GROUP_COPY = "group/copy";
    //获取退群列表
    public static final String GROUP_GET_EXITED = "group/member/quit/list";
    //获取群成员信息
    public static final String GROUP_GET_MEMBER_INFO_DES = "group/member/info";
    //设置群成员信息
    public static final String GROUP_SET_MEMBER_INFO_DES = "group/member_info/set";
    //获取好友列表
    public static final String GET_FRIEND_ALL = "friendship/list";
    //获取当前用户黑名单列表
    public static final String GET_BLACK_LIST = "user/blacklist";
    //将好友加入黑名单
    public static final String ADD_BLACK_LIST = "user/blacklist/add";
    //将好友移除黑名单
    public static final String REMOVE_BLACK_LIST = "user/blacklist/remove";
    //设置当前用户昵称
    public static final String SET_NICK_NAME = "user/nickname/set";
    //设置 SealTalk 号
    public static final String SET_ST_ACCOUNT = "user/qc_account/set";
    //设置性别
    public static final String SET_GENDER = "user/gender/set";
    //设置头像
    public static final String SET_PORTRAIT = "user/portrait/set";
    //同意添加好友
    public static final String ARGEE_FRIENDS = "friendship/agree";
    //忽略好友请求
    public static final String INGORE_FRIENDS = "friendship/ignore";
    //获取好友信息
    public static final String GET_FRIEND_PROFILE = "friendship/profile";
    //设置好友备注名
    public static final String SET_DISPLAY_NAME = "friendship/display_name/set";
    //发起添加好友
    public static final String INVITE_FRIEND = "friendship/invite";
    //删除好友
    public static final String DELETE_FREIND = "friendship/delete";

    public static final String GET_CONTACTS_INFO = "friendship/get_contacts_info";

    public static final String CLIENT_VERSION = "misc/client_version";
    //修改密码
    public static final String CHANGE_PASSWORD = "user/password/change";

    public static final String GET_DISCOVERY_CHAT_ROOM = "misc/demo_square";
    //根据手机号查找用户信息
    public static final String FIND_FRIEND = "user/find/by_phone";
    //批量删除管理员
    public static final String GROUP_REMOVE_MANAGER = "group/mamager/remove";
    //批量增加管理员
    public static final String GROUP_ADD_MANAGER = "group/mamager/add";
    //设置/取消 全员禁言
    public static final String GROUP_MUTE_ALL = "group/mute/set";
    //设置群成员保护模式
    public static final String GROUP_MEMBER_PROTECTION = "group/member_protect/set";
    //设置群认证
    public static final String GROUP_SET_CERTIFICATION = "group/join_verify/set";
    //设置个人隐私设置
    public static final String SET_PRIVACY = "user/privacy/set";
    //获取个人隐私设置
    public static final String GET_PRIVACY = "user/privacy/get";

    public static final String GET_SCREEN_CAPTURE = "misc/get_screen_capture";

    public static final String SET_SCREEN_CAPTURE = "misc/set_screen_capture";

    public static final String SEND_SC_MSG = "misc/send_sc_msg";
    //设置接收戳一下消息状态
    public static final String SET_RECEIVE_POKE_MESSAGE_STATUS = "user/poke/set";
    //获取接收戳一下消息状态
    public static final String GET_RECEIVE_POKE_MESSAGE_STATUS = "user/poke/get";
    //设置朋友备注和描述
    public static final String SET_FRIEND_DESCRIPTION = "friendship/meta/set";
    //获取朋友备注和描述
    public static final String GET_FRIEND_DESCRIPTION = "friendship/meta/get";
    //批量删除好友
    public static final String MULTI_DELETE_FRIEND = "friendship/delete/batch";
    //短信验证即登录
    public static final String REGISTER_AND_LOGIN = "user/code_register";
}
