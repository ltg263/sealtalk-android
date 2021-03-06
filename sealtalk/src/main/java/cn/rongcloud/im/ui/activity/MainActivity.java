package cn.rongcloud.im.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.im.R;
import cn.rongcloud.im.common.IntentExtra;
import cn.rongcloud.im.db.model.FriendShipInfo;
import cn.rongcloud.im.model.Resource;
import cn.rongcloud.im.model.Status;
import cn.rongcloud.im.model.VersionInfo;
import cn.rongcloud.im.ui.BaseActivity;
import cn.rongcloud.im.ui.dialog.MorePopWindow;
import cn.rongcloud.im.ui.fragment.MainContactsListFragment;
import cn.rongcloud.im.ui.fragment.MainDiscoveryFragment;
import cn.rongcloud.im.ui.fragment.MainMeFragment;
import cn.rongcloud.im.ui.view.MainBottomTabGroupView;
import cn.rongcloud.im.ui.view.MainBottomTabItem;
import cn.rongcloud.im.ui.widget.DragPointView;
import cn.rongcloud.im.ui.widget.TabGroupView;
import cn.rongcloud.im.ui.widget.TabItem;
import cn.rongcloud.im.viewmodel.AppViewModel;
import cn.rongcloud.im.viewmodel.MainViewModel;
import cn.rongcloud.im.utils.log.SLog;
import io.rong.imkit.RongIM;
import io.rong.imkit.conversationlist.ConversationListFragment;
import io.rong.imkit.picture.tools.ScreenUtils;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.model.Conversation;

public class MainActivity extends BaseActivity implements MorePopWindow.OnPopWindowItemClickListener {
    public static final String PARAMS_TAB_INDEX = "tab_index";
    private static final int REQUEST_START_CHAT = 0;
    private static final int REQUEST_START_GROUP = 1;
    private static final String TAG = "MainActivity";

    private ViewPager vpFragmentContainer;
    private MainBottomTabGroupView tabGroupView;
    private ImageView ivSearch;
    private ImageView ivMore;
    private AppViewModel appViewModel;
    public MainViewModel mainViewModel;
    private TextView tvTitle;
    private RelativeLayout btnSearch;
    private ImageButton btnMore;

    /**
     * tab ?????????
     */
    public enum Tab {
        /**
         * ??????
         */
        CHAT(0),
        /**
         * ?????????
         */
        CONTACTS(1),
        /**
         * ??????
         */
        FIND(2),
        /**
         * ??????
         */
        ME(3);

        private int value;

        Tab(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Tab getType(int value) {
            for (Tab type : Tab.values()) {
                if (value == type.getValue()) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * tabs ???????????????
     */
    private int[] tabImageRes = new int[]{
            R.drawable.seal_tab_chat_selector,
            R.drawable.seal_tab_contact_list_selector,
            R.drawable.seal_tab_find_selector,
            R.drawable.seal_tab_me_selector
    };

    /**
     * ?????? Fragment ??????
     */
    private List<Fragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_main);
//        setWindowStatusBarColor(this,R.color.white);
        initView();
        initViewModel();
        clearBadgeStatu();

    }
    //??????Activity?????????????????????????????????
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //?????????????????????
    private void clearBadgeStatu() {
        if (Build.MANUFACTURER.equalsIgnoreCase("HUAWEI")) {
            try {
                String packageName = getPackageName();
                String launchClassName = getPackageManager()
                        .getLaunchIntentForPackage(packageName)
                        .getComponent().getClassName();
                Bundle bundle = new Bundle();//?????????????????????
                bundle.putString("package", packageName);//??????
                bundle.putString("class", launchClassName);//?????????Activity????????????
                bundle.putInt("badgenumber", 0);//????????????????????????
                getContentResolver().call(
                        Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                        "change_badge", null, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ???????????????
     */
    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        btnSearch = findViewById(R.id.btn_search);
        btnMore = findViewById(R.id.btn_more);

        int tabIndex = getIntent().getIntExtra(PARAMS_TAB_INDEX, Tab.CHAT.getValue());

        // title
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SealSearchActivity.class);
                startActivity(intent);
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = vpFragmentContainer.getCurrentItem();
                if (currentItem == Tab.CHAT.getValue()) {
                    MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this, MainActivity.this);
                    morePopWindow.showPopupWindow(btnMore, 0.8f, -getXOffset(), 0);
                } else if (currentItem == Tab.CONTACTS.getValue()) {
                    onAddFriendClick();
                }

            }
        });

        // ????????????
        tabGroupView = findViewById(R.id.tg_bottom_tabs);
        vpFragmentContainer = findViewById(R.id.vp_main_container);

        // ??????????????? tabs
        initTabs();
        // ????????? fragment ??? viewpager
        initFragmentViewPager();

        // ????????????????????????????????????
        tabGroupView.setSelected(tabIndex);
        //???????????????
        tabGroupView.getView(2).setVisibility(View.GONE);
    }

    /**
     * ????????? Tabs
     */
    private void initTabs() {
        // ????????? tab
        List<TabItem> items = new ArrayList<>();
        String[] stringArray = getResources().getStringArray(R.array.tab_names);
        List<TabItem.AnimationDrawableBean> animationDrawableList = new ArrayList<>();
        animationDrawableList.add(new TabItem.AnimationDrawableBean(R.drawable.tab_chat_0, R.drawable.tab_chat_animation_list));
        animationDrawableList.add(new TabItem.AnimationDrawableBean(R.drawable.tab_contacts_0, R.drawable.tab_contacts_animation_list));
        animationDrawableList.add(new TabItem.AnimationDrawableBean(R.drawable.tab_chatroom_0, R.drawable.tab_chatroom_animation_list));
        animationDrawableList.add(new TabItem.AnimationDrawableBean(R.drawable.tab_me_0, R.drawable.tab_me_animation_list));
        for (Tab tab : Tab.values()) {
            TabItem tabItem = new TabItem();
            tabItem.id = tab.getValue();
            tabItem.text = stringArray[tab.getValue()];
            tabItem.animationDrawable = animationDrawableList.get(tab.getValue());
//            tabItem.drawable = tabImageRes[tab.getValue()];
            items.add(tabItem);
        }

        tabGroupView.initView(items, new TabGroupView.OnTabSelectedListener() {
            @Override
            public void onSelected(View view, TabItem item) {
                // ????????? tab ????????? ???????????????????????? fragment ??????
                int currentItem = vpFragmentContainer.getCurrentItem();
                if (currentItem != item.id) {
                    // ????????????
                    vpFragmentContainer.setCurrentItem(item.id);
                    if (item.id == Tab.ME.getValue()) {
                        // ???????????????????????? ???????????????
                        ((MainBottomTabItem) tabGroupView.getView(Tab.ME.getValue())).setRedVisibility(View.GONE);
                        tvTitle.setText(getResources().getStringArray(R.array.tab_names)[3]);
                        btnMore.setVisibility(View.GONE);
                        btnSearch.setVisibility(View.GONE);
                    }
                } else if (item.id == Tab.CHAT.getValue()) {
                    btnMore.setVisibility(View.VISIBLE);
                    btnSearch.setVisibility(View.VISIBLE);
                    btnMore.setImageDrawable(getResources().getDrawable(R.drawable.seal_ic_main_more));
                    tvTitle.setText(getResources().getStringArray(R.array.tab_names)[0]);
                } else if (item.id == Tab.CONTACTS.getValue()) {
                    btnMore.setVisibility(View.VISIBLE);
                    btnSearch.setVisibility(View.VISIBLE);
                    btnMore.setImageDrawable(getResources().getDrawable(R.drawable.seal_ic_main_add_friend));
                    tvTitle.setText(getResources().getStringArray(R.array.tab_names)[1]);
                } else if (item.id == Tab.FIND.getValue()) {
                    tvTitle.setText(getResources().getStringArray(R.array.tab_names)[2]);
                    btnMore.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.GONE);
                }
            }
        });

        tabGroupView.setOnTabDoubleClickListener(new MainBottomTabGroupView.OnTabDoubleClickListener() {
            @Override
            public void onDoubleClick(TabItem item, View view) {
                // ??????????????????????????????????????????
                if (item.id == Tab.CHAT.getValue()) {
                    //todo
//                    MainConversationListFragment fragment = (MainConversationListFragment) fragments.get(Tab.CHAT.getValue());
//                    fragment.focusUnreadItem();
                }
            }
        });

        // ???????????????
        ((MainBottomTabItem) tabGroupView.getView(Tab.CHAT.getValue())).setTabUnReadNumDragListener(new DragPointView.OnDragListencer() {

            @Override
            public void onDragOut() {
                ((MainBottomTabItem) tabGroupView.getView(Tab.CHAT.getValue())).setNumVisibility(View.GONE);
                showToast(getString(R.string.seal_main_toast_unread_clear_success));
                clearUnreadStatus();
            }
        });
        ((MainBottomTabItem) tabGroupView.getView(Tab.CHAT.getValue())).setNumVisibility(View.VISIBLE);
    }


    /**
     * ????????? initFragmentViewPager
     */
    private void initFragmentViewPager() {
        fragments.add(new ConversationListFragment());
        fragments.add(new MainContactsListFragment());
        fragments.add(new MainDiscoveryFragment());
        fragments.add(new MainMeFragment());

        // ViewPager ??? Adpater
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        vpFragmentContainer.setAdapter(fragmentPagerAdapter);
        vpFragmentContainer.setOffscreenPageLimit(fragments.size());
        // ????????????????????????
        vpFragmentContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // ?????????????????????????????? ??????????????? tab ????????????????????????
                tabGroupView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * ?????????ViewModel
     */
    private void initViewModel() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        appViewModel.getHasNewVersion().observe(this, new Observer<Resource<VersionInfo.AndroidVersion>>() {
            @Override
            public void onChanged(Resource<VersionInfo.AndroidVersion> resource) {
                if (resource.status == Status.SUCCESS && resource.data != null) {
                    if (tabGroupView.getSelectedItemId() != Tab.ME.getValue()) {
                        ((MainBottomTabItem) tabGroupView.getView(Tab.ME.getValue())).setRedVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // ????????????
        mainViewModel.getUnReadNum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                MainBottomTabItem chatTab = (MainBottomTabItem) tabGroupView.getView(Tab.CHAT.getValue());
                if (count == 0) {
                    chatTab.setNumVisibility(View.GONE);
                } else if (count > 0 && count < 100) {
                    chatTab.setNumVisibility(View.VISIBLE);
                    chatTab.setNum(String.valueOf(count));
                } else {
                    chatTab.setVisibility(View.VISIBLE);
                    chatTab.setNum(getString(R.string.seal_main_chat_tab_more_read_message));
                }
            }
        });

        // ???????????????
        mainViewModel.getNewFriendNum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                MainBottomTabItem chatTab = tabGroupView.getView(Tab.CONTACTS.getValue());
                if (count > 0) {
                    chatTab.setRedVisibility(View.VISIBLE);
                } else {
                    chatTab.setRedVisibility(View.GONE);
                }
            }
        });

        mainViewModel.getPrivateChatLiveData().observe(this, new Observer<FriendShipInfo>() {
            @Override
            public void onChanged(FriendShipInfo friendShipInfo) {
                Bundle bundle = new Bundle();
                bundle.putString("title", TextUtils.isEmpty(friendShipInfo.getDisplayName()) ? friendShipInfo.getNickname() : friendShipInfo.getDisplayName());
                RouteUtils.routeToConversationActivity(MainActivity.this, Conversation.ConversationType.PRIVATE,
                        friendShipInfo.getId(), bundle);
            }
        });

    }


    /**
     * ????????????????????????
     */
    private void clearUnreadStatus() {
        if (mainViewModel != null) {
            mainViewModel.clearMessageUnreadStatus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_START_CHAT:
                    mainViewModel.startPrivateChat(data.getStringExtra(IntentExtra.STR_TARGET_ID));
                    break;
                case REQUEST_START_GROUP:
                    ArrayList<String> memberList = data.getStringArrayListExtra(IntentExtra.LIST_STR_ID_LIST);
                    SLog.i(TAG, "memberList.size = " + memberList.size());
                    Intent intent = new Intent(this, CreateGroupActivity.class);
                    intent.putExtra(IntentExtra.LIST_STR_ID_LIST, memberList);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }


    }


    /**
     * ????????????
     */
    @Override
    public void onStartChartClick() {
        Intent intent = new Intent(this, SelectSingleFriendActivity.class);
        startActivityForResult(intent, REQUEST_START_CHAT);
    }

    /**
     * ????????????
     */
    @Override
    public void onCreateGroupClick() {
        Intent intent = new Intent(this, SelectCreateGroupActivity.class);
        startActivityForResult(intent, REQUEST_START_GROUP);
    }

    /**
     * ????????????
     */
    @Override
    public void onAddFriendClick() {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivity(intent);
    }

    /**
     * ?????????
     */
    @Override
    public void onScanClick() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private int getXOffset() {
        int marginEnd = ScreenUtils.dip2px(MainActivity.this, 12);
        float popSelfXOffset = getResources().getDimension(R.dimen.seal_main_title_popup_width) - btnMore.getWidth();
        return (int) (popSelfXOffset);

    }


}
