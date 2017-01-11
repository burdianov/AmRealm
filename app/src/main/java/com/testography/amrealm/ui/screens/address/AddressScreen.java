package com.testography.amrealm.ui.screens.address;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.testography.amrealm.R;
import com.testography.amrealm.data.storage.dto.UserAddressDto;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.scopes.AddressScope;
import com.testography.amrealm.flow.AbstractScreen;
import com.testography.amrealm.flow.Screen;
import com.testography.amrealm.mvp.models.AccountModel;
import com.testography.amrealm.mvp.presenters.AbstractPresenter;
import com.testography.amrealm.mvp.presenters.IAddressPresenter;
import com.testography.amrealm.ui.screens.account.AccountScreen;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import flow.TreeKey;
import mortar.MortarScope;

@Screen(R.layout.screen_add_address)
public class AddressScreen extends AbstractScreen<AccountScreen.Component>
        implements TreeKey {

    @Nullable
    private UserAddressDto mAddressDto;

    public AddressScreen(@Nullable UserAddressDto addressDto) {
        mAddressDto = addressDto;
        if (mAddressDto == null) {
            mAddressDto = new UserAddressDto();
        }
    }

    //region ==================== Flow & Mortar ===================

    @Override
    public boolean equals(Object o) {
        if (mAddressDto != null) {
            return o instanceof AddressScreen && mAddressDto.equals((
                    (AddressScreen) o).mAddressDto);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return mAddressDto != null ? mAddressDto.hashCode() : super.hashCode();
    }

    @Override
    public Object createScreenComponent(AccountScreen.Component parentComponent) {
        return DaggerAddressScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @Override
    public Object getParentKey() {
        return new AccountScreen();
    }

    //endregion

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @AddressScope
        AddressPresenter provideAddressPresenter() {
            return new AddressPresenter();
        }
    }

    @dagger.Component(dependencies = AccountScreen.Component.class, modules =
            Module.class)
    @AddressScope
    public interface Component {
        void inject(AddressPresenter presenter);
        void inject(AddressView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class AddressPresenter extends AbstractPresenter<AddressView, AccountModel>
            implements IAddressPresenter {

        @Inject
        AccountModel mAccountModel;

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Add/Edit Address")
                    .setBackArrow(true)
                    .build();
        }

        @Override
        protected void initFab() {
            // empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            // empty
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (mAddressDto != null && getView() != null) {
                getView().initView(mAddressDto);
            }
        }

        @Override
        public void clickOnAddAddress() {
            // TODO: 29-Nov-16 save address in model
            if (getView() != null) {
                UserAddressDto userAddress = getView().getUserAddress();

                mAddressDto.setId(userAddress.getId());
                mAddressDto.setName(userAddress.getName());
                mAddressDto.setStreet(userAddress.getStreet());
                mAddressDto.setHouse(userAddress.getHouse());
                mAddressDto.setApartment(userAddress.getApartment());
                mAddressDto.setFloor(userAddress.getFloor());
                mAddressDto.setComment(userAddress.getComment());
                mAddressDto.setFavorite(userAddress.isFavorite());
                mAccountModel.updateOrInsertAddress(mAddressDto);

                Flow.get(getView()).goBack();
            }
        }
    }

    //endregion
}
