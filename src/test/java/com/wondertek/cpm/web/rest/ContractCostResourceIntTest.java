//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.ContractCost;
//import com.wondertek.cpm.repository.ContractCostRepository;
//import com.wondertek.cpm.service.ContractCostService;
//import com.wondertek.cpm.repository.search.ContractCostSearchRepository;
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
// * Test class for the ContractCostResource REST controller.
// *
// * @see ContractCostResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class ContractCostResourceIntTest {
//
//    private static final Long DEFAULT_CONTRACT_ID = 1L;
//    private static final Long UPDATED_CONTRACT_ID = 2L;
//
//    private static final Long DEFAULT_BUDGET_ID = 1L;
//    private static final Long UPDATED_BUDGET_ID = 2L;
//
//    private static final Long DEFAULT_DEPT_ID = 1L;
//    private static final Long UPDATED_DEPT_ID = 2L;
//
//    private static final String DEFAULT_DEPT = "AAAAAAAAAA";
//    private static final String UPDATED_DEPT = "BBBBBBBBBB";
//
//    private static final String DEFAULT_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_NAME = "BBBBBBBBBB";
//
//    private static final Integer DEFAULT_TYPE = 1;
//    private static final Integer UPDATED_TYPE = 2;
//
//    private static final Double DEFAULT_TOTAL = 1D;
//    private static final Double UPDATED_TOTAL = 2D;
//
//    private static final String DEFAULT_COST_DESC = "AAAAAAAAAA";
//    private static final String UPDATED_COST_DESC = "BBBBBBBBBB";
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
//    private ContractCostRepository contractCostRepository;
//
//    @Inject
//    private ContractCostService contractCostService;
//
//    @Inject
//    private ContractCostSearchRepository contractCostSearchRepository;
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
//    private MockMvc restContractCostMockMvc;
//
//    private ContractCost contractCost;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        ContractCostResource contractCostResource = new ContractCostResource();
//        ReflectionTestUtils.setField(contractCostResource, "contractCostService", contractCostService);
//        this.restContractCostMockMvc = MockMvcBuilders.standaloneSetup(contractCostResource)
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
//    public static ContractCost createEntity(EntityManager em) {
//        ContractCost contractCost = new ContractCost()
//                .contractId(DEFAULT_CONTRACT_ID)
//                .budgetId(DEFAULT_BUDGET_ID)
//                .deptId(DEFAULT_DEPT_ID)
//                .dept(DEFAULT_DEPT)
//                .name(DEFAULT_NAME)
//                .type(DEFAULT_TYPE)
//                .total(DEFAULT_TOTAL)
//                .costDesc(DEFAULT_COST_DESC)
//                .status(DEFAULT_STATUS)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME);
//        return contractCost;
//    }
//
//    @Before
//    public void initTest() {
//        contractCostSearchRepository.deleteAll();
//        contractCost = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createContractCost() throws Exception {
//        int databaseSizeBeforeCreate = contractCostRepository.findAll().size();
//
//        // Create the ContractCost
//
//        restContractCostMockMvc.perform(post("/api/contract-costs")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractCost)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractCost in the database
//        List<ContractCost> contractCostList = contractCostRepository.findAll();
//        assertThat(contractCostList).hasSize(databaseSizeBeforeCreate + 1);
//        ContractCost testContractCost = contractCostList.get(contractCostList.size() - 1);
//        assertThat(testContractCost.getContractId()).isEqualTo(DEFAULT_CONTRACT_ID);
//        assertThat(testContractCost.getBudgetId()).isEqualTo(DEFAULT_BUDGET_ID);
//        assertThat(testContractCost.getDeptId()).isEqualTo(DEFAULT_DEPT_ID);
//        assertThat(testContractCost.getDept()).isEqualTo(DEFAULT_DEPT);
//        assertThat(testContractCost.getName()).isEqualTo(DEFAULT_NAME);
//        assertThat(testContractCost.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testContractCost.getTotal()).isEqualTo(DEFAULT_TOTAL);
//        assertThat(testContractCost.getCostDesc()).isEqualTo(DEFAULT_COST_DESC);
//        assertThat(testContractCost.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testContractCost.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testContractCost.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testContractCost.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testContractCost.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//
//        // Validate the ContractCost in ElasticSearch
//        ContractCost contractCostEs = contractCostSearchRepository.findOne(testContractCost.getId());
//        assertThat(contractCostEs).isEqualToComparingFieldByField(testContractCost);
//    }
//
//    @Test
//    @Transactional
//    public void createContractCostWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = contractCostRepository.findAll().size();
//
//        // Create the ContractCost with an existing ID
//        ContractCost existingContractCost = new ContractCost();
//        existingContractCost.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restContractCostMockMvc.perform(post("/api/contract-costs")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingContractCost)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<ContractCost> contractCostList = contractCostRepository.findAll();
//        assertThat(contractCostList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllContractCosts() throws Exception {
//        // Initialize the database
//        contractCostRepository.saveAndFlush(contractCost);
//
//        // Get all the contractCostList
//        restContractCostMockMvc.perform(get("/api/contract-costs?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractCost.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].budgetId").value(hasItem(DEFAULT_BUDGET_ID.intValue())))
//            .andExpect(jsonPath("$.[*].deptId").value(hasItem(DEFAULT_DEPT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].dept").value(hasItem(DEFAULT_DEPT.toString())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
//            .andExpect(jsonPath("$.[*].costDesc").value(hasItem(DEFAULT_COST_DESC.toString())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getContractCost() throws Exception {
//        // Initialize the database
//        contractCostRepository.saveAndFlush(contractCost);
//
//        // Get the contractCost
//        restContractCostMockMvc.perform(get("/api/contract-costs/{id}", contractCost.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(contractCost.getId().intValue()))
//            .andExpect(jsonPath("$.contractId").value(DEFAULT_CONTRACT_ID.intValue()))
//            .andExpect(jsonPath("$.budgetId").value(DEFAULT_BUDGET_ID.intValue()))
//            .andExpect(jsonPath("$.deptId").value(DEFAULT_DEPT_ID.intValue()))
//            .andExpect(jsonPath("$.dept").value(DEFAULT_DEPT.toString()))
//            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
//            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()))
//            .andExpect(jsonPath("$.costDesc").value(DEFAULT_COST_DESC.toString()))
//            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingContractCost() throws Exception {
//        // Get the contractCost
//        restContractCostMockMvc.perform(get("/api/contract-costs/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateContractCost() throws Exception {
//        // Initialize the database
//        contractCostService.save(contractCost);
//
//        int databaseSizeBeforeUpdate = contractCostRepository.findAll().size();
//
//        // Update the contractCost
//        ContractCost updatedContractCost = contractCostRepository.findOne(contractCost.getId());
//        updatedContractCost
//                .contractId(UPDATED_CONTRACT_ID)
//                .budgetId(UPDATED_BUDGET_ID)
//                .deptId(UPDATED_DEPT_ID)
//                .dept(UPDATED_DEPT)
//                .name(UPDATED_NAME)
//                .type(UPDATED_TYPE)
//                .total(UPDATED_TOTAL)
//                .costDesc(UPDATED_COST_DESC)
//                .status(UPDATED_STATUS)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME);
//
//        restContractCostMockMvc.perform(put("/api/contract-costs")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedContractCost)))
//            .andExpect(status().isOk());
//
//        // Validate the ContractCost in the database
//        List<ContractCost> contractCostList = contractCostRepository.findAll();
//        assertThat(contractCostList).hasSize(databaseSizeBeforeUpdate);
//        ContractCost testContractCost = contractCostList.get(contractCostList.size() - 1);
//        assertThat(testContractCost.getContractId()).isEqualTo(UPDATED_CONTRACT_ID);
//        assertThat(testContractCost.getBudgetId()).isEqualTo(UPDATED_BUDGET_ID);
//        assertThat(testContractCost.getDeptId()).isEqualTo(UPDATED_DEPT_ID);
//        assertThat(testContractCost.getDept()).isEqualTo(UPDATED_DEPT);
//        assertThat(testContractCost.getName()).isEqualTo(UPDATED_NAME);
//        assertThat(testContractCost.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testContractCost.getTotal()).isEqualTo(UPDATED_TOTAL);
//        assertThat(testContractCost.getCostDesc()).isEqualTo(UPDATED_COST_DESC);
//        assertThat(testContractCost.getStatus()).isEqualTo(UPDATED_STATUS);
//        assertThat(testContractCost.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testContractCost.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testContractCost.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testContractCost.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//
//        // Validate the ContractCost in ElasticSearch
//        ContractCost contractCostEs = contractCostSearchRepository.findOne(testContractCost.getId());
//        assertThat(contractCostEs).isEqualToComparingFieldByField(testContractCost);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingContractCost() throws Exception {
//        int databaseSizeBeforeUpdate = contractCostRepository.findAll().size();
//
//        // Create the ContractCost
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restContractCostMockMvc.perform(put("/api/contract-costs")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(contractCost)))
//            .andExpect(status().isCreated());
//
//        // Validate the ContractCost in the database
//        List<ContractCost> contractCostList = contractCostRepository.findAll();
//        assertThat(contractCostList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteContractCost() throws Exception {
//        // Initialize the database
//        contractCostService.save(contractCost);
//
//        int databaseSizeBeforeDelete = contractCostRepository.findAll().size();
//
//        // Get the contractCost
//        restContractCostMockMvc.perform(delete("/api/contract-costs/{id}", contractCost.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean contractCostExistsInEs = contractCostSearchRepository.exists(contractCost.getId());
//        assertThat(contractCostExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<ContractCost> contractCostList = contractCostRepository.findAll();
//        assertThat(contractCostList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchContractCost() throws Exception {
//        // Initialize the database
//        contractCostService.save(contractCost);
//
//        // Search the contractCost
//        restContractCostMockMvc.perform(get("/api/_search/contract-costs?query=id:" + contractCost.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(contractCost.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].budgetId").value(hasItem(DEFAULT_BUDGET_ID.intValue())))
//            .andExpect(jsonPath("$.[*].deptId").value(hasItem(DEFAULT_DEPT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].dept").value(hasItem(DEFAULT_DEPT.toString())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
//            .andExpect(jsonPath("$.[*].costDesc").value(hasItem(DEFAULT_COST_DESC.toString())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//}
