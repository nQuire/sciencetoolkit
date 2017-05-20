package org.greengin.sciencetoolkit.common.model;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.common.model.serialize.ModelDeserializer;
import org.greengin.sciencetoolkit.common.model.serialize.ModelSerializer;
import org.greengin.sciencetoolkit.common.model.serialize.ModelVersionManager;

import android.content.Context;

public abstract class AbstractModelManager implements ModelChangeListener, ModelVersionManager {
    ReentrantLock lock;
    Timer timer;
    int saveDelay;
    String filename;

    protected Context applicationContext;
    protected Hashtable<String, Model> items;

    protected AbstractModelManager(Context applicationContext, String filename, int saveDelay) {
        this.applicationContext = applicationContext;
        this.lock = new ReentrantLock();
        this.filename = filename;
        this.saveDelay = saveDelay;

        load();
    }

    private void load() {
        items = ModelDeserializer.xml2modelMap(this, this, applicationContext, filename);
    }

    public void forceSave() {
        this.save();
    }

    private void save() {
        lock.lock();
        ModelSerializer.model2xml(this, items, applicationContext, filename);
        lock.unlock();
    }

    protected Model get(String key, boolean create) {
        if (!items.containsKey(key)) {
            if (create) {
                Model model = new Model(this);
                items.put(key, model);
                model.setString("id", key);
                return model;
            } else {
                return null;
            }
        } else {
            return items.get(key);
        }
    }

    public Model remove(String key) {
        Model removed = items.remove(key);
        if (removed != null) {
            modelModified(null);
        }
        return removed;
    }

    @Override
    public void modelModified(Model model) {
        lock.lock();
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    timer = null;
                    save();

                }
            }, saveDelay);
        }
        lock.unlock();
    }

}
