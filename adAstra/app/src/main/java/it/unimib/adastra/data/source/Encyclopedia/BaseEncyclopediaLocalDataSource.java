package it.unimib.adastra.data.source.Encyclopedia;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.Result;

public abstract class BaseEncyclopediaLocalDataSource {
    public abstract void getEncyclopediaData(String query);
}
