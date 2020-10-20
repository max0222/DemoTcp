package com.eelink.tcp.utils;

public interface CONST {

    int TIMER_TASK_NUM			= 4;
    int TIMER_TASK_ID_WORKER	= 0;
    int TIMER_TASK_ID_SQL		= 1;
    int TIMER_TASK_ID_JPUSH		= 2;
    int TIMER_TASK_ID_SQL1		= 3;

    boolean BUSINESS_EDITION 	= false;

    double M_PI 				= 3.14159265358979323846;

    double EARTH_RADIUS			= 6371004.0;

    int MAX_SERVICE_NUM			= CONST.BUSINESS_EDITION ? 1 : 2;

    enum Warn {
        MX_CUST_WARN_SOS,
        MX_CUST_WARN_VIB,
        MX_CUST_WARN_EPD,
        MX_CUST_WARN_BAT,
        MX_CUST_WARN_ANT_OC,
        MX_CUST_WARN_ANT_SC,
        MX_CUST_WARN_CRASH,
        MX_CUST_WARN_FREEFALL,
        MX_CUST_WARN_SPD_LO,
        MX_CUST_WARN_SPD_HI,
        MX_CUST_WARN_BND_IN,
        MX_CUST_WARN_BND_OUT,
        MX_CUST_WARN_SHIFT,
        MX_CUST_WARN_MOTION,
        MX_CUST_WARN_TEMPOR,
        MX_CUST_WARN_HUMIDITY,
        MX_CUST_WARN_LIGHT,
        MX_CUST_WARN_CO2,
        MX_CUST_WARN_PROBE,
        MX_CUST_WARN_BRIGHT
    }

    enum Push {
        MX_CUST_PUSH_SOS,
        MX_CUST_PUSH_VIB,
        MX_CUST_PUSH_EPD,
        MX_CUST_PUSH_BAT,
        MX_CUST_PUSH_CRASH,
        MX_CUST_PUSH_FREEFALL,
        MX_CUST_PUSH_SPD,
        MX_CUST_PUSH_FENCE,
        MX_CUST_PUSH_SHIFT,
        MX_CUST_PUSH_TRIP,
        MX_CUST_PUSH_ACCOFF,
        MX_CUST_PUSH_ACCON,
        MX_CUST_PUSH_TEMPOR,
        MX_CUST_PUSH_HUMIDITY,
        MX_CUST_PUSH_LIGHT,
        MX_CUST_PUSH_CO2,
        MX_CUST_PUSH_PROBE,
        MX_CUST_PUSH_BRIGHT
    }

    enum StrWarn {
        STR_WARN_SOS, // warning
        STR_WARN_VIB,
        STR_WARN_EPD,
        STR_WARN_BAT,
        STR_WARN_ANT_OC,
        STR_WARN_ANT_SC,
        STR_WARN_CRASH,
        STR_WARN_FREEFALL,
        STR_WARN_SPD_LO,
        STR_WARN_SPD_HI,
        STR_WARN_BND_IN,
        STR_WARN_BND_OUT,
        STR_WARN_SHIFT,
        STR_WRN_MOTION,
        STR_WRN_TEMPOR,
        STR_WRN_HUMIDITY,
        STR_WRN_LIGHT,
        STR_WRN_CO2,
        STR_WRN_PROBE,
        STR_WRN_BRIGHT,
        STR_WRN_COMMON,
        STR_WRN_BRIGHT2,
        STR_TRIP_START, // trip
        STR_TRIP_STOP,
        STR_TRIP_TRAVEL_TIME,
        STR_TRIP_MILEAGE,
        STR_TRIP_FUEL_CONSUMPTION,
        STR_TRIP_ORIGIN,
        STR_TRIP_DESTINATION,
        STR_ACC_ON, // acc
        STR_ACC_OFF,
        STR_ONLINE, // online
        STR_BATTERY,
        STR_PUSH_WARNING, // push title
        STR_PUSH_NOTIFICATION,
        STR_PUSH_TRIP,
        STR_SPEED_UNIT,
        STR_BATTERY_UNIT,
        STR_TEMPERATURE_UNIT,
        STR_HUMIDITY_UNIT,
        STR_LIGHT_UNIT,
        STR_CO2_UNIT,
        STR_DEVICE_ID,
        STR_iBUTTON_DRIVER_ID,
        STR_iBUTTON_START_TIME,
        STR_iBUTTON_REPORT
    }

    int MX_LANG_UNKNOWN			= 255;
    int MX_LANG_ENGLISH			= 0;
    int MX_LANG_ZHCN			= 1;
    int MX_LANG_NUM				= 2;

    int MX_TZ_UNKNOWN			= 255;

    String J_INFO_WARN_STR		= "1";
    String J_INFO_REPORT_STR	= "2";
    int J_MT_NOTIFICATION		= 1;
    int J_MT_MESSAGE			= 2;
    int J_RT_IMEI				= 1;
    int J_RT_TAG				= 2;
    int J_RT_ALIAS				= 3;
    int J_RT_ALL				= 4;

    int MX_DEVICE_HEAP_SIZE		= 8192;
    int MX_SOC_QUEUE_NUM		= 32;
    int THREAD_NUM_PER_PORT		= 1;

    int GOOGLE					= 0;
    int BAIDU					= 1;

    int INSERTUPLINK 			= (1 << 0);
    int RESPONSE 				= (1 << 1);
    int INSERTOWNUPLINK 		= (1 << 2);

    int HTTP_SESSION_TIMEOUT	= 5000000;

    int MX_IMEI_LENGTH			= 15;
    int MX_IMSI_LENGTH			= 15;


    int ACC_ON					= 1;
    int ACC_OFF					= 2;

    int CHANGER_INSERTED		= 5;
    int CHANGER_REMOVED			= 6;

    boolean IOS_WITH_MESSAGE	= true;

    int PROTOCOL_HONGYUAN		= 1;
    int PROTOCOL_EELINK2		= 3;
    int PROTOCOL_EELINK1		= 1;

    String DRFMT				= "yyyy-MM-dd HH:mm:ss";
    long DT20160101000000		= 0x5685C180L * 1000;

    int BSINFOSIZE				= 7;

    int BEACON_INFO_SIZE		= 16;

    int RECV_HANDLE_MAX_NUM		= 24;

    int QUEUE_SQL				= 0;
    int QUEUE_JPUSH				= 1;
    int QUEUE_NUM				= 2;

    double DX					= 1800000.0;

    int MAX_BS_NUM				= 3;
    int MAX_WIFI_NUM			= 3;
    int BS_3G_FLAG				= 0x1000000;

    int MAX_JPUSH_PROFILES		= 8;

    int SCAN_DOWNLINK_NUM		= BUSINESS_EDITION ? 1 : 2;

    enum DataType {
        pkt_hand,
        pkt_log,
        pkt_gps,
        pkt_beat,
        pkt_status,
        pkt_warn,
        pkt_sms,
        pkt_obd,
        pkt_pdm,
        pkt_cmd,
        pkt_db,
        pkt_bwl,
        pkt_ibt,
        pkt_ibt_rep,
        pkt_ukn
    }

    int GPS_EXTEND_LEN			= 384;

    int MAXTABLELEN				= 1024;

    int MASK_GPS				= (1 << 0);
    int MASK_BSID0				= (1 << 1);
    int MASK_BSID1				= (1 << 2);
    int MASK_BSID2				= (1 << 3);
    int MASK_BSS0				= (1 << 4);
    int MASK_BSS1				= (1 << 5);
    int MASK_BSS2				= (1 << 6);

    int MX_PASSWORD_LENGTH		= 8;
    int MX_NUMBER_LENGTH		= 40;
    int MX_ALIAS_LENGTH			= 24;
    int MX_NOTE_LENGTH			= 60;
    int MC_MANAGER_COUNT		= 4;
    int MC_ALARM_COUNT			= 8;
    int MC_FENCE_COUNT			= 8;

    int MC_DATABASE				= 0x0001;

    int MARK_QUERY_VER			= 0x4c754c71;
    int MARK_UPDATE				= 0x4c754c72;
    int MARK_LAMPSTANDARD		= 0x4c754c73;
    int MARK_QUERY_LANG			= 0x4c754c74;

    String SQL_INI				= "SQL_INI";

    int REGION_MAINLAND			= 0;
    int REGION_HK				= 1;
    int REGION_MACAO			= 2;
    int REGION_TAIWAN			= 3;
    int REGION_OTHER			= 4;

    String[][] __globalStr 		=
            {
                    { "SOS!",                                    				"SOS报警!" },
                    { "Warning: vibrating!",                     				"振动报警!" },
                    { "Warning: external power shutdown!",       				"断电报警!" },
                    { "Warning: too low battery!",               				"电池低电量报警!" },
                    { "Warning: open-circuit antenna!",          				"天线断路报警!" },
                    { "Warning: short-circuit antenna!",         				"天线短路报警!" },
                    { "Warning: crash!",                         				"碰撞报警!" },
                    { "Warning: free fall!",                     				"跌落报警!" },
                    { "Warning: too low speed!",                 				"低速报警!" },
                    { "Warning: abnormal speed!",                				"速度超限!" },
                    { "Warning: into fence!",                    				"入围栏报警!" },
                    { "Warning: out of fence!",                  				"出围栏报警!" },
                    { "Warning: shifting!",                      				"位移报警!" },
                    { "Warning: motion!",                        				"振动报警!" },
                    { "Warning: abnormal temperature!",          				"温度异常!" },
                    { "Warning: abnormal humidity!",             				"湿度异常!" },
                    { "Warning: abnormal luminance!",            				"亮度异常!" },
                    { "Warning: abnormal concentration of CO2!", 				"CO2浓度异常!" },
                    { "Warning: abnormal probe temperature!",    				"探针温度异常!" },
                    { "Warning: enters bright environment!",     				"进入明亮的环境!" },
                    { "Warning!",                                				"报警!" },
                    { "Warning: Your device is removed from the vehicle!",   	"您的设备被拆除!" },
                    { "Trip start time",                         				"行程起始时间" },
                    { "Trip stop time",                          				"行程结束时间" },
                    { "Travel time",                            				"行程用时" },
                    { "Mileage",                        				        "里程" },
                    { "Fuel consumption",                        				"油耗" },
                    { "Origin",                                  				"起点" },
                    { "Destination",                             				"终点" },
                    { "ACC On",                                  				"已点火" },
                    { "ACC Off",                                 				"已熄火" },
                    { "Your device is online",                   				"设备上线" },
                    { "Battery",                                 				"电量" },
                    { "Warning",                                				"警报" },
                    { "Notification",                            				"通知" },
                    { "Trip Report",                             				"行程报告" },
                    { "km/h", 													"千米/小时" },
                    { "mV", 													"mV" },
                    { "℃", 													"℃" },
                    { "%RH",													"%RH" },
                    { "lx",														"lx" },
                    { "ppm",													"ppm" },
                    { "Device_ID",												"设备号" },
                    { "Driver_ID",												"司机ID" },
                    { "Start Time",												"启动时间" },
                    { "iButton Report",											"iButton 报告" },
            };

    int GOOME_NUMBER_LENGTH 	= 21;
    int MC_PID_COUNT 			= 256;

    enum Sensor {
        MC_SNR_TEMPOR,
        MC_SNR_HUMIDITY,
        MC_SNR_LIGHT,
        MC_SNR_CO2,
        MC_SNR_PROBE,
        MC_SNR_RESERVED,	// Reserved for future use

        MC_SNR_COUNT
    }

    int EZ_END_SYMBOL 			= 256;
    int EZ_MIN_LENGTH 			= 4;
    int EZ_MAX_LENGTH			= 258;
    int EZ_MIN_OFFSET 			= EZ_MIN_LENGTH;
    int EZ_MAX_OFFSET 			= 65535;
    char[] EZ_MARK 				= { 0x00, 0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3F, 0x7F, 0xFF };

    int ID_LEN 					= 12;
    int COMMAND_POS 			= 13;
    int PACKET_HEADER_LEN 		= 17;

    int MX_BUFFER_SIZE			= 2048;
    int LINK_DATA_LENGTH		= 192;

    int TIMEOUT_DISCONNECT		= 1;
}
