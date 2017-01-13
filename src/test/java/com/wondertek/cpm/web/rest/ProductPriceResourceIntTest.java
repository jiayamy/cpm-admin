//package com.wondertek.cpm.web.rest;
//
//import com.wondertek.cpm.CpmApp;
//
//import com.wondertek.cpm.domain.ProductPrice;
//import com.wondertek.cpm.repository.ProductPriceRepository;
//import com.wondertek.cpm.service.ProductPriceService;
//import com.wondertek.cpm.repository.search.ProductPriceSearchRepository;
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
// * Test class for the ProductPriceResource REST controller.
// *
// * @see ProductPriceResource
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpmApp.class)
//public class ProductPriceResourceIntTest {
//
//    private static final String DEFAULT_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_NAME = "BBBBBBBBBB";
//
//    private static final Integer DEFAULT_TYPE = 1;
//    private static final Integer UPDATED_TYPE = 2;
//
//    private static final String DEFAULT_UNITS = "AAAAAAAAAA";
//    private static final String UPDATED_UNITS = "BBBBBBBBBB";
//
//    private static final Double DEFAULT_PRICE = 1D;
//    private static final Double UPDATED_PRICE = 2D;
//
//    private static final Integer DEFAULT_SOURCE = 1;
//    private static final Integer UPDATED_SOURCE = 2;
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
//    private ProductPriceRepository productPriceRepository;
//
//    @Inject
//    private ProductPriceService productPriceService;
//
//    @Inject
//    private ProductPriceSearchRepository productPriceSearchRepository;
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
//    private MockMvc restProductPriceMockMvc;
//
//    private ProductPrice productPrice;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        ProductPriceResource productPriceResource = new ProductPriceResource();
//        ReflectionTestUtils.setField(productPriceResource, "productPriceService", productPriceService);
//        this.restProductPriceMockMvc = MockMvcBuilders.standaloneSetup(productPriceResource)
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
//    public static ProductPrice createEntity(EntityManager em) {
//        ProductPrice productPrice = new ProductPrice()
//                .name(DEFAULT_NAME)
//                .type(DEFAULT_TYPE)
//                .units(DEFAULT_UNITS)
//                .price(DEFAULT_PRICE)
//                .source(DEFAULT_SOURCE)
//                .creator(DEFAULT_CREATOR)
//                .createTime(DEFAULT_CREATE_TIME)
//                .updator(DEFAULT_UPDATOR)
//                .updateTime(DEFAULT_UPDATE_TIME);
//        return productPrice;
//    }
//
//    @Before
//    public void initTest() {
//        productPriceSearchRepository.deleteAll();
//        productPrice = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createProductPrice() throws Exception {
//        int databaseSizeBeforeCreate = productPriceRepository.findAll().size();
//
//        // Create the ProductPrice
//
//        restProductPriceMockMvc.perform(post("/api/product-prices")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(productPrice)))
//            .andExpect(status().isCreated());
//
//        // Validate the ProductPrice in the database
//        List<ProductPrice> productPriceList = productPriceRepository.findAll();
//        assertThat(productPriceList).hasSize(databaseSizeBeforeCreate + 1);
//        ProductPrice testProductPrice = productPriceList.get(productPriceList.size() - 1);
//        assertThat(testProductPrice.getName()).isEqualTo(DEFAULT_NAME);
//        assertThat(testProductPrice.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testProductPrice.getUnits()).isEqualTo(DEFAULT_UNITS);
//        assertThat(testProductPrice.getPrice()).isEqualTo(DEFAULT_PRICE);
//        assertThat(testProductPrice.getSource()).isEqualTo(DEFAULT_SOURCE);
//        assertThat(testProductPrice.getCreator()).isEqualTo(DEFAULT_CREATOR);
//        assertThat(testProductPrice.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
//        assertThat(testProductPrice.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
//        assertThat(testProductPrice.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
//
//        // Validate the ProductPrice in ElasticSearch
//        ProductPrice productPriceEs = productPriceSearchRepository.findOne(testProductPrice.getId());
//        assertThat(productPriceEs).isEqualToComparingFieldByField(testProductPrice);
//    }
//
//    @Test
//    @Transactional
//    public void createProductPriceWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = productPriceRepository.findAll().size();
//
//        // Create the ProductPrice with an existing ID
//        ProductPrice existingProductPrice = new ProductPrice();
//        existingProductPrice.setId(1L);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restProductPriceMockMvc.perform(post("/api/product-prices")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(existingProductPrice)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Alice in the database
//        List<ProductPrice> productPriceList = productPriceRepository.findAll();
//        assertThat(productPriceList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    public void getAllProductPrices() throws Exception {
//        // Initialize the database
//        productPriceRepository.saveAndFlush(productPrice);
//
//        // Get all the productPriceList
//        restProductPriceMockMvc.perform(get("/api/product-prices?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(productPrice.getId().intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].units").value(hasItem(DEFAULT_UNITS.toString())))
//            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
//            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//
//    @Test
//    @Transactional
//    public void getProductPrice() throws Exception {
//        // Initialize the database
//        productPriceRepository.saveAndFlush(productPrice);
//
//        // Get the productPrice
//        restProductPriceMockMvc.perform(get("/api/product-prices/{id}", productPrice.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(productPrice.getId().intValue()))
//            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
//            .andExpect(jsonPath("$.units").value(DEFAULT_UNITS.toString()))
//            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
//            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE))
//            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
//            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
//            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
//            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
//    }
//
//    @Test
//    @Transactional
//    public void getNonExistingProductPrice() throws Exception {
//        // Get the productPrice
//        restProductPriceMockMvc.perform(get("/api/product-prices/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateProductPrice() throws Exception {
//        // Initialize the database
//        productPriceService.save(productPrice);
//
//        int databaseSizeBeforeUpdate = productPriceRepository.findAll().size();
//
//        // Update the productPrice
//        ProductPrice updatedProductPrice = productPriceRepository.findOne(productPrice.getId());
//        updatedProductPrice
//                .name(UPDATED_NAME)
//                .type(UPDATED_TYPE)
//                .units(UPDATED_UNITS)
//                .price(UPDATED_PRICE)
//                .source(UPDATED_SOURCE)
//                .creator(UPDATED_CREATOR)
//                .createTime(UPDATED_CREATE_TIME)
//                .updator(UPDATED_UPDATOR)
//                .updateTime(UPDATED_UPDATE_TIME);
//
//        restProductPriceMockMvc.perform(put("/api/product-prices")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(updatedProductPrice)))
//            .andExpect(status().isOk());
//
//        // Validate the ProductPrice in the database
//        List<ProductPrice> productPriceList = productPriceRepository.findAll();
//        assertThat(productPriceList).hasSize(databaseSizeBeforeUpdate);
//        ProductPrice testProductPrice = productPriceList.get(productPriceList.size() - 1);
//        assertThat(testProductPrice.getName()).isEqualTo(UPDATED_NAME);
//        assertThat(testProductPrice.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testProductPrice.getUnits()).isEqualTo(UPDATED_UNITS);
//        assertThat(testProductPrice.getPrice()).isEqualTo(UPDATED_PRICE);
//        assertThat(testProductPrice.getSource()).isEqualTo(UPDATED_SOURCE);
//        assertThat(testProductPrice.getCreator()).isEqualTo(UPDATED_CREATOR);
//        assertThat(testProductPrice.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
//        assertThat(testProductPrice.getUpdator()).isEqualTo(UPDATED_UPDATOR);
//        assertThat(testProductPrice.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
//
//        // Validate the ProductPrice in ElasticSearch
//        ProductPrice productPriceEs = productPriceSearchRepository.findOne(testProductPrice.getId());
//        assertThat(productPriceEs).isEqualToComparingFieldByField(testProductPrice);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingProductPrice() throws Exception {
//        int databaseSizeBeforeUpdate = productPriceRepository.findAll().size();
//
//        // Create the ProductPrice
//
//        // If the entity doesn't have an ID, it will be created instead of just being updated
//        restProductPriceMockMvc.perform(put("/api/product-prices")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(productPrice)))
//            .andExpect(status().isCreated());
//
//        // Validate the ProductPrice in the database
//        List<ProductPrice> productPriceList = productPriceRepository.findAll();
//        assertThat(productPriceList).hasSize(databaseSizeBeforeUpdate + 1);
//    }
//
//    @Test
//    @Transactional
//    public void deleteProductPrice() throws Exception {
//        // Initialize the database
//        productPriceService.save(productPrice);
//
//        int databaseSizeBeforeDelete = productPriceRepository.findAll().size();
//
//        // Get the productPrice
//        restProductPriceMockMvc.perform(delete("/api/product-prices/{id}", productPrice.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isOk());
//
//        // Validate ElasticSearch is empty
//        boolean productPriceExistsInEs = productPriceSearchRepository.exists(productPrice.getId());
//        assertThat(productPriceExistsInEs).isFalse();
//
//        // Validate the database is empty
//        List<ProductPrice> productPriceList = productPriceRepository.findAll();
//        assertThat(productPriceList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    @Transactional
//    public void searchProductPrice() throws Exception {
//        // Initialize the database
//        productPriceService.save(productPrice);
//
//        // Search the productPrice
//        restProductPriceMockMvc.perform(get("/api/_search/product-prices?query=id:" + productPrice.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(productPrice.getId().intValue())))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].units").value(hasItem(DEFAULT_UNITS.toString())))
//            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
//            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
//            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
//            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
//            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
//            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
//    }
//}
