package kr.nexters.onepage.common;

import com.squareup.otto.Bus;

/**
 * Created by OhJaeHwan on 2017-02-19.
 */

public class BusProvider {
    private static Bus bus = new Bus();

    public static void register(Object target) {
        bus.register(target);
    }

    public static void unRegister(Object target) {
        bus.unregister(target);
    }

    public static void post(Object event) {
        bus.post(event);
    }
}
