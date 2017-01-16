//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.UserTimesheet;
//import com.wondertek.cpm.repository.UserTimesheetRepository;
//import com.wondertek.cpm.service.UserTimesheetService;
//import com.wondertek.cpm.repository.search.UserTimesheetSearchRepository;
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
// * Test class for the UserTimesheetResource REST controller.
// *
// * @see UserTimesheetResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class UserTimesheetResourceIntTest {
//
//    private static final String DEFAULT_WORK_DAY = "AAAAAAAAAA";
//    private static final String UPDATED_WORK_DAY = "BBBBBBBBBB";
//
//    private static final Long DEFAULT_USER_ID = 1L;
//    private static final Long UPDATED_USER_ID = 2L;
//
//    private static final Integer DEFAULT_TYPE = 1;
//    private static final Integer UPDATED_TYPE = 2;
//
//    private static final Long DEFAULT_OBJ_ID = 1L;
//    private static final Long UPDATED_OBJ_ID = 2L;
//
//    private static final String DEFAULT_OBJ_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_OBJ_NAME = "BBBBBBBBBB";
//
//    private static final Double DEFAULT_REAL_INPUT = 1D;
//    private static final Double UPDATED_REAL_INPUT = 2D;
//
//    private static final Double DEFAULT_ACCEPT_INPUT = 1D;
//    private static final Double UPDATED_ACCEPT_INPUT = 2D;
//
//    private static final Integer DEFAULT_STATUS = 1;
//    private static final Integer UPDATED_STATUS = 2;
//
//    private static final String DEFAULT_CREATOR = "AAAAAAAAAA";
//    private static final String UPDATED_CREATOR = "BBBBBBBBBB";
//
//    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
//
//    private static final String DEFAULT_UPDATOR = "AAAAAAAAAA";
//    private static final String UPDATED_UPDATOR = "BBBBBBBBBB";
//
//    private static final ZonedDateTime DEFAULT_UPDATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_UPDATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
//
//    @Inject
//    private UserTimesheetRepository userTimesheetRepository;
//
//    @Inject
//    private UserTimesheetService userTimesheetService;
//
//    @Inject
//    private UserTimesheetSearchRepository userTimesheetSearchRepository;
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
//    private MockMvc restUserTimesheetMockMvc;
//
//    private UserTimesheet userTimesheet;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        UserTimesheetResource userTimesheetResource = new UserTimesheetResource();
//        ReflectionTestUtils.setField(userTimesheetResource, "userTimesheetService", userTimesheetService);
//        this.restUserTimesheetMockMvc = MockMvcBuilders.standaloneSetup(userTimesheetResource)
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
//    public static UserTimesheet createEntity(EntityManager em) {
//        UserTimesheet userTimesheet = new UserTimesheet()
//                .userId(DEFAULT_USER_ID)
//                .type(DEFAULT_TYPE)
//                .objId(DEFAULT_OBJ_ID)
//                .objName(DEFAULT_OBJ_NAME)
//                .realInput(DEFAULT_REAL_INPUT)
//                .acceptInput(DEFAULT_ACCEPT_INPUT)
//                .status(DEFAULT_STATUS)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME);
//        return userTimesheet;
//    }
//
//    @Before
//    public void initTest() {
//        userTimesheetSearchRepository.deleteAll();
//        userTimesheet = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createUserTimesheet() throws Exception {
//        int databaseSizeBeforeCreate = userTimesheetRepository.findAll().size();
//
//        // Create the UserTimesheet
//
//        restUserTimesheetMockMvc.perform(post("/api/user-timesheets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(userTimesheet)))
//            .andExpect(status().isCreated());
//
//        // Validate the UserTimesheet in the database
//        List<UserTimesheet> userTimesheetList = userTimesheetRepository.findAll();
//        assertThat(userTimesheetList).hasSize(databaseSizeBeforeCreate + 1);
//        UserTimesheet testUserTimesheet = userTimesheetList.get(userTimesheetList.size() - 1);
//        assertThat(testUserTimesheet.getWorkDay()).isEqualTo(DEFAULT_WORK_DAY);
//        assertThat(testUserTimesheet.getUserId()).isEqualTo(DEFAULT_USER_ID);
//        assertThat(testUserTimesheet.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testUserTimesheet.getObjId()).isEqualTo(DEFAULT_OBJ_ID);
//        assertThat(testUserTimesheet.getObjName()).isEqualTo(DEFAULT_OBJ_NAME);
//        assertThat(testUserTimesheet.getRealInput()).isEqualTo(DEFAULT_REAL_INPUT);
//        assertThat(testUserTimesheet.getAcceptInput()).isEqualTo(DEFAULT_ACCEPT_INPUT);
//        assertThat(testUserTimesheet.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testUserTimesheet.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testUserTimesheet.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testUserTimesheet.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testUserTimesheet.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//
//        // Validate the UserTimesheet in ElasticSearch
//        UserTimesheet userTimesheetEs = userTimesheetSearchRepository.findOne(testUserTimesheet.getId());
//        assertThat(userTimesheetEs).isEqualToComparingFieldByField(testUserTimesheet);
//    }
//
//    @Test
//    @Transactional
//    public void createUserTimesheetWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = userTimesheetRepository.findAll().size();
//
//        // Create the UserTimesheet with an existing ID
//        UserTimesheet existingUserTimesheet = new UserTimesheet();
//        existingUserTimesheet.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restUserTimesheetMockMvc.perform(post("/api/user-timesheets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingUserTimesheet)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<UserTimesheet> userTimesheetList = userTimesheetRepository.findAll();
//        assertThat(userTimesheetList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllUserTimesheets() throws Exception {
//        // Initialize the database
//        userTimesheetRepository.saveAndFlush(userTimesheet);
//
//        // Get all the userTimesheetList
//        restUserTimesheetMockMvc.perform(get("/api/user-timesheets?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(userTimesheet.getId().intValue())))
//            .andExpect(jsonPath("$.[*].workDay").value(hasItem(DEFAULT_WORK_DAY.toString())))
//            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].objId").value(hasItem(DEFAULT_OBJ_ID.intValue())))
//            .andExpect(jsonPath("$.[*].objName").value(hasItem(DEFAULT_OBJ_NAME.toString())))
//            .andExpect(jsonPath("$.[*].realInput").value(hasItem(DEFAULT_REAL_INPUT.doubleValue())))
//            .andExpect(jsonPath("$.[*].acceptInput").value(hasItem(DEFAULT_ACCEPT_INPUT.doubleValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getUserTimesheet() throws Exception {
//        // Initialize the database
//        userTimesheetRepository.saveAndFlush(userTimesheet);
//
//        // Get the userTimesheet
//        restUserTimesheetMockMvc.perform(get("/api/user-timesheets/{id}", userTimesheet.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(userTimesheet.getId().intValue()))
//            .andExpect(jsonPath("$.workDay").value(DEFAULT_WORK_DAY.toString()))
//            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
//            .andExpect(jsonPath("$.objId").value(DEFAULT_OBJ_ID.intValue()))
//            .andExpect(jsonPath("$.objName").value(DEFAULT_OBJ_NAME.toString()))
//            .andExpect(jsonPath("$.realInput").value(DEFAULT_REAL_INPUT.doubleValue()))
//            .andExpect(jsonPath("$.acceptInput").value(DEFAULT_ACCEPT_INPUT.doubleValue()))
//            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingUserTimesheet() throws Exception {
//        // Get the userTimesheet
//        restUserTimesheetMockMvc.perform(get("/api/user-timesheets/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateUserTimesheet() throws Exception {
//        // Initialize the database
//        userTimesheetService.save(userTimesheet);
//
//        int databaseSizeBeforeUpdate = userTimesheetRepository.findAll().size();
//
//        // Update the userTimesheet
//        UserTimesheet updatedUserTimesheet = userTimesheetRepository.findOne(userTimesheet.getId());
//        updatedUserTimesheet
//                .userId(UPDATED_USER_ID)
//                .type(UPDATED_TYPE)
//                .objId(UPDATED_OBJ_ID)
//                .objName(UPDATED_OBJ_NAME)
//                .realInput(UPDATED_REAL_INPUT)
//                .acceptInput(UPDATED_ACCEPT_INPUT)
//                .status(UPDATED_STATUS)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME);
//
//        restUserTimesheetMockMvc.perform(put("/api/user-timesheets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedUserTimesheet)))
//            .andExpect(status().isOk());
//
//        // Validate the UserTimesheet in the database
//        List<UserTimesheet> userTimesheetList = userTimesheetRepository.findAll();
//        assertThat(userTimesheetList).hasSize(databaseSizeBeforeUpdate);
//        UserTimesheet testUserTimesheet = userTimesheetList.get(userTimesheetList.size() - 1);
//        assertThat(testUserTimesheet.getWorkDay()).isEqualTo(UPDATED_WORK_DAY);
//        assertThat(testUserTimesheet.getUserId()).isEqualTo(UPDATED_USER_ID);
//        assertThat(testUserTimesheet.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testUserTimesheet.getObjId()).isEqualTo(UPDATED_OBJ_ID);
//        assertThat(testUserTimesheet.getObjName()).isEqualTo(UPDATED_OBJ_NAME);
//        assertThat(testUserTimesheet.getRealInput()).isEqualTo(UPDATED_REAL_INPUT);
//        assertThat(testUserTimesheet.getAcceptInput()).isEqualTo(UPDATED_ACCEPT_INPUT);
//        assertThat(testUserTimesheet.getStatus()).isEqualTo(UPDATED_STATUS);
//        assertThat(testUserTimesheet.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testUserTimesheet.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testUserTimesheet.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testUserTimesheet.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//
//        // Validate the UserTimesheet in ElasticSearch
//        UserTimesheet userTimesheetEs = userTimesheetSearchRepository.findOne(testUserTimesheet.getId());
//        assertThat(userTimesheetEs).isEqualToComparingFieldByField(testUserTimesheet);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingUserTimesheet() throws Exception {
//        int databaseSizeBeforeUpdate = userTimesheetRepository.findAll().size();
//
//        // Create the UserTimesheet
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restUserTimesheetMockMvc.perform(put("/api/user-timesheets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(userTimesheet)))
//            .andExpect(status().isCreated());
//
//        // Validate the UserTimesheet in the database
//        List<UserTimesheet> userTimesheetList = userTimesheetRepository.findAll();
//        assertThat(userTimesheetList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteUserTimesheet() throws Exception {
//        // Initialize the database
//        userTimesheetService.save(userTimesheet);
//
//        int databaseSizeBeforeDelete = userTimesheetRepository.findAll().size();
//
//        // Get the userTimesheet
//        restUserTimesheetMockMvc.perform(delete("/api/user-timesheets/{id}", userTimesheet.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean userTimesheetExistsInEs = userTimesheetSearchRepository.exists(userTimesheet.getId());
//        assertThat(userTimesheetExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<UserTimesheet> userTimesheetList = userTimesheetRepository.findAll();
//        assertThat(userTimesheetList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchUserTimesheet() throws Exception {
//        // Initialize the database
//        userTimesheetService.save(userTimesheet);
//
//        // Search the userTimesheet
//        restUserTimesheetMockMvc.perform(get("/api/_search/user-timesheets?query=id:" + userTimesheet.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(userTimesheet.getId().intValue())))
//            .andExpect(jsonPath("$.[*].workDay").value(hasItem(DEFAULT_WORK_DAY.toString())))
//            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].objId").value(hasItem(DEFAULT_OBJ_ID.intValue())))
//            .andExpect(jsonPath("$.[*].objName").value(hasItem(DEFAULT_OBJ_NAME.toString())))
//            .andExpect(jsonPath("$.[*].realInput").value(hasItem(DEFAULT_REAL_INPUT.doubleValue())))
//            .andExpect(jsonPath("$.[*].acceptInput").value(hasItem(DEFAULT_ACCEPT_INPUT.doubleValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//}
