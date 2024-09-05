package it.unimib.adastra.data.repository.wiki;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.Result;

public interface IWikiRepository {

    MutableLiveData<Result> fetchWikiData(String query, String language);
}
