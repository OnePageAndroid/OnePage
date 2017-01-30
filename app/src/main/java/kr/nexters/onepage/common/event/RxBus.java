package kr.nexters.onepage.common.event;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by OhJaeHwan on 2017-01-29.
 */

public class RxBus {

    private final PublishSubject<Object> bus;

    private RxBus() {
        bus = PublishSubject.create();
    }

    private static class InstanceHolder {
        private static final RxBus INSTANCE = new RxBus();
    }

    public static RxBus getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
