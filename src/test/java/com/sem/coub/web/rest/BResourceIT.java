package com.sem.coub.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sem.coub.IntegrationTest;
import com.sem.coub.domain.B;
import com.sem.coub.repository.BRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BResourceIT {

    private static final String DEFAULT_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_GROUP = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_SATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_SATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/bs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private BRepository bRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBMockMvc;

    private B b;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static B createEntity(EntityManager em) {
        B b = new B().group(DEFAULT_GROUP).startSate(DEFAULT_START_SATE);
        return b;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static B createUpdatedEntity(EntityManager em) {
        B b = new B().group(UPDATED_GROUP).startSate(UPDATED_START_SATE);
        return b;
    }

    @BeforeEach
    public void initTest() {
        b = createEntity(em);
    }

    @Test
    @Transactional
    void createB() throws Exception {
        int databaseSizeBeforeCreate = bRepository.findAll().size();
        // Create the B
        restBMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(b)))
            .andExpect(status().isCreated());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeCreate + 1);
        B testB = bList.get(bList.size() - 1);
        assertThat(testB.getGroup()).isEqualTo(DEFAULT_GROUP);
        assertThat(testB.getStartSate()).isEqualTo(DEFAULT_START_SATE);
    }

    @Test
    @Transactional
    void createBWithExistingId() throws Exception {
        // Create the B with an existing ID
        bRepository.saveAndFlush(b);

        int databaseSizeBeforeCreate = bRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(b)))
            .andExpect(status().isBadRequest());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBS() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        // Get all the bList
        restBMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(b.getId().toString())))
            .andExpect(jsonPath("$.[*].group").value(hasItem(DEFAULT_GROUP)))
            .andExpect(jsonPath("$.[*].startSate").value(hasItem(DEFAULT_START_SATE.toString())));
    }

    @Test
    @Transactional
    void getB() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        // Get the b
        restBMockMvc
            .perform(get(ENTITY_API_URL_ID, b.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(b.getId().toString()))
            .andExpect(jsonPath("$.group").value(DEFAULT_GROUP))
            .andExpect(jsonPath("$.startSate").value(DEFAULT_START_SATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingB() throws Exception {
        // Get the b
        restBMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewB() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        int databaseSizeBeforeUpdate = bRepository.findAll().size();

        // Update the b
        B updatedB = bRepository.findById(b.getId()).get();
        // Disconnect from session so that the updates on updatedB are not directly saved in db
        em.detach(updatedB);
        updatedB.group(UPDATED_GROUP).startSate(UPDATED_START_SATE);

        restBMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedB.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedB))
            )
            .andExpect(status().isOk());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
        B testB = bList.get(bList.size() - 1);
        assertThat(testB.getGroup()).isEqualTo(UPDATED_GROUP);
        assertThat(testB.getStartSate()).isEqualTo(UPDATED_START_SATE);
    }

    @Test
    @Transactional
    void putNonExistingB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().size();
        b.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBMockMvc
            .perform(
                put(ENTITY_API_URL_ID, b.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(b))
            )
            .andExpect(status().isBadRequest());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().size();
        b.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(b))
            )
            .andExpect(status().isBadRequest());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().size();
        b.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(b)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBWithPatch() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        int databaseSizeBeforeUpdate = bRepository.findAll().size();

        // Update the b using partial update
        B partialUpdatedB = new B();
        partialUpdatedB.setId(b.getId());

        restBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedB.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedB))
            )
            .andExpect(status().isOk());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
        B testB = bList.get(bList.size() - 1);
        assertThat(testB.getGroup()).isEqualTo(DEFAULT_GROUP);
        assertThat(testB.getStartSate()).isEqualTo(DEFAULT_START_SATE);
    }

    @Test
    @Transactional
    void fullUpdateBWithPatch() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        int databaseSizeBeforeUpdate = bRepository.findAll().size();

        // Update the b using partial update
        B partialUpdatedB = new B();
        partialUpdatedB.setId(b.getId());

        partialUpdatedB.group(UPDATED_GROUP).startSate(UPDATED_START_SATE);

        restBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedB.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedB))
            )
            .andExpect(status().isOk());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
        B testB = bList.get(bList.size() - 1);
        assertThat(testB.getGroup()).isEqualTo(UPDATED_GROUP);
        assertThat(testB.getStartSate()).isEqualTo(UPDATED_START_SATE);
    }

    @Test
    @Transactional
    void patchNonExistingB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().size();
        b.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, b.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(b))
            )
            .andExpect(status().isBadRequest());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().size();
        b.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(b))
            )
            .andExpect(status().isBadRequest());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().size();
        b.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(b)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteB() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        int databaseSizeBeforeDelete = bRepository.findAll().size();

        // Delete the b
        restBMockMvc
            .perform(delete(ENTITY_API_URL_ID, b.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
