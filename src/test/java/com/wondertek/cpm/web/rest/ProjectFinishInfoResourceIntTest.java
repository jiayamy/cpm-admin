package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.ProjectFinishInfo;
import com.wondertek.cpm.repository.ProjectFinishInfoRepository;
import com.wondertek.cpm.service.ProjectFinishInfoService;
import com.wondertek.cpm.repository.search.ProjectFinishInfoSearchRepository;

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
 * Test class for the ProjectFinishInfoResource REST controller.
 *
 * @see ProjectFinishInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class ProjectFinishInfoResourceIntTest {

    private static final Long DEFAULT_PROJECT_ID = 1L;
    private static final Long UPDATED_PROJECT_ID = 2L;

    private static final Double DEFAULT_FINISH_RATE = 1D;
    private static final Double UPDATED_FINISH_RATE = 2D;

    private static final String DEFAULT_CREATOR = "AAAAAAAAAA";
    private static final String UPDATED_CREATOR = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private ProjectFinishInfoRepository projectFinishInfoRepository;

    @Inject
    private ProjectFinishInfoService projectFinishInfoService;

    @Inject
    private ProjectFinishInfoSearchRepository projectFinishInfoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProjectFinishInfoMockMvc;

    private ProjectFinishInfo projectFinishInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectFinishInfoResource projectFinishInfoResource = new ProjectFinishInfoResource();
        ReflectionTestUtils.setField(projectFinishInfoResource, "projectFinishInfoService", projectFinishInfoService);
        this.restProjectFinishInfoMockMvc = MockMvcBuilders.standaloneSetup(projectFinishInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectFinishInfo createEntity(EntityManager em) {
        ProjectFinishInfo projectFinishInfo = new ProjectFinishInfo()
                .projectId(DEFAULT_PROJECT_ID)
                .finishRate(DEFAULT_FINISH_RATE)
                .creator(DEFAULT_CREATOR)
                .createTime(DEFAULT_CREATE_TIME);
        return projectFinishInfo;
    }

    @Before
    public void initTest() {
        projectFinishInfoSearchRepository.deleteAll();
        projectFinishInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createProjectFinishInfo() throws Exception {
        int databaseSizeBeforeCreate = projectFinishInfoRepository.findAll().size();

        // Create the ProjectFinishInfo

        restProjectFinishInfoMockMvc.perform(post("/api/project-finish-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectFinishInfo)))
            .andExpect(status().isCreated());

        // Validate the ProjectFinishInfo in the database
        List<ProjectFinishInfo> projectFinishInfoList = projectFinishInfoRepository.findAll();
        assertThat(projectFinishInfoList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectFinishInfo testProjectFinishInfo = projectFinishInfoList.get(projectFinishInfoList.size() - 1);
        assertThat(testProjectFinishInfo.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testProjectFinishInfo.getFinishRate()).isEqualTo(DEFAULT_FINISH_RATE);
        assertThat(testProjectFinishInfo.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testProjectFinishInfo.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);

        // Validate the ProjectFinishInfo in ElasticSearch
        ProjectFinishInfo projectFinishInfoEs = projectFinishInfoSearchRepository.findOne(testProjectFinishInfo.getId());
        assertThat(projectFinishInfoEs).isEqualToComparingFieldByField(testProjectFinishInfo);
    }

    @Test
    @Transactional
    public void createProjectFinishInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectFinishInfoRepository.findAll().size();

        // Create the ProjectFinishInfo with an existing ID
        ProjectFinishInfo existingProjectFinishInfo = new ProjectFinishInfo();
        existingProjectFinishInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectFinishInfoMockMvc.perform(post("/api/project-finish-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingProjectFinishInfo)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProjectFinishInfo> projectFinishInfoList = projectFinishInfoRepository.findAll();
        assertThat(projectFinishInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProjectFinishInfos() throws Exception {
        // Initialize the database
        projectFinishInfoRepository.saveAndFlush(projectFinishInfo);

        // Get all the projectFinishInfoList
        restProjectFinishInfoMockMvc.perform(get("/api/project-finish-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectFinishInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].finishRate").value(hasItem(DEFAULT_FINISH_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))));
    }

    @Test
    @Transactional
    public void getProjectFinishInfo() throws Exception {
        // Initialize the database
        projectFinishInfoRepository.saveAndFlush(projectFinishInfo);

        // Get the projectFinishInfo
        restProjectFinishInfoMockMvc.perform(get("/api/project-finish-infos/{id}", projectFinishInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectFinishInfo.getId().intValue()))
            .andExpect(jsonPath("$.projectId").value(DEFAULT_PROJECT_ID.intValue()))
            .andExpect(jsonPath("$.finishRate").value(DEFAULT_FINISH_RATE.doubleValue()))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingProjectFinishInfo() throws Exception {
        // Get the projectFinishInfo
        restProjectFinishInfoMockMvc.perform(get("/api/project-finish-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectFinishInfo() throws Exception {
        // Initialize the database
        projectFinishInfoService.save(projectFinishInfo);

        int databaseSizeBeforeUpdate = projectFinishInfoRepository.findAll().size();

        // Update the projectFinishInfo
        ProjectFinishInfo updatedProjectFinishInfo = projectFinishInfoRepository.findOne(projectFinishInfo.getId());
        updatedProjectFinishInfo
                .projectId(UPDATED_PROJECT_ID)
                .finishRate(UPDATED_FINISH_RATE)
                .creator(UPDATED_CREATOR)
                .createTime(UPDATED_CREATE_TIME);

        restProjectFinishInfoMockMvc.perform(put("/api/project-finish-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProjectFinishInfo)))
            .andExpect(status().isOk());

        // Validate the ProjectFinishInfo in the database
        List<ProjectFinishInfo> projectFinishInfoList = projectFinishInfoRepository.findAll();
        assertThat(projectFinishInfoList).hasSize(databaseSizeBeforeUpdate);
        ProjectFinishInfo testProjectFinishInfo = projectFinishInfoList.get(projectFinishInfoList.size() - 1);
        assertThat(testProjectFinishInfo.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testProjectFinishInfo.getFinishRate()).isEqualTo(UPDATED_FINISH_RATE);
        assertThat(testProjectFinishInfo.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testProjectFinishInfo.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);

        // Validate the ProjectFinishInfo in ElasticSearch
        ProjectFinishInfo projectFinishInfoEs = projectFinishInfoSearchRepository.findOne(testProjectFinishInfo.getId());
        assertThat(projectFinishInfoEs).isEqualToComparingFieldByField(testProjectFinishInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingProjectFinishInfo() throws Exception {
        int databaseSizeBeforeUpdate = projectFinishInfoRepository.findAll().size();

        // Create the ProjectFinishInfo

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProjectFinishInfoMockMvc.perform(put("/api/project-finish-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectFinishInfo)))
            .andExpect(status().isCreated());

        // Validate the ProjectFinishInfo in the database
        List<ProjectFinishInfo> projectFinishInfoList = projectFinishInfoRepository.findAll();
        assertThat(projectFinishInfoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProjectFinishInfo() throws Exception {
        // Initialize the database
        projectFinishInfoService.save(projectFinishInfo);

        int databaseSizeBeforeDelete = projectFinishInfoRepository.findAll().size();

        // Get the projectFinishInfo
        restProjectFinishInfoMockMvc.perform(delete("/api/project-finish-infos/{id}", projectFinishInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean projectFinishInfoExistsInEs = projectFinishInfoSearchRepository.exists(projectFinishInfo.getId());
        assertThat(projectFinishInfoExistsInEs).isFalse();

        // Validate the database is empty
        List<ProjectFinishInfo> projectFinishInfoList = projectFinishInfoRepository.findAll();
        assertThat(projectFinishInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectFinishInfo() throws Exception {
        // Initialize the database
        projectFinishInfoService.save(projectFinishInfo);

        // Search the projectFinishInfo
        restProjectFinishInfoMockMvc.perform(get("/api/_search/project-finish-infos?query=id:" + projectFinishInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectFinishInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].finishRate").value(hasItem(DEFAULT_FINISH_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))));
    }
}
