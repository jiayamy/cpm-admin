package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.service.UserCostService;
import com.wondertek.cpm.repository.search.UserCostSearchRepository;

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
 * Test class for the UserCostResource REST controller.
 *
 * @see UserCostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class UserCostResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_COST_MONTH = "AAAAAAAAAA";
    private static final String UPDATED_COST_MONTH = "BBBBBBBBBB";

    private static final Double DEFAULT_INTERNAL_COST = 1D;
    private static final Double UPDATED_INTERNAL_COST = 2D;

    private static final Double DEFAULT_EXTERNAL_COST = 1D;
    private static final Double UPDATED_EXTERNAL_COST = 2D;

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
    private UserCostRepository userCostRepository;

    @Inject
    private UserCostService userCostService;

    @Inject
    private UserCostSearchRepository userCostSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restUserCostMockMvc;

    private UserCost userCost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserCostResource userCostResource = new UserCostResource();
        ReflectionTestUtils.setField(userCostResource, "userCostService", userCostService);
        this.restUserCostMockMvc = MockMvcBuilders.standaloneSetup(userCostResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserCost createEntity(EntityManager em) {
        UserCost userCost = new UserCost()
                .userId(DEFAULT_USER_ID)
                .internalCost(DEFAULT_INTERNAL_COST)
                .externalCost(DEFAULT_EXTERNAL_COST)
                .status(DEFAULT_STATUS)
                .creator(DEFAULT_CREATOR)
                .createTime(DEFAULT_CREATE_TIME)
                .updator(DEFAULT_UPDATOR)
                .updateTime(DEFAULT_UPDATE_TIME);
        return userCost;
    }

    @Before
    public void initTest() {
        userCostSearchRepository.deleteAll();
        userCost = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserCost() throws Exception {
        int databaseSizeBeforeCreate = userCostRepository.findAll().size();

        // Create the UserCost

        restUserCostMockMvc.perform(post("/api/user-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userCost)))
            .andExpect(status().isCreated());

        // Validate the UserCost in the database
        List<UserCost> userCostList = userCostRepository.findAll();
        assertThat(userCostList).hasSize(databaseSizeBeforeCreate + 1);
        UserCost testUserCost = userCostList.get(userCostList.size() - 1);
        assertThat(testUserCost.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserCost.getCostMonth()).isEqualTo(DEFAULT_COST_MONTH);
        assertThat(testUserCost.getInternalCost()).isEqualTo(DEFAULT_INTERNAL_COST);
        assertThat(testUserCost.getExternalCost()).isEqualTo(DEFAULT_EXTERNAL_COST);
        assertThat(testUserCost.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testUserCost.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testUserCost.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testUserCost.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
        assertThat(testUserCost.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);

        // Validate the UserCost in ElasticSearch
        UserCost userCostEs = userCostSearchRepository.findOne(testUserCost.getId());
        assertThat(userCostEs).isEqualToComparingFieldByField(testUserCost);
    }

    @Test
    @Transactional
    public void createUserCostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userCostRepository.findAll().size();

        // Create the UserCost with an existing ID
        UserCost existingUserCost = new UserCost();
        existingUserCost.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserCostMockMvc.perform(post("/api/user-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingUserCost)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<UserCost> userCostList = userCostRepository.findAll();
        assertThat(userCostList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUserCosts() throws Exception {
        // Initialize the database
        userCostRepository.saveAndFlush(userCost);

        // Get all the userCostList
        restUserCostMockMvc.perform(get("/api/user-costs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userCost.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].costMonth").value(hasItem(DEFAULT_COST_MONTH.toString())))
            .andExpect(jsonPath("$.[*].internalCost").value(hasItem(DEFAULT_INTERNAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].externalCost").value(hasItem(DEFAULT_EXTERNAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }

    @Test
    @Transactional
    public void getUserCost() throws Exception {
        // Initialize the database
        userCostRepository.saveAndFlush(userCost);

        // Get the userCost
        restUserCostMockMvc.perform(get("/api/user-costs/{id}", userCost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userCost.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.costMonth").value(DEFAULT_COST_MONTH.toString()))
            .andExpect(jsonPath("$.internalCost").value(DEFAULT_INTERNAL_COST.doubleValue()))
            .andExpect(jsonPath("$.externalCost").value(DEFAULT_EXTERNAL_COST.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingUserCost() throws Exception {
        // Get the userCost
        restUserCostMockMvc.perform(get("/api/user-costs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserCost() throws Exception {
        // Initialize the database
        userCostService.save(userCost);

        int databaseSizeBeforeUpdate = userCostRepository.findAll().size();

        // Update the userCost
        UserCost updatedUserCost = userCostRepository.findOne(userCost.getId());
        updatedUserCost
                .userId(UPDATED_USER_ID)
                .internalCost(UPDATED_INTERNAL_COST)
                .externalCost(UPDATED_EXTERNAL_COST)
                .status(UPDATED_STATUS)
                .creator(UPDATED_CREATOR)
                .createTime(UPDATED_CREATE_TIME)
                .updator(UPDATED_UPDATOR)
                .updateTime(UPDATED_UPDATE_TIME);

        restUserCostMockMvc.perform(put("/api/user-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserCost)))
            .andExpect(status().isOk());

        // Validate the UserCost in the database
        List<UserCost> userCostList = userCostRepository.findAll();
        assertThat(userCostList).hasSize(databaseSizeBeforeUpdate);
        UserCost testUserCost = userCostList.get(userCostList.size() - 1);
        assertThat(testUserCost.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserCost.getCostMonth()).isEqualTo(UPDATED_COST_MONTH);
        assertThat(testUserCost.getInternalCost()).isEqualTo(UPDATED_INTERNAL_COST);
        assertThat(testUserCost.getExternalCost()).isEqualTo(UPDATED_EXTERNAL_COST);
        assertThat(testUserCost.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testUserCost.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testUserCost.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testUserCost.getUpdator()).isEqualTo(UPDATED_UPDATOR);
        assertThat(testUserCost.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);

        // Validate the UserCost in ElasticSearch
        UserCost userCostEs = userCostSearchRepository.findOne(testUserCost.getId());
        assertThat(userCostEs).isEqualToComparingFieldByField(testUserCost);
    }

    @Test
    @Transactional
    public void updateNonExistingUserCost() throws Exception {
        int databaseSizeBeforeUpdate = userCostRepository.findAll().size();

        // Create the UserCost

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserCostMockMvc.perform(put("/api/user-costs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userCost)))
            .andExpect(status().isCreated());

        // Validate the UserCost in the database
        List<UserCost> userCostList = userCostRepository.findAll();
        assertThat(userCostList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUserCost() throws Exception {
        // Initialize the database
        userCostService.save(userCost);

        int databaseSizeBeforeDelete = userCostRepository.findAll().size();

        // Get the userCost
        restUserCostMockMvc.perform(delete("/api/user-costs/{id}", userCost.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean userCostExistsInEs = userCostSearchRepository.exists(userCost.getId());
        assertThat(userCostExistsInEs).isFalse();

        // Validate the database is empty
        List<UserCost> userCostList = userCostRepository.findAll();
        assertThat(userCostList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUserCost() throws Exception {
        // Initialize the database
        userCostService.save(userCost);

        // Search the userCost
        restUserCostMockMvc.perform(get("/api/_search/user-costs?query=id:" + userCost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userCost.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].costMonth").value(hasItem(DEFAULT_COST_MONTH.toString())))
            .andExpect(jsonPath("$.[*].internalCost").value(hasItem(DEFAULT_INTERNAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].externalCost").value(hasItem(DEFAULT_EXTERNAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }
}
