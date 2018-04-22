package com.sdm.trytomeet.fragments.Sites;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.R;
import com.sdm.trytomeet.activities.MainActivity;
import com.sdm.trytomeet.persistence.server.UserFirebaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoriteSitesFragment extends Fragment {
    private View parent;
    private String user_id;

    public ArrayList<HashMap<String,String>> sitesList;
    public ListView listViewSites;
    public SimpleAdapter adapter;

    public Site selectedSite;

    public ProgressBar progressBar;

    public FavoriteSitesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.frg_favorite_sites, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user_id = prefs.getString("account_id", "");

        sitesList = new ArrayList<HashMap<String,String>>();

        listViewSites = parent.findViewById(R.id.list_sites);
        adapter = new SimpleAdapter(getActivity(), sitesList, R.layout.row_favorite_sites,
                new String[]{"name","description"}, new int[]{R.id.siteName, R.id.siteDescription});
        listViewSites.setAdapter(adapter);

        // Updates the Site selected.
        listViewSites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String siteName = ((TextView)view.findViewById(R.id.siteName)).getText().toString();
                String siteDescription = ((TextView)view.findViewById(R.id.siteDescription)).getText().toString();

                selectedSite = new Site(siteName,siteDescription);

                listViewSites.setSelector(android.R.color.darker_gray);
            }
        });

        progressBar = ((ProgressBar)parent.findViewById(R.id.progressBar));
        progressBar.setIndeterminate(true);

        UserFirebaseService.getUserFavoriteSites(user_id,this);

        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.Favorite_sites_title));

        return parent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favorite_sites_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_favorite_site:
                deleteSite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSite() {
        if(selectedSite != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.delete_site));

            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Deletes the site.
                    UserFirebaseService.removeFavoriteSite(user_id, selectedSite);

                    // Hides the selector, so after removing an element looks like no one is selected.
                    listViewSites.setSelector(android.R.color.transparent);

                    // Deletes the site from the list of sites and udpates the view.
                    sitesList.remove(getSiteHashMap(selectedSite.name,selectedSite.description));
                    adapter.notifyDataSetChanged();

                    // It's set to null as in this way a site it's not deleted twice in
                    // different attempts. In other words, another row must be selected to delete again.
                    selectedSite = null;
                }
            });

            builder.setNegativeButton(android.R.string.no, null);
            builder.show();
        }
    }

    // This method is called by the Firebase service after reading the favourite sites of the user.
    public void addSitesToList(List<Site> favouriteSites) {
        progressBar.setVisibility(View.GONE);

        for (Site site: favouriteSites) {
            addSiteToList(site);
        }
    }

    public void addSiteToList(Site site){
        sitesList.add(getSiteHashMap(site.name,site.description));
        adapter.notifyDataSetChanged();
    }

    // Transform to the HashMap required by the list of elements passed to the SimpleAdapter.
    private HashMap<String, String> getSiteHashMap(String name, String description) {
        HashMap<String,String> item = new HashMap<String,String>();
        item.put("name", name);
        item.put("description", description);
        return item;
    }


}
