package com.testography.amrealm.data.managers;

import com.testography.amrealm.data.network.req.CommentReq;
import com.testography.amrealm.data.network.res.CommentRes;
import com.testography.amrealm.data.network.res.ProductRes;
import com.testography.amrealm.data.storage.realm.CommentRealm;
import com.testography.amrealm.data.storage.realm.ProductRealm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

public class RealmManager {
    private Realm mRealmInstance;

    public void saveNewCommentToRealm(String productId, CommentReq
            commentReq) {
        Realm realm = Realm.getDefaultInstance();
        ProductRealm product = realm.where(ProductRealm.class)
                .equalTo("id", productId)
                .findAll()
                .get(0);
        CommentRealm commentRealm = new CommentRealm(commentReq);
        realm.executeTransaction(realm1 -> product.addComment(commentRealm));
        realm.close();
    }

    public void saveProductResponseToRealm(ProductRes productRes) {
        Realm realm = Realm.getDefaultInstance();

        ProductRealm productRealm = new ProductRealm(productRes);
        if (!productRes.getComments().isEmpty()) {
            Observable.from(productRes.getComments())
                    .doOnNext(commentRes -> {
                        if (!commentRes.isActive()) {
                            deleteFromRealm(CommentRealm.class, commentRes.getId());
                        }
                    })
                    .filter(CommentRes::isActive)
                    .map(CommentRealm::new) // преобразовываем в RealmObject
                    .subscribe(commentRealm -> productRealm.getCommentsRealm()
                            .add(commentRealm)); // добавляем в ProductRealm

        }
        // добавляет или обновляем продукт в транзакции
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(productRealm));
        realm.close();
    }

    public void deleteFromRealm(Class<? extends RealmObject> entityRealmClass,
                                String id) {
        Realm realm = Realm.getDefaultInstance();
        RealmObject entity = realm
                .where(entityRealmClass).equalTo("id", id).findFirst();
        if (entity != null) {
            realm.executeTransaction(realm1 -> entity.deleteFromRealm());
            realm.close();
        }
    }

    public Observable<ProductRealm> getAllProductsFromRealm() {
        RealmResults<ProductRealm> managedProduct = getQueryRealmInstance()
                .where(ProductRealm.class).findAllAsync();
        return managedProduct
                .asObservable() // получаем RealmResult как Observable
                .filter(RealmResults::isLoaded) // получаем только загруженные результаты (hot Observable)
                //.first() // convert a hot observable into a cold one
                .flatMap(Observable::from); // преобразуем в Observable<ProductRealm>
    }

    private Realm getQueryRealmInstance() {
        if (mRealmInstance == null || mRealmInstance.isClosed()) {
            mRealmInstance = Realm.getDefaultInstance();
        }
        return mRealmInstance;
    }
}
