/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wang.ian.ocrdemo.logic;

import android.text.TextUtils;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.wang.ian.ocrdemo.ui.camera.GraphicOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private LanguageListener mListener;
    private int mIndex;
    private int mCurrent;
    private boolean mIsTranslating;

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, LanguageListener listener) {
        mGraphicOverlay = ocrGraphicOverlay;
        mListener = listener;
        mIndex = 0;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        addIndex();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        translate(items);
    }

    private void addIndex() {
        if (mIndex < Integer.MAX_VALUE) {
            mIndex ++;
        } else {
            mIndex = 0;
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

    private void translate(SparseArray<TextBlock> list) {
        if (list.size() == 0) {
            return;
        }
        boolean ok = false;
        if (!mIsTranslating) {
            ok = true;
        }
        if (mIndex > mCurrent + 4) {
            mCurrent = mIndex;
            ok = true;
        }
        if (ok) {
            mIsTranslating = true;
            final List<String> phrases = new ArrayList<>();
            final HashMap<String, TextBlock> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                TextBlock block = list.valueAt(i);
                String value = block.getValue();
                if (!TextUtils.isEmpty(value)) {
                    phrases.add(value);
                    map.put(value + String.valueOf(phrases.size() - 1), block);
                }
            }
            TranslateObservable.getTranslateObservable(mListener.to(), phrases)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<TranslationsListResponse>() {
                        @Override
                        public void call(TranslationsListResponse translationsListResponse) {
                            mGraphicOverlay.clear();
                            List<TranslationsResource> results = translationsListResponse.getTranslations();
                            if (results.size() == phrases.size()) {
                                for (int i = 0; i < phrases.size(); i++) {
                                    TextBlock block = map.get(phrases.get(i) + String.valueOf(i));
                                    if (block != null) {
                                        OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, block, results.get(i).getTranslatedText());
                                        mGraphicOverlay.add(graphic);
                                    }
                                }
                            } else {
                                for (TextBlock block : map.values()) {
                                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, block, block.getValue());
                                    mGraphicOverlay.add(graphic);
                                }
                            }
                            mIsTranslating = false;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mIsTranslating = false;
                            throwable.printStackTrace();
                        }
                    });
        }
    }
}
