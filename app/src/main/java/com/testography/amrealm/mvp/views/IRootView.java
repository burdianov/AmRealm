package com.testography.amrealm.mvp.views;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import com.testography.amrealm.data.storage.dto.UserInfoDto;

public interface IRootView extends IView {
    void showMessage(String message);
    void showError(Throwable e);

    void showLoad();
    void hideLoad();

    @Nullable
    IView getCurrentScreen();

    void initDrawer(UserInfoDto userInfoDto);

    FloatingActionButton getFab();
}
