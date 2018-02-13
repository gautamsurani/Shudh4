package com.shudh4sure.shopping.utils;

import com.google.android.gms.common.api.Status;

/*
 * Created by Gautam on 13-02-2018.
 */

public interface LocationInterface {
    void onSuccess(String address, String City, String State);

    void onFailure();

    void onResolve(Status status);
}
