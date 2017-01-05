package com.bitlove.fetlife.model.api;

import com.bitlove.fetlife.FetLifeApplication;

/**
 * Created by Titan on 1/2/2017.
 */

public class OfflineTestService extends FetLifeService {

    private final OfflineTestApi fetLifeApi;

    public OfflineTestService(FetLifeApplication fetLifeApplication) throws Exception {
        super(fetLifeApplication);
        fetLifeApi = new OfflineTestApi(fetLifeApplication);
    }

    @Override
    public FetLifeApi getFetLifeApi() {
        return fetLifeApi;
    }
}
