package com.testography.amrealm.data.managers;

import android.content.Context;
import android.support.annotation.Nullable;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.testography.amrealm.App;
import com.testography.amrealm.R;
import com.testography.amrealm.data.network.RestCallTransformer;
import com.testography.amrealm.data.network.RestService;
import com.testography.amrealm.data.network.res.CommentRes;
import com.testography.amrealm.data.network.res.ProductRes;
import com.testography.amrealm.data.storage.dto.CommentDto;
import com.testography.amrealm.data.storage.dto.ProductDto;
import com.testography.amrealm.data.storage.dto.ProductLocalInfo;
import com.testography.amrealm.data.storage.dto.UserAddressDto;
import com.testography.amrealm.data.storage.realm.ProductRealm;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.di.components.DaggerDataManagerComponent;
import com.testography.amrealm.di.components.DataManagerComponent;
import com.testography.amrealm.di.modules.LocalModule;
import com.testography.amrealm.di.modules.NetworkModule;
import com.testography.amrealm.utils.ConstantsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.testography.amrealm.data.managers.PreferencesManager.PROFILE_AVATAR_KEY;
import static com.testography.amrealm.data.managers.PreferencesManager.PROFILE_FULL_NAME_KEY;
import static com.testography.amrealm.data.managers.PreferencesManager.PROFILE_PHONE_KEY;

public class DataManager {

    private static DataManager sInstance = new DataManager();
    public static final String TAG = "DataManager";

    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    RestService mRestService;
    @Inject
    Retrofit mRetrofit;
    @Inject
    RealmManager mRealmManager;
    @Inject
    Context mAppContext;

    private Map<String, String> mUserProfileInfo;
    private List<UserAddressDto> mUserAddresses;
    private Map<String, Boolean> mUserSettings;

    public static DataManager getInstance() {
        return sInstance;
    }

    private DataManager() {
        DataManagerComponent component = DaggerService.getComponent
                (DataManagerComponent.class);
        if (component == null) {
            component = DaggerDataManagerComponent.builder()
                    .appComponent(App.getAppComponent())
                    .localModule(new LocalModule())
                    .networkModule(new NetworkModule())
                    .build();
            DaggerService.registerComponent(DataManagerComponent.class, component);
        }
        component.inject(this);

        generateMockData();
        initUserData();
    }

    private void initUserData() {
        mUserProfileInfo = new HashMap<>();
        mUserAddresses = new ArrayList<>();

        mUserProfileInfo = mPreferencesManager.getUserProfileInfo();
        if (mUserProfileInfo.get(PROFILE_FULL_NAME_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_FULL_NAME_KEY, "Hulk Hogan");
        }
        if (mUserProfileInfo.get(PROFILE_AVATAR_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_AVATAR_KEY,
                    "http://a1.files.biography.com/image/upload/c_fill,cs_srgb," +
                            "dpr_1.0,g_face,h_300,q_80,w_300/MTIwNjA4NjM0MDQyNzQ2Mzgw.jpg");
        }
        if (mUserProfileInfo.get(PROFILE_PHONE_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_PHONE_KEY, "+7(917)971-38-27");
        }
        List<UserAddressDto> userAddresses = getPreferencesManager().getUserAddresses();

        if (userAddresses == null) {
            UserAddressDto userAddress;
            userAddress = new UserAddressDto(1, "Home", "Airport Road", "24", "56",
                    9, "Beware of crazy dogs");
            mUserAddresses.add(userAddress);
            userAddress = new UserAddressDto(2, "Work", "Central Park", "123", "67",
                    2, "In the middle of nowhere");
            mUserAddresses.add(userAddress);
        } else {
            mUserAddresses = userAddresses;
        }
        mUserSettings = getPreferencesManager().getUserSettings();
    }

    public List<String> deletedPoducts = new ArrayList<>();

    @RxLogObservable
    public Observable<ProductRealm> getProductsObsFromNetwork() {
        return mRestService.getProductResObs(mPreferencesManager.getLastProductUpdate())
                .compose(new RestCallTransformer<List<ProductRes>>()) //
                // трансформируем response, выбрасываем ApiError в случае ошибки
                .flatMap(Observable::from) // преобразуем список товаров в последовательность товаров
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(productRes -> {
                    if (!productRes.isActive()) {
                        mRealmManager.deleteFromRealm(ProductRealm.class,
                                productRes.getId()); // удалить из базы данных если не активен
                        deletedPoducts.add(productRes.getId());
                    }
                })
                .distinct(ProductRes::getRemoteId)
                .filter(ProductRes::isActive) // пропускаем только активные товары
                .doOnNext(productRes -> mRealmManager.saveProductResponseToRealm
                        (productRes)) // сохраняем на диск только активные товары
                .flatMap(productRes -> Observable.empty());
    }

    public void saveCommentToNetworkAndRealm(String productId,
                                             CommentRes commentRes) {
        mRestService.uploadComment(productId, commentRes)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<CommentRes>() {
                    @Override
                    public void onCompleted() {

                }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CommentRes commentRes) {
                        mRealmManager.saveNewCommentToRealm(productId, commentRes);
                    }
                });
    }

    private void deleteFromDb(ProductRes productRes) {
        mPreferencesManager.deleteProduct(productRes);
    }

    @Nullable
    public List<ProductDto> fromDisk() {
        List<ProductDto> productDtoList = mPreferencesManager.getProductList();

        if (productDtoList == null) {
            productDtoList = generateMockData();
            mPreferencesManager.generateProductsMockData(productDtoList);
        }
        return productDtoList;
    }

    private void saveOnDisk(ProductRes productRes) {
//        mPreferencesManager.updateOrInsertProduct(productRes);
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Context getAppContext() {
        return mAppContext;
    }

    public boolean isAuthUser() {
        return !mPreferencesManager.getAuthToken().equals(ConstantsManager
                .INVALID_TOKEN);
    }

    public void loginUser(String email, String password) {
        // TODO: 23-Oct-16 implement user authentication
    }

    public void updateProduct(ProductDto product) {
        // TODO: 28-Oct-16 update product count or other property and save to DB
    }

    private String getResVal(int resourceId) {
        return getAppContext().getString(resourceId);
    }

    public Map<String, String> getUserProfileInfo() {
        return mUserProfileInfo;
    }

    public List<UserAddressDto> getUserAddresses() {
        return mUserAddresses;
    }

    public Map<String, Boolean> getUserSettings() {
        return mUserSettings;
    }

    public void saveProfileInfo(String name, String phone, String avatar) {
        mUserProfileInfo.put(PROFILE_FULL_NAME_KEY, name);
        mUserProfileInfo.put(PROFILE_AVATAR_KEY, avatar);
        mUserProfileInfo.put(PROFILE_PHONE_KEY, phone);
        mPreferencesManager.saveProfileInfo(mUserProfileInfo);
    }

    public void saveSetting(String notificationKey, boolean isChecked) {
        mPreferencesManager.saveSetting(notificationKey, isChecked);
        mUserSettings.put(notificationKey, isChecked);
    }

    public void updateOrInsertAddress(UserAddressDto addressDto) {
        if (mUserAddresses.contains(addressDto)) {
            mUserAddresses.set(mUserAddresses.indexOf(addressDto), addressDto);
        } else {
            mUserAddresses.add(0, addressDto);
        }
        mPreferencesManager.saveUserAddresses(mUserAddresses);
    }

    public void removeAddress(UserAddressDto addressDto) {
        if (mUserAddresses.contains(addressDto)) {
            mUserAddresses.remove(mUserAddresses.indexOf(addressDto));
            getPreferencesManager().saveUserAddresses(mUserAddresses);
        }
    }

    private List<ProductDto> generateMockData() {
        List<ProductDto> productDtoList = getPreferencesManager().getProductList();
        List<CommentDto> commentList = new ArrayList<>();
//        commentList.add(new CommentDto());

        if (productDtoList == null) {
            productDtoList = new ArrayList<>();
            productDtoList.add(new ProductDto(1, "disk " +
                    getResVal(R.string.product_name_1),
                    getResVal(R.string.product_url_1),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(2, "disk " +
                    getResVal(R.string.product_name_2),
                    getResVal(R.string.product_url_2),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(3, "disk " +
                    getResVal(R.string.product_name_3),
                    getResVal(R.string.product_url_3),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(4, "disk " +
                    getResVal(R.string.product_name_4),
                    getResVal(R.string.product_url_4),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(5, "disk " +
                    getResVal(R.string.product_name_5),
                    getResVal(R.string.product_url_5),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(6, "disk " +
                    getResVal(R.string.product_name_6),
                    getResVal(R.string.product_url_6),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(7, "disk " +
                    getResVal(R.string.product_name_7),
                    getResVal(R.string.product_url_7),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(8, "disk " +
                    getResVal(R.string.product_name_8),
                    getResVal(R.string.product_url_8),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(9, "disk " +
                    getResVal(R.string.product_name_9),
                    getResVal(R.string.product_url_9),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(10, "disk " +
                    getResVal(R.string.product_name_10),
                    getResVal(R.string.product_url_10),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
        }
        return productDtoList;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public Observable<ProductLocalInfo> getProductLocalInfoObs(ProductRes productRes) {
        return Observable.just(getPreferencesManager().getLocalInfo(productRes
                .getRemoteId()))
                .flatMap(productLocalInfo ->
                        productLocalInfo == null ?
                                Observable.just(new ProductLocalInfo()) :
                                Observable.just(productLocalInfo)
                );
    }

    public Observable<ProductRealm> getProductFromRealm() {
        return mRealmManager.getAllProductsFromRealm();
    }
}
