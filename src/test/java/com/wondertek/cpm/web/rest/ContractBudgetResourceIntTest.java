//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.ContractBudget;
//import com.wondertek.cpm.repository.ContractBudgetRepository;
//import com.wondertek.cpm.service.ContractBudgetService;
//import com.wondertek.cpm.repository.search.ContractBudgetSearchRepository;
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
// * Test class for the ContractBudgetResource REST controller.
// *
// * @see ContractBudgetResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class ContractBudgetResourceIntTest {
//
//    private static final Long DEFAULT_CONTRACT_ID = 1L;
//    private static final Long UPDATED_CONTRACT_ID = 2L;
//
//    private static final Integer DEFAULT_TYPE = 1;
//    private static final Integer UPDATED_TYPE = 2;
//
//    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
//    private static final String UPDATED_USER_ID = "BBBBBBBBBB";
//
//    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";
//
//    private static final String DEFAULT_DEPT = "AAAAAAAAAA";
//    private static final String UPDATED_DEPT = "BBBBBBBBBB";
//
//    private static final Integer DEFAULT_PURCHASE_TYPE = 1;
//    private static final Integer UPDATED_PURCHASE_TYPE = 2;
//
//    private static final Double DEFAULT_BUDGET_TOTAL = 1D;
//    private static final Double UPDATED_BUDGET_TOTAL = 2D;
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
//    private ContractBudgetRepository contractBudgetRepository;
//
//    @Inject
//    private ContractBudgetService contractBudgetService;
//
//    @Inject
//    private ContractBudgetSearchRepository contractBudgetSearchRepository;
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
//    private MockMvc restContractBudgetMockMvc;
//
//    private ContractBudget contractBudget;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        ContractBudgetResource contractBudgetResource = new ContractBudgetResource();
//        ReflectionTestUtils.setField(contractBudgetResource, "contractBudgetService", contractBudgetService);
//        this.restContractBudgetMockMvc = MockMvcBuilders.standaloneSetup(contractBudgetResource)
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
//    public static ContractBudget createEntity(EntityManager em) {
//        ContractBudget contractBudget = new ContractBudget()
//                .contractId(DEFAULT_CONTRACT_ID)
//                .type(DEFAULT_TYPE)
//                .userName(DEFAULT_USER_NAME)
//                .dept(DEFAULT_DEPT)
//                .purchaseType(DEFAULT_PURCHASE_TYPE)
//                .budgetTotal(DEFAULT_BUDGET_TOTAL)
//                .status(DEFAULT_STATUS)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME);
//        return contractBudget;
//    }
//
//    @Before
//    public void initTest() {
//        contractBudgetSearchRepository.deleteAll();
//        contractBudget = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createContractBudget() throws Exception {
//        int databaseSizeBeforeCreate = contractBudgetRepository.findAll().size();
//
//        // Create the ContractBudget
//
//        restContractBudgetMockMvc.perform(post("/api/contract-budgets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractBudget)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractBudget in the database
//        List<ContractBudget> contractBudgetList = contractBudgetRepository.findAll();
//        assertThat(contractBudgetList).hasSize(databaseSizeBeforeCreate + 1);
//        ContractBudget testContractBudget = contractBudgetList.get(contractBudgetList.size() - 1);
//        assertThat(testContractBudget.getContractId()).isEqualTo(DEFAULT_CONTRACT_ID);
//        assertThat(testContractBudget.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testContractBudget.getUserId()).isEqualTo(DEFAULT_USER_ID);
//        assertThat(testContractBudget.getUserName()).isEqualTo(DEFAULT_USER_NAME);
//        assertThat(testContractBudget.getDept()).isEqualTo(DEFAULT_DEPT);
//        assertThat(testContractBudget.getPurchaseType()).isEqualTo(DEFAULT_PURCHASE_TYPE);
//        assertThat(testContractBudget.getBudgetTotal()).isEqualTo(DEFAULT_BUDGET_TOTAL);
//        assertThat(testContractBudget.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testContractBudget.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testContractBudget.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testContractBudget.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testContractBudget.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//
//        // Validate the ContractBudget in ElasticSearch
//        ContractBudget contractBudgetEs = contractBudgetSearchRepository.findOne(testContractBudget.getId());
//        assertThat(contractBudgetEs).isEqualToComparingFieldByField(testContractBudget);
//    }
//
//    @Test
//    @Transactional
//    public void createContractBudgetWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = contractBudgetRepository.findAll().size();
//
//        // Create the ContractBudget with an existing ID
//        ContractBudget existingContractBudget = new ContractBudget();
//        existingContractBudget.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restContractBudgetMockMvc.perform(post("/api/contract-budgets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingContractBudget)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<ContractBudget> contractBudgetList = contractBudgetRepository.findAll();
//        assertThat(contractBudgetList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllContractBudgets() throws Exception {
//        // Initialize the database
//        contractBudgetRepository.saveAndFlush(contractBudget);
//
//        // Get all the contractBudgetList
//        restContractBudgetMockMvc.perform(get("/api/contract-budgets?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractBudget.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
//            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
//            .andExpect(jsonPath("$.[*].dept").value(hasItem(DEFAULT_DEPT.toString())))
//            .andExpect(jsonPath("$.[*].purchaseType").value(hasItem(DEFAULT_PURCHASE_TYPE)))
//            .andExpect(jsonPath("$.[*].budgetTotal").value(hasItem(DEFAULT_BUDGET_TOTAL.doubleValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getContractBudget() throws Exception {
//        // Initialize the database
//        contractBudgetRepository.saveAndFlush(contractBudget);
//
//        // Get the contractBudget
//        restContractBudgetMockMvc.perform(get("/api/contract-budgets/{id}", contractBudget.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(contractBudget.getId().intValue()))
//            .andExpect(jsonPath("$.contractId").value(DEFAULT_CONTRACT_ID.intValue()))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
//            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.toString()))
//            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()))
//            .andExpect(jsonPath("$.dept").value(DEFAULT_DEPT.toString()))
//            .andExpect(jsonPath("$.purchaseType").value(DEFAULT_PURCHASE_TYPE))
//            .andExpect(jsonPath("$.budgetTotal").value(DEFAULT_BUDGET_TOTAL.doubleValue()))
//            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingContractBudget() throws Exception {
//        // Get the contractBudget
//        restContractBudgetMockMvc.perform(get("/api/contract-budgets/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateContractBudget() throws Exception {
//        // Initialize the database
//        contractBudgetService.save(contractBudget);
//
//        int databaseSizeBeforeUpdate = contractBudgetRepository.findAll().size();
//
//        // Update the contractBudget
//        ContractBudget updatedContractBudget = contractBudgetRepository.findOne(contractBudget.getId());
//        updatedContractBudget
//                .contractId(UPDATED_CONTRACT_ID)
//                .type(UPDATED_TYPE)
//                .userName(UPDATED_USER_NAME)
//                .dept(UPDATED_DEPT)
//                .purchaseType(UPDATED_PURCHASE_TYPE)
//                .budgetTotal(UPDATED_BUDGET_TOTAL)
//                .status(UPDATED_STATUS)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME);
//
//        restContractBudgetMockMvc.perform(put("/api/contract-budgets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedContractBudget)))
//            .andExpect(status().isOk());
//
//        // Validate the ContractBudget in the database
//        List<ContractBudget> contractBudgetList = contractBudgetRepository.findAll();
//        assertThat(contractBudgetList).hasSize(databaseSizeBeforeUpdate);
//        ContractBudget testContractBudget = contractBudgetList.get(contractBudgetList.size() - 1);
//        assertThat(testContractBudget.getContractId()).isEqualTo(UPDATED_CONTRACT_ID);
//        assertThat(testContractBudget.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testContractBudget.getUserId()).isEqualTo(UPDATED_USER_ID);
//        assertThat(testContractBudget.getUserName()).isEqualTo(UPDATED_USER_NAME);
//        assertThat(testContractBudget.getDept()).isEqualTo(UPDATED_DEPT);
//        assertThat(testContractBudget.getPurchaseType()).isEqualTo(UPDATED_PURCHASE_TYPE);
//        assertThat(testContractBudget.getBudgetTotal()).isEqualTo(UPDATED_BUDGET_TOTAL);
//        assertThat(testContractBudget.getStatus()).isEqualTo(UPDATED_STATUS);
//        assertThat(testContractBudget.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testContractBudget.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testContractBudget.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testContractBudget.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//
//        // Validate the ContractBudget in ElasticSearch
//        ContractBudget contractBudgetEs = contractBudgetSearchRepository.findOne(testContractBudget.getId());
//        assertThat(contractBudgetEs).isEqualToComparingFieldByField(testContractBudget);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingContractBudget() throws Exception {
//        int databaseSizeBeforeUpdate = contractBudgetRepository.findAll().size();
//
//        // Create the ContractBudget
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restContractBudgetMockMvc.perform(put("/api/contract-budgets")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractBudget)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractBudget in the database
//        List<ContractBudget> contractBudgetList = contractBudgetRepository.findAll();
//        assertThat(contractBudgetList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteContractBudget() throws Exception {
//        // Initialize the database
//        contractBudgetService.save(contractBudget);
//
//        int databaseSizeBeforeDelete = contractBudgetRepository.findAll().size();
//
//        // Get the contractBudget
//        restContractBudgetMockMvc.perform(delete("/api/contract-budgets/{id}", contractBudget.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean contractBudgetExistsInEs = contractBudgetSearchRepository.exists(contractBudget.getId());
//        assertThat(contractBudgetExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<ContractBudget> contractBudgetList = contractBudgetRepository.findAll();
//        assertThat(contractBudgetList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchContractBudget() throws Exception {
//        // Initialize the database
//        contractBudgetService.save(contractBudget);
//
//        // Search the contractBudget
//        restContractBudgetMockMvc.perform(get("/api/_search/contract-budgets?query=id:" + contractBudget.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractBudget.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
//            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
//            .andExpect(jsonPath("$.[*].dept").value(hasItem(DEFAULT_DEPT.toString())))
//            .andExpect(jsonPath("$.[*].purchaseType").value(hasItem(DEFAULT_PURCHASE_TYPE)))
//            .andExpect(jsonPath("$.[*].budgetTotal").value(hasItem(DEFAULT_BUDGET_TOTAL.doubleValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//}
