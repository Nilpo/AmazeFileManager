/*
 * Copyright (C) 2014 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.filemanager.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.R;
import com.amaze.filemanager.adapters.DrawerAdapter;
import com.amaze.filemanager.database.TabHandler;
import com.amaze.filemanager.fragments.AppsList;
import com.amaze.filemanager.fragments.Main;
import com.amaze.filemanager.fragments.ProcessViewer;
import com.amaze.filemanager.fragments.TabFragment;
import com.amaze.filemanager.fragments.ZipViewer;
import com.amaze.filemanager.services.CopyService;
import com.amaze.filemanager.services.DeleteTask;
import com.amaze.filemanager.services.ExtractService;
import com.amaze.filemanager.services.ZipTask;
import com.amaze.filemanager.services.asynctasks.MoveFiles;
import com.amaze.filemanager.services.asynctasks.SearchTask;
import com.amaze.filemanager.ui.drawer.EntryItem;
import com.amaze.filemanager.ui.drawer.Item;
import com.amaze.filemanager.ui.drawer.SectionItem;
import com.amaze.filemanager.utils.FileUtil;
import com.amaze.filemanager.utils.Futils;
import com.amaze.filemanager.ui.icons.IconUtils;
import com.amaze.filemanager.utils.HFile;
import com.amaze.filemanager.utils.HistoryManager;
import com.amaze.filemanager.utils.PreferenceUtils;
import com.amaze.filemanager.utils.RootHelper;
import com.amaze.filemanager.ui.views.RoundedImageView;
import com.amaze.filemanager.ui.views.ScrimInsetsRelativeLayout;
import com.amaze.filemanager.utils.Shortcuts;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.stericson.RootTools.RootTools;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public Integer select;
    Futils utils;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    SharedPreferences Sp;
    private ActionBarDrawerToggle mDrawerToggle;
    public List<String> val;
    ArrayList<String> books;
    public ArrayList<String> Servers;
    MainActivity mainActivity = this;
    DrawerAdapter adapter;
    IconUtils util;
    public ScrimInsetsRelativeLayout mDrawerLinear;
    Shortcuts s, servers;
    public String skin, path = "", launchPath;
    public int theme;
    public ArrayList<String> COPY_PATH = null, MOVE_PATH = null;
    Context con = this;
    public FrameLayout frameLayout;
    public boolean mReturnIntent = false;
    private Intent intent;
    private static final Pattern DIR_SEPARATOR = Pattern.compile("/");
    public ArrayList<Item> list;
    public int theme1;
    public boolean rootmode, aBoolean, openzip = false;
    String zippath;
    public Spinner tabsSpinner;
    public boolean mRingtonePickerIntent = false, restart = false, colourednavigation = false;
    public Toolbar toolbar;
    public int skinStatusBar;
    FragmentTransaction pending_fragmentTransaction;
    String pending_path;
    boolean openprocesses = false;
    public int storage_count = 0;
    private GoogleApiClient mGoogleApiClient;
    private View drawerHeaderLayout;
    private View drawerHeaderView;
    private RoundedImageView drawerProfilePic;
    private DisplayImageOptions displayImageOptions;
    private int sdk;
    private TextView mGoogleName, mGoogleId;
    public String fabskin;
    private LinearLayout buttons;
    private HorizontalScrollView scroll, scroll1;
    private CountDownTimer timer;
    private IconUtils icons;
    private TabHandler tabHandler;
    int hidemode;
    public FloatingActionMenu floatingActionButton;
    public LinearLayout pathbar;
    public FrameLayout buttonBarFrame;
    private RelativeLayout drawerHeaderParent;
    int operation;
    ArrayList<String> oparrayList;
    String oppathe, oppathe1;
    // Check for user interaction for google+ api only once
    private boolean mGoogleApiKey = false;
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* A flag indicating that a PendingIntent is in progress and prevents
   * us from starting further intents.
   */
    private boolean mIntentInProgress, topfab = false, showHidden = false;
    public boolean isDrawerLocked = false;
    static final int DELETE = 0, COPY = 1, MOVE = 2, NEW_FOLDER = 3, RENAME = 4, NEW_FILE = 5, EXTRACT = 6, COMPRESS = 7;

    public HistoryManager history, hidden, grid, listManager;
    public ArrayList<String> hiddenfiles, gridfiles, listfiles;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Sp = PreferenceManager.getDefaultSharedPreferences(this);

        int th = Integer.parseInt(Sp.getString("theme", "0"));
        theme1 = th == 2 ? PreferenceUtils.hourOfDay() : th;

        fabskin = PreferenceUtils.getFabColor(Sp.getInt("fab_skin_color_position", 1));

        // setting accent theme
        if (Build.VERSION.SDK_INT >= 21) {

            switch (fabskin) {
                case "#F44336":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_red);
                    else
                        setTheme(R.style.pref_accent_dark_red);
                    break;

                case "#e91e63":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_pink);
                    else
                        setTheme(R.style.pref_accent_dark_pink);
                    break;

                case "#9c27b0":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_purple);
                    else
                        setTheme(R.style.pref_accent_dark_purple);
                    break;

                case "#673ab7":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_deep_purple);
                    else
                        setTheme(R.style.pref_accent_dark_deep_purple);
                    break;

                case "#3f51b5":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_indigo);
                    else
                        setTheme(R.style.pref_accent_dark_indigo);
                    break;

                case "#2196F3":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_blue);
                    else
                        setTheme(R.style.pref_accent_dark_blue);
                    break;

                case "#03A9F4":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_light_blue);
                    else
                        setTheme(R.style.pref_accent_dark_light_blue);
                    break;

                case "#00BCD4":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_cyan);
                    else
                        setTheme(R.style.pref_accent_dark_cyan);
                    break;

                case "#009688":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_teal);
                    else
                        setTheme(R.style.pref_accent_dark_teal);
                    break;

                case "#4CAF50":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_green);
                    else
                        setTheme(R.style.pref_accent_dark_green);
                    break;

                case "#8bc34a":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_light_green);
                    else
                        setTheme(R.style.pref_accent_dark_light_green);
                    break;

                case "#FFC107":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_amber);
                    else
                        setTheme(R.style.pref_accent_dark_amber);
                    break;

                case "#FF9800":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_orange);
                    else
                        setTheme(R.style.pref_accent_dark_orange);
                    break;

                case "#FF5722":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_deep_orange);
                    else
                        setTheme(R.style.pref_accent_dark_deep_orange);
                    break;

                case "#795548":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_brown);
                    else
                        setTheme(R.style.pref_accent_dark_brown);
                    break;

                case "#212121":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_black);
                    else
                        setTheme(R.style.pref_accent_dark_black);
                    break;

                case "#607d8b":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_blue_grey);
                    else
                        setTheme(R.style.pref_accent_dark_blue_grey);
                    break;

                case "#004d40":
                    if (theme1 == 0)
                        setTheme(R.style.pref_accent_light_super_su);
                    else
                        setTheme(R.style.pref_accent_dark_super_su);
                    break;
            }
        } else {
            if (theme1 == 1) {
                setTheme(R.style.appCompatDark);
            } else {
                setTheme(R.style.appCompatLight);
            }
        }

        setContentView(R.layout.main_toolbar);
        tabHandler = new TabHandler(this, null, null, 1);

        buttonBarFrame = (FrameLayout) findViewById(R.id.buttonbarframe);
        int fabSkinPressed = PreferenceUtils.getStatusColor(fabskin);

        boolean random = Sp.getBoolean("random_checkbox", false);
        if (random)
            skin = PreferenceUtils.random(Sp);
        else
            skin = PreferenceUtils.getSkinColor(Sp.getInt("skin_color_position", 4));

        hidemode = Sp.getInt("hidemode", 0);
        showHidden = Sp.getBoolean("showHidden", false);
        topfab = hidemode == 0 ? Sp.getBoolean("topFab", false) : false;
        floatingActionButton = !topfab ?
                (FloatingActionMenu) findViewById(R.id.menu) : (FloatingActionMenu) findViewById(R.id.menu_top);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.showMenuButton(true);
        floatingActionButton.setMenuButtonColorNormal(Color.parseColor(fabskin));
        floatingActionButton.setMenuButtonColorPressed(fabSkinPressed);

        //if (theme1 == 1) floatingActionButton.setMen
        floatingActionButton.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                View v = findViewById(R.id.fab_bg);
                if (b) revealShow(v, true);
                else revealShow(v, false);
            }
        });
        View v = findViewById(R.id.fab_bg);
        if (theme1 == 1)
            v.setBackgroundColor(Color.parseColor("#a6ffffff"));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton.close(true);
                revealShow(view, false);
            }
        });
        drawerHeaderLayout = getLayoutInflater().inflate(R.layout.drawerheader, null);
        drawerHeaderParent = (RelativeLayout) drawerHeaderLayout.findViewById(R.id.drawer_header_parent);
        drawerHeaderView = (View) drawerHeaderLayout.findViewById(R.id.drawer_header);
        drawerProfilePic = (RoundedImageView) drawerHeaderLayout.findViewById(R.id.profile_pic);
        mGoogleName = (TextView) drawerHeaderLayout.findViewById(R.id.account_header_drawer_name);
        mGoogleId = (TextView) drawerHeaderLayout.findViewById(R.id.account_header_drawer_email);
        history = new HistoryManager(this, "Table1", "Table2");
        hidden = new HistoryManager(this, "Table2", "Table2");
        grid = new HistoryManager(this, "grid", "listgridmodes");
        listManager = new HistoryManager(this, "list", "listgridmodes");
        hiddenfiles = hidden.readTable();
        gridfiles = grid.readTable();
        listfiles = listManager.readTable();
        // initialize g+ api client as per preferences
        if (Sp.getBoolean("plus_pic", false)) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();
        }
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.amaze_header)
                .showImageForEmptyUri(R.drawable.amaze_header)
                .showImageOnFail(R.drawable.amaze_header)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        if (!ImageLoader.getInstance().isInited()) {

            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        }

        utils = new Futils();
        s = new Shortcuts(this, "shortcut.xml");
        servers = new Shortcuts(this, "servers.xml");
        path = getIntent().getStringExtra("path");
        openprocesses = getIntent().getBooleanExtra("openprocesses", false);
        restart = getIntent().getBooleanExtra("restart", false);
        rootmode = Sp.getBoolean("rootmode", false);
        theme = Integer.parseInt(Sp.getString("theme", "0"));
        util = new IconUtils(Sp, this);
        icons = new IconUtils(Sp, this);


        pathbar = (LinearLayout) findViewById(R.id.pathbar);
        buttons = (LinearLayout) findViewById(R.id.buttons);
        scroll = (HorizontalScrollView) findViewById(R.id.scroll);
        scroll1 = (HorizontalScrollView) findViewById(R.id.scroll1);
        scroll.setSmoothScrollingEnabled(true);
        scroll1.setSmoothScrollingEnabled(true);
        FloatingActionButton floatingActionButton1 = (FloatingActionButton) findViewById(topfab ? R.id.menu_item_top : R.id.menu_item);
        String folder_skin = PreferenceUtils.getSkinColor(Sp.getInt("icon_skin_color_position", 4));
        int folderskin = Color.parseColor(folder_skin);
        int fabskinpressed = (PreferenceUtils.getStatusColor(folder_skin));
        floatingActionButton1.setColorNormal(folderskin);
        floatingActionButton1.setColorPressed(fabskinpressed);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(0);
                revealShow(findViewById(R.id.fab_bg), false);
                floatingActionButton.close(true);
            }
        });
        FloatingActionButton floatingActionButton2 = (FloatingActionButton) findViewById(topfab ? R.id.menu_item1_top : R.id.menu_item1);
        floatingActionButton2.setColorNormal(folderskin);
        floatingActionButton2.setColorPressed(fabskinpressed);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(1);
                revealShow(findViewById(R.id.fab_bg), false);
                floatingActionButton.close(true);
            }
        });
        FloatingActionButton floatingActionButton3 = (FloatingActionButton) findViewById(topfab ? R.id.menu_item2_top : R.id.menu_item2);
        floatingActionButton3.setColorNormal(folderskin);
        floatingActionButton3.setColorPressed(fabskinpressed);
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(2);
                revealShow(findViewById(R.id.fab_bg), false);
                floatingActionButton.close(true);
            }
        });
        if (topfab) {
            buttonBarFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) floatingActionButton.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, findViewById(R.id.lin)
                                    .getBottom() - (floatingActionButton.getMenuIconView().getHeight
                                    ()),
                            layoutParams.rightMargin,
                            layoutParams.bottomMargin);
                    floatingActionButton.setLayoutParams(layoutParams);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        buttonBarFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        buttonBarFrame.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }

            });
        }
        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        aBoolean = Sp.getBoolean("view", true);
        //ImageView overflow = ((ImageView)findViewById(R.id.action_overflow));

        //showPopup(overflow);
        tabsSpinner = (Spinner) findViewById(R.id.tab_spinner);
        //title = (TextView) findViewById(R.id.title);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                crossfadeInverse();
            }
        };

        try {
            intent = getIntent();
            if (intent.getAction().equals(Intent.ACTION_GET_CONTENT)) {

                // file picker intent
                mReturnIntent = true;
                Toast.makeText(this, utils.getString(con, R.string.pick_a_file), Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(RingtoneManager.ACTION_RINGTONE_PICKER)) {
                // ringtone picker intent
                mReturnIntent = true;
                mRingtonePickerIntent = true;
                System.out.println(intent.getData());
                Toast.makeText(this, utils.getString(con, R.string.pick_a_file), Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {

                // zip viewer intent
                Uri uri = intent.getData();
                openzip = true;
                zippath = uri.getPath();
            }
        } catch (Exception e) {

        }
        findViewById(R.id.buttonbarframe).setBackgroundColor(Color.parseColor(skin));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(skin)));

        skinStatusBar = (PreferenceUtils.getStatusColor(skin));

        mDrawerLinear = (ScrimInsetsRelativeLayout) findViewById(R.id.left_drawer);
        if (theme1 == 1) mDrawerLinear.setBackgroundColor(Color.parseColor("#303030"));
        else mDrawerLinear.setBackgroundColor(Color.WHITE);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(Color.parseColor(skin));
        mDrawerList = (ListView) findViewById(R.id.menu_drawer);
        if (((ViewGroup.MarginLayoutParams) findViewById(R.id.main_frame).getLayoutParams()).leftMargin == (int) getResources().getDimension(R.dimen.drawer_width)) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, mDrawerLinear);
            mDrawerLayout.setScrimColor(Color.TRANSPARENT);
            isDrawerLocked = true;
        }
        // status bar0
        sdk = Build.VERSION.SDK_INT;

        if (sdk == 20 || sdk == 19) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(Color.parseColor(skin));
            FrameLayout.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) findViewById(R.id.drawer_layout).getLayoutParams();
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            if (!isDrawerLocked) p.setMargins(0, config.getStatusBarHeight(), 0, 0);
        } else if (Build.VERSION.SDK_INT >= 21) {
            colourednavigation = Sp.getBoolean("colorednavigation", true);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (isDrawerLocked) {
                window.setStatusBarColor((skinStatusBar));
            } else window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (colourednavigation)
                window.setNavigationBarColor(skinStatusBar);

        }

        View settingsbutton = findViewById(R.id.settingsbutton);
        if (theme1 == 1) {
            settingsbutton.setBackgroundResource(R.drawable.safr_ripple_black);
            ((ImageView) settingsbutton.findViewById(R.id.settingicon)).setImageResource(R.drawable.ic_settings_white_48dp);
            ((TextView) settingsbutton.findViewById(R.id.settingtext)).setTextColor(getResources().getColor(android.R.color.white));
        }
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, Preferences.class);
                finish();
                final int enter_anim = android.R.anim.fade_in;
                final int exit_anim = android.R.anim.fade_out;
                Activity s = MainActivity.this;
                s.overridePendingTransition(exit_anim, enter_anim);
                s.finish();
                s.overridePendingTransition(enter_anim, exit_anim);
                s.startActivity(in);
            }

        });
        View appbutton = findViewById(R.id.appbutton);
        if (theme1 == 1) {
            appbutton.setBackgroundResource(R.drawable.safr_ripple_black);
            ((ImageView) appbutton.findViewById(R.id.appicon)).setImageResource(R.drawable.ic_doc_apk_white);
            ((TextView) appbutton.findViewById(R.id.apptext)).setTextColor(getResources().getColor(android.R.color.white));
        }
        appbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.content_frame, new AppsList());
                findViewById(R.id.lin).animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                pending_fragmentTransaction = transaction2;
                if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
                else onDrawerClosed();
                select = list.size() + 1;
                adapter.toggleChecked(false);
            }
        });
        ImageView divider = (ImageView) findViewById(R.id.divider1);
        if (theme1 == 0)
            divider.setImageResource(R.color.divider);
        else
            divider.setImageResource(R.color.divider_dark);

        mDrawerList.addHeaderView(drawerHeaderLayout);
        updateDrawer();
        drawerHeaderView.setBackgroundResource(R.drawable.amaze_header);
        drawerHeaderParent.setBackgroundColor(Color.parseColor(skin));
        if (savedInstanceState == null) {

            if (openprocesses) {
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new ProcessViewer());
                //   transaction.addToBackStack(null);
                select = 102;
                openprocesses = false;
                //title.setText(utils.getString(con, R.string.process_viewer));
                //Commit the transaction
                transaction.commit();
                supportInvalidateOptionsMenu();
            } else {
                goToMain(path);
            }
        } else {
            oppathe = savedInstanceState.getString("oppathe");
            oppathe1 = savedInstanceState.getString("oppathe1");
            ArrayList<String> k = savedInstanceState.getStringArrayList("oparrayList");
            if (k != null) {
                oparrayList = (k);
                operation = savedInstanceState.getInt("operation");
            }
            select = savedInstanceState.getInt("selectitem", 0);
            adapter.toggleChecked(select);
        }
        if (theme1 == 1) {
            mDrawerList.setBackgroundColor(getResources().getColor(R.color.holo_dark_background));
        }
        mDrawerList.setDivider(null);
        if (select == 0) {

            //title.setVisibility(View.GONE);
            tabsSpinner.setVisibility(View.VISIBLE);
        } else {

            //title.setVisibility(View.VISIBLE);
            tabsSpinner.setVisibility(View.GONE);
        }
        if (!isDrawerLocked) {
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer_l,  /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
                public void onDrawerClosed(View view) {
                    mainActivity.onDrawerClosed();
                }

                public void onDrawerOpened(View drawerView) {
                    //title.setText("Amaze File Manager");
                    // creates call to onPrepareOptionsMenu()
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_l);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerToggle.syncState();
        }/*((ImageButton) findViewById(R.id.drawer_buttton)).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(mDrawerLinear)) {
                    mDrawerLayout.closeDrawer(mDrawerLinear);
                } else mDrawerLayout.openDrawer(mDrawerLinear);
            }
        });*/
        if (mDrawerToggle != null) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer_l);
        }
        //recents header color implementation
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("Amaze", ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap(), Color.parseColor(skin));
            ((Activity) this).setTaskDescription(taskDescription);
        }
    }

    /**
     * Returns all available SD-Cards in the system (include emulated)
     * <p/>
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standard way to get it.
     * TODO: Test on future Android versions 4.4+
     *
     * @return paths to all available SD-Cards in the system (include emulated)
     */


    public List<String> getStorageDirectories() {
        // Final set of paths
        final ArrayList<String> rv = new ArrayList<String>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        rootmode = Sp.getBoolean("rootmode", false);
        if (rootmode)
            rv.add("/");
        File usb = getUsbDrive();
        if (usb != null && !rv.contains(usb.getPath())) rv.add(usb.getPath());

        return rv;
    }


    @Override
    public void onBackPressed() {
        if (!isDrawerLocked) {
            if (mDrawerLayout.isDrawerOpen(mDrawerLinear)) {
                mDrawerLayout.closeDrawer(mDrawerLinear);
            } else {
                onbackpressed();
            }
        } else onbackpressed();
    }

    void onbackpressed() {
        try {

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            String name = fragment.getClass().getName();
            if (name.contains("TabFragment")) {
                if (floatingActionButton.isOpened()) {
                    floatingActionButton.close(true);
                    revealShow(findViewById(R.id.fab_bg), false);
                } else {
                    TabFragment tabFragment = ((TabFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame));
                    Fragment fragment1 = tabFragment.getTab();
                    Main main = (Main) fragment1;
                    main.goBack();
                }
            } else if (name.contains("ZipViewer")) {
                ZipViewer zipViewer = (ZipViewer) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (zipViewer.mActionMode == null) {
                    if (zipViewer.cangoBack()) {

                        zipViewer.goBack();
                    } else if (openzip) {
                        openzip = false;
                        finish();
                    } else {

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_out_bottom, R.anim.slide_out_bottom);
                        fragmentTransaction.remove(zipViewer);
                        fragmentTransaction.commit();
                        supportInvalidateOptionsMenu();
                        floatingActionButton.showMenuButton(true);

                    }
                } else {
                    zipViewer.mActionMode.finish();
                }
            } else if (name.contains("Process")) {
                finish();
            } else
                goToMain("");
        } catch (ClassCastException e) {
            goToMain("");
        }
    }

    public void invalidatePasteButton(MenuItem paste) {
        if (MOVE_PATH != null || COPY_PATH != null) {
            paste.setVisible(true);
        } else {
            paste.setVisible(false);
        }
    }

    public void exit() {
        if (backPressedToExitOnce) {
            finish();
            if (rootmode) {
                try {
                    RootTools.closeAllShells();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.backPressedToExitOnce = true;
            showToast(utils.getString(this, R.string.pressagain));
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }
    }

    public void updateDrawer() {
        list = new ArrayList<>();
        val = getStorageDirectories();
        books = new ArrayList<>();
        Servers = new ArrayList<String>();
        storage_count = 0;
        for (String file : val) {
            File f = new File(file);
            String name;
            Drawable icon1 = ContextCompat.getDrawable(this, R.drawable.ic_sd_storage_white_56dp);
            if ("/storage/emulated/legacy".equals(file) || "/storage/emulated/0".equals(file)) {
                name = getResources().getString(R.string.storage);

            } else if ("/storage/sdcard1".equals(file)) {
                name = getResources().getString(R.string.extstorage);
            } else if ("/".equals(file)) {
                name = getResources().getString(R.string.rootdirectory);
                icon1 = ContextCompat.getDrawable(this, R.drawable.ic_drawer_root_white);
            } else name = f.getName();
            if (!f.isDirectory() || f.canExecute()) {
                storage_count++;
                list.add(new EntryItem(name, file, icon1));
            }
        }
        list.add(new SectionItem());
        File f = new File(getFilesDir() + "/servers.xml");
        if (f.exists()) {
            try {
                for (String s : servers.readS()) {
                    Servers.add(s);
                    list.add(new EntryItem(parseSmbPath(s), s, ContextCompat.getDrawable(this, R.drawable.ic_settings_remote_white_48dp)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Servers.size() > 0)
                list.add(new SectionItem());
        }
        try {
            File f1 = new File(getFilesDir() + "/shortcut.xml");
            if (!f1.exists()) s.makeS(true);
            for (String file : s.readS()) {
                String name = new File(file).getName();
                books.add(file);
                list.add(new EntryItem(name, file, ContextCompat.getDrawable(this, R.drawable
                        .folder_fab)));
            }
            if (books.size() > 0)
                list.add(new SectionItem());
        } catch (Exception e) {

        }
        list.add(new EntryItem("Images", "0", ContextCompat.getDrawable(this, R.drawable.ic_doc_image)));
        list.add(new EntryItem("Videos", "1", ContextCompat.getDrawable(this, R.drawable.ic_doc_video_am)));
        list.add(new EntryItem("Audio", "2", ContextCompat.getDrawable(this, R.drawable.ic_doc_audio_am)));
        list.add(new EntryItem("Documents", "3", ContextCompat.getDrawable(this, R.drawable
                .ic_doc_doc_am)));
        list.add(new EntryItem("Apks", "4", ContextCompat.getDrawable(this, R.drawable.ic_doc_apk_grid)));
        adapter = new DrawerAdapter(this, list, MainActivity.this, Sp);
        mDrawerList.setAdapter(adapter);
    }

    public void updateDrawer(String path) {
        new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                String path = strings[0];
                int k = 0, i = 0;
                for (Item item : list) {
                    if (!item.isSection()) {
                        if (((EntryItem) item).getPath().equals(path))
                            k = i;
                    }
                    i++;
                }
                return k;
            }

            @Override
            public void onPostExecute(Integer integers) {
                if (adapter != null)
                    adapter.toggleChecked(integers);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);

    }

    public void goToMain(String path) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //title.setText(R.string.app_name);
        TabFragment tabFragment = new TabFragment();
        if (path != null && path.length() > 0) {
            Bundle b = new Bundle();
            b.putString("path", path);
            tabFragment.setArguments(b);
        }
        transaction.replace(R.id.content_frame, tabFragment);
        // Commit the transaction
        select = 0;
        transaction.addToBackStack("tabt" + 1);
        transaction.commit();
        toolbar.setTitle(null);
        tabsSpinner.setVisibility(View.VISIBLE);
        floatingActionButton.showMenuButton(true);
        if (openzip && zippath != null) {
            if (zippath.endsWith(".zip") || zippath.endsWith(".apk")) openZip(zippath);
            else {
                openRar(zippath);
            }
            zippath = null;
        }
    }

    public void selectItem(final int i, boolean removeBookmark) {
        if (!list.get(i).isSection())
            if ((select == null || select >= list.size()) && !removeBookmark) {

                TabFragment tabFragment = new TabFragment();
                Bundle a = new Bundle();
                a.putString("path", ((EntryItem) list.get(i)).getPath());
                tabFragment.setArguments(a);

                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, tabFragment);

                transaction.addToBackStack("tabt1" + 1);
                pending_fragmentTransaction = transaction;
                select = i;
                adapter.toggleChecked(select);
                if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
                else onDrawerClosed();
                tabsSpinner.setVisibility(View.VISIBLE);
                floatingActionButton.showMenuButton(true);


            } else if (removeBookmark) {
                try {
                    String path = ((EntryItem) list.get(i)).getPath();
                    s.removeS(path, MainActivity.this);
                    books.remove(path);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshDrawer();
                select = 0;
            } else {
                pending_path = ((EntryItem) list.get(i)).getPath();
                select = i;
                adapter.toggleChecked(select);
                if (!isDrawerLocked) mDrawerLayout.closeDrawer(mDrawerLinear);
                else onDrawerClosed();

            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_extra, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem s = menu.findItem(R.id.view);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem paste = menu.findItem(R.id.paste);
        String f = null;
        Fragment fragment;
        try {
            fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            f = fragment.getClass().getName();
        } catch (Exception e1) {
            return true;
        }
        if (f.contains("TabFragment")) {
            if (aBoolean) {
                s.setTitle(getResources().getString(R.string.gridview));
            } else {
                s.setTitle(getResources().getString(R.string.listview));
            }
            try {
                TabFragment tabFragment = (TabFragment) fragment;
                Main ma = ((Main) tabFragment.getTab());
                if (ma.IS_LIST) s.setTitle(R.string.gridview);
                else s.setTitle(R.string.listview);
                updatePath(ma.CURRENT_PATH, ma.results, ma.openMode, ma.folder_count, ma.file_count);
            } catch (Exception e) {
            }
            tabsSpinner.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("");

            initiatebbar();
            if (Build.VERSION.SDK_INT >= 21) toolbar.setElevation(0);
            invalidatePasteButton(paste);
            search.setVisible(true);
            menu.findItem(R.id.search).setVisible(true);
            menu.findItem(R.id.home).setVisible(true);
            menu.findItem(R.id.history).setVisible(true);
            menu.findItem(R.id.item10).setVisible(true);
            if (showHidden) menu.findItem(R.id.hiddenitems).setVisible(true);
            menu.findItem(R.id.view).setVisible(true);
            menu.findItem(R.id.extract).setVisible(false);
            invalidatePasteButton(menu.findItem(R.id.paste));
            findViewById(R.id.buttonbarframe).setVisibility(View.VISIBLE);
        } else if (f.contains("AppsList") || f.contains("ProcessViewer")) {
            tabsSpinner.setVisibility(View.GONE);
            findViewById(R.id.buttonbarframe).setVisibility(View.GONE);
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.home).setVisible(false);
            menu.findItem(R.id.history).setVisible(false);
            menu.findItem(R.id.extract).setVisible(false);
            if (f.contains("ProcessViewer")) menu.findItem(R.id.item10).setVisible(false);
            else {
                menu.findItem(R.id.dsort).setVisible(false);
                menu.findItem(R.id.sortby).setVisible(false);
            }
            menu.findItem(R.id.hiddenitems).setVisible(false);
            menu.findItem(R.id.view).setVisible(false);
            menu.findItem(R.id.paste).setVisible(false);
        } else if (f.contains("ZipViewer")) {
            tabsSpinner.setVisibility(View.GONE);
            TextView textView = (TextView) mainActivity.pathbar.findViewById(R.id.fullpath);
            pathbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.home).setVisible(false);
            menu.findItem(R.id.history).setVisible(false);
            menu.findItem(R.id.item10).setVisible(false);
            menu.findItem(R.id.hiddenitems).setVisible(false);
            menu.findItem(R.id.view).setVisible(false);
            menu.findItem(R.id.paste).setVisible(false);
            menu.findItem(R.id.extract).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }

    private void killToast() {
        if (this.toast != null) {
            this.toast.cancel();
        }
    }

    public void back() {
        super.onBackPressed();
    }


    //// called when the user exits the action mode
//	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        Main ma = null;
        try {
            ma = (Main) getFragment().getTab();
        } catch (ClassCastException e) {
        }
        switch (item.getItemId()) {
            case R.id.home:
                ma.home();
                break;
            case R.id.history:
                utils.showHistoryDialog(ma);
                break;
            case R.id.item3:
                if (rootmode) {
                    try {
                        RootTools.closeAllShells();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                finish();
                break;
            case R.id.item10:
                Fragment fragment = getDFragment();
                if (fragment.getClass().getName().contains("AppsList"))
                    utils.showSortDialog((AppsList) fragment);

                break;
            case R.id.sortby:
                utils.showSortDialog(ma);
                break;
            case R.id.dsort:
                String[] sort = getResources().getStringArray(R.array.directorysortmode);
                MaterialDialog.Builder a = new MaterialDialog.Builder(mainActivity);
                if (theme == 1) a.theme(Theme.DARK);
                a.title(R.string.directorysort);
                int current = Integer.parseInt(Sp.getString("dirontop", "0"));
                a.items(sort).itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Sp.edit().putString("dirontop", "" + which).commit();
                        dialog.dismiss();
                        return true;
                    }
                });
                a.build().show();
                break;
            case R.id.hiddenitems:
                utils.showHiddenDialog(ma);
                break;
            case R.id.view:
                if (ma.IS_LIST) {
                    grid.addPath(ma.CURRENT_PATH);
                    gridfiles.add(ma.CURRENT_PATH);
                    listManager.removePath(ma.CURRENT_PATH);
                } else {
                    if (gridfiles.contains(ma.CURRENT_PATH)) {
                        gridfiles.remove(ma.CURRENT_PATH);
                        grid.removePath(ma.CURRENT_PATH);
                    }
                    listManager.addPath(ma.CURRENT_PATH);
                    listfiles.add(ma.CURRENT_PATH);

                }
                ma.switchView();
                break;
            case R.id.search:
                search();
                break;
            case R.id.paste:
                String path = ma.CURRENT_PATH;
                ArrayList<String> arrayList = new ArrayList<String>();
                if (COPY_PATH != null) {
                    arrayList = COPY_PATH;
                    new CheckForFiles(ma, path, false).executeOnExecutor(AsyncTask
                            .THREAD_POOL_EXECUTOR, arrayList);
                } else if (MOVE_PATH != null) {
                    arrayList = MOVE_PATH;
                    new CheckForFiles(ma, path, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            arrayList);
                }
                COPY_PATH = null;
                MOVE_PATH = null;

                invalidatePasteButton(item);
                break;
            case R.id.extract:
                Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (fragment1.getClass().getName().contains("ZipViewer"))
                    extractFile(((ZipViewer) fragment1).f);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) mDrawerToggle.syncState();
    }

    public void add(int pos) {
        final Main ma = (Main) ((TabFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame)).getTab();
        switch (pos) {

            case 0:
                final String path = ma.CURRENT_PATH;
                final MaterialDialog.Builder ba1 = new MaterialDialog.Builder(this);
                ba1.title(R.string.newfolder);
                ba1.input(utils.getString(this, R.string.entername), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                    }
                });
                if (theme1 == 1) ba1.theme(Theme.DARK);
                ba1.positiveText(R.string.create);
                ba1.negativeText(R.string.cancel);
                ba1.positiveColor(Color.parseColor(fabskin));
                ba1.negativeColor(Color.parseColor(fabskin));
                ba1.widgetColor(Color.parseColor(fabskin));
                ba1.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        String a = materialDialog.getInputEditText().getText().toString();
                        mkDir(path + "/" + a, ma);
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {

                    }
                });
                ba1.build().show();
                break;
            case 1:
                final String path1 = ma.CURRENT_PATH;
                final MaterialDialog.Builder ba2 = new MaterialDialog.Builder(this);
                ba2.title((R.string.newfile));
                View v1 = getLayoutInflater().inflate(R.layout.dialog_rename, null);
                final EditText edir1 = (EditText) v1.findViewById(R.id.newname);
                utils.setTint(edir1, Color.parseColor(fabskin));
                edir1.setHint(utils.getString(this, R.string.entername));
                ba2.customView(v1, true);
                edir1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        edir1.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(edir1, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    }
                });
                if (theme1 == 1) ba2.theme(Theme.DARK);
                ba2.negativeText(R.string.cancel);
                ba2.positiveText(R.string.create);
                ba2.positiveColor(Color.parseColor(fabskin));
                ba2.negativeColor(Color.parseColor(fabskin));
                ba2.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        String a = edir1.getText().toString();

                        mkFile(path1 + "/" + a, ma);
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {

                    }
                });
                ba2.build().show();
                break;
            case 2:
                createSmbDialog("", false, ma);
                break;
        }
    }


    public void search() {
        final Main ma = (Main) ((TabFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame)).getTab();
        final String fpath = ma.CURRENT_PATH;
        final MaterialDialog.Builder a = new MaterialDialog.Builder(this);
        a.title(R.string.search);
        a.input(utils.getString(this, R.string.enterfile), "", true, new MaterialDialog
                .InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
            }
        });
        if (theme1 == 1) a.theme(Theme.DARK);
        a.negativeText(R.string.cancel);
        a.positiveText(R.string.search);
        a.widgetColor(Color.parseColor(fabskin));
        a.positiveColor(Color.parseColor(fabskin));
        a.negativeColor(Color.parseColor(fabskin));
        a.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
                materialDialog.dismiss();
                String a = materialDialog.getInputEditText().getText().toString();
                if (a.length() == 0) {
                    return;
                }
                SearchTask task = new SearchTask(ma.searchHelper, ma, a);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fpath);
                ma.searchTask = task;
            }

            @Override
            public void onNegative(MaterialDialog materialDialog) {

            }
        });
        MaterialDialog b = a.build();
        if (fpath.startsWith("smb:")) b.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        b.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectitem", select);

        if (oppathe != null) {
            outState.putString("oppathe", oppathe);
            outState.putString("oppathe1", oppathe1);

            outState.putStringArrayList("oparraylist", (oparrayList));
            outState.putInt("operation", operation);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNotificationReceiver);
        killToast();
    }

    MaterialDialog materialDialog;

    @Override
    public void onResume() {
        super.onResume();
        if (materialDialog != null && !materialDialog.isShowing()) {
            materialDialog.show();
            materialDialog = null;
        }
        IntentFilter newFilter = new IntentFilter();
        newFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        newFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        newFilter.addDataScheme(ContentResolver.SCHEME_FILE);
        registerReceiver(mNotificationReceiver, newFilter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            /*ImageView ib = (ImageView) findViewById(R.id.action_overflow);
            if (ib.getVisibility() == View.VISIBLE) {
                ib.performClick();
            }*/
            // perform your desired action here

            // return 'true' to prevent further propagation of the key event
            return true;
        }

        // let the system handle all other key events
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sp.edit().putBoolean("remember", true).apply();
        if (grid != null)
            grid.end();
        if (listManager != null)
            listManager.end();
        if (history != null)
            history.end();
        if (hidden != null)
            hidden.end();
    }

    class CheckForFiles extends AsyncTask<ArrayList<String>, String, ArrayList<String>> {
        Main ma;
        String path;
        Boolean move;
        ArrayList<String> ab, a, b, lol;
        int counter = 0;

        public CheckForFiles(Main main, String path, Boolean move) {
            this.ma = main;
            this.path = path;
            this.move = move;
            a = new ArrayList<String>();
            b = new ArrayList<String>();
            lol = new ArrayList<String>();
        }

        @Override
        public void onProgressUpdate(String... message) {
            Toast.makeText(con, message[0], Toast.LENGTH_LONG).show();
        }

        @Override
        // Actual download method, run in the task thread
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {

            ab = params[0];
            long totalBytes = 0;

            for (int i = 0; i < params[0].size(); i++) {

                HFile f1 = new HFile(params[0].get(i));

                if (f1.isDirectory()) {

                    totalBytes = totalBytes + f1.folderSize();
                } else {

                    totalBytes = totalBytes + f1.length();
                }
            }
            HFile f = new HFile(path);
            if (f.getUsableSpace() > totalBytes) {

                for (String k1[] : f.listFiles(rootmode)) {
                    HFile k = new HFile(k1[0]);
                    for (String j : ab) {

                        if (k.getName().equals(new HFile(j).getName())) {

                            a.add(j);
                        }
                    }
                }
            } else publishProgress(utils.getString(con, R.string.in_safe));

            return a;
        }

        public void showDialog() {

            if (counter == a.size() || a.size() == 0) {

                if (ab != null && ab.size() != 0) {
                    int mode = checkFolder(new File(path), mainActivity);
                    if (mode == 2) {
                        oparrayList = (ab);
                        operation = move ? MOVE : COPY;
                        oppathe = path;

                    } else if (mode == 1 || mode == 0) {

                        if (!move) {

                            Intent intent = new Intent(con, CopyService.class);
                            intent.putExtra("FILE_PATHS", ab);
                            intent.putExtra("COPY_DIRECTORY", path);
                            startService(intent);
                        } else {

                            new MoveFiles(utils.toFileArray(ab), ma, ma.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
                        }
                    }
                } else {

                    Toast.makeText(MainActivity.this, utils.getString(con, R.string.no_file_overwrite), Toast.LENGTH_SHORT).show();
                }
            } else {

                final MaterialDialog.Builder x = new MaterialDialog.Builder(MainActivity.this);
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.copy_dialog, null);
                x.customView(view, true);
                // textView
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(utils.getString(con, R.string.fileexist) + "\n" + new File(a.get(counter)).getName());
                // checkBox
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                utils.setTint(checkBox, Color.parseColor(fabskin));
                if (theme1 == 1) x.theme(Theme.DARK);
                x.title(utils.getString(con, R.string.paste));
                x.positiveText(R.string.skip);
                x.negativeText(R.string.overwrite);
                x.neutralText(R.string.cancel);
                x.positiveColor(Color.parseColor(fabskin));
                x.negativeColor(Color.parseColor(fabskin));
                x.neutralColor(Color.parseColor(fabskin));
                x.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {

                        if (counter < a.size()) {

                            if (!checkBox.isChecked()) {

                                ab.remove(a.get(counter));
                                counter++;

                            } else {
                                for (int j = counter; j < a.size(); j++) {

                                    ab.remove(a.get(j));
                                }
                                counter = a.size();
                            }
                            showDialog();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {

                        if (counter < a.size()) {

                            if (!checkBox.isChecked()) {

                                counter++;
                            } else {

                                counter = a.size();
                            }
                            showDialog();
                        }

                    }
                });
                final MaterialDialog y = x.build();
                y.show();
                if (new File(ab.get(0)).getParent().equals(path)) {
                    View negative = y.getActionButton(DialogAction.NEGATIVE);
                    negative.setEnabled(false);
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            showDialog();
        }
    }

    public void updatepaths(int pos) {
        try {
            getFragment().updatepaths(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openZip(String path) {
        findViewById(R.id.lin).animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_in_bottom);
        Fragment zipFragment = new ZipViewer();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        zipFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.content_frame, zipFragment);
        fragmentTransaction.commit();
    }

    public void openRar(String path) {
        openZip(path);
    }

    public TabFragment getFragment() {
        TabFragment tabFragment = (TabFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        return tabFragment;
    }

    public Fragment getDFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    public void setPagingEnabled(boolean b) {
        getFragment().mViewPager.setPagingEnabled(b);
    }

    public File getUsbDrive() {
        File parent;
        parent = new File("/storage");

        try {
            for (File f : parent.listFiles()) {
                if (f.exists() && f.getName().toLowerCase().contains("usb") && f.canExecute()) {
                    return f;
                }
            }
        } catch (Exception e) {
        }
        parent = new File("/mnt/sdcard/usbStorage");
        if (parent.exists() && parent.canExecute())
            return (parent);
        parent = new File("/mnt/sdcard/usb_storage");
        if (parent.exists() && parent.canExecute())
            return parent;

        return null;
    }

    private final BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    Toast.makeText(con, "Media Mounted", Toast.LENGTH_SHORT).show();
                    String a = intent.getData().getPath();
                    if (a != null && a.trim().length() != 0 && new File(a).exists() && new File(a).canExecute()) {
                        list.add(new EntryItem(new File(a).getName(), a, ContextCompat
                                .getDrawable(mainActivity, R.drawable.ic_sd_storage_white_56dp)));
                        adapter = new DrawerAdapter(con, list, MainActivity.this, Sp);
                        mDrawerList.setAdapter(adapter);
                    } else {
                        refreshDrawer();
                    }
                } else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {

                    refreshDrawer();
                }
            }
        }
    };

    public void refreshDrawer() {
        val = getStorageDirectories();
        list = new ArrayList<>();
        storage_count = 0;
        for (String file : val) {
            File f = new File(file);
            String name;
            Drawable icon1 = ContextCompat.getDrawable(this, R.drawable.ic_sd_storage_white_56dp);
            if ("/storage/emulated/legacy".equals(file) || "/storage/emulated/0".equals(file)) {
                name = getResources().getString(R.string.storage);
            } else if ("/storage/sdcard1".equals(file)) {
                name = getResources().getString(R.string.extstorage);
            } else if ("/".equals(file)) {
                name = getResources().getString(R.string.rootdirectory);
                icon1 = ContextCompat.getDrawable(this, R.drawable.ic_drawer_root_white);
            } else name = f.getName();
            if (!f.isDirectory() || f.canExecute()) {
                storage_count++;
                list.add(new EntryItem(name, file, icon1));
            }
        }
        list.add(new SectionItem());
        if (Servers != null && Servers.size() > 0) {
            for (String file : Servers) {
                String name = parseSmbPath(file);
                list.add(new EntryItem(name, file, ContextCompat.getDrawable(this, R.drawable.ic_settings_remote_white_48dp)));
            }

            list.add(new SectionItem());
        }
        if (books != null && books.size() > 0) {

            for (String file : books) {
                String name = new File(file).getName();
                list.add(new EntryItem(name, file, ContextCompat.getDrawable(this, R.drawable
                        .folder_fab)));
            }
            list.add(new SectionItem());
        }
        list.add(new EntryItem("Images", "0", ContextCompat.getDrawable(this, R.drawable.ic_doc_image)));
        list.add(new EntryItem("Videos", "1", ContextCompat.getDrawable(this, R.drawable.ic_doc_video_am)));
        list.add(new EntryItem("Audio", "2", ContextCompat.getDrawable(this, R.drawable.ic_doc_audio_am)));
        list.add(new EntryItem("Documents", "3", ContextCompat.getDrawable(this, R.drawable
                .ic_doc_doc_am)));
        list.add(new EntryItem("Apks", "4", ContextCompat.getDrawable(this, R.drawable.ic_doc_apk_grid)));


        adapter = new DrawerAdapter(con, list, MainActivity.this, Sp);
        mDrawerList.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // check if user enabled g+ api from preferences
        if (mGoogleApiClient != null) {

            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Person.Image personImage;
            Person.Cover.CoverPhoto personCover;

            try {

                personImage = currentPerson.getImage();
                personCover = currentPerson.getCover().getCoverPhoto();
            } catch (Exception e) {

                personCover = null;
                personImage = null;
            }

            if (personCover != null && personImage != null) {

                String imgUrl = personImage.getUrl();

                // getting full size image
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(imgUrl);
                stringBuilder.delete(imgUrl.length() - 6, imgUrl.length());
                Log.d("G+", stringBuilder.toString());
                mGoogleName.setText(currentPerson.getDisplayName());
                mGoogleId.setText(accountName);
                // setting cover pic
                ImageLoader.getInstance().loadImage(personCover.getUrl(), displayImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        drawerHeaderParent.setBackgroundColor(Color.parseColor("#ffffff"));
                        drawerHeaderView.setBackground(new BitmapDrawable(loadedImage));
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        drawerHeaderView.setBackgroundResource(R.drawable.amaze_header);
                        drawerHeaderParent.setBackgroundColor(Color.parseColor(skin));
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        drawerHeaderView.setBackgroundResource(R.drawable.amaze_header);
                        drawerHeaderParent.setBackgroundColor(Color.parseColor(skin));
                    }
                });

                // setting profile pic
                ImageLoader.getInstance().loadImage(stringBuilder.toString(), displayImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        super.onLoadingComplete(imageUri, view, loadedImage);

                        drawerProfilePic.setImageBitmap(loadedImage);
                        drawerProfilePic.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                    }
                });
            } else {
                Toast.makeText(this, getResources().getText(R.string.no_cover_photo), Toast.LENGTH_SHORT).show();
                drawerHeaderView.setBackgroundResource(R.drawable.amaze_header);
                drawerHeaderParent.setBackgroundColor(Color.parseColor(skin));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d("G+", "Connection suspended");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mGoogleApiClient != null) {

                    mGoogleApiClient.connect();
                }
            }
        }).run();
    }


    public void onConnectionFailed(final ConnectionResult result) {
        Log.d("G+", "Connection failed");
        if (!mIntentInProgress && result.hasResolution()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mIntentInProgress = true;
                        startIntentSenderForResult(result.getResolution().getIntentSender(),
                                RC_SIGN_IN, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        // The intent was canceled before it was sent.  Return to the default
                        // state and attempt to connect to get an updated ConnectionResult.
                        mIntentInProgress = false;
                        if (mGoogleApiClient != null) {

                            mGoogleApiClient.connect();
                        }
                    }
                }
            }).run();
        }
    }
    public void guideDialogForLEXA(String path) {
        final MaterialDialog.Builder x = new MaterialDialog.Builder(MainActivity.this);
        if (theme1 == 1) x.theme(Theme.DARK);
        x.title(R.string.needsaccess);
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.lexadrawer, null);
        x.customView(view, true);
        // textView
        TextView textView = (TextView) view.findViewById(R.id.description);
        textView.setText(utils.getString(con, R.string.needsaccesssummary) + path + utils.getString(con, R.string.needsaccesssummary1));
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.sd_operate_step);
        x.positiveText(R.string.open);
        x.negativeText(R.string.cancel);
        x.positiveColor(Color.parseColor(fabskin));
        x.negativeColor(Color.parseColor(fabskin));
        x.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
                triggerStorageAccessFramework();
            }

            @Override
            public void onNegative(MaterialDialog materialDialog) {
                Toast.makeText(mainActivity, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
        final MaterialDialog y = x.build();
        y.show();
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN && !mGoogleApiKey && mGoogleApiClient != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mIntentInProgress = false;
                    mGoogleApiKey = true;
                    // !mGoogleApiClient.isConnecting
                    if (mGoogleApiClient.isConnecting()) {
                        mGoogleApiClient.connect();
                    } else
                        mGoogleApiClient.disconnect();

                }
            }).run();
        } else if (requestCode == 3) {
            String p = Sp.getString("URI", null);
            Uri oldUri = null;
            if (p != null) oldUri = Uri.parse(p);
            Uri treeUri = null;
            if (responseCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                treeUri = intent.getData();
                // Persist URI - this is required for verification of writability.
                if (treeUri != null) Sp.edit().putString("URI", treeUri.toString()).commit();
            }

            // If not confirmed SAF, or if still not writable, then revert settings.
            if (responseCode != Activity.RESULT_OK) {
               /* DialogUtil.displayError(getActivity(), R.string.message_dialog_cannot_write_to_folder_saf, false,
                        currentFolder);||!FileUtil.isWritableNormalOrSaf(currentFolder)
*/
                if (treeUri != null) Sp.edit().putString("URI", oldUri.toString()).commit();
                return;
            }

            // After confirmation, update stored value of folder.
            // Persist access permissions.
            final int takeFlags = intent.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            switch (operation) {
                case DELETE://deletion
                    new DeleteTask(null, mainActivity).execute((oparrayList));
                    break;
                case COPY://copying
                    Intent intent1 = new Intent(con, CopyService.class);
                    intent1.putExtra("FILE_PATHS", (oparrayList));
                    intent1.putExtra("COPY_DIRECTORY", oppathe);
                    startService(intent1);
                    break;
                case MOVE://moving
                    new MoveFiles(utils.toFileArray(oparrayList), ((Main) getFragment().getTab()), ((Main) getFragment().getTab()).getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
                    break;
                case NEW_FOLDER://mkdir
                    Main ma1 = ((Main) getFragment().getTab());
                    mkDir((oppathe), ma1);
                    break;
                case RENAME:
                    rename((oppathe), (oppathe1));
                    Main ma2 = ((Main) getFragment().getTab());
                    ma2.updateList();
                    break;
                case NEW_FILE:
                    Main ma3 = ((Main) getFragment().getTab());
                    mkFile((oppathe), ma3);

                    break;
                case EXTRACT:
                    extractFile(new File(oppathe));
                    break;
                case COMPRESS:
                    compressFiles(new File(oppathe), oparrayList);
            }
        }
    }

    public void rename(String f, String f1) {
        if (f.startsWith("smb:/")) {
            try {
                SmbFile smbFile = new SmbFile(f);

                smbFile.renameTo(new SmbFile(f1));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SmbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("loadlist");
            sendBroadcast(intent);
            return;
        }
        File file = new File(f);
        File file1 = new File(f1);
        int mode = checkFolder(file.getParentFile(), this);
        if (mode == 2) {
            oppathe = file.getPath();
            oppathe1 = file1.getPath();
            operation = RENAME;
        } else if (mode == 1) {
            boolean b = FileUtil.renameFolder(file, file1, mainActivity);
            if (b) {
                Toast.makeText(mainActivity,
                        utils.getString(mainActivity, R.string.renamed),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mainActivity,
                        utils.getString(mainActivity, R.string.renameerror),
                        Toast.LENGTH_LONG).show();

            }
        } else if (mode == 0) utils.rename(file, file1.getName(), rootmode);

        Intent intent = new Intent("loadlist");
        sendBroadcast(intent);
    }

    private int checkFolder(final File folder, Context context) {
        boolean lol=Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP,ext=FileUtil.isOnExtSdCard(folder, context);
        if (lol && ext) {
            if (!folder.exists() || !folder.isDirectory()) {
                return 0;
            }

            // On Android 5, trigger storage access framework.
            if (!FileUtil.isWritableNormalOrSaf(folder, context)) {
                guideDialogForLEXA(folder.getPath());
                return 2;
            }
            return 1;
        } else if (Build.VERSION.SDK_INT == 19 && FileUtil.isOnExtSdCard(folder, context)) {
            // Assume that Kitkat workaround works
            return 1;
        } else if (FileUtil.isWritable(new File(folder, "DummyFile"))) {
            return 1;
        } else {
            return 0;
        }
    }

    private void triggerStorageAccessFramework() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, 3);
    }


    public void bbar(final Main main) {
        final String text = main.CURRENT_PATH;
        try {
            buttons.removeAllViews();
            buttons.setMinimumHeight(pathbar.getHeight());
            Drawable arrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_holo_dark);
            Bundle b = utils.getPaths(text, this);
            ArrayList<String> names = b.getStringArrayList("names");
            ArrayList<String> rnames = new ArrayList<String>();

            for (int i = names.size() - 1; i >= 0; i--) {
                rnames.add(names.get(i));
            }

            ArrayList<String> paths = b.getStringArrayList("paths");
            final ArrayList<String> rpaths = new ArrayList<String>();

            for (int i = paths.size() - 1; i >= 0; i--) {
                rpaths.add(paths.get(i));
            }
            View view = new View(this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    tabsSpinner.getLeft(), LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params1);
            buttons.addView(view);
            for (int i = 0; i < names.size(); i++) {
                final int k = i;
                ImageView v = new ImageView(this);
                v.setImageDrawable(arrow);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                v.setLayoutParams(params);
                final int index = i;
                if (rpaths.get(i).equals("/")) {
                    ImageButton ib = new ImageButton(this);
                    ib.setImageDrawable(icons.getRootDrawable());
                    ib.setBackgroundColor(Color.parseColor("#00ffffff"));
                    ib.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View p1) {
                            main.loadlist(("/"), false, false);
                            timer.cancel();
                            timer.start();
                        }
                    });
                    ib.setLayoutParams(params);
                    buttons.addView(ib);
                    if (names.size() - i != 1)
                        buttons.addView(v);
                } else if (isStorage(rpaths.get(i))) {
                    ImageButton ib = new ImageButton(this);
                    ib.setImageDrawable(icons.getSdDrawable());
                    ib.setBackgroundColor(Color.parseColor("#00ffffff"));
                    ib.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View p1) {
                            main.loadlist((rpaths.get(k)), false, true);
                            timer.cancel();
                            timer.start();
                        }
                    });
                    ib.setLayoutParams(params);
                    buttons.addView(ib);
                    if (names.size() - i != 1)
                        buttons.addView(v);
                } else {
                    Button button = new Button(this);
                    button.setText(rnames.get(index));
                    button.setTextColor(getResources().getColor(android.R.color.white));
                    button.setTextSize(13);
                    button.setLayoutParams(params);
                    button.setBackgroundResource(0);
                    button.setOnClickListener(new Button.OnClickListener() {

                        public void onClick(View p1) {
                            main.loadlist((rpaths.get(k)), false, true);
                            main.loadlist((rpaths.get(k)), false, true);
                            timer.cancel();
                            timer.start();
                        }
                    });
                    button.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            File file1 = new File(rpaths.get(index));
                            copyToClipboard(MainActivity.this, file1.getPath());
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.pathcopied), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });

                    buttons.addView(button);
                    if (names.size() - i != 1)
                        buttons.addView(v);
                }
            }

            scroll.post(new Runnable() {
                @Override
                public void run() {
                    sendScroll(scroll);
                    sendScroll(scroll1);
                }
            });

            if (buttons.getVisibility() == View.VISIBLE) {
                timer.cancel();
                timer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("button view not available");
        }
    }

    boolean isStorage(String path) {
        for (int i = 0; i < storage_count; i++)
            if (((EntryItem) list.get(i)).getPath().equals(path)) return true;
        return false;
    }

    private void sendScroll(final HorizontalScrollView scrollView) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_RIGHT);
                    }
                });
            }
        }).start();
    }

    String newPath = null;

    String parseSmbPath(String a) {
        if (a.contains("@"))
            return "smb://" + a.substring(a.indexOf("@") + 1, a.length());
        else return a;
    }

    public void updatePath(@NonNull final String news, boolean results, int
            openmode, int folder_count, int file_count) {

        if (news.length() == 0) return;
        File f = null;
        if (news == null) return;
        if (openmode == 1 && news.startsWith("smb:/"))
            newPath = parseSmbPath(news);
        else if (openmode == 2)
            switch (Integer.parseInt(news)) {
                case 0:
                    newPath = getResources().getString(R.string.images);
                    break;
                case 1:
                    newPath = getResources().getString(R.string.videos);
                    break;
                case 2:
                    newPath = getResources().getString(R.string.audio);
                    break;
                case 3:
                    newPath = getResources().getString(R.string.documents);
                    break;
                case 4:
                    newPath = getResources().getString(R.string.apks);
                    break;
            }
        else newPath = news;
        try {
            f = new File(newPath);
        } catch (Exception e) {
            return;
        }
        final TextView bapath = (TextView) pathbar.findViewById(R.id.fullpath);
        final TextView animPath = (TextView) pathbar.findViewById(R.id.fullpath_anim);
        if (!results) {
            TextView textView = (TextView) pathbar.findViewById(R.id.pathname);
            textView.setText(folder_count + " " + getResources().getString(R.string.folders) + "" +
                    " " + file_count + " " + getResources().getString(R.string.files));
        }
        final String oldPath = bapath.getText().toString();
        if (oldPath != null && oldPath.equals(newPath)) return;


        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);

        final StringBuilder stringBuilder = new StringBuilder();
        if (newPath.length() >= oldPath.length()) {
            // navigate forward
            stringBuilder.append(newPath);
            stringBuilder.delete(0, oldPath.length());
            animPath.setAnimation(slideIn);
            animPath.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animPath.setVisibility(View.GONE);
                    bapath.setText(newPath);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    animPath.setVisibility(View.VISIBLE);
                    animPath.setText(stringBuilder.toString());
                    //bapath.setText(oldPath);

                    scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll1.fullScroll(View.FOCUS_RIGHT);
                        }
                    });
                }
            }).start();
        } else if (newPath.length() <= oldPath.length()) {
            // navigate backwards
            stringBuilder.append(oldPath);
            stringBuilder.delete(0, newPath.length());
            animPath.setAnimation(slideOut);
            animPath.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animPath.setVisibility(View.GONE);
                    bapath.setText(newPath);

                    scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll1.fullScroll(View.FOCUS_RIGHT);
                        }
                    });
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    animPath.setVisibility(View.VISIBLE);
                    animPath.setText(stringBuilder.toString());
                    bapath.setText(newPath);

                    scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll1.fullScroll(View.FOCUS_LEFT);
                        }
                    });
                }
            }).start();
        }
    }

    public int dpToPx(double dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)));
        return px;
    }

    public void initiatebbar() {
        View pathbar = findViewById(R.id.pathbar);
        TextView textView = (TextView) findViewById(R.id.fullpath);

        pathbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main m = ((Main) getFragment().getTab());
                if (m.openMode == 0) {
                    bbar(m);
                    crossfade();
                    timer.cancel();
                    timer.start();
                }
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main m = ((Main) getFragment().getTab());
                if (m.openMode == 0) {
                    bbar(m);
                    crossfade();
                    timer.cancel();
                    timer.start();
                }
            }
        });

    }

    public void crossfade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        buttons.setAlpha(0f);
        buttons.setVisibility(View.VISIBLE);


        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        buttons.animate()
                .alpha(1f)
                .setDuration(100)
                .setListener(null);
        pathbar.animate()
                .alpha(0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pathbar.setVisibility(View.GONE);
                    }
                });
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)

    }

    private void crossfadeInverse() {


        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.

        pathbar.setAlpha(0f);
        pathbar.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        pathbar.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
        buttons.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        buttons.setVisibility(View.GONE);
                    }
                });
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
    }

    public boolean copyToClipboard(Context context, String text) {
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText("Path copied to clipboard", text);
            clipboard.setPrimaryClip(clip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void revealShow(final View view, boolean reveal) {

        if (reveal) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
            animator.setDuration(300); //ms
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        } else {

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
            animator.setDuration(300); //ms
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
            animator.start();

        }

    }

    private void onDrawerClosed() {
        if (pending_fragmentTransaction != null) {
            pending_fragmentTransaction.commit();
            pending_fragmentTransaction = null;
        }
        if (pending_path != null) {
            try {
                TabFragment m = getFragment();
                HFile hFile = new HFile(pending_path);
                Main main = ((Main) m.getTab());
                if (main != null)
                    if (hFile.isDirectory() && !hFile.isSmb()) {
                        ((Main) m.getTab()).loadlist((pending_path), false, false);
                    } else if (hFile.isSmb() || hFile.isCustomPath())
                        ((Main) m.getTab()).loadCustomList((pending_path), false);
                    else utils.openFile(new File(pending_path), mainActivity);

            } catch (ClassCastException e) {
                select = null;
                goToMain("");
            }
            pending_path = null;
        }
        supportInvalidateOptionsMenu();
    }

    public void mkFile(String path, Main ma) {
        boolean b = false;
        if (path == null)
            return;
        if (path.startsWith("smb:/")) {
            try {
                new SmbFile(path).createNewFile();
            } catch (SmbException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ma.updateList();
            return;
        }
        File f1 = new File(path);
        if (!f1.exists()) {
            int mode = checkFolder(new File(f1.getParent()), mainActivity);
            if (mode == 1) try {
                b = FileUtil.mkfile(f1, mainActivity);
            } catch (IOException e) {
                e.printStackTrace();
                b = false;
            }
            else if (mode == 2) {
                oppathe = f1.getPath();
                operation = NEW_FILE;
            }
            ma.updateList();

        } else {
            Toast.makeText(mainActivity, (R.string.fileexist), Toast.LENGTH_LONG).show();
        }
        if (!b && rootmode) RootTools.remount(f1.getParent(), "rw");
        RootHelper.runAndWait("touch " + f1.getPath(), true);
        ma.updateList();

    }

    void mkDir(String path, Main ma) {
        boolean b = false;
        if (path == null)
            return;
        if (path.startsWith("smb:/")) {
            try {
                new SmbFile(path).mkdirs();
            } catch (SmbException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ma.updateList();
            return;
        }
        File f = new File(path);
        if (!f.exists()) {
            int mode = checkFolder(f.getParentFile(), mainActivity);
            if (mode == 1) b = FileUtil.mkdir(f, mainActivity);
            else if (mode == 2) {
                oppathe = f.getPath();
                operation = NEW_FOLDER;
            }
            ma.updateList();
            if (b)
                Toast.makeText(mainActivity, (R.string.foldercreated), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mainActivity, (R.string.fileexist), Toast.LENGTH_LONG).show();
        }
        if (!b && rootmode) {
            RootTools.remount(f.getParent(), "rw");
            RootHelper.runAndWait("mkdir " + f.getPath(), true);
            ma.updateList();
        }
    }

    public void deleteFiles(ArrayList<String> files) {
        if (files == null) return;
        if (files.get(0).startsWith("smb://")) {
            new DeleteTask(null, mainActivity).execute((files));
            return;
        }
        int mode = checkFolder(new File(files.get(0)).getParentFile(), this);
        if (mode == 2) {
            oparrayList = (files);
            operation = DELETE;
        } else if (mode == 1 || mode == 0)
            new DeleteTask(null, mainActivity).execute((files));
    }

    public void extractFile(File file) {
        int mode = checkFolder(file.getParentFile(), this);
        if (mode == 2) {
            oppathe = (file.getPath());
            operation = EXTRACT;
        } else if (mode == 1) {
            Intent intent = new Intent(this, ExtractService.class);
            intent.putExtra("zip", file.getPath());
            startService(intent);
        } else Toast.makeText(this, R.string.not_allowed, Toast.LENGTH_SHORT).show();
    }

    public void compressFiles(File file, ArrayList<String> b) {
        int mode = checkFolder(file.getParentFile(), this);
        if (mode == 2) {
            oppathe = (file.getPath());
            operation = COMPRESS;
            oparrayList = b;
        } else if (mode == 1) {
            Intent intent2 = new Intent(this, ZipTask.class);
            intent2.putExtra("name", file.getPath());
            intent2.putExtra("files", b);
            startService(intent2);
        } else Toast.makeText(this, R.string.not_allowed, Toast.LENGTH_SHORT).show();
    }

    public void createSmbDialog(final String path, final boolean edit, final Main ma1) {
        final MaterialDialog.Builder ba3 = new MaterialDialog.Builder(this);
        ba3.title((R.string.smb_con));
        final View v2 = getLayoutInflater().inflate(R.layout.smb_dialog, null);
        final EditText ip = (EditText) v2.findViewById(R.id.editText);
        int color = Color.parseColor(fabskin);
        utils.setTint(ip, color);
        final EditText user = (EditText) v2.findViewById(R.id.editText3);
        utils.setTint(user, color);
        final EditText pass = (EditText) v2.findViewById(R.id.editText2);
        utils.setTint(pass, color);
        final CheckBox ch = (CheckBox) v2.findViewById(R.id.checkBox2);
        utils.setTint(ch, color);
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ch.isChecked()) {
                    user.setEnabled(false);
                    pass.setEnabled(false);
                } else {
                    user.setEnabled(true);
                    pass.setEnabled(true);

                }
            }
        });
        if (edit) {
            String userp = "", passp = "", ipp = "";
            try {
                jcifs.Config.registerSmbURLHandler();
                URL a = new URL(path);
                String userinfo = a.getUserInfo();
                if (userinfo != null) {
                    String inf = URLDecoder.decode(userinfo, "UTF-8");
                    userp = inf.substring(0, inf.indexOf(":"));
                    passp = inf.substring(inf.indexOf(":") + 1, inf.length());
                    user.setText(userp);
                    pass.setText(passp);
                } else ch.setChecked(true);
                ipp = a.getHost();
                ip.setText(ipp);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        ba3.customView(v2, true);
        if (theme1 == 1) ba3.theme(Theme.DARK);
        ba3.neutralText(R.string.cancel);
        ba3.positiveText(R.string.create);
        if (edit) ba3.negativeText(R.string.delete);
        ba3.positiveColor(color).negativeColor(color).neutralColor(color);
        ba3.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
                Main ma = ma1;
                if (ma == null) ma = ((Main) getFragment().getTab());
                String ipa = ip.getText().toString();
                SmbFile smbFile;
                if (ch.isChecked())
                    smbFile = ma.connectingWithSmbServer(new String[]{ipa, "", ""}, true);
                else {
                    String useru = user.getText().toString();
                    String passp = pass.getText().toString();
                    smbFile = ma.connectingWithSmbServer(new String[]{ipa, useru, passp}, false);
                }
                if (smbFile == null) return;
                try {
                    if (!edit) {
                        ma.loadCustomList(smbFile.getPath(), false);
                        if (Servers == null) Servers = new ArrayList<String>();
                        Servers.add(smbFile.getPath());
                        refreshDrawer();
                        if (!new File(getFilesDir() + "/" + "servers.xml").exists())
                            servers.makeS(false);
                        servers.addS(smbFile.getPath());
                    } else {
                        if (Servers == null) Servers = new ArrayList<String>();
                        if (Servers.contains(path)) Servers.remove(path);
                        Servers.add(smbFile.getPath());
                        refreshDrawer();
                        if (!new File(getFilesDir() + "/" + "servers.xml").exists())
                            servers.makeS(false);
                        try {
                            servers.removeS(path, mainActivity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (TransformerException e) {
                            e.printStackTrace();
                        }
                        servers.addS(smbFile.getPath());
                    }
                } catch (Exception e) {
                    Toast.makeText(mainActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }


            @Override
            public void onNegative(MaterialDialog materialDialog) {
                if (Servers.contains(path)) {
                    Servers.remove(path);
                    refreshDrawer();
                    try {
                        servers.removeS(path, mainActivity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ba3.build().show();

    }

    public void translateDrawerList(boolean down) {
        if (down)
            mDrawerList.animate().translationY(toolbar.getHeight());
        else mDrawerList.setTranslationY(0);
    }
}