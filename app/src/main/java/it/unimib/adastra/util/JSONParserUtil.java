package it.unimib.adastra.util;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.unimib.adastra.model.ISS.ISSPositionResponse;

public class JSONParserUtil {

    private final Context context;

    public JSONParserUtil(Application application) {
        this.context = application.getApplicationContext();
    }

    // TODO implementare i parametri con query(risultato) e nome della classe, fixare il return
    public ISSPositionResponse parseJSONFileWithGSON(String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new Gson().fromJson(bufferedReader, ISSPositionResponse.class);
    }
}
