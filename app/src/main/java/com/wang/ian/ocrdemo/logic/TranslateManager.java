package com.wang.ian.ocrdemo.logic;

import com.google.api.services.translate.model.LanguagesListResponse;
import com.google.api.services.translate.model.LanguagesResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyongqiang on 4/12/16.
 * TranslateManager
 */

public class TranslateManager {
    private List<LanguagesResource> mSupportLanguageList = new ArrayList<>();
    private List<LanguagesResource> mOriginalLanguageList = new ArrayList<>();

    public static TranslateManager mInstance;

    private TranslateManager() {
        LanguagesResource resource = new LanguagesResource();
        resource.setLanguage("en");
        resource.setName("English");
        mOriginalLanguageList.add(resource);
    }

    public static TranslateManager getInstance() {
        if (mInstance == null) {
            mInstance = new TranslateManager();
        }
        return mInstance;
    }

    public void setSupportLanguageList(LanguagesListResponse response) {
        mSupportLanguageList = response.getLanguages();
    }

    public List<LanguagesResource> getSupportLanguageList() {
        return mSupportLanguageList;
    }

    public List<LanguagesResource> getOriginalLanguageList() {
        return mOriginalLanguageList;
    }
}
