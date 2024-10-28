package com.example.ultimateiptvplayer.Fragments.Categorie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ultimateiptvplayer.Enum.LANGAGES;
import com.example.ultimateiptvplayer.Entities.Channels.Channel;
import com.example.ultimateiptvplayer.Adapter.CategoryAdapter;
import com.example.ultimateiptvplayer.Entities.Playlist.Playlist;
import com.example.ultimateiptvplayer.Fragments.Player.AmbilightManager;
import com.example.ultimateiptvplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategorieFragment extends Fragment {
    private ListView categorieLV;
    private Spinner langageFilter;
    private final Playlist playlist;
    private final OnCategoriesListener onCategoriesListener;

    private String currentCategory;
    private int currentCategoryPosition = -1;

    public CategorieFragment(Playlist playlist, OnCategoriesListener onCategoriesListener) {
        this.playlist = playlist;
        this.onCategoriesListener = onCategoriesListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categorie, container, false);
        categorieLV = view.findViewById(R.id.categories_LV);
        langageFilter = view.findViewById(R.id.langage_filter);

        //Build the Langage Filter Spinner
        List<String> langages_choice = new ArrayList<>();
        for(LANGAGES langage : LANGAGES.values()) {langages_choice.add(langage.getLangage());}

        langageFilter.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, langages_choice));

        //set the behavior of the langage filter
       langageFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLangage = langages_choice.get(position);
                System.out.println("Selected Langage: " + selectedLangage);

                //get the selected langage
                LANGAGES langage = LANGAGES.valueOf(selectedLangage);

                // Filter the categories based on the selected langage
                setupListView(langage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

       //set the behavior of the categories list
        categorieLV.setOnItemClickListener((parent, view1, position, id) -> {
            String category = (String) parent.getItemAtPosition(position);
            System.out.println("Selected Category: " + category);

            this.currentCategory = category;
            this.currentCategoryPosition = position;

            this.onCategoriesListener.onCategoriesClick(category);
        });

        // Build the Channels ListView
        setupListView(LANGAGES.OTHER);

        return view;
    }


    public String getCurrentCategory() {
        return currentCategory;
    }
    private void setupListView(LANGAGES langage) {
        TreeMap<String, ArrayList<Channel>> channels = playlist.getAllChannels(); // Assurez-vous que la méthode getChannels() existe et retourne le TreeMap
        List<String> categories = filterAndSortCategories(new ArrayList<>(channels.keySet()), langage);

        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        categorieLV.setAdapter(adapter);
    }

    public void updateCategories() {
        LANGAGES langage = LANGAGES.valueOf(langageFilter.getSelectedItem().toString());
        setupListView(langage);
    }

    /**
     * Filter and sort the categories based on the specific order
     * @param categories
     * @return
     */
    private List<String> filterAndSortCategories(List<String> categories, LANGAGES langage) {
        List<String> sortedCategories = new ArrayList<>();

        //Add other pattern for other langages
        Pattern frenchPattern = Pattern.compile("EU \\| FRANCE", Pattern.CASE_INSENSITIVE);

        if(categories.contains("Favorites")){sortedCategories.add("Favorites");}
        //Add ONLY THE CATEGORIES WHICH MATCHES
        if(langage == LANGAGES.FR){
            for (String category : categories) {
                Matcher matcher = frenchPattern.matcher(category);
                if (matcher.find()) {sortedCategories.add(category);}
            }
        }
        //Doesn't match any langage -> DEFAULT ORDER (alphabetical)
        else{return categories;}

        // ADD the rest of the categories
        for (String category : categories) {
            if (!sortedCategories.contains(category)) {
                sortedCategories.add(category);
            }
        }

        return sortedCategories;
    }
}
