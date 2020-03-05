package eu.visiton.app.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;

public class PoiViewModel extends AndroidViewModel {
    private PoiRepository poiRepository;
    private LiveData<List<PoiResponse>> pois;

    public PoiViewModel(@NonNull Application application) {
        super(application);
        poiRepository = new PoiRepository();
    }

    public LiveData<List<PoiResponse>> getPois(double latitud, double longitud){
        pois = poiRepository.getAllPois(latitud,longitud);
        return pois;
    }
}
