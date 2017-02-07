package kr.nexters.onepage.common.model;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.nexters.onepage.map.LocationAPI;
import kr.nexters.onepage.map.LocationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.google.android.gms.internal.zzs.TAG;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Loc {
    private Long locationId;
    private String address;
    private double latitude;
    private double longitude;
    private String name;
    private Marker marker;
    private LocationService service;


    public String getLocationName(Marker marker) {
        return name;
    }
//    public LocationInfo getLocationInfo(Marker marker) {
//        service = new LocationService();
//        LocationInfo info = new LocationInfo();
//
//        String today =
//                new SimpleDateFormat("yyyy-mm-dd").format(new Date());
//
//        Log.i("Map", today);
//        //List<Loc> locations = service.getLocationList();
//        //Log.i("Map",locations.get(0).toString());
////        for(Loc loc : locations) {
////            Log.i("Map listzzz", "Zz");
////        }
//
//        return info;
//    }

//    public Loc getSelectedLoc(Marker marker) {
//        Loc loc = new Loc();
//        service = new LocationService();
//        List<Loc> locations = service.getLocationList();
//
//        int mid;
//        int left = 0;
//        int right = locations.size() - 1;
//
//        while (right >= left) {
//            mid = (right + left) / 2;
//
//            if (marker == locations.get(mid).marker) {
//                return locations.get(mid);
//            }
//
//            if (marker < arr[mid]) {
//                right = mid - 1;
//            } else {
//                left = mid + 1;
//            }
//
//        }
//
//        return loc;
//    }

}
