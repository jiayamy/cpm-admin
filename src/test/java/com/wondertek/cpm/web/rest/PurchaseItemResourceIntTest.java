//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.PurchaseItem;
//import com.wondertek.cpm.repository.PurchaseItemRepository;
//import com.wondertek.cpm.service.PurchaseItemService;
//import com.wondertek.cpm.repository.search.PurchaseItemSearchRepository;
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
// * Test class for the PurchaseItemResource REST controller.
// *
// * @see PurchaseItemResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class PurchaseItemResourceIntTest {
//
//    private static final Long DEFAULT_CONTRACT_ID = 1L;
//    private static final Long UPDATED_CONTRACT_ID = 2L;
//
//    private static final Long DEFAULT_BUDGET_ID = 1L;
//    private static final Long UPDATED_BUDGET_ID = 2L;
//
//    private static final String DEFAULT_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_NAME = "BBBBBBBBBB";
//
//    private static final Integer DEFAULT_QUANTITY = 1;
//    private static final Integer UPDATED_QUANTITY = 2;
//
//    private static final Double DEFAULT_PRICE = 1D;
//    private static final Double UPDATED_PRICE = 2D;
//
//    private static final String DEFAULT_UNITS = "AAAAAAAAAA";
//    private static final String UPDATED_UNITS = "BBBBBBBBBB";
//
//    private static final Integer DEFAULT_TYPE = 1;
//    private static final Integer UPDATED_TYPE = 2;
//
//    private static final Integer DEFAULT_SOURCE = 1;
//    private static final Integer UPDATED_SOURCE = 2;
//
//    private static final String DEFAULT_PURCHASER = "AAAAAAAAAA";
//    private static final String UPDATED_PURCHASER = "BBBBBBBBBB";
//
//    private static final Double DEFAULT_TOTAL_AMOUNT = 1D;
//    private static final Double UPDATED_TOTAL_AMOUNT = 2D;
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
//    private PurchaseItemRepository purchaseItemRepository;
//
//    @Inject
//    private PurchaseItemService purchaseItemService;
//
//    @Inject
//    private PurchaseItemSearchRepository purchaseItemSearchRepository;
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
//    private MockMvc restPurchaseItemMockMvc;
//
//    private PurchaseItem purchaseItem;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        PurchaseItemResource purchaseItemResource = new PurchaseItemResource();
//        ReflectionTestUtils.setField(purchaseItemResource, "purchaseItemService", purchaseItemService);
//        this.restPurchaseItemMockMvc = MockMvcBuilders.standaloneSetup(purchaseItemResource)
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
//    public static PurchaseItem createEntity(EntityManager em) {
//        PurchaseItem purchaseItem = new PurchaseItem()
//                .contractId(DEFAULT_CONTRACT_ID)
//                .budgetId(DEFAULT_BUDGET_ID)
//                .name(DEFAULT_NAME)
//                .quantity(DEFAULT_QUANTITY)
//                .price(DEFAULT_PRICE)
//                .units(DEFAULT_UNITS)
//                .type(DEFAULT_TYPE)
//                .source(DEFAULT_SOURCE)
//                .purchaser(DEFAULT_PURCHASER)
//                .totalAmount(DEFAULT_TOTAL_AMOUNT)
//                .status(DEFAULT_STATUS)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME);
//        return purchaseItem;
//    }
//
//    @Before
//    public void initTest() {
//        purchaseItemSearchRepository.deleteAll();
//        purchaseItem = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createPurchaseItem() throws Exception {
//        int databaseSizeBeforeCreate = purchaseItemRepository.findAll().size();
//
//        // Create the PurchaseItem
//
//        restPurchaseItemMockMvc.perform(post("/api/purchase-items")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(purchaseItem)))
//            .andExpect(status().isCreated());
//
//        // Validate the PurchaseItem in the database
//        List<PurchaseItem> purchaseItemList = purchaseItemRepository.findAll();
//        assertThat(purchaseItemList).hasSize(databaseSizeBeforeCreate + 1);
//        PurchaseItem testPurchaseItem = purchaseItemList.get(purchaseItemList.size() - 1);
//        assertThat(testPurchaseItem.getContractId()).isEqualTo(DEFAULT_CONTRACT_ID);
//        assertThat(testPurchaseItem.getBudgetId()).isEqualTo(DEFAULT_BUDGET_ID);
//        assertThat(testPurchaseItem.getName()).isEqualTo(DEFAULT_NAME);
//        assertThat(testPurchaseItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
//        assertThat(testPurchaseItem.getPrice()).isEqualTo(DEFAULT_PRICE);
//        assertThat(testPurchaseItem.getUnits()).isEqualTo(DEFAULT_UNITS);
//        assertThat(testPurchaseItem.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testPurchaseItem.getSource()).isEqualTo(DEFAULT_SOURCE);
//        assertThat(testPurchaseItem.getPurchaser()).isEqualTo(DEFAULT_PURCHASER);
//        assertThat(testPurchaseItem.getTotalAmount()).isEqualTo(DEFAULT_TOTAL_AMOUNT);
//        assertThat(testPurchaseItem.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testPurchaseItem.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testPurchaseItem.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testPurchaseItem.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testPurchaseItem.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//
//        // Validate the PurchaseItem in ElasticSearch
//        PurchaseItem purchaseItemEs = purchaseItemSearchRepository.findOne(testPurchaseItem.getId());
//        assertThat(purchaseItemEs).isEqualToComparingFieldByField(testPurchaseItem);
//    }
//
//    @Test
//    @Transactional
//    public void createPurchaseItemWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = purchaseItemRepository.findAll().size();
//
//        // Create the PurchaseItem with an existing ID
//        PurchaseItem existingPurchaseItem = new PurchaseItem();
//        existingPurchaseItem.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restPurchaseItemMockMvc.perform(post("/api/purchase-items")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingPurchaseItem)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<PurchaseItem> purchaseItemList = purchaseItemRepository.findAll();
//        assertThat(purchaseItemList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllPurchaseItems() throws Exception {
//        // Initialize the database
//        purchaseItemRepository.saveAndFlush(purchaseItem);
//
//        // Get all the purchaseItemList
//        restPurchaseItemMockMvc.perform(get("/api/purchase-items?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseItem.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].budgetId").value(hasItem(DEFAULT_BUDGET_ID.intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
//            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
//            .andExpect(jsonPath("$.[*].units").value(hasItem(DEFAULT_UNITS.toString())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
//            .andExpect(jsonPath("$.[*].purchaser").value(hasItem(DEFAULT_PURCHASER.toString())))
//            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT.doubleValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getPurchaseItem() throws Exception {
//        // Initialize the database
//        purchaseItemRepository.saveAndFlush(purchaseItem);
//
//        // Get the purchaseItem
//        restPurchaseItemMockMvc.perform(get("/api/purchase-items/{id}", purchaseItem.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(purchaseItem.getId().intValue()))
//            .andExpect(jsonPath("$.contractId").value(DEFAULT_CONTRACT_ID.intValue()))
//            .andExpect(jsonPath("$.budgetId").value(DEFAULT_BUDGET_ID.intValue()))
//            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
//            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
//            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
//            .andExpect(jsonPath("$.units").value(DEFAULT_UNITS.toString()))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
//            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE))
//            .andExpect(jsonPath("$.purchaser").value(DEFAULT_PURCHASER.toString()))
//            .andExpect(jsonPath("$.totalAmount").value(DEFAULT_TOTAL_AMOUNT.doubleValue()))
//            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingPurchaseItem() throws Exception {
//        // Get the purchaseItem
//        restPurchaseItemMockMvc.perform(get("/api/purchase-items/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updatePurchaseItem() throws Exception {
//        // Initialize the database
//        purchaseItemService.save(purchaseItem);
//
//        int databaseSizeBeforeUpdate = purchaseItemRepository.findAll().size();
//
//        // Update the purchaseItem
//        PurchaseItem updatedPurchaseItem = purchaseItemRepository.findOne(purchaseItem.getId());
//        updatedPurchaseItem
//                .contractId(UPDATED_CONTRACT_ID)
//                .budgetId(UPDATED_BUDGET_ID)
//                .name(UPDATED_NAME)
//                .quantity(UPDATED_QUANTITY)
//                .price(UPDATED_PRICE)
//                .units(UPDATED_UNITS)
//                .type(UPDATED_TYPE)
//                .source(UPDATED_SOURCE)
//                .purchaser(UPDATED_PURCHASER)
//                .totalAmount(UPDATED_TOTAL_AMOUNT)
//                .status(UPDATED_STATUS)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME);
//
//        restPurchaseItemMockMvc.perform(put("/api/purchase-items")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedPurchaseItem)))
//            .andExpect(status().isOk());
//
//        // Validate the PurchaseItem in the database
//        List<PurchaseItem> purchaseItemList = purchaseItemRepository.findAll();
//        assertThat(purchaseItemList).hasSize(databaseSizeBeforeUpdate);
//        PurchaseItem testPurchaseItem = purchaseItemList.get(purchaseItemList.size() - 1);
//        assertThat(testPurchaseItem.getContractId()).isEqualTo(UPDATED_CONTRACT_ID);
//        assertThat(testPurchaseItem.getBudgetId()).isEqualTo(UPDATED_BUDGET_ID);
//        assertThat(testPurchaseItem.getName()).isEqualTo(UPDATED_NAME);
//        assertThat(testPurchaseItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
//        assertThat(testPurchaseItem.getPrice()).isEqualTo(UPDATED_PRICE);
//        assertThat(testPurchaseItem.getUnits()).isEqualTo(UPDATED_UNITS);
//        assertThat(testPurchaseItem.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testPurchaseItem.getSource()).isEqualTo(UPDATED_SOURCE);
//        assertThat(testPurchaseItem.getPurchaser()).isEqualTo(UPDATED_PURCHASER);
//        assertThat(testPurchaseItem.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
//        assertThat(testPurchaseItem.getStatus()).isEqualTo(UPDATED_STATUS);
//        assertThat(testPurchaseItem.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testPurchaseItem.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testPurchaseItem.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testPurchaseItem.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//
//        // Validate the PurchaseItem in ElasticSearch
//        PurchaseItem purchaseItemEs = purchaseItemSearchRepository.findOne(testPurchaseItem.getId());
//        assertThat(purchaseItemEs).isEqualToComparingFieldByField(testPurchaseItem);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingPurchaseItem() throws Exception {
//        int databaseSizeBeforeUpdate = purchaseItemRepository.findAll().size();
//
//        // Create the PurchaseItem
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restPurchaseItemMockMvc.perform(put("/api/purchase-items")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(purchaseItem)))
//            .andExpect(status().isCreated());
//
//        // Validate the PurchaseItem in the database
//        List<PurchaseItem> purchaseItemList = purchaseItemRepository.findAll();
//        assertThat(purchaseItemList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deletePurchaseItem() throws Exception {
//        // Initialize the database
//        purchaseItemService.save(purchaseItem);
//
//        int databaseSizeBeforeDelete = purchaseItemRepository.findAll().size();
//
//        // Get the purchaseItem
//        restPurchaseItemMockMvc.perform(delete("/api/purchase-items/{id}", purchaseItem.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean purchaseItemExistsInEs = purchaseItemSearchRepository.exists(purchaseItem.getId());
//        assertThat(purchaseItemExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<PurchaseItem> purchaseItemList = purchaseItemRepository.findAll();
//        assertThat(purchaseItemList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchPurchaseItem() throws Exception {
//        // Initialize the database
//        purchaseItemService.save(purchaseItem);
//
//        // Search the purchaseItem
//        restPurchaseItemMockMvc.perform(get("/api/_search/purchase-items?query=id:" + purchaseItem.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseItem.getId().intValue())))
//            .andExpect(jsonPath("$.[*].contractId").value(hasItem(DEFAULT_CONTRACT_ID.intValue())))
//            .andExpect(jsonPath("$.[*].budgetId").value(hasItem(DEFAULT_BUDGET_ID.intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
//            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
//            .andExpect(jsonPath("$.[*].units").value(hasItem(DEFAULT_UNITS.toString())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
//            .andExpect(jsonPath("$.[*].purchaser").value(hasItem(DEFAULT_PURCHASER.toString())))
//            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT.doubleValue())))
//            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//}
