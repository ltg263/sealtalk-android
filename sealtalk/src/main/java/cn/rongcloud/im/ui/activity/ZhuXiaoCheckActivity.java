package cn.rongcloud.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import cn.rongcloud.im.R;
import cn.rongcloud.im.model.Resource;
import cn.rongcloud.im.model.Status;
import cn.rongcloud.im.ui.BaseActivity;
import cn.rongcloud.im.utils.ToastUtils;
import cn.rongcloud.im.viewmodel.LoginViewModel;
import cn.rongcloud.im.viewmodel.UserInfoViewModel;

public class ZhuXiaoCheckActivity extends BaseActivity {
    private LoginViewModel loginViewModel;
    private UserInfoViewModel userInfoViewModel;
    NumberEditText mNetCheck;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_zhuxiao);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        TextView tv_po = findViewById(R.id.tv_po);
        tv_po.setText("短信已发送");
        mNetCheck = findViewById(R.id.net_check);
        mNetCheck.setOnInputFinish(new NumberEditText.OnInputFinishListener() {
            @Override
            public void onInputFinish(String code) {
                userMobileLogin(code);
            }
        });
        loginViewModel.sendCodeAccountClose();
        // 获取删除好友
        userInfoViewModel.getUserCloseResult().observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> resource) {
                if (resource.status == Status.SUCCESS) {
                    ToastUtils.showToast("注销成功");
                    // 通知退出
                    sendLogoutNotify();
                    Intent intent = new Intent(ZhuXiaoCheckActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (resource.status == Status.ERROR) {
                    // 删除失败时置回删除行为
                    ToastUtils.showToast("注销失败");
                }
            }
        });

        loginViewModel.getSendCodeState().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> resource) {
                //提示
                if (resource.status == Status.SUCCESS) {
                    sessionId = resource.data;
                    showToast(R.string.seal_login_toast_send_code_success);
                } else if (resource.status == Status.LOADING) {

                } else {
                    showToast(resource.message);
                }
            }
        });
    }
    String sessionId = "";
    private void userMobileLogin(String code) {
        userInfoViewModel.userClose(sessionId ,code);
    }
}
