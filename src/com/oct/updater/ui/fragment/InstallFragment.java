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

package com.oct.updater.ui.fragment;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oct.updater.R;
import com.oct.updater.manager.ManagerFactory;
import com.oct.updater.ui.component.Item;
import com.oct.updater.ui.component.TouchInterceptor;
import com.oct.updater.util.Constants;
import com.oct.updater.util.FileItem;
import com.oct.updater.util.RequestFileActivity;
import com.oct.updater.util.RequestFileActivity.RequestFileCallback;

public class InstallFragment extends Fragment implements OnItemClickListener {

    private class FileItemsAdapter extends ArrayAdapter<FileItem> {

        public FileItemsAdapter() {
            super(mContext, R.layout.order_item, ManagerFactory.getFileManager(mContext)
                    .getFileItems());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout itemView;
            FileItem item = getItem(position);

            if (convertView == null) {
                itemView = new LinearLayout(getContext());
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                vi.inflate(R.layout.order_item, itemView, true);
            } else {
                itemView = (LinearLayout) convertView;
            }
            TextView title = (TextView) itemView.findViewById(R.id.title);
            TextView summary = (TextView) itemView.findViewById(R.id.summary);

            title.setText(item.getName());
            summary.setText(item.getPath());

            return itemView;
        }

    }

    private Context mContext;
    private Item mButtonFlash;
    private Item mButtonAdd;
    private TouchInterceptor mFileList;

    private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {

        public void drop(int from, int to) {
            if (from == to)
                return;
            List<FileItem> items = ManagerFactory.getFileManager(mContext).getFileItems();
            FileItem toMove = items.get(from);
            while (items.indexOf(toMove) != to) {
                int i = items.indexOf(toMove);
                Collections.swap(items, i, to < from ? i - 1 : i + 1);
            }
            ManagerFactory.getPreferencesManager(mContext).setFlashQueue(items);
            redrawItems();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mContext == null) {
            mContext = getActivity();
        }

        View view = inflater.inflate(R.layout.install_fragment, container, false);

        Constants.addRequestFileCallback(new RequestFileCallback() {

            @Override
            public void fileRequested(String filePath) {
                addItem(filePath);
            }
        });

        mButtonFlash = (Item) view.findViewById(R.id.button_flash_now);
        mButtonAdd = (Item) view.findViewById(R.id.button_add_zip);

        mButtonFlash.setOnItemClickListener(new Item.OnItemClickListener() {

            @Override
            public void onClick(int id) {
                ManagerFactory.getRebootManager(mContext).showRebootDialog(mContext);
            }
        });

        mButtonAdd.setOnItemClickListener(new Item.OnItemClickListener() {

            @Override
            public void onClick(int id) {
                Intent intent = new Intent(mContext, RequestFileActivity.class);
                mContext.startActivity(intent);
            }
        });

        mFileList = (TouchInterceptor) view.findViewById(R.id.file_list);
        mFileList.setOnItemClickListener(this);
        mFileList.setDropListener(mDropListener);

        redrawItems();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<FileItem> items = ManagerFactory.getFileManager(mContext).getFileItems();
        FileItem item = items.get(position);
        showInfoDialog(item);
    }

    private void redrawItems() {
        List<FileItem> items = ManagerFactory.getFileManager(mContext).getFileItems();
        mFileList.setAdapter(new FileItemsAdapter());
        mButtonFlash.setEnabled(items.size() > 0);
    }

    public void notifyDataChanged() {
        ((FileItemsAdapter)mFileList.getAdapter()).notifyDataSetChanged();
    }

    private void showInfoDialog(final FileItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(getResources().getString(R.string.alert_file_title,
                new Object[] { item.getName() }));

        String path = item.getPath();
        File file = new File(path);

        alert.setMessage(getResources().getString(
                R.string.alert_file_summary,
                new Object[] {
                        (file.getParent() == null ? "" : file.getParent()) + "/",
                        Constants.formatSize(file.length()),
                        new Date(file.lastModified()).toString() }));

        alert.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.alert_file_delete, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                ManagerFactory.getFileManager(mContext).removeItem(item);
                redrawItems();
            }
        });

        alert.show();
    }

    private void addItem(String filePath) {

        if (ManagerFactory.getFileManager(mContext).addItem(filePath)) {

            redrawItems();
        }
    }

}
