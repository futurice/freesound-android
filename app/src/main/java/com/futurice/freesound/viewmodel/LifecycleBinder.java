package com.futurice.freesound.viewmodel;

interface LifecycleBinder extends Binder {

    void onCreate();

    void onResume();

    void onPause();

    void onDestroyView();

    void onDestroy();

}
