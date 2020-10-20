package com.eelink.tcp.task;

import com.eelink.tcp.App;
import com.eelink.tcp.core.Device;
import com.eelink.tcp.utils.Logger;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

public class DeviceWorker implements TimerTask {

    @Override
    public void run( Timeout timeout ) {
        while ( true )
        {
            try
            {
                App.deviceArr.forEach( Device::work );
                App.mergeDevice();
                Thread.sleep( 66 );
            }
            catch ( Exception e )
            {
                Logger.ERR( e );
            }
        }
    }

}
