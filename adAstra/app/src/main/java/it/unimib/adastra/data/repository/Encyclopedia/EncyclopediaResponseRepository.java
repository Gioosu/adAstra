package it.unimib.adastra.data.repository.Encyclopedia;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.adastra.data.source.Encyclopedia.BaseEncyclopediaLocalDataSource;
import it.unimib.adastra.data.source.Encyclopedia.BaseEncyclopediaRemoteDataSource;
import it.unimib.adastra.model.Encyclopedia.Planet;
import it.unimib.adastra.model.Result;

public class EncyclopediaResponseRepository implements IEncyclopediaRepository, EncyclopediaResponseCallback {
    private static final String TAG = EncyclopediaResponseRepository.class.getSimpleName();
    private final BaseEncyclopediaLocalDataSource encyclopediaLocalDataSource;
    private final BaseEncyclopediaRemoteDataSource encyclopediaRemoteDataSource;
    private MutableLiveData<Result> encyclopediaMutableLiveData;

    public EncyclopediaResponseRepository(BaseEncyclopediaLocalDataSource BaseEncyclopediaLocalDataSource, BaseEncyclopediaRemoteDataSource BaseEncyclopediaRemoteDataSource) {
        this.encyclopediaLocalDataSource = BaseEncyclopediaLocalDataSource;
        this.encyclopediaRemoteDataSource = BaseEncyclopediaRemoteDataSource;
        this.encyclopediaMutableLiveData = new MutableLiveData<>();
        this.encyclopediaRemoteDataSource.setEncyclopediaCallback(this);
        this.encyclopediaLocalDataSource.setEncyclopediaCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchEncyclopediaData(String query, String language) {
        encyclopediaLocalDataSource.getEncyclopediaData(query, language);

        return encyclopediaMutableLiveData;
    }

    @Override
    public void onSuccessFromLocal(List<Planet> planets) {
        Result.EncyclopediaResponseSuccess result = new Result.EncyclopediaResponseSuccess(planets);
        encyclopediaMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(String query, String language, boolean isDBEmpty) {
        encyclopediaRemoteDataSource.getEncyclopediaData(query, language, isDBEmpty);
    }

    @Override
    public void onFailureFromLocal(String message) {
        Result.Error result = new Result.Error(message);
        encyclopediaMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemote(List<Planet> planets, boolean isDBEmpty) {
        encyclopediaLocalDataSource.updateEncyclopedia(planets, isDBEmpty);
    }

    @Override
    public void onFailureFromRemote(String message) {
        Result.Error result = new Result.Error(message);
        encyclopediaMutableLiveData.postValue(result);
    }
}
