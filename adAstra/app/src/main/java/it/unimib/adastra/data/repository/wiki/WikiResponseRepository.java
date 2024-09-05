package it.unimib.adastra.data.repository.wiki;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.adastra.data.source.wiki.BaseWikiLocalDataSource;
import it.unimib.adastra.data.source.wiki.BaseWikiRemoteDataSource;
import it.unimib.adastra.model.wiki.Planet;
import it.unimib.adastra.model.Result;

public class WikiResponseRepository implements IWikiRepository, WikiResponseCallback {
    private static final String TAG = WikiResponseRepository.class.getSimpleName();
    private final BaseWikiLocalDataSource wikiLocalDataSource;
    private final BaseWikiRemoteDataSource wikiRemoteDataSource;
    private MutableLiveData<Result> wikiMutableLiveData;

    public WikiResponseRepository(BaseWikiLocalDataSource BaseWikiLocalDataSource, BaseWikiRemoteDataSource BaseWikiRemoteDataSource) {
        this.wikiLocalDataSource = BaseWikiLocalDataSource;
        this.wikiRemoteDataSource = BaseWikiRemoteDataSource;
        this.wikiMutableLiveData = new MutableLiveData<>();
        this.wikiRemoteDataSource.setWikiCallback(this);
        this.wikiLocalDataSource.setWikiCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchWikiData(String query, String language) {
        wikiLocalDataSource.getWikiData(query, language);

        return wikiMutableLiveData;
    }

    @Override
    public void onSuccessFromLocal(List<Planet> planets) {
        Result.WikiResponseSuccess result = new Result.WikiResponseSuccess(planets);
        wikiMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(String query, String language, boolean isDBEmpty) {
        wikiRemoteDataSource.getWikiData(query, language, isDBEmpty);
    }

    @Override
    public void onFailureFromLocal(String message) {
        Result.Error result = new Result.Error(message);
        wikiMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemote(List<Planet> planets, boolean isDBEmpty) {
        wikiLocalDataSource.updateWiki(planets, isDBEmpty);
    }

    @Override
    public void onFailureFromRemote(String message) {
        Result.Error result = new Result.Error(message);
        wikiMutableLiveData.postValue(result);
    }
}
