//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.ProjectWeeklyStat;
//import com.wondertek.cpm.repository.ProjectWeeklyStatRepository;
//import com.wondertek.cpm.service.ProjectWeeklyStatService;
//import com.wondertek.cpm.repository.search.ProjectWeeklyStatSearchRepository;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import java.time.Instant;
//import java.time.ZonedDateTime;
//import java.time.ZoneOffset;
//import java.time.ZoneId;
//import java.util.List;
//
//import static com.wondertek.cpm.web.rest.TestUtil.sameInstant;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Test class for the ProjectWeeklyStatResource REST controller.
// *
// * @see ProjectWeeklyStatResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class ProjectWeeklyStatResourceIntTest {
//
//    private static final Long DEFAULT_PROJECT_ID = 1L;
//    private static final Long UPDATED_PROJECT_ID = 2L;
//
//    private static final Double DEFAULT_HUMAN_COST = 1D;
//    private static final Double UPDATED_HUMAN_COST = 2D;
//
//    private static final Double DEFAULT_PAYMENT = 1D;
//    private static final Double UPDATED_PAYMENT = 2D;
//
//    private static final String DEFAULT_STAT_WEEK = "AAAAAAAAAA";
//    private static final String UPDATED_STAT_WEEK = "BBBBBBBBBB";
//
//    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
//
//    @Inject
//    private ProjectWeeklyStatRepository projectWeeklyStatRepository;
//
//    @Inject
//    private ProjectWeeklyStatService projectWeeklyStatService;
//
//    @Inject
//    private ProjectWeeklyStatSearchRepository projectWeeklyStatSearchRepository;
//
//    @Inject
//    private MappingJackson2HttpMessageConverter jacksonMessageConverter;
//
//    @Inject
//    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
//
//    @Inject
//    private EntityManager em;
//
//    private MockMvc restProjectWeeklyStatMockMvc;
//
//    private ProjectWeeklyStat projectWeeklyStat;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        ProjectWeeklyStatResource projectWeeklyStatResource = new ProjectWeeklyStatResource();
//        ReflectionTestUtils.setField(projectWeeklyStatResource, "projectWeeklyStatService", projectWeeklyStatService);
//        this.restProjectWeeklyStatMockMvc = MockMvcBuilders.standaloneSetup(projectWeeklyStatResource)
//            .setCustomArgumentResolvers(pageableArgumentResolver)
//            .setMessageConverters(jacksonMessageConverter).build();
//    }
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static ProjectWeeklyStat createEntity(EntityManager em) {
//        ProjectWeeklyStat projectWeeklyStat = new ProjectWeeklyStat()
//                .projectId(DEFAULT_PROJECT_ID)
//                .humanCost(DEFAULT_HUMAN_COST)
//                .payment(DEFAULT_PAYMENT)
//                .createTime(DEFAULT_CREATE_TIME);
//        return projectWeeklyStat;
//    }
//
//    @Before
//    public void initTest() {
//        projectWeeklyStatSearchRepository.deleteAll();
//        projectWeeklyStat = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createProjectWeeklyStat() throws Exception {
//        int databaseSizeBeforeCreate = projectWeeklyStatRepository.findAll().size();
//
//        // Create the ProjectWeeklyStat
//
//        restProjectWeeklyStatMockMvc.perform(post("/api/project-weekly-stats")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(projectWeeklyStat)))
//            .andExpect(status().isCreated());
//
//        // Validate the ProjectWeeklyStat in the database
//        List<ProjectWeeklyStat> projectWeeklyStatList = projectWeeklyStatRepository.findAll();
//        assertThat(projectWeeklyStatList).hasSize(databaseSizeBeforeCreate + 1);
//        ProjectWeeklyStat testProjectWeeklyStat = projectWeeklyStatList.get(projectWeeklyStatList.size() - 1);
//        assertThat(testProjectWeeklyStat.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
//        assertThat(testProjectWeeklyStat.getHumanCost()).isEqualTo(DEFAULT_HUMAN_COST);
//        assertThat(testProjectWeeklyStat.getPayment()).isEqualTo(DEFAULT_PAYMENT);
//        assertThat(testProjectWeeklyStat.getStatWeek()).isEqualTo(DEFAULT_STAT_WEEK);
//        assertThat(testProjectWeeklyStat.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//
//        // Validate the ProjectWeeklyStat in ElasticSearch
//        ProjectWeeklyStat projectWeeklyStatEs = projectWeeklyStatSearchRepository.findOne(testProjectWeeklyStat.getId());
//        assertThat(projectWeeklyStatEs).isEqualToComparingFieldByField(testProjectWeeklyStat);
//    }
//
//    @Test
//    @Transactional
//    public void createProjectWeeklyStatWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = projectWeeklyStatRepository.findAll().size();
//
//        // Create the ProjectWeeklyStat with an existing ID
//        ProjectWeeklyStat existingProjectWeeklyStat = new ProjectWeeklyStat();
//        existingProjectWeeklyStat.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restProjectWeeklyStatMockMvc.perform(post("/api/project-weekly-stats")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingProjectWeeklyStat)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<ProjectWeeklyStat> projectWeeklyStatList = projectWeeklyStatRepository.findAll();
//        assertThat(projectWeeklyStatList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllProjectWeeklyStats() throws Exception {
//        // Initialize the database
//        projectWeeklyStatRepository.saveAndFlush(projectWeeklyStat);
//
//        // Get all the projectWeeklyStatList
//        restProjectWeeklyStatMockMvc.perform(get("/api/project-weekly-stats?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(projectWeeklyStat.getId().intValue())))
//            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].humanCost").value(hasItem(DEFAULT_HUMAN_COST.doubleValue())))
//            .andExpect(jsonPath("$.[*].payment").value(hasItem(DEFAULT_PAYMENT.doubleValue())))
//            .andExpect(jsonPath("$.[*].statWeek").value(hasItem(DEFAULT_STAT_WEEK.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getProjectWeeklyStat() throws Exception {
//        // Initialize the database
//        projectWeeklyStatRepository.saveAndFlush(projectWeeklyStat);
//
//        // Get the projectWeeklyStat
//        restProjectWeeklyStatMockMvc.perform(get("/api/project-weekly-stats/{id}", projectWeeklyStat.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(projectWeeklyStat.getId().intValue()))
//            .andExpect(jsonPath("$.projectId").value(DEFAULT_PROJECT_ID.intValue()))
//            .andExpect(jsonPath("$.humanCost").value(DEFAULT_HUMAN_COST.doubleValue()))
//            .andExpect(jsonPath("$.payment").value(DEFAULT_PAYMENT.doubleValue()))
//            .andExpect(jsonPath("$.statWeek").value(DEFAULT_STAT_WEEK.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingProjectWeeklyStat() throws Exception {
//        // Get the projectWeeklyStat
//        restProjectWeeklyStatMockMvc.perform(get("/api/project-weekly-stats/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateProjectWeeklyStat() throws Exception {
//        // Initialize the database
//        projectWeeklyStatService.save(projectWeeklyStat);
//
//        int databaseSizeBeforeUpdate = projectWeeklyStatRepository.findAll().size();
//
//        // Update the projectWeeklyStat
//        ProjectWeeklyStat updatedProjectWeeklyStat = projectWeeklyStatRepository.findOne(projectWeeklyStat.getId());
//        updatedProjectWeeklyStat
//                .projectId(UPDATED_PROJECT_ID)
//                .humanCost(UPDATED_HUMAN_COST)
//                .payment(UPDATED_PAYMENT)
//                .createTime(UPDATED_CREATE_TIME);
//
//        restProjectWeeklyStatMockMvc.perform(put("/api/project-weekly-stats")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedProjectWeeklyStat)))
//            .andExpect(status().isOk());
//
//        // Validate the ProjectWeeklyStat in the database
//        List<ProjectWeeklyStat> projectWeeklyStatList = projectWeeklyStatRepository.findAll();
//        assertThat(projectWeeklyStatList).hasSize(databaseSizeBeforeUpdate);
//        ProjectWeeklyStat testProjectWeeklyStat = projectWeeklyStatList.get(projectWeeklyStatList.size() - 1);
//        assertThat(testProjectWeeklyStat.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
//        assertThat(testProjectWeeklyStat.getHumanCost()).isEqualTo(UPDATED_HUMAN_COST);
//        assertThat(testProjectWeeklyStat.getPayment()).isEqualTo(UPDATED_PAYMENT);
//        assertThat(testProjectWeeklyStat.getStatWeek()).isEqualTo(UPDATED_STAT_WEEK);
//        assertThat(testProjectWeeklyStat.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//
//        // Validate the ProjectWeeklyStat in ElasticSearch
//        ProjectWeeklyStat projectWeeklyStatEs = projectWeeklyStatSearchRepository.findOne(testProjectWeeklyStat.getId());
//        assertThat(projectWeeklyStatEs).isEqualToComparingFieldByField(testProjectWeeklyStat);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingProjectWeeklyStat() throws Exception {
//        int databaseSizeBeforeUpdate = projectWeeklyStatRepository.findAll().size();
//
//        // Create the ProjectWeeklyStat
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restProjectWeeklyStatMockMvc.perform(put("/api/project-weekly-stats")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(projectWeeklyStat)))
//            .andExpect(status().isCreated());
//
//        // Validate the ProjectWeeklyStat in the database
//        List<ProjectWeeklyStat> projectWeeklyStatList = projectWeeklyStatRepository.findAll();
//        assertThat(projectWeeklyStatList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteProjectWeeklyStat() throws Exception {
//        // Initialize the database
//        projectWeeklyStatService.save(projectWeeklyStat);
//
//        int databaseSizeBeforeDelete = projectWeeklyStatRepository.findAll().size();
//
//        // Get the projectWeeklyStat
//        restProjectWeeklyStatMockMvc.perform(delete("/api/project-weekly-stats/{id}", projectWeeklyStat.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean projectWeeklyStatExistsInEs = projectWeeklyStatSearchRepository.exists(projectWeeklyStat.getId());
//        assertThat(projectWeeklyStatExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<ProjectWeeklyStat> projectWeeklyStatList = projectWeeklyStatRepository.findAll();
//        assertThat(projectWeeklyStatList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchProjectWeeklyStat() throws Exception {
//        // Initialize the database
//        projectWeeklyStatService.save(projectWeeklyStat);
//
//        // Search the projectWeeklyStat
//        restProjectWeeklyStatMockMvc.perform(get("/api/_search/project-weekly-stats?query=id:" + projectWeeklyStat.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(projectWeeklyStat.getId().intValue())))
//            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].humanCost").value(hasItem(DEFAULT_HUMAN_COST.doubleValue())))
//            .andExpect(jsonPath("$.[*].payment").value(hasItem(DEFAULT_PAYMENT.doubleValue())))
//            .andExpect(jsonPath("$.[*].statWeek").value(hasItem(DEFAULT_STAT_WEEK.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))));
//    }
//}
