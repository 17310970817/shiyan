package com.hq.hqmusic.UI;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hq.hqmusic.Adapter.MyAdapter;
import com.hq.hqmusic.CustomView.CustomDialog;
import com.hq.hqmusic.CustomView.GradientTextView;
import com.hq.hqmusic.CustomView.MyDialog;
import com.hq.hqmusic.Entity.Song;
import com.hq.hqmusic.R;
import com.hq.hqmusic.StatusBar.BaseActivity;
import com.hq.hqmusic.StatusBar.SystemBarTintManager;
import com.hq.hqmusic.Utils.ImageCacheUtil;
import com.hq.hqmusic.Utils.MusicUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private ListView listview;
    private List<Song> list;
    private MyAdapter adapter;
    private PopupWindow popupWindow;
    private MediaPlayer mplayer = new MediaPlayer();
    private GradientTextView text_main;
    private MyDialog myDialog, myDialog_bestlove;
    private SeekBar seekBar;
    private TextView textView1, textView2;
    private ImageView imageView_play, imageView_next, imageView_front,
            imageview, imageview_playstyle, imageview_location,
            imageview_setting;
    private int screen_width;
    private Random random = new Random();
    // ????????????????????????????????????0->????????????,1->????????????,2->????????????
    private int play_style = 0;
    // ??????seekbar??????????????????
    private boolean ischanging = false;
    private Thread thread;
    // ????????????????????????,???0??????
    private int currentposition;
    // ?????????????????????listview??????
    private int max_item;
    // ??????????????????????????????
    private String string_theme;
    // ?????????????????????????????????
    private SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //????????????
        getAuthority();

        sharedPreferences = getSharedPreferences("location", MODE_PRIVATE);
        // ????????????
        string_theme = sharedPreferences.getString("theme_select", "blue");
        if (string_theme.equals("blue")) {
            setTheme(R.style.Theme_blue);
        } else if (string_theme.equals("purple")) {
            setTheme(R.style.Theme_purple);
        } else if (string_theme.equals("green")) {
            setTheme(R.style.Theme_green);
        } else {
            setTheme(R.style.Theme_red);
        }
        setContentView(R.layout.activity_main);

        // ???????????????????????????
        mTintManager = new SystemBarTintManager(MainActivity.this);
        mTintManager.setStatusBarTintEnabled(true);
        if (string_theme.equals("blue")) {
            mTintManager.setStatusBarTintResource(R.color.blue);
        } else if (string_theme.equals("purple")) {
            mTintManager.setStatusBarTintResource(R.color.purple);
        } else if (string_theme.equals("green")) {
            mTintManager.setStatusBarTintResource(R.color.green);
        } else {
            mTintManager.setStatusBarTintResource(R.color.red);
        }

        // ??????????????????????????????screen_width???
        init_screen_width();

        // ??????currentposition???????????????
        currentposition = sharedPreferences.getInt("currentposition", 0);

        // ???????????????????????????
        initTopView();

        // ????????? ?????????????????????????????????
        setClick();

        // ???textView1???textView2?????????
        initText();

        // ??????popupwindow?????????????????????
        setpopupwindow();

        // listview?????????,????????????,???????????????????????????
        setListView();

        // ??????mediaplayer?????????
        setMediaPlayerListener();

    }

    @Override
    protected void onDestroy() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("song_name",
                cut_song_name(list.get(currentposition).getSong()));
        editor.putString("song_singer", list.get(currentposition).getSinger());
        editor.putInt("currentposition", currentposition);
        editor.commit();
        if (mplayer.isPlaying()) {
            mplayer.stop();
        }
        mplayer.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {

            Bitmap bitmap = ImageCacheUtil.getResizedBitmap(null, null,
                    MainActivity.this, data.getData(), screen_width, true);
            BitmapDrawable drawable = new BitmapDrawable(null, bitmap);

            listview.setBackground(drawable);
            saveDrawable(drawable);
            myDialog.dismiss();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ???????????????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        final CustomDialog customDialog = new CustomDialog(MainActivity.this,
                R.layout.layout_customdialog, R.style.dialogTheme);
        customDialog.setT("????????????");
        customDialog.setM("???????????????????????????????");
        customDialog.setButtonLeftText("??????");
        customDialog.setButtonRightText("??????");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (myDialog.isShowing()) {
                myDialog.dismiss();
            } else if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                customDialog.show();
                customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {

                    @Override
                    public void onClickOk() {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        customDialog.dismiss();
                    }

                    @Override
                    public void onClickCancel() {
                        // TODO Auto-generated method stub
                        customDialog.cancel();
                    }
                });
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ?????????????????????
    private void init_screen_width() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screen_width = size.x;
    }

    // ??????popupwindow??????????????????????????????????????????
    private void setpopupwindow() {
        View popup_layout = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.popupwindow_setting, null);
        popupWindow = new PopupWindow(popup_layout, ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        RelativeLayout relativeLayout_listbg = (RelativeLayout) popup_layout
                .findViewById(R.id.layout_listbg);

        RelativeLayout relativeLayout_skin = (RelativeLayout) popup_layout
                .findViewById(R.id.layout_skin);

        RelativeLayout relativeLayout_location = (RelativeLayout) popup_layout
                .findViewById(R.id.layout_location_bestlove);

        RelativeLayout relativeLayout_exit = (RelativeLayout) popup_layout
                .findViewById(R.id.layout_exit);

        // ?????????????????????????????????????????????dialog?????????????????????
        myDialog = new MyDialog(MainActivity.this, R.style.dialogTheme,
                R.layout.popup_changelistbg);
        final Window window = myDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.enter);

        relativeLayout_listbg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                popupWindow.dismiss();

                myDialog.show();
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = screen_width;
                params.dimAmount = 0.4f;
                window.setAttributes(params);

                // ??????dialog???????????????
                RelativeLayout select_localimage = (RelativeLayout) myDialog
                        .findViewById(R.id.select_localimage);
                RelativeLayout use_default = (RelativeLayout) myDialog
                        .findViewById(R.id.use_default);
                RelativeLayout select_xhh = (RelativeLayout) myDialog
                        .findViewById(R.id.select_xhh);
                select_localimage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra("return-data", true);

                        intent.putExtra("crop", "circle");
                        // ??????Intent.ACTION_GET_CONTENT??????Action
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        // ??????????????????????????????
                        startActivityForResult(intent, 1);
                    }
                });
                use_default.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        listview.setBackground(null);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("listbg", "");
                        editor.commit();
                        myDialog.dismiss();
                    }
                });
                select_xhh.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        listview.setBackground(getResources().getDrawable(
                                R.mipmap.xhh));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("listbg", "xhh");
                        editor.commit();
                        myDialog.dismiss();
                    }
                });

            }
        });

        relativeLayout_skin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("song_name",
                        cut_song_name(list.get(currentposition).getSong()));
                editor.putString("song_singer",
                        list.get(currentposition).getSinger());
                editor.putInt("currentposition", currentposition);

                editor.commit();
                Intent intent = new Intent(MainActivity.this,
                        ThemeSettingActivity.class);
                MainActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);

                if (mplayer.isPlaying()) {
                    mplayer.stop();
                    mplayer.reset();
                }
            }
        });

        // ?????????????????????
        relativeLayout_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                int bestlove_position = sharedPreferences.getInt(
                        "bestlove_position", 0);
                currentposition = bestlove_position;

                adapter.setFlag(currentposition);
                switch (string_theme) {
                    case "blue":
                        adapter.setTheme("blue");
                        break;
                    case "purple":
                        adapter.setTheme("purple");
                        break;
                    case "green":
                        adapter.setTheme("green");
                        break;
                    case "red":
                        adapter.setTheme("red");
                        break;
                    default:
                        break;
                }
                adapter.notifyDataSetChanged();

                if (bestlove_position - 3 < 0) {
                    listview.setSelection(0);
                } else {
                    listview.setSelection(bestlove_position - 3);
                }
                musicplay(bestlove_position);
            }
        });

        // ????????????????????????????????????dialog?????????????????????
        final CustomDialog customDialog = new CustomDialog(MainActivity.this,
                R.layout.layout_customdialog, R.style.dialogTheme);
        customDialog.setT("????????????");
        customDialog.setM("???????????????????????????????");
        customDialog.setButtonLeftText("??????");
        customDialog.setButtonRightText("??????");

        relativeLayout_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();

                customDialog.show();
                customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {

                    @Override
                    public void onClickOk() {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        customDialog.dismiss();
                    }

                    @Override
                    public void onClickCancel() {
                        // TODO Auto-generated method stub
                        customDialog.cancel();
                    }
                });
            }
        });
    }

    private void initTopView() {
        text_main = (GradientTextView) this.findViewById(R.id.text_main);
        imageview_playstyle = (ImageView) this.findViewById(R.id.play_style);
        imageview_setting = (ImageView) this.findViewById(R.id.settings);
        imageview_location = (ImageView) this
                .findViewById(R.id.imageview_location);
    }

    private void setClick() {

        imageview_setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.showAsDropDown(v, 0, 10);
                ;
            }
        });

        imageview_playstyle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                play_style++;
                if (play_style > 2) {
                    play_style = 0;
                }

                switch (play_style) {
                    case 0:
                        imageview_playstyle.setImageResource(R.mipmap.cicle);
                        Toast.makeText(MainActivity.this, "????????????",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        imageview_playstyle.setImageResource(R.mipmap.ordered);
                        Toast.makeText(MainActivity.this, "????????????",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        imageview_playstyle.setImageResource(R.mipmap.unordered);
                        Toast.makeText(MainActivity.this, "????????????",
                                Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });

        imageview_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (currentposition - 3 <= 0) {
                    listview.setSelection(0);
                } else {
                    listview.setSelection(currentposition - 3);
                }

            }
        });

        View layout_playbar = (View) findViewById(R.id.layout_playbar);
        imageview = (ImageView) layout_playbar.findViewById(R.id.imageview);
        imageView_play = (ImageView) layout_playbar
                .findViewById(R.id.imageview_play);
        imageView_next = (ImageView) layout_playbar
                .findViewById(R.id.imageview_next);
        imageView_front = (ImageView) layout_playbar
                .findViewById(R.id.imageview_front);
        textView1 = (TextView) layout_playbar.findViewById(R.id.name);
        textView2 = (TextView) layout_playbar.findViewById(R.id.singer);
        seekBar = (SeekBar) layout_playbar.findViewById(R.id.seekbar);
        imageView_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (string_theme) {
                    case "blue":
                        change_play_image(R.mipmap.play, R.mipmap.pause);
                        break;
                    case "purple":
                        change_play_image(R.mipmap.play_purple,
                                R.mipmap.pause_purple);
                        break;
                    case "green":
                        change_play_image(R.mipmap.play_green,
                                R.mipmap.pause_green);
                        break;
                    case "red":
                        change_play_image(R.mipmap.play_red, R.mipmap.pause_red);
                        break;
                    default:
                        break;
                }

                if (mplayer.isPlaying()) {
                    mplayer.pause();
                    imageview.clearAnimation();
                } else {
                    mplayer.start();
                    // thread = new Thread(new SeekBarThread());
                    // thread.start();
                    imageview.startAnimation(AnimationUtils.loadAnimation(
                            MainActivity.this, R.anim.imageview_rotate));
                }
            }
        });

        imageView_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (play_style == 2) {
                    random_nextMusic();
                    auto_change_listview();
                } else {
                    nextMusic();
                    auto_change_listview();
                }
            }
        });

        imageView_front.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (play_style == 2) {
                    random_nextMusic();
                    auto_change_listview();
                } else {
                    frontMusic();
                    auto_change_listview();
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                ischanging = false;
                mplayer.seekTo(seekBar.getProgress());
                thread = new Thread(new SeekBarThread());
                thread.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                ischanging = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                // ????????????????????????????????????????????????
            }
        });

    }

    private void initText() {

        textView1.setText(sharedPreferences.getString("song_name", "?????????").trim());
        textView2.setText(sharedPreferences.getString("song_singer", "??????").trim());
    }

    private void setListView() {
        listview = (ListView) this.findViewById(R.id.listveiw);

        list = new ArrayList<Song>();
        list = MusicUtils.getMusicData(MainActivity.this);
        adapter = new MyAdapter(MainActivity.this, list);
        // ?????????????????????????????????????????????
        adapter.setFlag(currentposition);
        switch (string_theme) {
            case "blue":
                adapter.setTheme("blue");
                break;
            case "purple":
                adapter.setTheme("purple");
                break;
            case "green":
                adapter.setTheme("green");
                break;
            case "red":
                adapter.setTheme("red");
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();

        listview.setAdapter(adapter);

        // ?????????????????????????????????????????????????????????????????????????????????????????????
        check_location();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                currentposition = position;
                musicplay(currentposition);

                text_main.setText(cut_song_name(list.get(currentposition).getSong()));
                adapter.setFlag(currentposition);
                adapter.notifyDataSetChanged();
            }
        });

        myDialog_bestlove = new MyDialog(MainActivity.this,
                R.style.dialogTheme, R.layout.setting_best_lovesong);
        final Window window2 = myDialog_bestlove.getWindow();
        window2.setGravity(Gravity.CENTER);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                myDialog_bestlove.show();
                WindowManager.LayoutParams params = window2.getAttributes();
                params.width = (int) (screen_width * 0.75);
                params.dimAmount = 0.4f;
                window2.setAttributes(params);

                RelativeLayout relativeLayout_make_bestlove = (RelativeLayout) myDialog_bestlove
                        .findViewById(R.id.make_bestlove);
                RelativeLayout relativeLayout_make_cancel = (RelativeLayout) myDialog_bestlove
                        .findViewById(R.id.make_cancel);
                final int best_love_position = position;
                relativeLayout_make_bestlove
                        .setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                myDialog_bestlove.dismiss();

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("bestlove_position",
                                        best_love_position);
                                editor.commit();
                                Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT)
                                        .show();

                            }
                        });
                relativeLayout_make_cancel
                        .setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                myDialog_bestlove.dismiss();
                            }
                        });
                return true;
            }
        });

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                if (currentposition >= firstVisibleItem
                        && currentposition <= firstVisibleItem
                        + visibleItemCount) {
                    imageview_location.setVisibility(View.INVISIBLE);
                } else {
                    imageview_location.setVisibility(View.VISIBLE);
                }
                max_item = visibleItemCount;
            }
        });
        // ???listview??????????????????
        if (loadDrawable() != null) {
            listview.setBackground(loadDrawable());
        } else if (sharedPreferences.getString("listbg", "").equals("xhh")) {
            listview.setBackground(getResources().getDrawable(R.mipmap.xhh));
        } else {
            listview.setBackground(null);
        }
    }

    private void musicplay(int position) {

        textView1.setText(cut_song_name(list.get(position).getSong()).trim());
        textView2.setText(list.get(position).getSinger().trim());
        text_main.setText(cut_song_name(list.get(currentposition).getSong()));
        seekBar.setMax(list.get(position).getDuration());
        imageview.startAnimation(AnimationUtils.loadAnimation(
                MainActivity.this, R.anim.imageview_rotate));
        switch (string_theme) {
            case "blue":
                imageView_play.setImageResource(R.mipmap.pause);
                break;
            case "purple":
                imageView_play.setImageResource(R.mipmap.pause_purple);
                break;
            case "green":
                imageView_play.setImageResource(R.mipmap.pause_green);
                break;
            case "red":
                imageView_play.setImageResource(R.mipmap.pause_red);
                break;
            default:
                break;
        }

        try {
            mplayer.reset();
            mplayer.setDataSource(list.get(position).getPath());
            mplayer.prepare();
            mplayer.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
        thread = new Thread(new SeekBarThread());
        thread.start();
    }

    private void setMediaPlayerListener() {
        // ??????mediaplayer?????????????????????
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                switch (play_style) {
                    case 0:
                        musicplay(currentposition);
                        break;
                    case 1:
                        // ????????????????????????????????????????????????????????????????????????????????????????????????
                        nextMusic();
                        break;
                    case 2:
                        random_nextMusic();
                        break;
                    default:

                        break;
                }
            }
        });
        // ???????????????????????????
        mplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                mp.reset();
                // Toast.makeText(MainActivity.this, "???????????????", 1500).show();
                return false;
            }
        });
    }

    // ??????????????????,????????????seekbar?????????
    class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (!ischanging && mplayer.isPlaying()) {
                // ???SeekBar?????????????????????????????????
                seekBar.setProgress(mplayer.getCurrentPosition());

                try {
                    // ???500????????????????????????
                    Thread.sleep(500);
                    // ????????????

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ?????????
    private void nextMusic() {
        currentposition++;
        if (currentposition > list.size() - 1) {
            currentposition = 0;
        }
        musicplay(currentposition);
        adapter.setFlag(currentposition);
        adapter.notifyDataSetChanged();
    }

    // ?????????
    private void frontMusic() {
        currentposition--;
        if (currentposition < 0) {
            currentposition = list.size() - 1;
        }
        musicplay(currentposition);
        adapter.setFlag(currentposition);
        adapter.notifyDataSetChanged();
    }

    // ?????????????????????
    private void random_nextMusic() {
        currentposition = currentposition + random.nextInt(list.size() - 1);
        currentposition %= list.size();
        musicplay(currentposition);
        adapter.setFlag(currentposition);
        adapter.notifyDataSetChanged();
    }

    // ???????????????????????????.mp3
    private String cut_song_name(String name) {
        if (name.length() >= 5
                && name.substring(name.length() - 4, name.length()).equals(
                ".mp3")) {
            return name.substring(0, name.length() - 4);
        }
        return name;
    }

    // ??????????????????????????????
    private void check_location() {
        if (currentposition >= listview.getFirstVisiblePosition()
                && currentposition <= listview.getLastVisiblePosition()) {
            imageview_location.setVisibility(View.INVISIBLE);
        } else {
            imageview_location.setVisibility(View.VISIBLE);
        }
    }

    // ?????????????????????????????????????????????
    private void auto_change_listview() {
        if (currentposition <= listview.getFirstVisiblePosition()) {
            listview.setSelection(currentposition);
        }
        if (currentposition >= listview.getLastVisiblePosition()) {
            // listview.smoothScrollToPosition(currentposition);
            listview.setSelection(currentposition - max_item + 2);
        }
    }

    // ??????sharedPreferences??????listview????????????
    private void saveDrawable(Drawable drawable) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        String imageBase64 = new String(Base64.encodeToString(
                baos.toByteArray(), Base64.DEFAULT));
        editor.putString("listbg", imageBase64);
        editor.commit();
    }

    // ?????????sharedPreferences???????????????
    private Drawable loadDrawable() {
        String temp = sharedPreferences.getString("listbg", "");
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(
                temp.getBytes(), Base64.DEFAULT));
        return Drawable.createFromStream(bais, "");
    }

    // ?????????????????????????????????????????????????????????
    private void change_play_image(int resID_play, int resID_pause) {
        if (imageView_play
                .getDrawable()
                .getCurrent()
                .getConstantState()
                .equals(getResources().getDrawable(resID_play)
                        .getConstantState())) {
            imageView_play.setImageResource(resID_pause);
        } else {
            imageView_play.setImageResource(resID_play);
        }
    }

    private void getAuthority(){
        //??????6.0????????????????????????
        PermissionGen.with(MainActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request();
    }

    //????????????????????????6.0????????????????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething(){
        //Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething(){
        //Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
    }
}
