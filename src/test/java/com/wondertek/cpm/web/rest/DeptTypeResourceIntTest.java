//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.DeptType;
//import com.wondertek.cpm.repository.DeptTypeRepository;
//import com.wondertek.cpm.service.DeptTypeService;
//import com.wondertek.cpm.repository.search.DeptTypeSearchRepository;
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
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Test class for the DeptTypeResource REST controller.
// *
// * @see DeptTypeResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class DeptTypeResourceIntTest {
//
//    private static final String DEFAULT_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_NAME = "BBBBBBBBBB";
//
//    @Inject
//    private DeptTypeRepository deptTypeRepository;
//
//    @Inject
//    private DeptTypeService deptTypeService;
//
//    @Inject
//    private DeptTypeSearchRepository deptTypeSearchRepository;
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
//    private MockMvc restDeptTypeMockMvc;
//
//    private DeptType deptType;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        DeptTypeResource deptTypeResource = new DeptTypeResource();
//        ReflectionTestUtils.setField(deptTypeResource, "deptTypeService", deptTypeService);
//        this.restDeptTypeMockMvc = MockMvcBuilders.standaloneSetup(deptTypeResource)
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
//    public static DeptType createEntity(EntityManager em) {
//        DeptType deptType = new DeptType()
//                .name(DEFAULT_NAME);
//        return deptType;
//    }
//
//    @Before
//    public void initTest() {
//        deptTypeSearchRepository.deleteAll();
//        deptType = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createDeptType() throws Exception {
//        int databaseSizeBeforeCreate = deptTypeRepository.findAll().size();
//
//        // Create the DeptType
//
//        restDeptTypeMockMvc.perform(post("/api/dept-types")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(deptType)))
//            .andExpect(status().isCreated());
//
//        // Validate the DeptType in the database
//        List<DeptType> deptTypeList = deptTypeRepository.findAll();
//        assertThat(deptTypeList).hasSize(databaseSizeBeforeCreate + 1);
//        DeptType testDeptType = deptTypeList.get(deptTypeList.size() - 1);
//        assertThat(testDeptType.getName()).isEqualTo(DEFAULT_NAME);
//
//        // Validate the DeptType in ElasticSearch
//        DeptType deptTypeEs = deptTypeSearchRepository.findOne(testDeptType.getId());
//        assertThat(deptTypeEs).isEqualToComparingFieldByField(testDeptType);
//    }
//
//    @Test
//    @Transactional
//    public void createDeptTypeWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = deptTypeRepository.findAll().size();
//
//        // Create the DeptType with an existing ID
//        DeptType existingDeptType = new DeptType();
//        existingDeptType.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restDeptTypeMockMvc.perform(post("/api/dept-types")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingDeptType)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<DeptType> deptTypeList = deptTypeRepository.findAll();
//        assertThat(deptTypeList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void checkNameIsRequired() throws Exception {
//        int databaseSizeBeforeTest = deptTypeRepository.findAll().size();
//        // set the field null
//        deptType.setName(null);
//
//        // Create the DeptType, which fails.
//
//        restDeptTypeMockMvc.perform(post("/api/dept-types")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(deptType)))
//            .andExpect(status().isBadRequest());
//
//        List<DeptType> deptTypeList = deptTypeRepository.findAll();
//        assertThat(deptTypeList).hasSize(databaseSizeBeforeTest);
//    }
//
//    @Test
//    @Transactional
//    public void getAllDeptTypes() throws Exception {
//        // Initialize the database
//        deptTypeRepository.saveAndFlush(deptType);
//
//        // Get all the deptTypeList
//        restDeptTypeMockMvc.perform(get("/api/dept-types?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(deptType.getId().intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
//    }
//
//    @Test
//    @Transactional
//    public void getDeptType() throws Exception {
//        // Initialize the database
//        deptTypeRepository.saveAndFlush(deptType);
//
//        // Get the deptType
//        restDeptTypeMockMvc.perform(get("/api/dept-types/{id}", deptType.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(deptType.getId().intValue()))
//            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingDeptType() throws Exception {
//        // Get the deptType
//        restDeptTypeMockMvc.perform(get("/api/dept-types/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateDeptType() throws Exception {
//        // Initialize the database
//        deptTypeService.save(deptType);
//
//        int databaseSizeBeforeUpdate = deptTypeRepository.findAll().size();
//
//        // Update the deptType
//        DeptType updatedDeptType = deptTypeRepository.findOne(deptType.getId());
//        updatedDeptType
//                .name(UPDATED_NAME);
//
//        restDeptTypeMockMvc.perform(put("/api/dept-types")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedDeptType)))
//            .andExpect(status().isOk());
//
//        // Validate the DeptType in the database
//        List<DeptType> deptTypeList = deptTypeRepository.findAll();
//        assertThat(deptTypeList).hasSize(databaseSizeBeforeUpdate);
//        DeptType testDeptType = deptTypeList.get(deptTypeList.size() - 1);
//        assertThat(testDeptType.getName()).isEqualTo(UPDATED_NAME);
//
//        // Validate the DeptType in ElasticSearch
//        DeptType deptTypeEs = deptTypeSearchRepository.findOne(testDeptType.getId());
//        assertThat(deptTypeEs).isEqualToComparingFieldByField(testDeptType);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingDeptType() throws Exception {
//        int databaseSizeBeforeUpdate = deptTypeRepository.findAll().size();
//
//        // Create the DeptType
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restDeptTypeMockMvc.perform(put("/api/dept-types")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(deptType)))
//            .andExpect(status().isCreated());
//
//        // Validate the DeptType in the database
//        List<DeptType> deptTypeList = deptTypeRepository.findAll();
//        assertThat(deptTypeList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteDeptType() throws Exception {
//        // Initialize the database
//        deptTypeService.save(deptType);
//
//        int databaseSizeBeforeDelete = deptTypeRepository.findAll().size();
//
//        // Get the deptType
//        restDeptTypeMockMvc.perform(delete("/api/dept-types/{id}", deptType.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean deptTypeExistsInEs = deptTypeSearchRepository.exists(deptType.getId());
//        assertThat(deptTypeExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<DeptType> deptTypeList = deptTypeRepository.findAll();
//        assertThat(deptTypeList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchDeptType() throws Exception {
//        // Initialize the database
//        deptTypeService.save(deptType);
//
//        // Search the deptType
//        restDeptTypeMockMvc.perform(get("/api/_search/dept-types?query=id:" + deptType.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(deptType.getId().intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
//    }
//}
