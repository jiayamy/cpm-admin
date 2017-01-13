//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.DeptInfo;
//import com.wondertek.cpm.repository.DeptInfoRepository;
//import com.wondertek.cpm.service.DeptInfoService;
//import com.wondertek.cpm.repository.search.DeptInfoSearchRepository;
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
// * Test class for the DeptInfoResource REST controller.
// *
// * @see DeptInfoResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class DeptInfoResourceIntTest {
//
//    private static final String DEFAULT_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_NAME = "BBBBBBBBBB";
//
//    private static final Long DEFAULT_PARENT_ID = 1L;
//    private static final Long UPDATED_PARENT_ID = 2L;
//
//    private static final Long DEFAULT_TYPE = 1L;
//    private static final Long UPDATED_TYPE = 2L;
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
//    private DeptInfoRepository deptInfoRepository;
//
//    @Inject
//    private DeptInfoService deptInfoService;
//
//    @Inject
//    private DeptInfoSearchRepository deptInfoSearchRepository;
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
//    private MockMvc restDeptInfoMockMvc;
//
//    private DeptInfo deptInfo;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        DeptInfoResource deptInfoResource = new DeptInfoResource();
//        ReflectionTestUtils.setField(deptInfoResource, "deptInfoService", deptInfoService);
//        this.restDeptInfoMockMvc = MockMvcBuilders.standaloneSetup(deptInfoResource)
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
//    public static DeptInfo createEntity(EntityManager em) {
//        DeptInfo deptInfo = new DeptInfo()
//                .name(DEFAULT_NAME)
//                .parentId(DEFAULT_PARENT_ID)
//                .type(DEFAULT_TYPE)
//                .status(DEFAULT_STATUS)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME);
//        return deptInfo;
//    }
//
//    @Before
//    public void initTest() {
//        deptInfoSearchRepository.deleteAll();
//        deptInfo = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createDeptInfo() throws Exception {
//        int databaseSizeBeforeCreate = deptInfoRepository.findAll().size();
//
//        // Create the DeptInfo
//
//        restDeptInfoMockMvc.perform(post("/api/dept-infos")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(deptInfo)))
//            .andExpect(status().isCreated());
//
//        // Validate the DeptInfo in the database
//        List<DeptInfo> deptInfoList = deptInfoRepository.findAll();
//        assertThat(deptInfoList).hasSize(databaseSizeBeforeCreate + 1);
//        DeptInfo testDeptInfo = deptInfoList.get(deptInfoList.size() - 1);
//        assertThat(testDeptInfo.getName()).isEqualTo(DEFAULT_NAME);
//        assertThat(testDeptInfo.getParentId()).isEqualTo(DEFAULT_PARENT_ID);
//        assertThat(testDeptInfo.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testDeptInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testDeptInfo.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testDeptInfo.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testDeptInfo.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testDeptInfo.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//
//        // Validate the DeptInfo in ElasticSearch
//        DeptInfo deptInfoEs = deptInfoSearchRepository.findOne(testDeptInfo.getId());
//        assertThat(deptInfoEs).isEqualToComparingFieldByField(testDeptInfo);
//    }
//
//    @Test
//    @Transactional
//    public void createDeptInfoWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = deptInfoRepository.findAll().size();
//
//        // Create the DeptInfo with an existing ID
//        DeptInfo existingDeptInfo = new DeptInfo();
//        existingDeptInfo.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restDeptInfoMockMvc.perform(post("/api/dept-infos")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingDeptInfo)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<DeptInfo> deptInfoList = deptInfoRepository.findAll();
//        assertThat(deptInfoList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void checkNameIsRequired() throws Exception {
//        int databaseSizeBeforeTest = deptInfoRepository.findAll().size();
//        // set the field null
//        deptInfo.setName(null);
//
//        // Create the DeptInfo, which fails.
//
//        restDeptInfoMockMvc.perform(post("/api/dept-infos")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(deptInfo)))
//            .andExpect(status().isBadRequest());
//
//        List<DeptInfo> deptInfoList = deptInfoRepository.findAll();
//        assertThat(deptInfoList).hasSize(databaseSizeBeforeTest);
//    }
//
//    @Test
//    @Transactional
//    public void getAllDeptInfos() throws Exception {
//        // Initialize the database
//        deptInfoRepository.saveAndFlush(deptInfo);
//
//        // Get all the deptInfoList
//        restDeptInfoMockMvc.perform(get("/api/dept-infos?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(deptInfo.getId().intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.intValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getDeptInfo() throws Exception {
//        // Initialize the database
//        deptInfoRepository.saveAndFlush(deptInfo);
//
//        // Get the deptInfo
//        restDeptInfoMockMvc.perform(get("/api/dept-infos/{id}", deptInfo.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(deptInfo.getId().intValue()))
//            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
//            .andExpect(jsonPath("$.parentId").value(DEFAULT_PARENT_ID.intValue()))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.intValue()))
//            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingDeptInfo() throws Exception {
//        // Get the deptInfo
//        restDeptInfoMockMvc.perform(get("/api/dept-infos/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateDeptInfo() throws Exception {
//        // Initialize the database
//        deptInfoService.save(deptInfo);
//
//        int databaseSizeBeforeUpdate = deptInfoRepository.findAll().size();
//
//        // Update the deptInfo
//        DeptInfo updatedDeptInfo = deptInfoRepository.findOne(deptInfo.getId());
//        updatedDeptInfo
//                .name(UPDATED_NAME)
//                .parentId(UPDATED_PARENT_ID)
//                .type(UPDATED_TYPE)
//                .status(UPDATED_STATUS)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME);
//
//        restDeptInfoMockMvc.perform(put("/api/dept-infos")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedDeptInfo)))
//            .andExpect(status().isOk());
//
//        // Validate the DeptInfo in the database
//        List<DeptInfo> deptInfoList = deptInfoRepository.findAll();
//        assertThat(deptInfoList).hasSize(databaseSizeBeforeUpdate);
//        DeptInfo testDeptInfo = deptInfoList.get(deptInfoList.size() - 1);
//        assertThat(testDeptInfo.getName()).isEqualTo(UPDATED_NAME);
//        assertThat(testDeptInfo.getParentId()).isEqualTo(UPDATED_PARENT_ID);
//        assertThat(testDeptInfo.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testDeptInfo.getStatus()).isEqualTo(UPDATED_STATUS);
//        assertThat(testDeptInfo.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testDeptInfo.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testDeptInfo.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testDeptInfo.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//
//        // Validate the DeptInfo in ElasticSearch
//        DeptInfo deptInfoEs = deptInfoSearchRepository.findOne(testDeptInfo.getId());
//        assertThat(deptInfoEs).isEqualToComparingFieldByField(testDeptInfo);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingDeptInfo() throws Exception {
//        int databaseSizeBeforeUpdate = deptInfoRepository.findAll().size();
//
//        // Create the DeptInfo
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restDeptInfoMockMvc.perform(put("/api/dept-infos")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(deptInfo)))
//            .andExpect(status().isCreated());
//
//        // Validate the DeptInfo in the database
//        List<DeptInfo> deptInfoList = deptInfoRepository.findAll();
//        assertThat(deptInfoList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteDeptInfo() throws Exception {
//        // Initialize the database
//        deptInfoService.save(deptInfo);
//
//        int databaseSizeBeforeDelete = deptInfoRepository.findAll().size();
//
//        // Get the deptInfo
//        restDeptInfoMockMvc.perform(delete("/api/dept-infos/{id}", deptInfo.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean deptInfoExistsInEs = deptInfoSearchRepository.exists(deptInfo.getId());
//        assertThat(deptInfoExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<DeptInfo> deptInfoList = deptInfoRepository.findAll();
//        assertThat(deptInfoList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchDeptInfo() throws Exception {
//        // Initialize the database
//        deptInfoService.save(deptInfo);
//
//        // Search the deptInfo
//        restDeptInfoMockMvc.perform(get("/api/_search/dept-infos?query=id:" + deptInfo.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(deptInfo.getId().intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.intValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//}
