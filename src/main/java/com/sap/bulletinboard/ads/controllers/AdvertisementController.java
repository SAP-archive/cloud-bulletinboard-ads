package com.sap.bulletinboard.ads.controllers;

import static org.springframework.http.HttpStatus.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;

/*
 * Use a path which does not end with a slash! Otherwise the controller is not reachable when not using the trailing
 * slash in the URL
 */
@RestController
@RequestMapping(path = AdvertisementController.PATH)
@RequestScope // @Scope(WebApplicationContext.SCOPE_REQUEST)
@Validated
public class AdvertisementController {
    public static final String PATH = "/api/v1/ads";

    private AdvertisementRepository adRepository;

    @Inject
    public AdvertisementController(AdvertisementRepository repository) {
        this.adRepository = repository;
    }

    @GetMapping
    public AdvertisementList advertisements() {
        return new AdvertisementList((Collection<Advertisement>) adRepository.findAll());
    }

    @GetMapping("/{id}")
    // We do not use primitive "long" type here to avoid unnecessary autoboxing
    public Advertisement advertisementById(@PathVariable("id") @Min(0) Long id) {
        throwIfNonexisting(id);
        return adRepository.findOne(id);
    }

    /**
     * @RequestBody is bound to the method argument. HttpMessageConverter resolves method argument depending on the
     *              content type.
     */
    @PostMapping
    public ResponseEntity<Advertisement> add(@Valid @RequestBody Advertisement advertisement,
            UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        throwIfIdNotNull(advertisement.getId());

        Advertisement savedAdvertisement = adRepository.save(advertisement);

        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}")
                .buildAndExpand(savedAdvertisement.getId());
        return ResponseEntity.created(new URI(uriComponents.getPath())).body(savedAdvertisement);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteAll() {
        adRepository.deleteAll();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        throwIfNonexisting(id);
        adRepository.delete(id);
    }

    @PutMapping("/{id}")
    public Advertisement update(@PathVariable("id") long id, @RequestBody Advertisement updatedAd) {
        throwIfInconsistent(id, updatedAd.getId());
        throwIfNonexisting(id);
        return adRepository.save(updatedAd);
    }

    private static void throwIfIdNotNull(final Long id) {
        if (id != null && id.intValue() != 0) {
            String message = String
                    .format("Remove 'id' property from request or use PUT method to update resource with id = %d", id);
            throw new BadRequestException(message);
        }
    }

    private void throwIfNonexisting(long id) {
        if (!adRepository.exists(id)) {
            throw new NotFoundException(id + " not found");
        }
    }

    private void throwIfInconsistent(Long expected, Long actual) {
        if (!expected.equals(actual)) {
            String message = String.format(
                    "bad request, inconsistent IDs between request and object: request id = %d, object id = %d",
                    expected, actual);
            throw new BadRequestException(message);
        }
    }

    public static class AdvertisementList {
        @JsonProperty("value")
        public List<Advertisement> advertisements = new ArrayList<>();

        public AdvertisementList(Iterable<Advertisement> ads) {
            ads.forEach(advertisements::add);
        }
    }
}