package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetCursorAdapter;
import com.example.android.pets.data.PetsContract.PetsEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String LOG_TAG = CatalogActivity.class.getSimpleName();
    private PetCursorAdapter petCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView catalogActivityListView = (ListView) findViewById(R.id.catalog_activity_listview);

        View emptyView = findViewById(R.id.empty_view);
        catalogActivityListView.setEmptyView(emptyView);

        petCursorAdapter = new PetCursorAdapter(getApplicationContext(), null);

        catalogActivityListView.setAdapter(petCursorAdapter);

        getLoaderManager().initLoader(0, null, this);

        catalogActivityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent editDataIntent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri petUri = ContentUris.withAppendedId(PetsEntry.CONTENT_URI, l);

                Log.e(LOG_TAG, "URI sent with Intent is: " + petUri);

                editDataIntent.setData(petUri);
                startActivity(editDataIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                addDummyPetData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDummyPetData() {
        //Set values for the new row
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsEntry.COLUMN_NAME_NAME, "Toto");
        contentValues.put(PetsEntry.COLUMN_NAME_BREED, "Terrier");
        contentValues.put(PetsEntry.COLUMN_NAME_GENDER, PetsEntry.GENDER_MALE);
        contentValues.put(PetsEntry.COLUMN_NAME_WEIGHT, 7);

        Log.e(LOG_TAG, "Table Name: " + PetsEntry.TABLE_NAME + ", ContentValues: " + contentValues.valueSet());

        Uri uri = getContentResolver().insert(PetsEntry.CONTENT_URI, contentValues);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, PetsEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        petCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petCursorAdapter.swapCursor(null);
    }
}