package it.unimib.adastra.data.repository.Encyclopedia;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.data.source.Encyclopedia.BaseEncyclopediaLocalDataSource;
import it.unimib.adastra.data.source.Encyclopedia.BaseEncyclopediaRemoteDataSource;
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
    }

    @Override
    public MutableLiveData<Result> fetchEncyclopediaData(String query) {
        encyclopediaLocalDataSource.getEncyclopediaData(query);
        return encyclopediaMutableLiveData;
    }
}
