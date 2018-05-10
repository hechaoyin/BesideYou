package com.example.hbz.besideyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.activity.FriendInfoActivity;
import com.example.hbz.besideyou.activity.GroupContactsActivity;
import com.example.hbz.besideyou.activity.SearchActivity;
import com.example.hbz.besideyou.adapter.recycleviewadapter.ContactsAdapter;
import com.example.hbz.besideyou.adapter.recycleviewadapter.listener.OnItemClickListener;
import com.example.hbz.besideyou.bean.CardItemBean;
import com.example.hbz.besideyou.bean.ContactsBean;
import com.example.hbz.besideyou.bean.MultipleItemBean;
import com.example.hbz.besideyou.event.ContactsEvent;
import com.example.hbz.besideyou.tools.PinyinUtil;
import com.example.hbz.besideyou.utils.StatusBarUtils;
import com.example.hbz.besideyou.view.recycleview.GroupInfo;
import com.example.hbz.besideyou.view.recycleview.SectionDecoration;
import com.example.im.data.FriendContactsData;
import com.tencent.TIMUserProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import static com.example.hbz.besideyou.activity.SearchActivity.ADD_FRIEND;
import static com.example.hbz.besideyou.activity.SearchActivity.SEARCH_FRIEND;
import static com.example.hbz.besideyou.activity.SearchActivity.SEARCH_TYPE_KEY;
import static com.example.hbz.besideyou.adapter.recycleviewadapter.ContactsAdapter.CONTACT_ITEM_STYLE_FRIEND;
import static com.example.hbz.besideyou.adapter.recycleviewadapter.ContactsAdapter.CONTACT_ITEM_STYLE_TOP;


/**
 * @ClassName: com.example.besideyou.fragment
 * @Description: 通信录界面
 * @Author: HBZ
 * @Date: 2018/3/17 11:45
 */

public class ContactsFragment extends BaseFragment implements OnItemClickListener, Observer {

    private View rootView;

    private RecyclerView rv_contacts;
    private ArrayList<MultipleItemBean> contacts = new ArrayList<>();   // 适配器数据
    private ArrayList<GroupInfo> groupInfos = new ArrayList<>();    // 分组信息
    private ContactsAdapter contactsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);//订阅EventBus
        }
        // 添加联系人数据观察者
        FriendContactsData.getInstance().addObserver(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_contacts, container, false);
            View top_view = rootView.findViewById(R.id.top_view);
            StatusBarUtils.setStatus(getActivity(), top_view);
            initView(rootView);

            FriendContactsData instance = FriendContactsData.getInstance();// 获取联系人数据
            if (instance.getFriendContacts().size() <= 0) { // 如果没数据，就通知刷新
                instance.onRefreshData();
            }
        }
        return rootView;
    }

    private void initView(View rootView) {
        rv_contacts = (RecyclerView) rootView.findViewById(R.id.rv_contacts);
        contactsAdapter = new ContactsAdapter(contacts);
        contactsAdapter.setOnItemClickListener(this);
        rv_contacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_contacts.setAdapter(contactsAdapter);
        rv_contacts.addItemDecoration(new SectionDecoration(getContext(), position -> groupInfos.get(position)));
        //设置
        rootView.findViewById(R.id.iv_setting).setOnClickListener(v -> setting(v));
        //搜索好友
        rootView.findViewById(R.id.iv_search_friend).setOnClickListener(v -> searchFriend());
        //添加好友
        rootView.findViewById(R.id.iv_add_friend).setOnClickListener(v -> addFriend());

        SwipeRefreshLayout srl_refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_refresh);
        // 设置下拉进度的背景颜色，默认就是白色的
        srl_refresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        srl_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        srl_refresh.setOnRefreshListener(() -> {
            FriendContactsData.getInstance().onRefreshData();
            srl_refresh.setRefreshing(false);
        });


    }

    private void addFriend() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra(SEARCH_TYPE_KEY, ADD_FRIEND);
        startActivity(intent);
    }

    private void searchFriend() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra(SEARCH_TYPE_KEY, SEARCH_FRIEND);
        startActivity(intent);
    }

    private void setting(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.contacts_menu, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.add_friend:
                    addFriend();
                    break;
                case R.id.search_friend:

                    break;
                case R.id.set_add_friend_type:

                    break;
                default:
                    break;
            }
            return false;
        });
        //显示
        popup.show();
    }

    private void getContactsData() {
        HashMap<String, TIMUserProfile> friendContacts = FriendContactsData.getInstance().getFriendContacts();
        if (friendContacts.size() <= 0) {
            return;
        }
        Collection<TIMUserProfile> result = friendContacts.values();

        ArrayList<ContactsBean> contactsBeanList = new ArrayList<>();
        for (TIMUserProfile profile : result) {
            String name = profile.getNickName();
            if (TextUtils.isEmpty(name)) {
                name = profile.getIdentifier();
            }
            contactsBeanList.add(new ContactsBean(name, profile.getFaceUrl(), profile.getIdentifier()));
        }
        // 排序
        sortContacts(contactsBeanList);
    }

    private void sortContacts(ArrayList<ContactsBean> contactsBeanList) {
        // 转拼音
        HashMap<ContactsBean, String> contactsPinYin = new HashMap<>();
        for (ContactsBean contactsBean : contactsBeanList) {
            contactsPinYin.put(contactsBean, PinyinUtil.toPinyin(contactsBean.getName(), ""));
        }

        // 排序
        Collections.sort(contactsBeanList, (o1, o2) -> {
            String name1 = contactsPinYin.get(o1);
            String name2 = contactsPinYin.get(o2);
            if (TextUtils.isEmpty(name1)) {
                if (TextUtils.isEmpty(name2)) {
                    return 0;
                }
                return -1;

            } else if (TextUtils.isEmpty(name2)) {
                return 1;
            }

            // 字母在后面，其他在前
            char c1 = name1.charAt(0);
            char c2 = name2.charAt(0);
            if (!Character.isLetter(c1)) {//第一个字符串首字符不是字母
                if (Character.isLetter(c2)) {// 第二个是
                    return -1;
                }
            } else if (!Character.isLetter(c2)) {
                return 1;
            }

            // 直接返回比较结果。
            return name1.compareTo(name2);
        });

        // 创建适配器数据，并更新UI
        contacts.clear();
        groupInfos.clear();

        // 添加特殊两项（新的朋友、群聊）
        groupInfos.add(new GroupInfo(0, "↑", 0));
        contacts.add(new MultipleItemBean(CONTACT_ITEM_STYLE_TOP, new CardItemBean(R.drawable.ic_add_friend_blue, "新的朋友", "")));
        groupInfos.add(new GroupInfo(1, "↑", 1));
        contacts.add(new MultipleItemBean(CONTACT_ITEM_STYLE_TOP, new CardItemBean(R.drawable.ic_group_blue, "群聊", "")));


        String groupTitle = "";
        int position = 0; // 所在组的位置
        for (int i = 0; i < contactsBeanList.size(); i++) {
            ContactsBean contactsBean = contactsBeanList.get(i);

            // 适配器数据
            contacts.add(new MultipleItemBean(CONTACT_ITEM_STYLE_FRIEND, contactsBean));

            String pinyin = contactsPinYin.get(contactsBean);
            if (TextUtils.isEmpty(pinyin)) {
                pinyin = "!";
            }
            char c = pinyin.charAt(0);// 首字符
            if (!Character.isLetter(c)) { // 不是字母（数字或特殊字符）
                c = '✩';
            }
            String title = String.valueOf(c);
            if (!groupTitle.equals(title)) {
                position = 0;
            } else {
                position++;
            }

            // 分组数据
            groupInfos.add(new GroupInfo(i + 2, title, position));
            groupTitle = title;
        }
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position >= contacts.size()) {
            return;
        }
        MultipleItemBean multipleItemBean = contacts.get(position);
        if (multipleItemBean.getStyle() == CONTACT_ITEM_STYLE_FRIEND) {
            if (!(multipleItemBean.getData() instanceof ContactsBean)) {
                return;
            }
            ContactsBean contactsBean = (ContactsBean) multipleItemBean.getData();
            String identifier = contactsBean.getIdentifier();

            Intent intent = new Intent(getContext(), FriendInfoActivity.class);

            intent.putExtra(FriendInfoActivity.FRIEND_INFO_IDENTIFIER_KEY, identifier);// 会话标识
            intent.putExtra(FriendInfoActivity.FRIEND_INFO_IS_FRIEND_KEY, true);// 标志为好友

            ContactsFragment.this.startActivity(intent);
        } else if (multipleItemBean.getStyle() == CONTACT_ITEM_STYLE_TOP) {
            if (position == 0) { //TODO: 新的朋友

            } else if (position == 1) {
                startActivity(new Intent(getActivity(), GroupContactsActivity.class));
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onNotifyContactsEvent(ContactsEvent event) {
        FriendContactsData.getInstance().onRefreshData();
    }

    @Override
    public void onDestroy() {
        EventBus eventBus = EventBus.getDefault();
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);//解除订阅EventBus
        }
        FriendContactsData.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) { // 联系人数据更新监听器
        if (arg instanceof FriendContactsData.NotifyCmd) {
            FriendContactsData.NotifyCmd notifyCmd = (FriendContactsData.NotifyCmd) arg;
            switch (notifyCmd.type) {
                case ADD:
                case DEL:
                case REFRESH:
                    getContactsData();
                    break;
                default:
                    break;
            }
        }
    }
}
