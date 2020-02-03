package com.mario.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mario.myapplication.R;

public class UtilToken {
    public static void setToken(Context mContext, String token) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        Constantes.PREF_FILE_LOGIN,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constantes.PREF_JWT_KEY, token);
        editor.commit();
    }

    public static String getToken(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                Constantes.PREF_FILE_LOGIN,
                Context.MODE_PRIVATE
        );

        String jwt = sharedPreferences
                .getString(Constantes.PREF_JWT_KEY, null);

        return jwt;
    }

    public static void setId(Context mContext, String id) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        Constantes.PREF_FILE_LOGIN,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constantes.PREF_USERID, id);
        editor.commit();
    }
    public static String getId(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                Constantes.PREF_FILE_LOGIN,
                Context.MODE_PRIVATE
        );

        String id = sharedPreferences
                .getString(Constantes.PREF_USERID, null);

        return id;
    }

    public static void setLanguageIsoCode(Context mContext, String language) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        Constantes.PREF_FILE_LOGIN,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constantes.PREF_LANGUAGE_ISO_CODE, language);
        editor.commit();
    }
    public static String getLanguageIsoCode(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                Constantes.PREF_FILE_LOGIN,
                Context.MODE_PRIVATE
        );

        String id = sharedPreferences
                .getString(Constantes.PREF_LANGUAGE_ISO_CODE, null);

        return id;
    }

    public static void setLanguageId(Context mContext, String language) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        Constantes.PREF_FILE_LOGIN,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constantes.PREF_LANGUAGE_ID, language);
        editor.commit();
    }
    public static String getLanguageId(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                Constantes.PREF_FILE_LOGIN,
                Context.MODE_PRIVATE
        );

        String id = sharedPreferences
                .getString(Constantes.PREF_LANGUAGE_ID, null);

        return id;
    }

    public static void removePreferences(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                Constantes.PREF_FILE_LOGIN,
                Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constantes.PREF_TOKEN);
        editor.remove(Constantes.PREF_USERID);
        editor.remove(Constantes.PREF_LANGUAGE_ISO_CODE);
        editor.remove(Constantes.PREF_LANGUAGE_ID);
        editor.clear();
        editor.commit();

    }


}
