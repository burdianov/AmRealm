package com.testography.amrealm.ui.screens.address;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.testography.amrealm.R;
import com.testography.amrealm.data.storage.dto.UserAddressDto;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.mvp.views.AbstractView;
import com.testography.amrealm.mvp.views.IAddressView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressView extends AbstractView<AddressScreen.AddressPresenter>
        implements IAddressView {

    @BindView(R.id.address_name_et)
    EditText mAddressNameEt;
    @BindView(R.id.street_et)
    EditText mStreetEt;
    @BindView(R.id.number_building_et)
    EditText mNumberBuildingEt;
    @BindView(R.id.number_apartment_et)
    EditText mNumberApartmentEt;
    @BindView(R.id.number_floor_et)
    EditText mNumberFloor;
    @BindView(R.id.comment_et)
    EditText mCommentEt;
    @BindView(R.id.add_btn)
    Button mAddBtn;

    @Inject
    AddressScreen.AddressPresenter mPresenter;

    private int mAddressId;

    public AddressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            DaggerService.<AddressScreen.Component>getDaggerComponent(context)
                    .inject(this);
        }
    }

    @Override
    protected void initDagger(Context context) {
        // empty
    }

    //region ==================== flow view lifecycle callbacks ===================

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.enter_animation));

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mPresenter.takeView(this);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
        }
    }

    //endregion

    //region ==================== IAddressView ===================

    public void initView(@Nullable UserAddressDto address) {
        if (address != null) {
            mAddressId = address.getId();
            mAddressNameEt.setText(address.getName());
            mStreetEt.setText(address.getStreet());
            mNumberBuildingEt.setText(address.getHouse());
            mNumberApartmentEt.setText(address.getApartment());
            mNumberFloor.setText(String.valueOf(address.getFloor()));
            mCommentEt.setText(address.getComment());
            mAddBtn.setText(R.string.save);
        }
    }

    @Override
    public void showInputError() {
        // TODO: 29-Nov-16 implement this
    }

    @Override
    public UserAddressDto getUserAddress() {
        return new UserAddressDto(mAddressId,
                validateString(mAddressNameEt),
                validateString(mStreetEt),
                validateString(mNumberBuildingEt),
                validateString(mNumberApartmentEt),
                validateInteger(mNumberFloor),
                validateString(mCommentEt)
        );
    }

    private String validateString(EditText editText) {
        return editText.getText().toString().equals("") ?
                getContext().getString(R.string.blank) :
                editText.getText().toString();
    }

    private int validateInteger(EditText editText) {
        return editText.getText().toString().equals("") ? 0 :
                Integer.parseInt(editText.getText().toString());
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    //endregion

    //region ==================== Events ===================

    @OnClick(R.id.add_btn)
    void AddAddress() {
        mPresenter.clickOnAddAddress();
    }

    //endregion
}
