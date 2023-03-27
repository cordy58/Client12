package com.client11;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import Data.DataCache;
import Models.Event;
import Models.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;

    private DataCache cache = DataCache.getInstance();

    private Map<String, Float> colorCodedEvents = new HashMap<>();
    private Vector<Float> googleColors = new Vector<>();
    private Vector<Marker> markers = new Vector<>();

    private ImageView eventIcon;
    private Drawable defaultIcon;
    private Drawable maleIcon;
    private Drawable femaleIcon;

    private TextView associatedPersonName;
    private TextView eventDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        defaultIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                colorRes(R.color.android_icon).sizeDp(60);
        maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                colorRes(R.color.male_icon).sizeDp(60);
        femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                colorRes(R.color.female_icon).sizeDp(60);

        eventIcon = view.findViewById(R.id.eventIcon);
        eventIcon.setImageDrawable(defaultIcon);

        associatedPersonName = view.findViewById(R.id.associatedPerson);
        eventDetails = view.findViewById(R.id.eventDetails);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        initGoogleColors();
        populateMap();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Event selectedEvent = (Event) marker.getTag();
                Person associatedPerson = cache.getPerson(selectedEvent.getPersonID());

                changeIcon(associatedPerson.getGender());

                String name = associatedPerson.getFirstName() + " " + associatedPerson.getLastName();
                associatedPersonName.setText(name);

                String details = selectedEvent.getEventType() + ": " + selectedEvent.getCity() +
                        ", " + selectedEvent.getCountry() + " (" + selectedEvent.getYear() + ")";
                eventDetails.setText(details);

                //draw lines

                return false;
            }
        });
    }

    private void populateMap() {
        int counter = 0;
        for(Event event: cache.getEvents().values()) {
            float googleColor;
            if (!colorCodedEvents.containsKey(event.getEventType().toLowerCase())) {
                colorCodedEvents.put(event.getEventType().toLowerCase(), googleColors.get(counter));
            }
            googleColor = colorCodedEvents.get(event.getEventType().toLowerCase());

            Marker marker = map.addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(googleColor)));

            marker.setTag(event);
            markers.add(marker);

            counter++;
            if (counter >= googleColors.size()) {
                counter -= googleColors.size();
            }
        }
    }

    private void initGoogleColors() {
        googleColors.add(BitmapDescriptorFactory.HUE_BLUE);
        googleColors.add(BitmapDescriptorFactory.HUE_AZURE);
        googleColors.add(BitmapDescriptorFactory.HUE_ORANGE);
        googleColors.add(BitmapDescriptorFactory.HUE_ROSE);
        googleColors.add(BitmapDescriptorFactory.HUE_CYAN);
        googleColors.add(BitmapDescriptorFactory.HUE_GREEN);
        googleColors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        googleColors.add(BitmapDescriptorFactory.HUE_RED);
        googleColors.add(BitmapDescriptorFactory.HUE_VIOLET);
        googleColors.add(BitmapDescriptorFactory.HUE_YELLOW);
    }

    private void changeIcon(String gender) {
        //this doesn't work. Always sets to female
        if (gender.equalsIgnoreCase("m")) {
            eventIcon.setImageDrawable(maleIcon);
        } else {
            eventIcon.setImageDrawable(femaleIcon);
        }
    }
}