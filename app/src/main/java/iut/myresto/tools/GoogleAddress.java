package iut.myresto.tools;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by amanda on 21/06/2017.
 */

public class GoogleAddress {

    //GOOGLE GEOLOCALITATION
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    private String address;
    private String city;

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public GoogleAddress(double lat, double lng, Context context) {

        String errorMessage = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);

            this.address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            this.city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
