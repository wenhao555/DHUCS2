package com.example.dhucs.listeners;

public interface IBaseView
{
    int getLayoutId();

    void initData();

    void initView();

    void requestCommit();

    void requestDelete();

}
