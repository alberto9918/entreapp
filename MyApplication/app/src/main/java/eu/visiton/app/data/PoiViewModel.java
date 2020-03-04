package eu.visiton.app.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;

public class PoiViewModel extends AndroidViewModel {
    private PoiRepository poiRepository;
    private LiveData<ResponseContainer<PoiResponse>> pois;

    public PoiViewModel(@NonNull Application application) {
        super(application);
        poiRepository = new PoiRepository();
        //Al llamar al metodo getallPois, en el fragmento habia que pasarle como parametro dos coordenadas, las cuales
        //he visto que al parecer se obtienen de un metodo del fragmento que se llama getDeviceLocation, y no se si ese
        //metodo tengo que pasarlo aqui o no
        pois = poiRepository.getAllPois(0,0);

    }

    public LiveData<ResponseContainer<PoiResponse>> getPois(){
        return pois;
    }
}
