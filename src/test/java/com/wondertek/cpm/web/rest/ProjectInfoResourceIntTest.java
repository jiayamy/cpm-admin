package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.repository.search.ProjectInfoSearchRepository;

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
 * Test class for the ProjectInfoResource REST controller.
 *
 * @see ProjectInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class ProjectInfoResourceIntTest {

    private static final String DEFAULT_SERIAL_NUM = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NUM = "BBBBBBBBBB";

    private static final Long DEFAULT_CONTRACT_ID = 1L;
    private static final Long UPDATED_CONTRACT_ID = 2L;

    private static final Long DEFAULT_BUDGET_ID = 1L;
    private static final Long UPDATED_BUDGET_ID = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PM = "AAAAAAAAAA";
    private static final String UPDATED_PM = "BBBBBBBBBB";

    private static final String DEFAULT_DEPT = "AAAAAAAAAA";
    private static final String UPDATED_DEPT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Double DEFAULT_BUDGET_TOTAL = 1D;
    private static final Double UPDATED_BUDGET_TOTAL = 2D;

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
    private ProjectInfoRepository projectInfoRepository;

    @Inject
    private ProjectInfoService projectInfoService;

    @Inject
    private ProjectInfoSearchRepository projectInfoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProjectInfoMockMvc;

    private ProjectInfo projectInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectInfoResource projectInfoResource = new ProjectInfoResource();
        ReflectionTestUtils.setField(projectInfoResource, "projectInfoService", projectInfoService);
        this.restProjectInfoMockMvc = MockMvcBuilders.standaloneSetup(projectInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectInfo createEntity(EntityManager em) {
        ProjectInfo projectInfo = new ProjectInfo()
                .serialNum(DEFAULT_SERIAL_NUM)
                .contractId(DEFAULT_CONTRACT_ID)
                .budgetId(DEFAULT_BUDGET_ID)
                .name(DEFAULT_NAME)
                .pm(DEFAULT_PM)
                .dept(DEFAULT_DEPT)
                .startDay(DEFAULT_START_DAY)
                .endDay(DEFAULT_END_DAY)
                .budgetTotal(DEFAULT_BUDGET_TOTAL)
                .status(DEFAULT_STATUS)
                .creator(DEFAULT_CREATOR)
                .createTime(DEFAULT_CREATE_TIME)
                .updator(DEFAULT_UPDATOR)
                .updateTime(DEFAULT_UPDATE_TIME);
        return projectInfo;
    }

    @Before
    public void initTest() {
        projectInfoSearchRepository.deleteAll();
        projectInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createProjectInfo() throws Exception {
        int databaseSizeBeforeCreate = projectInfoRepository.findAll().size();

        // Create the ProjectInfo

        restProjectInfoMockMvc.perform(post("/api/project-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectInfo)))
            .andExpect(status().isCreated());

        // Validate the ProjectInfo in the database
        List<ProjectInfo> projectInfoList = projectInfoRepository.findAll();
        assertThat(projectInfoList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectInfo testProjectInfo = projectInfoList.get(projectInfoList.size() - 1);
        assertThat(testProjectInfo.getSerialNum()).isEqualTo(DEFAULT_SERIAL_NUM);
        assertThat(testProjectInfo.getContractId()).isEqualTo(DEFAULT_CONTRACT_ID);
        assertThat(testProjectInfo.getBudgetId()).isEqualTo(DEFAULT_BUDGET_ID);
        assertThat(testProjectInfo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProjectInfo.getPm()).isEqualTo(DEFAULT_PM);
        assertThat(testProjectInfo.getDept()).isEqualTo(DEFAULT_DEPT);
        assertThat(testProjectInfo.getStartDay()).isEqualTo(DEFAULT_START_DAY);
        assertThat(testProjectInfo.getEndDay()).isEqualTo(DEFAULT_END_DAY);
        assertThat(testProjectInfo.getBudgetTotal()).isEqualTo(DEFAULT_BUDGET_TOTAL);
        assertThat(testProjectInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProjectInfo.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testProjectInfo.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testProjectInfo.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
        assertThat(testProjectInfo.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);

        // Validate the ProjectInfo in ElasticSearch
        ProjectInfo projectInfoEs = projectInfoSearchRepository.findOne(testProjectInfo.getId());
        assertThat(projectInfoEs).isEqualToComparingFieldByField(testProjectInfo);
    }

    @Test
    @Transactional
    public void createProjectInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectInfoRepository.findAll().size();

        // Create the ProjectInfo with an existing ID
        ProjectInfo existingProjectInfo = new ProjectInfo();
        existingProjectInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectInfoMockMvc.perform(post("/api/project-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingProjectInfo)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProjectInfo> projectInfoList = projectInfoRepository.findAll();
        assertThat(projectInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProjectInfos() throws Exception {
        // Initialize the database
        projectInfoRepository.saveAndFlush(projectInfo);

        // Get all the projectInfoList
        restProjectInfoMockMvc.perform(get("/api/project-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].serialNum").value(hasItem(DEFAULT_SERIAL_NUM.toString())))
            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
            .andExpect(jsonPath("$.[*].budgetId").value(hasItem(DEFAULT_BUDGET_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].pm").value(hasItem(DEFAULT_PM.toString())))
            .andExpect(jsonPath("$.[*].dept").value(hasItem(DEFAULT_DEPT.toString())))
            .andExpect(jsonPath("$.[*].startDay").value(hasItem(sameInstant(DEFAULT_START_DAY))))
            .andExpect(jsonPath("$.[*].endDay").value(hasItem(sameInstant(DEFAULT_END_DAY))))
            .andExpect(jsonPath("$.[*].budgetTotal").value(hasItem(DEFAULT_BUDGET_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }

    @Test
    @Transactional
    public void getProjectInfo() throws Exception {
        // Initialize the database
        projectInfoRepository.saveAndFlush(projectInfo);

        // Get the projectInfo
        restProjectInfoMockMvc.perform(get("/api/project-infos/{id}", projectInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectInfo.getId().intValue()))
            .andExpect(jsonPath("$.serialNum").value(DEFAULT_SERIAL_NUM.toString()))
            .andExpect(jsonPath("$.contractId").value(DEFAULT_CONTRACT_ID.intValue()))
            .andExpect(jsonPath("$.budgetId").value(DEFAULT_BUDGET_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.pm").value(DEFAULT_PM.toString()))
            .andExpect(jsonPath("$.dept").value(DEFAULT_DEPT.toString()))
            .andExpect(jsonPath("$.startDay").value(sameInstant(DEFAULT_START_DAY)))
            .andExpect(jsonPath("$.endDay").value(sameInstant(DEFAULT_END_DAY)))
            .andExpect(jsonPath("$.budgetTotal").value(DEFAULT_BUDGET_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingProjectInfo() throws Exception {
        // Get the projectInfo
        restProjectInfoMockMvc.perform(get("/api/project-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectInfo() throws Exception {
        // Initialize the database
        projectInfoService.save(projectInfo);

        int databaseSizeBeforeUpdate = projectInfoRepository.findAll().size();

        // Update the projectInfo
        ProjectInfo updatedProjectInfo = projectInfoRepository.findOne(projectInfo.getId());
        updatedProjectInfo
                .serialNum(UPDATED_SERIAL_NUM)
                .contractId(UPDATED_CONTRACT_ID)
                .budgetId(UPDATED_BUDGET_ID)
                .name(UPDATED_NAME)
                .pm(UPDATED_PM)
                .dept(UPDATED_DEPT)
                .startDay(UPDATED_START_DAY)
                .endDay(UPDATED_END_DAY)
                .budgetTotal(UPDATED_BUDGET_TOTAL)
                .status(UPDATED_STATUS)
                .creator(UPDATED_CREATOR)
                .createTime(UPDATED_CREATE_TIME)
                .updator(UPDATED_UPDATOR)
                .updateTime(UPDATED_UPDATE_TIME);

        restProjectInfoMockMvc.perform(put("/api/project-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProjectInfo)))
            .andExpect(status().isOk());

        // Validate the ProjectInfo in the database
        List<ProjectInfo> projectInfoList = projectInfoRepository.findAll();
        assertThat(projectInfoList).hasSize(databaseSizeBeforeUpdate);
        ProjectInfo testProjectInfo = projectInfoList.get(projectInfoList.size() - 1);
        assertThat(testProjectInfo.getSerialNum()).isEqualTo(UPDATED_SERIAL_NUM);
        assertThat(testProjectInfo.getContractId()).isEqualTo(UPDATED_CONTRACT_ID);
        assertThat(testProjectInfo.getBudgetId()).isEqualTo(UPDATED_BUDGET_ID);
        assertThat(testProjectInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProjectInfo.getPm()).isEqualTo(UPDATED_PM);
        assertThat(testProjectInfo.getDept()).isEqualTo(UPDATED_DEPT);
        assertThat(testProjectInfo.getStartDay()).isEqualTo(UPDATED_START_DAY);
        assertThat(testProjectInfo.getEndDay()).isEqualTo(UPDATED_END_DAY);
        assertThat(testProjectInfo.getBudgetTotal()).isEqualTo(UPDATED_BUDGET_TOTAL);
        assertThat(testProjectInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProjectInfo.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testProjectInfo.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testProjectInfo.getUpdator()).isEqualTo(UPDATED_UPDATOR);
        assertThat(testProjectInfo.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);

        // Validate the ProjectInfo in ElasticSearch
        ProjectInfo projectInfoEs = projectInfoSearchRepository.findOne(testProjectInfo.getId());
        assertThat(projectInfoEs).isEqualToComparingFieldByField(testProjectInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingProjectInfo() throws Exception {
        int databaseSizeBeforeUpdate = projectInfoRepository.findAll().size();

        // Create the ProjectInfo

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProjectInfoMockMvc.perform(put("/api/project-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectInfo)))
            .andExpect(status().isCreated());

        // Validate the ProjectInfo in the database
        List<ProjectInfo> projectInfoList = projectInfoRepository.findAll();
        assertThat(projectInfoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProjectInfo() throws Exception {
        // Initialize the database
        projectInfoService.save(projectInfo);

        int databaseSizeBeforeDelete = projectInfoRepository.findAll().size();

        // Get the projectInfo
        restProjectInfoMockMvc.perform(delete("/api/project-infos/{id}", projectInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean projectInfoExistsInEs = projectInfoSearchRepository.exists(projectInfo.getId());
        assertThat(projectInfoExistsInEs).isFalse();

        // Validate the database is empty
        List<ProjectInfo> projectInfoList = projectInfoRepository.findAll();
        assertThat(projectInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectInfo() throws Exception {
        // Initialize the database
        projectInfoService.save(projectInfo);

        // Search the projectInfo
        restProjectInfoMockMvc.perform(get("/api/_search/project-infos?query=id:" + projectInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].serialNum").value(hasItem(DEFAULT_SERIAL_NUM.toString())))
            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
            .andExpect(jsonPath("$.[*].budgetId").value(hasItem(DEFAULT_BUDGET_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].pm").value(hasItem(DEFAULT_PM.toString())))
            .andExpect(jsonPath("$.[*].dept").value(hasItem(DEFAULT_DEPT.toString())))
            .andExpect(jsonPath("$.[*].startDay").value(hasItem(sameInstant(DEFAULT_START_DAY))))
            .andExpect(jsonPath("$.[*].endDay").value(hasItem(sameInstant(DEFAULT_END_DAY))))
            .andExpect(jsonPath("$.[*].budgetTotal").value(hasItem(DEFAULT_BUDGET_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }
}
