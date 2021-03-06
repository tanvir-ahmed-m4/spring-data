package com.nixmash.springdata.mvc.controller;

import com.nixmash.springdata.jpa.exceptions.PostNotFoundException;
import com.nixmash.springdata.jpa.service.PostService;
import com.nixmash.springdata.jsoup.service.JsoupService;
import com.nixmash.springdata.mvc.AbstractContext;
import com.nixmash.springdata.mvc.components.WebUI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static com.nixmash.springdata.mvc.controller.PostsController.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by daveburke on 5/27/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PostsControllerTests extends AbstractContext {

    private MockMvc mockMvc;

    @Autowired
    WebUI webUI;

    @Autowired
    JsoupService jsoupService;

    @Autowired
    PostService postService;

    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void homePageTest() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(view().name(POSTS_LIST_VIEW));
    }

    @Test
    public void postDisplayPage() throws Exception {
        mockMvc.perform(get("/posts/post/javascript-bootstrap"))
                .andExpect(model().attributeExists("post"))
                .andExpect(view().name(POSTS_PERMALINK_VIEW));
    }

    @Test(expected = PostNotFoundException.class)
    public void notFoundPostName_ThrowsPostNotFoundException() throws Exception {
        String badName = "bad-name";
        when(postService.getPost(badName))
                .thenThrow(new PostNotFoundException());

        mockMvc.perform(get("/posts/" + badName))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/custom"));
    }


    @Test
    public void titlesPageLoadsTitleView() throws Exception {
        this.mockMvc.perform(get("/posts/titles"))
                .andExpect(status().isOk())
                .andExpect(view().name(POSTS_TITLES_VIEW));
    }

    @Test
    public void postsFeedLoads() throws Exception {
        this.mockMvc.perform(get("/posts/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/rss+xml"));
    }

    @Test
    @WithUserDetails(value = "keith")
    public void userLikedPosts_LoadsLikesView() throws Exception {
        this.mockMvc.perform(get("/posts/likes/3"))
                .andExpect(status().isOk())
                .andExpect(view().name(POSTS_LIKES_VIEW));
    }


    @Test
    public void loadPostAtoZView() throws Exception {
        this.mockMvc.perform(get("/posts/az"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("alphaLinks"))
                .andExpect(model().attributeExists("alphaPosts"))
                .andExpect(view().name(POSTS_AZ_VIEW));
    }

}
