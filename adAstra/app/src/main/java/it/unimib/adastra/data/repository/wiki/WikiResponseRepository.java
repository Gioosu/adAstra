package it.unimib.adastra.data.repository.wiki;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.adastra.data.source.wiki.BaseWikiLocalDataSource;
import it.unimib.adastra.data.source.wiki.BaseWikiRemoteDataSource;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.wiki.WikiObj;

public class WikiResponseRepository implements IWikiRepository, WikiResponseCallback {
    private static final String TAG = WikiResponseRepository.class.getSimpleName();
    private final BaseWikiLocalDataSource wikiLocalDataSource;
    private final BaseWikiRemoteDataSource wikiRemoteDataSource;
    private MutableLiveData<Result> wikiMutableLiveData;

    public WikiResponseRepository(BaseWikiLocalDataSource baseWikiLocalDataSource, BaseWikiRemoteDataSource baseWikiRemoteDataSource) {
        this.wikiLocalDataSource = baseWikiLocalDataSource;
        this.wikiRemoteDataSource = baseWikiRemoteDataSource;
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
    public void onSuccessFromLocal(List<WikiObj> wikiObjs) {
        Result.WikiResponseSuccess result = new Result.WikiResponseSuccess(wikiObjs);
        wikiMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(String query, String language, boolean isDBEmpty) {
        wikiRemoteDataSource.getInfo(query, language, isDBEmpty);
    }

    @Override
    public void onFailureFromLocal(String message) {
        Result.Error result = new Result.Error(message);
        wikiMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemote(String query, List<WikiObj> wikiObjs, boolean isDBEmpty) {
        wikiLocalDataSource.updateWiki(query, wikiObjs, isDBEmpty);
    }

    @Override
    public void onFailureFromRemote(String message) {
        Result.Error result = new Result.Error(message);
        wikiMutableLiveData.postValue(result);
    }
}
