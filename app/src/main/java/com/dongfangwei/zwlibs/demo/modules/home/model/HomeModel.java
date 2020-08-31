package com.dongfangwei.zwlibs.demo.modules.home.model;

import com.dongfangwei.zwlibs.demo.modules.home.presenter.IHomePresenter;
import com.dongfangwei.zwlibs.demo.modules.main.entity.HttpParameters;
import com.dongfangwei.zwlibs.demo.publics.util.ThreadManage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeModel implements IHomeModel {
    @Override
    public void getData(final HttpParameters parameters, final IHomePresenter presenter) {
        Observable.just(parameters)
                .subscribeOn(Schedulers.io())
                .map(new Function<HttpParameters, List<String>>() {
                    @Override
                    public List<String> apply(HttpParameters parameters) throws Exception {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int page = (int) parameters.get("page");
                        if (page == 1) {
//                            throw new Exception("出错啦");
                            return new ArrayList<>();
                        }
                        int size = (int) parameters.get("size");
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            list.add(String.format(Locale.getDefault(), "Home - %d - %d", page, i));
                        }
                        return list;
                    }
                }).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<String> list) {
                        presenter.getDataSuccess(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        presenter.getDataError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroy() {

    }
}
