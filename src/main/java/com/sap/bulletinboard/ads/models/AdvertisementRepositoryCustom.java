package com.sap.bulletinboard.ads.models;

import java.util.List;

public interface AdvertisementRepositoryCustom {
    List<Advertisement> findByTitle(String string);
}
