package it.unimib.adastra.ui.main.ISS;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.ISS.Result;

public class ISSViewModel extends ViewModel {
    private static final String TAG = ISSViewModel.class.getSimpleName();
    private final IISSPositionRepository iISSPositionRepository;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;
    private boolean firstLoading;
    private MutableLiveData<Result> issLiveData;

    public ISSViewModel(IISSPositionRepository issPositionRepository) {
        this.iISSPositionRepository = issPositionRepository;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    /**
     * Returns the LiveData object associated with the
     * news list to the Fragment/Activity.
     * @return The LiveData object associated with the news list.
     */
    public MutableLiveData<Result> getISSPosition(long timestamp) {
        if (issLiveData == null) {
            fetchISSPosition(timestamp);
        }

        return issLiveData;
    }

    /**
     * Updates the news status.
     * @param issPositionResponse The news to be updated.
     */
    public void updateISSPosition(ISSPositionResponse  issPositionResponse) {
        iISSPositionRepository.updateISSPosition(issPositionResponse);
    }

    public void fetchISSPosition() {
        iISSPositionRepository.fetchISSPosition();
    }

    /**
     * It uses the Repository to download the news list
     * and to associate it with the LiveData object.
     */
    public void fetchISSPosition(long timestamp) {
        issLiveData = iISSPositionRepository.fetchISSPosition(timestamp);
    }

    /**
     * Clears the list of favorite news.
     */
    public void deleteISSPosition() {
        iISSPositionRepository.deleteISSPosition();
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentResults() {
        return currentResults;
    }

    public void setCurrentResults(int currentResults) {
        this.currentResults = currentResults;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isFirstLoading() {
        return firstLoading;
    }

    public void setFirstLoading(boolean firstLoading) {
        this.firstLoading = firstLoading;
    }

    public MutableLiveData<Result> getISSResponseLiveData() {
        return issLiveData;
    }
}
