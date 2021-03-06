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

package com.oct.updater.updater;

import android.content.Context;

import com.oct.updater.updater.Updater.PackageInfo;

public class CancelPackage implements PackageInfo {

    @Override
    public boolean isDelta() {
        return false;
    }

    @Override
    public String getDeltaFilename() {
        return null;
    }

    @Override
    public String getDeltaPath() {
        return null;
    }

    @Override
    public String getDeltaMd5() {
        return null;
    }

    @Override
    public String getMd5() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getFolder() {
        return null;
    }

    @Override
    public String getMessage(Context context) {
        return null;
    }

    @Override
    public long getVersion() {
        return 0;
    }

    @Override
    public boolean isGapps() {
        return false;
    }

}
