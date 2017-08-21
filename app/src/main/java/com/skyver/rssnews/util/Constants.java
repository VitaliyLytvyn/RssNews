package com.skyver.rssnews.util;

/**
 * Created by skyver on 8/19/17.
 */

public class Constants {

    //public static String API_URL = "http://k.img.com.ua/rss/ru/all_news2.0.xml/";
    public static final String API_URL = "http://k.img.com.ua/";
    public static final String GET_RSS = "rss/ru/all_news2.0.xml";

    public static final String IMAGE_DIRCTORY = "imagerss";
    public static final String IMAGE_PREFIX = "image-";
    public static final String IMAGE_POSTEFIX = ".jpg";

    public static String makeFileName(String s){
        return IMAGE_PREFIX + s + IMAGE_POSTEFIX;
    }
}
