package com.example.hbz.besideyou.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.receiver.VolumeReceiver;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.PhotoUtils;
import com.example.hbz.besideyou.utils.SDCardPathUtil;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.example.hbz.besideyou.view.doodle.ColorSelectLayout;
import com.example.hbz.besideyou.view.doodle.DoodleView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DoodleActivity extends BastActivity {
    private static final int CODE_CAMERA_REQUEST = 1;//拍照
    private static final int CODE_GALLERY_REQUEST = 2;//相册
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/BesideYou/photo.jpg");
    private DoodleView doodle_view;
    private PopupWindow popupWindow;
    private List<Integer> colorList = new ArrayList<>();

    private AlertDialog.Builder normalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);

        initView();
        initBackDialog();
        initPopupWindow();
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inMutable = true;
//        op.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_test, op);
        doodle_view.setBitmap(bitmap);
//        doodle_view.setBitmap(null);
        doodle_view.invalidate();
    }

    @Override
    public void onBackPressed() {
        // 显示
        normalDialog.show();
    }

    public void initBackDialog() {
        normalDialog = new AlertDialog.Builder(this);
        normalDialog.setTitle("提示")
                .setMessage("你是要退出涂鸦，还是要切换后台?")
                .setPositiveButton("切换后台", (dialog, which) -> moveTaskToBack(false))
                .setNegativeButton("退出涂鸦", (dialog, which) -> finish());
    }


    private void initPopupWindow() {
        popupWindow = new PopupWindow(this);
        DisplayMetrics dm2 = getResources().getDisplayMetrics();//获取屏幕宽高
        popupWindow.setWidth((int) (dm2.widthPixels * 0.8f));
        popupWindow.setHeight((int) (dm2.heightPixels * 0.8f));

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_window_doodle, null);
        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.PopupAnimation); //动画
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);

        // 画笔大小设置
        TextView tv_brush_size = popupView.findViewById(R.id.tv_brush_size);
        SeekBar seek_bar = popupView.findViewById(R.id.seek_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seek_bar.setMin(1);
        }
        seek_bar.setMax(100);
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != 0) {
                    tv_brush_size.setText("" + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seek_bar.setProgress(5);

        // 画笔颜色设置
        int[] colors = getResources().getIntArray(R.array.doodle_color_48);
        for (int color : colors) {
            colorList.add(color);
        }
        ColorSelectLayout color_layout = popupView.findViewById(R.id.color_layout);
        color_layout.setColorList(colorList);

        // 后退 重置 返回
        popupView.findViewById(R.id.iv_doodle_back).setOnClickListener(v -> doodle_view.backOperation());
        popupView.findViewById(R.id.iv_doodle_reset).setOnClickListener(v -> doodle_view.resetOperation());
        popupView.findViewById(R.id.iv_doodle_advance).setOnClickListener(v -> doodle_view.advanceOperation());

        //选择背景图片。
        popupView.findViewById(R.id.iv_select_color).setOnClickListener(v -> selectBgByColor());
        popupView.findViewById(R.id.iv_select_camera).setOnClickListener(v -> selectBgByCamera());
        popupView.findViewById(R.id.iv_select_picture).setOnClickListener(v -> selectBgByPicture());

        // 保存涂鸦图片
        popupView.findViewById(R.id.btn_save_doodle).setOnClickListener(v -> saveDoodle());
        popupView.findViewById(R.id.btn_quit_doodle).setOnClickListener(v -> finish());

        popupWindow.setOnDismissListener(() -> {
            if (color_layout.getSelectColor() != Integer.MIN_VALUE) {
                doodle_view.setPaintColor(color_layout.getSelectColor());
            }
            doodle_view.setPaintStroke(seek_bar.getProgress() != 0 ? seek_bar.getProgress() : 1);
        });
    }

    private void dismissPopupWindow() {
        if (popupWindow == null || !popupWindow.isShowing()) {
            return;
        }
        popupWindow.dismiss();
    }

    private void showPopupWindow() {
        if (popupWindow == null || popupWindow.isShowing()) {
            return;
        }
        popupWindow.showAtLocation(doodle_view, Gravity.CENTER, 0, 0);
    }

    private void saveDoodle() {

        //  保存涂鸦
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(
                    Environment.getExternalStorageDirectory().getPath() +
                            "/BesideYou/Doodle/" + System.currentTimeMillis() + ".jpg"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap createBitmap = doodle_view.getCreateBitmap();
        boolean compress = false;
        if (createBitmap != null) {
            compress = createBitmap.compress(format, quality, stream);
        }
        if (compress) {
            ToastUtil.showLongToast("保存成功");
        } else {
            ToastUtil.showLongToast("保存失败");
        }

    }

    private void selectBgByColor() {
        dismissPopupWindow();
        //  根据颜色选择涂鸦背景 按钮
        PopupWindow bgPopupWindow = new PopupWindow(this);
        DisplayMetrics dm2 = getResources().getDisplayMetrics();//获取屏幕宽高
        bgPopupWindow.setWidth((int) (dm2.widthPixels * 0.8f));
        bgPopupWindow.setHeight((int) (dm2.heightPixels * 0.8f));

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_window_select_color, null);
        bgPopupWindow.setContentView(popupView);
        bgPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        bgPopupWindow.setOutsideTouchable(false);
//        bgPopupWindow.setAnimationStyle(R.style.PopupAnimation); //动画
        //点击空白处时，不隐藏掉pop窗口
        bgPopupWindow.setFocusable(true);

        ColorSelectLayout color_layout = (ColorSelectLayout) popupView.findViewById(R.id.color_layout);
        color_layout.setColorList(colorList);
        popupView.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            bgPopupWindow.dismiss();
            int color = color_layout.getSelectColor();
            if (Integer.MIN_VALUE == color) {
                return;
            }
            // 创建和屏幕相同的图
            Bitmap bitmap = Bitmap.createBitmap(dm2.widthPixels, dm2.heightPixels, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);
            canvas.drawColor(color);
            doodle_view.setBitmap(bitmap);
        });
        bgPopupWindow.showAtLocation(doodle_view, Gravity.CENTER, 0, 0);
    }

    private void selectBgByCamera() {
        //  根据相机选择涂鸦背景 按钮
        if (SDCardPathUtil.isHadSDCard()) {//判断是否有SD卡
            Uri imageUri = Uri.fromFile(fileUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //通过FileProvider创建一个content类型的Uri
                imageUri = FileProvider.getUriForFile(DoodleActivity.this, "com.example.hbz.besideyou.fileprovider", fileUri);
            }
            PhotoUtils.takePicture(DoodleActivity.this, imageUri, CODE_CAMERA_REQUEST);
        } else {
            Toast.makeText(DoodleActivity.this, "设备没有SD卡！", Toast.LENGTH_SHORT).show();
            LogUtil.e("设备没有SD卡");
        }
    }

    private void selectBgByPicture() {
        //  根据相册选择涂鸦背景 按钮
        PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inMutable = true;
//            op.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap;
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    bitmap = BitmapFactory.decodeFile(fileUri.getAbsolutePath(), op);
                    doodle_view.setBitmap(bitmap);
                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    String path = PhotoUtils.getPath(this, data.getData());
                    if (path != null && !"".equals(path)) {
                        bitmap = BitmapFactory.decodeFile(path, op);
                        doodle_view.setBitmap(bitmap);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initView() {
        doodle_view = (DoodleView) findViewById(R.id.doodle_view);
        doodle_view.setDoodleOnClickListener(() -> showPopupWindow());
    }

}
