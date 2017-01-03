package com.wondertek.cpm.web.rest;

import static com.wondertek.cpm.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

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

import com.wondertek.cpm.CpmApp;
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.repository.ContractWeeklyStatRepository;
import com.wondertek.cpm.repository.search.ContractWeeklyStatSearchRepository;
import com.wondertek.cpm.service.ContractWeeklyStatService;

/**
 * Test class for the ContractWeeklyStatResource REST controller.
 *
 * @see ContractWeeklyStatResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class ContractWeeklyStatResourceIntTest {

    private static final Long DEFAULT_CONTRACT_ID = 1l;
    private static final Long UPDATED_CONTRACT_ID = 2l;

    private static final Double DEFAULT_RECEIVE_TOTAL = 1D;
    private static final Double UPDATED_RECEIVE_TOTAL = 2D;

    private static final Double DEFAULT_COST_TOTAL = 1D;
    private static final Double UPDATED_COST_TOTAL = 2D;

    private static final Double DEFAULT_GROSS_PROFIT = 1D;
    private static final Double UPDATED_GROSS_PROFIT = 2D;

    private static final Double DEFAULT_SALES_HUMAN_COST = 1D;
    private static final Double UPDATED_SALES_HUMAN_COST = 2D;

    private static final Double DEFAULT_SALES_PAYMENT = 1D;
    private static final Double UPDATED_SALES_PAYMENT = 2D;

    private static final Double DEFAULT_CONSULT_HUMAN_COST = 1D;
    private static final Double UPDATED_CONSULT_HUMAN_COST = 2D;

    private static final Double DEFAULT_CONSULT_PAYMENT = 1D;
    private static final Double UPDATED_CONSULT_PAYMENT = 2D;

    private static final Double DEFAULT_HARDWARE_PURCHASE = 1D;
    private static final Double UPDATED_HARDWARE_PURCHASE = 2D;

    private static final Double DEFAULT_EXTERNAL_SOFTWARE = 1D;
    private static final Double UPDATED_EXTERNAL_SOFTWARE = 2D;

    private static final Double DEFAULT_INTERNAL_SOFTWARE = 1D;
    private static final Double UPDATED_INTERNAL_SOFTWARE = 2D;

    private static final Double DEFAULT_PROJECT_HUMAN_COST = 1D;
    private static final Double UPDATED_PROJECT_HUMAN_COST = 2D;

    private static final Double DEFAULT_PROJECT_PAYMENT = 1D;
    private static final Double UPDATED_PROJECT_PAYMENT = 2D;

    private static final String DEFAULT_STAT_WEEK = "AAAAAAAAAA";
    private static final String UPDATED_STAT_WEEK = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private ContractWeeklyStatRepository contractWeeklyStatRepository;

    @Inject
    private ContractWeeklyStatService contractWeeklyStatService;

    @Inject
    private ContractWeeklyStatSearchRepository contractWeeklyStatSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restContractWeeklyStatMockMvc;

    private ContractWeeklyStat contractWeeklyStat;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContractWeeklyStatResource contractWeeklyStatResource = new ContractWeeklyStatResource();
        ReflectionTestUtils.setField(contractWeeklyStatResource, "contractWeeklyStatService", contractWeeklyStatService);
        this.restContractWeeklyStatMockMvc = MockMvcBuilders.standaloneSetup(contractWeeklyStatResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContractWeeklyStat createEntity(EntityManager em) {
        ContractWeeklyStat contractWeeklyStat = new ContractWeeklyStat()
                .contractId(DEFAULT_CONTRACT_ID)
                .receiveTotal(DEFAULT_RECEIVE_TOTAL)
                .costTotal(DEFAULT_COST_TOTAL)
                .grossProfit(DEFAULT_GROSS_PROFIT)
                .salesHumanCost(DEFAULT_SALES_HUMAN_COST)
                .salesPayment(DEFAULT_SALES_PAYMENT)
                .consultHumanCost(DEFAULT_CONSULT_HUMAN_COST)
                .consultPayment(DEFAULT_CONSULT_PAYMENT)
                .hardwarePurchase(DEFAULT_HARDWARE_PURCHASE)
                .externalSoftware(DEFAULT_EXTERNAL_SOFTWARE)
                .internalSoftware(DEFAULT_INTERNAL_SOFTWARE)
                .projectHumanCost(DEFAULT_PROJECT_HUMAN_COST)
                .projectPayment(DEFAULT_PROJECT_PAYMENT)
                .createTime(DEFAULT_CREATE_TIME);
        return contractWeeklyStat;
    }

    @Before
    public void initTest() {
        contractWeeklyStatSearchRepository.deleteAll();
        contractWeeklyStat = createEntity(em);
    }

    @Test
    @Transactional
    public void createContractWeeklyStat() throws Exception {
        int databaseSizeBeforeCreate = contractWeeklyStatRepository.findAll().size();

        // Create the ContractWeeklyStat

        restContractWeeklyStatMockMvc.perform(post("/api/contract-weekly-stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(contractWeeklyStat)))
            .andExpect(status().isCreated());

        // Validate the ContractWeeklyStat in the database
        List<ContractWeeklyStat> contractWeeklyStatList = contractWeeklyStatRepository.findAll();
        assertThat(contractWeeklyStatList).hasSize(databaseSizeBeforeCreate + 1);
        ContractWeeklyStat testContractWeeklyStat = contractWeeklyStatList.get(contractWeeklyStatList.size() - 1);
        assertThat(testContractWeeklyStat.getContractId()).isEqualTo(DEFAULT_CONTRACT_ID);
        assertThat(testContractWeeklyStat.getReceiveTotal()).isEqualTo(DEFAULT_RECEIVE_TOTAL);
        assertThat(testContractWeeklyStat.getCostTotal()).isEqualTo(DEFAULT_COST_TOTAL);
        assertThat(testContractWeeklyStat.getGrossProfit()).isEqualTo(DEFAULT_GROSS_PROFIT);
        assertThat(testContractWeeklyStat.getSalesHumanCost()).isEqualTo(DEFAULT_SALES_HUMAN_COST);
        assertThat(testContractWeeklyStat.getSalesPayment()).isEqualTo(DEFAULT_SALES_PAYMENT);
        assertThat(testContractWeeklyStat.getConsultHumanCost()).isEqualTo(DEFAULT_CONSULT_HUMAN_COST);
        assertThat(testContractWeeklyStat.getConsultPayment()).isEqualTo(DEFAULT_CONSULT_PAYMENT);
        assertThat(testContractWeeklyStat.getHardwarePurchase()).isEqualTo(DEFAULT_HARDWARE_PURCHASE);
        assertThat(testContractWeeklyStat.getExternalSoftware()).isEqualTo(DEFAULT_EXTERNAL_SOFTWARE);
        assertThat(testContractWeeklyStat.getInternalSoftware()).isEqualTo(DEFAULT_INTERNAL_SOFTWARE);
        assertThat(testContractWeeklyStat.getProjectHumanCost()).isEqualTo(DEFAULT_PROJECT_HUMAN_COST);
        assertThat(testContractWeeklyStat.getProjectPayment()).isEqualTo(DEFAULT_PROJECT_PAYMENT);
        assertThat(testContractWeeklyStat.getStatWeek()).isEqualTo(DEFAULT_STAT_WEEK);
        assertThat(testContractWeeklyStat.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);

        // Validate the ContractWeeklyStat in ElasticSearch
        ContractWeeklyStat contractWeeklyStatEs = contractWeeklyStatSearchRepository.findOne(testContractWeeklyStat.getId());
        assertThat(contractWeeklyStatEs).isEqualToComparingFieldByField(testContractWeeklyStat);
    }

    @Test
    @Transactional
    public void createContractWeeklyStatWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = contractWeeklyStatRepository.findAll().size();

        // Create the ContractWeeklyStat with an existing ID
        ContractWeeklyStat existingContractWeeklyStat = new ContractWeeklyStat();
        existingContractWeeklyStat.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restContractWeeklyStatMockMvc.perform(post("/api/contract-weekly-stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingContractWeeklyStat)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ContractWeeklyStat> contractWeeklyStatList = contractWeeklyStatRepository.findAll();
        assertThat(contractWeeklyStatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllContractWeeklyStats() throws Exception {
        // Initialize the database
        contractWeeklyStatRepository.saveAndFlush(contractWeeklyStat);

        // Get all the contractWeeklyStatList
        restContractWeeklyStatMockMvc.perform(get("/api/contract-weekly-stats?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contractWeeklyStat.getId().intValue())))
            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.doubleValue())))
            .andExpect(jsonPath("$.[*].receiveTotal").value(hasItem(DEFAULT_RECEIVE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].costTotal").value(hasItem(DEFAULT_COST_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].grossProfit").value(hasItem(DEFAULT_GROSS_PROFIT.doubleValue())))
            .andExpect(jsonPath("$.[*].salesHumanCost").value(hasItem(DEFAULT_SALES_HUMAN_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].salesPayment").value(hasItem(DEFAULT_SALES_PAYMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].consultHumanCost").value(hasItem(DEFAULT_CONSULT_HUMAN_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].consultPayment").value(hasItem(DEFAULT_CONSULT_PAYMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].hardwarePurchase").value(hasItem(DEFAULT_HARDWARE_PURCHASE.doubleValue())))
            .andExpect(jsonPath("$.[*].externalSoftware").value(hasItem(DEFAULT_EXTERNAL_SOFTWARE.doubleValue())))
            .andExpect(jsonPath("$.[*].internalSoftware").value(hasItem(DEFAULT_INTERNAL_SOFTWARE.doubleValue())))
            .andExpect(jsonPath("$.[*].projectHumanCost").value(hasItem(DEFAULT_PROJECT_HUMAN_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].projectPayment").value(hasItem(DEFAULT_PROJECT_PAYMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].statWeek").value(hasItem(DEFAULT_STAT_WEEK.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))));
    }

    @Test
    @Transactional
    public void getContractWeeklyStat() throws Exception {
        // Initialize the database
        contractWeeklyStatRepository.saveAndFlush(contractWeeklyStat);

        // Get the contractWeeklyStat
        restContractWeeklyStatMockMvc.perform(get("/api/contract-weekly-stats/{id}", contractWeeklyStat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(contractWeeklyStat.getId().intValue()))
            .andExpect(jsonPath("$.contractId").value(DEFAULT_CONTRACT_ID.doubleValue()))
            .andExpect(jsonPath("$.receiveTotal").value(DEFAULT_RECEIVE_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.costTotal").value(DEFAULT_COST_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.grossProfit").value(DEFAULT_GROSS_PROFIT.doubleValue()))
            .andExpect(jsonPath("$.salesHumanCost").value(DEFAULT_SALES_HUMAN_COST.doubleValue()))
            .andExpect(jsonPath("$.salesPayment").value(DEFAULT_SALES_PAYMENT.doubleValue()))
            .andExpect(jsonPath("$.consultHumanCost").value(DEFAULT_CONSULT_HUMAN_COST.doubleValue()))
            .andExpect(jsonPath("$.consultPayment").value(DEFAULT_CONSULT_PAYMENT.doubleValue()))
            .andExpect(jsonPath("$.hardwarePurchase").value(DEFAULT_HARDWARE_PURCHASE.doubleValue()))
            .andExpect(jsonPath("$.externalSoftware").value(DEFAULT_EXTERNAL_SOFTWARE.doubleValue()))
            .andExpect(jsonPath("$.internalSoftware").value(DEFAULT_INTERNAL_SOFTWARE.doubleValue()))
            .andExpect(jsonPath("$.projectHumanCost").value(DEFAULT_PROJECT_HUMAN_COST.doubleValue()))
            .andExpect(jsonPath("$.projectPayment").value(DEFAULT_PROJECT_PAYMENT.doubleValue()))
            .andExpect(jsonPath("$.statWeek").value(DEFAULT_STAT_WEEK.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingContractWeeklyStat() throws Exception {
        // Get the contractWeeklyStat
        restContractWeeklyStatMockMvc.perform(get("/api/contract-weekly-stats/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContractWeeklyStat() throws Exception {
        // Initialize the database
        contractWeeklyStatService.save(contractWeeklyStat);

        int databaseSizeBeforeUpdate = contractWeeklyStatRepository.findAll().size();

        // Update the contractWeeklyStat
        ContractWeeklyStat updatedContractWeeklyStat = contractWeeklyStatRepository.findOne(contractWeeklyStat.getId());
        updatedContractWeeklyStat
                .contractId(UPDATED_CONTRACT_ID)
                .receiveTotal(UPDATED_RECEIVE_TOTAL)
                .costTotal(UPDATED_COST_TOTAL)
                .grossProfit(UPDATED_GROSS_PROFIT)
                .salesHumanCost(UPDATED_SALES_HUMAN_COST)
                .salesPayment(UPDATED_SALES_PAYMENT)
                .consultHumanCost(UPDATED_CONSULT_HUMAN_COST)
                .consultPayment(UPDATED_CONSULT_PAYMENT)
                .hardwarePurchase(UPDATED_HARDWARE_PURCHASE)
                .externalSoftware(UPDATED_EXTERNAL_SOFTWARE)
                .internalSoftware(UPDATED_INTERNAL_SOFTWARE)
                .projectHumanCost(UPDATED_PROJECT_HUMAN_COST)
                .projectPayment(UPDATED_PROJECT_PAYMENT)
                .createTime(UPDATED_CREATE_TIME);

        restContractWeeklyStatMockMvc.perform(put("/api/contract-weekly-stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedContractWeeklyStat)))
            .andExpect(status().isOk());

        // Validate the ContractWeeklyStat in the database
        List<ContractWeeklyStat> contractWeeklyStatList = contractWeeklyStatRepository.findAll();
        assertThat(contractWeeklyStatList).hasSize(databaseSizeBeforeUpdate);
        ContractWeeklyStat testContractWeeklyStat = contractWeeklyStatList.get(contractWeeklyStatList.size() - 1);
        assertThat(testContractWeeklyStat.getContractId()).isEqualTo(UPDATED_CONTRACT_ID);
        assertThat(testContractWeeklyStat.getReceiveTotal()).isEqualTo(UPDATED_RECEIVE_TOTAL);
        assertThat(testContractWeeklyStat.getCostTotal()).isEqualTo(UPDATED_COST_TOTAL);
        assertThat(testContractWeeklyStat.getGrossProfit()).isEqualTo(UPDATED_GROSS_PROFIT);
        assertThat(testContractWeeklyStat.getSalesHumanCost()).isEqualTo(UPDATED_SALES_HUMAN_COST);
        assertThat(testContractWeeklyStat.getSalesPayment()).isEqualTo(UPDATED_SALES_PAYMENT);
        assertThat(testContractWeeklyStat.getConsultHumanCost()).isEqualTo(UPDATED_CONSULT_HUMAN_COST);
        assertThat(testContractWeeklyStat.getConsultPayment()).isEqualTo(UPDATED_CONSULT_PAYMENT);
        assertThat(testContractWeeklyStat.getHardwarePurchase()).isEqualTo(UPDATED_HARDWARE_PURCHASE);
        assertThat(testContractWeeklyStat.getExternalSoftware()).isEqualTo(UPDATED_EXTERNAL_SOFTWARE);
        assertThat(testContractWeeklyStat.getInternalSoftware()).isEqualTo(UPDATED_INTERNAL_SOFTWARE);
        assertThat(testContractWeeklyStat.getProjectHumanCost()).isEqualTo(UPDATED_PROJECT_HUMAN_COST);
        assertThat(testContractWeeklyStat.getProjectPayment()).isEqualTo(UPDATED_PROJECT_PAYMENT);
        assertThat(testContractWeeklyStat.getStatWeek()).isEqualTo(UPDATED_STAT_WEEK);
        assertThat(testContractWeeklyStat.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);

        // Validate the ContractWeeklyStat in ElasticSearch
        ContractWeeklyStat contractWeeklyStatEs = contractWeeklyStatSearchRepository.findOne(testContractWeeklyStat.getId());
        assertThat(contractWeeklyStatEs).isEqualToComparingFieldByField(testContractWeeklyStat);
    }

    @Test
    @Transactional
    public void updateNonExistingContractWeeklyStat() throws Exception {
        int databaseSizeBeforeUpdate = contractWeeklyStatRepository.findAll().size();

        // Create the ContractWeeklyStat

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restContractWeeklyStatMockMvc.perform(put("/api/contract-weekly-stats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(contractWeeklyStat)))
            .andExpect(status().isCreated());

        // Validate the ContractWeeklyStat in the database
        List<ContractWeeklyStat> contractWeeklyStatList = contractWeeklyStatRepository.findAll();
        assertThat(contractWeeklyStatList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteContractWeeklyStat() throws Exception {
        // Initialize the database
        contractWeeklyStatService.save(contractWeeklyStat);

        int databaseSizeBeforeDelete = contractWeeklyStatRepository.findAll().size();

        // Get the contractWeeklyStat
        restContractWeeklyStatMockMvc.perform(delete("/api/contract-weekly-stats/{id}", contractWeeklyStat.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean contractWeeklyStatExistsInEs = contractWeeklyStatSearchRepository.exists(contractWeeklyStat.getId());
        assertThat(contractWeeklyStatExistsInEs).isFalse();

        // Validate the database is empty
        List<ContractWeeklyStat> contractWeeklyStatList = contractWeeklyStatRepository.findAll();
        assertThat(contractWeeklyStatList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchContractWeeklyStat() throws Exception {
        // Initialize the database
        contractWeeklyStatService.save(contractWeeklyStat);

        // Search the contractWeeklyStat
        restContractWeeklyStatMockMvc.perform(get("/api/_search/contract-weekly-stats?query=id:" + contractWeeklyStat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contractWeeklyStat.getId().intValue())))
            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.doubleValue())))
            .andExpect(jsonPath("$.[*].receiveTotal").value(hasItem(DEFAULT_RECEIVE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].costTotal").value(hasItem(DEFAULT_COST_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].grossProfit").value(hasItem(DEFAULT_GROSS_PROFIT.doubleValue())))
            .andExpect(jsonPath("$.[*].salesHumanCost").value(hasItem(DEFAULT_SALES_HUMAN_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].salesPayment").value(hasItem(DEFAULT_SALES_PAYMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].consultHumanCost").value(hasItem(DEFAULT_CONSULT_HUMAN_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].consultPayment").value(hasItem(DEFAULT_CONSULT_PAYMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].hardwarePurchase").value(hasItem(DEFAULT_HARDWARE_PURCHASE.doubleValue())))
            .andExpect(jsonPath("$.[*].externalSoftware").value(hasItem(DEFAULT_EXTERNAL_SOFTWARE.doubleValue())))
            .andExpect(jsonPath("$.[*].internalSoftware").value(hasItem(DEFAULT_INTERNAL_SOFTWARE.doubleValue())))
            .andExpect(jsonPath("$.[*].projectHumanCost").value(hasItem(DEFAULT_PROJECT_HUMAN_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].projectPayment").value(hasItem(DEFAULT_PROJECT_PAYMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].statWeek").value(hasItem(DEFAULT_STAT_WEEK.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))));
    }
}
