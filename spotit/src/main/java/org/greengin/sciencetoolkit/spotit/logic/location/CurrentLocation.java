package org.greengin.sciencetoolkit.spotit.logic.location;

import android.content.Context;
import android.util.Log;

import org.greengin.sciencetoolkit.common.logic.location.LocationTracker;
import org.greengin.sciencetoolkit.common.model.Model;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class CurrentLocation extends LocationTracker {
    private static int MAX_WAIT = 5000;

    Timer timer;
    Vector<Model> requests;

    public CurrentLocation(Context applicationContext) {
        super(applicationContext);

        this.requests = new Vector<Model>();
        this.timer = null;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    private void save(boolean delete) {
        String location = locationString();
        Log.d("stk location", "save: " + location + " " + delete);
        for (Model request : requests) {
            request.setString("location", location);
        }
        if (delete) {
            requests.clear();
        }
    }

    public void request(Model request) {
        cancelTimer();

        requests.add(request);
        setListening(true);

        save(false);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setListening(false);
                save(true);
                timer = null;
            }
        }, MAX_WAIT);
    }

    @Override
    public void locationReceived() {
        Log.d("stk location", this.locationString());
        setListening(false);
        cancelTimer();
        save(true);
    }
}
