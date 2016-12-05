package com.wang.ian.ocrdemo.logic;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.api.services.translate.model.LanguagesListResponse;
import com.google.api.services.translate.model.LanguagesResource;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by wangyongqiang on 4/12/16.
 * TranslateObservable
 */

public class TranslateObservable {

    private static final String GOOGLE_KEY = "AIzaSyAkf4eYSZNpXXiXtzOIeqq32TbcgIa3eK4";
    private static final TranslateRequestInitializer mInitializer = new TranslateRequestInitializer(GOOGLE_KEY);

    public static Observable<TranslationsListResponse> getTranslateObservable(final LanguagesResource languagesResource, final List<String> phraseList) {
        return Observable.create(new Observable.OnSubscribe<TranslationsListResponse>() {
            @Override
            public void call(Subscriber<? super TranslationsListResponse> subscriber) {
                try {
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory factory = JacksonFactory.getDefaultInstance();
                    Translate translate = new Translate.Builder(transport, factory, null)
                            .setTranslateRequestInitializer(mInitializer)
                            .setApplicationName("OCRDemo")
                            .build();
                    ImmutableList<String> translationList = ImmutableList.<String>builder().addAll(phraseList).build();
                    subscriber.onNext(translate.translations().list(translationList, languagesResource.getLanguage()).execute());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<LanguagesListResponse> getSupportLanguageObservable() {
        return Observable.create(new Observable.OnSubscribe<LanguagesListResponse>() {
            @Override
            public void call(Subscriber<? super LanguagesListResponse> subscriber) {
                try {
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory factory = JacksonFactory.getDefaultInstance();
                    Translate translate = new Translate.Builder(transport, factory, null)
                            .setTranslateRequestInitializer(mInitializer)
                            .setApplicationName("OCRDemo")
                            .build();
                    subscriber.onNext(translate.languages().list().execute());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }
}
