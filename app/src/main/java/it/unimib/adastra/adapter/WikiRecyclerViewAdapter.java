package it.unimib.adastra.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import it.unimib.adastra.databinding.WikiObjBinding;

import com.bumptech.glide.Glide;

import java.util.List;

import it.unimib.adastra.model.wiki.WikiObj;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of News
 * with a RecyclerView.
 */
public class WikiRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    private final List<WikiObj> wikiObjs;
    private final Application application;
    private WikiObjBinding binding;

    public WikiRecyclerViewAdapter(List<WikiObj> wikiObjs, Application application) {
        this.wikiObjs = wikiObjs;
        this.application = application;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = WikiObjBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WikiObjViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((WikiObjViewHolder) holder).bind(wikiObjs.get(position));
    }

    @Override
    public int getItemCount() {
        if (wikiObjs != null) {
            return wikiObjs.size();
        }
        return 0;
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class WikiObjViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewName;
        private final TextView textViewDescription;
        private final ImageView imageViewImage;

        public WikiObjViewHolder(@NonNull WikiObjBinding binding) {
            super(binding.getRoot());
            textViewName = binding.nameWikiobj;
            textViewDescription = binding.descriptionWikiobj;
            imageViewImage = binding.imageWikiobj;

        }

        public void bind(WikiObj wikiObj) {
            textViewName.setText(wikiObj.getName());
            textViewDescription.setText(wikiObj.getDescription());
            Glide.with(application)
                    .load(wikiObj.getUrl())
                    .into(imageViewImage);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
