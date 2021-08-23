package com.intelisoft.easytorrents.iface;

/**
 * Created by Technophile on 6/25/17.
 */

public interface MovieSubject {
    void register(MovieObserver observer);
    void remove(MovieObserver observer);
    void notifyObserver();
}
