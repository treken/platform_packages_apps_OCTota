/*
 * Copyright (C) 2013 OTAPlatform
 * Copyright (C) 2014 ProBAM ROM
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

package com.oct.updater.updater;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;

import com.oct.updater.R;
import com.oct.updater.updater.Updater.PackageInfo;

public class GooPackage implements PackageInfo, Serializable {

    private String md5 = null;
    private String incremental_md5 = null;
    private String filename = null;
    private String incremental_filename = null;
    private String path = null;
    private String incremental_path = null;
    private String folder = null;
    private long version = -1;
    private int id;
    private String type;
    private String description;
    private int is_flashable;
    private long modified;
    private int downloads;
    private int status;
    private String additional_info;
    private String short_url;
    private int developer_id;
    private String developerid;
    private String board;
    private String rom;
    private int gapps_package;
    private int incremental_file;
    private boolean isDelta = false;

    public GooPackage(JSONObject result, int previousVersion) {
        if (result == null) {
            version = -1;
        } else {

                JSONObject update = null;
                try {
                    // Getting JSON Array node
                    update = result.getJSONArray("list").getJSONObject(0);
                } catch (JSONException ex) {
                    update = result;
                }

                developerid = update.optString("ro_developerid");
                board = update.optString("ro_board");
                rom = update.optString("ro_rom");
                version = update.optInt("ro_version");
                id = update.optInt("id");
                filename = update.optString("filename");
                if(isGapps()){
                	path = "http://garr.dl.sourceforge.net/project/teamoctos/gapps/" + filename;
                }else{
                	path = "http://garr.dl.sourceforge.net/" + update.optString("path");
                }
                
                folder = update.optString("folder");
                md5 = update.optString("md5");
                type = update.optString("type");
                description = update.optString("description");
                is_flashable = update.optInt("is_flashable");
                modified = update.optLong("modified");
                downloads = update.optInt("downloads");
                status = update.optInt("status");
                additional_info = update.optString("additional_info");
                short_url = update.optString("short_url");
                developer_id = update.optInt("developer_id");
                gapps_package = update.optInt("gapps_package");
                incremental_file = update.optInt("incremental_file");

                if (version == 0) {
                    String[] split = filename.split("_");        
	                String getVer = split[2].replaceAll("\\D", "");
	                version = Integer.parseInt(getVer);
                }
	  
        }
    }

    @Override
    public boolean isDelta() {
        return isDelta;
    }

    @Override
    public String getDeltaFilename() {
        return incremental_filename;
    }

    @Override
    public String getDeltaPath() {
        return incremental_path;
    }

    @Override
    public String getDeltaMd5() {
        return incremental_md5;
    }

    @Override
    public String getMessage(Context context) {
        Resources res = context.getResources();
        return res.getString(R.string.goo_package_description, new Object[] {
                filename, md5, folder, description });
    }

    @Override
    public String getMd5() {
        return md5;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getFolder() {
        return folder;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getIs_flashable() {
        return is_flashable;
    }

    public long getModified() {
        return modified;
    }

    public int getDownloads() {
        return downloads;
    }

    public int getStatus() {
        return status;
    }

    public String getAdditional_info() {
        return additional_info;
    }

    public String getShort_url() {
        return short_url;
    }

    public int getDeveloper_id() {
        return developer_id;
    }

    public String getDeveloperid() {
        return developerid;
    }

    public String getBoard() {
        return board;
    }

    public String getRom() {
        return rom;
    }

    public int getGapps_package() {
        return gapps_package;
    }

    public int getIncremental_file() {
        return incremental_file;
    }

    @Override
    public boolean isGapps() {
        return filename != null && filename.indexOf("gapps") >= 0;
    }
}
