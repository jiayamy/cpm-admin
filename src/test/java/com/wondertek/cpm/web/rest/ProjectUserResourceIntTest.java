package com.wondertek.cpm.web.rest;

import com.wondertek.cpm.CpmApp;

import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.repository.ProjectUserRepository;
import com.wondertek.cpm.service.ProjectUserService;
import com.wondertek.cpm.repository.search.ProjectUserSearchRepository;

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
 * Test class for the ProjectUserResource REST controller.
 *
 * @see ProjectUserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CpmApp.class)
public class ProjectUserResourceIntTest {

    private static final Long DEFAULT_PROJECT_ID = 1L;
    private static final Long UPDATED_PROJECT_ID = 2L;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_USER_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_USER_ROLE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_JOIN_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_JOIN_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_GOODBYE_DAY = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_GOODBYE_DAY = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATOR = "AAAAAAAAAA";
    private static final String UPDATED_CREATOR = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_UPDATOR = "AAAAAAAAAA";
    private static final String UPDATED_UPDATOR = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private ProjectUserRepository projectUserRepository;

    @Inject
    private ProjectUserService projectUserService;

    @Inject
    private ProjectUserSearchRepository projectUserSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProjectUserMockMvc;

    private ProjectUser projectUser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectUserResource projectUserResource = new ProjectUserResource();
        ReflectionTestUtils.setField(projectUserResource, "projectUserService", projectUserService);
        this.restProjectUserMockMvc = MockMvcBuilders.standaloneSetup(projectUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectUser createEntity(EntityManager em) {
        ProjectUser projectUser = new ProjectUser()
                .projectId(DEFAULT_PROJECT_ID)
                .userId(DEFAULT_USER_ID)
                .userName(DEFAULT_USER_NAME)
                .userRole(DEFAULT_USER_ROLE)
                .creator(DEFAULT_CREATOR)
                .createTime(DEFAULT_CREATE_TIME)
                .updator(DEFAULT_UPDATOR)
                .updateTime(DEFAULT_UPDATE_TIME);
        return projectUser;
    }

    @Before
    public void initTest() {
        projectUserSearchRepository.deleteAll();
        projectUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createProjectUser() throws Exception {
        int databaseSizeBeforeCreate = projectUserRepository.findAll().size();

        // Create the ProjectUser

        restProjectUserMockMvc.perform(post("/api/project-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectUser)))
            .andExpect(status().isCreated());

        // Validate the ProjectUser in the database
        List<ProjectUser> projectUserList = projectUserRepository.findAll();
        assertThat(projectUserList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectUser testProjectUser = projectUserList.get(projectUserList.size() - 1);
        assertThat(testProjectUser.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testProjectUser.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testProjectUser.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testProjectUser.getUserRole()).isEqualTo(DEFAULT_USER_ROLE);
        assertThat(testProjectUser.getJoinDay()).isEqualTo(DEFAULT_JOIN_DAY);
        assertThat(testProjectUser.getLeaveDay()).isEqualTo(DEFAULT_GOODBYE_DAY);
        assertThat(testProjectUser.getCreator()).isEqualTo(DEFAULT_CREATOR);
        assertThat(testProjectUser.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testProjectUser.getUpdator()).isEqualTo(DEFAULT_UPDATOR);
        assertThat(testProjectUser.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);

        // Validate the ProjectUser in ElasticSearch
        ProjectUser projectUserEs = projectUserSearchRepository.findOne(testProjectUser.getId());
        assertThat(projectUserEs).isEqualToComparingFieldByField(testProjectUser);
    }

    @Test
    @Transactional
    public void createProjectUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectUserRepository.findAll().size();

        // Create the ProjectUser with an existing ID
        ProjectUser existingProjectUser = new ProjectUser();
        existingProjectUser.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectUserMockMvc.perform(post("/api/project-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingProjectUser)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProjectUser> projectUserList = projectUserRepository.findAll();
        assertThat(projectUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProjectUsers() throws Exception {
        // Initialize the database
        projectUserRepository.saveAndFlush(projectUser);

        // Get all the projectUserList
        restProjectUserMockMvc.perform(get("/api/project-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
            .andExpect(jsonPath("$.[*].userRole").value(hasItem(DEFAULT_USER_ROLE.toString())))
            .andExpect(jsonPath("$.[*].joinDay").value(hasItem(sameInstant(DEFAULT_JOIN_DAY))))
            .andExpect(jsonPath("$.[*].goodbyeDay").value(hasItem(sameInstant(DEFAULT_GOODBYE_DAY))))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }

    @Test
    @Transactional
    public void getProjectUser() throws Exception {
        // Initialize the database
        projectUserRepository.saveAndFlush(projectUser);

        // Get the projectUser
        restProjectUserMockMvc.perform(get("/api/project-users/{id}", projectUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectUser.getId().intValue()))
            .andExpect(jsonPath("$.projectId").value(DEFAULT_PROJECT_ID.intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()))
            .andExpect(jsonPath("$.userRole").value(DEFAULT_USER_ROLE.toString()))
            .andExpect(jsonPath("$.joinDay").value(sameInstant(DEFAULT_JOIN_DAY)))
            .andExpect(jsonPath("$.goodbyeDay").value(sameInstant(DEFAULT_GOODBYE_DAY)))
            .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
            .andExpect(jsonPath("$.updator").value(DEFAULT_UPDATOR.toString()))
            .andExpect(jsonPath("$.updateTime").value(sameInstant(DEFAULT_UPDATE_TIME)));
    }

    @Test
    @Transactional
    public void getNonExistingProjectUser() throws Exception {
        // Get the projectUser
        restProjectUserMockMvc.perform(get("/api/project-users/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectUser() throws Exception {
        // Initialize the database
        projectUserService.save(projectUser);

        int databaseSizeBeforeUpdate = projectUserRepository.findAll().size();

        // Update the projectUser
        ProjectUser updatedProjectUser = projectUserRepository.findOne(projectUser.getId());
        updatedProjectUser
                .projectId(UPDATED_PROJECT_ID)
                .userId(UPDATED_USER_ID)
                .userName(UPDATED_USER_NAME)
                .userRole(UPDATED_USER_ROLE)
                .creator(UPDATED_CREATOR)
                .createTime(UPDATED_CREATE_TIME)
                .updator(UPDATED_UPDATOR)
                .updateTime(UPDATED_UPDATE_TIME);

        restProjectUserMockMvc.perform(put("/api/project-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProjectUser)))
            .andExpect(status().isOk());

        // Validate the ProjectUser in the database
        List<ProjectUser> projectUserList = projectUserRepository.findAll();
        assertThat(projectUserList).hasSize(databaseSizeBeforeUpdate);
        ProjectUser testProjectUser = projectUserList.get(projectUserList.size() - 1);
        assertThat(testProjectUser.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testProjectUser.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testProjectUser.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testProjectUser.getUserRole()).isEqualTo(UPDATED_USER_ROLE);
        assertThat(testProjectUser.getJoinDay()).isEqualTo(UPDATED_JOIN_DAY);
        assertThat(testProjectUser.getLeaveDay()).isEqualTo(UPDATED_GOODBYE_DAY);
        assertThat(testProjectUser.getCreator()).isEqualTo(UPDATED_CREATOR);
        assertThat(testProjectUser.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testProjectUser.getUpdator()).isEqualTo(UPDATED_UPDATOR);
        assertThat(testProjectUser.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);

        // Validate the ProjectUser in ElasticSearch
        ProjectUser projectUserEs = projectUserSearchRepository.findOne(testProjectUser.getId());
        assertThat(projectUserEs).isEqualToComparingFieldByField(testProjectUser);
    }

    @Test
    @Transactional
    public void updateNonExistingProjectUser() throws Exception {
        int databaseSizeBeforeUpdate = projectUserRepository.findAll().size();

        // Create the ProjectUser

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProjectUserMockMvc.perform(put("/api/project-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectUser)))
            .andExpect(status().isCreated());

        // Validate the ProjectUser in the database
        List<ProjectUser> projectUserList = projectUserRepository.findAll();
        assertThat(projectUserList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProjectUser() throws Exception {
        // Initialize the database
        projectUserService.save(projectUser);

        int databaseSizeBeforeDelete = projectUserRepository.findAll().size();

        // Get the projectUser
        restProjectUserMockMvc.perform(delete("/api/project-users/{id}", projectUser.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean projectUserExistsInEs = projectUserSearchRepository.exists(projectUser.getId());
        assertThat(projectUserExistsInEs).isFalse();

        // Validate the database is empty
        List<ProjectUser> projectUserList = projectUserRepository.findAll();
        assertThat(projectUserList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectUser() throws Exception {
        // Initialize the database
        projectUserService.save(projectUser);

        // Search the projectUser
        restProjectUserMockMvc.perform(get("/api/_search/project-users?query=id:" + projectUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
            .andExpect(jsonPath("$.[*].userRole").value(hasItem(DEFAULT_USER_ROLE.toString())))
            .andExpect(jsonPath("$.[*].joinDay").value(hasItem(sameInstant(DEFAULT_JOIN_DAY))))
            .andExpect(jsonPath("$.[*].goodbyeDay").value(hasItem(sameInstant(DEFAULT_GOODBYE_DAY))))
            .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].updator").value(hasItem(DEFAULT_UPDATOR.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(sameInstant(DEFAULT_UPDATE_TIME))));
    }
}
