package cn.rongcloud.im.model;

/**
 * 请求上传图片结果
 */
public class UploadTokenResult {

    /**
     * 云存储类型
     */
    private String expire;
    /**
     * 云存储 Token
     */
    private String token;
    /**
     * 云存储图片地址域名
     */
    private String bucketDomain;
    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBucketDomain() {
        return bucketDomain;
    }

    public void setBucketDomain(String bucketDomain) {
        this.bucketDomain = bucketDomain;
    }
}
