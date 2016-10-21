package com.sap.bulletinboard.ads.controllers;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.validator.constraints.NotBlank;

import com.sap.bulletinboard.ads.models.Advertisement;

/**
 * A Data Transfer Object (DTO) is only a data structure without logic.
 * 
 * Note: This class implements also the mapping between DTO and Entity and vice versa
 */
public class AdvertisementDto {
    private Long id;

    @NotBlank
    public String title;

    public MetaData metadata = new MetaData();

    /**
     * Default constructor required by Jackson JSON Converter
     */
    public AdvertisementDto() {
    }

    public AdvertisementDto(String title) {
        this.title = title;
    }

    public AdvertisementDto(Advertisement ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.metadata.createdAt = convertToDateTime(ad.getCreatedAt());
        this.metadata.modifiedAt = convertToDateTime(ad.getModifiedAt());
        this.metadata.version = ad.getVersion();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // use only in tests
    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Advertisement toEntity() {
        // does not map "read-only" attributes
        Advertisement ad = new Advertisement(id, metadata.version);
        ad.setTitle(title);
        return ad;
    }

    private String convertToDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // ISO 8601
    }

    public static class MetaData {
        public String createdAt;
        public String modifiedAt;
        public long version = 0L;
    }

    @Override
    public String toString() {
        return "Advertisement [id=" + id + ", title=" + title + " (modified at: " + metadata.modifiedAt + ")]";
    }

}
