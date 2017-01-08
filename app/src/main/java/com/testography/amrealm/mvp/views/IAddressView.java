package com.testography.amrealm.mvp.views;

import com.testography.amrealm.data.storage.dto.UserAddressDto;

public interface IAddressView extends IView {
    void showInputError();
    UserAddressDto getUserAddress();
}
