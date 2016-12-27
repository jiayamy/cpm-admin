package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.repository.HolidayInfoRepository;
import com.wondertek.cpm.service.HolidayInfoService;
import com.wondertek.cpm.repository.search.HolidayInfoSearchRepository;

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
 * Test class for the HolidayInfoResource REST controller.
 *
 * @see HolidayInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class HolidayInfoResourceIntTest {

    private static final String DEFAULT_CURR_DAY = "AAAAAAAAAA";
    private static final String UPDATED_CURR_DAY = "BBBBBBBBBB";

    private static final Integer DEFAULT_TYPE = 1;
    private static final Integer UPDATED_TYPE = 2;

    private static final String DEFAULT_CREATOR = "AAAAAAAAAA";
    private static final String UPDATED_CREATOR = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_UPDATOR = "AAAAAAAAAA";
    private static final String UPDATED_UPDATOR = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private HolidayInfoRepository holidayInfoRepository;

    @Inject
    private HolidayInfoService holidayInfoService;

    @Inject
    private HolidayInfoSearchRepository holidayInfoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restHolidayInfoMockMvc;

    private HolidayInfo holidayInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        HolidayInfoResource holidayInfoResource = new HolidayInfoResource();
        ReflectionTestUtils.setField(holidayInfoResource, "holidayInfoService", holidayInfoService);
        this.restHolidayInfoMockMvc = MockMvcBuilders.standaloneSetup(holidayInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HolidayInfo createEntity(EntityManager em) {
        HolidayInfo holidayInfo = new HolidayInfo()
                .type(DEFAULT_TYPE)
                .creator(DEFAULT_CREATOR)
                .createTime(DEFAULT_CREATE_TIME)
                .updator(DEFAULT_UPDATOR)
                .updateTime(DEFAULT_UPDATE_TIME);
        return holidayInfo;
    }

    @Before
    public void initTest() {
        holidayInfoSearchRepository.deleteAll();
        holidayInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createHolidayInfo() throws Exception {
        int databaseSizeBeforeCreate = holidayInfoRepository.findAll().size();

        // Create the HolidayInfo

        restHolidayInfoMockMvc.perform(post("/api/holiday-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(holidayInfo)))
            .andExpect(status().isCreated());

        // Validate the HolidayInfo in the database
        List<HolidayInfo> holidayInfoList = holidayInfoRepository.findAll();
        assertThat(holidayInfoList).hasSize(databaseSizeBeforeCreate + 1);
        HolidayInfo testHolidayInfo = holidayInfoList.get(holidayInfoList.size() - 1);
        assertThat(testHolidayInfo.getCurrDay()).isEqualTo(DEFAULT_CURR_DAY);
        assertThat(testHolidayInfo.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testHolidayInfo.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testHolidayInfo.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testHolidayInfo.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
        assertThat(testHolidayInfo.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);

        // Validate the HolidayInfo in ElasticSearch
        HolidayInfo holidayInfoEs = holidayInfoSearchRepository.findOne(testHolidayInfo.getId());
        assertThat(holidayInfoEs).isEqualToComparingFieldByField(testHolidayInfo);
    }

    @Test
    @Transactional
    public void createHolidayInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = holidayInfoRepository.findAll().size();

        // Create the HolidayInfo with an existing ID
        HolidayInfo existingHolidayInfo = new HolidayInfo();
        existingHolidayInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restHolidayInfoMockMvc.perform(post("/api/holiday-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingHolidayInfo)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<HolidayInfo> holidayInfoList = holidayInfoRepository.findAll();
        assertThat(holidayInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllHolidayInfos() throws Exception {
        // Initialize the database
        holidayInfoRepository.saveAndFlush(holidayInfo);

        // Get all the holidayInfoList
        restHolidayInfoMockMvc.perform(get("/api/holiday-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(holidayInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].currDay").value(hasItem(DEFAULT_CURR_DAY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }

    @Test
    @Transactional
    public void getHolidayInfo() throws Exception {
        // Initialize the database
        holidayInfoRepository.saveAndFlush(holidayInfo);

        // Get the holidayInfo
        restHolidayInfoMockMvc.perform(get("/api/holiday-infos/{id}", holidayInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(holidayInfo.getId().intValue()))
            .andExpect(jsonPath("$.currDay").value(DEFAULT_CURR_DAY.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingHolidayInfo() throws Exception {
        // Get the holidayInfo
        restHolidayInfoMockMvc.perform(get("/api/holiday-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHolidayInfo() throws Exception {
        // Initialize the database
        holidayInfoService.save(holidayInfo);

        int databaseSizeBeforeUpdate = holidayInfoRepository.findAll().size();

        // Update the holidayInfo
        HolidayInfo updatedHolidayInfo = holidayInfoRepository.findOne(holidayInfo.getId());
        updatedHolidayInfo
                .type(UPDATED_TYPE)
                .creator(UPDATED_CREATOR)
                .createTime(UPDATED_CREATE_TIME)
                .updator(UPDATED_UPDATOR)
                .updateTime(UPDATED_UPDATE_TIME);

        restHolidayInfoMockMvc.perform(put("/api/holiday-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedHolidayInfo)))
            .andExpect(status().isOk());

        // Validate the HolidayInfo in the database
        List<HolidayInfo> holidayInfoList = holidayInfoRepository.findAll();
        assertThat(holidayInfoList).hasSize(databaseSizeBeforeUpdate);
        HolidayInfo testHolidayInfo = holidayInfoList.get(holidayInfoList.size() - 1);
        assertThat(testHolidayInfo.getCurrDay()).isEqualTo(UPDATED_CURR_DAY);
        assertThat(testHolidayInfo.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testHolidayInfo.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testHolidayInfo.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testHolidayInfo.getUpdator()).isEqualTo(UPDATED_UPDATOR);
        assertThat(testHolidayInfo.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);

        // Validate the HolidayInfo in ElasticSearch
        HolidayInfo holidayInfoEs = holidayInfoSearchRepository.findOne(testHolidayInfo.getId());
        assertThat(holidayInfoEs).isEqualToComparingFieldByField(testHolidayInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingHolidayInfo() throws Exception {
        int databaseSizeBeforeUpdate = holidayInfoRepository.findAll().size();

        // Create the HolidayInfo

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restHolidayInfoMockMvc.perform(put("/api/holiday-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(holidayInfo)))
            .andExpect(status().isCreated());

        // Validate the HolidayInfo in the database
        List<HolidayInfo> holidayInfoList = holidayInfoRepository.findAll();
        assertThat(holidayInfoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteHolidayInfo() throws Exception {
        // Initialize the database
        holidayInfoService.save(holidayInfo);

        int databaseSizeBeforeDelete = holidayInfoRepository.findAll().size();

        // Get the holidayInfo
        restHolidayInfoMockMvc.perform(delete("/api/holiday-infos/{id}", holidayInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean holidayInfoExistsInEs = holidayInfoSearchRepository.exists(holidayInfo.getId());
        assertThat(holidayInfoExistsInEs).isFalse();

        // Validate the database is empty
        List<HolidayInfo> holidayInfoList = holidayInfoRepository.findAll();
        assertThat(holidayInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchHolidayInfo() throws Exception {
        // Initialize the database
        holidayInfoService.save(holidayInfo);

        // Search the holidayInfo
        restHolidayInfoMockMvc.perform(get("/api/_search/holiday-infos?query=id:" + holidayInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(holidayInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].currDay").value(hasItem(DEFAULT_CURR_DAY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }
}
