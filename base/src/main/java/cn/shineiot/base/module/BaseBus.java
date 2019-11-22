package cn.shineiot.base.module;

import org.greenrobot.eventbus.EventBus;

/**
 * @author  by zhang on 2016/3/2.
 */

public class BaseBus {
    private static EventBus bus;

    public static EventBus getInstance() {
        if (bus == null) {
        	synchronized (BaseBus.class) {
        		if(bus == null) {
			        bus = EventBus.getDefault();
		        }
	        }
        }
        return bus;
    }
}
