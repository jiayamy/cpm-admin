package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.repository.search.ContractInfoSearchRepository;

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
 * Test class for the ContractInfoResource REST controller.
 *
 * @see ContractInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class ContractInfoResourceIntTest {

    private static final String DEFAULT_SERIAL_NUM = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    private static final Integer DEFAULT_TYPE = 1;
    private static final Integer UPDATED_TYPE = 2;

    private static final Boolean DEFAULT_IS_PREPARED = false;
    private static final Boolean UPDATED_IS_PREPARED = true;

    private static final Boolean DEFAULT_IS_EPIBOLIC = false;
    private static final Boolean UPDATED_IS_EPIBOLIC = true;

    private static final ZonedDateTime DEFAULT_START_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Double DEFAULT_TAX_RATE = 1D;
    private static final Double UPDATED_TAX_RATE = 2D;

    private static final Double DEFAULT_TAXES = 1D;
    private static final Double UPDATED_TAXES = 2D;

    private static final Double DEFAULT_SHARE_RATE = 1D;
    private static final Double UPDATED_SHARE_RATE = 2D;

    private static final Double DEFAULT_SHARE_COST = 1D;
    private static final Double UPDATED_SHARE_COST = 2D;

    private static final String DEFAULT_PAYMENT_WAY = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_WAY = "BBBBBBBBBB";

    private static final String DEFAULT_CONTRACTOR = "AAAAAAAAAA";
    private static final String UPDATED_CONTRACTOR = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_POSTCODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTCODE = "BBBBBBBBBB";

    private static final String DEFAULT_LINKMAN = "AAAAAAAAAA";
    private static final String UPDATED_LINKMAN = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_DEPT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_DEPT = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final Double DEFAULT_RECEIVE_TOTAL = 1D;
    private static final Double UPDATED_RECEIVE_TOTAL = 2D;

    private static final Double DEFAULT_FINISH_RATE = 1D;
    private static final Double UPDATED_FINISH_RATE = 2D;

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
    private ContractInfoRepository contractInfoRepository;

    @Inject
    private ContractInfoService contractInfoService;

    @Inject
    private ContractInfoSearchRepository contractInfoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restContractInfoMockMvc;

    private ContractInfo contractInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContractInfoResource contractInfoResource = new ContractInfoResource();
        ReflectionTestUtils.setField(contractInfoResource, "contractInfoService", contractInfoService);
        this.restContractInfoMockMvc = MockMvcBuilders.standaloneSetup(contractInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContractInfo createEntity(EntityManager em) {
        ContractInfo contractInfo = new ContractInfo()
                .serialNum(DEFAULT_SERIAL_NUM)
                .name(DEFAULT_NAME)
                .amount(DEFAULT_AMOUNT)
                .type(DEFAULT_TYPE)
                .isPrepared(DEFAULT_IS_PREPARED)
                .isEpibolic(DEFAULT_IS_EPIBOLIC)
                .startDay(DEFAULT_START_DAY)
                .endDay(DEFAULT_END_DAY)
                .taxRate(DEFAULT_TAX_RATE)
                .taxes(DEFAULT_TAXES)
                .shareRate(DEFAULT_SHARE_RATE)
                .shareCost(DEFAULT_SHARE_COST)
                .paymentWay(DEFAULT_PAYMENT_WAY)
                .contractor(DEFAULT_CONTRACTOR)
                .address(DEFAULT_ADDRESS)
                .postcode(DEFAULT_POSTCODE)
                .linkman(DEFAULT_LINKMAN)
                .contactDept(DEFAULT_CONTACT_DEPT)
                .telephone(DEFAULT_TELEPHONE)
                .receiveTotal(DEFAULT_RECEIVE_TOTAL)
                .finishRate(DEFAULT_FINISH_RATE)
                .status(DEFAULT_STATUS)
                .creator(DEFAULT_CREATOR)
                .createTime(DEFAULT_CREATE_TIME)
                .updator(DEFAULT_UPDATOR)
                .updateTime(DEFAULT_UPDATE_TIME);
        return contractInfo;
    }

    @Before
    public void initTest() {
        contractInfoSearchRepository.deleteAll();
        contractInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createContractInfo() throws Exception {
        int databaseSizeBeforeCreate = contractInfoRepository.findAll().size();

        // Create the ContractInfo

        restContractInfoMockMvc.perform(post("/api/contract-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(contractInfo)))
            .andExpect(status().isCreated());

        // Validate the ContractInfo in the database
        List<ContractInfo> contractInfoList = contractInfoRepository.findAll();
        assertThat(contractInfoList).hasSize(databaseSizeBeforeCreate + 1);
        ContractInfo testContractInfo = contractInfoList.get(contractInfoList.size() - 1);
        assertThat(testContractInfo.getSerialNum()).isEqualTo(DEFAULT_SERIAL_NUM);
        assertThat(testContractInfo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testContractInfo.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testContractInfo.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testContractInfo.isIsPrepared()).isEqualTo(DEFAULT_IS_PREPARED);
        assertThat(testContractInfo.isIsEpibolic()).isEqualTo(DEFAULT_IS_EPIBOLIC);
        assertThat(testContractInfo.getStartDay()).isEqualTo(DEFAULT_START_DAY);
        assertThat(testContractInfo.getEndDay()).isEqualTo(DEFAULT_END_DAY);
        assertThat(testContractInfo.getTaxRate()).isEqualTo(DEFAULT_TAX_RATE);
        assertThat(testContractInfo.getTaxes()).isEqualTo(DEFAULT_TAXES);
        assertThat(testContractInfo.getShareRate()).isEqualTo(DEFAULT_SHARE_RATE);
        assertThat(testContractInfo.getShareCost()).isEqualTo(DEFAULT_SHARE_COST);
        assertThat(testContractInfo.getPaymentWay()).isEqualTo(DEFAULT_PAYMENT_WAY);
        assertThat(testContractInfo.getContractor()).isEqualTo(DEFAULT_CONTRACTOR);
        assertThat(testContractInfo.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testContractInfo.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testContractInfo.getLinkman()).isEqualTo(DEFAULT_LINKMAN);
        assertThat(testContractInfo.getContactDept()).isEqualTo(DEFAULT_CONTACT_DEPT);
        assertThat(testContractInfo.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testContractInfo.getReceiveTotal()).isEqualTo(DEFAULT_RECEIVE_TOTAL);
        assertThat(testContractInfo.getFinishRate()).isEqualTo(DEFAULT_FINISH_RATE);
        assertThat(testContractInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testContractInfo.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testContractInfo.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testContractInfo.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
        assertThat(testContractInfo.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);

        // Validate the ContractInfo in ElasticSearch
        ContractInfo contractInfoEs = contractInfoSearchRepository.findOne(testContractInfo.getId());
        assertThat(contractInfoEs).isEqualToComparingFieldByField(testContractInfo);
    }

    @Test
    @Transactional
    public void createContractInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = contractInfoRepository.findAll().size();

        // Create the ContractInfo with an existing ID
        ContractInfo existingContractInfo = new ContractInfo();
        existingContractInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restContractInfoMockMvc.perform(post("/api/contract-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingContractInfo)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ContractInfo> contractInfoList = contractInfoRepository.findAll();
        assertThat(contractInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllContractInfos() throws Exception {
        // Initialize the database
        contractInfoRepository.saveAndFlush(contractInfo);

        // Get all the contractInfoList
        restContractInfoMockMvc.perform(get("/api/contract-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contractInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].serialNum").value(hasItem(DEFAULT_SERIAL_NUM.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].isPrepared").value(hasItem(DEFAULT_IS_PREPARED.booleanValue())))
            .andExpect(jsonPath("$.[*].isEpibolic").value(hasItem(DEFAULT_IS_EPIBOLIC.booleanValue())))
            .andExpect(jsonPath("$.[*].startDay").value(hasItem(sameInstant(DEFAULT_START_DAY))))
            .andExpect(jsonPath("$.[*].endDay").value(hasItem(sameInstant(DEFAULT_END_DAY))))
            .andExpect(jsonPath("$.[*].taxRate").value(hasItem(DEFAULT_TAX_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].taxes").value(hasItem(DEFAULT_TAXES.doubleValue())))
            .andExpect(jsonPath("$.[*].shareRate").value(hasItem(DEFAULT_SHARE_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].shareCost").value(hasItem(DEFAULT_SHARE_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].paymentWay").value(hasItem(DEFAULT_PAYMENT_WAY.toString())))
            .andExpect(jsonPath("$.[*].contractor").value(hasItem(DEFAULT_CONTRACTOR.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE.toString())))
            .andExpect(jsonPath("$.[*].linkman").value(hasItem(DEFAULT_LINKMAN.toString())))
            .andExpect(jsonPath("$.[*].contactDept").value(hasItem(DEFAULT_CONTACT_DEPT.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].receiveTotal").value(hasItem(DEFAULT_RECEIVE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].finishRate").value(hasItem(DEFAULT_FINISH_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }

    @Test
    @Transactional
    public void getContractInfo() throws Exception {
        // Initialize the database
        contractInfoRepository.saveAndFlush(contractInfo);

        // Get the contractInfo
        restContractInfoMockMvc.perform(get("/api/contract-infos/{id}", contractInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(contractInfo.getId().intValue()))
            .andExpect(jsonPath("$.serialNum").value(DEFAULT_SERIAL_NUM.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.isPrepared").value(DEFAULT_IS_PREPARED.booleanValue()))
            .andExpect(jsonPath("$.isEpibolic").value(DEFAULT_IS_EPIBOLIC.booleanValue()))
            .andExpect(jsonPath("$.startDay").value(sameInstant(DEFAULT_START_DAY)))
            .andExpect(jsonPath("$.endDay").value(sameInstant(DEFAULT_END_DAY)))
            .andExpect(jsonPath("$.taxRate").value(DEFAULT_TAX_RATE.doubleValue()))
            .andExpect(jsonPath("$.taxes").value(DEFAULT_TAXES.doubleValue()))
            .andExpect(jsonPath("$.shareRate").value(DEFAULT_SHARE_RATE.doubleValue()))
            .andExpect(jsonPath("$.shareCost").value(DEFAULT_SHARE_COST.doubleValue()))
            .andExpect(jsonPath("$.paymentWay").value(DEFAULT_PAYMENT_WAY.toString()))
            .andExpect(jsonPath("$.contractor").value(DEFAULT_CONTRACTOR.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.postcode").value(DEFAULT_POSTCODE.toString()))
            .andExpect(jsonPath("$.linkman").value(DEFAULT_LINKMAN.toString()))
            .andExpect(jsonPath("$.contactDept").value(DEFAULT_CONTACT_DEPT.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.toString()))
            .andExpect(jsonPath("$.receiveTotal").value(DEFAULT_RECEIVE_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.finishRate").value(DEFAULT_FINISH_RATE.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingContractInfo() throws Exception {
        // Get the contractInfo
        restContractInfoMockMvc.perform(get("/api/contract-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContractInfo() throws Exception {
        // Initialize the database
        contractInfoService.save(contractInfo);

        int databaseSizeBeforeUpdate = contractInfoRepository.findAll().size();

        // Update the contractInfo
        ContractInfo updatedContractInfo = contractInfoRepository.findOne(contractInfo.getId());
        updatedContractInfo
                .serialNum(UPDATED_SERIAL_NUM)
                .name(UPDATED_NAME)
                .amount(UPDATED_AMOUNT)
                .type(UPDATED_TYPE)
                .isPrepared(UPDATED_IS_PREPARED)
                .isEpibolic(UPDATED_IS_EPIBOLIC)
                .startDay(UPDATED_START_DAY)
                .endDay(UPDATED_END_DAY)
                .taxRate(UPDATED_TAX_RATE)
                .taxes(UPDATED_TAXES)
                .shareRate(UPDATED_SHARE_RATE)
                .shareCost(UPDATED_SHARE_COST)
                .paymentWay(UPDATED_PAYMENT_WAY)
                .contractor(UPDATED_CONTRACTOR)
                .address(UPDATED_ADDRESS)
                .postcode(UPDATED_POSTCODE)
                .linkman(UPDATED_LINKMAN)
                .contactDept(UPDATED_CONTACT_DEPT)
                .telephone(UPDATED_TELEPHONE)
                .receiveTotal(UPDATED_RECEIVE_TOTAL)
                .finishRate(UPDATED_FINISH_RATE)
                .status(UPDATED_STATUS)
                .creator(UPDATED_CREATOR)
                .createTime(UPDATED_CREATE_TIME)
                .updator(UPDATED_UPDATOR)
                .updateTime(UPDATED_UPDATE_TIME);

        restContractInfoMockMvc.perform(put("/api/contract-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedContractInfo)))
            .andExpect(status().isOk());

        // Validate the ContractInfo in the database
        List<ContractInfo> contractInfoList = contractInfoRepository.findAll();
        assertThat(contractInfoList).hasSize(databaseSizeBeforeUpdate);
        ContractInfo testContractInfo = contractInfoList.get(contractInfoList.size() - 1);
        assertThat(testContractInfo.getSerialNum()).isEqualTo(UPDATED_SERIAL_NUM);
        assertThat(testContractInfo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testContractInfo.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testContractInfo.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testContractInfo.isIsPrepared()).isEqualTo(UPDATED_IS_PREPARED);
        assertThat(testContractInfo.isIsEpibolic()).isEqualTo(UPDATED_IS_EPIBOLIC);
        assertThat(testContractInfo.getStartDay()).isEqualTo(UPDATED_START_DAY);
        assertThat(testContractInfo.getEndDay()).isEqualTo(UPDATED_END_DAY);
        assertThat(testContractInfo.getTaxRate()).isEqualTo(UPDATED_TAX_RATE);
        assertThat(testContractInfo.getTaxes()).isEqualTo(UPDATED_TAXES);
        assertThat(testContractInfo.getShareRate()).isEqualTo(UPDATED_SHARE_RATE);
        assertThat(testContractInfo.getShareCost()).isEqualTo(UPDATED_SHARE_COST);
        assertThat(testContractInfo.getPaymentWay()).isEqualTo(UPDATED_PAYMENT_WAY);
        assertThat(testContractInfo.getContractor()).isEqualTo(UPDATED_CONTRACTOR);
        assertThat(testContractInfo.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testContractInfo.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testContractInfo.getLinkman()).isEqualTo(UPDATED_LINKMAN);
        assertThat(testContractInfo.getContactDept()).isEqualTo(UPDATED_CONTACT_DEPT);
        assertThat(testContractInfo.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testContractInfo.getReceiveTotal()).isEqualTo(UPDATED_RECEIVE_TOTAL);
        assertThat(testContractInfo.getFinishRate()).isEqualTo(UPDATED_FINISH_RATE);
        assertThat(testContractInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testContractInfo.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testContractInfo.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testContractInfo.getUpdator()).isEqualTo(UPDATED_UPDATOR);
        assertThat(testContractInfo.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);

        // Validate the ContractInfo in ElasticSearch
        ContractInfo contractInfoEs = contractInfoSearchRepository.findOne(testContractInfo.getId());
        assertThat(contractInfoEs).isEqualToComparingFieldByField(testContractInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingContractInfo() throws Exception {
        int databaseSizeBeforeUpdate = contractInfoRepository.findAll().size();

        // Create the ContractInfo

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restContractInfoMockMvc.perform(put("/api/contract-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(contractInfo)))
            .andExpect(status().isCreated());

        // Validate the ContractInfo in the database
        List<ContractInfo> contractInfoList = contractInfoRepository.findAll();
        assertThat(contractInfoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteContractInfo() throws Exception {
        // Initialize the database
        contractInfoService.save(contractInfo);

        int databaseSizeBeforeDelete = contractInfoRepository.findAll().size();

        // Get the contractInfo
        restContractInfoMockMvc.perform(delete("/api/contract-infos/{id}", contractInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean contractInfoExistsInEs = contractInfoSearchRepository.exists(contractInfo.getId());
        assertThat(contractInfoExistsInEs).isFalse();

        // Validate the database is empty
        List<ContractInfo> contractInfoList = contractInfoRepository.findAll();
        assertThat(contractInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchContractInfo() throws Exception {
        // Initialize the database
        contractInfoService.save(contractInfo);

        // Search the contractInfo
        restContractInfoMockMvc.perform(get("/api/_search/contract-infos?query=id:" + contractInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contractInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].serialNum").value(hasItem(DEFAULT_SERIAL_NUM.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].isPrepared").value(hasItem(DEFAULT_IS_PREPARED.booleanValue())))
            .andExpect(jsonPath("$.[*].isEpibolic").value(hasItem(DEFAULT_IS_EPIBOLIC.booleanValue())))
            .andExpect(jsonPath("$.[*].startDay").value(hasItem(sameInstant(DEFAULT_START_DAY))))
            .andExpect(jsonPath("$.[*].endDay").value(hasItem(sameInstant(DEFAULT_END_DAY))))
            .andExpect(jsonPath("$.[*].taxRate").value(hasItem(DEFAULT_TAX_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].taxes").value(hasItem(DEFAULT_TAXES.doubleValue())))
            .andExpect(jsonPath("$.[*].shareRate").value(hasItem(DEFAULT_SHARE_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].shareCost").value(hasItem(DEFAULT_SHARE_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].paymentWay").value(hasItem(DEFAULT_PAYMENT_WAY.toString())))
            .andExpect(jsonPath("$.[*].contractor").value(hasItem(DEFAULT_CONTRACTOR.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE.toString())))
            .andExpect(jsonPath("$.[*].linkman").value(hasItem(DEFAULT_LINKMAN.toString())))
            .andExpect(jsonPath("$.[*].contactDept").value(hasItem(DEFAULT_CONTACT_DEPT.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].receiveTotal").value(hasItem(DEFAULT_RECEIVE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].finishRate").value(hasItem(DEFAULT_FINISH_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }
}
