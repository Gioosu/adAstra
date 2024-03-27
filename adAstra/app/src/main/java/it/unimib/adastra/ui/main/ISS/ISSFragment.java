package it.unimib.adastra.ui.main.ISS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ISSFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ISSFragment extends Fragment {
    private ISSViewModel issViewModel;

    public ISSFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ISSFragment.
     */
    public static ISSFragment newInstance(String param1, String param2) {
        return new ISSFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IISSPositionRepository iissPositionRepository =
                ServiceLocator.getInstance().getISSRepository(
                        requireActivity().getApplication());

        if (iissPositionRepository != null) {
            // This is the way to create a ViewModel with custom parameters
            // (see NewsViewModelFactory class for the implementation details)
            issViewModel = new ViewModelProvider(
                    requireActivity(),
                    new ISSViewModelFactory(iissPositionRepository)).get(ISSViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.error_unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_i_s_s, container, false);
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        issViewModel.getISSPosition(0).observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        ISSPositionResponse issPositionResponse = ((Result.NewsResponseSuccess) result).getData();
                        //List<News> fetchedNews = issPositionResponse.getNewsList();

                        if (!issViewModel.isLoading()) {
                            if (issViewModel.isFirstLoading()) {
                                issViewModel.setTotalResults(((ISSPositionResponse) issPositionResponse).get());
                                issViewModel.setFirstLoading(false);
                                this.newsList.addAll(fetchedNews);
                                newsRecyclerViewAdapter.notifyItemRangeInserted(0,
                                        this.newsList.size());
                            } else {
                                // Updates related to the favorite status of the news
                                newsList.clear();
                                newsList.addAll(fetchedNews);
                                newsRecyclerViewAdapter.notifyItemChanged(0, fetchedNews.size());
                            }
                            fragmentCountryNewsBinding.progressBar.setVisibility(View.GONE);
                        } else {
                            issViewModel.setLoading(false);
                            issViewModel.setCurrentResults(newsList.size());

                            int initialSize = newsList.size();

                            for (int i = 0; i < newsList.size(); i++) {
                                if (newsList.get(i) == null) {
                                    newsList.remove(newsList.get(i));
                                }
                            }
                            int startIndex = (issViewModel.getPage()*TOP_HEADLINES_PAGE_SIZE_VALUE) -
                                    TOP_HEADLINES_PAGE_SIZE_VALUE;
                            for (int i = startIndex; i < fetchedNews.size(); i++) {
                                newsList.add(fetchedNews.get(i));
                            }
                            newsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, newsList.size());
                        }
                    } else {
                        ErrorMessagesUtil errorMessagesUtil =
                                new ErrorMessagesUtil(requireActivity().getApplication());
                        Snackbar.make(view, errorMessagesUtil.
                                        getErrorMessage(((Result.Error)result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                        fragmentCountryNewsBinding.progressBar.setVisibility(View.GONE);
                    }
                });
    }*/
}