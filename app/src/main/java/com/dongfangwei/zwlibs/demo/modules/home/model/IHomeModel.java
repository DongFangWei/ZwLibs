package com.dongfangwei.zwlibs.demo.modules.home.model;

import com.dongfangwei.zwlibs.demo.modules.home.presenter.IHomePresenter;
import com.dongfangwei.zwlibs.demo.modules.main.entity.HttpParameters;
import com.dongfangwei.zwlibs.demo.publics.base.BaseModel;

public interface IHomeModel extends BaseModel {
    void getData(HttpParameters parameters, IHomePresenter presenter);
}
