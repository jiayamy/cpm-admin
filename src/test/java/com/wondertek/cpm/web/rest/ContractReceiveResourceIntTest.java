//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.ContractReceive;
//import com.wondertek.cpm.repository.ContractReceiveRepository;
//import com.wondertek.cpm.service.ContractReceiveService;
//import com.wondertek.cpm.repository.search.ContractReceiveSearchRepository;
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
// * Test class for the ContractReceiveResource REST controller.
// *
// * @see ContractReceiveResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class ContractReceiveResourceIntTest {
//
//    private static final Long DEFAULT_CONTRACT_ID = 1L;
//    private static final Long UPDATED_CONTRACT_ID = 2L;
//
//    private static final Double DEFAULT_RECEIVE_TOTAL = 1D;
//    private static final Double UPDATED_RECEIVE_TOTAL = 2D;
//
//    private static final ZonedDateTime DEFAULT_RECEIVE_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_RECEIVE_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
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
//    private static final String DEFAULT_RECEIVER = "AAAAAAAAAA";
//    private static final String UPDATED_RECEIVER = "BBBBBBBBBB";
//
//    @Inject
//    private ContractReceiveRepository contractReceiveRepository;
//
//    @Inject
//    private ContractReceiveService contractReceiveService;
//
//    @Inject
//    private ContractReceiveSearchRepository contractReceiveSearchRepository;
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
//    private MockMvc restContractReceiveMockMvc;
//
//    private ContractReceive contractReceive;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        ContractReceiveResource contractReceiveResource = new ContractReceiveResource();
//        ReflectionTestUtils.setField(contractReceiveResource, "contractReceiveService", contractReceiveService);
//        this.restContractReceiveMockMvc = MockMvcBuilders.standaloneSetup(contractReceiveResource)
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
//    public static ContractReceive createEntity(EntityManager em) {
//        ContractReceive contractReceive = new ContractReceive()
//                .contractId(DEFAULT_CONTRACT_ID)
//                .receiveTotal(DEFAULT_RECEIVE_TOTAL)
//                .status(DEFAULT_STATUS)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME)
//                .receiver(DEFAULT_RECEIVER);
//        return contractReceive;
//    }
//
//    @Before
//    public void initTest() {
//        contractReceiveSearchRepository.deleteAll();
//        contractReceive = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createContractReceive() throws Exception {
//        int databaseSizeBeforeCreate = contractReceiveRepository.findAll().size();
//
//        // Create the ContractReceive
//
//        restContractReceiveMockMvc.perform(post("/api/contract-receives")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractReceive)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractReceive in the database
//        List<ContractReceive> contractReceiveList = contractReceiveRepository.findAll();
//        assertThat(contractReceiveList).hasSize(databaseSizeBeforeCreate + 1);
//        ContractReceive testContractReceive = contractReceiveList.get(contractReceiveList.size() - 1);
//        assertThat(testContractReceive.getContractId()).isEqualTo(DEFAULT_CONTRACT_ID);
//        assertThat(testContractReceive.getReceiveTotal()).isEqualTo(DEFAULT_RECEIVE_TOTAL);
//        assertThat(testContractReceive.getReceiveDay()).isEqualTo(DEFAULT_RECEIVE_DAY);
//        assertThat(testContractReceive.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testContractReceive.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testContractReceive.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testContractReceive.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testContractReceive.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//        assertThat(testContractReceive.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
//
//        // Validate the ContractReceive in ElasticSearch
//        ContractReceive contractReceiveEs = contractReceiveSearchRepository.findOne(testContractReceive.getId());
//        assertThat(contractReceiveEs).isEqualToComparingFieldByField(testContractReceive);
//    }
//
//    @Test
//    @Transactional
//    public void createContractReceiveWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = contractReceiveRepository.findAll().size();
//
//        // Create the ContractReceive with an existing ID
//        ContractReceive existingContractReceive = new ContractReceive();
//        existingContractReceive.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restContractReceiveMockMvc.perform(post("/api/contract-receives")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingContractReceive)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<ContractReceive> contractReceiveList = contractReceiveRepository.findAll();
//        assertThat(contractReceiveList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllContractReceives() throws Exception {
//        // Initialize the database
//        contractReceiveRepository.saveAndFlush(contractReceive);
//
//        // Get all the contractReceiveList
//        restContractReceiveMockMvc.perform(get("/api/contract-receives?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractReceive.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].receiveTotal").value(hasItem(DEFAULT_RECEIVE_TOTAL.doubleValue())))
//            .andExpect(jsonPath("$.[*].receiveDay").value(hasItem(sameInstant(DEFAULT_RECEIVE_DAY))))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))))
//            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER.toString())));
//    }
//
//    @Test
//    @Transactional
//    public void getContractReceive() throws Exception {
//        // Initialize the database
//        contractReceiveRepository.saveAndFlush(contractReceive);
//
//        // Get the contractReceive
//        restContractReceiveMockMvc.perform(get("/api/contract-receives/{id}", contractReceive.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(contractReceive.getId().intValue()))
//            .andExpect(jsonPath("$.contractId").value(DEFAULT_CONTRACT_ID.intValue()))
//            .andExpect(jsonPath("$.receiveTotal").value(DEFAULT_RECEIVE_TOTAL.doubleValue()))
//            .andExpect(jsonPath("$.receiveDay").value(sameInstant(DEFAULT_RECEIVE_DAY)))
//            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)))
//            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER.toString()));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingContractReceive() throws Exception {
//        // Get the contractReceive
//        restContractReceiveMockMvc.perform(get("/api/contract-receives/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateContractReceive() throws Exception {
//        // Initialize the database
//        contractReceiveService.save(contractReceive);
//
//        int databaseSizeBeforeUpdate = contractReceiveRepository.findAll().size();
//
//        // Update the contractReceive
//        ContractReceive updatedContractReceive = contractReceiveRepository.findOne(contractReceive.getId());
//        updatedContractReceive
//                .contractId(UPDATED_CONTRACT_ID)
//                .receiveTotal(UPDATED_RECEIVE_TOTAL)
//                .status(UPDATED_STATUS)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME)
//                .receiver(UPDATED_RECEIVER);
//
//        restContractReceiveMockMvc.perform(put("/api/contract-receives")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedContractReceive)))
//            .andExpect(status().isOk());
//
//        // Validate the ContractReceive in the database
//        List<ContractReceive> contractReceiveList = contractReceiveRepository.findAll();
//        assertThat(contractReceiveList).hasSize(databaseSizeBeforeUpdate);
//        ContractReceive testContractReceive = contractReceiveList.get(contractReceiveList.size() - 1);
//        assertThat(testContractReceive.getContractId()).isEqualTo(UPDATED_CONTRACT_ID);
//        assertThat(testContractReceive.getReceiveTotal()).isEqualTo(UPDATED_RECEIVE_TOTAL);
//        assertThat(testContractReceive.getReceiveDay()).isEqualTo(UPDATED_RECEIVE_DAY);
//        assertThat(testContractReceive.getStatus()).isEqualTo(UPDATED_STATUS);
//        assertThat(testContractReceive.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testContractReceive.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testContractReceive.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testContractReceive.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//        assertThat(testContractReceive.getReceiver()).isEqualTo(UPDATED_RECEIVER);
//
//        // Validate the ContractReceive in ElasticSearch
//        ContractReceive contractReceiveEs = contractReceiveSearchRepository.findOne(testContractReceive.getId());
//        assertThat(contractReceiveEs).isEqualToComparingFieldByField(testContractReceive);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingContractReceive() throws Exception {
//        int databaseSizeBeforeUpdate = contractReceiveRepository.findAll().size();
//
//        // Create the ContractReceive
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restContractReceiveMockMvc.perform(put("/api/contract-receives")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractReceive)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractReceive in the database
//        List<ContractReceive> contractReceiveList = contractReceiveRepository.findAll();
//        assertThat(contractReceiveList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteContractReceive() throws Exception {
//        // Initialize the database
//        contractReceiveService.save(contractReceive);
//
//        int databaseSizeBeforeDelete = contractReceiveRepository.findAll().size();
//
//        // Get the contractReceive
//        restContractReceiveMockMvc.perform(delete("/api/contract-receives/{id}", contractReceive.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean contractReceiveExistsInEs = contractReceiveSearchRepository.exists(contractReceive.getId());
//        assertThat(contractReceiveExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<ContractReceive> contractReceiveList = contractReceiveRepository.findAll();
//        assertThat(contractReceiveList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchContractReceive() throws Exception {
//        // Initialize the database
//        contractReceiveService.save(contractReceive);
//
//        // Search the contractReceive
//        restContractReceiveMockMvc.perform(get("/api/_search/contract-receives?query=id:" + contractReceive.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractReceive.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].receiveTotal").value(hasItem(DEFAULT_RECEIVE_TOTAL.doubleValue())))
//            .andExpect(jsonPath("$.[*].receiveDay").value(hasItem(sameInstant(DEFAULT_RECEIVE_DAY))))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))))
//            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER.toString())));
//    }
//}
