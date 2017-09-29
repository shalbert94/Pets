/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract.PetsEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private Intent intent;
    private EditText mNameEditText;
    private boolean petHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            petHasChanged = true;
            return false;
        }
    };
    /**
     * EditText field to enter the pet's name
     */
    private EditText mBreedEditText;
    /**
     * EditText field to enter the pet's breed
     */
    private EditText mWeightEditText;
    /**
     * EditText field to enter the pet's weight
     */
    private Spinner mGenderSpinner; /** EditText field to enter the pet's gender */

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editPetCheck();
        setupSpinner();
        setTouchListener();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetsEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet();
                returnToCatalogActivity();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!petHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePet() {
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        String name = mNameEditText.getText().toString();
        String breed = mBreedEditText.getText().toString();
        String weight = mWeightEditText.getText().toString();
        String gender = String.valueOf(mGender);

        ContentValues values = new ContentValues();
        Context context = getApplicationContext();
        Toast toast = new Toast(context);
        int duration = Toast.LENGTH_LONG;

        values.put(PetsEntry.COLUMN_NAME_NAME, name);
        values.put(PetsEntry.COLUMN_NAME_BREED, breed);
        values.put(PetsEntry.COLUMN_NAME_WEIGHT, weight);
        values.put(PetsEntry.COLUMN_NAME_GENDER, gender);

        intent = getIntent();
        Uri uriFromIntent = intent.getData();

        if (checkForEmptyFields(name, breed, weight) == 0) {
            returnToCatalogActivity();
            return;
        } else if (checkForEmptyFields(name, breed, weight) == 1) {
            values.put(PetsEntry.COLUMN_NAME_WEIGHT, 0);
        }

        if (uriFromIntent != null) {
            int numberOfRowsAffected = getContentResolver().update(uriFromIntent, values, null, null);

            if (numberOfRowsAffected == 1) {
                toast = Toast.makeText(context, R.string.editor_update_pet_succesful, duration);
            } else {
                toast = Toast.makeText(context, R.string.editor_update_pet_failed, duration);
            }
        } else if (uriFromIntent == null) {
            Uri uri = getContentResolver().insert(PetsEntry.CONTENT_URI, values);

            if (uri == null) {
                toast = Toast.makeText(context, R.string.editor_insert_pet_failed, duration);
            } else {
                toast = Toast.makeText(context, R.string.editor_insert_pet_successful, duration);
            }
        }
        toast.show();
    }

    private void returnToCatalogActivity() {
        Intent home = new Intent(EditorActivity.this, CatalogActivity.class);
        startActivity(home);
    }

    private void editPetCheck() {
        intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            setTitle(R.string.editor_activity_title_edit_pet);
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = getIntent().getData();
        return new android.content.CursorLoader(getApplicationContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        cursor.moveToFirst();

        String nameFromCursor = cursor.getString(cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_NAME_NAME));
        String breedFromCursor = cursor.getString(cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_NAME_BREED));
        String weightFromCursor = cursor.getString(cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_NAME_WEIGHT));
        int genderFromCursor = cursor.getInt(cursor.getColumnIndexOrThrow(PetsEntry.COLUMN_NAME_GENDER));

        mNameEditText.setText(nameFromCursor);
        mBreedEditText.setText(breedFromCursor);
        mWeightEditText.setText(weightFromCursor);
        mGenderSpinner.setSelection(genderFromCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }

    private int checkForEmptyFields(String name, String breed, String weight) {
        int allEmpty = 0;
        int weightEmpty = 1;
        int filled = 2;

        if (name.length() == 0 && breed.length() == 0 && weight.length() == 0) {
            return allEmpty;
        } else if (weight.length() == 0) {
            return weightEmpty;
        } else {
            return filled;
        }
    }

    private void setTouchListener() {
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        
        mNameEditText.setOnTouchListener(touchListener);
        mBreedEditText.setOnTouchListener(touchListener);
        mWeightEditText.setOnTouchListener(touchListener);
        mGenderSpinner.setOnTouchListener(touchListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!petHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}