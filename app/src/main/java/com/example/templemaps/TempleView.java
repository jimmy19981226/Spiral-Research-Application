package com.example.templemaps;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class TempleView extends View {

    public int howManyTemples;
    private int sliderMax;
    private Paint blackPaint, greyPaint, spiralPaint, yearDisplayPaint;
    private float screenWidth, screenHeight;
    public float theta;
    private Path spiralLine;
    private Boolean loadedImages;
    private  float centerX;
    private float centerY;
    private float initialR;
    private boolean sliderMoving;
    private ArrayList<ArrayList<Float>> onScreenTemples;
    private ArrayList<Float> oneOnScreenTemple;
    private ArrayList<ArrayList<Float>> spiralCoordinates;
    private ArrayList<Float> sizes;
    private ArrayList<String> allTempleLinks;
//    private ArrayList<String> allTempleInfo;
    public ArrayList<String> allYears;
    public ArrayList<String> allTempleNames;
    private int eachIndex;
    private Matrix currentTempleMatrix;
    private float topCoordinateInSpiralX;
    private float topCoordinateInSpiralY;
    private float largestSizeInSpiral;
    private boolean coordinatesAndSizesUpdated;
    private boolean orientationJustChanged;
    public boolean touchDownOnScreenTempleView;
    private float downX;
    private float downY;
    private ArrayList<Float> movingCoordinatesLastTime;
    private long downTime;
    private float ultimateScreenWidth;
    private float initialRForLocation;
    private float windowWidth;
    private float windowHeight;
    private String lastSpiralEffectHolder;
    private static ArrayList<Integer> allLargeImageIds;
    private String oneTempleInfo;
    private static ArrayList<Integer> allTempleInfoFileIds = new ArrayList<>();
    private Boolean show_label;
    private String selectedYear;
    private Integer realEachIndex;
    private String templeUrl;
    private SingleTempleImage singleTempleImageView;
    private int staticCoordinatesGet = 0;
    private AlertDialog singleTempleDialog;
    private int selectedTempleIndex = -1;
    public String spiral_effect;
    private List<Temple> templeObjects;


    public TempleView(Context context) {
        super(context);
        templeObjects = Temple.loadTemplesFromJson(context, "temples.json"); // Load temple objects from JSON
        howManyTemples = templeObjects.size(); // Adjust howManyTemples based on loaded data
        // Other initializations

        for (Temple temple : templeObjects) {
            allTempleLinks.add(temple.getLink() != null ? temple.getLink() : ""); // Ensure there is a default value if no link exists
            allYears.add(extractYear(temple.getDescription()));
            allTempleNames.add(temple.getName());
        }


        howManyTemples = countTemples(context);
        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setTextSize(35);

        greyPaint = new Paint();
        greyPaint.setColor(Color.parseColor("#808080")); // Using a shade of grey
        greyPaint.setStyle(Paint.Style.FILL);
        greyPaint.setTextSize(60);

        spiralPaint = new Paint();
        spiralPaint.setColor(Color.parseColor("#808080"));
        spiralPaint.setStyle(Paint.Style.STROKE);
        spiralPaint.setStrokeWidth(5);
        spiralLine = new Path();
        loadedImages = false;
        spiralCoordinates = new ArrayList<>();
        sizes = new ArrayList<>();
        onScreenTemples = new ArrayList<>();
        oneOnScreenTemple = new ArrayList<>();
        allTempleLinks = new ArrayList<>();
//        allTempleInfo = new ArrayList<>();
        allYears = new ArrayList<>();
        allTempleNames = new ArrayList<>();
        theta = 5550;
        currentTempleMatrix = new Matrix();
        coordinatesAndSizesUpdated = FALSE;
        orientationJustChanged = FALSE;
        movingCoordinatesLastTime = new ArrayList<>();
        yearDisplayPaint = new Paint();
        selectedYear = "";
        sliderMax = howManyTemples * 30;

    }

    private String extractYear(String description) {
        String[] lines = description.split("\n");
        for (String line : lines) {
            if (line.startsWith("Dedication:")) {
                return line.substring("Dedication:".length()).trim().split(" ")[0];
            } else if (line.startsWith("Groundbreaking:")) {
                return line.substring("Groundbreaking:".length()).trim().split(" ")[0];
            } else if (line.startsWith("Announcement:")) {
                return line.substring("Announcement:".length()).trim().split(" ")[0];
            }
        }
        return "Unknown";
    }


    public void setDegree(int sliderP) {
        theta = sliderP;
        //Log.d("theta is ", theta + " ***************************************************************************************");
    }

    public float getLastProgress() {
        //Log.d("theta", " is " + theta + " ");
        return theta;
    }

    public void setSelectedTempleIndex(int i) {
        selectedTempleIndex = i;
    }

    public boolean sliderMovingOrAnimationInProgress() {
        return sliderMoving;
    }

    public void sliderStart(boolean s) {
        sliderMoving = s;
    }

    public void sliderStop(boolean s) {
        sliderMoving = s;
    }

    public void sliderInProgress(boolean s) {
        sliderMoving = s;
    }

//    public void readLinksFile() {
//        try {
//            InputStream allTempleLinksFile =  getContext().getResources().openRawResource(R.raw.all_temple_links);
//            if (allTempleLinksFile != null)
//            {
//                InputStreamReader ir = new InputStreamReader(allTempleLinksFile);
//                BufferedReader br = new BufferedReader(ir);
//                String line;
//                //read each line
//                int atThisLine = 0;
//                while (( line = br.readLine()) != null) {
//                    allTempleLinks.add(line+"\n");
//                    if (atThisLine < templeObjects.size()) {
//                        templeObjects.get(atThisLine).setLink(line+"\n");
//                        atThisLine ++;
//                    }
//                }
//                allTempleLinksFile.close();
//            }
//        }
//        catch (java.io.FileNotFoundException e)
//        {
//            Log.d("TestFile", "The File doesn't not exist.");
//        }
//        catch (IOException e)
//        {
//            Log.d("TestFile", e.getMessage());
//        }
//        //Log.d("allTempleLinks is ", allTempleLinks.get(1) + "");
//    }

    public void readOneInfoFile(int id) {
        try {
            InputStream oneTempleInfoFile =  this.getResources().openRawResource(id);
            if (oneTempleInfoFile != null)
            {
                InputStreamReader ir = new InputStreamReader(oneTempleInfoFile);
                BufferedReader br = new BufferedReader(ir);
                String line;
                //read each line
                while (( line = br.readLine()) != null) {
                    oneTempleInfo = oneTempleInfo + line+"\n";
                }
                oneTempleInfoFile.close();
            }
        }
        catch (java.io.FileNotFoundException e)
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        catch (IOException e)
        {
            Log.d("TestFile", e.getMessage());
        }
    }

//    public void readInfoFile() {
//        try {
//            InputStream allTempleInfoFile =  this.getResources().openRawResource(R.raw.temple_info);
//            if (allTempleInfoFile != null)
//            {
//                InputStreamReader ir = new InputStreamReader(allTempleInfoFile);
//                BufferedReader br = new BufferedReader(ir);
//                String line;
//                //read each line
//                while (( line = br.readLine()) != null) {
//                    allTempleInfo.add(line+"\n");
//                }
//                allTempleInfoFile.close();
//                allYears = getAllYearsFromAllTempleInfo(allTempleInfo);
//                allTempleNames = getAllTempleNamesFromAllTempleInfo(allTempleInfo);
//            }
//        }
//        catch (java.io.FileNotFoundException e)
//        {
//            Log.d("TestFile", "The File doesn't not exist.");
//        }
//        catch (IOException e)
//        {
//            Log.d("TestFile", e.getMessage());
//        }
//
//    }

//    public ArrayList<String> getAllYearsFromAllTempleInfo(ArrayList<String> allTempleInfoPassIn) {
//        ArrayList<String> temporary = new ArrayList<>();
////        for (int i = 0; i < temples.size(); i++) {
//        for (int i = 0; i < templeObjects.size(); i++) { // more OO
//            String year = allTempleInfo.get(i * 3 + 2) ;
////            Locale curLocale = getResources().getConfiguration().locale;
////            if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
////                // do nothing //中文
////                year = year;
////            } else {
////                year = year.substring(year.length()-5);
////                //英文
////            }
//
////            String curLan = getResources().getConfiguration().locale.getLanguage();
////            if (curLan.equals("zh")) {
////                // do nothing //中文
////                year = year;
////            } else {
////                year = year.substring(year.length()-5);
////                //英文
////            }
//
//
//
//
//            temporary.add(year.substring(0,4));
//        }
//        return temporary;
//    }
//
//    public ArrayList<String> getAllTempleNamesFromAllTempleInfo(ArrayList<String> allTempleInfoPassIn) {
//        ArrayList<String> temporary = new ArrayList<>();
//        for (int i = 0; i < templeObjects.size(); i++) { // more OO
//            String name = allTempleInfo.get(i * 3 + 0) ;
//            temporary.add(name);
//        }
//        return temporary;
//    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        float touchX= m.getX();;
        float touchY= m.getY();;
        //Log.d("TOUCH EVENT",  " touch event happens on screen at ************* " + touchX + " " + touchY );
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            downX = m.getX();
            downY = m.getY();
            //Toast.makeText(getContext(), "touched DOWN at " + downX + " " + downY, Toast.LENGTH_SHORT).show();
            movingCoordinatesLastTime.clear();
            movingCoordinatesLastTime.add(downX);
            movingCoordinatesLastTime.add(downY);
            //Log.d("DOWN",  " finger down on screen at |||||||||||||||" + downX + " " + downY );
            touchDownOnScreenTempleView = TRUE;
            downTime = System.currentTimeMillis();
        }

        if (m.getAction() == MotionEvent.ACTION_MOVE) {
            //Toast.makeText(getContext(), "finger moving on screen", Toast.LENGTH_SHORT).show();

            float movingX = m.getX();
            float movingY = m.getY();
            //Log.d("MOVING",  " finger moving on screen at " + movingX + " " + movingY );

            float lastX = movingCoordinatesLastTime.get(0);
            float lastY = movingCoordinatesLastTime.get(1);
            float xDisplacementFromLastMove = movingX - lastX;
            float yDisplacementFromLastMove = movingY - lastY;
            //Log.d("movingCoordinates", "movingCoordinatesLastTime is " + movingCoordinatesLastTime);

            movingCoordinatesLastTime.clear();
            movingCoordinatesLastTime.add(movingX);
            movingCoordinatesLastTime.add(movingY);
            //Log.d("xy displacementFLT ", xDisplacementFromLastMove + " " + yDisplacementFromLastMove);

            boolean top = (touchY <= centerY);
            boolean bottom = (touchY < 9 * screenHeight / 10 && touchY > centerY);

            boolean leftThirdVertical = (touchX <= centerX - screenWidth / 6 );
            boolean middleThirdVertical = (touchX > centerX - screenWidth / 6 && touchX < centerX + screenWidth / 6);
            boolean rightThirdVertical = (touchX >= centerX + screenWidth / 6 );

            int moveTheta = 10;

            boolean thetaMaxReached = theta >= sliderMax;
            boolean thetaMinReached = theta <= 30;

            if (leftThirdVertical) {
                if (yDisplacementFromLastMove > 0) {
                    if (thetaMaxReached) {
                    } else {
                        theta = theta + moveTheta;
                    }
                } else if (yDisplacementFromLastMove < 0) {
                    if (thetaMinReached) {
                    } else {
                        theta = theta - moveTheta;
                    }
                }
            } else if (rightThirdVertical) {
                if (yDisplacementFromLastMove > 0) {
                    if (thetaMinReached) {
                    } else {
                        theta = theta - moveTheta;
                    }
                } else if (yDisplacementFromLastMove < 0) {
                    if (thetaMaxReached) {
                    } else {
                        theta = theta + moveTheta;
                    }
                }
            } else if (middleThirdVertical) {
                if (touchY > centerY - screenWidth / 6 && touchY < centerY + screenWidth / 6) {
                    //do nothing, touch movement in center of spiral is disabled
                } else if (top) {
                    //check xd
                    if (xDisplacementFromLastMove > 0) {
                        if (thetaMinReached) {
                        } else {
                            theta = theta - moveTheta;
                        }
                    } else if (xDisplacementFromLastMove < 0) {
                        if (thetaMaxReached) {
                        } else {
                            theta = theta + moveTheta;
                        }
                    }
                } else if (bottom) {
                    //check xd
                    if (xDisplacementFromLastMove > 0) {
                        if (thetaMaxReached) {
                        } else {
                            theta = theta + moveTheta;
                        }
                    } else if (xDisplacementFromLastMove < 0) {
                        if (thetaMinReached) {
                        } else {
                            theta = theta - moveTheta;
                        }
                    }
                }
            }
        }

        if (m.getAction() == MotionEvent.ACTION_UP) {
            long upTime = System.currentTimeMillis();
            long period = upTime - downTime;
            touchDownOnScreenTempleView = FALSE;
            //helper--time test
            //Long timeLong = System.currentTimeMillis();
            //String time = String.valueOf(timeLong);
            //Toast.makeText(getContext(), "current time is " + time, Toast.LENGTH_SHORT).show();
            float x = m.getX();
            float y = m.getY();
            //Toast.makeText(getContext(), "touched a circle when UP at " + x + " " + y, Toast.LENGTH_SHORT).show();

            if (y < 9 * screenHeight / 10 && period < 100) {
                boolean singleTempleViewOpened = false;

                Collections.reverse(onScreenTemples);
                for (ArrayList<Float> eachOnScreenTemple : onScreenTemples) {
                    //remember each Float in inner class is a object, when convert it to int you need to use some method.
                    eachIndex = (int)(eachOnScreenTemple.get(0).floatValue());
                    float eachXCoordinate = eachOnScreenTemple.get(1);
                    float eachYCoordinate = eachOnScreenTemple.get(2);
                    float eachSize = eachOnScreenTemple.get(3);
                    float distanceToCurrentCoordinate = (float) (Math.sqrt(Math.pow(Math.abs(x - eachXCoordinate), 2) + Math.pow(Math.abs(y - eachYCoordinate), 2)));

                    if (distanceToCurrentCoordinate < eachSize) {
                        //Toast.makeText(getContext(), "touched a circle at " + x + " " + y + " and eachIndex here is " + eachIndex , Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(), "how many onScreenTemples last time? " + onScreenTemples.size(), Toast.LENGTH_SHORT).show();
                        //Log.d("singleTempleViewOpen? ", singleTempleViewOpened + "");
                        if (singleTempleViewOpened == false) {
                            if (eachIndex <= templeObjects.size()) {
                                singleTempleViewOpened = true;
                                //Log.d("eachIndex is ", eachIndex + " when click on circle");
                                singleTempleDialog();
                            } else {
                                //no link
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Nothing Here");
                                builder.setMessage("future temples to come!");
                                builder.setIcon(R.mipmap.ic_launcher_round);
                                builder.setCancelable(true);
                                final AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }
                }
                Collections.reverse(onScreenTemples);
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    public void singleTempleDialog() {


        LinearLayout.LayoutParams nice = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
        LinearLayout.LayoutParams niceFour = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 4);

        LinearLayout.LayoutParams wrapContent = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        LinearLayout lnl = new LinearLayout(getContext());
        lnl.setOrientation(LinearLayout.VERTICAL);

        LinearLayout lnlH = new LinearLayout(getContext());
        lnlH.setOrientation(LinearLayout.HORIZONTAL);

        if (eachIndex == 0) {
            singleTempleImageView = new SingleTempleImage(getContext(), allLargeImageIds.get(eachIndex), allLargeImageIds.get(eachIndex), allLargeImageIds.get(eachIndex + 1));
        } else if (eachIndex == templeObjects.size() - 1){
            singleTempleImageView = new SingleTempleImage(getContext(), allLargeImageIds.get(eachIndex), allLargeImageIds.get(eachIndex - 1), allLargeImageIds.get(eachIndex));
        } else {
            singleTempleImageView = new SingleTempleImage(getContext(), allLargeImageIds.get(eachIndex), allLargeImageIds.get(eachIndex - 1), allLargeImageIds.get(eachIndex + 1));
        }

        singleTempleImageView.setPadding(0,0,0,0);
        //singleTempleImageView.setBackgroundColor(Color.RED);

        // milestone dates
        oneTempleInfo = templeObjects.get(eachIndex).getDescription();

        final TextView singleTempleTextView = new TextView(getContext());
        singleTempleTextView.setText(oneTempleInfo);
        //singleTempleTextView.setBackgroundColor(Color.BLUE);
        singleTempleTextView.setGravity(Gravity.CENTER);

        ScrollView sv = new ScrollView(getContext());
        //sv.setPadding(100,100,100,100);
        sv.addView(singleTempleTextView);

        // here is where we get templeUrl, to avoid the eachIndex change error
        //final String templeUrl = allTempleLinks.get(eachIndex);
        realEachIndex = eachIndex; // we do this because each index is changing for some reason later...
        templeUrl = templeObjects.get(realEachIndex).link;

        final TextView singleTempleDialogTitleView = new TextView(getContext());
//        singleTempleDialogTitleView.setText(allTempleInfo.get(realEachIndex*3));
        singleTempleDialogTitleView.setText(templeObjects.get(realEachIndex).getName());
        singleTempleDialogTitleView.setTextSize(20);
        singleTempleDialogTitleView.setPadding(0,20,0,0);
        singleTempleDialogTitleView.setTextColor(Color.BLACK);
        singleTempleDialogTitleView.setGravity(Gravity.CENTER);
        //singleTempleDialogTitleView.setHeight((int)(Math.min(screenWidth, screenHeight) * 0.1));
        //singleTempleDialogTitleView.setMaxLines(1);
        //singleTempleDialogTitleView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        //singleTempleDialogTitleView.setPadding(1, 1, 1, 1);
        singleTempleDialogTitleView.setHeight((int)(Math.min(screenWidth, screenHeight) * 0.15));
        //singleTempleDialogTitleView.setMovementMethod(ScrollingMovementMethod.getInstance());

        final long[] timeStamp = new long[1];
        timeStamp[0] = 0;
        // view last or next temple buttons
        Button left = new Button(getContext());
        left.setWidth((int)screenWidth / 10);
        left.setText(">");
        left.setTextSize(20);
        left.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    // do nothing
                    //Toast.makeText(getContext(), realEachIndex.toString(), Toast.LENGTH_SHORT).show();
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if (realEachIndex < templeObjects.size() - 1) {
                        if (System.currentTimeMillis() - timeStamp[0] > 1550) {
                            realEachIndex = realEachIndex + 1;
                            singleTempleImageView.moveImage("left");
                            //Log.d("realEachIndex is ", realEachIndex.toString());
                            if (realEachIndex == templeObjects.size() - 1) {
                                singleTempleImageView.updateThreeTemplesBitmapIds(allLargeImageIds.get(realEachIndex), allLargeImageIds.get(realEachIndex - 1), allLargeImageIds.get(realEachIndex));
                            } else {
                                singleTempleImageView.updateThreeTemplesBitmapIds(allLargeImageIds.get(realEachIndex), allLargeImageIds.get(realEachIndex - 1), allLargeImageIds.get(realEachIndex + 1));
                            }
                            templeUrl = templeObjects.get(realEachIndex).link;
//                            singleTempleDialogTitleView.setText(allTempleInfo.get(realEachIndex*3));
                            singleTempleDialogTitleView.setText(allTempleNames.get(realEachIndex));
                            oneTempleInfo = "";
                            readOneInfoFile(allTempleInfoFileIds.get(realEachIndex));
                            singleTempleTextView.setText(oneTempleInfo);
                            timeStamp[0] = System.currentTimeMillis();
                        }
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.dubai_temple_is_the_most_recent_temple), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        Button right = new Button(getContext());
        right.setWidth((int)screenWidth / 10);
        right.setText("<");
        right.setTextSize(20);
        right.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    // do nothing
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    // do something
                    if (realEachIndex > 0) {
                        if (System.currentTimeMillis() - timeStamp[0] > 1550) {
                            realEachIndex = realEachIndex - 1;
                            singleTempleImageView.moveImage("right");
                            int lastTempleId = 0;
                            if (realEachIndex - 1 < 0) {
                                lastTempleId = allLargeImageIds.get(realEachIndex);
                            } else {
                                lastTempleId = allLargeImageIds.get(realEachIndex - 1);
                            }
                            singleTempleImageView.updateThreeTemplesBitmapIds(allLargeImageIds.get(realEachIndex), lastTempleId, allLargeImageIds.get(realEachIndex + 1));
                            templeUrl = templeObjects.get(realEachIndex).link;
//                            singleTempleDialogTitleView.setText(allTempleInfo.get(realEachIndex*3));
                            singleTempleDialogTitleView.setText(allTempleNames.get(realEachIndex));
                            oneTempleInfo = "";
                            readOneInfoFile(allTempleInfoFileIds.get(realEachIndex));
                            singleTempleTextView.setText(oneTempleInfo);
                            timeStamp[0] = System.currentTimeMillis();
                        }
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.kirtland_temple_is_the_oldest_temple), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        // the left button is actually on the right and the right button is actually on the left.
        lnlH.addView(right); // lnlH.addView(left);
        lnlH.addView(singleTempleImageView);
        lnlH.addView(left); // lnlH.addView(right);

        lnl.addView(singleTempleDialogTitleView);

        lnlH.setBackgroundColor(Color.parseColor("#ffffff"));
        sv.setBackgroundColor(Color.parseColor("#ffffff"));

        lnl.addView(lnlH);
        //lnlH.setBackgroundColor(Color.GREEN);
        //lnl.addView(sv);

        singleTempleTextView.setBackgroundColor(Color.parseColor("#ffffff"));
        ((ViewGroup)singleTempleTextView.getParent()).removeView(singleTempleTextView);
        lnl.addView(singleTempleTextView);
        //singleTempleTextView.setBackgroundColor(Color.RED);
        singleTempleTextView.setHeight((int)(Math.min(screenWidth, screenHeight) * 0.2));
        singleTempleTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        singleTempleImageView.setLayoutParams(nice);
        left.setLayoutParams(niceFour);
        right.setLayoutParams(niceFour);
        lnlH.setLayoutParams(nice);
        // singleTempleDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //builder.setTitle(allTempleInfo.get(realEachIndex*3));
        builder.setView(lnl);
        builder.setCancelable(true);
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.website_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.return_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //set onclick method for this button below
            }
        });
        singleTempleDialog = builder.create();
        singleTempleDialog.show();

        //singleTempleDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = singleTempleDialog.getWindow().getAttributes();
        int h = 0;
        int w = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            h = (int)(Math.min(windowHeight, windowWidth) * 1.2);
            w = (int)Math.min(windowHeight, windowWidth);
        } else {
            h = (int)(Math.min(windowHeight, windowWidth) * 0.9);
            w = (int)Math.min(windowHeight, windowWidth);
        }
        params.height = h;
        params.width =  w;
        singleTempleDialog.getWindow().setAttributes(params);
        singleTempleDialog.show();

        Button btnPositive = singleTempleDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = singleTempleDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);

        singleTempleDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //singleTempleDialog.dismiss();
                //singleTempleDialog stays when click on website button

                // for some reason, i don't why, but each index is changed in here,
                // so we get templeUrl before this, according to the correct eachIndex
                //String templeUrl = allTempleLinks.get(eachIndex);
                //Log.d("eachIndex is ", eachIndex + " when click on website button");
                //Log.d("templeUrl is ", templeUrl + "");

                if (templeUrl.equals("" + "\n")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("No Link Available");
                    builder.setMessage("Temple does not have a website yet");
                    builder.setIcon(R.mipmap.ic_launcher_round);
                    //点击对话框以外的区域是否让对话框消失
                    builder.setCancelable(true);
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Intent eachTemplePage= new Intent();
                    eachTemplePage.setAction("android.intent.action.VIEW");
                    Uri eachTemplePage_url = Uri.parse(templeUrl);
                    eachTemplePage.setData(eachTemplePage_url);
                    getContext().startActivity(eachTemplePage);
                }
            }
        });
    }

    public void orientationJustChanged(boolean b) {
        orientationJustChanged = b;
        //singleTempleImageView.updatePositionAndSizeOnceOrientationChanged();
        if (singleTempleImageView != null) { // the rotate phone without clicking on a temple
            singleTempleImageView.orientationJustChanged(b);
            //singleTempleImageView.invalidate();

            // reset single temple dialog size according to screen size once orientation change happens
            WindowManager.LayoutParams params = singleTempleDialog.getWindow().getAttributes();
            int h = 0;
            int w = 0;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                h = (int)(Math.min(windowHeight, windowWidth) * 1.2);
                w = (int)Math.min(windowHeight, windowWidth);
            } else {
                h = (int)(Math.min(windowHeight, windowWidth) * 0.9);
                w = (int)Math.min(windowHeight, windowWidth);
            }
            params.height = h;
            params.width =  w;
            singleTempleDialog.getWindow().setAttributes(params);
        }
    }

    public void getWindowSize(float w, float h) {
        windowWidth = w;
        windowHeight = h;
    }

    public void resetStaticCoordinatesGet() {
        staticCoordinatesGet = 0;
    }

    @Override
    public void onDraw(Canvas c) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenWidth = c.getWidth() / 2;
            screenHeight = (float) (c.getHeight() );
            centerX = screenWidth / 2 + 3 * screenWidth / 16;
            centerY = (float) (screenHeight / 2);
            ultimateScreenWidth = Math.min(windowHeight, windowWidth);
            yearDisplayPaint.setTextSize((int)(2 * screenHeight / 20));
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenWidth = c.getWidth();
            screenHeight = (float) (c.getHeight() );
            centerX = screenWidth * 0.57f;
            centerY = (float) (screenHeight / 2);
            ultimateScreenWidth = screenWidth;
            yearDisplayPaint.setTextSize((int)(screenHeight / 35));
            //Log.d("PORTRAIT ", "|||||||||||||||||||||||||||||" + screenWidth);
        }

        initialR = screenWidth / 10;
        initialRForLocation = ultimateScreenWidth / 10;

        spiral_effect = PrefsActivity.getSpiralEffectPref(getContext());
        show_label = PrefsActivity.getShowLabelPref(getContext());
        //Log.d("spiral effect ", spiral_effect + " ");

        if (spiral_effect.equalsIgnoreCase("static") && staticCoordinatesGet <= 10) {
            spiralCoordinates.clear();
            getCoordinates();
            staticCoordinatesGet += 1;
        }


        if (orientationJustChanged == TRUE) {
            spiralCoordinates.clear();
            //sizes.clear();
            getCoordinates();
            //getSizes();
            orientationJustChanged = FALSE;
            //Log.d("coordinates and sizes ", " just reset ");
            //Log.d("orChanged coorSize ", " ++++++++++++++++ "
                    //+ spiralCoordinates.size() + " "
                    //+ sizes.size());
            //Log.d("spiralCoordinates", spiralCoordinates + " ");
            //Log.d("sizes", sizes + " ");
        }
        //when app first launch this got called.
        if (coordinatesAndSizesUpdated == FALSE) {
            spiralCoordinates.clear();
            getCoordinates();
            getSizes();
            coordinatesAndSizesUpdated = TRUE;
            //Log.d("launch coorSize ", " ++++++++++++++++ "
                    //+ spiralCoordinates.size() + " "
                    //+ sizes.size());
            //Log.d("spiralCoordinates", spiralCoordinates + " ");
            //Log.d("sizes", sizes + " ");
            //Log.d("screenWidth", screenWidth + " ");
            //Log.d("screenHeight", screenHeight + " ");
        }


        // we need to update the coordinates when switching to static mode from other effect. other wise coordinates for other effect will be kept.
        if (spiral_effect.equalsIgnoreCase("static")) {
            //just turn to static or it was static before?
            if (lastSpiralEffectHolder == null) {
                //first time run, do noting
            } else {
                if (lastSpiralEffectHolder.equalsIgnoreCase(spiral_effect)) {
                    //do nothing, it have been static last time
                } else {
                    //just turn to static, get a new coordinates
                    spiralCoordinates.clear();
                    getCoordinates();
                }
            }
        } else {
            //not static, do nothing
        }
        lastSpiralEffectHolder = spiral_effect;


        if (spiral_effect.equalsIgnoreCase("spin")) {
            spiralCoordinates.clear();
            getCoordinatesRotateRegular();
        } else if (spiral_effect.equalsIgnoreCase("zoom")) {
            spiralCoordinates.clear();
            getCoordinatesRotateZoom();
        } else if (spiral_effect.equalsIgnoreCase("threeD")) {
            spiralCoordinates.clear();
            getCoordinatesThreeD();
        }

        //c.drawColor(Color.parseColor("#66ccff"));

        //Temple View Background color
        //c.drawColor(Color.parseColor("#24292b"));
        c.drawColor(Color.parseColor("#FFFFFF"));

        //we just want to load the images once, we don't have to load it every time when we re-draw. otherwise the program is gonna be so slow
        if (loadedImages == false) {
            loadedImages = true;
            //get the temple images in array list

            //when app launches, images are loaded according to screen width
            //when launches landscape, according to window height
            float temp;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                temp = windowHeight;
            } else {
                temp = screenWidth;
            }

            ResourceCache resourceCache = new ResourceCache(getContext(), temp);


//            ImageCache.init(getResources(), temp, screenHeight);
//            allLargeImageIds = ImageCache.getAllImageIds();

            allLargeImageIds = resourceCache.templeLargeDrawableIds;

//            allTempleInfoFileIds = ImageCache.getAllTempleInfoFileIds();

//            for(int i=0; i<allLargeImageIds.size(); i++) {
//                allTempleInfoFileIds.add(R.raw.albuquerque_temple);
//            }

            allTempleInfoFileIds = resourceCache.allTempleInfoFileIds;


            //temples = ImageCache.getTemplesList();

            // replacing this line
//            templeObjects = ImageCache.getTempleObjectsList(); // more OO
            // with
            templeObjects = resourceCache.templeObjects;

//            Log.d("old templeObjects size", templeObjects.size() + "");

//            readLinksFile();

//            allTempleLinks = resourceCache.allTempleLinks;

//            readInfoFile();
            allTempleNames = resourceCache.templeNames;
            allYears = resourceCache.templeYears;
//            Log.d("allyears", allYears.toString());
//            Log.d("allnames", allTempleNames.toString());


//            for (Temple t: templeObjects) {
//                t.setLink(allTempleLinks.get(templeObjects.indexOf(t)));
//            }

            yearDisplayPaint.setColor(Color.parseColor("#000000"));
            yearDisplayPaint.setStyle(Paint.Style.FILL);
            yearDisplayPaint.setTextAlign(Paint.Align.CENTER);
        }

        //helper
        //c.drawText("Screen Width and Height are " + screenWidth + " " + screenHeight, 0, screenHeight - 100, bluePaint);
        //c.drawText("how many temples " + temples.size() + " ", 0, screenHeight - 200, redPaint);
        //c.drawRect(0,3 * screenHeight/4, screenWidth, 3 * screenHeight/4 + 10, bluePaint);
        //the middle circle image is here ==============================================
        //drawMiddleCircle(c);

        placeAllCircles(c);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            yearDisplayLandscape(c);

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            yearDisplay(c);
        }


    }

    public void drawTempleLabels(float ts, Temple t, Canvas c) { // more OO

        float newCurrentTempleRadius = t.size * screenWidth / 2;

        Paint thisTempleLabelPaint = new Paint();
        thisTempleLabelPaint.setColor(Color.parseColor("#000000"));
        thisTempleLabelPaint.setStyle(Paint.Style.FILL);
        thisTempleLabelPaint.setTextSize((int)(newCurrentTempleRadius / 5));
        thisTempleLabelPaint.setTextAlign(Paint.Align.CENTER);
        thisTempleLabelPaint.setShadowLayer(20,0,-5,Color.BLACK);

        Paint thisTempleLabelPaintNoImage = new Paint();
        thisTempleLabelPaintNoImage.setColor(Color.rgb(0, 0, 0));
        thisTempleLabelPaintNoImage.setStyle(Paint.Style.FILL);
        thisTempleLabelPaintNoImage.setTextSize((int)(newCurrentTempleRadius / 4));
        thisTempleLabelPaintNoImage.setTextAlign(Paint.Align.CENTER);
//        thisTempleLabelPaintNoImage.setShadowLayer(20,0,-5,Color.BLACK);

//        int thisTempleIndex = temples.indexOf(t);
        int thisTempleIndex = templeObjects.indexOf(t); // more OO

//        String thisTempleName = allTempleInfo.get(thisTempleIndex*3);
        String thisTempleName = allTempleNames.get(thisTempleIndex);
        Locale curLocale = getResources().getConfiguration().locale;
        String curLan = getResources().getConfiguration().locale.getLanguage();
        //Log.d("current language: ", curLan);

        String thisTempleLocation = "";
        //通过Locale的equals方法，判断出当前语言环境
        //Log.d("thisTempleName: ", thisTempleName);
//        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
//            //中文
//            thisTempleLocation = thisTempleName.substring(0, thisTempleName.length() - 3);
//            //Log.d("language: ", "zh");
//        } else {
//            //英文
//            //Log.d("language: ", "en");
//            thisTempleLocation = thisTempleName.substring(0, thisTempleName.length() - 7);
//        }

        if (curLan.equals("zh")) {
            //中文
//            thisTempleLocation = thisTempleName.substring(0, thisTempleName.length() - 3);
            // TODO bug to fix later, in chinese mode, lable will just say lable ()
            thisTempleLocation = "";

        } else {
            //英文
            thisTempleLocation = thisTempleName.substring(0, thisTempleName.length() - 7);
        }


        //String thisTempleLocation = thisTempleName ;//.substring(0, thisTempleName.length() - 7);

        String[] thisTempleLocationWords = thisTempleLocation.split(" ");

        String thisTempleNameOne = "";
        String thisTempleNameTwo = "";
        if (thisTempleLocationWords.length % 2 == 0) { // if there are even number of words in location, then each line has the same number of words
            for (int i = 0; i < thisTempleLocationWords.length / 2; i ++) {
                thisTempleNameOne += thisTempleLocationWords[i] + " ";
            }
            for (int i = thisTempleLocationWords.length / 2; i < thisTempleLocationWords.length ; i ++) {
                thisTempleNameTwo += thisTempleLocationWords[i] + " ";
            }
        } else { // if there are odd number of words in location, then second line has one more line than first line
            for (int i = 0; i < thisTempleLocationWords.length / 2; i ++) {
                thisTempleNameOne += thisTempleLocationWords[i] + " ";
            }
            for (int i = thisTempleLocationWords.length / 2; i < thisTempleLocationWords.length ; i ++) {
                thisTempleNameTwo += thisTempleLocationWords[i] + " ";
            }
        }

//        if (sliderMoving == false && ts < 200 && thisTempleIndex < 185 && show_label) {
        if (show_label) {
            //c.drawText(thisTempleName, currentTempleX, currentTempleY + newCurrentTempleRadius + thisTempleLabelPaint.getTextSize(), thisTempleLabelPaint);

            if(t.hasImage) {
                if(ts < 200) {
                    if (sliderMoving == false) {
                        c.drawText(thisTempleNameOne, t.x, t.y + newCurrentTempleRadius - thisTempleLabelPaint.getTextSize(), thisTempleLabelPaint);
                        c.drawText(thisTempleNameTwo, t.x, t.y + newCurrentTempleRadius, thisTempleLabelPaint);
                    }
                }
            } else {
                c.drawText(thisTempleNameOne, t.x, t.y, thisTempleLabelPaintNoImage);
                c.drawText(thisTempleNameTwo, t.x, t.y + thisTempleLabelPaintNoImage.getTextSize(), thisTempleLabelPaintNoImage);
//                c.drawText("No Image", t.x, t.y + newCurrentTempleRadius, thisTempleLabelPaintNoImage);

            }
        }
    }

    public void getSelectedYear(String s) {
        selectedYear = s;
    }

    public void actuallyDrawing(Temple t, Canvas c, int thisTempleIndex) { // more OO


        float newCurrentTempleRadius = t.size * screenWidth / 2;

        currentTempleMatrix.setScale(4 * t.size, 4 * t.size);
//        currentTempleMatrix.postTranslate(currentTempleX - t.getWidth()  *currentTempleSize*2, currentTempleY - t.getHeight() * currentTempleSize*2);
        currentTempleMatrix.postTranslate(t.x - t.image.getWidth()  * t.size * 2, t.y - t.image.getHeight() * t.size * 2); // more OO

        Paint selectedYearTempleFramePaint = new Paint();
        selectedYearTempleFramePaint.setColor(Color.parseColor("#000000"));
        selectedYearTempleFramePaint.setStyle(Paint.Style.FILL);
        if (selectedYear.equals("Temples under construction") || selectedYear.equals("建设中的圣殿")) {
            selectedYear = "0000";
        } else if (selectedYear.equals("Future Temples") || selectedYear.equals("即将奉献的圣殿")) {
            selectedYear = "1111";
        }
        // if current temple is with selected year then draw a circle frame
        if (allYears.get(thisTempleIndex).equals(selectedYear)) {
            c.drawCircle(t.x, t.y, newCurrentTempleRadius * 1.1f , selectedYearTempleFramePaint);
            if (!(selectedYear.equals("0000") || selectedYear.equals("1111"))) {
                c.drawText(selectedYear, 100, 100, yearDisplayPaint);
            }

        } else {
            // do nothing
        }

        if (selectedTempleIndex == thisTempleIndex) {
            c.drawCircle(t.x, t.y, newCurrentTempleRadius * 1.1f , selectedYearTempleFramePaint);
        }

//        c.drawBitmap(t, currentTempleMatrix, null);
        c.drawBitmap(t.image, currentTempleMatrix, null); // more OO
    }

public void placeAllCircles(Canvas c) {
    onScreenTemples.clear();
    for (Temple t : templeObjects) {
        int thisTempleIndex = templeObjects.indexOf(t);
        float ts = theta - 30 * thisTempleIndex;
        if (ts > 0 && ts < spiralCoordinates.size() - 1) {
            t.size = sizes.get((int) ts);
            t.x = spiralCoordinates.get((int) ts).get(0);
            t.y = spiralCoordinates.get((int) ts).get(1);

            actuallyDrawing(t, c, thisTempleIndex);
            drawTempleLabels(ts, t, c);

            float currentTempleRadius = t.size * screenWidth / 2;
            oneOnScreenTemple.add((float) thisTempleIndex);
            oneOnScreenTemple.add(t.x);
            oneOnScreenTemple.add(t.y);
            oneOnScreenTemple.add(currentTempleRadius);
            ArrayList<Float> oneOnScreenTempleCopy = new ArrayList<>(oneOnScreenTemple);
            onScreenTemples.add(oneOnScreenTempleCopy);
            oneOnScreenTemple.clear();
        }
    }
    Collections.reverse(onScreenTemples);
}

    public void yearDisplay(Canvas c) {

        //get the index of on screen temples,
        //the first one in on screen temples to the last
        //go to temple info file, the specific line to get years
        //3 lines each temple in the file

        //c.drawRect(0, 9 * screenHeight / 10, screenWidth, screenHeight, bluePaint);
        float firstOnScreenTempleIndex = 0;
        float lastOnScreenTempleIndex = 0;

        // new year display logic
        if (onScreenTemples.size() != 0) {
            lastOnScreenTempleIndex = (onScreenTemples.get(onScreenTemples.size()-1).get(0));
            firstOnScreenTempleIndex = (onScreenTemples.get(0).get(0));
        }

//        String endYear = allTempleInfo.get((int)(firstOnScreenTempleIndex) * 3 + 2);
//        String startYear = allTempleInfo.get((int)(lastOnScreenTempleIndex) * 3 + 2) ;
        String endYear = allYears.get((int)(firstOnScreenTempleIndex));
        String startYear = allYears.get((int)(lastOnScreenTempleIndex)) ;


        String curLan = getResources().getConfiguration().locale.getLanguage();

        Locale curLocale = getResources().getConfiguration().locale;
//        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
//            // do nothing //中文
//            startYear = startYear.substring(0,4);
//            endYear = endYear.substring(0,4);
//        } else {
//            startYear = startYear.substring(startYear.length()-5);
//            endYear = endYear.substring(endYear.length()-5);
//            //英文
//        }
//
//        if (curLan.equals("zh")) {
//            // do nothing //中文
//            startYear = startYear.substring(0,4);
//            endYear = endYear.substring(0,4);
//        } else {
//            startYear = startYear.substring(startYear.length()-5);
//            endYear = endYear.substring(endYear.length()-5);
//            //英文
//        }



        if (theta <= 40){
            c.drawText( getResources().getString(R.string.first_temple) + "" + "1836", screenWidth / 2, 39 * screenHeight / 40, yearDisplayPaint);
        } else if (theta > 5550 ) {
            c.drawText( getResources().getString(R.string.future_temples), screenWidth / 2, 39 * screenHeight / 40, yearDisplayPaint);
        } else if (endYear.contains("0000") || endYear.contains("1111")){
            c.drawText( getResources().getString(R.string.years_of_temples) + " "  + startYear + "--- " + 2020, screenWidth / 2, 39 * screenHeight / 40, yearDisplayPaint);
        } else {
            //Log.d("endYeas is ", endYear);
            c.drawText( getResources().getString(R.string.years_of_temples) + " "  + startYear + "--- " + endYear, screenWidth / 2, 39 * screenHeight / 40, yearDisplayPaint);
        }
    }

    public void yearDisplayLandscape(Canvas c) {
        c.drawRect( 5 * screenWidth / 4, 0, 2 * screenWidth, screenHeight, blackPaint);
        float firstOnScreenTempleIndex = 0;
        float lastOnScreenTempleIndex = 0;

        // new year display logic
        if (onScreenTemples.size() != 0) {
            lastOnScreenTempleIndex = (onScreenTemples.get(onScreenTemples.size()-1).get(0));
            firstOnScreenTempleIndex = (onScreenTemples.get(0).get(0));
        }
//        String endYear = allTempleInfo.get((int)(firstOnScreenTempleIndex) * 3 + 2);
//        String startYear = allTempleInfo.get((int)(lastOnScreenTempleIndex) * 3 + 2) ;

        String endYear = allYears.get((int)(firstOnScreenTempleIndex));
        String startYear = allYears.get((int)(lastOnScreenTempleIndex)) ;

//        String curLan = getResources().getConfiguration().locale.getLanguage();
//        if (curLan.equals("zh")) {
//            // do nothing //中文
//            startYear = startYear.substring(0,4);
//            endYear = endYear.substring(0,4);
//        } else {
//            startYear = startYear.substring(startYear.length()-5);
//            endYear = endYear.substring(endYear.length()-5);
//            //英文
//        }


//        Locale curLocale = getResources().getConfiguration().locale;
//        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
//            // do nothing //中文
//            startYear = startYear.substring(0,4);
//            endYear = endYear.substring(0,4);
//        } else {
//            startYear = startYear.substring(startYear.length()-5);
//            endYear = endYear.substring(endYear.length()-5);
//            //英文
//        }





            //String curLan = getResources().getConfiguration().locale.getLanguage();
//            if (curLan.equals("zh")) {
//                // do nothing //中文
//                startYear = startYear.substring(0,4);
//                endYear = endYear.substring(0,4);
//            } else {
//                startYear = startYear.substring(startYear.length()-5);
//                endYear = endYear.substring(endYear.length()-5);
//                //英文
//            }

        if (theta <= 40){
            c.drawText(getResources().getString(R.string.first_temple), 6.5f * screenWidth / 4, 18 * screenHeight / 10, yearDisplayPaint);
            c.drawText("1836", 6.5f * screenWidth / 4, 22 * screenHeight / 10, yearDisplayPaint);
        } else if (theta > 5550 ) {
            c.drawText(getResources().getString(R.string.future_temples), 6.5f * screenWidth / 4, 20 * screenHeight / 10, yearDisplayPaint);
        } else if (endYear.contains("0000") || endYear.contains("1111")){
            c.drawText(getResources().getString(R.string.years_of_temples) + " " , 6.5f * screenWidth / 4, 15 * screenHeight / 10, yearDisplayPaint);
            c.drawText(startYear + " --- " + 2020, 6.5f * screenWidth / 4, 25 * screenHeight / 10, yearDisplayPaint);
        } else {
            c.drawText(getResources().getString(R.string.years_of_temples) + " " , 6.5f * screenWidth / 4, 15 * screenHeight / 10, yearDisplayPaint);
            c.drawText(startYear + " --- " + endYear, 6.5f * screenWidth / 4, 25 * screenHeight / 10, yearDisplayPaint);
       }
    }

    public void getCoordinates() {
        //spiral are impacted a lot by initialR.
        //circles locations remain whether landscape or portrait
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //initialRForLocation is 0 when app launches, causing spiral becomes a dot.
            //when first launch, I treat windowHeight as initial R, which is just screen width later
            //(有差距，因为有状态栏，so window height is slightly smaller than screen width)
            if (coordinatesAndSizesUpdated == FALSE) {
                initialR = windowHeight / 10;
            } else {
                initialR = initialRForLocation;
            }
            //Log.d("initialR", " " + initialR);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            initialR = screenWidth / 10;
        }

        //for (float t = -30; t < 30; t += 0.02f) {
        for (float t = -18; t < 17.5; t += 0.02f) {
            //Equiangular spiral function：
            //x = p * cosA, y = p * sinA, where p = N * e^(B * cotC)
            //When C = PI/2, graph is a circle, when C = 0, graph is a straight line
            float x = centerX + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t));
            float y = centerY + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t));

            //intendSize is the new size compare to original (original has width of 0.1 * screenWidth)

            ArrayList<Float> oneSpiralCoordinate = new ArrayList<>();
            oneSpiralCoordinate.add(x);
            oneSpiralCoordinate.add(y);

            ArrayList<Float> oneSpiralCoordinateCopy = new ArrayList<>();
            oneSpiralCoordinateCopy.addAll(oneSpiralCoordinate);
            //Log.d("x y coordinate", oneSpiralCoordinate.get(0) + "<- x, y -> " + oneSpiralCoordinate.get(1));
            spiralCoordinates.add(oneSpiralCoordinateCopy);
            //Log.d("x y coordinate", "right after adding, spiralCoordinates are " + spiralCoordinates);
            oneSpiralCoordinate.clear();
        }

        topCoordinateInSpiralX = spiralCoordinates.get(spiralCoordinates.size()-1).get(0);
        topCoordinateInSpiralY = spiralCoordinates.get(spiralCoordinates.size()-1).get(1);

        //when q += 12f, top lines circles next to each other the whole time\
        //must change the same time as getCoordinates()
        for (float q = 0; q < 20; q += 1) {

            ArrayList<Float> oneSpiralCoordinateTop = new ArrayList<>();
            oneSpiralCoordinateTop.add(topCoordinateInSpiralX + q * 20);
            oneSpiralCoordinateTop.add(topCoordinateInSpiralY);

            ArrayList<Float> oneSpiralCoordinateTopCopy = new ArrayList<>();
            oneSpiralCoordinateTopCopy.addAll(oneSpiralCoordinateTop);
            spiralCoordinates.add(oneSpiralCoordinateTopCopy);
            oneSpiralCoordinateTop.clear();
        }
        //Toast.makeText(getContext(), spiralCoordinates.size() + " ", Toast.LENGTH_SHORT).show();
        Collections.reverse(spiralCoordinates);
    }

    public void getCoordinatesRotateRegular() {
        //spiral are impacted a lot by initialR.
        //circles locations remain whether landscape or portrait
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //initialRForLocation is 0 when app launches, causing spiral becomes a dot.
            //when first launch, I treat windowHeight as initial R, which is just screen width later
            //(有差距，因为有状态栏，so window height is slightly smaller than screen width)
            if (coordinatesAndSizesUpdated == FALSE) {
                initialR = windowHeight / 10;
            } else {
                initialR = initialRForLocation;
            }
            //Log.d("initialR", " " + initialR);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            initialR = screenWidth / 10;
        }
        //for (float t = -30; t < 30; t += 0.02f) {
        for (float t = -18; t < 17.5; t += 0.02f) {
            //Equiangular spiral function：
            //x = p * cosA, y = p * sinA, where p = N * e^(B * cotC)
            //When C = PI/2, graph is a circle, when C = 0, graph is a straight line
            float x = centerX + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t));
            float y = centerY + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t));

            //intendSize is the new size compare to original (original has width of 0.1 * screenWidth)

            float angle = theta / 100;
            float xNew = (x - centerX) * (float) (Math.cos(angle)) - (y - centerY) * (float) (Math.sin(angle)) + centerX;
            float yNew = (y - centerY) * (float) (Math.cos(angle)) + (x - centerX) * (float) (Math.sin(angle)) + centerY;

            ArrayList<Float> oneSpiralCoordinate = new ArrayList<>();
            oneSpiralCoordinate.add(xNew);
            oneSpiralCoordinate.add(yNew);

            ArrayList<Float> oneSpiralCoordinateCopy = new ArrayList<>();
            oneSpiralCoordinateCopy.addAll(oneSpiralCoordinate);
            //Log.d("x y coordinate", oneSpiralCoordinate.get(0) + "<- x, y -> " + oneSpiralCoordinate.get(1));
            spiralCoordinates.add(oneSpiralCoordinateCopy);
            //Log.d("x y coordinate", "right after adding, spiralCoordinates are " + spiralCoordinates);
            oneSpiralCoordinate.clear();
        }

        topCoordinateInSpiralX = spiralCoordinates.get(spiralCoordinates.size()-1).get(0);
        topCoordinateInSpiralY = spiralCoordinates.get(spiralCoordinates.size()-1).get(1);

        float secondTopCoordinateInSpiralX = spiralCoordinates.get(spiralCoordinates.size()-2).get(0);
        float secondTopCoordinateInSpiralY = spiralCoordinates.get(spiralCoordinates.size()-2).get(1);

        //when q += 12f, top lines circles next to each other the whole time\
        //must change the same time as getCoordinates()
        for (float q = 0; q < 20; q += 1) {

            ArrayList<Float> oneSpiralCoordinateTop = new ArrayList<>();

            float xDirection = topCoordinateInSpiralX - secondTopCoordinateInSpiralX;
            float yDirection = topCoordinateInSpiralY - secondTopCoordinateInSpiralY;

            float step = q * 30;
            oneSpiralCoordinateTop.add(xDirection / Math.abs(xDirection) * step + secondTopCoordinateInSpiralX);
            oneSpiralCoordinateTop.add(yDirection / Math.abs(yDirection) * step + secondTopCoordinateInSpiralY);

            ArrayList<Float> oneSpiralCoordinateTopCopy = new ArrayList<>();
            oneSpiralCoordinateTopCopy.addAll(oneSpiralCoordinateTop);
            spiralCoordinates.add(oneSpiralCoordinateTopCopy);
            oneSpiralCoordinateTop.clear();
        }
        //Toast.makeText(getContext(), spiralCoordinates.size() + " ", Toast.LENGTH_SHORT).show();
        Collections.reverse(spiralCoordinates);
    }

    public void getCoordinatesRotateZoom() {
        //spiral are impacted a lot by initialR.
        //circles locations remain whether landscape or portrait
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //initialRForLocation is 0 when app launches, causing spiral becomes a dot.
            //when first launch, I treat windowHeight as initial R, which is just screen width later
            //(有差距，因为有状态栏，so window height is slightly smaller than screen width)
            if (coordinatesAndSizesUpdated == FALSE) {
                initialR = windowHeight / 10;
            } else {
                initialR = initialRForLocation;
            }
            //Log.d("initialR", " " + initialR);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            initialR = screenWidth / 10;
        }
        //for (float t = -30; t < 30; t += 0.02f) {
        for (float t = -18; t < 17.5; t += 0.02f) {
            //Equiangular spiral function：
            //x = p * cosA, y = p * sinA, where p = N * e^(B * cotC)
            //When C = PI/2, graph is a circle, when C = 0, graph is a straight line
            float x = centerX + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t));
            float y = centerY + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t));

            //intendSize is the new size compare to original (original has width of 0.1 * screenWidth)

            float angle = theta / 50;
            float xNew = (x - centerX) * (float) (Math.cos(angle)) - (y - centerY) * (float) (Math.sin(angle)) + centerX;
            float yNew = (y - centerY) * (float) (Math.cos(angle)) + (x - centerX) * (float) (Math.sin(angle)) + centerY;

            ArrayList<Float> oneSpiralCoordinate = new ArrayList<>();
            oneSpiralCoordinate.add(xNew);
            oneSpiralCoordinate.add(yNew);

            ArrayList<Float> oneSpiralCoordinateCopy = new ArrayList<>();
            oneSpiralCoordinateCopy.addAll(oneSpiralCoordinate);
            //Log.d("x y coordinate", oneSpiralCoordinate.get(0) + "<- x, y -> " + oneSpiralCoordinate.get(1));
            spiralCoordinates.add(oneSpiralCoordinateCopy);
            //Log.d("x y coordinate", "right after adding, spiralCoordinates are " + spiralCoordinates);
            oneSpiralCoordinate.clear();
        }

        topCoordinateInSpiralX = spiralCoordinates.get(spiralCoordinates.size()-1).get(0);
        topCoordinateInSpiralY = spiralCoordinates.get(spiralCoordinates.size()-1).get(1);

        //when q += 12f, top lines circles next to each other the whole time\
        //must change the same time as getCoordinates()
        for (float q = 0; q < 20; q += 1) {

            ArrayList<Float> oneSpiralCoordinateTop = new ArrayList<>();

            float xDirection = topCoordinateInSpiralX - centerX;
            float yDirection = topCoordinateInSpiralY - centerY;

            float step = q * 10;
            oneSpiralCoordinateTop.add((xDirection) / Math.abs(xDirection) * step + topCoordinateInSpiralX);
            oneSpiralCoordinateTop.add((yDirection) / Math.abs(yDirection) * step + topCoordinateInSpiralY);

            ArrayList<Float> oneSpiralCoordinateTopCopy = new ArrayList<>();
            oneSpiralCoordinateTopCopy.addAll(oneSpiralCoordinateTop);
            spiralCoordinates.add(oneSpiralCoordinateTopCopy);
            oneSpiralCoordinateTop.clear();
        }
        //Toast.makeText(getContext(), spiralCoordinates.size() + " ", Toast.LENGTH_SHORT).show();
        Collections.reverse(spiralCoordinates);
    }

    public void getCoordinatesThreeD() {
        //spiral are impacted a lot by initialR.
        //circles locations remain whether landscape or portrait
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //initialRForLocation is 0 when app launches, causing spiral becomes a dot.
            //when first launch, I treat windowHeight as initial R, which is just screen width later
            //(有差距，因为有状态栏，so window height is slightly smaller than screen width)
            if (coordinatesAndSizesUpdated == FALSE) {
                initialR = windowHeight / 10;
            } else {
                initialR = initialRForLocation;
            }
            //Log.d("initialR", " " + initialR);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            initialR = screenWidth / 10;
        }
        //for (float t = -30; t < 30; t += 0.02f) {
        for (float t = -18; t < 17.5; t += 0.02f) {
            //Equiangular spiral function：
            //x = p * cosA, y = p * sinA, where p = N * e^(B * cotC)
            //When C = PI/2, graph is a circle, when C = 0, graph is a straight line
            float x = centerX + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t));
            float y = centerY + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t));

            //intendSize is the new size compare to original (original has width of 0.1 * screenWidth)

            float angle = theta / 500;
            x = (x - centerX) * (float) (Math.cos(angle)) - (y - centerY) * (float) (Math.sin(angle)) + centerX;
            y = (y - centerY) * (float) (Math.cos(angle)) + (x - centerX) * (float) (Math.sin(angle)) + centerY;

            ArrayList<Float> oneSpiralCoordinate = new ArrayList<>();
            oneSpiralCoordinate.add(x);
            oneSpiralCoordinate.add(y);

            ArrayList<Float> oneSpiralCoordinateCopy = new ArrayList<>();
            oneSpiralCoordinateCopy.addAll(oneSpiralCoordinate);
            //Log.d("x y coordinate", oneSpiralCoordinate.get(0) + "<- x, y -> " + oneSpiralCoordinate.get(1));
            spiralCoordinates.add(oneSpiralCoordinateCopy);
            //Log.d("x y coordinate", "right after adding, spiralCoordinates are " + spiralCoordinates);
            oneSpiralCoordinate.clear();
        }

        topCoordinateInSpiralX = spiralCoordinates.get(spiralCoordinates.size()-1).get(0);
        topCoordinateInSpiralY = spiralCoordinates.get(spiralCoordinates.size()-1).get(1);

        //when q += 12f, top lines circles next to each other the whole time\
        //must change the same time as getCoordinates()
        for (float q = 0; q < 20; q += 1) {

            ArrayList<Float> oneSpiralCoordinateTop = new ArrayList<>();
            oneSpiralCoordinateTop.add(topCoordinateInSpiralX + q * 20);
            oneSpiralCoordinateTop.add(topCoordinateInSpiralY);

            ArrayList<Float> oneSpiralCoordinateTopCopy = new ArrayList<>();
            oneSpiralCoordinateTopCopy.addAll(oneSpiralCoordinateTop);
            spiralCoordinates.add(oneSpiralCoordinateTopCopy);
            oneSpiralCoordinateTop.clear();
        }
        //Toast.makeText(getContext(), spiralCoordinates.size() + " ", Toast.LENGTH_SHORT).show();
        Collections.reverse(spiralCoordinates);
    }

    public void getSizes() {
        float pi = (float) Math.PI;

        //circles sizes remain whether landscape or portrait
        initialR = screenWidth / 10;
        //Toast.makeText(getContext(), "getSizes called, sizes.length is " + sizes.size(), Toast.LENGTH_SHORT).show();

        float newSize = 0;
        //for (float t = -30; t < 30; t += 0.02f) {
        for (float t = -18; t < 17.5; t += 0.02f) {
            float x = centerX + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t));
            float y = centerY + initialR * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t));

            float t2 = t - 2 * pi;
            float x2 = centerX + initialR * (float) (Math.exp(t2 * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t2));
            float y2 = centerY + initialR * (float) (Math.exp(t2 * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t2));
            newSize = (float) (Math.sqrt(Math.pow(Math.abs(x - x2), 2) + Math.pow(Math.abs(y - y2), 2)));
            newSize = (newSize / screenWidth * 1.3f);
            sizes.add(newSize);
        }
        //Log.d("sizes 1400 are ", " " + sizes.get(1400));
        //Log.d("sizes size is ", " " + sizes.size());
        //Log.d("initialR is ", " " + initialR);

        int sizesSizeInSpiralPart = sizes.size();
        largestSizeInSpiral = sizes.get(sizesSizeInSpiralPart - 1);

        //when q += 12f, top lines circles next to each other the whole time
        //must change the same time as getCoordinates()
        for (float q = 0; q < 20; q += 1) {
            sizes.add(largestSizeInSpiral);
//            if (sizes.size() >= spiralCoordinates.size()) {
//                break;
//            }
        }
        Collections.reverse(sizes);
    }

    public void drawSpiral(Canvas c) {
        float e = (float) (Math.E);
        float a = screenWidth / 10;
        //draw spiral
        spiralLine.reset();
        spiralLine.moveTo(centerX, centerY);
        //radius of the circle in the middle
        //c.drawCircle(centerX, centerY, initialR, spiralPaint);
        Log.d("theta ", "is " + theta);
        for (float t = -18; t < 17.5; t += 0.02f) {
            //Equiangular spiral function：
            //x = p * cosA, y = p * sinA, where p = N * e^(B * cotC)
            //When C = PI/2, graph is a circle, when C = 0, graph is a straight line
//            float x = centerX + a * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t));
//            float y = centerY + a * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t));
            float x = centerX + a * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.cos(t));
            float y = centerY + a * (float) (Math.exp(t * 1 / (Math.tan(47 * Math.PI / 100)))) * (float) (Math.sin(t));

            // rotates about (0, 0)
            //float xNew = x * (float)(Math.cos(theta)) + y * (float)(Math.sin(theta));
            //float yNew = y * (float)(Math.cos(theta)) - x * (float)(Math.sin(theta));

            float angle = theta / 500;
            float xNew = (x - centerX) * (float) (Math.cos(angle)) - (y - centerY) * (float) (Math.sin(angle)) + centerX;
            float yNew = (y - centerY) * (float) (Math.cos(angle)) + (x - centerX) * (float) (Math.sin(angle)) + centerY;

            //spiral doesn't rotates
            //spiralLine.lineTo(x, y);

            //spiral rotates
            spiralLine.lineTo(xNew, yNew);
        }
        //draw the spiral ****************************************
        c.drawPath(spiralLine, spiralPaint);
        //Toast.makeText(getContext(), count + " ", Toast.LENGTH_SHORT).show();
    }

    public static int countTemples(Context context){ // returns the number of temples in temple_names.txt
        int count = 0;                               // manually inputting a number should no longer be necessary
        BufferedReader br = null;
        InputStream inputStream = context.getResources().openRawResource(R.raw.temple_names);
        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
            while (br.readLine() != null){
                count ++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

}
