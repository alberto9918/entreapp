package eu.visiton.app.data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eu.visiton.app.common.MyApp;
import eu.visiton.app.model.Poi;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.ui.pois.list.PoiListAdapter;
import eu.visiton.app.util.UtilToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoiRepository {
    PoiService poiService;
    String jwt;
    //La lista de LiveData tiene que ser de Objeto Poi o de objeto PoiResponse?
    //Además, el live data tiene que ser tipo List<> o tipo ResponseContainer<>?
    LiveData<ResponseContainer<PoiResponse>> allPois;

    PoiRepository(){
        //En el fragmento el jwt se crea en el metodo onCreate, no se si se tiene que poner aqui o no
        jwt = UtilToken.getToken(Objects.requireNonNull(MyApp.getContext()));
        poiService = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        //Al llamar al metodo getallPois, en el fragmento habia que pasarle como parametro dos coordenadas, las cuales
        //he visto que al parecer se obtienen de un metodo del fragmento que se llama getDeviceLocation, y no se si ese
        //metodo tengo que pasarlo aqui o no
        allPois = getAllPois(0,0);
    }

    public LiveData<ResponseContainer<PoiResponse>> getAllPois(double latitude, double longitude){
        final MutableLiveData<ResponseContainer<PoiResponse>> data = new MutableLiveData<>();

        //En el fragmento, items es un atributo que se crea en la clase y que es una lista de poiResponse, y que dentro del metodo
        //getAllPois, lo instancia como un ArrayList. No se si ese atributo se tiene que quedar en el fragmento, como se hace con el
        //adapter, o hay que crearlo aqui

        //items = new ArrayList<>();
        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);

        String coords = longitude + "," + latitude;
        Call<ResponseContainer<PoiResponse>> call = service.listPois(coords, 5000);
        call.enqueue(new Callback<ResponseContainer<PoiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Response<ResponseContainer<PoiResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    //items = Objects.requireNonNull(response.body()).getRows();
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(MyApp.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

        return data;
    }
}
