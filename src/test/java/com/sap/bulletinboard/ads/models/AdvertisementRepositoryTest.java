package com.sap.bulletinboard.ads.models;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.sql.Timestamp;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sap.bulletinboard.ads.config.EmbeddedDatabaseConfig;
import com.sap.bulletinboard.ads.controllers.AdvertisementDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmbeddedDatabaseConfig.class)
public class AdvertisementRepositoryTest {

    @Inject
    private AdvertisementRepository repo;
    private Advertisement entity;

    @Before
    public void setUp() {
        entity = new Advertisement();
        entity.setTitle("SOME title");
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll();
        assertThat(repo.count(), is(0L));
    }

    @Test
    public void shouldSetIdOnFirstSave() {
        entity = repo.save(entity);
        assertThat(entity.getId(), is(notNullValue()));
    }

    @Test
    public void shouldSetCreatedTimestampOnFirstSaveOnly() throws InterruptedException {
        entity = repo.save(entity);
        Timestamp timestampAfterCreation = entity.getCreatedAt();
        assertThat(timestampAfterCreation, is(notNullValue()));

        Advertisement entityUpdated = new Advertisement(entity.getId(), entity.getVersion());
        entityUpdated.setTitle("Updated Title");
        Thread.sleep(2); // Better: mock
        entityUpdated.setCreatedAt(Advertisement.now());
        repo.save(entityUpdated);

        Advertisement entityAfterUpdate = repo.findOne(entity.getId());
        assertThat(entityAfterUpdate.getCreatedAt(), is(timestampAfterCreation));
    }

    @Test
    public void shouldSetUpdatedTimestampOnEveryUpdate() throws InterruptedException {
        entity = repo.save(entity);

        Advertisement entityFirstUpdate = new Advertisement(entity.getId(), entity.getVersion());
        entityFirstUpdate.setTitle("Updated Title");
        Advertisement entityAfterFirstUpdate = repo.save(entityFirstUpdate);
        Timestamp timestampAfterFirstUpdate = entityAfterFirstUpdate.getModifiedAt();
        assertThat(timestampAfterFirstUpdate, is(notNullValue()));

        Thread.sleep(2); // Better: mock time!
        Advertisement entitySecondUpdate = new Advertisement(entity.getId(), entityAfterFirstUpdate.getVersion());
        entitySecondUpdate.setTitle("Updated Title 2");
        Timestamp timestampAfterSecondUpdate = repo.save(entitySecondUpdate).getModifiedAt();
        assertThat(timestampAfterSecondUpdate, is(not(timestampAfterFirstUpdate)));
    }

    @Test(expected = JpaOptimisticLockingFailureException.class)
    public void shouldUseVersionForConflicts() {
        entity.setTitle("some title");
        entity = repo.save(entity); // persists entity and sets initial version

        entity.setTitle("entity instance 1");
        Advertisement updatedEntity = repo.save(entity); // returns instance with updated version

        repo.save(entity); // tries to persist entity with outdated version
    }

    @Test
    public void fromEntityToDto() {
        Advertisement entity = repo.save(new Advertisement("some title"));
        AdvertisementDto dto = new AdvertisementDto(entity);

        assertThat("" + dto.getId(), not(isEmptyOrNullString()));
        assertThat(dto.metadata.version, is(1L));
        assertThat(dto.title, is("some title"));
    }

    @Test
    public void fromDtoToEntity() {
        Advertisement entity = repo.save(new Advertisement("some title"));
        AdvertisementDto dto = new AdvertisementDto(entity);

        Advertisement dtoEntity = dto.toEntity();
        assertThat(dtoEntity.getId(), is(dto.getId()));
        assertThat(dtoEntity.getTitle(), is(dto.title));
        assertThat(dtoEntity.getVersion(), is(dto.metadata.version));
    }
    
    @Test
    public void shouldFindByTitle() {
        String title = "Find me";

        entity.setTitle(title);
        repo.save(entity);

        Advertisement foundEntity = repo.findByTitle(title).get(0);
        assertThat(foundEntity.getTitle(), is(title));
    }
}
