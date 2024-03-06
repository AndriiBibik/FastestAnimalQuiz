package fastest.animal.quiz;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.ArrayList;
import java.util.List;

public class AdsUtils {

    private String testDeviceXiaomiMi9SE;
    private String testDeviceLgLeon;
    private String testDeviceEmulator = AdRequest.DEVICE_ID_EMULATOR;

    public AdsUtils(Context context) {
        testDeviceXiaomiMi9SE = context.getString(R.string.TEST_DEVICE_XIAOMI_MI_9_SE);
        testDeviceLgLeon = context.getString(R.string.TEST_DEVICE_LG_LEON);
    }

    public List<String> getTestDevices() {
        List<String> testDevices = new ArrayList<>();

        testDevices.add(testDeviceEmulator);
        testDevices.add(testDeviceLgLeon);
        testDevices.add(testDeviceXiaomiMi9SE);

        return testDevices;
    }

    public void setRequestConfigurationForTestDevices() {
        RequestConfiguration requestConfiguration
                = new RequestConfiguration.Builder()
                .setTestDeviceIds(getTestDevices())
                .build();

        MobileAds.setRequestConfiguration(requestConfiguration);
    }
}
