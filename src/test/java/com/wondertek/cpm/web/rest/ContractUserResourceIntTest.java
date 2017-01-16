//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.ContractUser;
//import com.wondertek.cpm.repository.ContractUserRepository;
//import com.wondertek.cpm.service.ContractUserService;
//import com.wondertek.cpm.repository.search.ContractUserSearchRepository;
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
// * Test class for the ContractUserResource REST controller.
// *
// * @see ContractUserResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class ContractUserResourceIntTest {
//
//    private static final Long DEFAULT_CONTRACT_ID = 1L;
//    private static final Long UPDATED_CONTRACT_ID = 2L;
//
//    private static final Long DEFAULT_USER_ID = 1L;
//    private static final Long UPDATED_USER_ID = 2L;
//
//    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";
//
//    private static final Long DEFAULT_DEPT_ID = 1L;
//    private static final Long UPDATED_DEPT_ID = 2L;
//
//    private static final ZonedDateTime DEFAULT_JOIN_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_JOIN_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
//
//    private static final ZonedDateTime DEFAULT_LEAVE_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_LEAVE_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
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
//    private ContractUserRepository contractUserRepository;
//
//    @Inject
//    private ContractUserService contractUserService;
//
//    @Inject
//    private ContractUserSearchRepository contractUserSearchRepository;
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
//    private MockMvc restContractUserMockMvc;
//
//    private ContractUser contractUser;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        ContractUserResource contractUserResource = new ContractUserResource();
//        ReflectionTestUtils.setField(contractUserResource, "contractUserService", contractUserService);
//        this.restContractUserMockMvc = MockMvcBuilders.standaloneSetup(contractUserResource)
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
//    public static ContractUser createEntity(EntityManager em) {
//        ContractUser contractUser = new ContractUser()
//                .contractId(DEFAULT_CONTRACT_ID)
//                .userId(DEFAULT_USER_ID)
//                .userName(DEFAULT_USER_NAME)
//                .deptId(DEFAULT_DEPT_ID)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME);
//        return contractUser;
//    }
//
//    @Before
//    public void initTest() {
//        contractUserSearchRepository.deleteAll();
//        contractUser = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createContractUser() throws Exception {
//        int databaseSizeBeforeCreate = contractUserRepository.findAll().size();
//
//        // Create the ContractUser
//
//        restContractUserMockMvc.perform(post("/api/contract-users")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractUser)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractUser in the database
//        List<ContractUser> contractUserList = contractUserRepository.findAll();
//        assertThat(contractUserList).hasSize(databaseSizeBeforeCreate + 1);
//        ContractUser testContractUser = contractUserList.get(contractUserList.size() - 1);
//        assertThat(testContractUser.getContractId()).isEqualTo(DEFAULT_CONTRACT_ID);
//        assertThat(testContractUser.getUserId()).isEqualTo(DEFAULT_USER_ID);
//        assertThat(testContractUser.getUserName()).isEqualTo(DEFAULT_USER_NAME);
//        assertThat(testContractUser.getDeptId()).isEqualTo(DEFAULT_DEPT_ID);
//        assertThat(testContractUser.getJoinDay()).isEqualTo(DEFAULT_JOIN_DAY);
//        assertThat(testContractUser.getLeaveDay()).isEqualTo(DEFAULT_LEAVE_DAY);
//        assertThat(testContractUser.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testContractUser.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testContractUser.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testContractUser.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//
//        // Validate the ContractUser in ElasticSearch
//        ContractUser contractUserEs = contractUserSearchRepository.findOne(testContractUser.getId());
//        assertThat(contractUserEs).isEqualToComparingFieldByField(testContractUser);
//    }
//
//    @Test
//    @Transactional
//    public void createContractUserWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = contractUserRepository.findAll().size();
//
//        // Create the ContractUser with an existing ID
//        ContractUser existingContractUser = new ContractUser();
//        existingContractUser.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restContractUserMockMvc.perform(post("/api/contract-users")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingContractUser)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<ContractUser> contractUserList = contractUserRepository.findAll();
//        assertThat(contractUserList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllContractUsers() throws Exception {
//        // Initialize the database
//        contractUserRepository.saveAndFlush(contractUser);
//
//        // Get all the contractUserList
//        restContractUserMockMvc.perform(get("/api/contract-users?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractUser.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
//            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
//            .andExpect(jsonPath("$.[*].deptId").value(hasItem(DEFAULT_DEPT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].joinDay").value(hasItem(sameInstant(DEFAULT_JOIN_DAY))))
//            .andExpect(jsonPath("$.[*].leaveDay").value(hasItem(sameInstant(DEFAULT_LEAVE_DAY))))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getContractUser() throws Exception {
//        // Initialize the database
//        contractUserRepository.saveAndFlush(contractUser);
//
//        // Get the contractUser
//        restContractUserMockMvc.perform(get("/api/contract-users/{id}", contractUser.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(contractUser.getId().intValue()))
//            .andExpect(jsonPath("$.contractId").value(DEFAULT_CONTRACT_ID.intValue()))
//            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
//            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()))
//            .andExpect(jsonPath("$.deptId").value(DEFAULT_DEPT_ID.intValue()))
//            .andExpect(jsonPath("$.joinDay").value(sameInstant(DEFAULT_JOIN_DAY)))
//            .andExpect(jsonPath("$.leaveDay").value(sameInstant(DEFAULT_LEAVE_DAY)))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingContractUser() throws Exception {
//        // Get the contractUser
//        restContractUserMockMvc.perform(get("/api/contract-users/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateContractUser() throws Exception {
//        // Initialize the database
//        contractUserService.save(contractUser);
//
//        int databaseSizeBeforeUpdate = contractUserRepository.findAll().size();
//
//        // Update the contractUser
//        ContractUser updatedContractUser = contractUserRepository.findOne(contractUser.getId());
//        updatedContractUser
//                .contractId(UPDATED_CONTRACT_ID)
//                .userId(UPDATED_USER_ID)
//                .userName(UPDATED_USER_NAME)
//                .deptId(UPDATED_DEPT_ID)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME);
//
//        restContractUserMockMvc.perform(put("/api/contract-users")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedContractUser)))
//            .andExpect(status().isOk());
//
//        // Validate the ContractUser in the database
//        List<ContractUser> contractUserList = contractUserRepository.findAll();
//        assertThat(contractUserList).hasSize(databaseSizeBeforeUpdate);
//        ContractUser testContractUser = contractUserList.get(contractUserList.size() - 1);
//        assertThat(testContractUser.getContractId()).isEqualTo(UPDATED_CONTRACT_ID);
//        assertThat(testContractUser.getUserId()).isEqualTo(UPDATED_USER_ID);
//        assertThat(testContractUser.getUserName()).isEqualTo(UPDATED_USER_NAME);
//        assertThat(testContractUser.getDeptId()).isEqualTo(UPDATED_DEPT_ID);
//        assertThat(testContractUser.getJoinDay()).isEqualTo(UPDATED_JOIN_DAY);
//        assertThat(testContractUser.getLeaveDay()).isEqualTo(UPDATED_LEAVE_DAY);
//        assertThat(testContractUser.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testContractUser.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testContractUser.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testContractUser.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//
//        // Validate the ContractUser in ElasticSearch
//        ContractUser contractUserEs = contractUserSearchRepository.findOne(testContractUser.getId());
//        assertThat(contractUserEs).isEqualToComparingFieldByField(testContractUser);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingContractUser() throws Exception {
//        int databaseSizeBeforeUpdate = contractUserRepository.findAll().size();
//
//        // Create the ContractUser
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restContractUserMockMvc.perform(put("/api/contract-users")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractUser)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractUser in the database
//        List<ContractUser> contractUserList = contractUserRepository.findAll();
//        assertThat(contractUserList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteContractUser() throws Exception {
//        // Initialize the database
//        contractUserService.save(contractUser);
//
//        int databaseSizeBeforeDelete = contractUserRepository.findAll().size();
//
//        // Get the contractUser
//        restContractUserMockMvc.perform(delete("/api/contract-users/{id}", contractUser.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean contractUserExistsInEs = contractUserSearchRepository.exists(contractUser.getId());
//        assertThat(contractUserExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<ContractUser> contractUserList = contractUserRepository.findAll();
//        assertThat(contractUserList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchContractUser() throws Exception {
//        // Initialize the database
//        contractUserService.save(contractUser);
//
//        // Search the contractUser
//        restContractUserMockMvc.perform(get("/api/_search/contract-users?query=id:" + contractUser.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractUser.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
//            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
//            .andExpect(jsonPath("$.[*].deptId").value(hasItem(DEFAULT_DEPT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].joinDay").value(hasItem(sameInstant(DEFAULT_JOIN_DAY))))
//            .andExpect(jsonPath("$.[*].leaveDay").value(hasItem(sameInstant(DEFAULT_LEAVE_DAY))))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//}
