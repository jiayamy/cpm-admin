package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.service.ProjectCostService;
import com.wondertek.cpm.repository.search.ProjectCostSearchRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.wondertek.cpm.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProjectCostResource REST controller.
 *
 * @see ProjectCostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class ProjectCostResourceIntTest {

    private static final Long DEFAULT_PROJECT_ID = 1L;
    private static final Long UPDATED_PROJECT_ID = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_TYPE = 1;
    private static final Integer UPDATED_TYPE = 2;

    private static final Double DEFAULT_TOTAL = 1D;
    private static final Double UPDATED_TOTAL = 2D;

    private static final String DEFAULT_COST_DESC = "AAAAAAAAAA";
    private static final String UPDATED_COST_DESC = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final String DEFAULT_CREATOR = "AAAAAAAAAA";
    private static final String UPDATED_CREATOR = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_UPDATOR = "AAAAAAAAAA";
    private static final String UPDATED_UPDATOR = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private ProjectCostRepository projectCostRepository;

    @Inject
    private ProjectCostService projectCostService;

    @Inject
    private ProjectCostSearchRepository projectCostSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProjectCostMockMvc;

    private ProjectCost projectCost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectCostResource projectCostResource = new ProjectCostResource();
        ReflectionTestUtils.setField(projectCostResource, "projectCostService", projectCostService);
        this.restProjectCostMockMvc = MockMvcBuilders.standaloneSetup(projectCostResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectCost createEntity(EntityManager em) {
        ProjectCost projectCost = new ProjectCost()
                .projectId(DEFAULT_PROJECT_ID)
                .name(DEFAULT_NAME)
                .type(DEFAULT_TYPE)
                .total(DEFAULT_TOTAL)
                .costDesc(DEFAULT_COST_DESC)
                .status(DEFAULT_STATUS)
                .creator(DEFAULT_CREATOR)
                .createTime(DEFAULT_CREATE_TIME)
                .updator(DEFAULT_UPDATOR)
                .updateTime(DEFAULT_UPDATE_TIME);
        return projectCost;
    }

    @Before
    public void initTest() {
        projectCostSearchRepository.deleteAll();
        projectCost = createEntity(em);
    }

    @Test
    @Transactional
    public void createProjectCost() throws Exception {
        int databaseSizeBeforeCreate = projectCostRepository.findAll().size();

        // Create the ProjectCost

        restProjectCostMockMvc.perform(post("/api/project-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectCost)))
            .andExpect(status().isCreated());

        // Validate the ProjectCost in the database
        List<ProjectCost> projectCostList = projectCostRepository.findAll();
        assertThat(projectCostList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectCost testProjectCost = projectCostList.get(projectCostList.size() - 1);
        assertThat(testProjectCost.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testProjectCost.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProjectCost.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testProjectCost.getTotal()).isEqualTo(DEFAULT_TOTAL);
        assertThat(testProjectCost.getCostDesc()).isEqualTo(DEFAULT_COST_DESC);
        assertThat(testProjectCost.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProjectCost.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testProjectCost.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testProjectCost.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
        assertThat(testProjectCost.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);

        // Validate the ProjectCost in ElasticSearch
        ProjectCost projectCostEs = projectCostSearchRepository.findOne(testProjectCost.getId());
        assertThat(projectCostEs).isEqualToComparingFieldByField(testProjectCost);
    }

    @Test
    @Transactional
    public void createProjectCostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectCostRepository.findAll().size();

        // Create the ProjectCost with an existing ID
        ProjectCost existingProjectCost = new ProjectCost();
        existingProjectCost.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectCostMockMvc.perform(post("/api/project-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingProjectCost)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProjectCost> projectCostList = projectCostRepository.findAll();
        assertThat(projectCostList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProjectCosts() throws Exception {
        // Initialize the database
        projectCostRepository.saveAndFlush(projectCost);

        // Get all the projectCostList
        restProjectCostMockMvc.perform(get("/api/project-costs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectCost.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].costDesc").value(hasItem(DEFAULT_COST_DESC.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }

    @Test
    @Transactional
    public void getProjectCost() throws Exception {
        // Initialize the database
        projectCostRepository.saveAndFlush(projectCost);

        // Get the projectCost
        restProjectCostMockMvc.perform(get("/api/project-costs/{id}", projectCost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectCost.getId().intValue()))
            .andExpect(jsonPath("$.projectId").value(DEFAULT_PROJECT_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.costDesc").value(DEFAULT_COST_DESC.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingProjectCost() throws Exception {
        // Get the projectCost
        restProjectCostMockMvc.perform(get("/api/project-costs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectCost() throws Exception {
        // Initialize the database
        projectCostService.save(projectCost);

        int databaseSizeBeforeUpdate = projectCostRepository.findAll().size();

        // Update the projectCost
        ProjectCost updatedProjectCost = projectCostRepository.findOne(projectCost.getId());
        updatedProjectCost
                .projectId(UPDATED_PROJECT_ID)
                .name(UPDATED_NAME)
                .type(UPDATED_TYPE)
                .total(UPDATED_TOTAL)
                .costDesc(UPDATED_COST_DESC)
                .status(UPDATED_STATUS)
                .creator(UPDATED_CREATOR)
                .createTime(UPDATED_CREATE_TIME)
                .updator(UPDATED_UPDATOR)
                .updateTime(UPDATED_UPDATE_TIME);

        restProjectCostMockMvc.perform(put("/api/project-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProjectCost)))
            .andExpect(status().isOk());

        // Validate the ProjectCost in the database
        List<ProjectCost> projectCostList = projectCostRepository.findAll();
        assertThat(projectCostList).hasSize(databaseSizeBeforeUpdate);
        ProjectCost testProjectCost = projectCostList.get(projectCostList.size() - 1);
        assertThat(testProjectCost.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testProjectCost.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProjectCost.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testProjectCost.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testProjectCost.getCostDesc()).isEqualTo(UPDATED_COST_DESC);
        assertThat(testProjectCost.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProjectCost.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testProjectCost.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testProjectCost.getUpdator()).isEqualTo(UPDATED_UPDATOR);
        assertThat(testProjectCost.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);

        // Validate the ProjectCost in ElasticSearch
        ProjectCost projectCostEs = projectCostSearchRepository.findOne(testProjectCost.getId());
        assertThat(projectCostEs).isEqualToComparingFieldByField(testProjectCost);
    }

    @Test
    @Transactional
    public void updateNonExistingProjectCost() throws Exception {
        int databaseSizeBeforeUpdate = projectCostRepository.findAll().size();

        // Create the ProjectCost

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProjectCostMockMvc.perform(put("/api/project-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectCost)))
            .andExpect(status().isCreated());

        // Validate the ProjectCost in the database
        List<ProjectCost> projectCostList = projectCostRepository.findAll();
        assertThat(projectCostList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProjectCost() throws Exception {
        // Initialize the database
        projectCostService.save(projectCost);

        int databaseSizeBeforeDelete = projectCostRepository.findAll().size();

        // Get the projectCost
        restProjectCostMockMvc.perform(delete("/api/project-costs/{id}", projectCost.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean projectCostExistsInEs = projectCostSearchRepository.exists(projectCost.getId());
        assertThat(projectCostExistsInEs).isFalse();

        // Validate the database is empty
        List<ProjectCost> projectCostList = projectCostRepository.findAll();
        assertThat(projectCostList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectCost() throws Exception {
        // Initialize the database
        projectCostService.save(projectCost);

        // Search the projectCost
        restProjectCostMockMvc.perform(get("/api/_search/project-costs?query=id:" + projectCost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectCost.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].costDesc").value(hasItem(DEFAULT_COST_DESC.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }
}
