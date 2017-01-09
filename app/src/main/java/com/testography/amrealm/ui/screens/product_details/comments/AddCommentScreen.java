package com.testography.amrealm.ui.screens.product_details.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.testography.amrealm.R;
import com.testography.amrealm.data.network.res.CommentRes;
import com.testography.amrealm.data.storage.realm.ProductRealm;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.scopes.DaggerScope;
import com.testography.amrealm.flow.AbstractScreen;
import com.testography.amrealm.flow.Screen;
import com.testography.amrealm.mvp.models.DetailModel;
import com.testography.amrealm.mvp.presenters.AbstractPresenter;
import com.testography.amrealm.mvp.presenters.MenuItemHolder;
import com.testography.amrealm.ui.screens.product_details.DetailScreen;

import dagger.Provides;
import flow.TreeKey;
import mortar.MortarScope;

@Screen(R.layout.screen_add_comment)
public class AddCommentScreen extends AbstractScreen<DetailScreen.Component>
        implements TreeKey {

    private final ProductRealm mProductRealm;

    public AddCommentScreen(ProductRealm productRealm) {
        mProductRealm = productRealm;
    }

    @NonNull
    @Override
    public Object getParentKey() {
        return new CommentsScreen(mProductRealm);
    }

    //region ==================== DI ===================

    @Override
    public Object createScreenComponent(DetailScreen.Component parentComponent) {
        return DaggerAddCommentScreen_Component.builder()
                .component(parentComponent)
                .module(new AddCommentScreen.Module())
                .build();
    }

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(AddCommentScreen.class)
        AddCommentPresenter provideAddCommentPresenter() {
            return new AddCommentPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = DetailScreen.Component.class, modules =
            Module.class)
    @DaggerScope(AddCommentScreen.class)
    public interface Component {
        void inject(AddCommentPresenter presenter);

        void inject(AddCommentView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class AddCommentPresenter extends AbstractPresenter<AddCommentView,
            DetailModel> {

        private final ProductRealm mProduct;

        public AddCommentPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Rate the Product")
                    .addAction(new MenuItemHolder("Save comment", R.drawable
                            .ic_done_orange_24dp, item -> {
                        getRootView().showMessage("Save Comment");
                        return true;
                    }))
                    .setBackArrow(true)
                    .build();
        }

        @Override
        protected void initFab() {
            mRootPresenter.newFabBuilder()
                    .setVisible(View.GONE)
                    .setOnClickListener(null)
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((AddCommentScreen.Component) scope.getService(DaggerService
                    .SERVICE_NAME)).inject(this);
        }

        public void clickOnSaveComment(CommentRes commentRes) {
            Context context = getView().getContext();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(context.getString(R.string.add_comment))
                    .setPositiveButton(context.getString(R.string.yes),
                            (dialog, which) -> {
                                mModel.saveComment(mProduct.getId(), commentRes);
                                getView().mCommentEt.setText("");
                                getView().mCommentEt.clearFocus();
                                getView().mRating.setRating(0);
                            })
                    .setNegativeButton(R.string.no,
                            (dialog, which) -> {
                            })
                    .show();
            //Flow.get(getView()).goBack();
        }
    }

    //endregion
}
