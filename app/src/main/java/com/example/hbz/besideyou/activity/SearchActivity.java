package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BastActivity {

    public static final String SEARCH_TYPE_KEY = "search_type";
    public static final int SEARCH_FRIEND = 1;
    public static final int ADD_FRIEND = 2;
    private int searchType;

    private EditText ev_search_input;
    private ImageView iv_clear_search;
    private TextView tv_search;
    private ImageView iv_search_content_img;
    private TextView tv_search_content;
    private LinearLayout ll_search_content;
    private RecyclerView rv_search_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        if (intent != null) {
            searchType = intent.getIntExtra(SEARCH_TYPE_KEY, ADD_FRIEND);
        }
        initView();
    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> finish());

        ev_search_input = (EditText) findViewById(R.id.ev_search_input);
        ev_search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    if (ll_search_content.getVisibility() != View.GONE) {
                        ll_search_content.setVisibility(View.GONE);
                    }
                    if (iv_clear_search.getVisibility() != View.GONE) {
                        iv_clear_search.setVisibility(View.GONE);
                    }
                } else {
                    if (searchType == ADD_FRIEND) {
                        if (ll_search_content.getVisibility() != View.VISIBLE) {
                            ll_search_content.setVisibility(View.VISIBLE);
                        }
                        tv_search_content.setText(s);
                    }
                    if (iv_clear_search.getVisibility() != View.VISIBLE) {
                        iv_clear_search.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_clear_search = (ImageView) findViewById(R.id.iv_clear_search);
        iv_clear_search.setOnClickListener(v -> ev_search_input.setText(""));

        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(v -> submitSearch());

        iv_search_content_img = (ImageView) findViewById(R.id.iv_search_content_img);
        switch (searchType) {
            case SEARCH_FRIEND:
                break;
            case ADD_FRIEND:
                iv_search_content_img.setImageResource(R.drawable.ic_add_friend);
                break;
            default:
                break;
        }
        tv_search_content = (TextView) findViewById(R.id.tv_search_content);
        ll_search_content = (LinearLayout) findViewById(R.id.ll_search_content);
        ll_search_content.setOnClickListener(v -> submitSearch());

        //TODO 搜索结果内容
        rv_search_result = (RecyclerView) findViewById(R.id.rv_search_result);

    }

    private void submitSearch() {
        String input = ev_search_input.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> users = new ArrayList<>();
        users.add(input);

        if (searchType == ADD_FRIEND) {
            TIMFriendshipManager.getInstance().getUsersProfile(users,
                    new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {
                            LogUtil.e("搜索用户出错：" + i + "," + s);
                            ToastUtil.showShortToast("搜索用户出错：" + s);
                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            if (timUserProfiles == null || timUserProfiles.size() <= 0) {
                                return;
                            }
                            TIMUserProfile timUserProfile = timUserProfiles.get(0);
                            String identifier = timUserProfile.getIdentifier();
                            if (!TextUtils.isEmpty(identifier)) {
                                Intent intent = new Intent(SearchActivity.this, FriendInfoActivity.class);
                                intent.putExtra(FriendInfoActivity.FRIEND_INFO_IDENTIFIER_KEY, identifier);// 会话标识
                                intent.putExtra(FriendInfoActivity.FRIEND_INFO_IS_FRIEND_KEY, false);// 会话标识
                                startActivity(intent);
                            }
                        }
                    });
        }


    }
}
