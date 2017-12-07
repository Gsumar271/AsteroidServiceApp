package com.eugenesumaryev.myapipractice02;

import android.util.JsonReader;
import android.util.JsonToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by eugenesumaryev on 11/15/17.
 */

public class JSNParser {

    private JsonReader in;

    public JSNParser() {

    }

    public JSNParser(JsonReader _in){

        in = _in;
    }


    public JSONObject readJSONObject(InputStream _in) throws IOException, JSONException {

        JsonReader reader = new JsonReader(new InputStreamReader(_in, "UTF-8"));
        try {

            return readJSONObjects(reader);

        } finally {
            reader.close();
        }
    }

    public JSONArray readJSONArray(InputStream _in) throws IOException, JSONException {

        JsonReader reader = new JsonReader(new InputStreamReader(_in, "UTF-8"));
        try {

            return readJSONArrays(reader);

        } finally {
            reader.close();
        }
    }


    public JSONObject readJSONObjects(JsonReader reader) throws IOException, JSONException{

        String name;
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        reader.beginObject();

        while (reader.hasNext()) {

            if (reader.peek() == JsonToken.NAME) {
                    name = reader.nextName();

                if (reader.peek() == JsonToken.BEGIN_OBJECT) {

                        jsonObject.accumulate(name, readJSONObjects(reader));

                } else if (reader.peek() == JsonToken.BEGIN_ARRAY) {

                        jsonObject.accumulate(name, readJSONArrays(reader));

                } else if ((reader.peek() == JsonToken.STRING) || (reader.peek() == JsonToken.NUMBER)) {

                    jsonObject.accumulate(name, reader.nextString());

                }
                else
                {
                    reader.skipValue();
                }
            }
            else
                reader.skipValue();

        }

        reader.endObject();

        return jsonObject;


        /*
        if (reader.peek() == JsonToken.BEGIN_ARRAY){
            reader.beginObject();
            while (reader.hasNext()) {
                return readJSONArray(reader);
            }
            reader.endObject();
        }


        if (reader.peek() == JsonToken.NAME){

            String name = reader.nextName();

            JSONObject jsonObject = new JSONObject();
           // jsonObject.accumulate(name, readJSONObject(reader));

            if ((reader.peek() == JsonToken.STRING) || (reader.peek() == JsonToken.NUMBER) ) {
                jsonObject.accumulate(name, reader.nextString());
            }

            return  readValue(name, reader);

        }
        */
    }

    public JSONArray readJSONArrays(JsonReader reader) throws IOException, JSONException{

        JSONArray jsonArray = new JSONArray();

        reader.beginArray();

        while (reader.hasNext()){

            if (reader.peek() == JsonToken.BEGIN_OBJECT){

                jsonArray.put(readJSONObjects(reader));

            }

            else if (reader.peek() == JsonToken.BEGIN_ARRAY){

                jsonArray.put(readJSONArrays(reader));

            }

            else if ((reader.peek() == JsonToken.STRING) || (reader.peek() == JsonToken.NUMBER)){

                jsonArray.put(reader.nextString());

            }

            else

            {
                reader.skipValue();
            }

        }

        reader.endArray();


        return jsonArray;

    }



    public JSONObject readValue(String _name, JsonReader reader) throws IOException, JSONException{

        JSONObject jsonObject = new JSONObject();


        if ((reader.peek() == JsonToken.STRING) || (reader.peek() == JsonToken.NUMBER) ) {
            jsonObject.accumulate(_name, reader.nextString());
        }

        return jsonObject;

    }


}





    /*

    public JSONObject readAllObjects(JsonReader reader) throws IOException {

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("near_earth_objects"))
                readDatesObjects(reader);
            else
                reader.skipValue();
        }

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            while (reader.hasNext()) {
                jsonObject = readAllObjects(reader);
            }
            reader.endObject();
        }

        if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            reader.beginArray();
            while (reader.hasNext()) {
                jsonArray = readAllArrays(reader);
            }
            reader.endArray();
        }

        return jsonObject;

    }

    */