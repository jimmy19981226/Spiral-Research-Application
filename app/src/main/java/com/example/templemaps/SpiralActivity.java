package com.example.templemaps;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SpiralActivity extends AppCompatActivity {

    private TempleView tv;
    public SeekBar slider;
    public Context mContext;
    private int oldProgress;
    //not using Timer to make slider smooth anymore
    private MyTimer timA;
    public int progress;
    public int lastProgress;
    private int stop;
    private boolean sliderTouchedByHuman;
    private boolean justStartSlider;
    private float startPoint;
    private float stopPoint;
    private LinearLayout lnl;
    private LinearLayout lnlH;
    private Boolean sliderChangedByButton = false;
    private String selectedYear;
    private int selectedYearIndex;
    private String yearPickerString;
    private ArrayList<Integer> templeYearsThetaFriends = new ArrayList<Integer>();
    private AlertDialog.Builder yearPickerDialogBuilder;
    private boolean yearPickerDialogDismissedByPositiveButton;
    private String spaceDependingOnLanguage = "";
    private int width;
    private int height;
    private AlertDialog searchDialog;
    private int sliderMax;

    public class MyTimer extends Handler {

        public MyTimer() {
            sendMessageDelayed(obtainMessage(0), 1);
        }

        @Override
        public void handleMessage(Message m) {
            //Log.d("My Timer here ", "My Timer ****************" + " ");

            if (tv.touchDownOnScreenTempleView == TRUE) {
                progress = lastProgress;
            }

            float lastProgressF = tv.getLastProgress();
            lastProgress = (int)lastProgressF;
            float difference = Math.abs(lastProgress - progress);

            int eachStep = 0;
            if (sliderChangedByButton) {
                eachStep = 1;
            } else {
                eachStep = 8;
            }


            if (difference > eachStep) {
                tv.sliderInProgress(TRUE);
                if (lastProgress > progress) {
                    lastProgress = lastProgress - eachStep;
                    slider.setProgress((int)(lastProgress));
                    tv.setDegree((int)(lastProgress));
                } else if (lastProgress < progress) {
                    lastProgress = lastProgress + eachStep;
                    slider.setProgress((int)(lastProgress));
                    tv.setDegree((int) (lastProgress));
                }
                tv.invalidate();
                difference --;
                stop = 0;
            } else {
                if (stop == 0) {
                    tv.sliderInProgress(FALSE);
                    tv.invalidate();
                    sliderChangedByButton = false;
                }
                stop ++;
            }

            //very helper log here, display current slider progress and it's target progress
            //Log.d("progress is ", progress + " ");
            //Log.d("last progress is ", lastProgress + " ");

            sendMessageDelayed(obtainMessage(0), 1);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_spiral);
        mContext = SpiralActivity.this;


        //Initialization of Bottom Nav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);
        // Set the selected item listener
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_spiral) {
                    Log.d("Navigation", "Spiral selected");
                    // Already in the map activity, do nothing
                    return true;
                } else if (id == R.id.navigation_map) {
                    Log.d("Navigation", "Map selected");
                    startActivity(new Intent(SpiralActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.navigation_list) {
                    Log.d("Navigation", "List selected");
                    // Start the ListActivity
                    startActivity(new Intent(SpiralActivity.this, ListAct.class));
                    return true;
                }
                return false;
            }
        });
        bottomNav.setSelectedItemId(R.id.navigation_spiral);

        //Custom view setup
        tv = new TempleView(this);
        sliderMax = tv.howManyTemples * 30;

        // Adding values to the templeYearsThetaFriends list
        List<Integer> temporaryHolder = Arrays.asList(245, 300, 325, 340, 380, 420, 450, 490, 520, 540, 570, 610, 680, 715, 750, 780, 810, 850, 890, 1070, 1290, 1430, 1520, 1540, 1575, 1630, 1660, 1700, 1710, 1755, 1850, 1890, 2315, 3330, 3540, 3720, 3800, 3850, 3950, 4030, 4110, 4200, 4300, 4400, 4520, 4540, 4650, 4785, 4935, 5100, 5110, 5320, 5330, 6000, 7160);
        for (int i : temporaryHolder) {
            templeYearsThetaFriends.add(i);
        }
        yearPickerDialogBuilder = new AlertDialog.Builder(this);
        yearPickerDialogDismissedByPositiveButton = false;
        selectedYearIndex = 52;


        //Get Screen Dimensions
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;

        //Layout Parameters for Custom View
        LinearLayout.LayoutParams nice = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);


        tv.getWindowSize(width, height);
        tv.setLayoutParams(nice);

        //Setup slider
        slider = findViewById(R.id.seekBar3);
        slider.setBackgroundColor(Color.parseColor("#ffffff"));
        slider.setMax(sliderMax);
        slider.setProgress(5550);
        timA = new MyTimer();
        progress = 5550;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            slider.setMin(30);
        }
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //if (i >= 30) {
                if (sliderTouchedByHuman) {
                    int disableClickLastProgress = (int) (tv.getLastProgress());

                    if (Math.abs(disableClickLastProgress - i) > 200) {
                        slider.setProgress(disableClickLastProgress);
                    } else {
                        tv.setDegree(i);
                        tv.invalidate();
                    }
                    progress = i;
                } else {
                    slider.setProgress(lastProgress);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tv.sliderStart(TRUE);
                tv.invalidate();
                sliderTouchedByHuman = TRUE;
                slider.setProgress(lastProgress);
                justStartSlider = TRUE;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv.sliderStop(FALSE);
                tv.invalidate();

                sliderTouchedByHuman = FALSE;
                stopPoint = slider.getProgress();
            }
        });

        //Set up Left Button
        final ImageButton leftButton = findViewById(R.id.slider_left_button);
        leftButton.setBackgroundColor(Color.parseColor("#ffffff"));

        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftButton.setBackgroundColor(Color.parseColor("#ffffff"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftButton.setBackgroundColor(Color.parseColor("#ffffff"));
                    if (slider.getProgress() - 30 < 30) {
                        progress = 30;
                    } else {
                        progress = slider.getProgress() - 30;
                    }
                    slider.setProgress(lastProgress);
                    tv.setDegree(slider.getProgress());
                    tv.invalidate();
                    sliderChangedByButton = true;
                }
                return false;
            }
        });

        //Setup Right Button
        final ImageButton rightButton = findViewById(R.id.slider_right_button);
        rightButton.setBackgroundColor(Color.parseColor("#ffffff"));

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightButton.setBackgroundColor(Color.parseColor("#ffffff"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    rightButton.setBackgroundColor(Color.parseColor("#ffffff"));
                    if (slider.getProgress() + 30 > sliderMax) {
                        progress = sliderMax;
                    } else {
                        progress = slider.getProgress() + 30;
                    }
                    slider.setProgress(lastProgress);
                    tv.setDegree(slider.getProgress());
                    tv.invalidate();
                    sliderChangedByButton = true;
                }
                return false;
            }
        });


        // Layout parameters
        LinearLayout.LayoutParams one = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1);

        LinearLayout.LayoutParams two = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 4);

        leftButton.setLayoutParams(two);
        rightButton.setLayoutParams(two);

// Horizontal layout for buttons and slider
        lnlH = new LinearLayout(this);
        lnlH.setOrientation(LinearLayout.HORIZONTAL);
        lnlH.setBackgroundColor(Color.parseColor("#ffffff"));

        ((ViewGroup) leftButton.getParent()).removeView(leftButton);
        lnlH.addView(leftButton);

// Vertical layout for slider and its labels
        LinearLayout lnlSlider = new LinearLayout(this);
        lnlSlider.setOrientation(LinearLayout.VERTICAL);

        LinearLayout sliderLabelNoText = findViewById(R.id.sliderLabelNoText);
        sliderLabelNoText.setBackgroundColor(Color.parseColor("#ffffff"));
        ((ViewGroup) sliderLabelNoText.getParent()).removeView(sliderLabelNoText);
        lnlSlider.addView(sliderLabelNoText);

        ((ViewGroup) slider.getParent()).removeView(slider);
        lnlSlider.addView(slider);

        LinearLayout sliderLabelNoTextTwo = findViewById(R.id.sliderLabelNoTextTwo);
        sliderLabelNoTextTwo.setBackgroundColor(Color.parseColor("#ffffff"));
        ((ViewGroup) sliderLabelNoTextTwo.getParent()).removeView(sliderLabelNoTextTwo);
        lnlSlider.addView(sliderLabelNoTextTwo);

        sliderLabelNoText.setLayoutParams(two);
        slider.setLayoutParams(one);
        sliderLabelNoTextTwo.setLayoutParams(two);

        lnlSlider.setLayoutParams(one);

// Add slider layout to horizontal layout
        lnlH.addView(lnlSlider);

        ((ViewGroup) rightButton.getParent()).removeView(rightButton);
        lnlH.addView(rightButton);

        Integer sliderHeight = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sliderHeight = (int) (height * 0.1);
        } else {
            sliderHeight = (int) (height * 0.06);
        }

        lnlH.setMinimumHeight(sliderHeight);

// Main vertical layout
        lnl = new LinearLayout(this);
        lnl.setOrientation(LinearLayout.VERTICAL);

// Add TextView to main layout
        lnl.addView(tv);

// Add horizontal layout to main layout
        lnl.addView(lnlH);

// Bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(Color.parseColor("#ffffff"));

        if (bottomNavigationView.getParent() != null) {
            ((ViewGroup) bottomNavigationView.getParent()).removeView(bottomNavigationView);
        }
        lnl.addView(bottomNavigationView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

// Set layout parameters for main layout
        LinearLayout.LayoutParams lnlParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lnl.setLayoutParams(lnlParams);

// Set the final layout
        setContentView(lnl);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tv.orientationJustChanged(TRUE);
        tv.resetStaticCoordinatesGet();
        Log.d("1"," -- onConfigurationChanged");
//        if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//            //切换到竖屏
//            tv.orientationJustChanged(TRUE);
//            //Log.d("1"," -- onConfigurationChanged  可以在竖屏方向 to do something");
//        }else{
//            //切换到横屏
//            tv.orientationJustChanged(TRUE);
//            //Log.d("1"," -- onConfigurationChanged  可以在横屏方向 to do something");
//        }
        if (searchDialog != null) {
            setDialogSize(searchDialog);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(1,RED,1,R.string.settings);
        menu.add(1,GREEN,2,R.string.about);

        MenuItem item=menu.add(0,BLUE,0,"hi");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);//主要是这句话
        //item.setOnMenuItemClickListener(listener);//添加监听事件
        item.setIcon(R.drawable.calendar);//设置图标

        MenuItem itemSearch=menu.add(0,YELLOW,0,"hello");
        itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);//主要是这句话
        itemSearch.setIcon(R.drawable.search_icon);//设置图标

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case RED:
                //Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();

                Intent setting = new Intent(this, PrefsActivity.class);
                this.startActivity(setting);
                break;
            case GREEN:
                //Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                showAboutDialog();
                break;
            case BLUE:
                showYearPickerDialog();
                break;
            case YELLOW:
                showSearchDialog();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showSearchDialog() {

        //Toast.makeText(mContext, "allTempleNames length is: " + tv.allTempleNames.size(), Toast.LENGTH_SHORT).show();

        final ArrayAdapter<String> allTempleNamesAdapter = new ArrayAdapter<String>(
                SpiralActivity.this,   // Context上下文
                android.R.layout.simple_list_item_1,  // 子项布局id
                tv.allTempleNames
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(getResources().getString(R.string.app_name));

        final SearchView searchView = new SearchView(this);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getResources().getString(R.string.type_in_here_to_search_a_temple));
        searchView.setPadding(10,30,10,10);
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        final ListView listView = new ListView(this);
        listView.setAdapter(allTempleNamesAdapter);
        listView.setTextFilterEnabled(true);
        listView.setPadding(10,10,10,10);
        listView.setBackgroundColor(Color.parseColor("#ffffff"));


        LinearLayout.LayoutParams nice = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);

        listView.setLayoutParams(nice);

        //listView.setBackgroundColor(RED);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                Filter filter = ((Filterable) allTempleNamesAdapter).getFilter();
                if (!TextUtils.isEmpty(newText)){
                    //listView.setFilterText(newText);
                    filter.filter(newText);
                }else{
                    //listView.clearTextFilter();
                    filter.filter(null);
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long positionId) {//arg1为当前的view，用当前的view
                //Toast.makeText(mContext, "list position and positionId are: " + position + " " + positionId, Toast.LENGTH_SHORT).show();
                searchView.setQuery(allTempleNamesAdapter.getItem(position), false);
            }
        });



        LinearLayout searchDialogView = new LinearLayout(this);
        searchDialogView.setOrientation(LinearLayout.VERTICAL);
        searchDialogView.addView(searchView);
        searchDialogView.addView(listView);

        builder.setView(searchDialogView);
        //builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setCancelable(true);

        builder.setPositiveButton(getResources().getString(R.string.view), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int thisIndex = tv.allTempleNames.indexOf(searchView.getQuery().toString());
                int targetTempleSliderProgress = tv.spiral_effect.equalsIgnoreCase("static") ?  thisIndex * 30 + 30 : thisIndex * 30 + 150;
                if (targetTempleSliderProgress >= sliderMax) {
                    targetTempleSliderProgress = sliderMax;
                }
                tv.setSelectedTempleIndex(tv.allTempleNames.indexOf(searchView.getQuery().toString()));

                //Toast.makeText(mContext, searchView.getQuery() + " at progress " + targetTempleSliderProgress, Toast.LENGTH_SHORT).show();
                //we can use this selected year value to update spiral
                progress = targetTempleSliderProgress;
                slider.setProgress(lastProgress);
                tv.setDegree(slider.getProgress());
                tv.invalidate();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.return_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });

        searchDialog = builder.create();
        searchDialog.show();

        if (searchDialog != null) {
            setDialogSize(searchDialog);
        }

        Button btnPositive = searchDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = searchDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);

    }

    private void setDialogSize(Dialog dialog) {

        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);



        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        int h = 0;
        int w = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            h = (int)(Math.max(width, height) * 0.6);
            w = (int)(Math.min(width, height) * 0.9);
        } else {
            h = (int)((Math.min(width, height))* 0.9);
            w = (int)(Math.max(width, height) * 0.5);
        }
        //params.height = h;
        params.width =  w;
        dialog.getWindow().setAttributes(params);
        dialog.show();


    }



    public void showAboutDialog() {

        String html = getResources().getString(R.string.about_content_one) + "<br><br>";
        html += "<a href='" + getResources().getString(R.string.app_website_link) + "'>" + getResources().getString(R.string.about_content_two)+ "</a> <br>";
        html += "<br>" +
                getResources().getString(R.string.about_content_three)+ "<br>" +
                "<br>" +
                getResources().getString(R.string.about_content_four);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));

        TextView aboutTv = new TextView(this);
        aboutTv.setText(Html.fromHtml(html));
        aboutTv.setMovementMethod(LinkMovementMethod.getInstance());
        aboutTv.setGravity(Gravity.LEFT);
        aboutTv.setTextSize(20);
        aboutTv.setPadding(50,50,50,50);
        aboutTv.setBackgroundColor(Color.parseColor("#ffffee"));

        builder.setView(aboutTv);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setCancelable(true);
        builder.setNeutralButton(getResources().getString(R.string.return_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        final Button positiveButton=dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams positiveButtonLL =(LinearLayout.LayoutParams)positiveButton.getLayoutParams();
        positiveButtonLL.gravity=Gravity.CENTER;
        positiveButtonLL.width=ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showYearPickerDialog() {

        final NumberPicker yearPickerPicker = new NumberPicker(this);

        final ArrayList<String> allYearsWithoutDuplicates = new ArrayList<>();
        for (int i=0; i<tv.allYears.size(); i++) {
            String toBeAdded = tv.allYears.get(i);
            if (toBeAdded.equals("0000")) {
                toBeAdded = getResources().getString(R.string.temples_under_construction);
            } else if (toBeAdded.equals("1111")) {
                toBeAdded = getResources().getString(R.string.future_temples);
            }
            if(!allYearsWithoutDuplicates.contains(toBeAdded)) {
                allYearsWithoutDuplicates.add(toBeAdded);
            }
        }
        // i have to use this for loop to covert allYears arraylist to String[], I used toArray() on the arraylist, but did work
        final String[] temporary = new String[allYearsWithoutDuplicates.size()];
        for (int i = 0; i < allYearsWithoutDuplicates.size(); i++) {
            temporary[i] = allYearsWithoutDuplicates.get(i);
        }

        //Toast.makeText(mContext, "temporary length is: " + temporary.length + "", Toast.LENGTH_SHORT).show();
        //Toast.makeText(mContext, "allYeas size: " + tv.allYears.size() + "", Toast.LENGTH_SHORT).show();
        //Toast.makeText(mContext, temporary[100] + "", Toast.LENGTH_SHORT).show();

        yearPickerPicker.setDisplayedValues(temporary); //设置文字
        yearPickerPicker.setMaxValue(temporary.length - 1); //设置最大值
        //yearPickerPicker.setValue(0);
        yearPickerPicker.setValue(selectedYearIndex);
        selectedYear = "2020"; // we need this here, other wise, selectedYear is null when first time open year yearPickerPicker dialog and not moving the yearPickerPicker when passed in TempleView through method.
        //yearPickerPicker.setTextColor(Color.GRAY);

//        Locale curLocale = getResources().getConfiguration().locale;
        String curLan = getResources().getConfiguration().locale.getLanguage();


//        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
        if (curLan.equals("zh")) {
            // do nothing //中文
        } else {
            spaceDependingOnLanguage = " "; //英文
        }

        // we can use this text view to pass over want ever year is selected, or we can use a field so that it can be accessed from inner class
        // this text view is the title
        final TextView tx = new TextView(this);
        tx.setGravity(Gravity.CENTER);
//        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
        if (curLan.equals("zh")) {
            yearPickerString = getResources().getString(R.string.view) + " " + "2020" + " " + getResources().getString(R.string.year) + getResources().getString(R.string.view_temples_dedicated_In); //英文
            // 中文
        } else {
            yearPickerString = getResources().getString(R.string.view_temples_dedicated_In) + "2020"; //英文
        }
        tx.setText(yearPickerString);
        tx.setTextSize(20);
        tx.setPadding(5,20,5,5);
        tx.setTextColor(Color.BLACK);

        yearPickerPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int i, int i1) {
                selectedYear = temporary[i1]; // pass this selected value to dialog button, we can use a field so that it can be accessed from inner class
                selectedYearIndex = i1;
                if (temporary[i1].length() == 4) {
//                    Locale curLocale = getResources().getConfiguration().locale;
                    String curLan = getResources().getConfiguration().locale.getLanguage();
//                    if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
                    if (curLan.equals("zh")) {
                        yearPickerString = getResources().getString(R.string.view) + " " + temporary[i1] + " " + getResources().getString(R.string.year) + getResources().getString(R.string.view_temples_dedicated_In); //英文
                        // 中文
                    } else {
                        yearPickerString = getResources().getString(R.string.view_temples_dedicated_In) + temporary[i1]; //英文
                    }

                } else {
                    yearPickerString = getResources().getString(R.string.view) + spaceDependingOnLanguage + temporary[i1];
                }
                tx.setText(yearPickerString);
            }
        });


        yearPickerPicker.setBackgroundColor(Color.parseColor("#000000"));

        LinearLayout yearPickerView = new LinearLayout(this);
        yearPickerView.setOrientation(LinearLayout.VERTICAL);
        yearPickerView.addView(tx);
        yearPickerView.addView(yearPickerPicker);



        //builder.setTitle("hi");
        yearPickerDialogBuilder.setView(yearPickerView);
        yearPickerDialogBuilder.setCancelable(true);

        // user cannot enter a value
        //yearPickerPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        yearPickerDialogBuilder.setPositiveButton(getResources().getString(R.string.view), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // this enables year yearPickerPicker setOnValueChangedListener get called when click on positive button after entering a value.
                yearPickerPicker.clearFocus();
                //set onclick method for this button below
                Toast.makeText(mContext, yearPickerString, Toast.LENGTH_SHORT).show();
                //we can use this selected year value to update spiral
                progress = templeYearsThetaFriends.get(selectedYearIndex) - 200;
                slider.setProgress(lastProgress);
                tv.setDegree(slider.getProgress());
                tv.invalidate();
                tv.getSelectedYear(selectedYear);
                yearPickerDialogDismissedByPositiveButton = true;
            }
        });
        yearPickerDialogBuilder.setNegativeButton(getResources().getString(R.string.return_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
                //Toast.makeText(mContext, "Year Picker Dismissed" + templeYearsThetaFriends.size(), Toast.LENGTH_SHORT).show();
            }
        });
        yearPickerDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //处理监听事件
                if(!yearPickerDialogDismissedByPositiveButton) {
                    Toast.makeText(mContext, "You didn't pick any year", Toast.LENGTH_SHORT).show();
                }
                yearPickerDialogDismissedByPositiveButton = false;

            }
        });

        final AlertDialog dialog = yearPickerDialogBuilder.create();
        dialog.show();

        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);

        // these will override the onclick above
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //do something
//                Toast.makeText(mContext, "click on yes", Toast.LENGTH_SHORT).show();
//            }
//        });
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //do something
//                Toast.makeText(mContext, "click on no", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

}
