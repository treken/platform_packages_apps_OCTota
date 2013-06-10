/*
 * Copyright (C) 2013 OTAPlatform
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

package com.beerbong.otaplatform.ui.fragment;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beerbong.otaplatform.R;
import com.beerbong.otaplatform.manager.ManagerFactory;
import com.beerbong.otaplatform.ui.component.Item;
import com.beerbong.otaplatform.ui.component.Item.OnItemClickListener;
import com.beerbong.otaplatform.updater.CancelPackage;
import com.beerbong.otaplatform.updater.GappsUpdater;
import com.beerbong.otaplatform.updater.RomUpdater;
import com.beerbong.otaplatform.updater.Updater;
import com.beerbong.otaplatform.updater.Updater.PackageInfo;
import com.beerbong.otaplatform.util.Constants;

public class UpdateFragment extends Fragment implements RomUpdater.RomUpdaterListener,
        GappsUpdater.GappsUpdaterListener {

    private static PackageInfo mNewRom = null;
    private static boolean mStartup = true;

    private ProgressDialog mProgress;
    private ProgressBar mProgressBar;
    private RomUpdater mRomUpdater;
    private GappsUpdater mGappsUpdater;
    private Item mButtonCheckRom;
    private Item mButtonCheckGapps;
    private Item mButtonDownload;
    private Item mButtonDownloadDelta;
    private TextView mNoNewRom;
    private Map<Integer, View> mSeps;
    private boolean mRomCanUpdate = true;

    public UpdateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_fragment, container, false);

        mRomUpdater = Updater.getRomUpdater(getActivity(), this, false);

        mGappsUpdater = new GappsUpdater(getActivity(), this, false);

        mRomCanUpdate = mRomUpdater != null && mRomUpdater.canUpdate();

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mButtonCheckRom = (Item) view.findViewById(R.id.button_checkupdates);
        mButtonCheckGapps = (Item) view.findViewById(R.id.button_checkupdatesgapps);
        mButtonDownload = (Item) view.findViewById(R.id.button_download);
        mButtonDownloadDelta = (Item) view.findViewById(R.id.button_download_delta);
        mNoNewRom = (TextView) view.findViewById(R.id.no_new_version);

        mSeps = new HashMap<Integer, View>();
        mSeps.put(R.id.button_download_sep, view.findViewById(R.id.button_download_sep));
        mSeps.put(R.id.button_download_delta_sep, view.findViewById(R.id.button_download_delta_sep));

        mButtonCheckRom.setEnabled(mRomCanUpdate);
        mButtonCheckRom.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onClick(int id) {
                checkRom();
            }
        });

        mButtonCheckGapps.setEnabled(mGappsUpdater.canUpdate());
        mButtonCheckGapps.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onClick(int id) {
                checkGapps();
            }
        });

        mButtonDownload.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onClick(int id) {
                ManagerFactory.getFileManager(getActivity()).download(getActivity(),
                        mNewRom.getPath(), mNewRom.getFilename(), mNewRom.getMd5(), false,
                        Constants.DOWNLOADROM_NOTIFICATION_ID);
            }
        });

        mButtonDownloadDelta.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onClick(int id) {
                ManagerFactory.getFileManager(getActivity()).download(getActivity(),
                        mNewRom.getDeltaPath(), mNewRom.getDeltaFilename(), mNewRom.getDeltaMd5(),
                        true, Constants.DOWNLOADROM_NOTIFICATION_ID);
            }
        });

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.getExtras() != null
                && intent.getExtras().get("NOTIFICATION_ID") != null) {
            PackageInfo info = ManagerFactory.getFileManager(getActivity()).onNewIntent(
                    getActivity(), intent);
            if (!(info instanceof CancelPackage)) {
                mNewRom = info;
            }
        }

        if (mNewRom != null || !mStartup) {
            checkRomCompleted(mNewRom);
        } else if (mRomCanUpdate) {
            checkRom();
        }
        mStartup = false;

        if (savedInstanceState == null) {
            if (mRomCanUpdate) {
                getActivity().setTitle(
                        getActivity().getResources().getString(R.string.app_name_short,
                                new Object[] { mRomUpdater.getRomName() }));
            }
        }

        return view;
    }

    @Override
    public void checkGappsCompleted(long newVersion) {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
        mButtonCheckGapps.setEnabled(mGappsUpdater.canUpdate());
    }

    @Override
    public void checkRomCompleted(PackageInfo info) {
        mProgressBar.setVisibility(View.GONE);
        if (info == null) {
            mNewRom = null;
            mButtonDownload.setVisibility(View.GONE);
            mButtonDownloadDelta.setVisibility(View.GONE);
            mSeps.get(R.id.button_download_sep).setVisibility(View.GONE);
            mSeps.get(R.id.button_download_delta_sep).setVisibility(View.GONE);
            mNoNewRom.setVisibility(View.VISIBLE);
        } else {
            mNewRom = info;
            mNoNewRom.setVisibility(View.GONE);
            mButtonDownload.setVisibility(View.VISIBLE);
            mButtonDownloadDelta.setVisibility(info.isDelta() ? View.VISIBLE : View.GONE);
            mSeps.get(R.id.button_download_sep).setVisibility(View.VISIBLE);
            mSeps.get(R.id.button_download_delta_sep).setVisibility(info.isDelta() ? View.VISIBLE : View.GONE);
            mButtonDownload.setSummary(getActivity().getResources().getString(
                    R.string.rom_download_summary, new Object[] { info.getVersion() }));
        }
        mButtonCheckRom.setEnabled(mRomUpdater != null && mRomUpdater.canUpdate());
    }

    private void checkRom() {
        mNoNewRom.setVisibility(View.GONE);
        mButtonDownload.setVisibility(View.GONE);
        mButtonDownloadDelta.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mSeps.get(R.id.button_download_sep).setVisibility(View.GONE);
        mSeps.get(R.id.button_download_delta_sep).setVisibility(View.GONE);
        mRomUpdater.check();
    }

    private void checkGapps() {
        mProgress = ProgressDialog.show(getActivity(), null, getActivity().getResources()
                .getString(R.string.checking), true, true);
        mGappsUpdater.check();
    }

}
