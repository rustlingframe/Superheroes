package edu.miracosta.cs134.aespinoza.superheroes.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class loads Superhero data from a formatted JSON (JavaScript Object Notation) file.
 * Populates data model (Superhero) with data.
 */
public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     *
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */
    public static List<Superhero> loadJSONFromAsset(Context context) throws IOException {
        List<Superhero> allSuperheroesList = new ArrayList<>();
        String json = null;
        InputStream is = context.getAssets().open("cs134superheroes.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperheroesJSON = jsonRootObject.getJSONArray("CS134Superheroes");

            // DONE: Loop through all the countries in the JSON data, create a Country
            // DONE: object for each and add the object to the allCountriesList
            JSONObject superheroJSON;
            int count = allSuperheroesJSON.length();
            String name, superpower,oneThing,fileName;

            for (int i = 0; i < count; i++) {
                superheroJSON = allSuperheroesJSON.getJSONObject(i);
                name = superheroJSON.getString("Name");
                superpower = superheroJSON.getString("Superpower");
                oneThing = superheroJSON.getString("OneThing");
                fileName = superheroJSON.getString("FileName");
                allSuperheroesList.add(new Superhero(name, superpower,oneThing,fileName));
            }

        } catch (JSONException e) {
            Log.e("Flag Quiz", e.getMessage());
        }

        return allSuperheroesList;
    }
}
