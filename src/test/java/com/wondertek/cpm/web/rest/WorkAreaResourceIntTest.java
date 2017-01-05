package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.WorkArea;
import com.wondertek.cpm.repository.WorkAreaRepository;
import com.wondertek.cpm.service.WorkAreaService;
import com.wondertek.cpm.repository.search.WorkAreaSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the WorkAreaResource REST controller.
 *
 * @see WorkAreaResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class WorkAreaResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Inject
    private WorkAreaRepository workAreaRepository;

    @Inject
    private WorkAreaService workAreaService;

    @Inject
    private WorkAreaSearchRepository workAreaSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restWorkAreaMockMvc;

    private WorkArea workArea;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorkAreaResource workAreaResource = new WorkAreaResource();
        ReflectionTestUtils.setField(workAreaResource, "workAreaService", workAreaService);
        this.restWorkAreaMockMvc = MockMvcBuilders.standaloneSetup(workAreaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkArea createEntity(EntityManager em) {
        WorkArea workArea = new WorkArea()
                .name(DEFAULT_NAME);
        return workArea;
    }

    @Before
    public void initTest() {
        workAreaSearchRepository.deleteAll();
        workArea = createEntity(em);
    }

    @Test
    @Transactional
    public void createWorkArea() throws Exception {
        int databaseSizeBeforeCreate = workAreaRepository.findAll().size();

        // Create the WorkArea

        restWorkAreaMockMvc.perform(post("/api/work-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workArea)))
            .andExpect(status().isCreated());

        // Validate the WorkArea in the database
        List<WorkArea> workAreaList = workAreaRepository.findAll();
        assertThat(workAreaList).hasSize(databaseSizeBeforeCreate + 1);
        WorkArea testWorkArea = workAreaList.get(workAreaList.size() - 1);
        assertThat(testWorkArea.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the WorkArea in ElasticSearch
        WorkArea workAreaEs = workAreaSearchRepository.findOne(testWorkArea.getId());
        assertThat(workAreaEs).isEqualToComparingFieldByField(testWorkArea);
    }

    @Test
    @Transactional
    public void createWorkAreaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = workAreaRepository.findAll().size();

        // Create the WorkArea with an existing ID
        WorkArea existingWorkArea = new WorkArea();
        existingWorkArea.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkAreaMockMvc.perform(post("/api/work-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingWorkArea)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<WorkArea> workAreaList = workAreaRepository.findAll();
        assertThat(workAreaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllWorkAreas() throws Exception {
        // Initialize the database
        workAreaRepository.saveAndFlush(workArea);

        // Get all the workAreaList
        restWorkAreaMockMvc.perform(get("/api/work-areas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workArea.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getWorkArea() throws Exception {
        // Initialize the database
        workAreaRepository.saveAndFlush(workArea);

        // Get the workArea
        restWorkAreaMockMvc.perform(get("/api/work-areas/{id}", workArea.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(workArea.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWorkArea() throws Exception {
        // Get the workArea
        restWorkAreaMockMvc.perform(get("/api/work-areas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorkArea() throws Exception {
        // Initialize the database
        workAreaService.save(workArea);

        int databaseSizeBeforeUpdate = workAreaRepository.findAll().size();

        // Update the workArea
        WorkArea updatedWorkArea = workAreaRepository.findOne(workArea.getId());
        updatedWorkArea
                .name(UPDATED_NAME);

        restWorkAreaMockMvc.perform(put("/api/work-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWorkArea)))
            .andExpect(status().isOk());

        // Validate the WorkArea in the database
        List<WorkArea> workAreaList = workAreaRepository.findAll();
        assertThat(workAreaList).hasSize(databaseSizeBeforeUpdate);
        WorkArea testWorkArea = workAreaList.get(workAreaList.size() - 1);
        assertThat(testWorkArea.getName()).isEqualTo(UPDATED_NAME);

        // Validate the WorkArea in ElasticSearch
        WorkArea workAreaEs = workAreaSearchRepository.findOne(testWorkArea.getId());
        assertThat(workAreaEs).isEqualToComparingFieldByField(testWorkArea);
    }

    @Test
    @Transactional
    public void updateNonExistingWorkArea() throws Exception {
        int databaseSizeBeforeUpdate = workAreaRepository.findAll().size();

        // Create the WorkArea

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWorkAreaMockMvc.perform(put("/api/work-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workArea)))
            .andExpect(status().isCreated());

        // Validate the WorkArea in the database
        List<WorkArea> workAreaList = workAreaRepository.findAll();
        assertThat(workAreaList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteWorkArea() throws Exception {
        // Initialize the database
        workAreaService.save(workArea);

        int databaseSizeBeforeDelete = workAreaRepository.findAll().size();

        // Get the workArea
        restWorkAreaMockMvc.perform(delete("/api/work-areas/{id}", workArea.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean workAreaExistsInEs = workAreaSearchRepository.exists(workArea.getId());
        assertThat(workAreaExistsInEs).isFalse();

        // Validate the database is empty
        List<WorkArea> workAreaList = workAreaRepository.findAll();
        assertThat(workAreaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchWorkArea() throws Exception {
        // Initialize the database
        workAreaService.save(workArea);

        // Search the workArea
        restWorkAreaMockMvc.perform(get("/api/_search/work-areas?query=id:" + workArea.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workArea.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
