package com.example.templemaps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResourceCache {

//    Integer testIdentifier;
    private ArrayList<String> templeInfo = new ArrayList<>();
    private ArrayList<String> templeDrawableNames = new ArrayList<>();
    public ArrayList<Integer> templeLargeDrawableIds = new ArrayList<>();
    public ArrayList<String> templeYears = new ArrayList<>();
    public ArrayList<String> allTempleLinks = new ArrayList<>();
    public ArrayList<String> templeNames = new ArrayList<>();
    public  ArrayList<Integer> smallImageIdentifiers = new ArrayList<>();
    public  ArrayList<Temple> templeObjects = new ArrayList<>();
    public  ArrayList<Integer>  allTempleInfoFileIds = new ArrayList<>();

    public ResourceCache(Context context, float w2) {

//        testIdentifier = context.getResources().getIdentifier("antofagasta_chile_temple", "drawable", "edu.byuh.cis.templevis");
//        Log.d("identifier 11111", testIdentifier + "");
//        Log.d("identifier 22222", R.drawable.antofagasta_chile_temple + "");


        Integer noImageIdentifier = context.getResources().getIdentifier("no_image", "drawable", "edu.byuh.cis.templevis");

        readInfoFile(context);
            for (String s: templeInfo) {
                if (s.length() >= 6) { // Ensure there's enough length for both operations
                    templeDrawableNames.add(s.substring(0, s.length()-6));
                    templeYears.add(s.substring(s.length()-5, s.length()-1));
                } else {
                    Log.e("ResourceCache", "Invalid string length for: " + s);
                    // Handle the error case, maybe add a default value or skip
                }


        }

        Log.d("temples count", templeInfo.size() + "");
//        Log.d("temples drawable names", templeDrawableNames.toString());
//        Log.d("temples years", templeYears.toString());

        for (String s: templeDrawableNames) {
            Integer identifier = context.getResources().getIdentifier(s, "drawable", "com.example.templemaps");
            if (identifier != 0) {
                smallImageIdentifiers.add(identifier);
            } else {
                smallImageIdentifiers.add(context.getResources().getIdentifier("no_image", "drawable", "com.example.templemaps"));
            }

            Integer largeIdentifier = context.getResources().getIdentifier(s + "_large", "drawable", "com.example.templemaps");
            if (largeIdentifier != 0) {
                templeLargeDrawableIds.add(largeIdentifier);
            } else {
                templeLargeDrawableIds.add(context.getResources().getIdentifier("no_image_large", "drawable", "com.example.templemaps"));
            }
//            Log.d("identifier", identifier + " is " + s);

            Integer infoFileIdentifier = context.getResources().getIdentifier(s, "raw", "com.example.templemaps");
            if (infoFileIdentifier != 0) {
                allTempleInfoFileIds.add(infoFileIdentifier);
            } else {
                allTempleInfoFileIds.add(context.getResources().getIdentifier("no_info", "raw", "com.example.templemaps"));
            }

            Log.d(s, infoFileIdentifier + " " + (infoFileIdentifier == 0 ?  "------" + context.getResources().getIdentifier("no_info", "raw", "com.example.templemaps") : " "));
            // rename raw files for those 0's

            String[] templeNameList = s.split("_");
            String templeName = "";
            for(int i = 0; i < templeNameList.length; i++) {
                String word = templeNameList[i];
                word = word.substring(0, 1).toUpperCase() + word.substring(1);
                templeName = templeName + " " + word;
            }
            templeNames.add(templeName.substring(1, templeName.length()));

            String[] templeLinkList = s.split("_");
            String templeLink = "";
            for(int i = 0; i < templeLinkList.length; i++) {
                String word = templeLinkList[i];
                word = word.substring(0, 1) + word.substring(1);
                templeLink = templeLink + "-" + word;
            }
            // update some broken links later.

//            templeLink = "https://www.churchofjesuschrist.org/temples/details/" + templeLink.substring(1,templeLink.length()) + "?lang=eng";
//
//            if (templeLink.contains("kirtland-temple")) {
//                templeLink = "https://www.kirtlandtemple.org/";
//            } else if (templeLink.contains("old-nauvoo-temple")) {
//                templeLink = "https://www.churchofjesuschrist.org/temples/details/nauvoo-illinois-temple?lang=eng";
//            } else if (templeLink.contains("st-george-utah-temple")) {
//                templeLink = "https://www.churchofjesuschrist.org/temples/details/st.-george-utah-temple?lang=eng";
//            } else if (templeLink.contains("")) {
//                templeLink = "";
//            } else if (templeLink.contains("")) {
//                templeLink = "";
//            } else if (templeLink.contains("")) {
//                templeLink = "";
//            } else if (templeLink.contains("")) {
//                templeLink = "";
//            } else if (templeLink.contains("")) {
//                templeLink = "";
//            }
//
            templeLink = "https://www.churchofjesuschrist.org/search?lang=eng&query=" + s;

//            Log.d(s, templeLink);

            allTempleLinks.add(templeLink);
        }

//        Log.d("small identifiers", smallImageIdentifiers.toString());
//        Log.d("temple names", templeNames.toString());
//        Log.d("large identifiers", templeLargeDrawableIds.toString());
//          Log.d("temple links", allTempleLinks.toString());
//        Log.d("temple info file ids", allTempleInfoFileIds.toString());

        float w = w2 / 4;

        for (int i:smallImageIdentifiers) {
            Bitmap temple = loadAndScale(context.getResources(),i, w);

            if(i == noImageIdentifier) {
                templeObjects.add(new Temple(temple, 0f, 0f, 0f, false));
            } else {
                templeObjects.add(new Temple(temple, 0f, 0f, 0f, true));
            }
        }
        Log.d("templeObjects size", templeObjects.size() + "");

        for(Temple temple: templeObjects) {
            temple.setLink(allTempleLinks.get(templeObjects.indexOf(temple)));
        }


    }

    public void readInfoFile(Context context) {
        try {
            InputStream templeInfosFile = context.getResources().openRawResource(R.raw.temple_names);
            if (templeInfosFile != null)
            {
                InputStreamReader ir = new InputStreamReader(templeInfosFile);
                BufferedReader br = new BufferedReader(ir);
                String line;
                //read each line
                while (( line = br.readLine()) != null) {
                    templeInfo.add(line+"\n");
                }
                templeInfosFile.close();
            }
        }
        catch (FileNotFoundException e)
        {
            Log.d("File", "The File doesn't not exist.");
        }
        catch (IOException e)
        {
            Log.d("File", e.getMessage());
        }

    }

    private static Bitmap loadAndScale(Resources res, int id, float newWidth) {
        Bitmap original = BitmapFactory.decodeResource(res, id);
        float aspectRatio = (float)original.getHeight()/(float)original.getWidth();
        float newHeight = newWidth * aspectRatio;
        return Bitmap.createScaledBitmap(original, (int)newWidth, (int)newHeight, true);
    }

}
