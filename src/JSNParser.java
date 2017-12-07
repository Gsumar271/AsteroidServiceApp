package com.asteroidserviceapp;

import android.util.JsonReader;
import android.util.JsonToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


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

